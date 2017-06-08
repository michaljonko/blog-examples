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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import com.google.common.collect.ImmutableSet;

import org.junit.Test;

import pl.coffeepower.blog.dp.creational.PizzaAbstractFactory.PizzaFactory;
import pl.coffeepower.blog.dp.creational.vo.IPizza;
import pl.coffeepower.blog.dp.creational.vo.Pizza;
import pl.coffeepower.blog.dp.creational.vo.PizzaName;
import pl.coffeepower.blog.dp.creational.vo.PizzaPrototype;

import java.math.BigInteger;

public class CreationalPatternsTest {

  @Test
  public void shouldCreatePizzaVegetarianaWithBuilder() throws Exception {
    PizzaBuilder pizzaBuilder = PizzaBuilder.aPizza();

    IPizza pizzaVegetariana = pizzaBuilder
        .withName(Fixtures.PIZZA_NAME)
        .withPrice(Fixtures.PIZZA_PRICE)
        .withComponents(Fixtures.PIZZA_COMPONENTS)
        .build();

    assertThat(pizzaVegetariana, is(Fixtures.EXPECTED_PIZZA));
    assertThat(pizzaBuilder.withName(PizzaName.FAMILY).build(),
        is(not(Fixtures.EXPECTED_PIZZA)));
  }

  @Test
  public void shouldCreatePizzaVegetarianaWithAbstractFactory() throws Exception {
    PizzaFactory pizzaFactory = PizzaAbstractFactory.getFactory(Fixtures.PIZZA_NAME);

    IPizza pizzaVegetariana = pizzaFactory.createPizza();

    assertThat(pizzaVegetariana, is(Fixtures.EXPECTED_PIZZA));
    assertThat(pizzaVegetariana, not(sameInstance(pizzaFactory.createPizza())));
  }

  @Test
  public void shouldCreateAndClonePizzaVegetarianaWithPrototype() throws Exception {
    PizzaPrototype pizzaVegetarianaPrototype = new PizzaPrototype(
        Fixtures.PIZZA_NAME.name(), Fixtures.PIZZA_COMPONENTS, Fixtures.PIZZA_PRICE);

    IPizza pizzaVegetarianaClone = pizzaVegetarianaPrototype.clone();

    assertThat(pizzaVegetarianaPrototype, is(Fixtures.EXPECTED_PIZZA));
    assertThat(pizzaVegetarianaPrototype, not(sameInstance(pizzaVegetarianaClone)));
    assertThat(pizzaVegetarianaClone, is(pizzaVegetarianaPrototype));
  }

  @Test
  public void shouldCreatePizzaWithSingleton() throws Exception {
    PizzaSingleton singleton = PizzaSingleton.getInstance();

    IPizza pizzaVegetariana = singleton.getVegetarianaPizza();

    assertThat(pizzaVegetariana, is(Fixtures.EXPECTED_PIZZA));
    assertThat(pizzaVegetariana, sameInstance(singleton.getVegetarianaPizza()));
  }

  private static final class Fixtures {

    private static final PizzaName PIZZA_NAME = PizzaName.VEGETARIANA;
    private static final BigInteger PIZZA_PRICE = BigInteger.valueOf(90L);
    private static final ImmutableSet<String> PIZZA_COMPONENTS = ImmutableSet.of("tomatoes", "onion", "beans");
    private static final IPizza EXPECTED_PIZZA = new Pizza(PIZZA_NAME.name(), PIZZA_COMPONENTS, PIZZA_PRICE);
  }
}