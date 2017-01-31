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
import com.google.common.collect.Sets;

import lombok.NoArgsConstructor;
import lombok.NonNull;

import pl.coffeepower.blog.dp.creational.vo.IPizza;
import pl.coffeepower.blog.dp.creational.vo.Pizza;
import pl.coffeepower.blog.dp.creational.vo.PizzaName;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Set;

@NoArgsConstructor(staticName = "aPizza")
public final class PizzaBuilder {

  private PizzaName pizzaName;
  private Set<String> components = Sets.newHashSet();
  private BigInteger price = BigInteger.ZERO;

  public IPizza build() {
    return new Pizza(pizzaName.name(), ImmutableSet.copyOf(components), price);
  }

  public PizzaBuilder withName(@NonNull PizzaName value) {
    pizzaName = value;
    return this;
  }

  public PizzaBuilder withPrice(BigInteger value) {
    price = value;
    return this;
  }

  public PizzaBuilder withComponents(Collection<String> values) {
    components.clear();
    components.addAll(values);
    return this;
  }

  public PizzaBuilder addComponent(String value) {
    components.add(value);
    return this;
  }

}
