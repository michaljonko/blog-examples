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

package pl.coffeepower.blog.messagebus.aeron;

import com.google.common.base.Preconditions;
import com.google.common.net.InetAddresses;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import pl.coffeepower.blog.messagebus.Configuration;
import pl.coffeepower.blog.messagebus.Publisher;

import uk.co.real_logic.aeron.Aeron;
import uk.co.real_logic.aeron.Publication;
import uk.co.real_logic.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

@Log4j2
final class AeronPublisher implements Publisher {

    private final AtomicBoolean lock = new AtomicBoolean(false);
    private final AtomicBoolean opened = new AtomicBoolean(false);
    private final UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(AeronConst.BUFFER_SIZE));
    private final Aeron aeron;
    private final Publication publication;

    @Inject
    private AeronPublisher(@NonNull Aeron aeron, @NonNull Configuration configuration) {
        Preconditions.checkArgument(InetAddresses.forString(configuration.getMulticastAddress()).getAddress()[3] % 2 != 0, "Lowest byte in multicast address has to be odd");
        String channel = "aeron:udp?group=" + configuration.getMulticastAddress() + ":" + configuration.getMulticastPort() + "|interface=" + configuration.getBindAddress();
        this.aeron = aeron;
        this.publication = this.aeron.addPublication(channel, configuration.getTopicId());
        this.opened.set(true);
        log.info("Created Publisher: channel={}, streamId={}", channel, configuration.getTopicId());
    }

    @Override
    public boolean send(@NonNull byte[] data) {
        try {
            lock();
            Preconditions.checkState(opened.get(), "Already closed");
            buffer.putBytes(0, data);
            return publication.offer(buffer, 0, data.length) >= 0;
        } finally {
            unlock();
        }
    }

    @Override
    public void close() throws Exception {
        Preconditions.checkState(opened.get(), "Already closed");
        publication.close();
        aeron.close();
        opened.set(false);
    }

    private void lock() {
        while (!lock.compareAndSet(false, true)) ;
    }

    private void unlock() {
        lock.set(false);
    }
}
