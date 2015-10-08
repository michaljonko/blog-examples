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

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertTrue;

public class MapsTest {

    @Test
    public void testName() throws Exception {
        ClassicValuesMap valuesMap = new ClassicValuesMap();
        valuesMap.increase();
        for (Integer i : valuesMap.getMap().values()) {
            assertTrue("i=0", i > 0);
        }
    }

    @Test
    public void testName2() throws Exception {
        AtomicValuesMap valuesMap = new AtomicValuesMap();
        valuesMap.increase();
        for (AtomicInteger i : valuesMap.getMap().values()) {
            assertTrue("i=0", i.get() > 0);
        }
    }

    @Test
    public void testName3() throws Exception {
        ArrayValuesMap valuesMap = new ArrayValuesMap();
        valuesMap.increase();
        for (int[] i : valuesMap.getMap().values()) {
            assertTrue("i=0", i[0] > 0);
        }
    }
}