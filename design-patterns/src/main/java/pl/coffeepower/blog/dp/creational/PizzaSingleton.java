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

package pl.coffeepower.blog.dp.creational;

import com.google.common.collect.ImmutableSet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import pl.coffeepower.blog.dp.creational.vo.IPizza;
import pl.coffeepower.blog.dp.creational.vo.Pizza;
import pl.coffeepower.blog.dp.creational.vo.PizzaName;

import java.math.BigInteger;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PizzaSingleton {

  private final IPizza vegetarianaPizza = new Pizza(
      PizzaName.VEGETARIANA.name(),
      ImmutableSet.of("tomatoes", "onion", "beans"),
      BigInteger.valueOf(90L));
  private final IPizza familyPizza = new Pizza(
      PizzaName.FAMILY.name(),
      ImmutableSet.of("tomatoes", "cheese", "meat"),
      BigInteger.valueOf(100L));

  public static PizzaSingleton getInstance() {
    return PizzaSingletonHelper.INSTANCE;
  }

  private final static class PizzaSingletonHelper {

    private static final PizzaSingleton INSTANCE = new PizzaSingleton();
  }
}
