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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javasnack.regexp.codezinedemo.Regexp.RegexpOption;

public class NfaBackTrackPerformanceDoSDemo2Test {

    static final String COLUMN_LABELS = String.join(",",
            "regexp",
            "len",
            "NFA(backtrack-on-skip)",
            "countOfBackTracked(-skip)");

    /* O(N^2), O(2^N) で劣化する正規表現をテストしているが、
     * 与える文字列は "bbb...b" と無害なため、大きめの値で検証しても大丈夫です。
     */
    static final int MAX_LENGTH = 50;

    static void benchmark(final String regexp) {
        System.out.println(COLUMN_LABELS);
        IntStream.range(1, MAX_LENGTH + 1).forEach((len) -> {
            final String matchTo = "b".repeat(len);
            final int count = 1000;
            final AtomicInteger countOfNfaBackTrackedNoSkip = new AtomicInteger(0);
            final Regexp compiled = Regexp.compileNfa(regexp,
                    RegexpOption.NFA_BACKTRACK,
                    RegexpOption.DISABLE_NFA_TRACED_BACKTRACK_SKIPPING);

            final BenchmarkResult r = BenchmarkResult.benchmark(() -> {
                final boolean m = compiled.match(matchTo);
                countOfNfaBackTrackedNoSkip.addAndGet(compiled.getCountOfNfaBackTrack());
                return m;
            }, count);
            System.out.println(regexp + "," + len + "," + String.join(",",
                    Long.toString(r.totalNanos),
                    Integer.toString(countOfNfaBackTrackedNoSkip.get())));
        });
    }

    @Test
    public void testExponentialDegreeOfAmbiguityDemo1CompletelyUnMatch() {
        System.out.println(">>>> benchmark demo for EDA : Exponential Degree of Ambiguity(completely-un-match)");
        benchmark("(a|a)*");
        System.out.println("<<<<");
    }

    @Disabled("トレース済み分岐の除去を無効化すると、このパターンで無限ループ発生するためテスト除外")
    @Test
    public void testExponentialDegreeOfAmbiguityDemo2CompletelyUnMatch() {
        System.out.println(">>>> benchmark demo for EDA : Exponential Degree of Ambiguity(completely-un-match)");
        benchmark("(a*)*");
        System.out.println("<<<<");
    }

    @Test
    public void testInfiniteDegreeOfAmgibuityDemo1CompletelyUnMatch() {
        System.out
                .println(">>>> benchmark demo for IDA : Infinite Degree of Amgibuity(Polynomial)(completely-un-match)");
        benchmark("a*a*");
        System.out.println("<<<<");
    }

    @Test
    public void testInfiniteDegreeOfAmgibuityDemo2CompletelyUnMatch() {
        System.out
                .println(">>>> benchmark demo for IDA : Infinite Degree of Amgibuity(Polynomial)(completely-un-match)");
        benchmark("a*a*a*");
        System.out.println("<<<<");
    }

    @Test
    public void testInfiniteDegreeOfAmgibuityDemo3CompletelyUnMatch() {
        System.out
                .println(">>>> benchmark demo for IDA : Infinite Degree of Amgibuity(Polynomial)(completely-un-match)");
        benchmark("a*a*a*a*");
        System.out.println("<<<<");
    }
}
