package javasnack.langspecs.generics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.Test;

public class TestGenericsViaEffectiveJavaChapter5 {
    /* Effective Java 3rd の Chapter 5, Generics : item 26 - 33 までのサンプルコードとポイントメモ。
     * 
     * 2020-04 時点では以下のURLで Chapter 5 のみ試し読み可能。
     * https://www.informit.com/articles/article.aspx?p=2861454
     * 
     * NOTE: 文中では "reified" という表現が何箇所か出てくる。
     * array は reified で、generics は not reified という風に使われる。
     * -> 配列の型は残るが、generics は erasure によって消されるということを表現しているらしい。
     * "reify" 自体は具象化する・具体的に考えるなどを意味するため、
     * 具体的な型として残る array が reified で、
     * erasure によって型変数部分が消される generic は not reified というニュアンスと思われる。
     */

    /* item 26 : raw type より generic type を使う。
     * -> instanceof では実型パラメータ指定できないので、
     * 非境界型ワイルドカードでキャストして使うと便利、というとこだけサンプルコードに。
     */

    static int item26demo(final Object o) {
        // instanceof でチェック後のcat -> コンパイラエラーにならないし、警告も出ない。
        if (o instanceof Set) {
            final Set<?> s = (Set<?>) o;
            return s.size();
        } else if (o instanceof List) {
            final List<?> list = (List<?>) o;
            return list.size();
        } else if (o instanceof Map) {
            final Map<?, ?> m = (Map<?, ?>) o;
            return m.size();
        }
        return -1;
    }

    @Test
    public void testItem26Demo() {
        final Set<String> set1 = Set.of("aa", "bb");
        assertThat(item26demo(set1)).isEqualTo(2);
        final List<String> list1 = List.of("cc", "dd", "ee");
        assertThat(item26demo(list1)).isEqualTo(3);
        final Map<String, String> map1 = Map.of("ff", "gg");
        assertThat(item26demo(map1)).isEqualTo(1);
        assertThat(item26demo("xxx")).isEqualTo(-1);
    }

    /* item 27 : unchecked warning は原則無視せずきっちり対処する。
     * 「どう頑張ってもwarningが消せない」というときは、
     * まず自分が generics や型システムを間違って使ってないかチェックする。
     * 本当に安全なときだけ @SuppressWarnings("unchecked") を使う。
     * さらに、 @SuppressWarnings は可能な限りスコープを狭くして使う。 
     */

    static <T> T[] item27CopyArray(final T[] src, final T[] dest) {
        final int size = src.length;
        if (dest.length < size) {
            /* Type safety: Unchecked cast from Object[] to T[] が発生する。
             * -> 可能な限りスコープを狭くするため、ローカル変数に一度代入し、
             * そこの箇所だけ限定で "@SuppressWarnings" を使う。
             */
            @SuppressWarnings("unchecked")
            final T[] ret = (T[]) Arrays.copyOf(src, size, dest.getClass());
            return ret;
        }
        System.arraycopy(src, 0, dest, 0, size);
        if (dest.length > size) {
            dest[size] = null;
        }
        return dest;
    }

    @Test
    public void testItem27Demo() {
        final String[] arr1 = item27CopyArray(new String[] { "aa", "bb", "cc" }, new String[2]);
        assertThat(arr1).hasSize(3);
        assertThat(arr1[0]).isEqualTo("aa");
        assertThat(arr1[1]).isEqualTo("bb");
        assertThat(arr1[2]).isEqualTo("cc");

        final String[] dest2 = { "a", "b", "c", "d" };
        final String[] arr2 = item27CopyArray(new String[] { "AA", "BB", "CC" }, dest2);
        assertThat(arr2).hasSize(4);
        assertThat(arr2[0]).isEqualTo("AA");
        assertThat(arr2[1]).isEqualTo("BB");
        assertThat(arr2[2]).isEqualTo("CC");
        assertThat(arr2[3]).isNull();
    }

    /* item 28 : array より List を使おう。
     * 
     */

    @Test
    public void testItem28Demo() {
        // 子クラスの array 参照を親クラスの array 参照に代入できる。
        Object[] objectArray = new Long[1];
        assertThrows(ArrayStoreException.class, () -> {
            // compileは通るが、ランタイム例外が発生する。
            objectArray[0] = "hello";
        });

        /* generic型の配列 / パラメータ化された型の配列 / 型変数の配列は作れない。
         * new List<E>[]
         * new List<String>[]
         * new E[]
         */

        /* 以下はコンパイルできないデモと解説。
         * (1) パラメータ化された方の配列はコンパイルエラー。
         * List<String>[] stringLists = new List<String>[1];
         * (2) これは普通の Integer の List
         * List<Integer> intList = List.of(42);
         * (3) Object型の配列であれば、子クラスの配列を参照できる。
         * Object[] objects = stringLists;
         * (4) Object型なので、なんでも代入できてしまう。
         * objects[0] = intList;
         * (5) そうなると (3) で参照していた配列を更新してしまい、実行時に ClassCastException が発生する。
         * String s = stringLists[0].get(0);
         * ->  そうならないように、(1) でコンパイルエラーとしてコンパイルできないようになっている。
        */

        // generics以降は非推奨で安全では無くなった設計例 : Object[] を使用
        class UnsafeChooser {
            private final Object[] choiceArray;

            public UnsafeChooser(@SuppressWarnings("rawtypes") Collection choices) {
                choiceArray = choices.toArray();
            }

            public Object choose() {
                Random rnd = ThreadLocalRandom.current();
                return choiceArray[rnd.nextInt(choiceArray.length)];
            }
        }

        // 動くは動くけど、人間がちゃんとcastしないといけないので安全とは言えない。
        final List<String> src = List.of("aa", "bb", "cc");
        final UnsafeChooser chooser = new UnsafeChooser(src);
        final String r1 = (String) chooser.choose();
        assertThat(src.contains(r1)).isTrue();

        // generics 型を使った、より安全な設計例
        class SaferChooser<T> {
            private final List<T> choiceList;

            public SaferChooser(Collection<T> choices) {
                choiceList = new ArrayList<>(choices);
            }

            public T choose() {
                Random rnd = ThreadLocalRandom.current();
                return choiceList.get(rnd.nextInt(choiceList.size()));
            }
        }

        // 人間が cast する必要が無くなった。in-outともにコンパイラがチェックしてくれる。
        final SaferChooser<String> chooser2 = new SaferChooser<>(src);
        assertThat(src.contains(chooser2.choose())).isTrue();
    }

    /* item 29 : generic type を使おう！
     * -> Object[] を使った昔ながらのstackクラス実装を、generic type による E[] の実装に改善するデモ。
     * (インスタンススコープの generic type のデモ)
     * 
     * 試し読みにも書いてあるが、List<E> を使ったほうがわかりやすさはある。
     * ただし stack / list / map など基本的なデータ構造は配列を使って実装していたりもするので、
     * そうした実装でのgeneric typeの使い方を学ぶという観点から E[] を使っている。
     */

    // Object[] を使った古い設計の stack
    static class ClassicStack {
        private Object[] elements;
        private int size = 0;
        private static final int DEFAULT_INITIAL_CAPACITY = 16;

        public ClassicStack() {
            elements = new Object[DEFAULT_INITIAL_CAPACITY];
        }

        public void push(Object e) {
            ensureCapacity();
            elements[size++] = e;
        }

        public Object pop() {
            if (size == 0) {
                throw new EmptyStackException();
            }
            Object result = elements[--size];
            elements[size] = null; // Eliminate obsolete reference
            return result;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        private void ensureCapacity() {
            if (elements.length == size) {
                elements = Arrays.copyOf(elements, 2 * size + 1);
            }
        }
    }

    // generic type を使った type safe な設計の stack
    static class GenericsStack<E> {
        private E[] elements;
        private int size = 0;
        private static final int DEFAULT_INITIAL_CAPACITY = 16;

        // The elements array will contain only E instances from push(E).
        // This is sufficient to ensure type safety, but the runtime
        // type of the array won't be E[]; it will always be Object[]!
        @SuppressWarnings("unchecked")
        public GenericsStack() {
            elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
        }

        public void push(E e) {
            ensureCapacity();
            elements[size++] = e;
        }

        public E pop() {
            if (size == 0) {
                throw new EmptyStackException();
            }
            /* 参照元の試し読みではここでも @SuppressWarnings("unchecked") を指定していた。
             * しかし java11 では不要なwarningと指摘されたためコメントアウト。
             * castも不要となっている。(java8の型推論強化の影響か？)
             */
            // push requires elements to be of type E, so cast is correct
            //@SuppressWarnings("unchecked")
            E result = elements[--size];
            elements[size] = null; // Eliminate obsolete reference
            return result;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        private void ensureCapacity() {
            if (elements.length == size) {
                elements = Arrays.copyOf(elements, 2 * size + 1);
            }
        }
    }

    @Test
    public void testItem29Demo() {
        // Object[] 版のstackの動作確認
        final ClassicStack stack1 = new ClassicStack();
        assertThat(stack1.isEmpty()).isTrue();
        stack1.push("hello");
        assertThat(stack1.isEmpty()).isFalse();
        stack1.push(Integer.valueOf(100));
        stack1.push(new int[] { 1, 2, 3 });
        int[] r1 = (int[]) stack1.pop();
        assertThat(r1).isEqualTo(new int[] { 1, 2, 3 });
        Integer r2 = (Integer) stack1.pop();
        assertThat(r2).isEqualTo(Integer.valueOf(100));
        String r3 = (String) stack1.pop();
        assertThat(r3).isEqualTo("hello");
        // -> なんでも push できてしまい、pop する際は cast が必要。

        // generic type 版のstackの動作確認
        final GenericsStack<String> stack2 = new GenericsStack<>();
        assertThat(stack2.isEmpty()).isTrue();
        stack2.push("aa");
        assertThat(stack2.isEmpty()).isFalse();
        // -> コンパイルエラーになり、型安全が保たれる。
        //stack2.push(Integer.valueOf(100));
        //stack2.push(new int[] { 1, 2, 3 });
        stack2.push("bb");
        stack2.push("cc");
        assertThat(stack2.pop()).isEqualTo("cc");
        assertThat(stack2.pop()).isEqualTo("bb");
        assertThat(stack2.pop()).isEqualTo("aa");
    }

    /* item 30 : generic method を使おう！ : メソッドスコープのジェネリクスの使い方。
     */

    // generics 登場前の、raw type を使った安全でないメソッド例
    @SuppressWarnings({ "rawtypes", "unchecked" })
    static Set rawtypedUnion(Set s1, Set s2) {
        Set result = new HashSet(s1);
        result.addAll(s2);
        return result;
    }

    // generics を使った、type safe なメソッド例
    static <E> Set<E> genericsUnion(Set<E> s1, Set<E> s2) {
        Set<E> result = new HashSet<>(s1);
        result.addAll(s2);
        return result;
    }

    @Test
    public void testItem30UnionDemo() {
        final Set<String> set1 = Set.of("aa", "bb");
        final Set<String> set2 = Set.of("bb", "cc");

        // raw type 版
        @SuppressWarnings("unchecked")
        final Set<String> r1 = (Set<String>) rawtypedUnion(set1, set2);
        assertThat(r1).isEqualTo(Set.of("aa", "bb", "cc"));

        // generics 版
        final Set<String> r2 = genericsUnion(set1, set2);
        assertThat(r2).isEqualTo(Set.of("aa", "bb", "cc"));
    }

    /* item 30 (2) : generic singleton factory pattern のデモ。
     * immutable なインスタンス作成を複数の型に対応させたい場合のデザインパターン。
     * -> Object型を返す Function をシングルトンとして用意し、
     * それをメソッドスコープのgenericsを適用したメソッドでラップする。
     * Collection のメソッドや Collections.empty{Set|List|Map}() などで使われている。
     */

    // Generic singleton factory pattern
    static UnaryOperator<Object> IDENTITY_FN = (t) -> t;

    /* unchecked warning が発生するが、T が何であっても副作用無く
     * IDENTITY_FNを返すだけなので、これは suppress してOK.
     */
    @SuppressWarnings("unchecked")
    static <T> UnaryOperator<T> identityFunction() {
        return (UnaryOperator<T>) IDENTITY_FN;
    }

    @Test
    public void testItem30GenericSingletonFactoryPatternDemo() {
        final UnaryOperator<String> sameString = identityFunction();
        assertThat(sameString.apply("hello")).isEqualTo("hello");
        final UnaryOperator<Number> sameNumber = identityFunction();
        assertThat(sameNumber.apply(100)).isEqualTo(100);
    }

    /* item 30 (3) : recursive type bound (再帰的型境界?) の使用例
     * 
     * max() の実装で、引数として受け取る Collection<E> の実型パラメータには
     * Comparable を実装している型を必要とする。
     * -> "<E extends Comparable<E>>" という再帰的な境界型パラメータを使って実現する。
     */
    static <E extends Comparable<E>> E max(Collection<E> c) {
        if (c.isEmpty()) {
            throw new IllegalArgumentException("Empty collection");
        }
        E result = null;
        for (E e : c) {
            if (result == null || e.compareTo(result) > 0) {
                result = Objects.requireNonNull(e);
            }
        }
        return result;
    }

    @Test
    public void testItem30MaxDemo() {
        assertThat(max(List.of(1, 3, 2))).isEqualTo(3);
        assertThat(max(List.of("aa", "bb", "cc"))).isEqualTo("cc");
    }

    /* item 31 境界ワイルドカード型(bounded wildcard type) の使用例とPECS原則の解説
     * 
     * ジェネリクス対応した GenericsStack<E> について poshAll() と popAll() を実装してみる。
     * -> 引数の型について、bounded wildcard type を導入することで
     * APIとしての柔軟性を実現する例をデモしている。
     */

    // ジェネリクス型の非変性(invariant)を確認できる pushAll()/popAll() 実装
    static class InvariantStack<E> extends GenericsStack<E> {
        public void pushAll(Iterable<E> src) {
            for (E e : src) {
                push(e);
            }
        }

        public void popAll(Collection<E> dst) {
            while (!isEmpty()) {
                dst.add(pop());
            }
        }
    }

    // 境界ワイルドカード型を使った pushAll()/popAll() 実装
    static class WildcardTypedStack<E> extends GenericsStack<E> {
        // producer として引数を受け取るときは、extends で柔軟性を確保。
        public void pushAll(Iterable<? extends E> producer) {
            for (E e : producer) {
                push(e);
            }
        }

        // consumer として引数を受け取るときは、super で柔軟性を確保。
        public void popAll(Collection<? super E> consumer) {
            while (!isEmpty()) {
                consumer.add(pop());
            }
        }
    }

    @Test
    public void testItem31StackDemo() {
        final InvariantStack<SomeChild> stack1 = new InvariantStack<>();
        stack1.pushAll(List.of(new SomeChild("aa", "bb"), new SomeChild("cc", "dd")));
        // パラメータ化された型は非変のため、親クラスの実型パラメータではコンパイルエラー。
        //stack1.pushAll(List.of(new SomeParent("aa"), new SomeParent("cc")));
        // パラメータ化された型は非変のため、子クラスの実型パラメータでもコンパイルエラー。
        // (java8以降の型推論を回避するため、メソッド呼び出し時に実型パラメータを明示的にバインド)
        //stack1.pushAll(List.<SomeGrandChild>of(new SomeGrandChild("aa", "bb", "cc")));
        // java8以降は型推論してくれるため、以下はコンパイルエラーとならない。
        stack1.pushAll(List.of(new SomeGrandChild("AA", "BB", "CC")));

        final List<SomeParent> parents = new ArrayList<>();
        final List<SomeChild> children = new ArrayList<>();
        @SuppressWarnings("unused")
        final List<SomeGrandChild> grandChildren = new ArrayList<>();
        // stack1.popAll(parents); // コンパイルエラー
        stack1.popAll(children);
        // stack1.popAll(grandChildren); // コンパイルエラー

        // 境界ワイルドカード型を使うと・・・
        final WildcardTypedStack<SomeChild> stack2 = new WildcardTypedStack<>();
        stack2.pushAll(List.of(new SomeChild("aa", "bb"), new SomeChild("cc", "dd")));
        // 実型パラメータが子クラスであれば Child xx = new GrandChild() が可能となり、コンパイルエラーにならない。 
        // (java8以降の型推論を回避するため、メソッド呼び出し時に実型パラメータを明示的にバインド)
        stack2.pushAll(List.<SomeGrandChild>of(new SomeGrandChild("AA", "BB", "CC")));
        // Child xx = new Parent() は通常参照型でも成立しないため、これはコンパイルエラー。
        //stack2.pushAll(List.of(new SomeParent("aa"), new SomeParent("cc")));

        // <? super E> : Parent super Child が成立するので、以下はコンパイルエラーにならない。
        stack2.popAll(parents);
        /* -> 注意深く見ると、メソッド内部で consumer が値を消費する際は
         * consumer<super class of E>.consume(E target)
         * となり、consumeは E の親クラスを受け取るシグネチャとなっていることが期待される。
         * つまり consume() の呼び出しでは Parent xxx = new Child() という関係が成立し、
         * Java の参照型における共変性が現れている。
         * (一見した感じだと「反変」(Child xxx = new Parnet()) な機能だが、
         *  中を追っていくとちゃんと「共変」(Parent xxx = new Child()) となっている)
         */
        stack2.popAll(children);
        // stack2.popAll(grandChildren); // コンパイルエラー

        assertThat(parents).hasSize(3);
        assertThat(parents.get(0).field1).isEqualTo("AA");
        assertThat(parents.get(1).field1).isEqualTo("cc");
        assertThat(parents.get(2).field1).isEqualTo("aa");
    }

    // item 31 (2) : bounded wildcard type を使って柔軟性を向上させたunion 
    static <E> Set<E> wildcardTypedUnion(Set<? extends E> s1, Set<? extends E> s2) {
        Set<E> result = new HashSet<>(s1);
        result.addAll(s2);
        return result;
    }

    @Test
    public void testItem31UnionDemo() {
        final Set<Integer> set1 = Set.of(1, 2);
        final Set<Double> set2 = Set.of(3.0, 4.0);
        // java8 以降だとメソッドスコープの型変数を推論してくれる。
        final Set<Number> r1 = wildcardTypedUnion(set1, set2);
        assertThat(r1).isEqualTo(Set.of(1, 2, 3.0, 4.0));

        // java8 より前だと、型推論が弱かったため明示的に実型パラメータをバインドする必要があった。
        final Set<Number> r2 = TestGenericsViaEffectiveJavaChapter5.<Number>wildcardTypedUnion(set1, set2);
        assertThat(r2).isEqualTo(Set.of(1, 2, 3.0, 4.0));
    }

    /* item 31 (3) : max() を境界型ワイルドカード型で柔軟性を拡張した例。
     * Comparable を compareTo() として使うだけであれば consumer なので、Comparable<? super E> が使える。
     * Collection を iterate するだけならば producer なので、Collection<? extends E> が使える。
     */
    static <E extends Comparable<? super E>> E wildcardTypedMax(Collection<? extends E> c) {
        if (c.isEmpty()) {
            throw new IllegalArgumentException("Empty collection");
        }
        E result = null;
        for (E e : c) {
            if (result == null || e.compareTo(result) > 0) {
                result = Objects.requireNonNull(e);
            }
        }
        return result;
    }

    @Test
    public void testItem31MaxDemo() {
        assertThat(max(List.of(1, 3, 2))).isEqualTo(3);
        assertThat(max(List.of("aa", "bb", "cc"))).isEqualTo("cc");
    }

    /* item 31 (4) : unbounded wildcard type (非境界ワイルドカード型) の活用例。
     * public API において、
     * もしメソッドスコープで型変数が戻り値と引数すべてで1度しか登場しないのであれば、
     * それは非境界ワイルドカード型に置換すると良い。
     * public API という観点でみれば、非境界ワイルドカード型として何でも渡せる方がシンプルで好ましい。
     */

    // List<E> の一箇所でしか型変数が使われていない -> List<?> としたほうがpublic API としては好ましい。
    static <E> void genericsSwap(List<E> list, int i, int j) {
        list.set(i, list.set(j, list.get(i)));
    }

    // 非境界ワイルドカード型に置換した版。
    static void unboundedSwap(List<?> list, int i, int j) {
        // 非境界ワイルドカード型だとget/setが使えない。
        //list.set(i, list.set(j, list.get(i)));
        // -> generics 版をヘルパーメソッドとして呼び出し、そちらに委譲する。
        genericsSwap(list, i, j);
    }

    @Test
    public void testItem31SwapDemo() {
        final ArrayList<Integer> integers = new ArrayList<>(List.of(1, 2, 3));
        final ArrayList<Double> doubles = new ArrayList<>(List.of(1.0, 2.0, 3.0));
        genericsSwap(integers, 0, 2);
        assertThat(integers.get(0)).isEqualTo(3);
        assertThat(integers.get(1)).isEqualTo(2);
        assertThat(integers.get(2)).isEqualTo(1);
        genericsSwap(doubles, 0, 2);
        assertThat(doubles.get(0)).isEqualTo(3.0);
        assertThat(doubles.get(1)).isEqualTo(2.0);
        assertThat(doubles.get(2)).isEqualTo(1.0);

        unboundedSwap(integers, 0, 2);
        assertThat(integers.get(0)).isEqualTo(1);
        assertThat(integers.get(1)).isEqualTo(2);
        assertThat(integers.get(2)).isEqualTo(3);
        unboundedSwap(doubles, 0, 2);
        assertThat(doubles.get(0)).isEqualTo(1.0);
        assertThat(doubles.get(1)).isEqualTo(2.0);
        assertThat(doubles.get(2)).isEqualTo(3.0);
    }

    /* item 32 : 可変長引数でジェネリクス使うのは慎重になること。
     * 
     * 可変長引数は配列として扱われる。
     * そこにジェネリクスが入ることで、heap pollution につながる危険性がある。
     * 
     * このトピックスについては reifiable type (具象化可能型) と、
     * ジェネリクス型が主に該当する non-reifiable type (具象化不可能型) の違いに注意する。
     * 
     * 参考資料:
     * - 非具象化可能仮パラメータを可変長引数メソッドに使用する場合のコンパイラの警告の改善
     *   https://docs.oracle.com/javase/jp/8/docs/technotes/guides/language/non-reifiable-varargs.html
     * - (他 本 package の README.md 参照)
     */

    /* 可変長引数の型にジェネリクス型を使う、危険な例。
     * コンパイラは可変長引数を配列に変換する。
     * 一方で Java ではジェネリクスの配列作成は許可されていない。
     * (via : イレイジャによる型消去 + item 28 の例参照)
     * -> コンパイラはジェネリクス型を使った可変長引数を Object[] に変換する。
     * 
     * 可変長引数について "unchecked warning" (Potential heap pollution via varargs parameter stringLists)
     * が生成されるため、一旦 suppress しておく。 
     */
    static void dangerousVarargsDemo(@SuppressWarnings("unchecked") List<String>... stringLists) {
        // (1) この時点で stringLists は内部的には Object[] として扱われる。
        // (2) これは普通の Integer の List
        List<Integer> intList = List.of(42);
        // Object型の配列であれば、子クラスの配列を参照できる。
        Object[] objects = stringLists;
        // (4) Object型なので、なんでも代入できてしまう。
        objects[0] = intList; // Heap pollution
        // (5) そうなると (3) で参照していた配列を更新してしまい、実行時に ClassCastException が発生する。
        @SuppressWarnings("unused")
        String s = stringLists[0].get(0); // ClassCastException
        // -> 一般的に、ジェネリクス型の可変長引数に値を保存するのは危険な操作で非推奨。
    }

    // 続いて、コンパイルエラーにならず一見型安全となるように見えるけど実行時に ClassCastException が発生する例。
    static <T> T[] dangerousToArray(@SuppressWarnings("unchecked") T... args) {
        return args;
    }

    @SuppressWarnings("unchecked")
    static <T> T[] pickTwo(T a, T b, T c) {
        switch (ThreadLocalRandom.current().nextInt(3)) {
        case 0:
            return dangerousToArray(a, b);
        case 1:
            return dangerousToArray(a, c);
        case 2:
            return dangerousToArray(b, c);
        default:
            throw new AssertionError(); // Can't get here
        }
    }

    @Test
    public void testItem32DangerousToArrayDemo() {
        assertThrows(ClassCastException.class, () -> {
            @SuppressWarnings("unused")
            String[] attributes = pickTwo("Good", "Fast", "Cheap");
        });
        /* コンパイルエラーが無いのに、なぜ ClassCastException が発生するか？
         * -> dangerousToArray(T... args) が呼ばれた段階で、型変数がイレイジャによって削除されているため
         * コンパイラはObject[]に変換している。そのため実際の戻り値は Object[] 型になっており、
         * Javaでは子に親クラスのインスタンスを参照させることはできず、実行時に ClassCastException が発生してしまう。
         */
    }

    /* (ジェネリクス型 or 型変数の)可変長引数について、
     * 何も保存せず、さらに別のメソッドに配列として渡すようなことをしなければ、
     * @SafeVarargs によりwarningを抑制できる。
     */
    @SafeVarargs
    static <T> List<T> safeFlatten(List<? extends T>... lists) {
        List<T> result = new ArrayList<>();
        for (List<? extends T> list : lists) {
            result.addAll(list);
        }
        /* 境界ワイルドカード型の可変長引数について、何も保存せず(iterateしてるだけ)、
         * 外部にも渡していないため安全とマークできる。
         */
        return result;
    }

    /* 可変長引数を使わず List<E> でラップすれば、@SafeVarargsは不要。
     * ただし List の処理で array 操作よりは多少のオーバーヘッドが見込まれる。
     */
    static <T> List<T> safeFlatten2(List<List<? extends T>> lists) {
        List<T> result = new ArrayList<>();
        for (List<? extends T> list : lists) {
            result.addAll(list);
        }
        return result;
    }

    @Test
    public void testItem32SafeFlattenDemo() {
        final List<String> list1 = List.of("aa", "bb");
        final List<String> list2 = List.of("cc", "dd");
        assertThat(safeFlatten(list1, list2)).isEqualTo(List.of("aa", "bb", "cc", "dd"));
        assertThat(safeFlatten2(List.of(list1, list2))).isEqualTo(List.of("aa", "bb", "cc", "dd"));
    }

    // -> pickTwo() についても、安全が保証されている List.of() を使えばtype safeな実装にできる。
    static <T> List<T> pickTwo2(T a, T b, T c) {
        switch (ThreadLocalRandom.current().nextInt(3)) {
        case 0:
            return List.of(a, b);
        case 1:
            return List.of(a, c);
        case 2:
            return List.of(b, c);
        default:
            throw new AssertionError(); // Can't get here
        }
    }

    /* item 33 : タイプセーフな型混在コンテナの実装例
     */

    static class TypeSafeHeterogeneousContainerDemo {
        /* 非境界ワイルドカード型を使っているので、container は put/get できないんじゃ？
         * -> 非境界ワイルドカード型を使っているのはあくまでもキーとなる Class<?> 部分なので、
         * Map 全体には影響せず、put/get が可能となっている。
         * これにより Class<String> や Class<Integer> など型をキーとしたコンテナを実現できる。
         * 
         * もう一つのポイントは value を Object 型としていることで、これによりどんな値でも格納可能としている。
         * 逆に言えば、この場面で「Class<?> に対応する型が入る」型、というような表現が2020-04時点のJavaではできない。
         */
        private Map<Class<?>, Object> container = new HashMap<>();

        public <T> void put(Class<T> type, T instance) {
            container.put(Objects.requireNonNull(type), instance);
        }

        public <T> T get(Class<T> type) {
            final Object o = container.get(type);
            /* Class#cast() は動的なキャストを実現する。
             * シグネチャは T cast(Object obj) となっていて、内部では型チェックも行われる。
             * (よってこの時点で o が type のインスタンスでなければ、ClassCastException が発生する。
             */
            return type.cast(o);
        }

        // put()の実装をよりタイプセーフにするため、put()の時点で Class#cast() を使う例。
        public <T> void putMoreSafe(Class<T> type, T instance) {
            container.put(Objects.requireNonNull(type), type.cast(instance));
        }
    }

    @Test
    public void testItem33TypeSafeHeterogeneousContainerDemo() {
        // クラス名長いので2020-04時点ではjavasnack全体がjava11前提なのを良いことに var 使う。
        final var container = new TypeSafeHeterogeneousContainerDemo();
        container.put(String.class, "Java");
        container.put(Integer.class, 0xcafebabe);
        container.put(Class.class, TypeSafeHeterogeneousContainerDemo.class);

        assertThat(container.get(String.class)).isEqualTo("Java");
        assertThat(container.get(Integer.class)).isEqualTo(0xcafebabe);
        assertThat(container.get(Class.class)).isEqualTo(TypeSafeHeterogeneousContainerDemo.class);

        // put() では型がノーチェックなので、ジェネリクス型のイレイジャを考慮するとこんなことも・・・
        final HashSet<String> set1 = new HashSet<>(Set.of("aa", "bb"));
        container.put(HashSet.class, set1);
        @SuppressWarnings("unchecked")
        final HashSet<Integer> set2 = container.get(HashSet.class);
        assertThrows(ClassCastException.class, () -> {
            for (int i : set2) {
                System.out.println(i);
            }
        });
        // このパターンはputMoreSafe()でも防げない。
        container.putMoreSafe(HashSet.class, set1);
        @SuppressWarnings("unchecked")
        final HashSet<String> set3 = container.get(HashSet.class);
        assertThat(set3).hasSize(2);
        assertThat(set3.contains("aa")).isTrue();
        assertThat(set3.contains("bb")).isTrue();
    }
}
