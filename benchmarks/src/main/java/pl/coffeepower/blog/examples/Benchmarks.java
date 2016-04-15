package pl.coffeepower.blog.examples;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Warmup(iterations = 20)
@Measurement(iterations = 50)
@Threads(value = 1)
@Fork(value = 1)
@OutputTimeUnit(value = TimeUnit.NANOSECONDS)
public class Benchmarks {

    private float randomNumber;

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(Benchmarks.class.getSimpleName())
                .shouldFailOnError(true)
                .build()
        ).run();
    }

    @Setup(value = Level.Invocation)
    public void setup() {
        randomNumber = (float) Math.random();
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureMul(Blackhole blackhole) {
        float number = randomNumber;
        number *= 0.01f;
        blackhole.consume(number);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void measureDiv(Blackhole blackhole) {
        float number = randomNumber;
        number /= 100f;
        blackhole.consume(number);
    }
}
