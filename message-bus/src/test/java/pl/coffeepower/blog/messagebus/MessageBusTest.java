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

import pl.coffeepower.blog.messagebus.config.ConfigModule;
import pl.coffeepower.blog.messagebus.util.BytesEventModule;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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
        JvmProps.at(cloud.node("**")).addJvmArg("-Xms128m").addJvmArg("-Xmx256m");
        cloud.nodes("pub", "sub").touch();
    }

    @After
    public void tearDown() throws Exception {
        cloud.shutdown();
    }

    @Test
    public void shouldRetrieveAllMessagesWithFastCast() throws Exception {
        Future<Boolean> subTask = createSubscriberFuture(Engine.FAST_CAST);
        Future<Boolean> pubTask = createPublisherFuture(Engine.FAST_CAST);
        assertThat(subTask.get(2L, TimeUnit.MINUTES), is(true));
        assertThat(pubTask.get(), is(true));
    }

    @Test
    public void shouldRetrieveAllMessagesWithAeron() throws Exception {
        Future<Boolean> subTask = createSubscriberFuture(Engine.AERON);
        Future<Boolean> pubTask = createPublisherFuture(Engine.AERON);
        assertThat(subTask.get(2L, TimeUnit.MINUTES), is(true));
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
                        while (!publisher.send(Bytes.concat(Longs.toByteArray(value), fixtures.getAdditionalData()))) {
                            Thread.yield();
                        }
                    });
            System.out.println("Sent all messages in " + stopwatch);
            return Boolean.TRUE;
        });
    }

    private Future<Boolean> createSubscriberFuture(final Engine engine) {
        return cloud.node("sub").submit((Callable<Boolean> & Serializable) () -> {
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
            while (state.get() && lastReceived.get() < fixtures.getNumberOfMessages()) {
                Thread.yield();
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
        long numberOfMessages = 1_000_000L;
        byte[] additionalData = "9876543210123456789qazxswedcvfrtgbnhyujm".getBytes();
        int additionalDataLength = additionalData.length;
        byte lastAdditionalDataByte = additionalData[additionalDataLength - 1];
    }
}