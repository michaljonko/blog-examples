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
 * Time: 23:04
 */
package pl.coffeepower.blog.messagebus.fastcast;

import com.google.common.net.InetAddresses;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;

import org.nustaq.fastcast.config.PhysicalTransportConf;
import org.nustaq.fastcast.config.PublisherConf;
import org.nustaq.fastcast.config.SubscriberConf;

import pl.coffeepower.blog.messagebus.Configuration;
import pl.coffeepower.blog.messagebus.Receiver;
import pl.coffeepower.blog.messagebus.Sender;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.NonNull;
import lombok.extern.java.Log;

@Log
public final class FastCastModule extends AbstractModule {

    private static final int TOPIC_ID = 1;

    protected void configure() {
        bind(Sender.class).to(FastCastSender.class).in(Scopes.SINGLETON);
        bind(Receiver.class).to(FastCastReceiver.class).in(Scopes.SINGLETON);
    }

    @Provides
    @Singleton
    @Inject
    private PhysicalTransportConf createPhysicalTransportConf(@NonNull Configuration configuration) {
        return new PhysicalTransportConf(configuration.getChannelId())
                .loopBack(InetAddresses.forString(configuration.getInterfaceAddress()).isLoopbackAddress())
                .interfaceAdr(configuration.getInterfaceAddress())
                .mulitcastAdr(configuration.getMulticastAddress())
                .port(configuration.getMulticastPort())
                .setDgramsize(1_500)
                .idleParkMicros(10)
                .spinLoopMicros(1_000);
    }

    @Provides
    @Singleton
    private PublisherConf createPublisherConf() {
        return new PublisherConf(TOPIC_ID)
                .heartbeatInterval(5)
                .numPacketHistory(30_000)
                .pps(5_000);
    }

    @Provides
    @Singleton
    private SubscriberConf createSubscriberConf() {
        return new SubscriberConf(TOPIC_ID)
                .receiveBufferPackets(30_000)
                .maxDelayRetransMS(1)
                .maxDelayNextRetransMS(1);
    }
}