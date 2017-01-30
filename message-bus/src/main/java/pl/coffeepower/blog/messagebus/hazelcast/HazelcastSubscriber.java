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

import com.hazelcast.core.ITopic;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import pl.coffeepower.blog.messagebus.Subscriber;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

@Log4j2
final class HazelcastSubscriber implements Subscriber {

  private final AtomicBoolean opened = new AtomicBoolean(false);
  private final ITopic<byte[]> topic;
  @Inject
  private Handler handler;

  @Inject
  private HazelcastSubscriber(@NonNull ITopic<byte[]> iTopic) {
    topic = iTopic;
    topic.addMessageListener(message -> {
      Preconditions.checkNotNull(handler);
      Preconditions.checkState(opened.get(), "Already closed");
      byte[] data = message.getMessageObject();
      handler.received(data, data.length);
    });
    opened.set(true);
    log.info("Created Subscriber: topicId={}", iTopic.getName());
  }

  @Override
  public void register(@NonNull Handler handler) {
    this.handler = handler;
  }

  @Override
  public void close() throws Exception {
    Preconditions.checkState(opened.get(), "Already closed");
    topic.destroy();
    handler = null;
    opened.set(false);
  }
}
