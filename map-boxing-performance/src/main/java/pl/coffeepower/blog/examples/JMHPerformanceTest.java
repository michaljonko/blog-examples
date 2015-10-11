package pl.coffeepower.blog.examples;

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

import java.util.UUID;

@State(Scope.Benchmark)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
@Threads(1)
@Fork(value = 1, jvmArgsAppend = "-ea")
public class JMHPerformanceTest {

    private static final boolean FAIL_ON_ERROR = true;

    private final String word = UUID.randomUUID().toString();
    private WordsCounter classicValuesMap;
    private WordsCounter atomicIntWordsCounter;
    private WordsCounter arrayAsValueWordsCounter;
    private WordsCounter classValuesMap;

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(JMHPerformanceTest.class.getSimpleName())
                .shouldFailOnError(FAIL_ON_ERROR)
                .build()
        ).run();
    }

    @Setup
    public void setup() {
        classicValuesMap = new IntegerWordsCounter();
        atomicIntWordsCounter = new AtomicIntWordsCounter();
        arrayAsValueWordsCounter = new ArrayIntWordsCounter();
        classValuesMap = new ClassWordsCounter();
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureClassicValues() {
        classicValuesMap.increase(word);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureAtomicValues() {
        atomicIntWordsCounter.increase(word);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureArrayValues() {
        arrayAsValueWordsCounter.increase(word);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureClassValues() {
        classValuesMap.increase(word);
    }
}
