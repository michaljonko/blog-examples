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

package pl.coffeepower.blog.examples;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

import java.util.List;
import java.util.Random;

import lombok.Getter;
import lombok.extern.java.Log;

import static pl.coffeepower.blog.examples.NumberUtils.isEvenNumber;
import static pl.coffeepower.blog.examples.NumberUtils.isOddNumber;

@Log
public final class LambdaExpExample {

    @Getter
    private final List<Integer> numbers;
    private final int numbersSize = 20;

    LambdaExpExample() {
        Random random = new Random();
        ImmutableList.Builder<Integer> listBuilder = ImmutableList.builder();
        for (int i = 0; i < numbersSize; i++) {
            listBuilder.add(random.nextInt(numbersSize));
        }
        numbers = listBuilder.build();
    }

    public static void main(String[] args) {
        LambdaExpExample example = new LambdaExpExample();
        Joiner joiner = Joiner.on(", ");
        log.info("numbers: " + joiner.join(example.getNumbers()));
        log.info("strange sort numbers: " + joiner.join(example.getStrangeSortedNumbers()));
        log.info("odd numbers: " + joiner.join(example.getOddNumbers()));
        log.info("even numbers: " + joiner.join(example.getEvenNumbers()));
    }

    public final Iterable<Integer> getStrangeSortedNumbers() {
        return Ordering.<Integer>from((number1, number2) -> number2 * 2 - number1 / 2).sortedCopy(numbers);
    }

    public final Iterable<Integer> getOddNumbers() {
        return Iterables.filter(numbers, number -> isOddNumber(number));
    }

    public final Iterable<Integer> getEvenNumbers() {
        return Iterables.filter(numbers, number -> isEvenNumber(number));
    }
}
