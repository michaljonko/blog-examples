package pl.coffeepower.blog.messagebus.raw;

import com.google.inject.AbstractModule;

import lombok.extern.log4j.Log4j2;

import pl.coffeepower.blog.messagebus.Publisher;
import pl.coffeepower.blog.messagebus.Subscriber;
import pl.coffeepower.blog.messagebus.util.LoggerReceiveHandler;

@Log4j2
public final class RawModule extends AbstractModule {

    protected void configure() {
        bind(Publisher.class).to(RawPublisher.class);
        bind(Subscriber.class).to(RawSubscriber.class);
        bind(Subscriber.Handler.class).to(LoggerReceiveHandler.class);
    }
}
