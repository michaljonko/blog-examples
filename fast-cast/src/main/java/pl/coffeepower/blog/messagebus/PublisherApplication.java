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

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.common.util.concurrent.ServiceManager;
import com.google.inject.Guice;

import java.util.Collections;
import java.util.stream.LongStream;

import lombok.extern.java.Log;

@Log
public final class PublisherApplication extends AbstractExecutionThreadService {

    private final Publisher publisher;

    PublisherApplication(Engine engine) {
        this.publisher = Guice.createInjector(new ConfigModule(), engine.getModule())
                .getInstance(Publisher.class);
    }

    public static void main(String[] args) {
        ServiceManager serviceManager =
                new ServiceManager(
                        Collections.singleton(new PublisherApplication(Engine.FAST_CAST)));
        serviceManager.startAsync().awaitHealthy();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            serviceManager.stopAsync().awaitStopped();
        }));
    }

    @Override
    protected void run() throws Exception {
        byte[] additionalData = new byte[32];
        LongStream.rangeClosed(1L, 1_000_000L)
                .onClose(() -> System.exit(0))
                .forEach(value -> {
                    publisher.send(Bytes.concat(Longs.toByteArray(value), additionalData));
                    if (value % 10_000 == 0) {
                        log.info("Sent " + value + " messages");
                    }
                });
    }

    @Override
    protected void shutDown() throws Exception {
        super.shutDown();
        publisher.close();
    }
}
