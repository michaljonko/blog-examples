/*
 * Created by IntelliJ IDEA.
 * User: Michal_Jonko
 * Date: 2/23/2016
 * Time: 4:42 PM
 */
package pl.coffeepower.blog.messagebus.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Provider;

import com.lmax.disruptor.LiteBlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import pl.coffeepower.blog.messagebus.util.BytesEventFactory.BytesEvent;

final class BytesEventDisruptorProvider implements Provider<Disruptor<BytesEvent>> {

    public static final int DISRUPTOR_SIZE = 64;

    public Disruptor<BytesEvent> get() {
        return new Disruptor<>(new BytesEventFactory(), DISRUPTOR_SIZE,
                new ThreadFactoryBuilder().setNameFormat("bytes-event-disruptor-%d").setDaemon(true).build(),
                ProducerType.SINGLE, new LiteBlockingWaitStrategy());
    }
}
