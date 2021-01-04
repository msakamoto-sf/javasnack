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

import org.junit.jupiter.api.Test;

import com.google.re2j.Pattern;

import javasnack.regexp.codezinedemo.Regexp.RegexpOption;

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

    @Test
    public void testBenchmarkDemo() {
        System.out.println(">>>> benchmark demo for https://codezine.jp/article/detail/3188?p=2");

        final String regexp = "(f|F)(o|O)(o|O)";
        final String matchTo = "FoO";
        System.out.println("NFA2DFA v.s. java.util.regex for regexp=[" + regexp + "], match to=[" + matchTo + "]");

        final int count = 1000;

        final Regexp nfa2dfa = Regexp.compileNfa2Dfa(regexp);
        final BenchmarkResult r1 = BenchmarkResult.benchmark(() -> {
            return nfa2dfa.match(matchTo);
        }, count);
        assertThat(r1.matchedCount).isEqualTo(count);

        final Pattern javaregexp = Pattern.compile(regexp);
        final BenchmarkResult r2 = BenchmarkResult.benchmark(() -> {
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
        final BenchmarkResult r1 = BenchmarkResult.benchmark(() -> {
            return nfa2dfaWithoutCache.match(matchTo);
        }, count);
        assertThat(r1.matchedCount).isEqualTo(count);

        final Regexp nfa2dfaWithCache = Regexp.compileNfa2Dfa(regexp, RegexpOption.ENABLE_NFA2DFA_TRANSITION_CACHE);
        final BenchmarkResult r2 = BenchmarkResult.benchmark(() -> {
            return nfa2dfaWithCache.match(matchTo);
        }, count);
        assertThat(r2.matchedCount).isEqualTo(count);

        System.out.println("[NFA2DFA(cache-off) total nanos= " + r1.fomatTotalNanos() + " ]");
        System.out.println("[NFA2DFA(cache-on ) total nanos= " + r2.fomatTotalNanos() + " ]");

        System.out.println("<<<<");
    }

    @Test
    public void testBenchmarkHeavyDemo() {
        System.out.println(">>>> benchmark demo for https://codezine.jp/article/detail/3188?p=2");

        final String regexp = "X*X*X*X*X*X*X*X*X*X*XXXXXXXXXX";
        final String matchTo = "XXXXXXXXXX";
        System.out.println("NFA2DFA v.s. java.util.regex for regexp=[" + regexp + "], match to=[" + matchTo + "]");

        final int count = 1000;

        final Regexp nfa2dfa1 = Regexp.compileNfa2Dfa(regexp);
        final BenchmarkResult r1 = BenchmarkResult.benchmark(() -> {
            return nfa2dfa1.match(matchTo);
        }, count);
        assertThat(r1.matchedCount).isEqualTo(count);

        final Regexp nfa2dfa2 = Regexp.compileNfa2Dfa(regexp, RegexpOption.ENABLE_NFA2DFA_TRANSITION_CACHE);
        final BenchmarkResult r2 = BenchmarkResult.benchmark(() -> {
            return nfa2dfa2.match(matchTo);
        }, count);
        assertThat(r2.matchedCount).isEqualTo(count);

        final Pattern javaregexp = Pattern.compile(regexp);
        final BenchmarkResult r3 = BenchmarkResult.benchmark(() -> {
            return javaregexp.matcher(matchTo).matches();
        }, count);
        assertThat(r3.matchedCount).isEqualTo(count);

        System.out.println("[NFA2DFA(cache-off) total nanos= " + r1.fomatTotalNanos() + " ]");
        System.out.println("[NFA2DFA(cache-on ) total nanos= " + r2.fomatTotalNanos() + " ]");
        System.out.println("[java.util.regex total nanos= " + r3.fomatTotalNanos() + " ]");

        System.out.println("<<<<");
    }
}
