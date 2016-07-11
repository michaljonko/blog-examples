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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import lombok.NonNull;

import java.math.BigInteger;
import java.util.Map;

public final class PizzaAbstractFactory {

    private final static Map<String, PizzaFactory> FACTORIES = ImmutableMap.of(
            VegetarianaPizzaFactory.pizzaName, new VegetarianaPizzaFactory(),
            FamilyPizzaFactory.pizzaName, new FamilyPizzaFactory()
    );

    public static PizzaFactory getFactory(@NonNull String pizzaName) {
        return FACTORIES.get(pizzaName);
    }

    public interface PizzaFactory {

        IPizza createPizza();
    }

    private static final class VegetarianaPizzaFactory implements PizzaFactory {

        private static final String pizzaName = "vegetariana";

        @Override
        public IPizza createPizza() {
            return new Pizza(pizzaName, Sets.newHashSet("tomatoes", "onion", "beans"), BigInteger.TEN);
        }
    }

    private static final class FamilyPizzaFactory implements PizzaFactory {

        private static final String pizzaName = "family";

        @Override
        public IPizza createPizza() {
            return new Pizza(pizzaName, Sets.newHashSet("tomatoes", "cheese", "meat"), BigInteger.ONE);
        }
    }
}
