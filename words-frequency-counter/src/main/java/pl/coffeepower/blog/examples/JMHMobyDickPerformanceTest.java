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

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import lombok.extern.log4j.Log4j2;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
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

import pl.coffeepower.blog.examples.counters.ArrayWordsFrequencyCounter;
import pl.coffeepower.blog.examples.counters.AtomicWordsFrequencyCounter;
import pl.coffeepower.blog.examples.counters.ClassWordsFrequencyCounter;
import pl.coffeepower.blog.examples.counters.SimpleWordsFrequencyCounter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Log4j2
@State(Scope.Benchmark)
@Warmup(iterations = 10)
@Measurement(iterations = 10)
@Threads(1)
@Fork(value = 3, jvmArgsAppend = "-ea")
public class JMHMobyDickPerformanceTest {

    private static final boolean FAIL_ON_ERROR = true;

    private final List<String> words = Lists.newLinkedList();

    private WordsFrequencyCounter simpleWordsFrequencyCounter;
    private WordsFrequencyCounter atomicWordsFrequencyCounter;
    private WordsFrequencyCounter arrayWordsFrequencyCounter;
    private WordsFrequencyCounter classWordsFrequencyCounter;

    {
        try {
            Tokenizer tokenizer = new TokenizerME(
                    new TokenizerModel(Resources.getResource("en-token.bin")));
            Resources.readLines(Resources.getResource("mobydick.txt"), Charsets.UTF_8)
                    .forEach(line -> words.addAll(Arrays.asList(tokenizer.tokenize(line))));
        } catch (IOException e) {
            log.error("Exception while init", e);
            System.exit(-1);
        }
    }

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(JMHMobyDickPerformanceTest.class.getSimpleName())
                .shouldFailOnError(FAIL_ON_ERROR)
                .build()
        ).run();
    }

    @Setup
    public void setup() throws IOException {
        this.simpleWordsFrequencyCounter = new SimpleWordsFrequencyCounter();
        this.atomicWordsFrequencyCounter = new AtomicWordsFrequencyCounter();
        this.arrayWordsFrequencyCounter = new ArrayWordsFrequencyCounter();
        this.classWordsFrequencyCounter = new ClassWordsFrequencyCounter();
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureClassicValues() {
        this.words.forEach(word -> simpleWordsFrequencyCounter.increase(word));
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureAtomicValues() {
        this.words.forEach(word -> atomicWordsFrequencyCounter.increase(word));
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureArrayValues() {
        this.words.forEach(word -> arrayWordsFrequencyCounter.increase(word));
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureClassValues() {
        this.words.forEach(word -> classWordsFrequencyCounter.increase(word));
    }
}
