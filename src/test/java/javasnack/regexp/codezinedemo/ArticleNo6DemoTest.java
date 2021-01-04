/*
 * Copyright 2020 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javasnack.regexp.codezinedemo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import com.google.re2j.Pattern;

import javasnack.regexp.codezinedemo.Regexp.RegexpOption;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

public class ArticleNo6DemoTest {
    @Test
    public void testDebugDump() {
        System.out.println(">>>> debug dump demo for https://codezine.jp/article/detail/3188");

        Regexp r = Regexp.compileNfa2Dfa("XY*Z", RegexpOption.DEBUG_LOG);
        System.out.println("------------------");
        assertTrue(r.match("XYYYZ"));
        System.out.println("------------------");
        assertFalse(r.match("XYZYZYZ"));

        System.out.println("<<<<");
    }

    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor(staticName = "of")
    private static class BenchmarkResult {
        final long totalNanos;
        final double avgNanos;
        final int matchedCount;

        final String fomatTotalNanos() {
            final NumberFormat fmt = NumberFormat.getNumberInstance();
            return fmt.format(totalNanos);
        }
    }

    private static BenchmarkResult benchmark(final Supplier<Boolean> regexpTask, final int count) {
        final List<Long> elapsedNanos = new ArrayList<>(count);
        int matched = 0;
        for (int i = 0; i < count; i++) {
            final long started = System.nanoTime();
            /* JITによる未使用コードの削除を回避するため、マッチ結果の戻り値を使う処理を挿入
             * -> マッチしたらカウントアップする処理を入れている。
             */
            final boolean r = regexpTask.get();
            final long elapsed = System.nanoTime() - started;
            elapsedNanos.add(elapsed);
            if (r) {
                matched++;
            }
        }

        return BenchmarkResult.of(
                elapsedNanos.stream().mapToLong(x -> x).sum(),
                elapsedNanos.stream().mapToLong(x -> x).average().getAsDouble(),
                matched);
    }

    @Test
    public void testBenchmarkDemo() {
        System.out.println(">>>> benchmark demo for https://codezine.jp/article/detail/3188?p=2");

        final String regexp = "(f|F)(o|O)(o|O)";
        final String matchTo = "FoO";
        System.out.println("NFA2DFA v.s. java.util.regex for regexp=[" + regexp + "], match to=[" + matchTo + "]");

        final int count = 1000;

        final Regexp nfa2dfa = Regexp.compileNfa2Dfa(regexp);
        final BenchmarkResult r1 = benchmark(() -> {
            return nfa2dfa.match(matchTo);
        }, count);
        assertThat(r1.matchedCount).isEqualTo(count);

        final Pattern javaregexp = Pattern.compile(regexp);
        final BenchmarkResult r2 = benchmark(() -> {
            return javaregexp.matcher(matchTo).matches();
        }, count);
        assertThat(r2.matchedCount).isEqualTo(count);

        System.out.println("[NFA2DFA total nanos= " + r1.fomatTotalNanos() + " ]");
        System.out.println("[java.util.regex total nanos= " + r2.fomatTotalNanos() + " ]");

        System.out.println("<<<<");
    }

    @Test
    public void testBenchmarkDemoCacheOnOff() {
        System.out.println(">>>> benchmark demo for https://codezine.jp/article/detail/3188?p=2");

        final String regexp = "(f|F)(o|O)(o|O)";
        final String matchTo = "FoO";
        System.out.println(
                "NFA2DFA(cache-off) v.s. NFA2DFA(cache-on) for regexp=[" + regexp + "], match to=[" + matchTo + "]");

        final int count = 1000;

        final Regexp nfa2dfaWithoutCache = Regexp.compileNfa2Dfa(regexp);
        final BenchmarkResult r1 = benchmark(() -> {
            return nfa2dfaWithoutCache.match(matchTo);
        }, count);
        assertThat(r1.matchedCount).isEqualTo(count);

        final Regexp nfa2dfaWithCache = Regexp.compileNfa2Dfa(regexp, RegexpOption.ENABLE_NFA2DFA_TRANSITION_CACHE);
        final BenchmarkResult r2 = benchmark(() -> {
            return nfa2dfaWithCache.match(matchTo);
        }, count);
        assertThat(r2.matchedCount).isEqualTo(count);

        System.out.println("[NFA2DFA(cache-off) total nanos= " + r1.fomatTotalNanos() + " ]");
        System.out.println("[NFA2DFA(cache-on ) total nanos= " + r2.fomatTotalNanos() + " ]");

        System.out.println("<<<<");
    }
}
