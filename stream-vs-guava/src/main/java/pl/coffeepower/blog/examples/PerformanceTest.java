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

import com.google.common.io.Resources;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import lombok.Cleanup;

@State(Scope.Benchmark)
@Warmup(iterations = 10)
@Measurement(iterations = 30)
@Threads(1)
@Fork(value = 3)
public class PerformanceTest {

    private final GuavaDishService guavaDishService = new GuavaDishService();
    private final StreamsDishService streamsDishService = new StreamsDishService();
    private List<Dish> dishes;

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(PerformanceTest.class.getSimpleName())
                .shouldFailOnError(true)
                .build()
        ).run();
    }

    @Setup(Level.Invocation)
    public void setup() throws IOException, ClassNotFoundException {
        @Cleanup
        ObjectInputStream resourcesStream = new ObjectInputStream(Resources.getResource("dishes.bin").openStream());
        dishes = (List<Dish>) resourcesStream.readObject();
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureGuava() {
        guavaDishService.getSortedFitDishNamesWithMeat(dishes).size();
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureStream() {
        streamsDishService.getSortedFitDishNamesWithMeat(dishes).size();
    }
}
