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

import com.google.common.net.InetAddresses;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.validators.PositiveInteger;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import pl.coffeepower.blog.messagebus.Configuration;
import pl.coffeepower.blog.messagebus.Engine;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TechTalkConfiguration implements Configuration {

    @Parameter(names = "topicId", validateWith = PositiveInteger.class)
    int topicId = 1;
    @Parameter(names = "multicastAddr", validateWith = MulticastAddressValidator.class)
    String multicastAddress = "225.0.0.123";
    @Parameter(names = "multicastPort", validateWith = PositiveInteger.class)
    int multicastPort = 12345;
    @Parameter(names = "ifaceAddr", validateWith = IPv4AddressValidator.class)
    String bindAddress = InetAddress.getLoopbackAddress().getHostAddress();
    @Parameter(names = "engine")
    Engine engine = Engine.FAST_CAST;
    @Parameter(names = "packets", validateWith = PositiveInteger.class)
    int packets = 1_000_000;

    private static final class MulticastAddressValidator implements IParameterValidator {

        @Override
        public void validate(String name, @NonNull String value) throws ParameterException {
            try {
                if (!InetAddresses.forString(value).isMulticastAddress()) {
                    throw new ParameterException("Parameter " + name + " is not proper multicast address (found " + value + ").");
                }
            } catch (IllegalArgumentException e) {
                throw new ParameterException("Parameter " + name + " is not proper IP address (found " + value + ").");
            }
        }
    }

    private static final class IPv4AddressValidator implements IParameterValidator {

        @Override
        public void validate(String name, @NonNull String value) throws ParameterException {
            try {
                if (!InetAddresses.isInetAddress(value) || !NetworkInterface.getByInetAddress(InetAddresses.forString(value)).isUp()) {
                    throw new ParameterException("Parameter " + name + " is not proper interface address or interface is down (found " + value + ").");
                }
            } catch (IllegalArgumentException | SocketException e) {
                throw new ParameterException("Parameter " + name + " is not proper interface IP address (found " + value + ").");
            }
        }
    }
}
