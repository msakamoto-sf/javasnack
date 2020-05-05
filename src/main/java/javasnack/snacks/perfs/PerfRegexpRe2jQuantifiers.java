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

package javasnack.snacks.perfs;

import java.io.IOException;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javasnack.RunnableSnack;

/* google re2/j における量指定子の性能を比較するデモ。
 * https://github.com/google/re2j
 * 
 * re2jでは強欲な数量子(possessive quantifier)はサポートしていないため、
 * 最長一致数量子(greedy) と 最短一致数量子(reluctant or lazy) の違いを
 * 非常に単純なデモで確認してみるコードになっている。
 * 
 * 実際に動かしてみると、elapsed平均値取得のための繰り返し回数が10くらいだと
 * greedy < reluctant だが 100 回以上になると reluctant も greedy のオーダーに近づく。
 * どちらにしても、micro-seconds のオーダー。
 * (ここまでのコメントは adopt-openjdk 11 hotspotvm 上での実行結果に基づく)
 */
public class PerfRegexpRe2jQuantifiers implements RunnableSnack {

    @Override
    public void run(final String... args) throws IOException {
        final int avgnum = 1000;

        benchmark(Pattern.compile("ab*c"), 20, (num) -> {
            return "a" + "b".repeat(num * 10) + "c";
        }, avgnum);
        benchmark(Pattern.compile("ab*?c"), 20, (num) -> {
            return "a" + "b".repeat(num * 10) + "c";
        }, avgnum);

        benchmark(Pattern.compile("ab+c"), 20, (num) -> {
            return "a" + "b".repeat(num * 10) + "c";
        }, avgnum);
        benchmark(Pattern.compile("ab+?c"), 20, (num) -> {
            return "a" + "b".repeat(num * 10) + "c";
        }, avgnum);
    }

    void benchmark(final Pattern pattern, final int numOfRepeat, final Function<Integer, String> gen,
            final int avgnum) throws IOException {
        try {
            benchmark0(pattern, numOfRepeat, gen, avgnum);
        } catch (Throwable t) {
            System.err.print("caught " + t.getClass() + ":" + t.getMessage());
        }
    }

    void benchmark0(final Pattern pattern, final int numOfRepeat, final Function<Integer, String> gen,
            final int avgnum) throws IOException {
        final String patternLabel = pattern.pattern();
        for (int i = 1; i <= numOfRepeat; i++) {
            final String s = gen.apply(i);
            int matched = 0;
            long sumOfElapsed = 0;
            for (int j = 0; j < avgnum; j++) {
                final long started = System.nanoTime();
                final Matcher m = pattern.matcher(s);
                if (m.find()) {
                    matched++;
                }
                final long elapsed = (System.nanoTime() - started);
                sumOfElapsed += elapsed;
            }
            long avg = sumOfElapsed / (avgnum * 1000);
            System.out.println(
                    String.format("pattern[%s] repeat#%02d avg[%,10d us] matched=%d", patternLabel, i, avg, matched));
        }
    }
}
