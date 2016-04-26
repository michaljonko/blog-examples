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

import com.google.common.base.Preconditions;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import org.nustaq.fastcast.api.FCPublisher;
import org.nustaq.fastcast.api.FastCast;
import org.nustaq.fastcast.config.PhysicalTransportConf;
import org.nustaq.fastcast.config.PublisherConf;

import pl.coffeepower.blog.messagebus.Configuration.Const;
import pl.coffeepower.blog.messagebus.Publisher;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Named;

@Log4j2
final class FastCastPublisher implements Publisher {

    private final AtomicBoolean opened = new AtomicBoolean(false);
    private final AtomicBoolean lock = new AtomicBoolean(false);
    private final FastCast fastCast;
    private final FCPublisher publisher;
    private final String physicalTransportName;

    @Inject
    private FastCastPublisher(@NonNull FastCast fastCast,
                              @NonNull @Named(Const.PUBLISHER_NAME_KEY) String nodeId,
                              @NonNull PhysicalTransportConf physicalTransportConf,
                              @NonNull PublisherConf publisherConf) {
        this.fastCast = fastCast;
        this.fastCast.setNodeId(nodeId);
        this.fastCast.addTransport(physicalTransportConf);
        this.physicalTransportName = physicalTransportConf.getName();
        this.publisher = this.fastCast.onTransport(physicalTransportName).publish(publisherConf).batchOnLimit(true);
        this.opened.set(true);
        log.info("Created Publisher: nodeId={}, physicalTransportName={}", nodeId, this.physicalTransportName);
    }

    @Override
    public boolean send(@NonNull byte[] data) {
        try {
            lock();
            Preconditions.checkState(opened.get(), "Already closed");
            return publisher.offer(null, data, 0, data.length, false);
        } finally {
            unlock();
        }
    }

    @Override
    public void close() throws Exception {
        Preconditions.checkState(opened.get(), "Already closed");
        publisher.flush();
        fastCast.onTransport(physicalTransportName).terminate();
        unlock();
        opened.set(false);
    }

    private void lock() {
        while (!lock.compareAndSet(false, true)) ;
    }

    private void unlock() {
        lock.set(false);
    }
}
