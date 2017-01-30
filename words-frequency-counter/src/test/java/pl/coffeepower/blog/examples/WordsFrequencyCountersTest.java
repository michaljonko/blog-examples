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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import pl.coffeepower.blog.examples.counters.ArrayWordsFrequencyCounter;
import pl.coffeepower.blog.examples.counters.AtomicWordsFrequencyCounter;
import pl.coffeepower.blog.examples.counters.ClassWordsFrequencyCounter;
import pl.coffeepower.blog.examples.counters.SimpleWordsFrequencyCounter;

public class WordsFrequencyCountersTest {

  private final String word = "qwertyuiopasdfghjklzxcvbnm";

  @Test
  public void testIntegerWordsCounter() throws Exception {
    WordsFrequencyCounter valuesMap = new SimpleWordsFrequencyCounter();
    valuesMap.increase(word);
    valuesMap.increase(word);
    valuesMap.wordsFrequency().values()
        .forEach(value -> assertThat("ASSERT!", value.intValue(), is(2)));
  }

  @Test
  public void testAtomicIntWordsCounter() throws Exception {
    WordsFrequencyCounter valuesMap = new AtomicWordsFrequencyCounter();
    valuesMap.increase(word);
    valuesMap.increase(word);
    valuesMap.wordsFrequency().values()
        .forEach(value -> assertThat("ASSERT!", value.intValue(), is(2)));
  }

  @Test
  public void testArrayIntWordsCounter() throws Exception {
    WordsFrequencyCounter valuesMap = new ArrayWordsFrequencyCounter();
    valuesMap.increase(word);
    valuesMap.increase(word);
    valuesMap.wordsFrequency().values()
        .forEach(value -> assertThat("ASSERT!", value.intValue(), is(2)));
  }

  @Test
  public void testClassWordsCounter() throws Exception {
    WordsFrequencyCounter valuesMap = new ClassWordsFrequencyCounter();
    valuesMap.increase(word);
    valuesMap.increase(word);
    valuesMap.wordsFrequency().values()
        .forEach(value -> assertThat("ASSERT!", value.intValue(), is(2)));
  }
}
