/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Michał Jonko
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

import com.google.common.primitives.Longs;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.ServiceManager;
import com.google.inject.Guice;

import lombok.extern.java.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

@Log
public final class SubscriberApplication extends AbstractIdleService {

    private final Subscriber subscriber;
    private final AtomicLong lastReceived = new AtomicLong(0);

    SubscriberApplication(Engine engine) {
        subscriber = Guice.createInjector(new ConfigModule(), engine.getModule())
                .getInstance(Subscriber.class);
        subscriber.register(data -> {
            long prevReceivedValue = lastReceived.getAndSet(
                    Longs.fromByteArray(Arrays.copyOf(data, Longs.BYTES)));
            long lastReceivedValue = lastReceived.get();
            if (lastReceivedValue != (prevReceivedValue + 1)) {
                log.severe("Broken connection on " + lastReceivedValue);
                System.exit(-1);
            }
            if (lastReceivedValue % 10_000 == 0) {
                log.info("Received " + lastReceivedValue + " messages");
            }
            if (lastReceivedValue >= 10_000_000) {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        ServiceManager serviceManager = new ServiceManager(
                Collections.singleton(new SubscriberApplication(Engine.FAST_CAST)));
        serviceManager.startAsync().awaitHealthy();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                serviceManager.stopAsync().awaitStopped(1L, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                System.exit(-1);
            }
        }));
    }

    @Override
    protected void startUp() throws Exception {
        log.info("Starting...");
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("Stopping...");
        subscriber.close();
        subscriber.register(null);
    }
}
