package javasnack.ojcp.se8gold;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

public class Test04MethodReferences {
    /* メソッド参照のサンプルテストコード
     * 
     * "The Java(TM) Tutorials" 参考:
     * ref: https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html
     */

    static String getString() {
        return "hello";
    }

    @Test
    public void testStaticMethodReferenceDemo() {
        // 引数なしの static method reference
        Supplier<String> s1 = Test04MethodReferences::getString;
        assertThat(s1.get()).isEqualTo("hello");
        //Supplier<String> s2 = this::getString; // static method だと this:: は compile error
        //Supplier<String> s3 = ::getString; // 自分のクラスだからと省略もNG, compile error

        // 引数1つの static method reference
        Function<String, Integer> f1 = Integer::parseInt;
        assertThat(f1.apply("123")).isEqualTo(123);

        // 引数2つの static method reference (Comparator も functional interface)
        Comparator<Integer> cmp1 = Integer::compare;
        assertThat(cmp1.compare(1, 2)).isEqualTo(-1);
        assertThat(cmp1.compare(2, 1)).isEqualTo(1);
    }

    String strToUpper(String s) {
        return s.toUpperCase();
    }

    @Test
    public void testInstanceMethodReferenceDemo() {
        List<String> strings = List.of("aa", "bb", "cc");
        StringBuilder sb = new StringBuilder();
        // 呼びたいメソッドの持ち主(instance)が外部から与えられるケース
        strings.forEach(sb::append);
        // Consumer<String>.accept(String s) -> sb.append(s) になる。
        assertThat(sb.toString()).isEqualTo("aabbcc");

        // compile error になる書き方バリエーション
        /*
        Consumer<String> c1 = (String s) -> {
            sb::append;
        };
        Consumer<String> c1 = (String s) -> sb::append;
        Consumer<String> c1 = s -> sb::append;
        */
        // これはOK.
        Consumer<String> c1 = sb::append;
        c1.accept("dd");
        assertThat(sb.toString()).isEqualTo("aabbccdd");

        // 呼びたいメソッドの持ち主(instance)がラムダ式の引数となるケース
        Function<String, Integer> f1 = s -> s.length(); // lambda expression version
        //Function<String, Integer> f2 = s -> s::length(); // compile error
        Function<String, Integer> f3 = String::length; // apply(String s) の s.length() が呼ばれる。
        assertThat(f1.apply("aa")).isEqualTo(2);
        assertThat(f3.apply("bbb")).isEqualTo(3);

        // this 経由の method reference
        Function<String, String> fthis = this::strToUpper;
        assertThat(fthis.apply("hello")).isEqualTo("HELLO");

        /* lambda method の引数が2つ以上あるときは、
         * 1つめが method reference での持ち主(instance)になり、
         * 2つめ以降が method reference に渡される引数になる。
         */
        BiFunction<String, Integer, Character> f4 = (s, i) -> s.charAt(i);
        assertThat(f4.apply("abc", 1)).isEqualTo('b');
        //BiFunction<String, Integer, Character> f5 = (s, i) -> String::charAt; // compile error

        // OK : 1つめが String.charAt(int index) の呼び出し元 instance になり、
        // 2つめが charAt(int index) の引数になる。
        BiFunction<String, Integer, Character> f6 = String::charAt;
        assertThat(f6.apply("abc", 1)).isEqualTo('b');

        // NG : 1つめが Integer なので、 String.charAt(int index) の呼び出し元になれない。
        //BiFunction<Integer, String, Character> f7 = String::charAt; // compile error

        // static method と instance method で同じ名前がある場合は、メソッド参照できない。
        //Function<Double, Integer> f1 = Double::hashCode; // compile error
    }

    static class SomeClass0 {
        int a = 10;
    }

    static class SomeClass1 {
        final int a;

        SomeClass1(final int a) {
            this.a = a;
        }
    }

    static class SomeClass2 {
        final int a;

        SomeClass2(final int x, final int y) {
            this.a = x + y;
        }
    }

    @Test
    public void testConstructorMethodReferenceDemo() {
        // 引数なしの constructor reference
        Supplier<SomeClass0> s1 = () -> new SomeClass0();
        SomeClass0 o0 = s1.get();
        assertThat(o0.a).isEqualTo(10);
        Supplier<SomeClass0> s2 = SomeClass0::new;
        o0 = s2.get();
        assertThat(o0.a).isEqualTo(10);

        // 引数1つの constructor reference
        Function<Integer, SomeClass1> f1 = i -> new SomeClass1(i);
        assertThat(f1.apply(25).a).isEqualTo(25);
        Function<Integer, SomeClass1> f2 = SomeClass1::new;
        assertThat(f2.apply(30).a).isEqualByComparingTo(30);

        // 引数2つの constructor reference
        BiFunction<Integer, Integer, SomeClass2> f3 = (i, j) -> new SomeClass2(i, j);
        assertThat(f3.apply(1, 2).a).isEqualTo(3);
        BiFunction<Integer, Integer, SomeClass2> f4 = SomeClass2::new;
        assertThat(f4.apply(3, 4).a).isEqualTo(7);

        // List<E> を生成する constructor reference
        Supplier<List<SomeClass0>> s3 = () -> new ArrayList<>();
        List<SomeClass0> l0 = s3.get();
        assertThat(l0.size()).isEqualTo(0);
        //Supplier<List<SomeClass0>> s4 = ArrayList<>::new; // compile error
        Supplier<List<SomeClass0>> s4 = ArrayList<SomeClass0>::new;
        l0 = s4.get();
        assertThat(l0.size()).isEqualTo(0);

        // String[] を生成する constructor reference : 長さを使うため Function で参照する。
        Function<Integer, String[]> f5 = len -> new String[len];
        assertThat(f5.apply(5).length).isEqualTo(5);
        Function<Integer, String[]> f6 = String[]::new;
        assertThat(f6.apply(6).length).isEqualTo(6);
    }
}
