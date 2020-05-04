package javasnack.snacks.jmh.samples;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/* copied from:
 * https://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
 * 
 * jmh args: JmhSamples05StateFixtures.measureRight -wf 0 -wi 0 -f 1 -i 2 -r 2s
 * jmh args: JmhSamples05StateFixtures.measureWrong -wf 0 -wi 0 -f 1 -i 2 -r 2s
 */
@State(Scope.Thread)
public class JmhSamples05StateFixtures {
    double someDouble;

    /*
     * Since @State objects are kept around during the lifetime of the
     * benchmark, it helps to have the methods which do state housekeeping.
     * These are usual fixture methods, you are probably familiar with them from
     * JUnit and TestNG.
     *
     * Fixture methods make sense only on @State objects, and JMH will fail to
     * compile the test otherwise.
     *
     * As with the State, fixture methods are only called by those benchmark
     * threads which are using the state. That means you can operate in the
     * thread-local context, and (not) use synchronization as if you are
     * executing in the context of benchmark thread.
     *
     * Note: fixture methods can also work with static fields, although the
     * semantics of these operations fall back out of State scope, and obey
     * usual Java rules (i.e. one static field per class).
     */

    /*
     * Ok, let's prepare our benchmark:
     */

    @Setup
    public void prepare() {
        someDouble = Math.PI;
        System.out.println("\n### setup : someDouble = " + someDouble);
    }

    /*
     * And, check the benchmark went fine afterwards:
     */

    @TearDown
    public void check() {
        System.out.println("\n### tear-down : someDouble = " + someDouble);
        assert someDouble > Math.PI : "Nothing changed?";
    }

    /*
     * This method obviously does the right thing, incrementing the field x
     * in the benchmark state. check() will never fail this way, because
     * we are always guaranteed to have at least one benchmark call.
     */

    @Benchmark
    public void measureRight() {
        someDouble++;
    }

    /*
     * This method, however, will fail the check(), because we deliberately
     * have the "typo", and increment only the local variable. This should
     * not pass the check, and JMH will fail the run.
     */

    @Benchmark
    public void measureWrong() {
        @SuppressWarnings("unused")
        double someDoubl = 0;
        someDoubl++;
    }
}
