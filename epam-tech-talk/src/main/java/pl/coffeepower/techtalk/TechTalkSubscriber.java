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

package pl.coffeepower.techtalk;

import com.google.common.primitives.Longs;

import com.beust.jcommander.JCommander;

import lombok.extern.log4j.Log4j2;

import pl.coffeepower.blog.messagebus.Subscriber;
import pl.coffeepower.blog.messagebus.util.BytesEventModule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.inject.Guice.createInjector;

@Log4j2
public final class TechTalkSubscriber {

    private final Subscriber subscriber;
    private final int packets;

    private TechTalkSubscriber(TechTalkConfiguration configuration) {
        subscriber = createInjector(new TechTalkConfigModule(configuration), new BytesEventModule(), configuration.getEngine().getModule())
                .getInstance(Subscriber.class);
        packets = configuration.getPackets();
    }

    private void receive() {
        subscriber.register((data, length) -> {
            long number = Longs.fromByteArray(data);
            if (number % 1_000 == 0)
                log.info("Got " + number + " message");
            if (number >= packets) {
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(() -> {
                    try {
                        subscriber.close();
                        System.exit(0);
                    } catch (Exception e) {
                        log.error(e);
                    }
                });
                executorService.shutdown();
            }
        });
    }

    public static void main(String[] args) {
        TechTalkConfiguration configuration = new TechTalkConfiguration();
        new JCommander(configuration, args).usage();
        new TechTalkSubscriber(configuration).receive();
    }
}
