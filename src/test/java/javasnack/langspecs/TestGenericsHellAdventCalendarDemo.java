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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class TestGenericsHellAdventCalendarDemo {
    /* Java ジェネリクスのブログ記事を参考にしたユースケースのデモと練習。
     * reference:
     * - Java Generics Hell Advent Calendar 2017 - Adventar
     *   https://adventar.org/calendars/2751
     * - Javaジェネリクス再入門 - プログラマーの脳みそ
     *   https://nagise.hatenablog.jp/entry/20101105/1288938415
     * - JJUG CCC 2013 Fall でジェネリクスのセッションやりました - プログラマーの脳みそ
     *   https://nagise.hatenablog.jp/entry/20131111/1384168238
     * - Java ジェネリクスのポイント - Qiita
     *   https://qiita.com/pebblip/items/1206f866980f2ff91e77
     */

    static class SomeParent {
        final String field1;

        SomeParent(final String field1) {
            this.field1 = field1;
        }
    }

    static class SomeChild extends SomeParent {
        final String field2;

        SomeChild(final String field1, final String field2) {
            super(field1);
            this.field2 = field2;
        }
    }

    static class SomeGrandChild extends SomeChild {
        final String field3;

        SomeGrandChild(final String field1, final String field2, final String field3) {
            super(field1, field2);
            this.field3 = field3;
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
    }

    @Test
    public void testInvariantForParameterizedType() {
        /* DAY-5, パラメタライズドタイプ : https://nagise.hatenablog.jp/entry/20171205/1512480154
         * 
         * 実型パラメータ(actual type parameter) に親子関係がありそれ単体なら共変が可能でも、
         * パラメータ化された型(parameterized type)では共変は成立しない。
         * = 子クラスの実型パラメータでパラメータ化された型(List<SomeChild>)を、
         * 親クラスの実型パラメータでパラメータ化された型(List<SomeParent>)に参照代入できない。
         * = 非変, invariant
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
     * 
     * 基本的な型変数の宣言と使用方法の例示
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

        // new 演算子におけるバインド例 : 省略してるのでただ <> があるだけ。
        final InstanceScopeDemo1<String, SomeParent> d2 = new InstanceScopeDemo1<>("ccc", new SomeParent("ddd"));
        final Map<String, SomeParent> map2 = d2.asMap();
        assertThat(map2).hasSize(1);
        assertThat(map2.get("ccc").field1).isEqualTo("ddd");
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
    public void testCovarianceAkaBoundedWildcardDemo() {
        // DAY-13, 共変ワイルドカード : https://nagise.hatenablog.jp/entry/20171213/1513172356
        final List<SomeParent> parents = List.of(new SomeParent("aa"));
        final List<SomeChild> children = List.of(new SomeChild("bb", "cc"));
        final List<SomeGrandChild> grandChildren = List.of(new SomeGrandChild("dd", "ee", "ff"));
        // ジェネリクスでは実型パラメータ(actual type parameter)については非変となり、以下はコンパイルエラー。
        //parents = children;
        //children = parents;

        // 境界ワイルドカード型(bounded wildcard type or 共変ワイルドカード)を使うと共変関係があれば代入可能となる。
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

    /* TODO
     * 11日目, https://nagise.hatenablog.jp/entry/20171211/1512993295
     * から。
     * 
     * - Javaのジェネリクスとリフレクション - プログラマーの脳みそ
     * https://nagise.hatenablog.jp/entry/20121226/1356531878
     * - Javaのジェネリクスとリフレクション応用編 - プログラマーの脳みそ
     * https://nagise.hatenablog.jp/entry/20130815/1376527213
     * 
     * - 引数と戻り値の不一致 - ジェネリクス・ケーススタディ - プログラマーの脳みそ
     * https://nagise.hatenablog.jp/entry/20150219/1424313791
     * 
     * - new T()したいケースへの対処法 - プログラマーの脳みそ
     * https://nagise.hatenablog.jp/entry/20131121/1385046248
     * 
     * - ジェネリクスと配列 - プログラマーの脳みそ
     * https://nagise.hatenablog.jp/entry/20180214/1518569217
     * 
     */

}
