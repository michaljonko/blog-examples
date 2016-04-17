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

import com.google.common.base.Stopwatch;
import com.google.common.primitives.Longs;

import com.beust.jcommander.JCommander;

import pl.coffeepower.blog.messagebus.Publisher;
import pl.coffeepower.blog.messagebus.util.BytesEventModule;

import uk.co.real_logic.agrona.concurrent.IdleStrategy;
import uk.co.real_logic.agrona.concurrent.SleepingIdleStrategy;

import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

import lombok.extern.log4j.Log4j2;

import static com.google.inject.Guice.createInjector;

@Log4j2
public final class TechTalkPublisher {

    private final Publisher publisher;
    private final int packets;

    private TechTalkPublisher(TechTalkConfiguration configuration) {
        publisher = createInjector(new TechTalkConfigModule(configuration), new BytesEventModule(), configuration.getEngine().getModule())
                .getInstance(Publisher.class);
        packets = configuration.getPackets();
    }

    private void publish() throws Exception {
        IdleStrategy idleStrategy = new SleepingIdleStrategy(1L);
        Stopwatch stopwatch = Stopwatch.createStarted();
        LongStream.rangeClosed(1, packets).forEach(value -> {
            while (!publisher.send(Longs.toByteArray(value))) {
                idleStrategy.idle();
            }
        });
        log.info("Sent all packets in " + stopwatch.stop());
        TimeUnit.SECONDS.sleep(10L);
        publisher.close();
    }

    public static void main(String[] args) throws Exception {
        TechTalkConfiguration configuration = new TechTalkConfiguration();
        new JCommander(configuration, args).usage();
        new TechTalkPublisher(configuration).publish();
    }
}
