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

package pl.coffeepower.blog.messagebus.fastcast;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class FastCastConst {

    static final int DATAGRAM_SIZE = 804;
    static final int IDLE_PARK_MICROS = 1;
    static final int SPIN_LOOP_MICROS = 1;
    static final int PUBLISHER_PPS = 10_000;
    static final int PUBLISHER_PPS_WINDOW = 100;
    static final int PUBLISHER_PACKET_HISTORY = 4 * PUBLISHER_PPS;
    static final int PUBLISHER_HEARTBEAT_INTERVAL = 5_000;
    static final int SUBSCRIBER_BUFFER_PACKETS = 4 * PUBLISHER_PPS;
    static final int SUBSCRIBER_MAX_DELAY_RETRANS_MS = 1;
    static final int SUBSCRIBER_MAX_DELAY_NEXT_RETRANS_MS = 1;
    static final boolean SUBSCRIBER_UNRELIABLE = false;
    static final String STREAM_NAME = "coffeepower";
}
