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

package pl.coffeepower.blog.examples.counters;

import com.google.common.collect.Maps;

import lombok.NoArgsConstructor;

import pl.coffeepower.blog.examples.WordsFrequencyCounter;

import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
public final class ArrayWordsFrequencyCounter implements WordsFrequencyCounter {

    private final Map<String, int[]> map = Maps.newConcurrentMap();

    @Override
    public final void increase(String word) {
        int[] counter = map.get(word);
        if (counter == null) {
            counter = new int[]{1};
            map.put(word, counter);
        } else {
            counter[0]++;
        }
    }

    @Override
    public final Map<String, Integer> wordsFrequency() {
        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()[0]));
    }
}
