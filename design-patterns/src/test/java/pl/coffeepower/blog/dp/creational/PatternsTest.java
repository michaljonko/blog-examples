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

import java.math.BigInteger;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class PatternsTest {

    private final Fixtures fixtures = new Fixtures();

    @Test
    public void shouldCreatePizzaWithBuilder() throws Exception {
        Pizza pizza;
        PizzaBuilder pizzaBuilder = PizzaBuilder.aPizza();

        pizza = pizzaBuilder.withName(fixtures.getPizzaName())
                .withPrice(fixtures.getPizzaPrice())
                .withComponents(fixtures.getPizzaComponents())
                .build();
        assertThat(pizza, is(fixtures.getExpectedPizza()));

        pizza = pizzaBuilder.withName("vege").build();
        assertThat(pizza, is(not(fixtures.getExpectedPizza())));
    }

    @Value
    private static final class Fixtures {
        String pizzaName = "vegetariana";
        BigInteger pizzaPrice = BigInteger.TEN;
        Set<String> pizzaComponents = ImmutableSet.of("tomatos", "onion", "beans");
        Pizza expectedPizza = new Pizza(pizzaName, pizzaComponents, pizzaPrice);
    }
}