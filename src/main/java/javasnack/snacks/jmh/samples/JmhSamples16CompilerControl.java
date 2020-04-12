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
 * jmh args: JmhSamples16CompilerControl -wi 0 -wf 0 -f 1 -i 3 -r 3s
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JmhSamples16CompilerControl {
    /*
     * We can use HotSpot-specific functionality to tell the compiler what
     * do we want to do with particular methods. To demonstrate the effects,
     * we end up with 3 methods in this sample.
     */

    /*
     * These are our targets:
     *   - first method is prohibited from inlining
     *   - second method is forced to inline
     *   - third method is prohibited from compiling
     *
     * We might even place the annotations directly to the benchmarked
     * methods, but this expresses the intent more clearly.
     */

    public void target_blank() {
        // this method was intentionally left blank
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void target_dontInline() {
        // this method was intentionally left blank
    }

    @CompilerControl(CompilerControl.Mode.INLINE)
    public void target_inline() {
        // this method was intentionally left blank
    }

    @CompilerControl(CompilerControl.Mode.EXCLUDE)
    public void target_exclude() {
        // this method was intentionally left blank
    }

    /*
     * These method measures the calls performance.
     */

    @Benchmark
    public void baseline() {
        // this method was intentionally left blank
    }

    @Benchmark
    public void blank() {
        target_blank();
    }

    @Benchmark
    public void dontinline() {
        target_dontInline();
    }

    @Benchmark
    public void inline() {
        target_inline();
    }

    @Benchmark
    public void exclude() {
        target_exclude();
    }

    /* Note the performance of the baseline, blank, and inline methods are the same.
     * dontinline differs a bit, because we are making the proper call.
     * exclude is severely slower, becase we are not compiling it at all.     * 
     */
}
