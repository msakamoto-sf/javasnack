package javasnack.ojcp.se8gold;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.Test;

public class Test04LambdaFunctions {
    /* lambda 式の書き方と、そのサンプルとしての主要関数型インターフェイスの紹介デモ。
     * 
     * "The Java(TM) Tutorials" 参考:
     * ref: https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
     */

    @Test
    public void testLambdaExpressionDemo() {
        // 基本の書き方(1), 引数1つ + 戻り値あり
        Function<String, Integer> lam1 = (String s) -> {
            return s.length();
        };
        assertThat(lam1.apply("abc")).isEqualTo(3);

        // 基本の書き方(2), 引数2つ以上 + 戻り値あり
        BiFunction<String, Integer, Integer> lam2 = (String s, Integer i) -> {
            return s.length() + i;
        };
        assertThat(lam2.apply("abc", 10)).isEqualTo(13);

        // 基本の書き方(3), 引数無し + 戻り値あり
        Supplier<String> lam3 = () -> {
            return "xxx";
        };
        assertThat(lam3.get()).isEqualTo("xxx");

        // 基本の書き方(4), 引数1つ + 戻り値無し
        final StringBuilder sb = new StringBuilder();
        Consumer<String> lam4 = (String s) -> {
            sb.append(s);
        };
        lam4.accept("hello");
        assertThat(sb.toString()).isEqualTo("hello");

        // 引数の省略(1), 引数1つ -> 型指定無し : OK
        Function<String, Integer> lam5 = (s) -> {
            return s.length();
        };
        assertThat(lam5.apply("abc")).isEqualTo(3);

        // 引数の省略(2), 引数2つ以上 -> 型指定無し : OK
        BiFunction<String, Integer, Integer> lam6 = (s, i) -> {
            return s.length() + i;
        };
        assertThat(lam6.apply("abc", 10)).isEqualTo(13);

        /* NOTE: () 省略のルール:
         * 引数1つで型を省略した場合のみ、() を省略できる。
         * 上記以外の、以下のようなケースでは () 省略NG : compile error
         * - 引数1つで型指定あり。
         * - 引数2つ以上(型指定の有無関わらず)
         * - 引数無し
         */

        // ()の省略(1), 引数1つ -> 型指定無し : OK
        Function<String, Integer> lam7 = s -> {
            return s.length();
        };
        assertThat(lam7.apply("abc")).isEqualTo(3);

        // ()の省略(2), 引数1つ -> 型指定あり : compile error
        /*
        Function<String, Integer> lamx = String s -> {
            return s.length();
        };
        */

        // ()の省略(3), 引数2つ以上 -> 型指定無し : compile error
        /*
        BiFunction<String, Integer, Integer> lamx = s, i -> {
            return s.length() + i;
        };
        */

        // ()の省略(4), 引数2つ以上 -> 型指定あり : compile error
        /*
        BiFunction<String, Integer, Integer> lamx = String s, Integer i -> {
            return s.length() + i;
        };
        */

        // ()の省略(5), 引数無し : compile error
        /*
        Supplier<String> lamx = -> {
            return "xxx";
        };
        */

        /* NOTE: {} 省略のルール:
         * - 戻り値ありの場合, {} を省略するなら return も省略。
         *   (片方だけの省略は compile error)
         * - 戻り値無しの場合, {} を省略可能、ただし1行しか書けない。
         */

        // {}の省略(1), return 無し, {} 無し : OK
        Function<String, Integer> lam8 = s -> s.length();
        assertThat(lam8.apply("abc")).isEqualTo(3);

        // {}の省略(2), return あり, {} 無し : compile error
        //Function<String, Integer> lamx = s -> return s.length();

        // {}の省略(3), return 無し, {} あり : compile error
        //Function<String, Integer> lamx = s -> { s.length(); };
        //Function<String, Integer> lamx = s -> { s.length() };

        // {}の省略(4), 戻り値無し, {} 無し : OK
        Consumer<String> lam9 = s -> sb.append(s.toUpperCase());
        //sb.append(s.toLowerCase()); // {} 省略時は1 statement しか書けない -> 2行目はlambdaの外になる。
        lam9.accept("hello");
        assertThat(sb.toString()).isEqualTo("helloHELLO");

    }

    @Test
    public void testMajorFunctionalInterfaceDemo() {
        /* 教科書で紹介されてるような主な関数型インターフェイスの
         * lambda式/anonymous classの両方の書き方デモ
         */

        // Function 系は apply()
        Function<String, Integer> f1a = (String s) -> {
            return s.length();
        };
        assertThat(f1a.apply("abc")).isEqualTo(3);
        Function<String, Integer> f1b = new Function<>() {
            @Override
            public Integer apply(String t) {
                return t.length();
            }
        };
        assertThat(f1b.apply("abc")).isEqualTo(3);

        BiFunction<String, Integer, Integer> f2a = (String s, Integer i) -> {
            return s.length() + i;
        };
        assertThat(f2a.apply("abc", 10)).isEqualTo(13);
        BiFunction<String, Integer, Integer> f2b = new BiFunction<>() {
            @Override
            public Integer apply(String s, Integer i) {
                return s.length() + i;
            }
        };
        assertThat(f2b.apply("abc", 10)).isEqualTo(13);

        // Consumer 系は accept()
        StringBuilder sb = new StringBuilder();
        Consumer<String> c1a = (String s) -> {
            sb.append(s);
        };
        c1a.accept("aaa");
        assertThat(sb.toString()).isEqualTo("aaa");
        Consumer<String> c1b = new Consumer<>() {
            @Override
            public void accept(String t) {
                sb.append(t.toUpperCase());
            }
        };
        c1b.accept("aaa");
        assertThat(sb.toString()).isEqualTo("aaaAAA");

        BiConsumer<String, Integer> c2a = (String s, Integer n) -> {
            for (int i = 0; i < n; i++) {
                sb.append(s.toUpperCase());
            }
        };
        c2a.accept("bb,", 2);
        assertThat(sb.toString()).isEqualTo("aaaAAABB,BB,");
        BiConsumer<String, Integer> c2b = new BiConsumer<>() {
            @Override
            public void accept(String s, Integer n) {
                for (int i = 0; i < n; i++) {
                    sb.append(s.toLowerCase());
                }
            }
        };
        c2b.accept("CC,", 2);
        assertThat(sb.toString()).isEqualTo("aaaAAABB,BB,cc,cc,");

        // Predicate 系は test()
        Predicate<String> p1a = (String s) -> "aaa".equals(s);
        assertThat(p1a.test("aaa")).isTrue();
        assertThat(p1a.test("bbb")).isFalse();

        Predicate<String> p1b = new Predicate<>() {
            @Override
            public boolean test(String s) {
                return "aaa".equals(s);
            }
        };
        assertThat(p1b.test("aaa")).isTrue();
        assertThat(p1b.test("bbb")).isFalse();

        BiPredicate<String, Integer> p2a = (String s, Integer i) -> s.length() == i;
        assertThat(p2a.test("aaa", 3)).isTrue();
        assertThat(p2a.test("aaa", 4)).isFalse();

        BiPredicate<String, Integer> p2b = new BiPredicate<>() {
            @Override
            public boolean test(String s, Integer i) {
                return s.length() != i;
            }
        };
        assertThat(p2b.test("aaa", 3)).isFalse();
        assertThat(p2b.test("aaa", 4)).isTrue();

        // Supplier 系は get()
        Supplier<String> s1a = () -> "hello";
        assertThat(s1a.get()).isEqualTo("hello");
        Supplier<String> s1b = new Supplier<>() {
            @Override
            public String get() {
                return "HELLO";
            }
        };
        assertThat(s1b.get()).isEqualTo("HELLO");

        // Operator 系は apply() (Function の派生だから?)
        UnaryOperator<String> op1a = (String s) -> s.toUpperCase();
        assertThat(op1a.apply("hello")).isEqualTo("HELLO");
        UnaryOperator<String> op1b = new UnaryOperator<>() {
            @Override
            public String apply(String s) {
                return s.toLowerCase();
            }
        };
        assertThat(op1b.apply("HELLO")).isEqualTo("hello");

        BinaryOperator<String> op2a = (String s1, String s2) -> s1.toUpperCase() + s2.toLowerCase();
        assertThat(op2a.apply("aa", "BB")).isEqualTo("AAbb");
        BinaryOperator<String> op2b = new BinaryOperator<>() {
            @Override
            public String apply(String s1, String s2) {
                return s1.toLowerCase() + s2.toUpperCase();
            }
        };
        assertThat(op2b.apply("AA", "bb")).isEqualTo("aaBB");
    }

    @Test
    public void testImplicitlyFinalDemo() {
        final int a = 10;
        int b = 20; // implicitly final
        @SuppressWarnings("unused")
        int c = 30;
        c = 35; // non-final
        int d = 40; // implicitly final
        Supplier<String> s1 = () -> {
            //return "a=" + a + ", b=" + b + ", c=" + c + ", d=" + d; // c の参照箇所で compile error
            //d = 50; // compile error
            return "a=" + a + ",b=" + b + ",d=" + d;
        };
        assertThat(s1.get()).isEqualTo("a=10,b=20,d=40");
    }
}
