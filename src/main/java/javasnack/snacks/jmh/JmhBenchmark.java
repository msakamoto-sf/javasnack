package javasnack.snacks.jmh;

import javasnack.RunnableSnack;

/* reference:
 * - OpenJDK: jmh
 *  - https://openjdk.java.net/projects/code-tools/jmh/
 * - JMHでJavaのマイクロベンチマークを取得する - 覚えたら書く
 *  - https://blog.y-yuki.net/entry/2016/11/05/000000
 * - JMH - Java Microbenchmark Harness
 *  - http://tutorials.jenkov.com/java-performance/jmh.html
 * 
 * tutorials:
 * - JMH(Java Microbenchmark Harness)のサンプルを動かしながら勉強 - Mitsuyuki.Shiiba
 *   - https://bufferings.hatenablog.com/entry/2018/10/14/232631
 * - 後編：JMH(Java Microbenchmark Harness)のサンプルを動かしながら勉強 - Mitsuyuki.Shiiba
 *   - https://bufferings.hatenablog.com/entry/2018/10/30/014901
 * - Java JMH Benchmark Tutorial – Mkyong.com
 *   - https://mkyong.com/java/java-jmh-benchmark-tutorial/
 * - Microbenchmarking with Java | Baeldung
 *   - https://www.baeldung.com/java-microbenchmark-harness
 */
public class JmhBenchmark implements RunnableSnack {
    @Override
    public void run(String... args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}
