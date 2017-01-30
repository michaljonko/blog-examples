/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Michał Jonko
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

package pl.coffeepower.blog.messagebus;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.google.common.base.Stopwatch;

import org.junit.Test;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FastCastMISOTest extends MessageBusTestHelper {

  @Test
  public void shouldRetrieveAllMessages() throws Exception {
    long timeout = 60L;
    Future<Boolean> subscriberFuture1 = createSubscriberFuture(NODE_SUBSCRIBER_1, Engine.FAST_CAST);
    TimeUnit.SECONDS.sleep(5L);
    Future<Boolean> subscriberFuture2 = createSubscriberFuture(NODE_SUBSCRIBER_2, Engine.FAST_CAST);
    TimeUnit.SECONDS.sleep(5L);
    Stopwatch stopwatch = Stopwatch.createStarted();
    Publisher publisher = executePublisher(Engine.FAST_CAST);
    assertThat(subscriberFuture1.get(timeout, TimeUnit.SECONDS), is(true));
    assertThat(subscriberFuture2.get(timeout, TimeUnit.SECONDS), is(true));
    System.out.println("All messages received in " + stopwatch.stop());
    publisher.close();
  }
}
