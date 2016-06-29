/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Michał Jonko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package pl.coffeepower.blog.messagebus;

import com.google.common.base.MoreObjects;
import com.google.common.base.Stopwatch;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import lombok.Getter;
import lombok.Value;

import org.gridkit.nanocloud.Cloud;
import org.gridkit.nanocloud.CloudFactory;
import org.gridkit.vicluster.ViProps;
import org.gridkit.vicluster.telecontrol.jvm.JvmProps;
import org.junit.After;
import org.junit.Before;

import pl.coffeepower.blog.messagebus.Configuration.Const;
import pl.coffeepower.blog.messagebus.util.BytesEventModule;

import uk.co.real_logic.agrona.concurrent.IdleStrategy;
import uk.co.real_logic.agrona.concurrent.SleepingIdleStrategy;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

public abstract class MessageBusTestHelper implements Serializable {

    static final String NODE_SUBSCRIBER_1 = "sub-1";
    static final String NODE_SUBSCRIBER_2 = "sub-2";
    static final String NODE_SUBSCRIBER_3 = "sub-3";
    static final String NODE_PUBLISHER = "pub";
    private static final long serialVersionUID = -5525765906174658715L;
    @Getter
    private Cloud cloud;

    @Before
    public void setUp() throws Exception {
        cloud = CloudFactory.createCloud();
        ViProps.at(cloud.node("**")).setLocalType();
        JvmProps.at(cloud.node("**")).addJvmArgs("-Xms128m", "-Xmx128m");
        cloud.node(NODE_SUBSCRIBER_1).setProp(Const.SUBSCRIBER_NAME_KEY, "SUB-1");
        cloud.node(NODE_SUBSCRIBER_2).setProp(Const.SUBSCRIBER_NAME_KEY, "SUB-2");
        cloud.node(NODE_SUBSCRIBER_3).setProp(Const.SUBSCRIBER_NAME_KEY, "SUB-3");
        cloud.nodes(NODE_PUBLISHER, NODE_SUBSCRIBER_1, NODE_SUBSCRIBER_2, NODE_SUBSCRIBER_3).touch();
    }

    @After
    public void tearDown() throws Exception {
        cloud.shutdown();
    }

    Publisher executePublisher(final Engine engine) throws InterruptedException {
        Publisher publisher = Guice.createInjector(new TestConfigurationModule(), new BytesEventModule(), engine.getModule()).getInstance(Publisher.class);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Fixtures fixtures = new Fixtures();
            Stopwatch stopwatch = Stopwatch.createStarted();
            LongStream.rangeClosed(fixtures.getFirstMessageId(), fixtures.getNumberOfMessages())
                    .forEach(value -> {
                        IdleStrategy idleStrategy = new SleepingIdleStrategy(TimeUnit.MICROSECONDS.toNanos(1L));
                        while (!publisher.send(Bytes.concat(Longs.toByteArray(value), fixtures.getAdditionalData()))) {
                            idleStrategy.idle();
                        }
                    });
            System.out.println("Sent all messages in " + stopwatch.stop());
        });
        executorService.shutdown();
        executorService.awaitTermination(1L, TimeUnit.MINUTES);
        return publisher;
    }

    Future<Boolean> createSubscriberFuture(final Engine engine) {
        return createSubscriberFuture(NODE_SUBSCRIBER_1, engine);
    }

    Future<Boolean> createSubscriberFuture(final String cloudNode, final Engine engine) {
        return cloud.node(cloudNode).submit((Callable<Boolean> & Serializable) () -> {
            Fixtures _fixtures = new Fixtures();
            AtomicBoolean state = new AtomicBoolean(true);
            AtomicLong lastReceived = new AtomicLong();
            AtomicLong messagesCounter = new AtomicLong();
            Subscriber subscriber = Guice.createInjector(new TestConfigurationModule(), new BytesEventModule(), engine.getModule()).getInstance(Subscriber.class);
            subscriber.register((data, length) -> {
                messagesCounter.incrementAndGet();
                if (state.get()) {
                    long prevReceivedValue = lastReceived.getAndSet(Longs.fromByteArray(data));
                    long lastReceivedValue = lastReceived.get();
                    if (lastReceivedValue != (prevReceivedValue + 1) || data[length - 1] != _fixtures.getLastAdditionalDataByte()) {
                        state.set(false);
                    }
                    if (lastReceivedValue % 10_000 == 0) {
                        System.out.println("got " + lastReceivedValue);
                    }
                }
            });
            IdleStrategy idleStrategy = new SleepingIdleStrategy(TimeUnit.MICROSECONDS.toNanos(100L));
            while (state.get() && lastReceived.get() < _fixtures.getNumberOfMessages()) {
                idleStrategy.idle();
            }
            subscriber.close();
            if (!state.get()) {
                System.out.println("Broken connection on messageId=" + lastReceived.get());
                System.out.println("Last messageId=" + lastReceived);
            }
            return state.get();
        });
    }

    private static final class TestConfigurationModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(Configuration.class).to(TestConfiguration.class).asEagerSingleton();
        }
    }

    private static final class TestConfiguration implements Configuration {

        @Override
        public String getMulticastAddress() {
            return "225.0.0.11";
        }

        @Override
        public int getMulticastPort() {
            return 12345;
        }

        @Override
        public String getBindAddress() {
            return "127.0.0.1";
        }

        @Override
        public int getTopicId() {
            return 1;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("multicastAddress", getMulticastAddress())
                    .add("multicastPort", getMulticastPort())
                    .add("bindAddress", getBindAddress())
                    .add("topicId", getTopicId())
                    .toString();
        }
    }

    @Value
    private static final class Fixtures implements Serializable {
        long firstMessageId = 1L;
        long numberOfMessages = 100_000L;
        byte[] additionalData = "9876543210123456789qazxswedcvfrtgbnhyujm".getBytes();
        int additionalDataLength = additionalData.length;
        byte lastAdditionalDataByte = additionalData[additionalDataLength - 1];
    }
}