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
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import lombok.NonNull;
import lombok.extern.java.Log;

import pl.coffeepower.blog.messagebus.Configuration;
import pl.coffeepower.blog.messagebus.Subscriber;

import uk.co.real_logic.aeron.Aeron;
import uk.co.real_logic.aeron.Subscription;
import uk.co.real_logic.agrona.concurrent.IdleStrategy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;

@Log
@Singleton
final class AeronSubscriber implements Subscriber {

    private final AtomicBoolean opened = new AtomicBoolean(false);
    private final Aeron aeron;
    private final Subscription subscription;
    private final ExecutorService executor;
    private final IdleStrategy idleStrategy;

    @Inject
    private AeronSubscriber(@NonNull Aeron aeron, @NonNull IdleStrategy _idleStrategy, @NonNull Configuration configuration) {
        String channel = "aeron:udp?group=" + configuration.getMulticastAddress() + ":" + configuration.getMulticastPort() + "|interface=" + configuration.getBindAddress();
        this.aeron = aeron;
        this.idleStrategy = _idleStrategy;
        this.subscription = this.aeron.addSubscription(channel, configuration.getChannelId());
        opened.set(true);
        this.executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("subscriber-thread").build());
        this.executor.execute(() -> {
            while (opened.get()) {
//idleStrategy.idle(subscription.poll());
            }
        });
        this.executor.shutdown();
    }

    @Override
    public void close() throws Exception {
        Preconditions.checkState(opened.get(), "Already closed");
        opened.set(false);
        executor.shutdownNow();
        subscription.close();
        aeron.close();
    }

    @Override
    public void register(@NonNull Handler handler) {

    }
}
