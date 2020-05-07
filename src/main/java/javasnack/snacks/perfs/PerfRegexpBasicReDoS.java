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

/* Javaの正規表現エンジンにおける、バックトラックによる指数関数的なマッチ時間の増大デモ。
 * ReDoS (regular expression dos) の検証を兼ねている。
 * 
 * 後述の通り java9 以降で regexp エンジンで対策が行われたようで、
 * java11で典型例を実行してみた限りでは指数関数的な増大は確認できなかった。
 * 
 * reference:
 * ref[1]: 正規表現とセキュリティ / Regular Expressions and Their Security-Related Aspects - Speaker Deck
 *         https://speakerdeck.com/lmt_swallow/regular-expressions-and-their-security-related-aspects
 * ref[2]: 遅いッ！遅すぎるッ！Java の正規表現のお話。 - Cybozu Inside Out | サイボウズエンジニアのブログ
 *         https://blog.cybozu.io/entry/8757
 * ref[3]: 正規表現のパフォーマンスの話をされても全くピンと来なかった僕は、backtrackに出会いました。 - Qiita
 *         https://qiita.com/mochizukikotaro/items/d36e61e56220da5f95d1
 * ref[4]: パフォーマンスを意識して正規表現を書く - Shin x Blog
 *         https://blog.shin1x1.com/entry/regex-performance
 * ref[5]: Regexes: the Bad, the Better, and the Best
 *         https://www.loggly.com/blog/regexes-the-bad-better-best/
 * ref[6]: Regular expression Denial of Service - ReDoS Software Attack | OWASP
 *         https://owasp.org/www-community/attacks/Regular_expression_Denial_of_Service_-_ReDoS
 * ref[7]: 正規表現の落とし穴（ReDoS - Regular Expressions DoS） - Qiita
 *         https://qiita.com/prograti/items/9b54cf82a08302a5d2c7
 * ref[8]: Online regex tester and debugger: PHP, PCRE, Python, Golang and JavaScript
 *         https://regex101.com/
 */
public class PerfRegexpBasicReDoS implements RunnableSnack {

    @Override
    public void run(final String... args) throws IOException {
        final int avgnum10 = 10;

        // 文字列終端で数回のバックトラックが発生するものの、線形増加する正規表現の例
        benchmark(Pattern.compile(".*ab.*cd"), 10, (num) -> {
            return "xxabyycd".repeat(num * 1000);
        }, avgnum10);

        /* バックトラックにより O(2^n) の指数関数的にマッチ時間が増大する例
         * ・・・のハズだが、java11で実行してみると指数関数的な増加傾向は確認できない。
         * micro-seconds単位では緩やかな線形増加の傾向が見られる。
         * 以下の記事によると java9 でredos対策で regexp が改良されたらしく、その影響と思われる。
         * https://stackoverflow.com/questions/53048859/is-java-redos-vulnerable
         */
        benchmark(Pattern.compile("(a+)+"), 100, (num) -> {
            // repeat数を100倍にしてもすぐ終わってしまう。(java11)
            return "a".repeat(num * 100) + "b";
        }, avgnum10);

        // これも瞬時に終わる。micro-seconds単位では線形増加の傾向が見られる。
        benchmark(Pattern.compile("(a*)*"), 100, (num) -> {
            // repeat数を100倍にしてもすぐ終わってしまう。(java11)
            return "a".repeat(num * 100) + "b";
        }, avgnum10);

        // これも瞬時に終わる。micro-seconds単位では線形増加の傾向が見られる。
        benchmark(Pattern.compile("([a-zA-Z]+)*"), 100, (num) -> {
            // repeat数を100倍にしてもすぐ終わってしまう。(java11)
            return "a".repeat(num * 100) + "b";
        }, avgnum10);

        /* これも指数関数的に増加する例のはずだが、java11では線形増加傾向になるパターン。
         * 代わりに repeat 数を20以上になってまもなく StackOverflowError が発生する。
         */
        benchmark(Pattern.compile("(a|aa)+"), 100, (num) -> {
            // repeat数を100倍にしてもすぐ終わってしまう。(java11)
            return "a".repeat(num * 100) + "b";
        }, avgnum10);
    }

    void benchmark(final Pattern pattern, final int numOfRepeat, final Function<Integer, String> gen, final int avgnum)
            throws IOException {
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
