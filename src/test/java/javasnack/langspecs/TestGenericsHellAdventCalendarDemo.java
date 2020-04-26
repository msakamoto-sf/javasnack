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

package javasnack.langspecs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.util.Optional;

public class TestGenericsHellAdventCalendarDemo {
    /* 以下のブログ記事を参考にしたユースケースのデモと練習。
     * - Java Generics Hell Advent Calendar 2017 - Adventar
     *   https://adventar.org/calendars/2751
     * 
     * Java ジェネリクス全体における参考資料:
     * 
     * - Javaジェネリクス再入門 - プログラマーの脳みそ
     *   https://nagise.hatenablog.jp/entry/20101105/1288938415
     * 
     * - JJUG CCC 2013 Fall でジェネリクスのセッションやりました - プログラマーの脳みそ
     *   https://nagise.hatenablog.jp/entry/20131111/1384168238
     * 
     * - Java ジェネリクスのポイント - Qiita
     *   https://qiita.com/pebblip/items/1206f866980f2ff91e77
     * 
     * - Java総称型メモ(Hishidama's Java Generics Memo)
     *   https://www.ne.jp/asahi/hishidama/home/tech/java/generics.html
     * 
     * - Lesson: Generics (Updated) (The Java™ Tutorials > Learning the Java Language)
     *   https://docs.oracle.com/javase/tutorial/java/generics/index.html
     *   
     * - AngelikaLanger.com - Java Generics FAQs - Frequently Asked Questions - Angelika Langer Training/Consulting
     *   http://www.angelikalanger.com/GenericsFAQ/JavaGenericsFAQ.html
     *   
     * - generics - Difference between <? super T> and <? extends T> in Java - Stack Overflow
     *   https://stackoverflow.com/questions/4343202/difference-between-super-t-and-extends-t-in-java
     *   (境界ワイルドカード型のupper/lower boundedの使い分けについてはPECS原則というのがあり、
     *    それについて良くまとめられている。)
     */

    static class SomeParent {
        final String field1;

        SomeParent(final String field1) {
            this.field1 = field1;
        }

        @Override
        public String toString() {
            return "[field1=" + this.field1 + "]";
        }
    }

    static class SomeChild extends SomeParent {
        final String field2;

        SomeChild(final String field1, final String field2) {
            super(field1);
            this.field2 = field2;
        }

        @Override
        public String toString() {
            return "[field1=" + this.field1 + ", field2=" + this.field2 + "]";
        }
    }

    static class SomeGrandChild extends SomeChild {
        final String field3;

        SomeGrandChild(final String field1, final String field2, final String field3) {
            super(field1, field2);
            this.field3 = field3;
        }

        @Override
        public String toString() {
            return "[field1=" + this.field1 + ", field2=" + this.field2 + ", field3=" + this.field3 + "]";
        }
    }

    @Test
    public void testBasicCovarianceForReferenceType() {
        /* DAY-4, 配列と変性 : https://nagise.hatenablog.jp/entry/20171204/1512395207
         */
        SomeChild c0 = new SomeChild("bbb", "ccc");
        SomeGrandChild gc0 = new SomeGrandChild("ddd", "eee", "fff");

        /* 子クラスの参照を代入できる : 共変, covariance
         * やってることは子クラスを親クラスに変換する「アップキャスト」
         * (upcast / Widening Reference Conversion)
         */
        SomeParent p1 = c0;
        SomeParent p2 = gc0;
        assertThat(p1.field1).isEqualTo("bbb");
        assertThat(p2.field1).isEqualTo("ddd");
        // ちなみにキャスト後も instanceof は子クラスでtrueになる。
        assertThat(p1 instanceof SomeParent).isTrue();
        assertThat(p1 instanceof SomeChild).isTrue();
        assertThat(p1 instanceof SomeGrandChild).isFalse(); // 元がSomeChildなのでこれはfalse.
        assertThat(p2 instanceof SomeParent).isTrue();
        assertThat(p2 instanceof SomeChild).isTrue();
        assertThat(p2 instanceof SomeGrandChild).isTrue();

        /* 親クラスの参照は代入できない。(代入できる場合は 反変, contravariance となる)
         * ここでは親クラスを子クラスに変換する「ダウンキャスト」はコンパイルエラーになる。
         * (down cast / Narrowing Reference Conversion)
         */
        // SomeChild c1 = new SomeParent("aaa");

        // 共変は参照型の配列にも適用される。
        SomeChild[] children = { new SomeChild("aaa", "bbb"), new SomeChild("ccc", "ddd") };
        SomeParent[] parents = children;
        assertThat(parents).hasSize(2);
        assertThat(parents[0].field1).isEqualTo("aaa");
        assertThat(parents[1].field1).isEqualTo("ccc");
        // 配列でも反変はコンパイルエラーになり使えない。
        //SomeChild[] children2 = parents;
    }

    @Test
    public void testPitFallInArrayOfReferenceType() {
        /* DAY-4, 配列と変性 : https://nagise.hatenablog.jp/entry/20171204/1512395207
         */
        String[] stringArray = { "one", "two" };
        Object[] objectArray = stringArray; // 代入できる
        assertThrows(ArrayStoreException.class, () -> {
            // compileは通るが、ランタイム例外が発生する。
            objectArray[0] = Integer.valueOf(1);
        });

        /* 「ジェネリクスの配列」のようなより細かいコーナーケースについては以下参照。
         * - ジェネリクスと配列 - プログラマーの脳みそ
         *   https://nagise.hatenablog.jp/entry/20180214/1518569217
         */
    }

    @Test
    public void testInvariantForParameterizedType() {
        /* DAY-5, パラメタライズドタイプ : https://nagise.hatenablog.jp/entry/20171205/1512480154
         * 
         * 実型パラメータ(actual type parameter) に親子関係がありそれ単体なら共変が可能でも、
         * パラメータ化された型(parameterized type)では共変は成立しない。
         * = 子クラスの実型パラメータでパラメータ化された型(List<SomeChild>)を、
         * 親クラスの実型パラメータでパラメータ化された型(List<SomeParent>)に参照代入できない。
         * = 非変, invariant(or nonvariant)
         */
        @SuppressWarnings("unused")
        List<SomeChild> children = Collections.emptyList();
        //List<SomeParent> parent = children;
        /* もちろん、親クラスの実型パラメータでパラメータ化された型(List<SomeChild>)を、
         * 子クラスの実型パラメータでパラメータ化された型(List<SomeGrandChild>)に参照代入もできない。 
         */
        //List<SomeGrandChild> grandchildren = children;

    }

    @Test
    public void testCovarianceForRawType() {
        /* 実型パラメータが等しい場合であれば
         * パラメータ化された型の原型(raw type)に親子関係があるときに、共変が可能となる。
         */
        ArrayList<SomeChild> arrayListOfChild = new ArrayList<>();
        arrayListOfChild.add(new SomeChild("aaa", "bbb"));
        arrayListOfChild.add(new SomeGrandChild("ccc", "ddd", "eee"));
        // 原型(raw type) において ArrayList is a List となり upcast 可能。
        List<SomeChild> children = arrayListOfChild;
        assertThat(children).hasSize(2);
        assertThat(children.get(0).field1).isEqualTo("aaa");
        assertThat(children.get(0).field2).isEqualTo("bbb");
        assertThat(children.get(1).field1).isEqualTo("ccc");
        assertThat(children.get(1).field2).isEqualTo("ddd");
    }

    /* DAY-6, ジェネリクスの構文 : https://nagise.hatenablog.jp/entry/20171206/1512567630
     * DAY-7, メソッドスコープのジェネリクス : https://nagise.hatenablog.jp/entry/20171207/1512654575
     * DAY-8, インスタンススコープのジェネリクス : https://nagise.hatenablog.jp/entry/20171208/1512741372
     * DAY-16, 型変数のバインド : https://nagise.hatenablog.jp/entry/20171216/1513429565
     * 
     * 型変数の宣言と使用方法, バインドの例示
     */

    // メソッドスコープの型変数の例
    static class MethodScopeDemo1 {
        final int count;

        MethodScopeDemo1(final int count) {
            this.count = count;
        }

        // インスタンスメソッドでの型変数 : 先頭に angle bracket で型変数を宣言
        <T> List<T> asList(final T o) {
            final List<T> r = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                r.add(o);
            }
            return r;
        }

        // static メソッドでの型変数 : 先頭に angle bracket で型変数を宣言
        static <K, V> Map<K, V> singleMap(final K key, final V value) {
            return new HashMap<>(Map.of(key, value));
        }
    }

    // インスタンススコープの型変数の例
    static class InstanceScopeDemo1<T1, T2> {
        final T1 left;
        final T2 right;

        InstanceScopeDemo1(final T1 left, final T2 right) {
            this.left = left;
            this.right = right;
        }

        Map<T1, T2> asMap() {
            return new HashMap<>(Map.of(left, right));
        }
    }

    @Test
    public void testTypeParameterDeclarationDemo() {

        // メソッドスコープの型変数(type parameter, 仮型パラメータ)であれば型推論により省略できるデモ
        final MethodScopeDemo1 d1 = new MethodScopeDemo1(3);
        // parameterized type : パラメータ化された型 = List<String>
        final List<String> strings1 = d1.asList("hello");
        assertThat(strings1).hasSize(3);
        assertThat(strings1.get(0)).isEqualTo("hello");
        assertThat(strings1.get(1)).isEqualTo("hello");
        assertThat(strings1.get(2)).isEqualTo("hello");
        final List<SomeParent> parents1 = d1.asList(new SomeParent("world"));
        assertThat(parents1).hasSize(3);
        assertThat(parents1.get(0).field1).isEqualTo("world");
        assertThat(parents1.get(1).field1).isEqualTo("world");
        assertThat(parents1.get(2).field1).isEqualTo("world");
        final Map<String, SomeParent> map1 = MethodScopeDemo1.singleMap("aaa", new SomeParent("bbb"));
        assertThat(map1).hasSize(1);
        assertThat(map1.get("aaa").field1).isEqualTo("bbb");

        // メソッドにおけるバインドを明示するなら、こんな風に書ける。
        parents1.addAll(d1.<SomeParent>asList(new SomeParent("xx")));
        assertThat(parents1).hasSize(6);
        final List<SomeParent> parents2 = List.<SomeParent>of(new SomeParent("AA"));
        assertThat(parents2.get(0).field1).isEqualTo("AA");
        final List<SomeChild> children2 = List.<SomeChild>of(new SomeChild("BB", "CC"));
        assertThat(children2.get(0).field1).isEqualTo("BB");
        assertThat(children2.get(0).field2).isEqualTo("CC");
        final List<SomeGrandChild> grandChildren2 = List.<SomeGrandChild>of(new SomeGrandChild("DD", "EE", "FF"));
        assertThat(grandChildren2.get(0).field1).isEqualTo("DD");
        assertThat(grandChildren2.get(0).field2).isEqualTo("EE");
        assertThat(grandChildren2.get(0).field3).isEqualTo("FF");

        // 分かりづらいがバインド部分とnewしてる型が共変なのでコンパイル成功。
        List<SomeParent> parents3 = List.<SomeParent>of(new SomeGrandChild("xx1", "yy1", "zz1"));
        assertThat(parents3.get(0).field1).isEqualTo("xx1");
        List<SomeChild> children3 = List.<SomeChild>of(new SomeGrandChild("xx2", "yy2", "zz2"));
        assertThat(children3.get(0).field1).isEqualTo("xx2");
        assertThat(children3.get(0).field2).isEqualTo("yy2");

        // new 演算子におけるバインド例 : Java7以降なら<>で省略できる。
        final InstanceScopeDemo1<String, SomeParent> d2 = new InstanceScopeDemo1<>("ccc", new SomeParent("ddd"));
        final Map<String, SomeParent> map2 = d2.asMap();
        assertThat(map2).hasSize(1);
        assertThat(map2.get("ccc").field1).isEqualTo("ddd");
        // 明示するならこう。
        @SuppressWarnings("unused")
        final List<String> strings3 = new ArrayList<String>();
        @SuppressWarnings("unused")
        final Map<String, SomeParent> map3 = new HashMap<String, SomeParent>();

        // 継承によるバインド例
        class StringList extends ArrayList<String> {
            private static final long serialVersionUID = 1L;

            StringList(final Collection<? extends String> c) {
                super(c);
            }
        }

        final StringList strings4 = new StringList(List.of("xx3", "yy3"));
        strings4.add("zz3");
        assertThat(strings4).hasSize(3);
        assertThat(strings4.get(0)).isEqualTo("xx3");
        assertThat(strings4.get(1)).isEqualTo("yy3");
        assertThat(strings4.get(2)).isEqualTo("zz3");
        assertThat(strings4).isEqualTo(List.of("xx3", "yy3", "zz3"));

        class StringToParents extends HashMap<String, SomeParent> {
            private static final long serialVersionUID = 1L;

            StringToParents(final Map<? extends String, ? extends SomeParent> m) {
                super(m);
            }
        }

        final StringToParents parents4 = new StringToParents(
                Map.of("foo", new SomeParent("FOO"), "bar", new SomeParent("BAR")));
        assertThat(parents4).hasSize(2);
        assertThat(parents4.get("foo").field1).isEqualTo("FOO");
        assertThat(parents4.get("bar").field1).isEqualTo("BAR");
    }

    /* DAY-11, 型変数の境界 : https://nagise.hatenablog.jp/entry/20171211/1512993295
     */

    // 型変数の宣言においては境界型パラメータ (bounded type parameter) を使える。
    static class BoundedTypeParameter1<T extends SomeChild> {
        final T child;

        BoundedTypeParameter1(final T child) {
            this.child = child;
        }
    }

    static class SomeSerializable implements Serializable {
        private static final long serialVersionUID = 1L;
    }

    static class BoundedTypeParameter2<T extends Serializable> {
        final T ser;

        BoundedTypeParameter2(final T ser) {
            this.ser = ser;
        }
    }

    static class CloneAndAutoCloseable implements Cloneable, AutoCloseable {
        String name;

        CloneAndAutoCloseable(final String name) {
            this.name = name;
        }

        @Override
        public void close() throws Exception {
            this.name = "";
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return new CloneAndAutoCloseable(name);
        }
    }

    static class BoundedTypeParameter3<T extends Cloneable & AutoCloseable> {
        final T poly;

        BoundedTypeParameter3(final T poly) {
            this.poly = poly;
        }
    }

    @Test
    public void testBoundedTypeParameterDemo() {
        // 境界型パラメータの境界型を使ってバインドできる。
        final BoundedTypeParameter1<SomeChild> o1 = new BoundedTypeParameter1<>(new SomeChild("aa", "bb"));
        assertThat(o1.child.field1).isEqualTo("aa");
        assertThat(o1.child.field2).isEqualTo("bb");

        // 境界型パラメータの境界型の派生もバインドできる。
        final BoundedTypeParameter1<SomeGrandChild> o2 = new BoundedTypeParameter1<>(
                new SomeGrandChild("cc", "dd", "ee"));
        assertThat(o2.child.field1).isEqualTo("cc");
        assertThat(o2.child.field2).isEqualTo("dd");
        assertThat(o2.child.field3).isEqualTo("ee");

        // 境界型と共変関係にない型(境界型の親、全く別の型)ではバインドできない。
        //new BoundedTypeParameter1<SomeParent>(new SomeParent("ff"));
        //new BoundedTypeParameter1<String>("gg");

        // 境界型としてinterfaceを使うこともできる。
        final BoundedTypeParameter2<Serializable> o3 = new BoundedTypeParameter2<>(new Serializable() {
            private static final long serialVersionUID = 1L;
        });
        assertThat(o3.ser instanceof Serializable).isTrue();

        final BoundedTypeParameter2<SomeSerializable> o4 = new BoundedTypeParameter2<>(new SomeSerializable());
        assertThat(o4.ser instanceof Serializable).isTrue();
        assertThat(o4.ser instanceof SomeSerializable).isTrue();

        // 複数のinterfaceをすべてimplementした型のみに制限することもできる。
        // -> 境界型として implement してほしい interface を & で複数指定する。
        final BoundedTypeParameter3<CloneAndAutoCloseable> o5 = new BoundedTypeParameter3<>(
                new CloneAndAutoCloseable("xx"));
        assertThat(o5.poly.name).isEqualTo("xx");
        assertThat(o5.poly instanceof Cloneable).isTrue();
        assertThat(o5.poly instanceof AutoCloseable).isTrue();
        /*
        new BoundedTypeParameter3<Cloneable>(new Cloneable() {
        });
        new BoundedTypeParameter3<AutoCloseable>(new AutoCloseable() {
            @Override
            public void close() throws Exception {
                // stub
            }
        });
        */

        // 使わない型変数を潰したいときにVoid型を使うことがある。
        new HashMap<String, Void>();
        // 潰したい型変数が境界型の場合、Void型と共変関係になければコンパイルエラーとなり、この技法は使えない。
        //new BoundedTypeParameter1<Void>(null);

        // 再帰ジェネリクスについては省略
    }

    @Test
    public void testCovarianceUpperBoundedWildcardDemo() {
        // DAY-13, 共変ワイルドカード : https://nagise.hatenablog.jp/entry/20171213/1513172356
        final List<SomeParent> parents = List.of(new SomeParent("aa"));
        final List<SomeChild> children = List.of(new SomeChild("bb", "cc"));
        final List<SomeGrandChild> grandChildren = List.of(new SomeGrandChild("dd", "ee", "ff"));
        // ジェネリクスでは実型パラメータ(actual type parameter)については非変となり、以下はコンパイルエラー。
        //parents = children;
        //children = parents;

        // 境界ワイルドカード型(bounded wildcard type or 共変ワイルドカード)を使うと共変関係があれば代入可能となる。
        // または後述の下限付きと比較して 上限付き境界ワイルドカード型 (upper bounded wildcard type) とも。
        List<? extends SomeParent> parents2 = parents;
        assertThat(parents2.get(0).field1).isEqualTo("aa");
        parents2 = children;
        assertThat(parents2.get(0).field1).isEqualTo("bb");
        parents2 = grandChildren;
        assertThat(parents2.get(0).field1).isEqualTo("dd");

        List<? extends SomeChild> children2 = grandChildren;
        //assertThat(children2.get(0).field1).isEqualTo("dd"); // なぜかこれがコンパイルエラーになる。
        assertThat(children2.get(0).field2).isEqualTo("ee");

        /* 境界ワイルドカード型をリスコフの置換原則から考えてみる。
         * まずシンプルなジェネリック型Foo<T>を考える。
         * 
         * class Foo<T> {
         *   T value;
         *   Foo(T value) { this.value = value; }
         *   T get() { return this.value; }
         *   void set(T value) { this.value = value; }
         * }
         * 
         * Foo<SomeParent> fooOfParent = new Foo<>(new SomeParent(...));
         * -> SomeParent を get/set できる。
         * 
         * Foo<SomeChild> fooOfChild = new Foo<>(new SomeChild(...));
         * -> SomeChild を get/set できる。
         * 
         * ここで fooOfParent = fooOfChild ができるとはどういうことか？
         * リスコフの置換原則に従えば、もしこれができたとしたら、
         * fooOfChild では fooOfParent の機能が使える = SomeParnet を get/set できる必要がある。
         * 
         * [a] getについては、SomeChildを返す = 共変関係が成立して SomeParent を返していることになる。
         * (= SomeParnet parent = fooOfChild.get() が可能)
         * 
         * [b] setについては、SomeChildを引数にとるがこれは SomeParent に代替できない。
         * なぜなら SomeChild を引数にとっている以上、SomeChild の機能を有さない SomeParent を受け取ることができない。
         * (= fooOfChild.set(new SomeParent(...)) が不可能)
         * 
         * つまり、リスコフの置換原則の観点からは fooOfParent = fooOfChild は不可能となる。
         * 
         * そこで導入された制限が「仮型パラメータを引数にとるメソッドは使用できない」であり、
         * これにより [b] の制約をそもそも考えなくて良いことになった。
         * 
         * fooOfParent/fooOfChild に話を戻せば、
         * Foo<SomeParnet> newParents = fooOfChild
         * としたとき、newParents.set() メソッドを呼び出せないこととすればよい。
         * 
         * これを実現するための実型パラメータ(actual type parameter)の表記が
         * capture であり、境界ワイルドカード型の "? extends SomeParent" になる。
         * Foo<? extends SomeParent> newParents = fooOfChild; 
         * 
         * capture "? extends A" は以下の性質を満たす。
         * [C1] A型は "? extends A" に変換できない。
         * -> newParents.set(new SomeParent(...)); // NG
         * [C2] "? extends A" はA型に暗黙に安全に変換できる。
         * -> SomeParent x = newParents.get(); // OK
         * 
         * 実際、以下のように仮型パラメータを引数にとっているメソッドを呼ぶとコンパイルエラーになる。
         */
        /*
        parents2.add(new SomeParent("gg"));
        parents2.add(new SomeChild("hh", "ii"));
        parents2.add(new SomeGrandChild("jj", "kk"));
        children2.add(new SomeParent("gg"));
        children2.add(new SomeChild("hh", "ii"));
        children2.add(new SomeGrandChild("jj", "kk"));
        */

        // 戻り値については、境界ワイルドカードと同様に扱うことができる。
        SomeParent x1 = parents2.get(0);
        assertThat(x1.field1).isEqualTo("dd");
        //SomeChild x2 = parents2.get(0);
        //SomeGrandChild x3 = parents2.get(0);

        SomeParent y1 = children2.get(0);
        assertThat(y1.field1).isEqualTo("dd");
        SomeChild y2 = children2.get(0);
        assertThat(y2.field1).isEqualTo("dd");
        assertThat(y2.field2).isEqualTo("ee");
        //SomeGrandChild y3 = children2.get(0);

        /* 境界ワイルドカード型をうまく使うと、 void xxx(List<? extends SomeParent> parents)
         * のようなメソッドにすることで List<SomeChild>, List<SomeGrandChild>, ... 
         * も引数に渡せるようになる。
         * (ただしメソッドの内部で add() など仮型パラメータを引数に取るメソッドは使えなくなる。
         *  immutableに扱うことしかできなくなるが、関数型プログラミング風に実装するのであれば
         *  大きな制約にはならないかもしれない)
         */
    }

    @Test
    public void testNullCastingDemo() {
        /* from:
         * DAY-11, 型変数の境界 : https://nagise.hatenablog.jp/entry/20171211/1512993295
         * DAY-13, 共変ワイルドカード : https://nagise.hatenablog.jp/entry/20171213/1513172356
         */

        // nullは配列に混ぜることができる。
        String[] stringarr1 = { "xxx", "yyy", null };
        assertThat(stringarr1).hasSize(3);
        assertThat(stringarr1[0]).isEqualTo("xxx");
        assertThat(stringarr1[1]).isEqualTo("yyy");
        assertThat(stringarr1[2]).isNull();

        // nullは他の型の値として使うことができる。
        final List<String> strings1 = new ArrayList<>();
        strings1.add("hello");
        strings1.add(null);
        assertThat(strings1).hasSize(2);
        assertThat(strings1.get(0)).isEqualTo("hello");
        assertThat(strings1.get(1)).isNull();

        final List<? extends SomeChild> strings2 = new ArrayList<>();
        // 境界ワイルドカード(capture)を使った場合は以下はコンパイルエラー。
        //strings2.add(new SomeChild("aaa", "bbb"));
        // nullはcapture部分にキャスト可能なので以下はOK.
        strings2.add(null);
        assertThat(strings2).hasSize(1);
        assertThat(strings2.get(0)).isNull();
    }

    @Test
    public void testContraVarianceLowerBoundedWildcardDemo() {
        // DAY-14, 反変ワイルドカード : https://nagise.hatenablog.jp/entry/20171214/1513260215
        final List<SomeParent> parents = new ArrayList<>(List.of(new SomeParent("aa")));
        final List<SomeChild> children = new ArrayList<>(List.of(new SomeChild("bb", "cc")));
        // ジェネリクスでは実型パラメータ(actual type parameter)については非変となり、以下はコンパイルエラー。
        //parents = children;
        //children = parents;

        // 下限付き境界ワイルドカード型(lower bounded wildcard type or 反変ワイルドカード)を使うと
        // 実型パラメータで子の型に対して親の型を代入可能となる(= 反変性)。
        List<? super SomeChild> children2 = parents;

        // super で指定した型と同じ場合も代入できる。
        children2 = children;

        // super から派生した型はコンパイルエラーとなる。
        //children2 = List.<SomeGrandChild>of(new SomeGrandChild("dd", "ee", "ff"));

        // upper-bounded と異なり、lower-bounded ではcaptureが引数に入ってくるメソッドを呼び出せる。
        // パラメータ化された型が反変性を持つとはいえ、capture部分については共変性が維持される。
        children2 = parents;
        children2.add(new SomeChild("xx", "yy"));
        children2.add(new SomeGrandChild("xx", "yy", "zz"));
        assertThat(children2).hasSize(3);

        // これは "? super SomeChild" に SomeParent 参照を代入しようとしてるのでコンパイルエラー。
        // children2.add(new SomeParent("xx"));

        // 上限付きとは逆に、下限付きではメソッド戻り値のcaptureが Object 型扱いとなる。
        Object o0 = children2.get(0);
        assertThat(o0 instanceof SomeParent).isTrue();
        assertThat(o0 instanceof SomeChild).isFalse();

        // 以下はコンパイルエラーとなる。
        //SomeParent parent0 = children2.get(0);
        //SomeChild child0 = children2.get(0);
        //SomeGrandChild grandChild0 = children2.get(0);

        // キャストすれば一応代入できる。
        SomeParent parent0 = (SomeParent) children2.get(0);
        assertThat(parent0.field1).isEqualTo("aa");
        parent0 = (SomeParent) children2.get(1);
        assertThat(parent0.field1).isEqualTo("xx");
        parent0 = (SomeParent) children2.get(2);
        assertThat(parent0.field1).isEqualTo("xx");

        // upper/lower bounded wildcard の使い分けを、以下にPECS原則のデモとして示す。
    }

    /* PECS原則(Producer - extends, Consumer - super) のデモ
     * see:
     * https://stackoverflow.com/questions/4343202/difference-between-super-t-and-extends-t-in-java
     * 
     * NOTE: PECS原則が生きてくるのは、APIメソッドの引数でジェネリクス型を扱うとき。
     * その場面で、メソッドが引数に対して read操作(= producerとして扱う) / write操作(= consumerとして扱う)
     * のどちらの操作をするかによって、extends / super それぞれの境界ワイルドカード型が活用できる。
     * 
     * 境界型ワイルドカード型は、代入時のdestination側で使うため、メソッドの引数で使うシーンがメインとなる:
     * foo(List<? extends Xxx) x, ...) // これがメジャーなユースケース
     * 
     * 代入時のsource側では使わないので、メソッドの戻り値の型に使う例は見当たらない:
     * List<? extends Xxx) bar(...) // この例は見たことが無い。
     */

    static class NonPecsPrincipalDemo<T> {
        final T item;

        NonPecsPrincipalDemo(final T item) {
            this.item = item;
        }

        List<String> mapToStringsThenAdd(final List<T> items) {
            final List<String> strings = new ArrayList<>(items.size() + 1);
            for (T check : items) {
                strings.add(check.toString());
            }
            strings.add(item.toString());
            return strings;
        }

        void add(List<T> items) {
            items.add(item);
        }
    }

    static class PecsPrincipalDemo<T> {
        final T item;

        PecsPrincipalDemo(final T item) {
            this.item = item;
        }

        List<String> mapToStringsThenAdd(final List<? extends T> items) {
            final List<String> strings = new ArrayList<>(items.size() + 1);
            for (T check : items) {
                strings.add(check.toString());
            }
            strings.add(item.toString());
            return strings;
        }

        void add(List<? super T> items) {
            items.add(item);
        }
    }

    @Test
    public void testPecsPrincipalDemo() {
        // [1] 境界付きワイルドカード型を使っていない例。
        final NonPecsPrincipalDemo<SomeChild> o1 = new NonPecsPrincipalDemo<>(new SomeChild("aa1", "bb1"));

        // 実型パラメータが子となるような List<SomeParent> を引数に渡せない。
        // (SomeChild c = new SomeGrandChild(...) はできるのに!)
        //o1.mapToStringsThenAdd(new ArrayList<SomeGrandChild>(List.of(new SomeGrandChild("cc1", "dd1", "ee1"))));

        // 他の処理で生成された、実型パラメータが親となるような List<SomeParent> を引数に渡せない。
        //o1.add(new ArrayList<SomeParent>(List.of(new SomeParent("xx1"))));

        // -> 受け渡しできるのは List<SomeChild> のみに制限され、APIとしては使い勝手が窮屈になる。
        final List<SomeChild> children1 = new ArrayList<>(List.of(new SomeChild("yy1", "zz1")));
        assertThat(o1.mapToStringsThenAdd(children1))
                .isEqualTo(List.of("[field1=yy1, field2=zz1]", "[field1=aa1, field2=bb1]"));
        o1.add(children1);
        assertThat(children1).hasSize(2);
        assertThat(children1.get(0).field1).isEqualTo("yy1");
        assertThat(children1.get(0).field2).isEqualTo("zz1");
        assertThat(children1.get(1).field1).isEqualTo("aa1");
        assertThat(children1.get(1).field2).isEqualTo("bb1");

        // [2] 境界付きワイルドカード型を活用した例。
        final PecsPrincipalDemo<SomeChild> o2 = new PecsPrincipalDemo<>(new SomeChild("aa2", "bb2"));
        /* メソッド内部でreadアクセスしか使わない場合は、? extends T を使うことで子クラスを渡せるようになる。
         * -> そのメソッドは引数として T 以降の型なら何でも対応していることを意味する。
         */
        final List<SomeGrandChild> grandChildren2 = new ArrayList<>(List.of(new SomeGrandChild("cc2", "dd2", "ee2")));
        assertThat(o2.mapToStringsThenAdd(grandChildren2))
                .isEqualTo(List.of("[field1=cc2, field2=dd2, field3=ee2]", "[field1=aa2, field2=bb2]"));

        /* メソッド内部でwriteアクセスしか使わない場合は、? super T を使うことで親クラスも渡せるようになる。
         * -> そのメソッドは引数として Object - T までなら何でも対応していることを意味する。
         */
        final List<SomeParent> parents2 = new ArrayList<>(List.of(new SomeParent("xx2")));
        o2.add(parents2);
        assertThat(parents2).hasSize(2);
        assertThat(parents2.get(0).field1).isEqualTo("xx2");
        assertThat(parents2.get(1).field1).isEqualTo("aa2");
    }

    @Test
    public void testUnboundedWildcardDemo() {
        // DAY-15, ワイルドカード落穂ひろい : https://nagise.hatenablog.jp/entry/20171215/1513333070

        // 非境界ワイルドカード型 (unbounded wildcard type) -> 何でも代入できる。
        List<?> anylist = new ArrayList<String>(List.of("aa", "bb"));
        anylist = new ArrayList<Integer>(List.of(Integer.valueOf(0), Integer.valueOf(1)));
        assertThat(anylist).hasSize(2);

        // ただし取り出すのはObject型になる。 (<? super T> の特性)
        Object o1 = anylist.get(0);
        assertThat(o1 instanceof Integer).isTrue();

        // キャストすれば一応取り出せる。
        Integer i1 = (Integer) anylist.get(0);
        assertThat(i1).isEqualTo(0);
        i1 = (Integer) anylist.get(1);
        assertThat(i1).isEqualTo(1);

        // メソッド引数の場合は null以外代入できなくなる。(<? extends T> の特性)
        //anylist.add("xx");
        //anylist.add(Integer.valueOf(2));
        anylist.add(null);
        assertThat(anylist).hasSize(3);
        assertThat(anylist.get(2)).isNull();
    }

    /* DAY-18, ジェネリックな例外 : https://nagise.hatenablog.jp/entry/20171218/1513595260
     * 
     * に行く前に、そもそも interface 側のthrowを、それを継承した interface や実装クラス側でどう変更できるか
     * いくつかのパターンを試してみる。
     * 
     * -> implement 側で例外をinterface側throwsの派生クラスに絞り込める。
     * interface を継承した場合も、継承先の override で throws を派生クラスに絞り込める。
     * 
     * DAY-18の記事では、AutoCloseableを実装した ByteArrayInputStream を例にしている。
     * ところがこれが、実際は
     * AutoCloseable (throws Exception) -> Closeable (throws IOException) と絞り込みがあり、
     * ByteArrayInputStream は Closeable もimplementsしていることから close() の throws はIOExceptionとなっている。
     * このあたりが記事の方では少々読み取りづらいため、注意が必要。
     */

    interface SomethingThrowInterface1 {
        void doSomething() throws Exception;
    }

    interface SomethingThrowInterface2 {
        void doSomething() throws IOException;
    }

    interface SomethingThrowInterface3 {
        void doSomething() throws IllegalArgumentException;
    }

    interface SomethingThrowInterface4 extends SomethingThrowInterface1 {
        // Exception -> IOException と親子関係にあるため、継承先で絞り込むのはOK.
        @Override
        void doSomething() throws IOException;
    }

    // 元が IllegalArgumentException で、親子関係に無いIOExceptionでoverrideしようとするとコンパイルエラー。
    /*
    interface SomethingThrowInterface5 extends SomethingThrowInterface3 {
        @Override
        void doSomething() throws IOException;
    }
    */

    static class SomethingThrows1 implements SomethingThrowInterface1 {
        // これは普通に実装側も throws Exception でOK.
        @Override
        public void doSomething() throws Exception {
            throw new Exception("demo1");
        }
    }

    static class SomethingThrows1b implements SomethingThrowInterface1 {
        // 実装側で、throws の例外を継承元の派生クラスで絞り込むことはOK.
        @Override
        public void doSomething() throws IOException {
            throw new IOException("demo1b");
        }
    }

    static class SomethingThrows2 implements SomethingThrowInterface2 {
        // これも普通に実装側で throws IOException でOK.
        @Override
        public void doSomething() throws IOException {
            throw new IOException("demo2");
        }
    }

    static class SomethingThrows3 implements SomethingThrowInterface1, SomethingThrowInterface2 {
        // これは throws Exception だとコンパイルエラーになる。
        @Override
        public void doSomething() throws IOException {
            throw new IOException("demo3");
        }
    }

    static class SomethingThrows4 implements SomethingThrowInterface1, SomethingThrowInterface3 {
        // これは throws Exception だとコンパイルエラーになる。 
        @Override
        public void doSomething() throws IllegalArgumentException {
            throw new IllegalArgumentException("demo4");
        }
    }

    static class SomethingThrows5 implements SomethingThrowInterface2, SomethingThrowInterface3 {
        @Override
        public void doSomething() throws IllegalArgumentException {
            throw new IllegalArgumentException("demo5");
        }
        /* 詳しい理由を追いかけきれていないが、以下はコンパイルエラーになる。
        @Override
        public void doSomething() throws IOException {
            throw new IOException("demo5");
        }
        */
    }

    static class SomethingThrows6 implements SomethingThrowInterface4 {
        // これは throws Exception だとコンパイルエラーになる。 
        @Override
        public void doSomething() throws IOException {
            throw new IOException("demo6");
        }
    }

    @Test
    public void testInterfaceMethodsThrowDeclarationDemo() {
        /* 実際に実装クラスをnewしてみて、実装クラスで受けた場合と interface で受けた場合とで、
         * throwされる例外クラスと try-catch ペアがどう変わるか主なパターンを試してみる。
         */

        final SomethingThrows1 st1 = new SomethingThrows1();
        try {
            st1.doSomething();
            fail("should not reach here");
        } catch (Exception e) {
            // interface - implement でthrowsが一致してるので素直。
            assertThat(e.getMessage()).isEqualTo("demo1");
        }

        final SomethingThrows1b st1b = new SomethingThrows1b();
        try {
            st1b.doSomething();
            fail("should not reach here");
        } catch (IOException e) {
            // interface - implement でthrowsが一致してるので素直。
            assertThat(e.getMessage()).isEqualTo("demo1b");
        }

        final SomethingThrows2 st2 = new SomethingThrows2();
        try {
            st2.doSomething();
            fail("should not reach here");
        } catch (IOException e) {
            // interface - implement でthrowsが一致してるので素直。
            assertThat(e.getMessage()).isEqualTo("demo2");
        }

        final SomethingThrows3 st3 = new SomethingThrows3();
        try {
            st3.doSomething();
            fail("should not reach here");
        } catch (IOException e) {
            // 実装クラスで受けてるので IOException がthrowされる。
            assertThat(e.getMessage()).isEqualTo("demo3");
        } catch (Exception e) {
            fail("should not reach here");
        }

        final SomethingThrowInterface1 sti1a = st3;
        try {
            sti1a.doSomething();
            fail("should not reach here");
        } catch (Exception e) {
            // interface で受けていても、具象クラスが動いてるのでそちらがthrowした例外を受ける。
            assertThat(e instanceof IOException).isTrue();
            assertThat(e.getMessage()).isEqualTo("demo3");
        }

        final SomethingThrows4 st4 = new SomethingThrows4();
        try {
            st4.doSomething();
            fail("should not reach here");
        } catch (IllegalArgumentException e) {
            // 実装クラスで受けてるので IllegalArgumentException がthrowされる。
            assertThat(e.getMessage()).isEqualTo("demo4");
        } catch (Exception e) {
            fail("should not reach here");
        }

        final SomethingThrowInterface1 sti1b = st4;
        try {
            sti1b.doSomething();
            fail("should not reach here");
        } catch (Exception e) {
            // interface で受けていても、具象クラスが動いてるのでそちらがthrowした例外を受ける。
            assertThat(e instanceof IllegalArgumentException).isTrue();
            assertThat(e.getMessage()).isEqualTo("demo4");
        }

        final SomethingThrows5 st5 = new SomethingThrows5();
        try {
            st5.doSomething();
            fail("should not reach here");
        } catch (IllegalArgumentException e) {
            // 実装クラスで受けてるので IllegalArgumentException がthrowされる。
            assertThat(e.getMessage()).isEqualTo("demo5");
        } catch (Exception e) {
            fail("should not reach here");
        }

        final SomethingThrowInterface2 sti2b = st5;
        try {
            sti2b.doSomething();
            fail("should not reach here");
        } catch (IOException e) {
            // IOException にはならない。
            fail("should not reach here");
        } catch (IllegalArgumentException e) {
            // interface 上は IOException の throw になるが、具象クラスがthrowした例外を受ける。
            assertThat(e.getMessage()).isEqualTo("demo5");
        } catch (Exception e) {
            fail("should not reach here");
        }

        final SomethingThrowInterface3 sti3b = st5;
        try {
            sti3b.doSomething();
            fail("should not reach here");
        } catch (IllegalArgumentException e) {
            // interface - implement でthrowsが一致してる。
            assertThat(e.getMessage()).isEqualTo("demo5");
        } catch (Exception e) {
            fail("should not reach here");
        }

        final SomethingThrows6 st6 = new SomethingThrows6();
        try {
            st6.doSomething();
            fail("should not reach here");
        } catch (IOException e) {
            // interface - implement でthrowsが一致してる。
            assertThat(e.getMessage()).isEqualTo("demo6");
        } catch (Exception e) {
            fail("should not reach here");
        }

        final SomethingThrowInterface1 sti1c = st6;
        try {
            sti1c.doSomething();
            fail("should not reach here");
        } catch (IOException e) {
            // 実装クラスで受けてるので IOException がthrowされる。
            assertThat(e.getMessage()).isEqualTo("demo6");
        } catch (Exception e) {
            fail("should not reach here");
        }
    }

    <E extends Exception> void methodScopedGenericsExceptionDemo() throws E {
        // stub
    }

    interface InstanceScopedGenericsExceptionInterfaceDemo<E extends Exception> {
        void doSomething() throws E;
    }

    @Test
    public void testExceptionWithGenericsDemo() {
        // DAY-18, ジェネリックな例外 : https://nagise.hatenablog.jp/entry/20171218/1513595260

        // メソッドスコープでジェネリクスな例外をバインドしてみるデモ
        try {
            this.<IOException>methodScopedGenericsExceptionDemo();
        } catch (IOException expected) {
        }
        try {
            this.<SQLException>methodScopedGenericsExceptionDemo();
        } catch (SQLException expected) {
        }
        // no need to try-catch
        this.<RuntimeException>methodScopedGenericsExceptionDemo();

        class Foo implements InstanceScopedGenericsExceptionInterfaceDemo<IOException> {
            @Override
            public void doSomething() throws IOException {
                throw new IOException("demo");
            }
        }

        final Foo foo = new Foo();
        InstanceScopedGenericsExceptionInterfaceDemo<IOException> d1 = foo;
        try {
            d1.doSomething();
            fail("should not reach here");
        } catch (IOException e) {
            // 実型パラメータに指定された例外で受けられる。
            assertThat(e.getMessage()).isEqualTo("demo");
        }
    }

    /* DAY-19, 内部クラスと型変数のスコープ : https://nagise.hatenablog.jp/entry/20171219/1513681482
     * -> あまり実際のユースケースや面白そうなデモコードが思いつかなかったためスキップ。
     */

    /* DAY-20, ブリッジメソッド : https://nagise.hatenablog.jp/entry/20171220/1513780658
     * 
     * 先に Java 5 で可能になった共変戻り値(Covariance return type)がどういうものか見てみる。
     */

    interface CovarianceRTDemoInterface {
        SomeParent getSomething();
    }

    static class CovarianceRTDemo1 implements CovarianceRTDemoInterface {
        // interface側の戻り値型の派生型であれば、戻り値を override できる。
        @Override
        public SomeChild getSomething() {
            return new SomeChild("aa", "bb");
        }
    }

    abstract static class CovarianceRTDemoAbstractClass {
        abstract SomeParent getSomething();
    }

    static class CovarianceRTDemo2 extends CovarianceRTDemoAbstractClass {
        // 継承元の戻り値型の派生型であれば、戻り値を override できる。
        @Override
        public SomeChild getSomething() {
            return new SomeChild("cc", "dd");
        }
    }

    @Test
    public void testCovarianceReturnTypeDemo() throws NoSuchMethodException, SecurityException {
        // 以下、共変戻り値を実際に取得してみるデモ。

        final CovarianceRTDemo1 d1 = new CovarianceRTDemo1();
        final SomeChild c1 = d1.getSomething();
        assertThat(c1.field1).isEqualTo("aa");

        final CovarianceRTDemoInterface i1 = d1;
        // interface 経由だと戻り値はSomeParentなので、以下はコンパイルエラー。
        //final SomeChild c2 = i1.getSomething();
        // こちらはコンパイル成功。戻り値自体は SomeChild のインスタンスになっている。
        final SomeParent p1 = i1.getSomething();
        assertThat(p1.field1).isEqualTo("aa");
        assertThat(p1 instanceof SomeChild).isTrue();

        /* getSomething というメソッドは2つあり、戻り値型が異なる。
         * Class#getMethod(name, ...) では戻り値型までは指定できずどちらが返るかわからないため、
         * getMethods()でループして探す。
         */
        Method getSomethingBridge = null;
        Method getSomethingNotBridge = null;
        for (Method m : CovarianceRTDemo1.class.getMethods()) {
            if (!"getSomething".equals(m.getName())) {
                continue;
            }
            if (m.isBridge() && m.isSynthetic()) {
                getSomethingBridge = m;
            } else {
                getSomethingNotBridge = m;
            }
        }
        // bridge & synthetic メソッドの方が、コンパイラが生成したメソッドで interface 側の戻り値。
        assertThat(getSomethingBridge.getReturnType()).isEqualTo(SomeParent.class);
        // 非 bridge & synthetic メソッドがソースコードで宣言した通りのメソッドで 実装側の戻り値。
        assertThat(getSomethingNotBridge.getReturnType()).isEqualTo(SomeChild.class);

        final CovarianceRTDemo2 d2 = new CovarianceRTDemo2();
        final SomeChild c2 = d2.getSomething();
        assertThat(c2.field1).isEqualTo("cc");

        final CovarianceRTDemoAbstractClass b2 = d2;
        // abstract class 経由だと戻り値はSomeParentなので、以下はコンパイルエラー。
        //final SomeChild c3 = b2.getSomething();
        // こちらはコンパイル成功。戻り値自体は SomeChild のインスタンスになっている。
        final SomeParent p2 = b2.getSomething();
        assertThat(p2.field1).isEqualTo("cc");
        assertThat(p2 instanceof SomeChild).isTrue();

        /* getSomething というメソッドは2つあり、戻り値型が異なる。
         * Class#getMethod(name, ...) では戻り値型までは指定できずどちらが返るかわからないため、
         * getMethods()でループして探す。
         */
        getSomethingBridge = null;
        getSomethingNotBridge = null;
        for (Method m : CovarianceRTDemo2.class.getMethods()) {
            if (!"getSomething".equals(m.getName())) {
                continue;
            }
            if (m.isBridge() && m.isSynthetic()) {
                getSomethingBridge = m;
            } else {
                getSomethingNotBridge = m;
            }
        }
        // bridge & synthetic メソッドの方が、コンパイラが生成したメソッドで interface 側の戻り値。
        assertThat(getSomethingBridge.getReturnType()).isEqualTo(SomeParent.class);
        // 非 bridge & synthetic メソッドがソースコードで宣言した通りのメソッドで 実装側の戻り値。
        assertThat(getSomethingNotBridge.getReturnType()).isEqualTo(SomeChild.class);
    }

    interface CovarianceRTDemoWithGenericsInterface<T> {
        T getSomething();
    }

    static class CovarianceRTDemoWithGenericsDemo1 implements CovarianceRTDemoWithGenericsInterface<SomeParent> {
        public SomeParent getSomething() {
            return new SomeParent("xx");
        }
    }

    @Test
    public void testCovarianceReturnTypeWithGenericsDemo() throws NoSuchMethodException, SecurityException {
        // 以下、パラメータ化された型を戻り値としたメソッドの synthetic/bridge 状態をチェックするデモ。

        final CovarianceRTDemoWithGenericsDemo1 d1 = new CovarianceRTDemoWithGenericsDemo1();
        SomeParent p1 = d1.getSomething();
        assertThat(p1.field1).isEqualTo("xx");

        final CovarianceRTDemoWithGenericsInterface<SomeParent> i1 = d1;
        // パラメータ化された型で受け取っているので、以下はコンパイル正常。
        p1 = i1.getSomething();
        assertThat(p1.field1).isEqualTo("xx");

        /* getSomething というメソッドは2つあり、戻り値型が異なる。
         * Class#getMethod(name, ...) では戻り値型までは指定できずどちらが返るかわからないため、
         * getMethods()でループして探す。
         */
        Method getSomethingBridge = null;
        Method getSomethingNotBridge = null;
        for (Method m : CovarianceRTDemoWithGenericsDemo1.class.getMethods()) {
            if (!"getSomething".equals(m.getName())) {
                continue;
            }
            if (m.isBridge() && m.isSynthetic()) {
                getSomethingBridge = m;
            } else {
                getSomethingNotBridge = m;
            }
        }
        // bridge & synthetic メソッドの方が、コンパイラが生成したメソッドで interface 側の戻り値。
        // (仮型パラメータが消され、 Object型を戻り値として生成されている)
        assertThat(getSomethingBridge.getReturnType()).isEqualTo(Object.class);
        // 非 bridge & synthetic メソッドがソースコードで宣言した通りのメソッドで 実装側の戻り値。
        assertThat(getSomethingNotBridge.getReturnType()).isEqualTo(SomeParent.class);
    }

    static <T> Optional<T> wrap(T value) {
        return Optional.of(value);
    }

    @Test
    public void testJava8TypeInferenceImprovementDemo() {
        /* (advent calendar には含まれていないが興味深いので採用)
         * - 引数と戻り値の不一致 - ジェネリクス・ケーススタディ - プログラマーの脳みそ
         *   https://nagise.hatenablog.jp/entry/20150219/1424313791
         */

        Optional<SomeParent> o1 = wrap(new SomeParent("aa"));
        assertThat(o1.get().field1).isEqualTo("aa");

        Optional<SomeChild> o2 = wrap(new SomeChild("bb", "cc"));
        assertThat(o2.get().field1).isEqualTo("bb");
        assertThat(o2.get().field2).isEqualTo("cc");

        /* java7 までは、ジェネリクスは非変なため Foo<SomeParent> = xxx<SomeChild> はコンパイルエラー。
         * しかし Java 8 で型推論が強化され、左辺から推論して右辺の仮型パラメータを
         * SomeParent にバインドしてくれるようになった。
         */
        Optional<SomeParent> o3 = wrap(new SomeChild("dd", "ee"));
        assertThat(o3.get().field1).isEqualTo("dd");
        // 中身自体は SomeChild のまま。
        assertThat(o3.get() instanceof SomeChild).isTrue();

        Optional<SomeParent> o4 = wrap((SomeParent) (new SomeChild("ff", "gg")));
        assertThat(o4.get().field1).isEqualTo("ff");
        // 中身自体は SomeChild のまま。
        assertThat(o4.get() instanceof SomeChild).isTrue();

        Optional<SomeParent> o5 = TestGenericsHellAdventCalendarDemo.<SomeParent>wrap(new SomeChild("hh", "ii"));
        assertThat(o5.get().field1).isEqualTo("hh");
        // 中身自体は SomeChild のまま。
        assertThat(o5.get() instanceof SomeChild).isTrue();
    }

    /* TODO
     * DAY-22, イレイジャ : https://nagise.hatenablog.jp/entry/20171222/1513951362
     * 
     * - Javaのジェネリクスとリフレクション - プログラマーの脳みそ
     *   https://nagise.hatenablog.jp/entry/20121226/1356531878
     * - Javaのジェネリクスとリフレクション応用編 - プログラマーの脳みそ
     *   https://nagise.hatenablog.jp/entry/20130815/1376527213
     */

    /* DAY-24, new T() : https://nagise.hatenablog.jp/entry/20171224/1514127133
     * 
     * 他, new T[] など含め参照:
     * 
     * [ref1] : new T()したいケースへの対処法 - プログラマーの脳みそ
     * https://nagise.hatenablog.jp/entry/20131121/1385046248
     * 
     * [ref2] : Java ジェネリクスのポイント - Qiita
     *   https://qiita.com/pebblip/items/1206f866980f2ff91e77
     * 
     * NOTE-1: まとめると Java8 時点では new T() はできず、new T[] もできない。
     * new T() と同等のことをするには、以下のパターンがある。
     * [パターン1] : Class<T> を渡してもらってそこから Class#newInstance()
     * [パターン2] : インスタンススコープの実型パラメータをリフレクションで取り出し Class#newInstance()
     * [パターン3] : factory クラスを経由。java8 なら Supplier として Foo::new コンストラクタ参照を経由。
     * 
     * DAY-24 ではパターン1を非推奨として紹介し、java8時代の記事ということもありパターン3を推奨として紹介している。
     * また [ref1] ではパターン1に加えてパターン2についても具体的なコードを紹介している。
     * [ref2] ではパターン1を紹介し、加えて newT[] ができない点についても解説している。
     * (解決策は明示されていないが、List<T>を作るに留めるしか無い気がする。利用側でlist -> arrayに変換。)
     * 
     * NOTE-2: Class#newInstance() は java9 から deprecated されたので、もしパターン1を使うなら
     * Class#getDeclaredConstructor().newInstance() を使う方が良さそう。
     * (Class#newInstance() では、Class#newInstance() 自身のチェック例外が定義されているにも
     *  関わらず元のコンストラクタのチェック例外も送出されてしまい、チェック例外がすり抜けてしまう問題があった。
     *  -> Class#newInstance() 用に try-catch で補足していても、元のコンストラクタからのチェック例外により
     *     catch漏れが発生する。
     *  これの対策として、 Constructor#newInstance() が推奨されるようになったらしい。
     *  こちらではコンストラクタが送出するチェック例外を InvocationTargetException でラップする。
     *  InvocationTargetException は Constructor#newInstance() のチェック例外となっているので、
     *  これについて try-catch で補足していれば、チェック例外が漏れることはなくなる。
     *  オリジナルのチェック例外を取得する場合は InvocationTargetException#getTargetException() または
     *  InvocationTargetException#getCause() で取得できる。)
     * 
     * see-also:
     * https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Class.html#newInstance()
     * https://bugs.openjdk.java.net/browse/JDK-6850612
     * http://siosio.hatenablog.com/entry/2012/05/15/232823
     * https://blog.y-yuki.net/entry/2017/07/10/173000
     */

    static class SomeDefaultConstructor {
    }

    // [パターン1]
    static <T> T createSomeNewInstance1(Class<T> clazz)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        //return clazz.newInstance(); // java9 からdeprecated
        return clazz.getDeclaredConstructor().newInstance();
    }

    @Test
    public void testCreateSomeNewInstance1Demo() throws ReflectiveOperationException {
        final SomeDefaultConstructor o1 = createSomeNewInstance1(SomeDefaultConstructor.class);
        assertThat(o1 instanceof SomeDefaultConstructor).isTrue();
    }

    // [パターン2]
    static class CreateSomeNewInstanceBase<T> {
        @SuppressWarnings("unchecked")
        T create() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
                InvocationTargetException, NoSuchMethodException, SecurityException {
            // 実行時に自分自身の型を取得
            Class<?> clazz = this.getClass();
            // 継承でバインドされたことを想定し、ジェネリクス化された親の型情報を取得
            Type type = clazz.getGenericSuperclass();
            ParameterizedType pt = (ParameterizedType) type;
            // 親の型変数に対するバインドされた型がとれる
            Type[] actualTypeArguments = pt.getActualTypeArguments();
            Class<?> entityClass = (Class<?>) actualTypeArguments[0];
            // リフレクションでインスタンスを生成
            return (T) entityClass.getDeclaredConstructor().newInstance();
        }
    }

    // 継承によるバインド
    static class CreateSomeNewInstance2 extends CreateSomeNewInstanceBase<SomeDefaultConstructor> {
    }

    @Test
    public void testCreateSomeNewInstance2Demo() throws ReflectiveOperationException {
        final CreateSomeNewInstance2 creator = new CreateSomeNewInstance2();
        final SomeDefaultConstructor o1 = creator.create();
        assertThat(o1 instanceof SomeDefaultConstructor).isTrue();
    }

    // [パターン3]
    static <T> T createSomeNewInstance3(final Supplier<T> factory) {
        return factory.get();
    }

    @Test
    public void testCreateSomeNewInstance3Demo() {
        final SomeDefaultConstructor o1 = createSomeNewInstance3(SomeDefaultConstructor::new);
        assertThat(o1 instanceof SomeDefaultConstructor).isTrue();
    }
}
