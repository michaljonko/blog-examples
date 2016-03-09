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

import java.util.List;
import java.util.Random;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;

@Log4j2
public final class StreamsExample {

    private final List<Integer> numbers;
    private final int numbersSize = 10;

    StreamsExample() {
        Random random = new Random();
        ImmutableList.Builder<Integer> listBuilder = ImmutableList.builder();
        for (int i = 0; i < numbersSize; i++) {
            listBuilder.add(random.nextInt(numbersSize));
        }
        numbers = listBuilder.build();
    }

    public static void main(String[] args) {
        StreamsExample example = new StreamsExample();
        log.info("odd numbers: " + Joiner.on(", ").join(example.getOddNumbers()));
        example.changeOddToEvenNumbers()
                .forEach(number -> log.info("odd value changed to even:" + number));
    }

    public final List<Integer> getOddNumbers() {
        return StreamSupport.stream(numbers)
                .filter(number -> NumberUtils.isOddNumber(number))
                .collect(Collectors.toList());
    }

    public final List<Integer> changeOddToEvenNumbers() {
        return StreamSupport.stream(numbers)
                .filter(number -> NumberUtils.isOddNumber(number))
                .map(value -> value + 1)
                .collect(Collectors.toList());
    }
}
