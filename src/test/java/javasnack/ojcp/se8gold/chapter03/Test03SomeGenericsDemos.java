package javasnack.ojcp.se8gold.chapter03;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.Test;

public class Test03SomeGenericsDemos {
    /* generics については test の javasnack.langspecs.generics パッケージ配下の
     * サンプルテストコードで色々実験しているので、重箱の隅をつつくようなケースはそちらを参照。
     * 
     * こちらでは教科書的な単純・シンプル・典型的なサンプルコードの例示に抑えておく。
     */

    // diamond 演算子が使える場所のデモ

    // メソッド引数で diamond 演算子使える。
    static void m1(List<String> strings) {
    }

    static List<String> m2() {
        // 戻り値の型があるので、diamond 演算子使える。
        return new ArrayList<>();
    }

    /* 戻り値の型では diamond 演算子は使えない。
    static List<> m3() {
        return new ArrayList<String>();
    }
    */

    @Test
    public void testDiamondOperatorDemo() {
        @SuppressWarnings("unused")
        List<String> l1 = new ArrayList<>(); // OK
        m1(new ArrayList<>()); // Java SE8 からOK.
        m1(new ArrayList<String>()); // Java SE8 以前
    }

    // 型パラメータを使ったクラスの例
    static class SomeContainer<T1, T2, T3> {
        final T1 val1;
        final T2 val2;
        final T3 val3;
        //static T1 sval1; // static field には型パラメータを使えない : compile error

        SomeContainer(final T1 val1, final T2 val2, final T3 val3) {
            this.val1 = val1;
            this.val2 = val2;
            this.val3 = val3;
        }
    }

    @Test
    public void testTypeParameterClassDemo() {
        SomeContainer<String, Integer, File> o1 = new SomeContainer<>("hello", 100, new File("aa"));
        assertThat(o1.val1).isEqualTo("hello");
        assertThat(o1.val2).isEqualTo(100);
        assertThat(o1.val3).isEqualTo(new File("aa"));
    }

    // 型パラメータを使ったメソッドの例
    <T> List<T> asList(T a, T b, T c) {
        List<T> r = new ArrayList<>();
        r.add(a);
        r.add(b);
        r.add(c);
        return r;
    }

    static <K, V> Map<K, V> asMap(K k1, V v1, K k2, V v2) {
        Map<K, V> r = new HashMap<>();
        r.put(k1, v1);
        r.put(k2, v2);
        return r;
    }

    @Test
    public void testTypeParameterMethodDemo() {
        List<String> l1 = asList("aa", "bb", "cc"); // diamond 演算子不要
        assertThat(l1).isEqualTo(List.of("aa", "bb", "cc"));
        Map<String, Integer> m1 = asMap("aa", 10, "bb", 20);
        assertThat(m1.size()).isEqualTo(2);
        assertThat(m1.get("aa")).isEqualTo(10);
        assertThat(m1.get("bb")).isEqualTo(20);
    }

    // interface で generics を使う例
    static interface TypedList<T> {
        // 型パラメータを使ったメソッド宣言
        List<T> asList(T a, T b, T c);

        /* interface の static メソッドでは型パラメータを使えない
        static List<T> emptyList() {
            return Collections.emtpyList();
        }
        */

        // default method では型パラメータを使える
        default List<T> asList(T a, T b) {
            List<T> r = new ArrayList<>();
            r.add(a);
            r.add(b);
            return r;
        }
    }

    static class StringList implements TypedList<String> {
        @Override
        public List<String> asList(String a, String b, String c) {
            List<String> r = new ArrayList<>();
            r.add(a);
            r.add(b);
            r.add(c);
            return r;
        }
    }

    static class IntegerList implements TypedList<Integer> {
        @Override
        public List<Integer> asList(Integer a, Integer b, Integer c) {
            List<Integer> r = new ArrayList<>();
            r.add(a);
            r.add(b);
            r.add(c);
            return r;
        }
    }

    @Test
    public void testInterfaceWithGenericsDemo() {
        TypedList<String> l1 = new StringList(); // diamond 演算子不要
        assertThat(l1.asList("aa", "bb")).isEqualTo(List.of("aa", "bb")); // default method
        assertThat(l1.asList("aa", "bb", "cc")).isEqualTo(List.of("aa", "bb", "cc"));
        //l1 = new IntegerList(); // compile error
        TypedList<Integer> l2 = new IntegerList(); // diamond 演算子不要
        assertThat(l2.asList(10, 20)).isEqualTo(List.of(10, 20)); // default method
        assertThat(l2.asList(10, 20, 30)).isEqualTo(List.of(10, 20, 30));
        l1 = new TypedList<>() { // ここで diamond 演算子を使える。
            @Override
            public List<String> asList(String a, String b, String c) {
                List<String> r = new ArrayList<>();
                r.add(a + b + c);
                return r;
            }
        };
        assertThat(l1.asList("AA", "BB")).isEqualTo(List.of("AA", "BB")); // default method
        assertThat(l1.asList("aa", "bb", "cc")).isEqualTo(List.of("aabbcc"));
    }

    // extends を使った型パラメータのデモ
    static class NumberContainer<T extends Number> {
        private final T num;

        NumberContainer(final T num) {
            this.num = num;
        }

        T getNumber() {
            return this.num;
        }
    }

    @Test
    public void testExtendedTypeParameterDemo() {
        NumberContainer<Integer> o1 = new NumberContainer<>(100);
        assertThat(o1.getNumber()).isEqualTo(100);
        NumberContainer<Double> o2 = new NumberContainer<>(3.14);
        assertThat(o2.getNumber()).isEqualTo(3.14);
    }

    // ワイルドカードを使った例

    static class SomeParent {
        @Override
        public String toString() {
            return "[parent]";
        }
    }

    static class SomeChild extends SomeParent {
        @Override
        public String toString() {
            return "[child]";
        }
    }

    static class SomeGrandChild extends SomeChild {
        @Override
        public String toString() {
            return "[grand-child]";
        }
    }

    // <? extends X> : X自身か、Xを継承した型を受け入れる。
    static String l2stra(List<? extends SomeChild> l0) {
        // NOTE: argument で使える以上、ローカル変数でのassignにも使える。
        @SuppressWarnings("unused")
        List<? extends SomeChild> lx = l0;
        @SuppressWarnings("unused")
        List<? super SomeChild> ly = Collections.emptyList();
        /* l0 の中身は List<SomeChild> または List<SomeGrandChild> となる。
         * List<SomeChild> に SomeChild / SomeGrandChild いずれかを add() できても問題ないが、
         * List<SomeGrandChild> に SomeChild を add() すると、あとで SomeGrandChild を想定した
         * get() で不整合が発生する。(SomeGrandChild is SomeChild, but SomeChild IS NOT SomeGrandChild)
         * -> このため、ワイルドカードを引数に持つメソッドを呼べないようになっている。(compile error) 
         */
        //l0.add(new SomeParent());
        //l0.add(new SomeChild());
        //l0.add(new SomeGrandChild());
        l0.add(null); // nullは追加OK.
        StringBuilder sb = new StringBuilder();
        for (SomeChild x : l0) {
            if (Objects.isNull(x)) {
                continue;
            }
            sb.append(x.toString());
        }
        return sb.toString();
    }

    // <? super X> : X自身か、Xの親クラスを受け入れる。
    static String l2strb(List<? super SomeChild> l0) {
        /* l0 の中身は List<SomeParent> または List<SomeChild> となる。
         * あるいは、SomeParentの更に親となる List<Object>, List<SomeParent1>, List<SomeParent2>, ...
         * のどれかかもしれない。
         * List<SomeChild> に SomeChild / SomeGrandChild いずれかを add() できても問題ないが、
         * 仮に List<SomeParent> だったときに、SomeChildの一応の親クラスである Object を add()
         * できてしまうと不整合が発生する。
         * (SomeParent is Object, but Object IS NOT SomeParent)
         * -> List<? super X> では X および X の継承クラスは add() が可能になるが、
         * Xの親クラスを add() する(= ワイルドカードを引数に持つメソッド)は呼べないようになっている。(compile error) 
         */
        //l0.add(new SomeParent());
        l0.add(new SomeChild());
        l0.add(new SomeGrandChild());
        l0.add(null); // nullは追加OK.
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < l0.size(); i++) {
            /* l0 の中身が List<Object> や List<SomeParent> あるいは他の途中の親クラスとなるため、
             * <? super X> のインスタンスを参照するための適切なクラスを決定できない。
             * このため、<? super X> を戻り値とするメソッドで参照を取得することができない。(compile error)
             */
            //SomeChild x = l0.get(i);
            Object x = l0.get(i); // Object としては参照できる。
            if (Objects.isNull(x)) {
                continue;
            }
            sb.append(x.toString());
        }
        return sb.toString();
    }

    @Test
    public void testWildcardGenericsDemo() {
        List<SomeParent> parents0 = new ArrayList<>(List.of(new SomeParent()));
        List<SomeChild> children0 = new ArrayList<>(List.of(new SomeChild()));
        List<SomeGrandChild> grandChildren0 = new ArrayList<>(List.of(new SomeGrandChild()));

        // <? extends SomeChild>
        //l2stra(parents0); // compile error
        assertThat(l2stra(children0)).isEqualTo("[child]");
        assertThat(l2stra(grandChildren0)).isEqualTo("[grand-child]");

        // <? super SomeChild>
        assertThat(l2strb(parents0)).isEqualTo("[parent][child][grand-child]");
        assertThat(l2strb(children0)).isEqualTo("[child][child][grand-child]");
        //l2strb(grandChildren0); // compile error
    }
}
