package javasnack.snacks.jmh.samples;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/* copied from:
 * https://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
 */
public class JmhSamples35Profilers {
    /*
     * This sample serves as the profiler overview.
     *
     * JMH has a few very handy profilers that help to understand your benchmarks. While
     * these profilers are not the substitute for full-fledged external profilers, in many
     * cases, these are handy to quickly dig into the benchmark behavior. When you are
     * doing many cycles of tuning up the benchmark code itself, it is important to have
     * a quick turnaround for the results.
     *
     * Use -lprof to list the profilers. There are quite a few profilers, and this sample
     * would expand on a handful of most useful ones. Many profilers have their own options,
     * usually accessible via -prof <profiler-name>:help.
     *
     * Since profilers are reporting on different things, it is hard to construct a single
     * benchmark sample that will show all profilers in action. Therefore, we have a couple
     * of benchmarks in this sample.
     */

    /*
     * ================================ MAPS BENCHMARK ================================
     *
     * jmh args:
     * JmhSamples35Profilers.*Maps -prof stack
     * JmhSamples35Profilers.*Maps -prof gc
     * 
     * 補足:
     * "-prof stack" では性能検証を意図してるメソッドが確実に実行されているか確認できる。
     * (上の実行例だと HashMap.get のような瞬時に実行されるメソッドなどはスルーしてしまう)
     * 
     * "-prof gc" ではGC状況のプロファイリングを確認できる。
     */

    @State(Scope.Thread)
    @Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @Fork(3)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public static class Maps {
        private Map<Integer, Integer> map;

        @Param({ "hashmap", "treemap" })
        private String type;

        private int begin;
        private int end;

        @Setup
        public void setup() {
            switch (type) {
            case "hashmap":
                map = new HashMap<>();
                break;
            case "treemap":
                map = new TreeMap<>();
                break;
            default:
                throw new IllegalStateException("Unknown type: " + type);
            }

            begin = 1;
            end = 256;
            for (int i = begin; i < end; i++) {
                map.put(i, i);
            }
        }

        @Benchmark
        public void test(Blackhole bh) {
            for (int i = begin; i < end; i++) {
                bh.consume(map.get(i));
            }
        }
    }

    /*
     * ================================ CLASSLOADER BENCHMARK ================================
     *
     * jmh args:
     * JmhSamples35Profilers.*Classy -prof cl
     * JmhSamples35Profilers.*Classy -prof comp
     * 
     * 補足:
     * "-prof cl" ではClassLoaderのload/unload状況を確認できる。
     * "-prof comp" ではコンパイラのプロファイリングを確認できる。
     */

    @State(Scope.Thread)
    @Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @Fork(3)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public static class Classy {

        /**
         * Our own crippled classloader, that can only load a simple class over and over again.
         */
        public static class XLoader extends URLClassLoader {
            private static final byte[] X_BYTECODE = new byte[] {
                    (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE, 0x00, 0x00, 0x00, 0x34, 0x00, 0x0D, 0x0A, 0x00,
                    0x03, 0x00,
                    0x0A, 0x07, 0x00, 0x0B, 0x07, 0x00, 0x0C, 0x01, 0x00, 0x06, 0x3C, 0x69, 0x6E, 0x69, 0x74, 0x3E,
                    0x01, 0x00, 0x03,
                    0x28, 0x29, 0x56, 0x01, 0x00, 0x04, 0x43, 0x6F, 0x64, 0x65, 0x01, 0x00, 0x0F, 0x4C, 0x69, 0x6E,
                    0x65, 0x4E, 0x75,
                    0x6D, 0x62, 0x65, 0x72, 0x54, 0x61, 0x62, 0x6C, 0x65, 0x01, 0x00, 0x0A, 0x53, 0x6F, 0x75, 0x72,
                    0x63, 0x65, 0x46,
                    0x69, 0x6C, 0x65, 0x01, 0x00, 0x06, 0x58, 0x2E, 0x6A, 0x61, 0x76, 0x61, 0x0C, 0x00, 0x04, 0x00,
                    0x05, 0x01, 0x00,
                    0x01, 0x58, 0x01, 0x00, 0x10, 0x6A, 0x61, 0x76, 0x61, 0x2F, 0x6C, 0x61, 0x6E, 0x67, 0x2F, 0x4F,
                    0x62, 0x6A, 0x65,
                    0x63, 0x74, 0x00, 0x20, 0x00, 0x02, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00,
                    0x00, 0x04, 0x00,
                    0x05, 0x00, 0x01, 0x00, 0x06, 0x00, 0x00, 0x00, 0x1D, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00,
                    0x05, 0x2A,
                    (byte) 0xB7, 0x00, 0x01, (byte) 0xB1, 0x00, 0x00, 0x00, 0x01, 0x00, 0x07, 0x00, 0x00, 0x00, 0x06,
                    0x00, 0x01, 0x00,
                    0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x08, 0x00, 0x00, 0x00, 0x02, 0x00, 0x09,
            };

            public XLoader() {
                super(new URL[0], ClassLoader.getSystemClassLoader());
            }

            @Override
            protected Class<?> findClass(final String name) throws ClassNotFoundException {
                return defineClass(name, X_BYTECODE, 0, X_BYTECODE.length);
            }
        }

        @Benchmark
        public Class<?> load() throws ClassNotFoundException {
            return Class.forName("X", true, new XLoader());
        }
    }

    /*
     * ================================ ATOMIC LONG BENCHMARK ================================
     *
     * jmh args:
     * JmhSamples35Profilers.*Atomic -prof perf (linux)
     * JmhSamples35Profilers.*Atomic -prof perfnorm (linux)
     * JmhSamples35Profilers.*Atomic -prof perfasm (linux)
     * JmhSamples35Profilers.*Atomic -prof xperfasm (windows)
     * JmhSamples35Profilers.*Atomic -prof dtraceasm (macosx)
     * 
     * 補足:
     * 各プラットフォームごとのプロファイラを使ったプロファイリング結果を確認できる。
     */

    @State(Scope.Benchmark)
    @Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @Fork(1)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public static class Atomic {
        private AtomicLong num;

        @Setup
        public void setup() {
            num = new AtomicLong();
        }

        @Benchmark
        public long test() {
            return num.incrementAndGet();
        }
    }
}
