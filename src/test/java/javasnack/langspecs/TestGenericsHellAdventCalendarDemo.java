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
        String[] stringArray = { "one", "two" };
        Object[] objectArray = stringArray; // 代入できる
        assertThrows(ArrayStoreException.class, () -> {
            // compileは通るが、ランタイム例外が発生する。
            objectArray[0] = Integer.valueOf(1);
        });
    }

    @Test
    public void testInvariantForParameterizedType() {
        /* 実型パラメータ(actual type parameter) に親子関係がありそれ単体なら共変が可能でも、
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

    /* 基本的な型変数の宣言と使用方法の例示
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
