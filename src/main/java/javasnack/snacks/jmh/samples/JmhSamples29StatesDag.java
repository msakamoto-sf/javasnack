package javasnack.snacks.jmh.samples;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

/* copied from:
 * https://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
 * 
 * jmh args: JmhSamples29StatesDag
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Thread)
public class JmhSamples29StatesDag {
    /*
     * WARNING:
     * THIS IS AN EXPERIMENTAL FEATURE, BE READY FOR IT BECOME REMOVED WITHOUT NOTICE!
     */

    /*
     * There are weird cases when the benchmark state is more cleanly described
     * by the set of @States, and those @States reference each other. JMH allows
     * linking @States in directed acyclic graphs (DAGs) by referencing @States
     * in helper method signatures. (Note that {@link org.openjdk.jmh.samples.JMHSample_28_BlackholeHelpers}
     * is just a special case of that.
     *
     * Following the interface for @Benchmark calls, all @Setups for
     * referenced @State-s are fired before it becomes accessible to current @State.
     * Similarly, no @TearDown methods are fired for referenced @State before
     * current @State is done with it.
     */

    /*
     * This is a model case, and it might not be a good benchmark.
     * // Replace it with the benchmark which does something useful.
     */

    public static class Counter {
        int someX;

        public int inc() {
            return someX++;
        }

        public void dispose() {
            // pretend this is something really useful
        }
    }

    /*
     * Shared state maintains the set of Counters, and worker threads should
     * poll their own instances of Counter to work with. However, it should only
     * be done once, and therefore, Local state caches it after requesting the
     * counter from Shared state.
     */

    @State(Scope.Benchmark)
    public static class Shared {
        List<Counter> all;
        Queue<Counter> available;

        @Setup
        public synchronized void setup() {
            all = new ArrayList<>();
            for (int c = 0; c < 10; c++) {
                all.add(new Counter());
            }

            available = new LinkedList<>();
            available.addAll(all);
        }

        @TearDown
        public synchronized void tearDown() {
            for (Counter c : all) {
                c.dispose();
            }
        }

        public synchronized Counter getMine() {
            return available.poll();
        }
    }

    @State(Scope.Thread)
    public static class Local {
        Counter cnt;

        @Setup
        public void setup(Shared shared) {
            cnt = shared.getMine();
        }
    }

    @Benchmark
    public int test(Local local) {
        return local.cnt.inc();
    }
}
