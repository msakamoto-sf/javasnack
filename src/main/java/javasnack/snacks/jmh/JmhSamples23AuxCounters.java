package javasnack.snacks.jmh;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.AuxCounters;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/* copied from:
 * https://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
 * 
 * jmh args: JmhSamples23AuxCounters
 */
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class JmhSamples23AuxCounters {
    /*
     * In some weird cases you need to get the separate throughput/time
     * metrics for the benchmarked code depending on the outcome of the
     * current code. Trying to accommodate the cases like this, JMH optionally
     * provides the special annotation which treats @State objects
     * as the object bearing user counters. See @AuxCounters javadoc for
     * the limitations.
     */

    @State(Scope.Thread)
    @AuxCounters(AuxCounters.Type.OPERATIONS)
    public static class OpCounters {
        // These fields would be counted as metrics
        public int case1;
        public int case2;

        // This accessor will also produce a metric
        public int total() {
            return case1 + case2;
        }
    }

    @State(Scope.Thread)
    @AuxCounters(AuxCounters.Type.EVENTS)
    public static class EventCounters {
        // This field would be counted as metric
        public int wows;
    }

    /*
     * This code measures the "throughput" in two parts of the branch.
     * The @AuxCounters state above holds the counters which we increment
     * ourselves, and then let JMH to use their values in the performance
     * calculations.
     */

    @Benchmark
    public void splitBranch(OpCounters counters) {
        if (Math.random() < 0.1) {
            counters.case1++;
        } else {
            counters.case2++;
        }
    }

    @Benchmark
    public void runSETI(EventCounters counters) {
        float random = (float) Math.random();
        float wowSignal = (float) Math.PI / 4;
        if (random == wowSignal) {
            // WOW, that's unusual.
            counters.wows++;
        }
    }
}
