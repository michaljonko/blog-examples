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
 * Date: 21.12.15
 * Time: 00:02
 */
package pl.coffeepower.blog.messagebus.aeron;

import com.google.common.base.StandardSystemProperty;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import pl.coffeepower.blog.messagebus.Configuration;

import uk.co.real_logic.aeron.Aeron;
import uk.co.real_logic.aeron.driver.MediaDriver;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.NonNull;
import lombok.extern.java.Log;

@Log
public final class AeronModule extends AbstractModule {

    protected void configure() {
//add configuration logic here
    }

    @Provides
    @Singleton
    @Inject
    public MediaDriver createMediaDriver(@NonNull Configuration configuration) {
        MediaDriver.Context context = new MediaDriver.Context()
                .dirsDeleteOnStart(true);
        context.aeronDirectoryName(StandardSystemProperty.JAVA_IO_TMPDIR.value() + File.separator + "aeron");
        return MediaDriver.launch(context);
    }

    @Provides
    @Singleton
    @Inject
    public Aeron.Context createAeronContext(@NonNull MediaDriver mediaDriver) {
        Aeron.Context context = new Aeron.Context();
        context.aeronDirectoryName(mediaDriver.aeronDirectoryName());
        return context;
    }
}
