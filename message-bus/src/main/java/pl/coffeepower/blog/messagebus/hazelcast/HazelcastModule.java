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

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import com.hazelcast.config.Config;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import pl.coffeepower.blog.messagebus.Configuration;
import pl.coffeepower.blog.messagebus.Publisher;
import pl.coffeepower.blog.messagebus.Subscriber;
import pl.coffeepower.blog.messagebus.util.LoggerReceiveHandler;

import javax.inject.Inject;
import javax.inject.Singleton;

@Log4j2
public final class HazelcastModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Publisher.class).to(HazelcastPublisher.class);
        bind(Subscriber.class).to(HazelcastSubscriber.class);
        bind(Subscriber.Handler.class).to(LoggerReceiveHandler.class);
    }

    @Provides
    @Inject
    private HazelcastInstance getHazelcastInstance(@NonNull Config config) {
        return Hazelcast.newHazelcastInstance(config);
    }

    @Provides
    @Inject
    private ITopic<byte[]> createTopic(@NonNull HazelcastInstance hazelcastInstance, @NonNull Configuration configuration) {
        return hazelcastInstance.getTopic(String.valueOf(configuration.getTopicId()));
    }

    @Provides
    @Singleton
    private Config createConfig(@NonNull Configuration configuration) {
        Config config = new Config();
        NetworkConfig networkConfig = config.getNetworkConfig()
                .setPublicAddress(configuration.getBindAddress())
                .setPort(configuration.getMulticastPort())
                .setReuseAddress(true);
        configuration.getMulticastAddress();
        return config;
    }
}
