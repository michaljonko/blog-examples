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

package pl.coffeepower.blog.messagebus.util;

import com.google.common.base.MoreObjects;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;

import lombok.NonNull;

import java.util.Collections;

public final class DefaultBasicService implements BasicService {

  private final ServiceManager serviceManager;

  public DefaultBasicService(@NonNull Service service) {
    this.serviceManager = new ServiceManager(Collections.singleton(service));
  }

  @Override
  public void start() throws Exception {
    serviceManager.startAsync().awaitHealthy();
  }

  @Override
  public void stop() throws Exception {
    serviceManager.stopAsync().awaitStopped();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("serviceManager", serviceManager)
        .toString();
  }
}
