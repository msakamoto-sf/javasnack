package javasnack.functionlambdastream.fpjbook;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class TestChapter7SamplesDemo {

    // chapter 7.1 : before optimization

    static int factorialRec(final int number) {
        if (number == 1) {
            return number;
        }
        return number * factorialRec(number - 1);
    }

    @Test
    public void testFactorialRec() {
        assertThat(factorialRec(5)).isEqualTo(120);

        // DONT DO THIS X(
        // assertThat(factorialRec(1_000_000)).isEqualTo(0);
    }

    /* 末尾呼び出し(tail call)の最適化の原理:
     * SomeType recursiveFunction(SomeType v(, int counter)) {
     *     // 何らかの終端判定 + return
     *     if (v (or counter) == 1) {
     *         return v;
     *     }
     *     some-process (v => new-v)
     *     return (some-process) recursiveFunction(new-v(, counter -1))
     * }
     * ->
     * SomeType recursiveFunction(SomeType v(, int counter)) {
     *     for (;;) {
     *         // 何らかの終端判定 + return
     *         if (v (or counter) == 1) {
     *             return v;
     *         }
     *         some-process (update v => new-v)
     *         v = new-v
     *         counter counter - 1
     *     }
     * }
     * 多少不正確なところもあるが、2020-01-11時点の理解では上記のように再帰処理の中身を
     * 終端判定 + return を含んだ無限ループに書き換えるのが、末尾呼び出し(tail call) の最適化の原理らしい。
     * これをJavaでどう実現したのか、本書のサンプルコードを最終形態として、途中過程を逆算してみる。
     * 
     * (1) まず some-process のところはラムダ式で実現できそうである。
     * 
     * (2) 次に for(;;) 中で some-process 後の値の更新部分だが、これは for(;;) 文による擬似構文で
     * 書いているからこう見えるのであって、本質的には some-procss が参照する v(, counter) が更新されれば良い。
     * -> これをラムダ式で考えれば、更新された v(, counter) を参照する新しいラムダ式を使うアプローチがありえる:
     * SomeType recursiveFunction(SomeType v(, int counter)) {
     *     for (;;) {
     *         // 何らかの終端判定 + return
     *         if (v (or counter) == 1) {
     *             return v;
     *         }
     *         lambda = {
     *             some-process (update v => new -v)
     *             return lambda {
     *                 return some-process (v, counter - 1)
     *             }
     *         }
     *         lambda = call lambda
     *     }
     * }
     * NOTE: lambdaが自分自身を更新する新しいlambdaを返す、というのが(多分)ポイント。
     * 
     * (3) (2)まで some-process をラムダに組み込んでループで回せる雰囲気が出てきた。
     * では終端判定をどうするか？ できれば、some-processを組み込んだラムダの中で終端判定もできれば、
     * ラムダがより再帰末尾呼び出しっぽく書けそう。
     * -> そこで本書のサンプルコードでは独自の Functional Interface を定義して、
     * デフォルトメソッドとして isComplete() を返すようにしたと推測される。
     * これを some-process のラムダ式で使えば、(2) の for(;;) を以下のように改良できる:
     * SomeType recursiveFunction(SomeType v(, int counter)) {
     *     for (;;) {
     *         lambda = {
     *             // ラムダ式の属性として終端フラグを含めるようにして、
     *             // 終端判定がtrueなら、終端フラグをON + 終端用の値を返すラムダを返して
     *             // for(;;)中のラムダを更新する。
     *             if (v (or counter) == 1) {
     *                 return lambda {
     *                     isComplete() : return true
     *                     return some-process : return v
     *                 }
     *             }
     *             some-process (update v => new -v)
     *             return lambda {
     *                 isComplete() : return false
     *                 return some-process (v, counter - 1)
     *             }
     *         }
     *         if (lambda.isComplete()) {
     *             // "「終端用の値を返すラムダ」を返すラムダ" を呼ぶ。
     *             return call (call lambda)
     *         }
     *         lambda = call lambda
     *     }
     * }
     * 
     * (4) これにより、lambdaの部分を切り離すことが可能となった。
     * for (;;) と終端処理については、Stream.iterate() と filter() でスマートな書き方ができる。
     * まず for(;;) + lambda の更新については、Stream.iterate(初期値, ジェネレータ) を適用できる。
     * 初期値を最初のラムダ式として、ジェネレータを「ラムダ式を返すラムダ式」と考えれば、
     * Stream.iterate(lambda0, call lambda)
     * はそのまま以下のfor文と同等とみなせる。
     * for (lambda = lambda0; true;) {
     *    lambda = call lambda
     * }
     * 
     * (5) 終端処理については、lambdaを返すstreamなので filter (lambda -> lambda.isComplete()) を適用できる。
     * これにより、遅延された無限ループを適切に終端させることが可能となる。
     * この構造だと Stream.iterate().filter() は、sream 終端処理の発火により isComplete() がtrueを返すまで
     * ラムダ式のリストを生成する : (3) のおかげで、「生成 = some-processの実行」という連鎖が成立している。
     * ここから値を取り出すには、isComplete() = true でフィルタされた単一のラムダ式が必要なので、findFirst()
     * の終端処理を呼び出せば良い:
     * Stream.iterate(lambda0, call lambda).filter(lambda -> lambda.isComplete().findFirst()
     * 
     * (6) findFirst().get() で取り出せるのは終端用ラムダなので、これの apply() を呼び出すと
     * 最終的な計算結果が取得できる。
     * 以上をまとめた擬似コード:
     * lambda = {
     *     if (v (or counter) == 1) {
     *         return lambda {
     *             isComplete() : return true
     *             return some-process : return v
     *         }
     *     }
     *     some-process (update v => new -v)
     *     return lambda {
     *         isComplete() : return false
     *         return some-process (v, counter - 1)
     *     }
     * }
     * lambda0 = call lambda (init-v, count)
     * result = Stream.iterate(lambda0, call lambda)
     *             .filter(lambda -> lambda.isComplete())
     *             .findFirst()
     *             .get()
     *             .apply() // 書籍では result() という独自のデフォルトメソッドでわかりやすくしてる
     * 
     * これをJava8のラムダ式とStreamでまとめ上げたのが、本書のサンプルコードと考えられる。
     * 
     * Tail Call Optimization (TCO) reference:
     * https://stackoverflow.com/questions/310974/what-is-tail-call-optimization
     * https://dev.to/rohit/demystifying-tail-call-optimization-5bf3
     * https://www.geeksforgeeks.org/tail-recursion/
     * https://ja.wikipedia.org/wiki/%E6%9C%AB%E5%B0%BE%E5%86%8D%E5%B8%B0
     */
    static interface TailCall<T> {
        TailCall<T> apply();

        default boolean isComplete() {
            return false;
        }

        default T result() {
            throw new Error("not immplemented");
        }

        default T invoke() {
            return Stream.iterate(this, TailCall::apply)
                    .filter(TailCall::isComplete)
                    .findFirst()
                    .get()
                    .result();
        }
    }

    static class TailCalls {
        /* 書籍ではこれが用意されているが、callというメソッド名が「その場で呼び出すのか？」
         * などタイミング由来の疑問を生起させうるのと、実態としては単にラムダ式をそれっぽい名前で
         * ラップしてるだけなので、冗長と考えて無効化してみた。
         */
        /*
        public static <T> TailCall<T> call(final TailCall<T> nextCall) {
            return nextCall;
        }
        */

        /* 書籍では done というメソッド名だが、これも「doneするタイミングは？」など
         * タイミング関連の疑問につながってしまうため、「終端させるラムダ」をより
         * 強く表現するためにも terminator という名前にしてみた。
         */
        public static <T> TailCall<T> terminator(final T value) {
            return new TailCall<T>() {
                @Override
                public boolean isComplete() {
                    return true;
                }

                @Override
                public T result() {
                    return value;
                }

                @Override
                public TailCall<T> apply() {
                    throw new Error("not implemented");
                }
            };
        }
    }

    static TailCall<Integer> factorialTailRec(final int factorial, final int number) {
        if (number == 1) {
            return TailCalls.terminator(factorial);
        }
        return () -> factorialTailRec(factorial * number, number - 1);
    }

    static int factorial(final int number) {
        return factorialTailRec(1, number).invoke();
    }

    @Test
    public void testFactorialTailRec() {
        assertThat(factorial(5)).isEqualTo(120);
        assertThat(factorial(20_000)).isEqualTo(0); // oops -> use BigInteger
    }

    static class BigFactorial {
        public static BigInteger decrement(final BigInteger number) {
            return number.subtract(BigInteger.ONE);
        }

        public static BigInteger multiply(final BigInteger first, final BigInteger second) {
            return first.multiply(second);
        }
    }

    static TailCall<BigInteger> factorialTailRec(final BigInteger factorial, final BigInteger number) {
        if (number.equals(BigInteger.ONE)) {
            return TailCalls.terminator(factorial);
        }
        return () -> factorialTailRec(BigFactorial.multiply(factorial, number), BigFactorial.decrement(number));
    }

    static BigInteger factorial(final BigInteger number) {
        return factorialTailRec(BigInteger.ONE, number).invoke();
    }

    @Test
    public void testFactorialTailRecForBigInteger() {
        assertThat(factorial(BigInteger.valueOf(5))).isEqualTo(BigInteger.valueOf(120));
        assertThat(String.format("%.10s...", factorial(BigInteger.valueOf(20_000)))).isEqualTo("1819206320...");
    }

    // chapter 7.2 : memoization example

    static class RodCutterBasic {
        final List<Integer> prices;

        public RodCutterBasic(final List<Integer> pricesForLength) {
            this.prices = pricesForLength;
        }

        public int maxProfit(final int length) {
            int profit = (length <= prices.size()) ? prices.get(length - 1) : 0;
            for (int i = 1; i < length; i++) {
                final int priceWhenCut = maxProfit(i) + maxProfit(length - i);
                if (profit < priceWhenCut) {
                    profit = priceWhenCut;
                }
            }
            return profit;
        }
    }

    final List<Integer> priceValues = Arrays.asList(2, 1, 1, 2, 2, 2, 1, 8, 9, 15);

    @Test
    public void testRodCutterBasicDemo() {
        final RodCutterBasic rc = new RodCutterBasic(priceValues);
        assertThat(rc.maxProfit(5)).isEqualTo(10);

        // DONT CALL THIS : IT TAKES LONG TIME X(
        // assertThat(rc.maxProfit(22)).isEqualTo(44);
    }

    static class Memoizer {
        public static <T, R> R callMemoized(final BiFunction<Function<T, R>, T, R> function, final T input) {
            /* 引数 function は Function<T, R> と T を受け取りRを返す。
             * ここで Function<T, R>.apply() を呼び出すときに、すでに計算されたTがあるならそれを返したい。
             * つまり 元の Function<T, R> をラップして key-value 形式の簡易キャッシュを挟みたい。
             * しかも Function<T, R> は再帰処理を前提としたい。
             * -> 再帰処理自体 = Function<T, R> を引数として渡せる構造にする。
             * これにより、「キャッシュでラップした Function<T, R>」(i) それ自体を再帰的に渡すことが可能となる。
             * 再帰の中では (i) の apply() を呼び出すことで、キャッシュがあればそれを返し、
             * なければ元の 元の Function<T, R> を返すようにする。
             * これにより、同じ値の計算が大量に発生するような巨大な再帰処理を高速化できる。
             */
            final Function<T, R> memoized = new Function<T, R>() {
                private final Map<T, R> store = new HashMap<>();

                @Override
                public R apply(final T input) {
                    //return store.computeIfAbsent(input, key -> function.apply(this, key));
                    /* 上記書籍コードのままだと、store.computeIfAbsent() の中で再帰的に
                     * store.computeIfAbsent() が呼ばれるため java.util.ConcurrentModificationException
                     * がスローされてしまう。
                     * よく理解せずに HashMap -> ConcurrentHashMap に変更してみたら、今度は
                     * "java.lang.IllegalStateException: Recursive update" が発生してしまう。
                     * そのため computeIfAbsent() の処理をベタ書きに変更した。
                     * computeIfAbsent()の役割としては atomic に処理する = スレッドセーフが望める
                     * 効果もあるが、この場でのユースケースとしては同一スレッドでの再帰呼び出しを
                     * 前提とするため、store に対するスレッドセーフは考慮する必要は無い。
                     * そのため atomic 性を壊しても支障なく、それにより再帰処理により
                     * storeの内部状態の変更が衝突しないよう、操作をロックなしで分離した。
                     */
                    if (store.containsKey(input)) {
                        return store.get(input);
                    }
                    final R v = function.apply(this, input);
                    store.put(input, v);
                    return v;
                }
            };
            return memoized.apply(input);
        }
    }

    static class RodCutterMemoized {
        final List<Integer> prices;

        public RodCutterMemoized(final List<Integer> pricesForLength) {
            this.prices = pricesForLength;
        }

        public int maxProfit(final int rodLength) {
            /* キャッシュを間に挟むようラップされた Function<Integer, Integer> が第一引数で渡されるようにしている。
             * 再帰の部分では maxProfit() 自体ではなく、ラップされた func.apply() を呼ぶことで、
             * キャッシュを使うことによる効率化を実現できる。
             */
            final BiFunction<Function<Integer, Integer>, Integer, Integer> compute = (func, length) -> {
                int profit = (length <= prices.size()) ? prices.get(length - 1) : 0;
                for (int i = 1; i < length; i++) {
                    final int priceWhenCut = func.apply(i) + func.apply(length - i);
                    if (profit < priceWhenCut) {
                        profit = priceWhenCut;
                    }
                }
                return profit;
            };
            return Memoizer.callMemoized(compute, rodLength);
        }
    }

    @Test
    public void testRodCutterMemoizedDemo() {
        final RodCutterMemoized rc = new RodCutterMemoized(priceValues);
        assertThat(rc.maxProfit(5)).isEqualTo(10);
        assertThat(rc.maxProfit(22)).isEqualTo(44);
    }
}
