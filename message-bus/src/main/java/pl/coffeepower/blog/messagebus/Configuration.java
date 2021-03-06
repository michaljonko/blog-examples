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

package pl.coffeepower.blog.messagebus;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

public interface Configuration {

    String getMulticastAddress();

    int getMulticastPort();

    String getBindAddress();

    int getTopicId();

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final class Const {
        public static final String TOPIC_ID_KEY = "topic.id";
        public static final String MULTICAST_ADDRESS_KEY = "multicast.address";
        public static final String MULTICAST_PORT_KEY = "multicast.port";
        public static final String BIND_ADDRESS_KEY = "bind.address";
        public static final String PUBLISHER_NAME_KEY = "publisher.name";
        public static final String SUBSCRIBER_NAME_KEY = "subscriber.name";
    }
}
