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
        final Pattern jrePattern;

        CompiledRegexps(final String regexp) {
            this.nfa2dfaNoCache = Regexp.compileNfa2Dfa(regexp);
            this.nfa2dfaCached = Regexp.compileNfa2Dfa(regexp, RegexpOption.ENABLE_NFA2DFA_TRANSITION_CACHE);
            this.nfa = Regexp.compileNfa(regexp);
            this.nfaBackTracked = Regexp.compileNfa(regexp, RegexpOption.NFA_BACKTRACK);
            this.jrePattern = Pattern.compile(regexp);
        }
    }

    String benchmarkForEachWays(final CompiledRegexps compiled, final String matchTo) {
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

        final BenchmarkResult r4 = BenchmarkResult.benchmark(() -> {
            return compiled.nfaBackTracked.match(matchTo);
        }, count);

        final BenchmarkResult r5 = BenchmarkResult.benchmark(() -> {
            return compiled.jrePattern.matcher(matchTo).matches();
        }, count);

        return r1.totalNanos + "," + r2.totalNanos + "," + r3.totalNanos + "," + r4.totalNanos + "," + r5.totalNanos;
    }

    static final String COLUMN_LABELS = "len,NFA2DFA(cache-off),NFA2DFA(cache-on),NFA(backtrack-off),NFA(backtrack-on),java.util.regex";

    // 実際に試すときは50以上の大きめの値で実験してください。
    static final int MAX_LENGTH = 10;

    @Test
    public void testExponentialDegreeOfAmbiguityDemo1NonMatch() {
        System.out.println(">>>> benchmark demo for EDA : Exponential Degree of Ambiguity(1-not-match)");
        final CompiledRegexps compiled = new CompiledRegexps("(a|a)*");
        System.out.println(COLUMN_LABELS);
        IntStream.range(1, MAX_LENGTH + 1).forEach((len) -> {
            final String matchTo = "a".repeat(len) + "b";
            System.out.println(len + "," + benchmarkForEachWays(compiled, matchTo));
        });
        System.out.println("<<<<");
    }

    @Test
    public void testExponentialDegreeOfAmbiguityDemo1Match() {
        System.out.println(">>>> benchmark demo for EDA : Exponential Degree of Ambiguity(1-match)");
        final CompiledRegexps compiled = new CompiledRegexps("(a|a)*");
        System.out.println(COLUMN_LABELS);
        IntStream.range(1, MAX_LENGTH + 1).forEach((len) -> {
            final String matchTo = "a".repeat(len);
            System.out.println(len + "," + benchmarkForEachWays(compiled, matchTo));
        });
        System.out.println("<<<<");
    }

    @Test
    public void testExponentialDegreeOfAmbiguityDemo2NonMatch() {
        System.out.println(">>>> benchmark demo for EDA : Exponential Degree of Ambiguity(2-not-match)");
        final CompiledRegexps compiled = new CompiledRegexps("(a*)*");
        System.out.println(COLUMN_LABELS);
        IntStream.range(1, MAX_LENGTH + 1).forEach((len) -> {
            final String matchTo = "a".repeat(len) + "b";
            System.out.println(len + "," + benchmarkForEachWays(compiled, matchTo));
        });
        System.out.println("<<<<");
    }

    @Test
    public void testExponentialDegreeOfAmbiguityDemo2Match() {
        System.out.println(">>>> benchmark demo for EDA : Exponential Degree of Ambiguity(2-match)");
        final CompiledRegexps compiled = new CompiledRegexps("(a*)*");
        System.out.println(COLUMN_LABELS);
        IntStream.range(1, MAX_LENGTH + 1).forEach((len) -> {
            final String matchTo = "a".repeat(len);
            System.out.println(len + "," + benchmarkForEachWays(compiled, matchTo));
        });
        System.out.println("<<<<");
    }

    @Test
    public void testInfiniteDegreeOfAmgibuityDemoNonMatch() {
        System.out.println(">>>> benchmark demo for IDA : Infinite Degree of Amgibuity(Polynomial)(not-match");
        final CompiledRegexps compiled = new CompiledRegexps("a*a*");
        System.out.println(COLUMN_LABELS);
        IntStream.range(1, MAX_LENGTH + 1).forEach((len) -> {
            final String matchTo = "a".repeat(len) + "b";
            System.out.println(len + "," + benchmarkForEachWays(compiled, matchTo));
        });
        System.out.println("<<<<");
    }

    @Test
    public void testInfiniteDegreeOfAmgibuityDemoMatch() {
        System.out.println(">>>> benchmark demo for IDA : Infinite Degree of Amgibuity(Polynomial)(match)");
        final CompiledRegexps compiled = new CompiledRegexps("a*a*");
        System.out.println(COLUMN_LABELS);
        IntStream.range(1, MAX_LENGTH + 1).forEach((len) -> {
            final String matchTo = "a".repeat(len);
            System.out.println(len + "," + benchmarkForEachWays(compiled, matchTo));
        });
        System.out.println("<<<<");
    }
}
