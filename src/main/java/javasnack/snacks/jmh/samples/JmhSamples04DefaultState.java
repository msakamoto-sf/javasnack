package javasnack.snacks.jmh.samples;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/* copied from:
 * https://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
 * 
 * jmh args: JmhSamples01HelloWorld (-f [forks] -i [iterations] -wi [warmup-iterations]...) 
 */
/*
 * Fortunately, in many cases you just need a single state object.
 * In that case, we can mark the benchmark instance itself to be
 * the @State. Then, we can reference its own fields as any
 * Java program does.
 */
@State(Scope.Thread)
public class JmhSamples04DefaultState {

    double someDouble = Math.PI;

    @Benchmark
    public void measure() {
        someDouble++;
    }
}
