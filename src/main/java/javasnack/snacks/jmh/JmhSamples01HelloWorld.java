package javasnack.snacks.jmh;

import org.openjdk.jmh.annotations.Benchmark;

/* copied from:
 * https://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
 * 
 * jmh args: JmhSamples01HelloWorld (-f [forks] -i [iterations] -wi [warmup-iterations]...) 
 */
public class JmhSamples01HelloWorld {
    @Benchmark
    public void wellHelloThere() {
        // this method was intentionally left blank.
    }
}
