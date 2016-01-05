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

/*
 * Created by IntelliJ IDEA.
 * User: yogurt
 * Date: 24.11.15
 * Time: 23:07
 */
package pl.coffeepower.blog.messagebus.config;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.net.InetAddresses;

import lombok.Value;

import pl.coffeepower.blog.messagebus.Configuration;

import javax.inject.Singleton;

@Singleton
@Value
final class DefaultConfiguration implements Configuration {

    private String channelId;
    private String multicastAddress;
    private int multicastPort;
    private String bindAddress;

    public DefaultConfiguration() {
        Preconditions.checkArgument(
                !Strings.isNullOrEmpty(this.channelId = System.getProperty(Const.CHANNEL_ID_KEY, "coffeepower")),
                "channelId is empty");
        Preconditions.checkArgument(
                InetAddresses.isInetAddress(this.multicastAddress = System.getProperty(Const.MULTICAST_ADDRESS_KEY, "225.0.0.10")),
                "multicastAddress is not a valid IP");
        Preconditions.checkArgument(
                InetAddresses.forString(this.multicastAddress).isMulticastAddress(),
                "multicastAddress is not a valid multicast IP");
        this.multicastPort = Integer.parseInt(
                System.getProperty(Const.MULTICAST_PORT_KEY, "12345"));
        Preconditions.checkArgument(
                this.multicastPort > 0 && this.multicastPort < 65535,
                "multicastPort must be a number between 1 and 65535");
        Preconditions.checkArgument(
                InetAddresses.isInetAddress(this.bindAddress = System.getProperty(Const.BIND_ADDRESS_KEY, "127.0.0.1")),
                "bindAddress is not a valid IP");
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("channelId", channelId)
                .add("multicastAddress", multicastAddress)
                .add("multicastPort", multicastPort)
                .add("bindAddress", bindAddress)
                .toString();
    }
}
