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
import com.google.inject.Guice;

import lombok.extern.log4j.Log4j2;

import pl.coffeepower.blog.messagebus.config.ConfigModule;
import pl.coffeepower.blog.messagebus.util.BytesEventModule;
import pl.coffeepower.blog.messagebus.util.DefaultBasicService;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@Log4j2
public final class SubscriberApplication extends AbstractIdleService {

    private final Subscriber subscriber;
    private final AtomicLong lastReceived = new AtomicLong(0);

    SubscriberApplication(Engine engine) {
        subscriber = Guice.createInjector(new ConfigModule(), new BytesEventModule(), engine.getModule()).getInstance(Subscriber.class);
        subscriber.register((data, length) -> {
            long prevReceivedValue = lastReceived.getAndSet(Longs.fromByteArray(data));
            long lastReceivedValue = lastReceived.get();
            if (lastReceivedValue != (prevReceivedValue + 1)) {
                log.error("Broken connection on {}", lastReceivedValue);
                System.exit(-1);
            }
            if (lastReceivedValue % 10_000 == 0) {
                log.info("Retrieved {} messages", lastReceivedValue);
            }
            if (lastReceivedValue >= PublisherApplication.MESSAGES_COUNT) {
                stopAsync();
            }
        });
    }

    public static void main(String[] args) throws Exception {
        new DefaultBasicService(new SubscriberApplication(Engine.FAST_CAST)).start();
    }

    @Override
    protected void startUp() throws Exception {
        log.info("startUp");
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("shutDown");
        subscriber.close();
        Executors.newCachedThreadPool().execute(() -> System.exit(0));
    }
}
