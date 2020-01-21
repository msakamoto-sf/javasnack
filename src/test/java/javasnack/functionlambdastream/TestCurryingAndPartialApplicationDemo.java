package javasnack.functionlambdastream;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

/* カリー化(currying) と部分適用(partial application) のそれぞれの特徴と違いのメモ
 * + 簡単なサンプルコード。
 * 
 * see:
 * 
 * ## curry化
 * 
 * https://en.wikipedia.org/wiki/Currying
 * 
 * "functional programming - What is 'Currying'? - Stack Overflow"
 * https://stackoverflow.com/questions/36314/what-is-currying
 * 
 * ## JavaでのCurry化参考
 * 
 * "Currying Functions in Java with Examples - GeeksforGeeks"
 * https://www.geeksforgeeks.org/currying-functions-in-java-with-examples/
 * 
 * "Currying in Java | Baeldung"
 * https://www.baeldung.com/java-currying
 * 
 * "functional programming - Does Java support Currying? - Stack Overflow"
 * https://stackoverflow.com/questions/6134278/does-java-support-currying
 * 
 * ## カリー化と部分適用の違い
 * 
 * "[Java] カリー化と部分適用 - Qiita"
 * https://qiita.com/yoshi389111/items/4e40c026f6af364d219a
 * 
 * "カリー化はナンの役に立つのか"
 * https://speakerdeck.com/fsubal/karihua-hananfalseyi-nili-tufalseka
 * 
 * "language agnostic - What is the difference between currying and partial application? - Stack Overflow"
 * https://stackoverflow.com/questions/218025/what-is-the-difference-between-currying-and-partial-application
 * 
 * "Functional Programming Demystified: Currying Vs Partial Application - Programming with Mosh"
 * https://programmingwithmosh.com/javascript/currying-vs-partial-application/
 * 
 * "Javascript- Currying VS Partial Application - Towards Data Science"
 * https://towardsdatascience.com/javascript-currying-vs-partial-application-4db5b2442be8
 * 
 * "Curry or Partial Application? - JavaScript Scene - Medium"
 * https://medium.com/javascript-scene/curry-or-partial-application-8150044c78b8
 */
public class TestCurryingAndPartialApplicationDemo {

    /* currying(カリー化) と partial application (部分適用) の違いを掴むには
     * 引数4つくらいの文字列連結関数を題材として使ってみる。
     * (引数が2つの関数だと、表現や実装が同じ形になってしまい違いがわかりづらくなる)
     */

    static String concat(final String s1, final String s2, final String s3, final String s4) {
        return s1 + ":" + s2 + ":" + s3 + ":" + s4;
    }

    /* 上記 concat() を currying したのが下の curryingDemo() となる。
     * N個の引数を取る処理を引数1つの処理のチェーンとして表現できるようになるのがポイント。
     * 「引数1つの処理のチェーン」として扱えると何が嬉しいのか？だが、2020-01時点では調査不足で不明。
     * currying の発祥が関数型プログラミングにつながる Haskell Curry らの数学研究由来である（らしい）
     * 点から、何かしらの理論上で currying が役立つ・必要とされるシーンがあったものと想像する。
     * 今なら map 処理でもしかしたら便利なのかなー・・・とか？
     * 
     * いずれにせよ、currying を partial application と異なるものとしている重要ポイントが
     * 「引数N個の処理を1個の処理のチェーンに分割」する点。
     * プログラミング言語としての表現は同じ技術(ファーストクラス関数, 高階関数)を使っているものの、
     * 背景にある用途や意図が異なるので要注意。
     */
    static Function<String, Function<String, Function<String, String>>> curryingDemo(final String s1) {
        return (s2) -> {
            return (s3) -> {
                return (s4) -> {
                    return concat(s1, s2, s3, s4);
                };
            };
        };
    }

    @Test
    public void testCurryingDemo() {
        final String r = curryingDemo("aaa").apply("bbb").apply("ccc").apply("ddd");
        assertThat(r).isEqualTo("aaa:bbb:ccc:ddd");
    }

    /* partial application(部分適用) は curryingと違い「引数1つ」の制約が無い。
     * N個の引数のうち、1 - (N-1) 個までの引数を固定化した新しい関数を作る。
     * どの引数を固定するかも順番など制限なく、自由に決めて良い。
     * 
     * Javaの場合、標準ライブラリの範囲では BiFunction までしか用意されていない。
     * そのため、部分適用した関数としての戻り値では、1 or 2 個の引数を取る関数までしか返せない。
     * よって部分適用で固定するための最小引数としては N - 2 個が必要となる。
     * -> 3つ以上の引数を取る FunctionInterface を独自定義すればこの制限は無くせる。
     * 以下の例がそのデモを含んでいる。
     * 
     * currying との違いは引数の数の自由度がポイントとなる。
     * なぜ currying と partial application とで別々のアイデアで分けられたのかは 2020-01 時点では調査不足で不明。
     * なんとなく雰囲気としては、「新しい関数やオペレータを作成する」用途がメインなのかな？と思われる。
     * currying よりは partial application の方が気軽に使えて、役立つシーンも多そうな印象を受けた。
     */

    static Function<String, String> concat3(final String s1, final String s2, final String s3) {
        return (s4) -> {
            return concat(s1, s2, s3, s4);
        };
    }

    static BiFunction<String, String, String> concat2(final String s1, final String s3) {
        return (s2, s4) -> {
            return concat(s1, s2, s3, s4);
        };
    }

    @FunctionalInterface
    interface TriFunction<T1, T2, T3, R> {
        R apply(T1 t1, T2 t2, T3 t3);
    }

    static TriFunction<String, String, String, String> concat1(final String s3) {
        return (s1, s2, s4) -> {
            return concat(s1, s2, s3, s4);
        };
    }

    @Test
    public void testPartialApplicationDemo() {
        assertThat(concat1("aaa").apply("bbb", "ccc", "ddd")).isEqualTo("bbb:ccc:aaa:ddd");
        assertThat(concat2("aaa", "bbb").apply("ccc", "ddd")).isEqualTo("aaa:ccc:bbb:ddd");
        assertThat(concat3("aaa", "bbb", "ccc").apply("ddd")).isEqualTo("aaa:bbb:ccc:ddd");
    }

    /* 個人的には currying より partial application の方が理解しやすかった。
     * 理由としては、partial function は以下のように、コンストラクタ・インジェクションを使った
     * 状態を持たない immutable object で関数オブジェクトを表現するアイデアと似ているため。
     * 
     * もちろん背景にある理論や考え方は異なり、そもそも partial application の方が先で、
     * その関数型プログラミングとしての実装やアイデアを OOP パラダイムに取り込んだのが
     * 以下の「コンストラクタ・インジェクションによる immutable object としての高階関数」
     * になると思われる。
     * 
     * よって厳密に運用するのであれば、以下の例と partial application は分けて扱うべき。
     * まぁ実用上はOOPと非常に親和性が高いので、なんとなく/なんちゃってで使っちゃってる。
     */

    static class Concat3 {
        private final String s1;
        private final String s2;
        private final String s3;

        public Concat3(final String s1, final String s2, final String s3) {
            this.s1 = s1;
            this.s2 = s2;
            this.s3 = s3;
        }

        public String apply(final String s4) {
            return concat(s1, s2, s3, s4);
        }
    }

    static class Concat2 {
        private final String s1;
        private final String s3;

        public Concat2(final String s1, final String s3) {
            this.s1 = s1;
            this.s3 = s3;
        }

        public String apply(final String s2, final String s4) {
            return concat(s1, s2, s3, s4);
        }
    }

    static class Concat1 {
        private final String s3;

        public Concat1(final String s3) {
            this.s3 = s3;
        }

        public String apply(final String s1, final String s2, final String s4) {
            return concat(s1, s2, s3, s4);
        }
    }

    @Test
    public void testPartialApplicationByOOPDemo() {
        assertThat(new Concat1("aaa").apply("bbb", "ccc", "ddd")).isEqualTo("bbb:ccc:aaa:ddd");
        assertThat(new Concat2("aaa", "bbb").apply("ccc", "ddd")).isEqualTo("aaa:ccc:bbb:ddd");
        assertThat(new Concat3("aaa", "bbb", "ccc").apply("ddd")).isEqualTo("aaa:bbb:ccc:ddd");
    }
}
