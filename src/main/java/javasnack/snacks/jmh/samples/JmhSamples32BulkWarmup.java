package javasnack.snacks.jmh.samples;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/* copied from:
 * https://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
 * 
 * jmh args: JmhSamples32BulkWarmup -wm BULK -w 3s -wi 2 -f 1 -t 2 -i 3 -r 3s
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JmhSamples32BulkWarmup {
    /*
     * This is an addendum to JMHSample_12_Forking test.
     *
     * Sometimes you want an opposite configuration: instead of separating the profiles
     * for different benchmarks, you want to mix them together to test the worst-case
     * scenario.
     *
     * JMH has a bulk warmup feature for that: it does the warmups for all the tests
     * first, and then measures them. JMH still forks the JVM for each test, but once the
     * new JVM has started, all the warmups are being run there, before running the
     * measurement. This helps to dodge the type profile skews, as each test is still
     * executed in a different JVM, and we only "mix" the warmup code we want.
     */

    /*
     * These test classes are borrowed verbatim from JMHSample_12_Forking.
     */

    public interface Counter {
        int inc();
    }

    public static class Counter1 implements Counter {
        private int someX;

        @Override
        public int inc() {
            return someX++;
        }
    }

    public static class Counter2 implements Counter {
        private int someX;

        @Override
        public int inc() {
            return someX++;
        }
    }

    Counter c1 = new Counter1();
    Counter c2 = new Counter2();

    /*
     * And this is our test payload. Notice we have to break the inlining of the payload,
     * so that in could not be inlined in either measure_c1() or measure_c2() below, and
     * specialized for that only call.
     */

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int measure(Counter c) {
        int s = 0;
        for (int i = 0; i < 10; i++) {
            s += c.inc();
        }
        return s;
    }

    @Benchmark
    public int measure_c1() {
        return measure(c1);
    }

    @Benchmark
    public int measure_c2() {
        return measure(c2);
    }

    /* Note how JMH runs the warmups first, and only then a given test. Note how JMH re-warmups
     * the JVM for each test. The scores for C1 and C2 cases are equally bad, compare them to
     * the scores from JMHSample_12_Forking. 
     */
}
