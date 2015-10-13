package pl.coffeepower.blog.examples;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
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

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 10)
@Measurement(iterations = 10)
@Threads(1)
@Fork(value = 3, jvmArgsAppend = "-ea")
public class JMHPerformanceTest {

    private static final boolean FAIL_ON_ERROR = true;

    private final String word = UUID.randomUUID().toString();
    private WordsFrequencyCounter simpleWordsFrequencyCounter;
    private WordsFrequencyCounter atomicWordsFrequencyCounter;
    private WordsFrequencyCounter arrayWordsFrequencyCounter;
    private WordsFrequencyCounter classWordsFrequencyCounter;

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(JMHPerformanceTest.class.getSimpleName())
                .shouldFailOnError(FAIL_ON_ERROR)
                .build()
        ).run();
    }

    @Setup
    public void setup() {
        simpleWordsFrequencyCounter = new SimpleWordsFrequencyCounter();
        atomicWordsFrequencyCounter = new AtomicWordsFrequencyCounter();
        arrayWordsFrequencyCounter = new ArrayWordsFrequencyCounter();
        classWordsFrequencyCounter = new ClassWordsFrequencyCounter();
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureClassicValues() {
        simpleWordsFrequencyCounter.increase(word);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureAtomicValues() {
        atomicWordsFrequencyCounter.increase(word);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureArrayValues() {
        arrayWordsFrequencyCounter.increase(word);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureClassValues() {
        classWordsFrequencyCounter.increase(word);
    }
}
