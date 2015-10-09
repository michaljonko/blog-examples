package pl.coffeepower.blog.examples;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
public class JMHPerformanceTest {

    private static final int FORKS = 1;
    private static final int THREADS = 1;
    private static final int WARMUP_ITERATIONS = 3;
    private static final int MEASUREMENT_ITERATIONS = 3;
    private static final boolean FAIL_ON_ERROR = true;

    private ClassicValuesMap classicValuesMap;
    private AtomicValuesMap atomicValuesMap;
    private ArrayValuesMap arrayValuesMap;
    private ClassValuesMap classValuesMap;

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(JMHPerformanceTest.class.getSimpleName())
                .warmupIterations(WARMUP_ITERATIONS)
                .measurementIterations(MEASUREMENT_ITERATIONS)
                .shouldFailOnError(FAIL_ON_ERROR)
                .forks(FORKS)
                .threads(THREADS)
                .jvmArgs("-ea")
                .build()
        ).run();
    }

    @Setup
    public void setup() {
        classicValuesMap = new ClassicValuesMap();
        atomicValuesMap = new AtomicValuesMap();
        arrayValuesMap = new ArrayValuesMap();
        classValuesMap = new ClassValuesMap();
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureClassicValues() {
        classicValuesMap.increase();
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureAtomicValues() {
        atomicValuesMap.increase();
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureArrayValues() {
        arrayValuesMap.increase();
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureClassValues() {
        classValuesMap.increase();
    }
}
