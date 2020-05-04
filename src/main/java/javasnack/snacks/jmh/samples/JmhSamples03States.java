package javasnack.snacks.jmh.samples;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/* copied from:
 * https://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
 * 
 * jmh args: JmhSamples03States -f 1 -i 2 -r 3s -t 4 -w 2s
 */
public class JmhSamples03States {
    /*
     * Most of the time, you need to maintain some state while the benchmark is
     * running. Since JMH is heavily used to build concurrent benchmarks, we
     * opted for an explicit notion of state-bearing objects.
     *
     * Below are two state objects. Their class names are not essential, it
     * matters they are marked with @State. These objects will be instantiated
     * on demand, and reused during the entire benchmark trial.
     *
     * The important property is that state is always instantiated by one of
     * those benchmark threads which will then have the access to that state.
     * That means you can initialize the fields as if you do that in worker
     * threads (ThreadLocals are yours, etc).
     */

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        volatile double someDouble = Math.PI;
    }

    @State(Scope.Thread)
    public static class ThreadState {
        volatile double someDouble = Math.PI;
    }

    /*
     * Benchmark methods can reference the states, and JMH will inject the
     * appropriate states while calling these methods. You can have no states at
     * all, or have only one state, or have multiple states referenced. This
     * makes building multi-threaded benchmark a breeze.
     *
     * For this exercise, we have two methods.
     */

    @Benchmark
    public void measureUnshared(ThreadState state) {
        // All benchmark threads will call in this method.
        //
        // However, since ThreadState is the Scope.Thread, each thread
        // will have it's own copy of the state, and this benchmark
        // will measure unshared case.
        state.someDouble++;
    }

    @Benchmark
    public void measureShared(BenchmarkState state) {
        // All benchmark threads will call in this method.
        //
        // Since BenchmarkState is the Scope.Benchmark, all threads
        // will share the state instance, and we will end up measuring
        // shared case.
        state.someDouble++;
    }
}
