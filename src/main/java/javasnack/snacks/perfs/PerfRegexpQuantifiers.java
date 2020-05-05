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

/* Javaの正規表現エンジンにおける量指定子の性能を比較するデモ。
 * 非常に単純なデモなので、greedy (無印) と possessive (+) の差はそれほど大きくない。
 * 
 * reluctant(or lazy) (?) については面白い現象が見られ、
 * 平均値計算用のループ回数が10回程度だと greedy/possessive の10倍程度の時間がかかるが、
 * 100以上のオーダーにすると内部で最適化がされるためかほとんど差が無くなる。
 * (ただ、これはあくまでもこのデモならではの事象なので、プロダクション環境ではさらにランダムな検証が必要)
 * 全体的にはmicro-seocondsのオーダーで収まっている。
 * 
 * (ここまでのコメントは adopt-openjdk 11 hotspotvm 上での実行結果に基づく)
 * 
 * reference:
 * ref[1]: Flagrant Badassery » Performance of Greedy vs. Lazy Regex Quantifiers
 *         http://blog.stevenlevithan.com/archives/greedy-lazy-performance
 * ref[2]: java - Can I improve performance of this regular expression further - Stack Overflow
 *         https://stackoverflow.com/questions/33869557/can-i-improve-performance-of-this-regular-expression-further/33869801
 * ref[3]: Runaway Regular Expressions: Catastrophic Backtracking
 *         https://www.regular-expressions.info/catastrophic.html
 * ref[4]: Optimizing regular expressions in Java | JavaWorld
 *         https://www.javaworld.com/article/2077757/optimizing-regular-expressions-in-java.html
 */
public class PerfRegexpQuantifiers implements RunnableSnack {

    @Override
    public void run(final String... args) throws IOException {
        final int avgnum = 1000;

        benchmark(Pattern.compile("ab*c"), 20, (num) -> {
            return "a" + "b".repeat(num * 10) + "c";
        }, avgnum);
        benchmark(Pattern.compile("ab*?c"), 20, (num) -> {
            return "a" + "b".repeat(num * 10) + "c";
        }, avgnum);
        benchmark(Pattern.compile("ab*+c"), 20, (num) -> {
            return "a" + "b".repeat(num * 10) + "c";
        }, avgnum);

        benchmark(Pattern.compile("ab+c"), 20, (num) -> {
            return "a" + "b".repeat(num * 10) + "c";
        }, avgnum);
        benchmark(Pattern.compile("ab+?c"), 20, (num) -> {
            return "a" + "b".repeat(num * 10) + "c";
        }, avgnum);
        benchmark(Pattern.compile("ab++c"), 20, (num) -> {
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
