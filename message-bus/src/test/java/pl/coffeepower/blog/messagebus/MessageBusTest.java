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

import com.google.common.base.Stopwatch;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.google.inject.Guice;

import lombok.Value;

import org.gridkit.nanocloud.Cloud;
import org.gridkit.nanocloud.CloudFactory;
import org.gridkit.vicluster.ViProps;
import org.gridkit.vicluster.telecontrol.jvm.JvmProps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.coffeepower.blog.messagebus.Configuration.Const;
import pl.coffeepower.blog.messagebus.config.ConfigModule;
import pl.coffeepower.blog.messagebus.util.BytesEventModule;

import uk.co.real_logic.agrona.concurrent.IdleStrategy;
import uk.co.real_logic.agrona.concurrent.SleepingIdleStrategy;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MessageBusTest {

    private Cloud cloud;

    @Before
    public void setUp() throws Exception {
        cloud = CloudFactory.createCloud();
        ViProps.at(cloud.node("**")).setLocalType();
        JvmProps.at(cloud.node("**")).addJvmArgs("-Xms128m", "-Xmx256m");
        cloud.node("sub-1").setProp(Const.SUBSCRIBER_NAME_KEY, "SUB-1");
        cloud.node("sub-2").setProp(Const.SUBSCRIBER_NAME_KEY, "SUB-2");
        cloud.node("sub-3").setProp(Const.SUBSCRIBER_NAME_KEY, "SUB-3");
        cloud.nodes("pub", "sub-1", "sub-2", "sub-3").touch();
    }

    @After
    public void tearDown() throws Exception {
        cloud.shutdown();
    }

    @Test
    public void shouldRetrieveAllMessagesWithFastCastSISO() throws Exception {
        sisoTest(Engine.FAST_CAST);
    }

    @Test
    public void shouldRetrieveAllMessagesWithFastCastMISO() throws Exception {
        misoTest(Engine.FAST_CAST);
    }

    @Test
    public void shouldRetrieveAllMessagesWithAeronSISO() throws Exception {
        sisoTest(Engine.AERON);
    }

    @Test
    public void shouldRetrieveAllMessagesWithAeronMISO() throws Exception {
        misoTest(Engine.AERON);
    }

    private void sisoTest(final Engine engine) throws InterruptedException, ExecutionException, TimeoutException {
        long timeout = 5L;
        Future<Boolean> subTask = createSubscriberFuture(engine);
        Future<Boolean> pubTask = createPublisherFuture(engine);
        assertThat(subTask.get(timeout, TimeUnit.MINUTES), is(true));
        assertThat(pubTask.get(), is(true));
    }

    private void misoTest(final Engine engine) throws InterruptedException, ExecutionException, TimeoutException {
        long timeout = 5L;
        Future<Boolean> subTask1 = createSubscriberFuture("sub-1", engine);
        Future<Boolean> subTask2 = createSubscriberFuture("sub-2", engine);
        Future<Boolean> subTask3 = createSubscriberFuture("sub-3", engine);
        Future<Boolean> pubTask = createPublisherFuture(engine);
        assertThat(subTask1.get(timeout, TimeUnit.MINUTES), is(true));
        assertThat(subTask2.get(timeout, TimeUnit.MINUTES), is(true));
        assertThat(subTask3.get(timeout, TimeUnit.MINUTES), is(true));
        assertThat(pubTask.get(), is(true));
    }

    private Future<Boolean> createPublisherFuture(final Engine engine) {
        return cloud.node("pub").submit((Callable<Boolean> & Serializable) () -> {
            Fixtures fixtures = new Fixtures();
            Publisher publisher = Guice.createInjector(new ConfigModule(), new BytesEventModule(), engine.getModule()).getInstance(Publisher.class);
            Stopwatch stopwatch = Stopwatch.createStarted();
            LongStream.rangeClosed(fixtures.getFirstMessageId(), fixtures.getNumberOfMessages())
                    .onClose(() -> stopwatch.stop())
                    .forEach(value -> {
                        IdleStrategy idleStrategy = new SleepingIdleStrategy(TimeUnit.MICROSECONDS.toNanos(1L));
                        while (!publisher.send(Bytes.concat(Longs.toByteArray(value), fixtures.getAdditionalData()))) {
                            idleStrategy.idle();
                        }
                    });
            System.out.println("Sent all messages in " + stopwatch);
            return Boolean.TRUE;
        });
    }

    private Future<Boolean> createSubscriberFuture(final Engine engine) {
        return createSubscriberFuture("sub-1", engine);
    }

    private Future<Boolean> createSubscriberFuture(final String cloudNode, final Engine engine) {
        return cloud.node(cloudNode).submit((Callable<Boolean> & Serializable) () -> {
            Fixtures fixtures = new Fixtures();
            AtomicBoolean state = new AtomicBoolean(true);
            AtomicLong lastReceived = new AtomicLong();
            AtomicLong messagesCounter = new AtomicLong();
            Subscriber subscriber = Guice.createInjector(new ConfigModule(), new BytesEventModule(), engine.getModule()).getInstance(Subscriber.class);
            Stopwatch stopwatch = Stopwatch.createStarted();
            subscriber.register((data, length) -> {
                messagesCounter.incrementAndGet();
                if (state.get()) {
                    long prevReceivedValue = lastReceived.getAndSet(Longs.fromByteArray(data));
                    long lastReceivedValue = lastReceived.get();
                    if (lastReceivedValue != (prevReceivedValue + 1) || data[length - 1] != fixtures.getLastAdditionalDataByte()) {
                        state.set(false);
                    }
                }
            });
            IdleStrategy idleStrategy = new SleepingIdleStrategy(TimeUnit.MICROSECONDS.toNanos(100L));
            while (state.get() && lastReceived.get() < fixtures.getNumberOfMessages()) {
                idleStrategy.idle();
            }
            subscriber.close();
            if (!state.get()) {
                System.out.println("Broken connection on messageId=" + lastReceived.get());
            } else {
                System.out.println("Received messages " + messagesCounter + " in " + stopwatch.stop());
                System.out.println("Last messageId=" + lastReceived);
            }
            return state.get();
        });
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