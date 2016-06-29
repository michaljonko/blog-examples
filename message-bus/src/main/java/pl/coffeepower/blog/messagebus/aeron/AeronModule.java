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

package pl.coffeepower.blog.messagebus.aeron;

import com.google.common.base.StandardSystemProperty;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import pl.coffeepower.blog.messagebus.Publisher;
import pl.coffeepower.blog.messagebus.Subscriber;
import pl.coffeepower.blog.messagebus.util.LoggerReceiveHandler;

import uk.co.real_logic.aeron.Aeron;
import uk.co.real_logic.aeron.Subscription;
import uk.co.real_logic.aeron.driver.MediaDriver;
import uk.co.real_logic.aeron.driver.ThreadingMode;
import uk.co.real_logic.agrona.concurrent.BusySpinIdleStrategy;
import uk.co.real_logic.agrona.concurrent.IdleStrategy;

import java.io.File;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

@Log4j2
public final class AeronModule extends AbstractModule {

    protected void configure() {
        bind(Publisher.class).to(AeronPublisher.class);
        bind(Subscriber.class).to(AeronSubscriber.class);
        bind(Subscriber.Handler.class).to(LoggerReceiveHandler.class);
    }

    @Provides
    @Singleton
    private MediaDriver createMediaDriver() {
        MediaDriver.Context context = new MediaDriver.Context()
                .threadingMode(ThreadingMode.SHARED_NETWORK)
                .dirsDeleteOnStart(true);
        context.aeronDirectoryName(
                System.getProperty("aeron.dir",
                        StandardSystemProperty.JAVA_IO_TMPDIR.value() + File.separator + "aeron" + File.separator + UUID.randomUUID().toString()));
        return MediaDriver.launch(context);
    }

    @Provides
    @Singleton
    @Inject
    private Aeron.Context createAeronContext(@NonNull MediaDriver mediaDriver) {
        Aeron.Context context = new Aeron.Context();
        context.aeronDirectoryName(mediaDriver.aeronDirectoryName());
        context.errorHandler(throwable -> log.catching(throwable));
        if (log.isDebugEnabled()) {
            context.availableImageHandler(image -> {
                Subscription subscription = image.subscription();
                log.debug("Image is available: streamId={}, channel={}, sessionId={}, identity={}", subscription.channel(), subscription.streamId(), image.sessionId(), image.sourceIdentity());
            });
            context.unavailableImageHandler(image -> {
                Subscription subscription = image.subscription();
                log.debug("Image is unavailable: streamId={}, channel={}, sessionId={}", subscription.channel(), subscription.streamId(), image.sessionId());
            });
        }
        return context;
    }

    @Provides
    @Singleton
    @Inject
    private Aeron createAeron(@NonNull Aeron.Context context) {
        if (log.isDebugEnabled()) {
            log.info("aeronDirectoryName={}", context.aeronDirectoryName());
        }
        return Aeron.connect(context);
    }

    @Provides
    private IdleStrategy createIdleStrategy() {
        return new BusySpinIdleStrategy();
    }
}
