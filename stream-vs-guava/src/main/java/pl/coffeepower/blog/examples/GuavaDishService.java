/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Michał Jonko
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

package pl.coffeepower.blog.examples;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import lombok.NonNull;

import java.util.List;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;

public final class GuavaDishService implements DishService {

    private static final Predicate<Dish> DISH_WITH_MEAT_PREDICATE = new Predicate<Dish>() {
        @Override
        public boolean apply(Dish dish) {
            return dish.isWithMeat();
        }
    };
    private static final Function<Dish, String> DISH_NAME_FUNCTION = new Function<Dish, String>() {
        @Override
        public String apply(Dish dish) {
            return dish.getName();
        }
    };
    private static final Predicate<Dish> FIT_DISH_WITHOUT_MEAT_PREDICATE = new Predicate<Dish>() {
        @Override
        public boolean apply(Dish dish) {
            return !dish.isWithMeat() && dish.getCalories() < 200;
        }
    };
    private static final Predicate<Dish> FIT_DISH_WITH_MEAT_PREDICATE = new Predicate<Dish>() {
        @Override
        public boolean apply(Dish dish) {
            return dish.isWithMeat() && dish.getCalories() < 200;
        }
    };
    private static final Ordering<Comparable> NATURAL_ORDERING = Ordering.natural();

    @Override
    public List<String> findDishNamesWithMeat(@NonNull List<Dish> dishes) {
        return Lists.newArrayList(
                transform(
                        filter(dishes, DISH_WITH_MEAT_PREDICATE),
                        DISH_NAME_FUNCTION
                ));
    }

    @Override
    public List<Dish> findFitDishesWithoutMeat(@NonNull List<Dish> dishes) {
        return Lists.newArrayList(
                filter(dishes, FIT_DISH_WITHOUT_MEAT_PREDICATE));
    }

    @Override
    public List<String> getSortedFitDishNamesWithMeat(@NonNull List<Dish> dishes) {
        return NATURAL_ORDERING.sortedCopy(
                Lists.newArrayList(
                        transform(
                                filter(dishes, FIT_DISH_WITH_MEAT_PREDICATE),
                                DISH_NAME_FUNCTION
                        )));
    }
}
