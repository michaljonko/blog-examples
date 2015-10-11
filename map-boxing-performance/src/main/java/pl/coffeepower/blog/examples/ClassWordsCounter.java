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

import com.google.common.collect.Maps;

import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public final class ClassWordsCounter implements WordsCounter {

    private final Map<String, Value> map = Maps.newConcurrentMap();

    @Override
    public void increaseAll() {
        map.forEach((key, value) -> value.inc());
    }

    @Override
    public final void increase(String word) {
        Value counter = map.get(word);
        if (counter == null) {
            counter = new Value(1);
            map.put(word, counter);
        } else {
            counter.inc();
        }
    }

    @Override
    public final void decrease(String word) {
        Value counter = map.get(word);
        if (counter != null && counter.getValue() > 0) {
            counter.dec();
        }
    }

    @Override
    public final Map<String, ?> wordsFrequency() {
        return map;
    }

    @EqualsAndHashCode
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Value {

        @Getter
        private int value;

        public final void inc() {
            value++;
        }

        public final void dec() {
            value--;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
}
