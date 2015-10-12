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
    private WordsFrequencyCounter intWordsFrequencyCounter;
    private WordsFrequencyCounter atomicIntWordsFrequencyCounter;
    private WordsFrequencyCounter arrayIntWordsFrequencyCounter;
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
        intWordsFrequencyCounter = new IntWordsFrequencyCounter();
        atomicIntWordsFrequencyCounter = new AtomicIntWordsFrequencyCounter();
        arrayIntWordsFrequencyCounter = new ArrayIntWordsFrequencyCounter();
        classWordsFrequencyCounter = new ClassWordsFrequencyCounter();
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureClassicValues() {
        intWordsFrequencyCounter.increase(word);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureAtomicValues() {
        atomicIntWordsFrequencyCounter.increase(word);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureArrayValues() {
        arrayIntWordsFrequencyCounter.increase(word);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureClassValues() {
        classWordsFrequencyCounter.increase(word);
    }
}
