package javasnack.snacks.jmh.samples;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/* copied from:
 * https://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
 * 
 * jmh args: JmhSamples13RunToRun -wi 0 -wf 0 -i 3 -r 3s -t 1 
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JmhSamples13RunToRun {
    /*
     * Forking also allows to estimate run-to-run variance.
     *
     * JVMs are complex systems, and the non-determinism is inherent for them.
     * This requires us to always account the run-to-run variance as the one
     * of the effects in our experiments.
     *
     * Luckily, forking aggregates the results across several JVM launches.
     */

    /*
     * In order to introduce readily measurable run-to-run variance, we build
     * the workload which performance differs from run to run. Note that many workloads
     * will have the similar behavior, but we do that artificially to make a point.
     */

    @State(Scope.Thread)
    public static class SleepyState {
        public long sleepTime;

        @Setup
        public void setup() {
            sleepTime = (long) (Math.random() * 1000);
        }
    }

    /*
     * Now, we will run this different number of times.
     */

    @Benchmark
    @Fork(1)
    public void baseline(SleepyState s) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(s.sleepTime);
    }

    @Benchmark
    @Fork(5)
    public void fork_1(SleepyState s) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(s.sleepTime);
    }

    @Benchmark
    @Fork(20)
    public void fork_2(SleepyState s) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(s.sleepTime);
    }

    /* Note the baseline is random within [0..1000] msec; and both forked runs
     * are estimating the average 500 msec with some confidence.
     */
}
