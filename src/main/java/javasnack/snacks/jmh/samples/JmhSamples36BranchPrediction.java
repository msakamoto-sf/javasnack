package javasnack.snacks.jmh.samples;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/* copied from:
 * https://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
 * 
 * jmh args: JmhSamples36BranchPrediction -prof stack
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
@State(Scope.Benchmark)
public class JmhSamples36BranchPrediction {
    /*
     * This sample serves as a warning against regular data sets.
     *
     * It is very tempting to present a regular data set to benchmark, either due to
     * naive generation strategy, or just from feeling better about regular data sets.
     * Unfortunately, it frequently backfires: the regular datasets are known to be
     * optimized well by software and hardware. This example exploits one of these
     * optimizations: branch prediction.
     *
     * Imagine our benchmark selects the branch based on the array contents, as
     * we are streaming through it:
     */

    private static final int COUNT = 1024 * 1024;

    private byte[] sorted;
    private byte[] unsorted;

    @Setup
    public void setup() {
        sorted = new byte[COUNT];
        unsorted = new byte[COUNT];
        Random random = new Random(1234);
        random.nextBytes(sorted);
        random.nextBytes(unsorted);
        Arrays.sort(sorted);
    }

    @Benchmark
    @OperationsPerInvocation(COUNT)
    public void sorted(Blackhole bh1, Blackhole bh2) {
        for (byte v : sorted) {
            if (v > 0) {
                bh1.consume(v);
            } else {
                bh2.consume(v);
            }
        }
    }

    @Benchmark
    @OperationsPerInvocation(COUNT)
    public void unsorted(Blackhole bh1, Blackhole bh2) {
        for (byte v : unsorted) {
            if (v > 0) {
                bh1.consume(v);
            } else {
                bh2.consume(v);
            }
        }
    }

    /* 補足: オリジナルのソースコードコメントには "-prof perfnorm" でプロファイリングした結果を示し、
     * sorted()よりunsoreted()の方がbranch-miss の頻度が高いことを示している。
     * →「綺麗にソートされて整列したデータ」は実行観点では「最適化されてしまっている」データであり、
     * 現実のランダムなデータでのベンチマークを取ったことにはならない、ということが言える。
     */
}
