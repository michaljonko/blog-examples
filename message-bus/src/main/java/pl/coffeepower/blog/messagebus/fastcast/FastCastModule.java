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
import com.google.inject.name.Names;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import org.nustaq.fastcast.config.PhysicalTransportConf;
import org.nustaq.fastcast.config.PublisherConf;
import org.nustaq.fastcast.config.SubscriberConf;
import org.nustaq.fastcast.util.FCLog;

import pl.coffeepower.blog.messagebus.Configuration;
import pl.coffeepower.blog.messagebus.Configuration.Const;
import pl.coffeepower.blog.messagebus.Publisher;
import pl.coffeepower.blog.messagebus.Subscriber;
import pl.coffeepower.blog.messagebus.util.BytesEventFactory;

import java.util.Properties;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.NonNull;
import lombok.extern.java.Log;

@Log
public final class FastCastModule extends AbstractModule {

    private final Properties properties = new Properties();

    public FastCastModule() {
        FCLog.get().setLogLevel(FCLog.SEVER);
        properties.setProperty(Const.PUBLISHER_NAME_KEY,
                System.getProperty(Const.PUBLISHER_NAME_KEY, "PUB"));
        properties.setProperty(Const.SUBSCRIBER_NAME_KEY,
                System.getProperty(Const.SUBSCRIBER_NAME_KEY, "SUB"));
    }

    protected void configure() {
        bind(Publisher.class).to(FastCastPublisher.class);
        bind(Subscriber.class).to(FastCastSubscriber.class);
        bind(Subscriber.Handler.class).to(FastCastReceiveHandler.class);
        Names.bindProperties(binder(), properties);
    }

    @Provides
    @Singleton
    @Inject
    private PhysicalTransportConf createPhysicalTransportConf(@NonNull Configuration configuration) {
        log.info("Creating PhysicalTransportConf with Configuration: " + configuration);
        return new PhysicalTransportConf(configuration.getChannelId())
                .loopBack(InetAddresses.forString(configuration.getBindAddress())
                        .isLoopbackAddress())
                .interfaceAdr(configuration.getBindAddress())
                .mulitcastAdr(configuration.getMulticastAddress())
                .port(configuration.getMulticastPort())
                .setDgramsize(FastCastConst.DATAGRAM_SIZE)
                .idleParkMicros(FastCastConst.IDLE_PARK_MICROS)
                .spinLoopMicros(FastCastConst.SPIN_LOOP_MICROS);
    }

    @Provides
    @Singleton
    private PublisherConf createPublisherConf() {
        return new PublisherConf(FastCastConst.TOPIC_ID)
                .numPacketHistory(FastCastConst.PUBLISHER_PACKET_HISTORY)
                .pps(FastCastConst.PUBLISHER_PPS)
                .heartbeatInterval(FastCastConst.PUBLISHER_HEARTBEAT_INTERVAL);
    }

    @Provides
    @Singleton
    private SubscriberConf createSubscriberConf() {
        return new SubscriberConf(FastCastConst.TOPIC_ID)
                .receiveBufferPackets(FastCastConst.SUBSCRIBER_BUFFER_PACKETS)
                .maxDelayRetransMS(FastCastConst.SUBSCRIBER_MAX_DELAY_RETRANS_MS)
                .maxDelayNextRetransMS(FastCastConst.SUBSCRIBER_MAX_DELAY_NEXT_RETRANS_MS)
                .unreliable(FastCastConst.SUBSCRIBER_UNRELIABLE);
    }

    @Provides
    private Disruptor<BytesEventFactory.BytesEvent> createDisruptor() {
        return new Disruptor<>(
                new BytesEventFactory(), FastCastConst.DISRUPTOR_SIZE, Executors.newCachedThreadPool(), ProducerType.SINGLE, new BlockingWaitStrategy());
    }
}
