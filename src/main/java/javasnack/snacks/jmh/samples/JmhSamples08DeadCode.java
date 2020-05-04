package javasnack.snacks.jmh.samples;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/* copied from:
 * https://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
 * 
 * jmh args: JmhSamples08DeadCode -wi 0 -wf 0 -f 1 -i 3 -r 3s -t 2 
 */

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JmhSamples08DeadCode {
    /*
     * The downfall of many benchmarks is Dead-Code Elimination (DCE): compilers
     * are smart enough to deduce some computations are redundant and eliminate
     * them completely. If the eliminated part was our benchmarked code, we are
     * in trouble.
     *
     * Fortunately, JMH provides the essential infrastructure to fight this
     * where appropriate: returning the result of the computation will ask JMH
     * to deal with the result to limit dead-code elimination (returned results
     * are implicitly consumed by Blackholes, see JMHSample_09_Blackholes).
     */

    private double someDouble = Math.PI;

    @Benchmark
    public void baseline() {
        // do nothing, this is a baseline
    }

    @Benchmark
    public void measureWrong() {
        // This is wrong: result is not used and the entire computation is optimized away.
        Math.log(someDouble);
    }

    @Benchmark
    public double measureRight() {
        // This is correct: the result is being used.
        return Math.log(someDouble);
    }
}
