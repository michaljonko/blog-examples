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

import lombok.Value;

import org.junit.Test;

import pl.coffeepower.blog.dp.creational.PizzaAbstractFactory.PizzaFactory;

import java.math.BigInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static pl.coffeepower.blog.dp.creational.PizzaPrototype.PizzaProto;

public class CreationalPatternsTest {

    private final Fixtures fixtures = new Fixtures();

    @Test
    public void shouldCreatePizzaWithBuilder() throws Exception {
        PizzaBuilder pizzaBuilder = PizzaBuilder.aPizza();
        assertThat(pizzaBuilder
                        .withName(fixtures.getPizzaName())
                        .withPrice(fixtures.getPizzaPrice())
                        .withComponents(fixtures.getPizzaComponents())
                        .build(),
                is(fixtures.getExpectedPizza()));
        assertThat(pizzaBuilder
                        .withName("vege").build(),
                is(not(fixtures.getExpectedPizza())));
    }

    @Test
    public void shouldCreatePizzaWithAbstractFactory() throws Exception {
        PizzaFactory pizzaFactory = PizzaAbstractFactory.getFactory(fixtures.getPizzaName());
        assertThat(pizzaFactory.createPizza(), is(fixtures.getExpectedPizza()));
    }

    @Test
    public void shouldCreateCloneablePizzaWithPrototype() throws Exception {
        PizzaProto pizzaPrototype = new PizzaProto(fixtures.getPizzaName(), fixtures.getPizzaComponents(), fixtures.getPizzaPrice());
        IPizza pizzaClone = pizzaPrototype.clone();
        assertThat(pizzaPrototype, is(fixtures.expectedPizza));
        assertThat(pizzaClone, is(fixtures.expectedPizza));
        assertThat(pizzaClone, not(sameInstance(pizzaPrototype)));
        assertThat(pizzaClone, not(sameInstance(pizzaPrototype)));
    }

    @Value
    private static final class Fixtures {
        String pizzaName = "vegetariana";
        BigInteger pizzaPrice = BigInteger.TEN;
        ImmutableSet<String> pizzaComponents = ImmutableSet.of("tomatoes", "onion", "beans");
        IPizza expectedPizza = new Pizza(pizzaName, pizzaComponents, pizzaPrice);
    }
}