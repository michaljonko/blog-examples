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

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import org.nustaq.fastcast.api.FastCast;
import org.nustaq.fastcast.api.util.ByteArraySubscriber;
import org.nustaq.fastcast.config.PhysicalTransportConf;
import org.nustaq.fastcast.config.SubscriberConf;

import pl.coffeepower.blog.messagebus.Configuration.Const;
import pl.coffeepower.blog.messagebus.Subscriber;
import pl.coffeepower.blog.messagebus.util.BytesEventFactory.BytesEvent;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Log4j2
final class FastCastSubscriber implements Subscriber {

    private final AtomicBoolean opened = new AtomicBoolean(false);
    private final FastCast fastCast;
    private final Disruptor<BytesEvent> disruptor;
    private final RingBuffer<BytesEvent> ringBuffer;
    private final String physicalTransportName;
    @Inject
    private Handler handler;

    @Inject
    private FastCastSubscriber(@NonNull FastCast fastCast,
                               @NonNull @Named(Const.SUBSCRIBER_NAME_KEY) String nodeId,
                               @NonNull PhysicalTransportConf physicalTransportConf,
                               @NonNull SubscriberConf subscriberConf,
                               @NonNull Disruptor<BytesEvent> disruptor) {
        this.disruptor = disruptor;
        this.disruptor.handleEventsWith((event, sequence, endOfBatch) -> {
            Preconditions.checkNotNull(handler);
            handler.received(event.getBuffer(), event.getCurrentLength());
        });
        this.ringBuffer = this.disruptor.start();
        this.fastCast = fastCast;
        this.fastCast.setNodeId(nodeId);
        this.fastCast.addTransport(physicalTransportConf);
        this.physicalTransportName = physicalTransportConf.getName();
        this.fastCast.onTransport(physicalTransportName).subscribe(
                subscriberConf, new ByteArraySubscriber(false) {
                    @Override
                    protected void messageReceived(String sender, long sequence, byte[] msg, int off, int len) {
                        Preconditions.checkState(opened.get(), "Already closed");
                        ringBuffer.publishEvent((event, seq) -> event.copyToBuffer(msg));
                    }
                });
        this.opened.set(true);
    }

    @Override
    public void close() throws Exception {
        Preconditions.checkState(opened.get(), "Already closed");
        disruptor.shutdown();
        fastCast.onTransport(physicalTransportName).terminate();
        handler = null;
        opened.set(false);
    }

    @Override
    public void register(@NonNull Handler handler) {
        this.handler = handler;
    }
}
