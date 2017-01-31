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

package pl.coffeepower.blog.springvsguice.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import lombok.extern.slf4j.Slf4j;

import pl.coffeepower.blog.springvsguice.Config;
import pl.coffeepower.blog.springvsguice.MapContainer;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Slf4j
public class GuiceConfig extends AbstractModule {

  public GuiceConfig() {
    log.info("Using {}", this.getClass().getSimpleName());
  }

  protected void configure() {
  }

  @Singleton
  @Provides
  @Named(Config.MAP_QUALIFIER)
  private Map<String, String> createMap() {
    Map<String, String> map = new HashMap<>();
    map.put("1", "Guice");
    map.put("2", "framework");
    return map;
  }

  @Singleton
  @Provides
  private Map<String, String> createAnotherMap() {
    Map<String, String> map = new HashMap<>();
    map.put("1", "Other");
    map.put("2", "framework");
    return map;
  }

  @Provides
  @Inject
  private MapContainer createMapContainer(@Named(Config.MAP_QUALIFIER) Map<String, String> map) {
    return new MapContainer(map);
  }
}
