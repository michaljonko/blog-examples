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

package pl.coffeepower.techtalk;

import com.beust.jcommander.Parameter;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import pl.coffeepower.blog.messagebus.Configuration;
import pl.coffeepower.blog.messagebus.Engine;

import java.net.InetAddress;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TechTalkConfiguration implements Configuration {

    @Parameter(names = "--topicId", arity = 1, required = false)
    int topicId = 1;
    @Parameter(names = "--multicastAddr", arity = 1, required = false)
    String multicastAddress = "225.0.0.123";
    @Parameter(names = "--multicastPort", arity = 1, required = false)
    int multicastPort = 12345;
    @Parameter(names = "--ifaceAddr", arity = 1, required = false)
    String bindAddress = InetAddress.getLoopbackAddress().getHostAddress();
    @Parameter(names = "--engine", arity = 1, required = false)
    Engine engine = Engine.FAST_CAST;
    @Parameter(names = "--packets", arity = 1, required = false)
    int packets = 1_000_000;
}
