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

import com.google.common.base.Stopwatch;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.inject.Guice;

import pl.coffeepower.blog.messagebus.util.DefaultBasicService;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

import lombok.extern.java.Log;

@Log
public final class PublisherApplication extends AbstractExecutionThreadService {

    public static final long MESSAGES_COUNT = 500_000L;
    private final Publisher publisher;

    PublisherApplication(Engine engine) {
        this.publisher = Guice.createInjector(new ConfigModule(), engine.getModule())
                .getInstance(Publisher.class);
    }

    public static void main(String[] args) throws Exception {
        new DefaultBasicService(new PublisherApplication(Engine.FAST_CAST)).start();
    }

    @Override
    protected void run() throws Exception {
        byte[] additionalData = "Hello MessageBus".getBytes();
        Stopwatch stopwatch = Stopwatch.createStarted();
        LongStream.rangeClosed(1L, MESSAGES_COUNT)
                .onClose(() -> stopwatch.stop())
                .forEach(value -> {
                    publisher.send(Bytes.concat(Longs.toByteArray(value), additionalData));
                    if (value % 10_000 == 0) {
                        log.info("Sent " + value + " messages in " + stopwatch.toString());
                    }
                });
    }

    @Override
    protected void startUp() throws Exception {
        log.info("startUp");
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("shutDown");
        publisher.close();
        Executors.newCachedThreadPool().execute(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
            }
            System.exit(0);
        });
    }
}
