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

package pl.coffeepower.blog.messagebus.fastcast;

import com.google.common.primitives.Longs;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.junit.Test;

import pl.coffeepower.blog.messagebus.ConfigModule;
import pl.coffeepower.blog.messagebus.Receiver;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class FastCastReceiverTest {

    private final Injector injector = Guice.createInjector(new ConfigModule(), new FastCastModule());

    @Test
    public void shouldRecCurrentTime() throws Exception {
        AtomicLong lastReceived = new AtomicLong(0);
        Receiver receiver = injector.getInstance(Receiver.class);
        receiver.register(data -> {
            assertThat(data.length, is(Longs.BYTES + 123));
            long prevReceived = lastReceived.getAndSet(
                    Longs.fromByteArray(Arrays.copyOf(data, Longs.BYTES)));
            assertThat(lastReceived.get(), is(prevReceived + 1));
        });
        while (lastReceived.get() < 10_000_000L) {
            TimeUnit.SECONDS.sleep(1L);
            System.out.println("lastReceived: " + lastReceived.get());
        }
        receiver.close();
    }
}