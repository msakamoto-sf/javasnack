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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import com.google.re2j.Pattern;

import javasnack.regexp.codezinedemo.Regexp.RegexpOption;

public class NfaBackTrackPerformanceDemoTest {

    private static class CompiledRegexps {
        final Regexp nfa2dfaNoCache;
        final Regexp nfa2dfaCached;
        final Regexp nfa;
        final Regexp nfaBackTracked;
        final Regexp nfaBackTrackedNoSkip;
        final Pattern jrePattern;

        CompiledRegexps(final String regexp) {
            this.nfa2dfaNoCache = Regexp.compileNfa2Dfa(regexp);
            this.nfa2dfaCached = Regexp.compileNfa2Dfa(regexp, RegexpOption.ENABLE_NFA2DFA_TRANSITION_CACHE);
            this.nfa = Regexp.compileNfa(regexp);
            this.nfaBackTracked = Regexp.compileNfa(regexp, RegexpOption.NFA_BACKTRACK);
            this.nfaBackTrackedNoSkip = Regexp.compileNfa(regexp,
                    RegexpOption.NFA_BACKTRACK,
                    RegexpOption.DISABLE_NFA_TRACED_BACKTRACK_SKIPPING);
            this.jrePattern = Pattern.compile(regexp);
        }
    }

    static String benchmarkForEachWays(final CompiledRegexps compiled, final String matchTo) {
        final int count = 1000;

        final BenchmarkResult r1 = BenchmarkResult.benchmark(() -> {
            return compiled.nfa2dfaNoCache.match(matchTo);
        }, count);

        final BenchmarkResult r2 = BenchmarkResult.benchmark(() -> {
            return compiled.nfa2dfaCached.match(matchTo);
        }, count);

        final BenchmarkResult r3 = BenchmarkResult.benchmark(() -> {
            return compiled.nfa.match(matchTo);
        }, count);

        final AtomicInteger countOfNfaBackTracked = new AtomicInteger(0);
        final BenchmarkResult r4 = BenchmarkResult.benchmark(() -> {
            final boolean m = compiled.nfaBackTracked.match(matchTo);
            countOfNfaBackTracked.addAndGet(compiled.nfaBackTracked.getCountOfNfaBackTrack());
            return m;
        }, count);

        final AtomicInteger countOfNfaBackTrackedNoSkip = new AtomicInteger(0);
        final BenchmarkResult r5 = BenchmarkResult.benchmark(() -> {
            final boolean m = compiled.nfaBackTrackedNoSkip.match(matchTo);
            countOfNfaBackTrackedNoSkip.addAndGet(compiled.nfaBackTrackedNoSkip.getCountOfNfaBackTrack());
            return m;
        }, count);

        final BenchmarkResult r6 = BenchmarkResult.benchmark(() -> {
            return compiled.jrePattern.matcher(matchTo).matches();
        }, count);

        return String.join(",",
                Long.toString(r1.totalNanos),
                Long.toString(r2.totalNanos),
                Long.toString(r3.totalNanos),
                Long.toString(r4.totalNanos),
                Integer.toString(countOfNfaBackTracked.get()),
                Long.toString(r5.totalNanos),
                Integer.toString(countOfNfaBackTrackedNoSkip.get()),
                Long.toString(r6.totalNanos));
    }

    static final String COLUMN_LABELS = String.join(",",
            "regexp",
            "len",
            "NFA2DFA(cache-off)",
            "NFA2DFA(cache-on)",
            "NFA(backtrack-off)",
            "NFA(backtrack-on+skip)",
            "countOfBackTracked(+skip)",
            "NFA(backtrack-on-skip)",
            "countOfBackTracked(-skip)",
            "java.util.regex");

    /* O(N^2), O(2^N) で劣化するテストケースが含まれているため、
     * 10を超える値で検証するときは、時間と検証環境(CPU)に余裕をもたせてください。
     */
    static final int MAX_LENGTH = 10;

    static void benchmark(final String regexp, final boolean measureAsMatch) {
        final CompiledRegexps compiled = new CompiledRegexps("(a|a)*");
        System.out.println(COLUMN_LABELS);
        IntStream.range(1, MAX_LENGTH + 1).forEach((len) -> {
            final String matchTo = "a".repeat(len) + (measureAsMatch ? "" : "b");
            System.out.println(regexp + "," + len + "," + benchmarkForEachWays(compiled, matchTo));
        });
    }

    @Test
    public void testExponentialDegreeOfAmbiguityDemo1NonMatch() {
        System.out.println(">>>> benchmark demo for EDA : Exponential Degree of Ambiguity(1-not-match)");
        benchmark("(a|a)*", false);
        System.out.println("<<<<");
    }

    @Test
    public void testExponentialDegreeOfAmbiguityDemo1Match() {
        System.out.println(">>>> benchmark demo for EDA : Exponential Degree of Ambiguity(1-match)");
        benchmark("(a|a)*", true);
        System.out.println("<<<<");
    }

    @Test
    public void testExponentialDegreeOfAmbiguityDemo2NonMatch() {
        System.out.println(">>>> benchmark demo for EDA : Exponential Degree of Ambiguity(2-not-match)");
        benchmark("(a*)*", false);
        System.out.println("<<<<");
    }

    @Test
    public void testExponentialDegreeOfAmbiguityDemo2Match() {
        System.out.println(">>>> benchmark demo for EDA : Exponential Degree of Ambiguity(2-match)");
        benchmark("(a*)*", true);
        System.out.println("<<<<");
    }

    @Test
    public void testInfiniteDegreeOfAmgibuityDemo1NonMatch() {
        System.out.println(">>>> benchmark demo for IDA : Infinite Degree of Amgibuity(Polynomial)(1-not-match");
        benchmark("a*a*", false);
        System.out.println("<<<<");
    }

    @Test
    public void testInfiniteDegreeOfAmgibuityDemo1Match() {
        System.out.println(">>>> benchmark demo for IDA : Infinite Degree of Amgibuity(Polynomial)(1-match)");
        benchmark("a*a*", true);
        System.out.println("<<<<");
    }

    @Test
    public void testInfiniteDegreeOfAmgibuityDemo2NonMatch() {
        System.out.println(">>>> benchmark demo for IDA : Infinite Degree of Amgibuity(Polynomial)(2-not-match");
        benchmark("a*a*a*", false);
        System.out.println("<<<<");
    }

    @Test
    public void testInfiniteDegreeOfAmgibuityDemo2Match() {
        System.out.println(">>>> benchmark demo for IDA : Infinite Degree of Amgibuity(Polynomial)(2-match)");
        benchmark("a*a*a*", true);
        System.out.println("<<<<");
    }

    @Test
    public void testInfiniteDegreeOfAmgibuityDemo3NonMatch() {
        System.out.println(">>>> benchmark demo for IDA : Infinite Degree of Amgibuity(Polynomial)(3-not-match");
        benchmark("a*a*a*a*", false);
        System.out.println("<<<<");
    }

    @Test
    public void testInfiniteDegreeOfAmgibuityDemo3Match() {
        System.out.println(">>>> benchmark demo for IDA : Infinite Degree of Amgibuity(Polynomial)(3-match)");
        benchmark("a*a*a*a*", true);
        System.out.println("<<<<");
    }
}
