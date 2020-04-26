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

package javasnack.langspecs.generics;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class TestGenericsHellAdventCalendarReflectionDemo {
    /* DAY-22, イレイジャ : https://nagise.hatenablog.jp/entry/20171222/1513951362
     * -> 長くなったのでテストコードを分割した。
     * 
     * リフレクションからの取得におけるクラス階層の考え方:
     * 
     * java.lang.reflect.Type : これが「型」を総合的に扱うインターフェイスとなる。
     *   -> ここからジェネリクスに対応したサブインターフェイスが派生する。
     *     -> ParameterizedType : List<E> などのパラメータ化された型 (parameterized type) を表現
     *     -> TypeVariable : E などの型変数(type parameter) を表現
     *     -> WildcardType : <?> / <? extends|super T> などの bounded wildcard を表現
     *     -> GenericArrayType : T[] などの配列を表現
     *   -> Class も Type を実装している。
     * 
     * Class を起点として、Type を返すリフレクションAPIについては
     * 上記のような具象インスタンスのいずれかを返すことになる。
     * -> instanceof などで適宜判定して具象型/サブインターフェイスにキャストして扱う。
     * 
     * Type系のインスタンスメソッドは、再帰的に Type/Type[] を返すものもある。
     * -> List<List<Map<Foo, Bar>>> などネストしたジェネリクス型を表現できるようになっている。
     * 
     * その他参照:
     * - Javaのジェネリクスとリフレクション - プログラマーの脳みそ
     *   https://nagise.hatenablog.jp/entry/20121226/1356531878
     * - Javaのジェネリクスとリフレクション応用編 - プログラマーの脳みそ
     *   https://nagise.hatenablog.jp/entry/20130815/1376527213
     * - リフレクションで、ジェネリクスの情報にアクセスする
     *   https://kazuhira-r.hatenablog.com/entry/20130309/1362838458
     * - Javaのジェネリック型引数をリフレクションによって取得する方法の実験サンプル。
     *   https://gist.github.com/seraphy/c8f088fc081054116266011ae56d4bd7
     */

    static class ReflectionDemoOfParameterizedFieldConstructorParameter {
        // Field#getGenericType() -> String.class
        final String name;

        // Field#getGenericType() -> List.class
        @SuppressWarnings("rawtypes")
        final List rawlist;

        /* Field#getGenericType()
         *   -> ParameterizedType
         *     -> #getRawType() -> List.class
         *     -> #getActualTypeArguments()
         *       -> Type[0] -> SomeChild.class
         */
        final List<SomeChild> parameterizedList;

        /* Field#getGenericType()
         *   -> ParameterizedType
         *     -> #getRawType() -> Map.class
         *     -> #getActualTypeArguments()
         *       -> Type[0] -> String.class
         *       -> Type[1] -> SomeChild.class
         */
        final Map<String, SomeChild> parameterizedMap;

        @SuppressWarnings("rawtypes")
        ReflectionDemoOfParameterizedFieldConstructorParameter(
                /* Parameter#getType() -> String.class
                 * Parameter#getParameterizedType()
                 *   -> String.class
                 */
                final String name,

                /* Parameter#getType() -> List.class
                 * Parameter#getParameterizedType()
                 *   -> List.class
                 */
                final List rawlist,

                /* Parameter#getType() -> List.class
                 * Parameter#getParameterizedType()
                 *   -> ParameterizedType
                 *     -> #getRawType() -> List.class
                 *     -> #getActualTypeArguments()
                 *       -> Type[0] -> SomeChild.class
                 */
                final List<SomeChild> parameterizedList,

                /* Parameter#getType() -> Map.class
                 * Parameter#getParameterizedType()
                 *   -> ParameterizedType
                 *     -> #getRawType() -> Map.class
                 *     -> #getActualTypeArguments()
                 *        -> Type[0] -> String.class
                 *        -> Type[1] -> SomeChild.class
                 */
                final Map<String, SomeChild> parameterizedMap) {
            this.name = name;
            this.rawlist = rawlist;
            this.parameterizedList = parameterizedList;
            this.parameterizedMap = parameterizedMap;
        }
    }

    @Test
    public void testReflectionDemoOfParameterizedFieldConstructorParameter() throws Exception {
        Type t0 = null;
        final Class<?> clazz = ReflectionDemoOfParameterizedFieldConstructorParameter.class;

        final Field nameField = clazz.getDeclaredField("name");
        t0 = nameField.getGenericType();
        assertThat(t0).isEqualTo(String.class);
        assertThat(t0 instanceof ParameterizedType).isFalse();

        final Field rawListField = clazz.getDeclaredField("rawlist");
        t0 = rawListField.getGenericType();
        assertThat(t0).isEqualTo(List.class);
        assertThat(t0 instanceof ParameterizedType).isFalse();

        final Field parameterizedListField = clazz.getDeclaredField("parameterizedList");
        t0 = parameterizedListField.getGenericType();
        // パラメータ化された型のフィールドだと原型とは互換性がなくなり、ParameterizedTypeになる。
        assertThat(t0).isNotEqualTo(List.class);
        assertThat(t0 instanceof ParameterizedType).isTrue();
        ParameterizedType pt0 = (ParameterizedType) t0;
        // 実型パラメータを取得
        Type[] types = pt0.getActualTypeArguments();
        assertThat(types).hasSize(1);
        assertThat(types[0]).isEqualTo(SomeChild.class);
        // 原型を取得
        t0 = pt0.getRawType();
        assertThat(t0).isEqualTo(List.class);

        final Field parameterizedMapField = clazz.getDeclaredField("parameterizedMap");
        t0 = parameterizedMapField.getGenericType();
        // パラメータ化された型のフィールドだと原型とは互換性がなくなり、ParameterizedTypeになる。
        assertThat(t0).isNotEqualTo(Map.class);
        assertThat(t0 instanceof ParameterizedType).isTrue();
        pt0 = (ParameterizedType) t0;
        // 実型パラメータを取得
        types = pt0.getActualTypeArguments();
        assertThat(types).hasSize(2);
        assertThat(types[0]).isEqualTo(String.class);
        assertThat(types[1]).isEqualTo(SomeChild.class);
        // 原型を取得
        t0 = pt0.getRawType();
        assertThat(t0).isEqualTo(Map.class);

        // コンストラクタ
        final Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        assertThat(constructors).hasSize(1);
        final Constructor<?> c0 = constructors[0];
        // コンストラクタ引数
        Parameter[] parameters = c0.getParameters();
        assertThat(parameters).hasSize(4);
        // String
        Parameter p0 = parameters[0];
        assertThat(p0.getType()).isEqualTo(String.class);
        t0 = p0.getParameterizedType();
        assertThat(t0).isEqualTo(String.class);
        assertThat(t0 instanceof ParameterizedType).isFalse();
        // List
        p0 = parameters[1];
        assertThat(p0.getType()).isEqualTo(List.class);
        t0 = p0.getParameterizedType();
        assertThat(t0).isEqualTo(List.class);
        assertThat(t0 instanceof ParameterizedType).isFalse();
        // List<SomeChild>
        p0 = parameters[2];
        assertThat(p0.getType()).isEqualTo(List.class);
        t0 = p0.getParameterizedType();
        // パラメータ化された型のフィールドだと原型とは互換性がなくなり、ParameterizedTypeになる。
        assertThat(t0).isNotEqualTo(List.class);
        assertThat(t0 instanceof ParameterizedType).isTrue();
        pt0 = (ParameterizedType) t0;
        // 実型パラメータを取得
        types = pt0.getActualTypeArguments();
        assertThat(types).hasSize(1);
        assertThat(types[0]).isEqualTo(SomeChild.class);
        // 原型を取得
        t0 = pt0.getRawType();
        assertThat(t0).isEqualTo(List.class);
        // Map<String, SomeParent>
        p0 = parameters[3];
        assertThat(p0.getType()).isEqualTo(Map.class);
        t0 = p0.getParameterizedType();
        // パラメータ化された型のフィールドだと原型とは互換性がなくなり、ParameterizedTypeになる。
        assertThat(t0).isNotEqualTo(Map.class);
        assertThat(t0 instanceof ParameterizedType).isTrue();
        pt0 = (ParameterizedType) t0;
        // 実型パラメータを取得
        types = pt0.getActualTypeArguments();
        assertThat(types).hasSize(2);
        assertThat(types[0]).isEqualTo(String.class);
        assertThat(types[1]).isEqualTo(SomeChild.class);
        // 原型を取得
        t0 = pt0.getRawType();
        assertThat(t0).isEqualTo(Map.class);
    }

    interface ReflectionDemoOfParameterizedMethodParameterAndReturnValue {
        /* Method#getParameters()
         * -> Parameter[0]
         *   -> #getType() -> String.class
         *   -> #getParameterizedType() -> String.class
         * 
         * Method#getGenericParameterTypes()
         * -> Type[0] -> String.class
         * 
         * Method#getReturnType() -> String.class 
         * 
         * Method#getGenericReturnType() -> String.class
         */
        String returnNonGeneric(String name);

        /* Method#getParameters()
         * -> Parameter[0]
         *   -> #getType() -> List.class
         *   -> #getParameterizedType() -> List.class
         * 
         * Method#getGenericParameterTypes()
         * -> Type[0] -> List.class
         * 
         * Method#getReturnType() -> List.class 
         * 
         * Method#getGenericReturnType() -> List.class
         */
        @SuppressWarnings("rawtypes")
        List returnRawType(List arg1);

        /* Method#getParameters()
         * -> Parameter[0]
         *   -> #getType() -> List.class
         *   -> #getParameterizedType()
         *     -> ParameterizedType
         *       -> #getRawType() -> List.class
         *       -> #getActualTypeArguments()
         *         -> Type[0] -> SomeParent.class
         * 
         * Method#getGenericParameterTypes()
         * -> Type[0]
         *   -> ParameterizedType
         *     -> #getRawType() -> List.class
         *     -> #getActualTypeArguments()
         *       -> Type[0] -> SomeParent.class
         * 
         * Method#getReturnType() -> List.class 
         * 
         * Method#getGenericReturnType()
         * -> ParameterizedType
         *   -> #getRawType() -> List.class
         *   -> #getActualTypeArguments()
         *     -> Type[0] -> SomeParent.class
         */
        List<SomeChild> returnParameterizedList(List<SomeParent> arg1);

        /* Method#getParameters()
         * -> Parameter[0]
         *   -> #getType() -> Map.class
         *   -> #getParameterizedType()
         *     -> ParameterizedType
         *       -> #getRawType() -> Map.class
         *       -> #getActualTypeArguments()
         *         -> Type[0] -> String.class
         *         -> Type[1] -> SomeChild.class
         * 
         * Method#getGenericParameterTypes()
         * -> Type[0]
         *   -> ParameterizedType
         *     -> #getRawType() -> Map.class
         *     -> #getActualTypeArguments()
         *       -> Type[0] -> String.class
         *       -> Type[1] -> SomeChild.class
         * 
         * Method#getReturnType() -> Map.class 
         * 
         * Method#getGenericReturnType()
         * -> ParameterizedType
         *   -> #getRawType() -> Map.class
         *   -> #getActualTypeArguments()
         *     -> Type[0] -> String.class
         *     -> Type[1] -> SomeChild.class
         */
        Map<String, SomeChild> returnParameterizedMap(Map<String, SomeParent> arg1);
    }

    @Test
    public void testReflectionDemoOfParameterizedMethodParameterAndReturnValue() throws Exception {
        final Class<?> clazz = ReflectionDemoOfParameterizedMethodParameterAndReturnValue.class;

        Method m0 = clazz.getDeclaredMethod("returnNonGeneric", String.class);
        // メソッド引数
        Parameter[] parameters = m0.getParameters();
        assertThat(parameters).hasSize(1);
        Parameter p0 = parameters[0];
        assertThat(p0.getType()).isEqualTo(String.class);
        Type t0 = p0.getParameterizedType();
        assertThat(t0).isEqualTo(String.class);
        assertThat(t0 instanceof ParameterizedType).isFalse();
        // メソッド戻り値
        Class<?> clazz0 = m0.getReturnType();
        assertThat(clazz0).isEqualTo(String.class);
        // メソッド引数(generic対応版)
        Type[] types = m0.getGenericParameterTypes();
        assertThat(types).hasSize(1);
        t0 = types[0];
        assertThat(t0).isEqualTo(String.class);
        assertThat(t0 instanceof ParameterizedType).isFalse();
        // メソッド戻り値(generic対応版)
        t0 = m0.getGenericReturnType();
        assertThat(t0).isEqualTo(String.class);
        assertThat(t0 instanceof ParameterizedType).isFalse();

        m0 = clazz.getDeclaredMethod("returnRawType", List.class);
        // メソッド引数
        parameters = m0.getParameters();
        assertThat(parameters).hasSize(1);
        p0 = parameters[0];
        assertThat(p0.getType()).isEqualTo(List.class);
        t0 = p0.getParameterizedType();
        assertThat(t0).isEqualTo(List.class);
        assertThat(t0 instanceof ParameterizedType).isFalse();
        // メソッド戻り値
        clazz0 = m0.getReturnType();
        assertThat(clazz0).isEqualTo(List.class);
        // メソッド引数(generic対応版)
        types = m0.getGenericParameterTypes();
        assertThat(types).hasSize(1);
        t0 = types[0];
        assertThat(t0).isEqualTo(List.class);
        assertThat(t0 instanceof ParameterizedType).isFalse();
        // メソッド戻り値(generic対応版)
        t0 = m0.getGenericReturnType();
        assertThat(t0).isEqualTo(List.class);
        assertThat(t0 instanceof ParameterizedType).isFalse();

        m0 = clazz.getDeclaredMethod("returnParameterizedList", List.class);
        // メソッド引数
        parameters = m0.getParameters();
        assertThat(parameters).hasSize(1);
        p0 = parameters[0];
        assertThat(p0.getType()).isEqualTo(List.class);
        t0 = p0.getParameterizedType();
        // パラメータ化された型のフィールドだと原型とは互換性がなくなり、ParameterizedTypeになる。
        assertThat(t0).isNotEqualTo(List.class);
        assertThat(t0 instanceof ParameterizedType).isTrue();
        ParameterizedType pt0 = (ParameterizedType) t0;
        // 実型パラメータを取得
        types = pt0.getActualTypeArguments();
        assertThat(types).hasSize(1);
        assertThat(types[0]).isEqualTo(SomeParent.class);
        // 原型を取得
        t0 = pt0.getRawType();
        assertThat(t0).isEqualTo(List.class);

        // メソッド引数(generic対応版)
        types = m0.getGenericParameterTypes();
        assertThat(types).hasSize(1);
        t0 = types[0];
        // パラメータ化された型のフィールドだと原型とは互換性がなくなり、ParameterizedTypeになる。
        assertThat(t0).isNotEqualTo(List.class);
        assertThat(t0 instanceof ParameterizedType).isTrue();
        pt0 = (ParameterizedType) t0;
        // 実型パラメータを取得
        types = pt0.getActualTypeArguments();
        assertThat(types).hasSize(1);
        assertThat(types[0]).isEqualTo(SomeParent.class);
        // 原型を取得
        t0 = pt0.getRawType();
        assertThat(t0).isEqualTo(List.class);

        // メソッド戻り値
        clazz0 = m0.getReturnType();
        assertThat(clazz0).isEqualTo(List.class);

        // メソッド戻り値(generic対応版)
        t0 = m0.getGenericReturnType();
        assertThat(t0).isNotEqualTo(List.class);
        assertThat(t0 instanceof ParameterizedType).isTrue();
        pt0 = (ParameterizedType) t0;
        // 実型パラメータを取得
        types = pt0.getActualTypeArguments();
        assertThat(types).hasSize(1);
        assertThat(types[0]).isEqualTo(SomeChild.class);
        // 原型を取得
        t0 = pt0.getRawType();
        assertThat(t0).isEqualTo(List.class);

        m0 = clazz.getDeclaredMethod("returnParameterizedMap", Map.class);
        // メソッド引数
        parameters = m0.getParameters();
        assertThat(parameters).hasSize(1);
        p0 = parameters[0];
        assertThat(p0.getType()).isEqualTo(Map.class);
        t0 = p0.getParameterizedType();
        // パラメータ化された型のフィールドだと原型とは互換性がなくなり、ParameterizedTypeになる。
        assertThat(t0).isNotEqualTo(Map.class);
        assertThat(t0 instanceof ParameterizedType).isTrue();
        pt0 = (ParameterizedType) t0;
        // 実型パラメータを取得
        types = pt0.getActualTypeArguments();
        assertThat(types).hasSize(2);
        assertThat(types[0]).isEqualTo(String.class);
        assertThat(types[1]).isEqualTo(SomeParent.class);
        // 原型を取得
        t0 = pt0.getRawType();
        assertThat(t0).isEqualTo(Map.class);

        // メソッド引数(generic対応版)
        types = m0.getGenericParameterTypes();
        assertThat(types).hasSize(1);
        t0 = types[0];
        // パラメータ化された型のフィールドだと原型とは互換性がなくなり、ParameterizedTypeになる。
        assertThat(t0).isNotEqualTo(Map.class);
        assertThat(t0 instanceof ParameterizedType).isTrue();
        pt0 = (ParameterizedType) t0;
        // 実型パラメータを取得
        types = pt0.getActualTypeArguments();
        assertThat(types).hasSize(2);
        assertThat(types[0]).isEqualTo(String.class);
        assertThat(types[1]).isEqualTo(SomeParent.class);
        // 原型を取得
        t0 = pt0.getRawType();
        assertThat(t0).isEqualTo(Map.class);

        // メソッド戻り値
        clazz0 = m0.getReturnType();
        assertThat(clazz0).isEqualTo(Map.class);

        // メソッド戻り値(generic対応版)
        t0 = m0.getGenericReturnType();
        assertThat(t0).isNotEqualTo(Map.class);
        assertThat(t0 instanceof ParameterizedType).isTrue();
        pt0 = (ParameterizedType) t0;
        // 実型パラメータを取得
        types = pt0.getActualTypeArguments();
        assertThat(types).hasSize(2);
        assertThat(types[0]).isEqualTo(String.class);
        assertThat(types[1]).isEqualTo(SomeChild.class);
        // 原型を取得
        t0 = pt0.getRawType();
        assertThat(t0).isEqualTo(Map.class);
    }

    static class ReflectionDemoOfUpperLowerUnBoundWildcardType {
        /* 非境界型ワイルドカード型
         * Field#getGenericType()
         * -> ParameterizedType
         *   -> #getRawType() -> List.class
         *   -> #getActualTypeArguments()
         *     -> Type[0]
         *       -> WildcardType
         *         -> #getLowerBounds() -> {}
         *         -> #getUpperBounds() -> { Object.class }
         */
        List<?> unboundWildcardList;

        /* 共変 or 上限付き境界型ワイルドカード型
         * Field#getGenericType()
         * -> ParameterizedType
         *   -> #getRawType() -> List.class
         *   -> #getActualTypeArguments()
         *     -> Type[0]
         *       -> WildcardType
         *         -> #getLowerBounds() -> {}
         *         -> #getUpperBounds() -> { SomeChild.class }
         */
        List<? extends SomeChild> upperBoundWildcardList;

        /* 反変 or 下限付き境界型ワイルドカード型
         * Field#getGenericType()
         * -> ParameterizedType
         *   -> #getRawType() -> List.class
         *   -> #getActualTypeArguments()
         *     -> Type[0]
         *       -> WildcardType
         *         -> #getLowerBounds() -> { SomeChild.class }
         *         -> #getUpperBounds() -> { Object.class }
         */
        List<? super SomeChild> lowerBoundWildcardList;
    }

    @Test
    public void testReflectionDemoOfUpperLowerUnBoundWildcardType() throws Exception {
        Type t0 = null;
        final Class<?> clazz = ReflectionDemoOfUpperLowerUnBoundWildcardType.class;

        final Field unbound = clazz.getDeclaredField("unboundWildcardList");
        t0 = unbound.getGenericType();
        // パラメータ化された型のフィールドだと原型とは互換性がなくなり、ParameterizedTypeになる。
        assertThat(t0).isNotEqualTo(List.class);
        assertThat(t0 instanceof ParameterizedType).isTrue();
        ParameterizedType pt0 = (ParameterizedType) t0;
        // 実型パラメータを取得
        Type[] types = pt0.getActualTypeArguments();
        assertThat(types).hasSize(1);
        t0 = types[0];
        assertThat(t0 instanceof WildcardType).isTrue();
        WildcardType wt0 = (WildcardType) t0;
        assertThat(wt0.getLowerBounds()).hasSize(0);
        assertThat(wt0.getUpperBounds()).hasSize(1);
        assertThat(wt0.getUpperBounds()[0]).isEqualTo(Object.class);
        // 原型を取得
        t0 = pt0.getRawType();
        assertThat(t0).isEqualTo(List.class);

        final Field upperBound = clazz.getDeclaredField("upperBoundWildcardList");
        t0 = upperBound.getGenericType();
        // パラメータ化された型のフィールドだと原型とは互換性がなくなり、ParameterizedTypeになる。
        assertThat(t0).isNotEqualTo(List.class);
        assertThat(t0 instanceof ParameterizedType).isTrue();
        pt0 = (ParameterizedType) t0;
        // 実型パラメータを取得
        types = pt0.getActualTypeArguments();
        assertThat(types).hasSize(1);
        t0 = types[0];
        assertThat(t0 instanceof WildcardType).isTrue();
        wt0 = (WildcardType) t0;
        assertThat(wt0.getLowerBounds()).hasSize(0);
        assertThat(wt0.getUpperBounds()).hasSize(1);
        assertThat(wt0.getUpperBounds()[0]).isEqualTo(SomeChild.class);
        // 原型を取得
        t0 = pt0.getRawType();
        assertThat(t0).isEqualTo(List.class);

        final Field lowerBound = clazz.getDeclaredField("lowerBoundWildcardList");
        t0 = lowerBound.getGenericType();
        // パラメータ化された型のフィールドだと原型とは互換性がなくなり、ParameterizedTypeになる。
        assertThat(t0).isNotEqualTo(List.class);
        assertThat(t0 instanceof ParameterizedType).isTrue();
        pt0 = (ParameterizedType) t0;
        // 実型パラメータを取得
        types = pt0.getActualTypeArguments();
        assertThat(types).hasSize(1);
        t0 = types[0];
        assertThat(t0 instanceof WildcardType).isTrue();
        wt0 = (WildcardType) t0;
        assertThat(wt0.getLowerBounds()).hasSize(1);
        assertThat(wt0.getLowerBounds()[0]).isEqualTo(SomeChild.class);
        assertThat(wt0.getUpperBounds()).hasSize(1);
        assertThat(wt0.getUpperBounds()[0]).isEqualTo(Object.class);
        // 原型を取得
        t0 = pt0.getRawType();
        assertThat(t0).isEqualTo(List.class);
    }

    abstract static class ReflectionDemoGenericType1<T> {
        /* Field#getGenericType()
         * -> TypeVariable
         *   -> #getName() = T
         */
        public T field;

        /* Field#getGenericType()
         * -> GenericArrayType
         *   -> #getGenericComponentType()
         *     -> TypeVariable
         *       -> #getName() = T
         */
        public T[] arrayfield;

        /* Field#getGenericType()
         * -> ParameterizedType
         *   -> #getRawType() -> List.class
         *   -> #getActualTypeArguments()
         *     -> Type[0] -> TypeVariable
         *        -> #getName() = T
         */
        public List<T> listfield;

        /* Method#getParameters()
         * -> Parameter[0]
         *   -> #getType() -> Object.class
         *   -> #getParameterizedType()
         *     -> TypeVariable
         *       -> #getName() = T
         * 
         * Method#getGenericParameterTypes()
         * -> Type[0]
         *   -> TypeVariable
         *     -> #getName() = T
         * 
         * Method#getReturnType() -> Object.class 
         * 
         * Method#getGenericReturnType()
         * -> TypeVariable
         *   -> #getName() = T
         */
        public abstract T getAndSet1(T newValue);

        /* Method#getParameters()
         * -> Parameter[0]
         *   -> #getType() -> Object[].class
         *   -> #getParameterizedType()
         *     -> GenericArrayType
         *       -> #getGenericComponentType()
         *         -> TypeVariable
         *           -> #getName() = T
         * 
         * Method#getGenericParameterTypes()
         * -> Type[0]
         *   -> GenericArrayType
         *     -> #getGenericComponentType()
         *       -> TypeVariable
         *         -> #getName() = T
         * 
         * Method#getReturnType() -> Object[].class 
         * 
         * Method#getGenericReturnType()
         * -> GenericArrayType
         *   -> #getGenericComponentType()
         *     -> TypeVariable
         *       -> #getName() = T
         */
        public abstract T[] getAndSet2(T[] newArray);

        /* Method#getParameters()
         * -> Parameter[0]
         *   -> #getType() -> List.class
         *   -> #getParameterizedType()
         *     -> ParameterizedType
         *       -> #getRawType() -> List.class
         *       -> #getActualTypeArguments()
         *         -> Type[0] -> TypeVariable
         *           -> #getName() = T
         * 
         * Method#getGenericParameterTypes()
         * -> Type[0]
         *   -> ParameterizedType
         *     -> #getRawType() -> List.class
         *     -> #getActualTypeArguments()
         *       -> Type[0] -> TypeVariable
         *         -> #getName() = T
         * 
         * Method#getReturnType() -> List.class 
         * 
         * Method#getGenericReturnType()
         * -> ParameterizedType
         *   -> #getRawType() -> List.class
         *   -> #getActualTypeArguments()
         *     -> Type[0] -> TypeVariable
         *       -> #getName() = T
         */
        public abstract List<T> getAndSet3(List<T> newList);

        public Class<?> getBoundClass() {
            // 実行時に自分自身の型を取得
            Class<?> clazz = this.getClass();
            // 継承でバインドされたことを想定し、ジェネリクス化された親の型情報を取得
            Type type = clazz.getGenericSuperclass();
            ParameterizedType pt = (ParameterizedType) type;
            // 親の型変数に対するバインドされた型がとれる
            Type[] actualTypeArguments = pt.getActualTypeArguments();
            return (Class<?>) actualTypeArguments[0];
        }
    }

    @Test
    public void testReflectionDemoGenericType1() throws Exception {
        Type t0 = null;
        final Class<?> clazz = ReflectionDemoGenericType1.class;

        final Field field1 = clazz.getDeclaredField("field");
        t0 = field1.getGenericType();
        assertThat(t0 instanceof Class).isFalse();
        assertThat(t0 instanceof ParameterizedType).isFalse();
        // 型変数として取得できる。
        assertThat(t0 instanceof TypeVariable).isTrue();
        TypeVariable<?> tv0 = (TypeVariable<?>) t0;
        assertThat(tv0.getName()).isEqualTo("T");

        final Field array1 = clazz.getDeclaredField("arrayfield");
        t0 = array1.getGenericType();
        assertThat(t0 instanceof Class).isFalse();
        assertThat(t0 instanceof ParameterizedType).isFalse();
        assertThat(t0 instanceof TypeVariable).isFalse();
        assertThat(t0 instanceof GenericArrayType).isTrue();
        // 配列 -> 型変数として取得できる。
        GenericArrayType gat0 = (GenericArrayType) t0;
        t0 = gat0.getGenericComponentType();
        assertThat(t0 instanceof ParameterizedType).isFalse();
        assertThat(t0 instanceof TypeVariable).isTrue();
        tv0 = (TypeVariable<?>) t0;
        assertThat(tv0.getName()).isEqualTo("T");

        final Field listfield1 = clazz.getDeclaredField("listfield");
        t0 = listfield1.getGenericType();
        assertThat(t0 instanceof Class).isFalse();
        assertThat(t0 instanceof ParameterizedType).isTrue();
        ParameterizedType pt0 = (ParameterizedType) t0;
        // 実型パラメータを取得
        Type[] types = pt0.getActualTypeArguments();
        assertThat(types).hasSize(1);
        // 型変数として取得できる。
        assertThat(types[0] instanceof TypeVariable).isTrue();
        tv0 = (TypeVariable<?>) types[0];
        assertThat(tv0.getName()).isEqualTo("T");
        // 原型を取得
        t0 = pt0.getRawType();
        assertThat(t0).isEqualTo(List.class);

        // 型変数をパラメータにとっているため、暫定で Object 型でメソッドを取得する。
        Method m0 = clazz.getDeclaredMethod("getAndSet1", Object.class);
        // メソッド引数
        Parameter[] parameters = m0.getParameters();
        assertThat(parameters).hasSize(1);
        Parameter p0 = parameters[0];
        assertThat(p0.getType()).isEqualTo(Object.class);
        t0 = p0.getParameterizedType();
        // 型変数として取得できる。
        assertThat(t0 instanceof TypeVariable).isTrue();
        tv0 = (TypeVariable<?>) t0;
        assertThat(tv0.getName()).isEqualTo("T");

        // メソッド引数(generic対応版)
        types = m0.getGenericParameterTypes();
        assertThat(types).hasSize(1);
        t0 = types[0];
        // 仮型パラメータのフィールドだと TypeVariable になる。
        assertThat(t0).isNotEqualTo(Object.class);
        assertThat(t0 instanceof ParameterizedType).isFalse();
        assertThat(t0 instanceof TypeVariable).isTrue();
        tv0 = (TypeVariable<?>) t0;
        assertThat(tv0.getName()).isEqualTo("T");

        // メソッド戻り値
        Class<?> clazz0 = m0.getReturnType();
        assertThat(clazz0).isEqualTo(Object.class);

        // メソッド戻り値(generic対応版)
        t0 = m0.getGenericReturnType();
        // 仮型パラメータのフィールドだと TypeVariable になる。
        assertThat(t0).isNotEqualTo(Object.class);
        assertThat(t0 instanceof ParameterizedType).isFalse();
        assertThat(t0 instanceof TypeVariable).isTrue();
        tv0 = (TypeVariable<?>) t0;
        assertThat(tv0.getName()).isEqualTo("T");

        // 型変数をパラメータにとっているため、暫定で Object[] 型でメソッドを取得する。
        m0 = clazz.getDeclaredMethod("getAndSet2", new Class[] { Object[].class });
        // メソッド引数
        parameters = m0.getParameters();
        assertThat(parameters).hasSize(1);
        p0 = parameters[0];
        assertThat(p0.getType()).isEqualTo(Object[].class);
        t0 = p0.getParameterizedType();
        // 配列 -> 型変数として取得できる。
        assertThat(t0 instanceof ParameterizedType).isFalse();
        assertThat(t0 instanceof TypeVariable).isFalse();
        assertThat(t0 instanceof GenericArrayType).isTrue();
        gat0 = (GenericArrayType) t0;
        t0 = gat0.getGenericComponentType();
        assertThat(t0 instanceof ParameterizedType).isFalse();
        assertThat(t0 instanceof TypeVariable).isTrue();
        tv0 = (TypeVariable<?>) t0;
        assertThat(tv0.getName()).isEqualTo("T");

        // メソッド引数(generic対応版)
        types = m0.getGenericParameterTypes();
        assertThat(types).hasSize(1);
        t0 = types[0];
        // 配列 -> 型変数として取得できる。
        assertThat(t0).isNotEqualTo(Object[].class);
        assertThat(t0 instanceof ParameterizedType).isFalse();
        assertThat(t0 instanceof TypeVariable).isFalse();
        assertThat(t0 instanceof GenericArrayType).isTrue();
        gat0 = (GenericArrayType) t0;
        t0 = gat0.getGenericComponentType();
        assertThat(t0 instanceof ParameterizedType).isFalse();
        assertThat(t0 instanceof TypeVariable).isTrue();
        tv0 = (TypeVariable<?>) t0;
        assertThat(tv0.getName()).isEqualTo("T");

        // メソッド戻り値
        clazz0 = m0.getReturnType();
        assertThat(clazz0).isEqualTo(Object[].class);

        // メソッド戻り値(generic対応版)
        t0 = m0.getGenericReturnType();
        // 仮型パラメータのフィールドだと TypeVariable になる。
        assertThat(t0).isNotEqualTo(Object[].class);
        assertThat(t0 instanceof ParameterizedType).isFalse();
        assertThat(t0 instanceof TypeVariable).isFalse();
        assertThat(t0 instanceof GenericArrayType).isTrue();
        gat0 = (GenericArrayType) t0;
        t0 = gat0.getGenericComponentType();
        assertThat(t0 instanceof ParameterizedType).isFalse();
        assertThat(t0 instanceof TypeVariable).isTrue();
        tv0 = (TypeVariable<?>) t0;
        assertThat(tv0.getName()).isEqualTo("T");

        // ジェネリック型をパラメータにとっているため、原型でメソッドを取得する。
        m0 = clazz.getDeclaredMethod("getAndSet3", List.class);
        // メソッド引数
        parameters = m0.getParameters();
        assertThat(parameters).hasSize(1);
        p0 = parameters[0];
        assertThat(p0.getType()).isEqualTo(List.class);
        t0 = p0.getParameterizedType();
        // パラメータ化された型のフィールドだと原型とは互換性がなくなり、ParameterizedTypeになる。
        assertThat(t0).isNotEqualTo(List.class);
        assertThat(t0 instanceof ParameterizedType).isTrue();
        pt0 = (ParameterizedType) t0;
        // 実型パラメータを取得
        types = pt0.getActualTypeArguments();
        assertThat(types).hasSize(1);
        // 型変数として取得できる。
        assertThat(types[0] instanceof TypeVariable).isTrue();
        tv0 = (TypeVariable<?>) types[0];
        assertThat(tv0.getName()).isEqualTo("T");
        // 原型を取得
        t0 = pt0.getRawType();
        assertThat(t0).isEqualTo(List.class);

        // メソッド引数(generic対応版)
        types = m0.getGenericParameterTypes();
        assertThat(types).hasSize(1);
        t0 = types[0];
        // パラメータ化された型のフィールドだと原型とは互換性がなくなり、ParameterizedTypeになる。
        assertThat(t0).isNotEqualTo(List.class);
        assertThat(t0 instanceof ParameterizedType).isTrue();
        pt0 = (ParameterizedType) t0;
        // 実型パラメータを取得
        types = pt0.getActualTypeArguments();
        assertThat(types).hasSize(1);
        // 型変数として取得できる。
        assertThat(types[0] instanceof TypeVariable).isTrue();
        tv0 = (TypeVariable<?>) types[0];
        assertThat(tv0.getName()).isEqualTo("T");
        // 原型を取得
        t0 = pt0.getRawType();
        assertThat(t0).isEqualTo(List.class);

        // メソッド戻り値
        clazz0 = m0.getReturnType();
        assertThat(clazz0).isEqualTo(List.class);

        // メソッド戻り値(generic対応版)
        t0 = m0.getGenericReturnType();
        assertThat(t0).isNotEqualTo(List.class);
        assertThat(t0 instanceof ParameterizedType).isTrue();
        pt0 = (ParameterizedType) t0;
        // 実型パラメータを取得
        types = pt0.getActualTypeArguments();
        assertThat(types).hasSize(1);
        // 型変数として取得できる。
        assertThat(types[0] instanceof TypeVariable).isTrue();
        tv0 = (TypeVariable<?>) types[0];
        assertThat(tv0.getName()).isEqualTo("T");
        // 原型を取得
        t0 = pt0.getRawType();
        assertThat(t0).isEqualTo(List.class);
    }

    static class ReflectionDemoBoundGenericType1 extends ReflectionDemoGenericType1<String> {
        @Override
        public String getAndSet1(String newValue) {
            return newValue;
        }

        @Override
        public String[] getAndSet2(String[] newArray) {
            return newArray;
        }

        @Override
        public List<String> getAndSet3(List<String> newList) {
            return newList;
        }
    }

    @Test
    public void testReflectionDemoBoundGenericType1() throws Exception {
        final ReflectionDemoBoundGenericType1 t1 = new ReflectionDemoBoundGenericType1();
        assertThat(t1.getBoundClass()).isEqualTo(String.class);
    }
}
