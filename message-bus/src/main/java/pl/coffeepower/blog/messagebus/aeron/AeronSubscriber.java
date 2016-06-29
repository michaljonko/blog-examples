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
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import pl.coffeepower.blog.messagebus.Configuration;
import pl.coffeepower.blog.messagebus.Subscriber;
import pl.coffeepower.blog.messagebus.util.BytesEventFactory;

import uk.co.real_logic.aeron.Aeron;
import uk.co.real_logic.aeron.Subscription;
import uk.co.real_logic.agrona.concurrent.IdleStrategy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

@Log4j2
final class AeronSubscriber implements Subscriber {

    private final AtomicBoolean opened = new AtomicBoolean(false);
    private final Aeron aeron;
    private final Subscription subscription;
    private final ExecutorService executor;
    private final IdleStrategy idleStrategy;
    private final Disruptor<BytesEventFactory.BytesEvent> disruptor;
    private final RingBuffer<BytesEventFactory.BytesEvent> ringBuffer;
    @Inject
    private Handler handler;

    @Inject
    private AeronSubscriber(@NonNull Aeron aeron,
                            @NonNull IdleStrategy idleStrategy,
                            @NonNull Configuration configuration,
                            @NonNull Disruptor<BytesEventFactory.BytesEvent> disruptor) {
        Preconditions.checkArgument(InetAddresses.forString(configuration.getMulticastAddress()).getAddress()[3] % 2 != 0, "Lowest byte in multicast address has to be odd");
        String channel = "aeron:udp?group=" + configuration.getMulticastAddress() + ":" + configuration.getMulticastPort() + "|interface=" + configuration.getBindAddress();
        this.aeron = aeron;
        this.subscription = this.aeron.addSubscription(channel, configuration.getTopicId());
        this.idleStrategy = idleStrategy;
        this.disruptor = disruptor;
        this.disruptor.handleEventsWith((event, sequence, endOfBatch) -> {
            Preconditions.checkNotNull(handler);
            handler.received(event.getBuffer(), event.getCurrentLength());
        });
        this.ringBuffer = this.disruptor.start();
        this.opened.set(true);
        this.executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("subscriber-thread").build());
        this.executor.execute(() -> {
            byte[] bytes = new byte[AeronConst.BUFFER_SIZE];
            while (opened.get()) {
                this.idleStrategy.idle(this.subscription.poll((buffer, offset, length, header) -> {
                    Preconditions.checkState(opened.get(), "Already closed");
                    buffer.getBytes(offset, bytes, 0, length);
                    ringBuffer.publishEvent((event, sequence) -> event.copyToBuffer(bytes, length));
                }, 1));
            }
        });
        this.executor.shutdown();
        log.info("Created Subscriber: channel={}, streamId={}", channel, configuration.getTopicId());
    }

    @Override
    public void register(@NonNull Handler handler) {
        this.handler = handler;
    }

    @Override
    public boolean isOpened() {
        return opened.get();
    }

    @Override
    public void close() throws Exception {
        Preconditions.checkState(opened.get(), "Already closed");
        disruptor.shutdown();
        executor.shutdownNow();
        subscription.close();
        aeron.close();
        handler = null;
        opened.set(false);
    }
}
