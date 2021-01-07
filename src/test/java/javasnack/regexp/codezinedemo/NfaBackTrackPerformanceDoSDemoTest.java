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

public class NfaBackTrackPerformanceDoSDemoTest {

    static final String COLUMN_LABELS = String.join(",",
            "regexp",
            "len",
            "NFA(backtrack-on-skip)",
            "countOfBackTracked(-skip)");

    /* O(N^2), O(2^N) で劣化するテストケースが含まれているため、
     * 10を超える値で検証するときは、時間と検証環境(CPU)に余裕をもたせてください。
     */
    static final int MAX_LENGTH = 10;

    static void benchmark(final String regexp, final boolean measureAsMatch) {
        System.out.println(COLUMN_LABELS);
        IntStream.range(1, MAX_LENGTH + 1).forEach((len) -> {
            final String matchTo = "a".repeat(len) + (measureAsMatch ? "" : "b");
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

    @Disabled("トレース済み分岐の除去を無効化すると、このパターンで無限ループ発生するためテスト除外")
    @Test
    public void testExponentialDegreeOfAmbiguityDemo2NonMatch() {
        System.out.println(">>>> benchmark demo for EDA : Exponential Degree of Ambiguity(2-not-match)");
        benchmark("(a*)*", false);
        System.out.println("<<<<");
    }

    @Disabled("トレース済み分岐の除去を無効化すると、このパターンで無限ループ発生するためテスト除外")
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
        System.out.println(">>>> benchmark demo for IDA : Infinite Degree of Amgibuity(Polynomial)(1-not-match");
        benchmark("a*a*a*", false);
        System.out.println("<<<<");
    }

    @Test
    public void testInfiniteDegreeOfAmgibuityDemo2Match() {
        System.out.println(">>>> benchmark demo for IDA : Infinite Degree of Amgibuity(Polynomial)(1-match)");
        benchmark("a*a*a*", true);
        System.out.println("<<<<");
    }

    @Test
    public void testInfiniteDegreeOfAmgibuityDemo3NonMatch() {
        System.out.println(">>>> benchmark demo for IDA : Infinite Degree of Amgibuity(Polynomial)(1-not-match");
        benchmark("a*a*a*a*", false);
        System.out.println("<<<<");
    }

    @Test
    public void testInfiniteDegreeOfAmgibuityDemo3Match() {
        System.out.println(">>>> benchmark demo for IDA : Infinite Degree of Amgibuity(Polynomial)(1-match)");
        benchmark("a*a*a*a*", true);
        System.out.println("<<<<");
    }
}
