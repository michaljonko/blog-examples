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

package pl.coffeepower.blog.messagebus.hazelcast;

import com.google.common.base.Preconditions;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.topic.TopicOverloadException;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import pl.coffeepower.blog.messagebus.Configuration;
import pl.coffeepower.blog.messagebus.Publisher;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

@Log4j2
final class HazelcastPublisher implements Publisher {

    private final AtomicBoolean opened = new AtomicBoolean(false);
    private final AtomicBoolean lock = new AtomicBoolean(false);
    private final ITopic<byte[]> topic;

    @Inject
    private HazelcastPublisher(@NonNull HazelcastInstance hazelcastInstance, @NonNull Configuration configuration) {
        topic = hazelcastInstance.getTopic(String.valueOf(configuration.getTopicId()));
        opened.set(true);
        log.info("Created Publisher: topicId={}", configuration.getTopicId());
    }

    @Override
    public boolean send(@NonNull byte[] data) {
        try {
            lock();
            topic.publish(data);
        } catch (TopicOverloadException e) {
            return false;
        } finally {
            unlock();
        }
        return true;
    }

    @Override
    public void close() throws Exception {
        Preconditions.checkState(opened.get(), "Already closed");
        topic.destroy();
    }

    private void lock() {
        while (!lock.compareAndSet(false, true)) ;
    }

    private void unlock() {
        lock.set(false);
    }
}
