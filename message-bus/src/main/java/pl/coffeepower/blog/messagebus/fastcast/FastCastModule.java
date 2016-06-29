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

package pl.coffeepower.blog.messagebus.fastcast;

import com.google.common.net.InetAddresses;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import org.nustaq.fastcast.api.FastCast;
import org.nustaq.fastcast.config.PhysicalTransportConf;
import org.nustaq.fastcast.config.PublisherConf;
import org.nustaq.fastcast.config.SubscriberConf;
import org.nustaq.fastcast.util.FCLog;

import pl.coffeepower.blog.messagebus.Configuration;
import pl.coffeepower.blog.messagebus.Configuration.Const;
import pl.coffeepower.blog.messagebus.Publisher;
import pl.coffeepower.blog.messagebus.Subscriber;
import pl.coffeepower.blog.messagebus.util.LoggerReceiveHandler;

import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Singleton;

@Log4j2
public final class FastCastModule extends AbstractModule {

    private final Properties properties = new Properties();

    public FastCastModule() {
        if (log.isDebugEnabled()) {
            FCLog.get().setLogLevel(FCLog.DEBUG);
        } else {
            FCLog.get().setLogLevel(FCLog.SEVER);
        }
        properties.setProperty(Const.PUBLISHER_NAME_KEY,
                System.getProperty(Const.PUBLISHER_NAME_KEY, "PUB"));
        properties.setProperty(Const.SUBSCRIBER_NAME_KEY,
                System.getProperty(Const.SUBSCRIBER_NAME_KEY, "SUB"));
    }

    protected void configure() {
        bind(Publisher.class).to(FastCastPublisher.class);
        bind(Subscriber.class).to(FastCastSubscriber.class);
        bind(Subscriber.Handler.class).to(LoggerReceiveHandler.class);
        Names.bindProperties(binder(), properties);
    }

    @Provides
    private FastCast getFastCast() {
        return FastCast.getFastCast();
    }

    @Provides
    @Singleton
    @Inject
    private PhysicalTransportConf createPhysicalTransportConf(@NonNull Configuration configuration) {
        log.info("Creating PhysicalTransportConf with Configuration: {}", configuration);
        return new PhysicalTransportConf(FastCastConst.STREAM_NAME)
                .loopBack(InetAddresses.forString(configuration.getBindAddress()).isLoopbackAddress())
                .interfaceAdr(configuration.getBindAddress())
                .mulitcastAdr(configuration.getMulticastAddress())
                .port(configuration.getMulticastPort());
    }

    @Provides
    @Singleton
    @Inject
    private PublisherConf createPublisherConf(@NonNull Configuration configuration) {
        return new PublisherConf(configuration.getTopicId());
    }

    @Provides
    @Singleton
    @Inject
    private SubscriberConf createSubscriberConf(@NonNull Configuration configuration) {
        return new SubscriberConf(configuration.getTopicId());
    }
}
