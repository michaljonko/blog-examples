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

import org.gridkit.nanocloud.Cloud;
import org.gridkit.nanocloud.CloudFactory;
import org.gridkit.vicluster.ViProps;
import org.gridkit.vicluster.telecontrol.jvm.JvmProps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.coffeepower.blog.messagebus.config.ConfigModule;
import pl.coffeepower.blog.messagebus.fastcast.FastCastModule;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FastCastTest {

    private Cloud cloud;

    @Before
    public void setUp() throws Exception {
        cloud = CloudFactory.createCloud();
        ViProps.at(cloud.node("**")).setLocalType();
        JvmProps.at(cloud.node("**")).addJvmArg("-Xms64m").addJvmArg("-Xmx128m");
    }

    @After
    public void tearDown() throws Exception {
        cloud.shutdown();
    }

    @Test
    public void shouldRetriveAllMessages() throws Exception {
        cloud.nodes("pub", "sub");
        cloud.node("**").touch();
        Future<Boolean> subTask = cloud.node("sub").submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                AtomicBoolean state = new AtomicBoolean(true);
                AtomicLong lastReceived = new AtomicLong(0);
                Subscriber subscriber = Guice.createInjector(new ConfigModule(), new FastCastModule()).getInstance(Subscriber.class);
                subscriber.register(data -> {
                    long prevReceivedValue = lastReceived.getAndSet(Longs.fromByteArray(data));
                    long lastReceivedValue = lastReceived.get();
                    if (lastReceivedValue != (prevReceivedValue + 1)) {
                        state.set(false);
                    }
                });
                while (lastReceived.get() < 100_000L) {
                    Thread.yield();
                }
                System.out.println("Last received message is " + lastReceived);
                return state.get();
            }
        });
        Future<Boolean> pubTask = cloud.node("pub").submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Publisher publisher = Guice.createInjector(new ConfigModule(), new FastCastModule()).getInstance(Publisher.class);
                byte[] additionalData = "QWERTYUIOPLKJHGFDSAZXCVBNM".getBytes();
                Stopwatch stopwatch = Stopwatch.createStarted();
                LongStream.rangeClosed(1L, 100_000L)
                        .onClose(() -> stopwatch.stop())
                        .forEach(value -> {
                            while (!publisher.send(Bytes.concat(Longs.toByteArray(value), additionalData))) {
                                Thread.yield();
                            }
                        });
                System.out.println("Sent all messages in " + stopwatch);
                return Boolean.TRUE;
            }
        });
        assertThat(subTask.get(1L, TimeUnit.MINUTES), is(true));
        assertThat(pubTask.get(1L, TimeUnit.MINUTES), is(true));
    }
}