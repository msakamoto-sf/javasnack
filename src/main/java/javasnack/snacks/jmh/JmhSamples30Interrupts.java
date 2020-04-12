package javasnack.snacks.jmh;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/* copied from:
 * https://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
 * 
 * jmh args: JmhSamples30Interrupts -w 3s -wi 3 -f 5 -t 2 -i 3 -r 5s -to 3s
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Group)
public class JmhSamples30Interrupts {
    /*
     * JMH can also detect when threads are stuck in the benchmarks, and try
     * to forcefully interrupt the benchmark thread. JMH tries to do that
     * when it is arguably sure it would not affect the measurement.
     */

    /*
     * In this example, we want to measure the simple performance characteristics
     * of the ArrayBlockingQueue. Unfortunately, doing that without a harness
     * support will deadlock one of the threads, because the executions of
     * take/put are not paired perfectly. Fortunately for us, both methods react
     * to interrupts well, and therefore we can rely on JMH to terminate the
     * measurement for us. JMH will notify users about the interrupt actions
     * nevertheless, so users can see if those interrupts affected the measurement.
     * JMH will start issuing interrupts after the default or user-specified timeout
     * had been reached.
     *
     * This is a variant of org.openjdk.jmh.samples.JMHSample_18_Control, but without
     * the explicit control objects. This example is suitable for the methods which
     * react to interrupts gracefully.
     */

    private BlockingQueue<Integer> queue;

    @Setup
    public void setup() {
        queue = new ArrayBlockingQueue<>(1);
    }

    @Group("Q")
    @Benchmark
    public Integer take() throws InterruptedException {
        return queue.take();
    }

    @Group("Q")
    @Benchmark
    public void put() throws InterruptedException {
        queue.put(42);
    }
}
