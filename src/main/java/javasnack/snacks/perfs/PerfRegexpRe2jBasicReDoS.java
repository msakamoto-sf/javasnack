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

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;

import javasnack.RunnableSnack;

/* google re2/j におけるバックトラック対策の検証デモ。
 * https://github.com/google/re2j
 * 
 * 全体的に線形増加の様子を確認できる。
 */
public class PerfRegexpRe2jBasicReDoS implements RunnableSnack {

    @Override
    public void run(final String... args) throws IOException {
        final int avgnum10 = 10;

        // エンジンによってはバックトラックが発生する正規表現の例
        benchmark(Pattern.compile(".*ab.*cd"), 10, (num) -> {
            return "xxabyycd".repeat(num * 1000);
        }, avgnum10);
        benchmark(Pattern.compile("(a+)+"), 100, (num) -> {
            return "a".repeat(num * 100) + "b";
        }, avgnum10);
        benchmark(Pattern.compile("(a*)*"), 100, (num) -> {
            return "a".repeat(num * 100) + "b";
        }, avgnum10);
        benchmark(Pattern.compile("([a-zA-Z]+)*"), 100, (num) -> {
            return "a".repeat(num * 100) + "b";
        }, avgnum10);
        /* java11 の java.util.regex では線形増加 + 20回以上の繰り返しで StackOverflowError が発生したが、
         * re2jでは発生せず、なだらかな線形増加傾向が見られた。
         */
        benchmark(Pattern.compile("(a|aa)+"), 100, (num) -> {
            // repeat数を100倍にしてもすぐ終わってしまう。(java11)
            return "a".repeat(num * 100) + "b";
        }, avgnum10);
    }

    void benchmark(final Pattern pattern, final int numOfRepeat,
            final Function<Integer, String> gen, final int avgnum) throws IOException {
        try {
            benchmark0(pattern, numOfRepeat, gen, avgnum);
        } catch (Throwable t) {
            System.err.print("caught " + t.getClass() + ":" + t.getMessage());
        }
    }

    void benchmark0(final Pattern pattern, final int numOfRepeat, final Function<Integer, String> gen, final int avgnum)
            throws IOException {
        System.out.println("pattern=[" + pattern.pattern() + "]");
        for (int i = 1; i <= numOfRepeat; i++) {
            final String s = gen.apply(i);

            int matched = 0;
            long sumOfElapsed = 0;
            for (int j = 0; j < avgnum; j++) {
                final long started = System.nanoTime();
                final Matcher m = pattern.matcher(s);
                // 戻り値を使わないと JIT などによって Matcher#find() 呼び出し自体が削除されてしまう可能性がある。
                if (m.find()) {
                    matched++;
                }
                final long elapsed = (System.nanoTime() - started);
                sumOfElapsed += elapsed;
            }
            long avg = sumOfElapsed / (avgnum * 1000);
            System.out.println(String.format("avg[%,10d us] repeat#%02d matched=%d", avg, i, matched));
        }
    }
}
