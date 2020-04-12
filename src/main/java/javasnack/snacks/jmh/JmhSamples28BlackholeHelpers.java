package javasnack.snacks.jmh;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/* copied from:
 * https://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
 * 
 * jmh args: JmhSamples28BlackholeHelpers
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Thread)
public class JmhSamples28BlackholeHelpers {
    /**
     * Sometimes you need the black hole not in @Benchmark method, but in
     * helper methods, because you want to pass it through to the concrete
     * implementation which is instantiated in helper methods. In this case,
     * you can request the black hole straight in the helper method signature.
     * This applies to both @Setup and @TearDown methods, and also to other
     * JMH infrastructure objects, like Control.
     *
     * Below is the variant of {@link org.openjdk.jmh.samples.JMHSample_08_DeadCode}
     * test, but wrapped in the anonymous classes.
     */

    public interface Worker {
        void work();
    }

    private Worker workerBaseline;
    private Worker workerRight;
    private Worker workerWrong;

    @Setup
    public void setup(final Blackhole bh) {
        workerBaseline = new Worker() {
            @SuppressWarnings("unused")
            double someX;

            @Override
            public void work() {
                // do nothing
            }
        };

        workerWrong = new Worker() {
            double someX;

            @Override
            public void work() {
                Math.log(someX);
            }
        };

        workerRight = new Worker() {
            double someX;

            @Override
            public void work() {
                bh.consume(Math.log(someX));
            }
        };

    }

    @Benchmark
    public void baseline() {
        workerBaseline.work();
    }

    @Benchmark
    public void measureWrong() {
        workerWrong.work();
    }

    @Benchmark
    public void measureRight() {
        workerRight.work();
    }

    /* You will see measureWrong() running on-par with baseline().
     * Both measureRight() are measuring twice the baseline, so the logs are intact. 
     */
}
