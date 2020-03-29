package javasnack.snacks.jmh;

import java.math.BigInteger;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/* copied from:
 * https://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
 * 
 * jmh args: JmhSamples06FixtureLevel -wi 0 -wf 0 -r 2s -i 2 -f 1 
 */
@State(Scope.Thread)
public class JmhSamples06FixtureLevel {
    BigInteger counter1;
    BigInteger counter2;

    /*
     * Fixture methods have different levels to control when they should be run.
     * There are at least three Levels available to the user. These are, from
     * top to bottom:
     *
     * Level.Trial: before or after the entire benchmark run (the sequence of iterations)
     * Level.Iteration: before or after the benchmark iteration (the sequence of invocations)
     * Level.Invocation; before or after the benchmark method invocation (WARNING: read the Javadoc before using)
     *
     * Time spent in fixture methods does not count into the performance
     * metrics, so you can use this to do some heavy-lifting.
     */

    @Setup(Level.Trial)
    public void setupTrial() {
        counter1 = BigInteger.ZERO;
    }

    @TearDown(Level.Trial)
    public void tearDownTrial() {
        System.out.println("\n### counter1=" + counter1.toString());
    }

    @Setup(Level.Iteration)
    public void setupIteration() {
        counter2 = BigInteger.ZERO;
    }

    @TearDown(Level.Iteration)
    public void tearDownIteration() {
        System.out.println("\\n### counter2=" + counter2.toString());
    }

    @Benchmark
    public void measure() {
        counter1 = counter1.add(BigInteger.ONE);
        counter2 = counter2.add(BigInteger.ONE);
    }
}
