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
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Objects;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javasnack.langspecs.TestClassNames.NestedClass.NestedNestedClass;
import lombok.Value;

/* 以下の記事のクラス名解説と、Class.forName() ではどれを渡せば良いのかの確認テスト。
 * see:
 * "Javaのクラス名の形式まとめ - プログラマーの脳みそ"
 * https://nagise.hatenablog.jp/entry/2020/04/14/135846
 * 
 * もともとこの記事を読んで抱いた疑問として「では Class.forName() にはどれを渡せばいいの？」
 * で、回答を書くと Class.getName() で取得した「バイナリ名(binary name)」を渡せば良い。
 */
public class TestClassNames {

    @Value
    private static class ClassNames {
        /**
         * {@link Class#getSimpleName()},
         * Simple Type Name : 単純型名。
         * パッケージ名を含まない型名。Qualified Type Nameと対比して使う表現
         */
        final String simpleName;

        /**
         * {@link Class#getName()},
         * 参照型の場合はそのバイナリ名(binary name)を返す。
         * primitive型の場合はその型名を返す。
         * 配列の場合はちょっと特殊になる。
         * 
         * バイナリ名は、トップレベル型の場合は正規名と同じ。
         * メンバー型(inner/nested)は外側のクラスのバイナリ名 + "$" + 単純型名。
         * ローカル型は外側のクラスのバイナリ名 + "$" + シーケンス番号 + 単純型名。
         */
        final String name;

        /**
         * {@link Class#getTypeName()},
         * informative(情報提供) な型名を返す。
         * (特に正式に仕様化されたものでは無さそう)
         */
        final String typeName;

        /**
         * {@link Class#getCanonicalName()},
         * Canonical Name : 正規名。
         * 
         * Qualified Type Name : 限定型名, Simple Type Name との対比でパッケージ名を含む型名。
         * Fully Qualified Name : 完全限定名, Qualified Type Name に加えて primitive 型も含む型名。
         * → 同じ内部クラスを指すのにFQNが異なるケースがあり、それを正規化したものがCanonical Name。
         * 
         * 通常は Canonical Name = FQN であり、それはprimitive型を含む Qualified Type Name として考えて支障ない。
         */
        final String canonicalName;

        static ClassNames of(final String simpleName, final String unpackagedName, final String unpackagedTypeName,
                final String unpackagedCanonicalName) {
            final String pkgprefix = ClassNames.class.getPackageName() + ".";
            return new ClassNames(
                    simpleName,
                    unpackagedName.replace("##PKG##", pkgprefix),
                    unpackagedTypeName.replace("##PKG##", pkgprefix),
                    Objects.nonNull(unpackagedCanonicalName)
                            ? unpackagedCanonicalName.replace("##PKG##", pkgprefix)
                            : null);
        }

        static ClassNames of(Class<?> clazz) {
            return new ClassNames(
                    clazz.getSimpleName(),
                    clazz.getName(),
                    clazz.getTypeName(),
                    clazz.getCanonicalName());
        }
    }

    @Test
    public void testPrimitiveTypeNames() {
        Class<?> c0 = Integer.TYPE;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "int",
                "int",
                "int",
                "int"));

        c0 = Boolean.TYPE;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "boolean",
                "boolean",
                "boolean",
                "boolean"));

        c0 = Character.TYPE;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "char",
                "char",
                "char",
                "char"));
    }

    @Test
    public void testArrayOfPrimitiveTypeNames() {
        Class<?> c0 = int[].class;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "int[]",
                "[I",
                "int[]",
                "int[]"));

        c0 = boolean[].class;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "boolean[]",
                "[Z",
                "boolean[]",
                "boolean[]"));

        c0 = char[].class;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "char[]",
                "[C",
                "char[]",
                "char[]"));
    }

    class InnerClass {
    }

    static class NestedClass {
        public static class NestedNestedClass {
        }
    }

    interface InnerInterface {
    }

    // member interface は暗黙的にstaticなので、わざわざ static 宣言バージョンを検証する必要は無い。
    // see: https://docs.oracle.com/javase/specs/jls/se10/html/jls-8.html#jls-8.5.1
    //static interface NestedInterface {
    //}

    static class NestedClassA {
        class InnerClassA {
        }
    }

    static class NestedClassB extends NestedClassA {
    }

    @Test
    public void testReferenceTypeNames() {
        Class<?> c0 = String.class;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "String",
                "java.lang.String",
                "java.lang.String",
                "java.lang.String"));

        c0 = TestClassNames.class;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "TestClassNames",
                "##PKG##TestClassNames",
                "##PKG##TestClassNames",
                "##PKG##TestClassNames"));

        class LocalClass {
        }

        c0 = LocalClass.class;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "LocalClass",
                "##PKG##TestClassNames$1LocalClass",
                "##PKG##TestClassNames$1LocalClass",
                null));

        c0 = InnerClass.class;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "InnerClass",
                "##PKG##TestClassNames$InnerClass",
                "##PKG##TestClassNames$InnerClass",
                "##PKG##TestClassNames.InnerClass"));

        c0 = NestedClass.class;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "NestedClass",
                "##PKG##TestClassNames$NestedClass",
                "##PKG##TestClassNames$NestedClass",
                "##PKG##TestClassNames.NestedClass"));

        c0 = NestedNestedClass.class;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "NestedNestedClass",
                "##PKG##TestClassNames$NestedClass$NestedNestedClass",
                "##PKG##TestClassNames$NestedClass$NestedNestedClass",
                "##PKG##TestClassNames.NestedClass.NestedNestedClass"));

        c0 = InnerInterface.class;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "InnerInterface",
                "##PKG##TestClassNames$InnerInterface",
                "##PKG##TestClassNames$InnerInterface",
                "##PKG##TestClassNames.InnerInterface"));

        c0 = NestedClassA.InnerClassA.class;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "InnerClassA",
                "##PKG##TestClassNames$NestedClassA$InnerClassA",
                "##PKG##TestClassNames$NestedClassA$InnerClassA",
                "##PKG##TestClassNames.NestedClassA.InnerClassA"));

        c0 = NestedClassB.InnerClassA.class;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "InnerClassA",
                "##PKG##TestClassNames$NestedClassA$InnerClassA",
                "##PKG##TestClassNames$NestedClassA$InnerClassA",
                "##PKG##TestClassNames.NestedClassA.InnerClassA"));
    }

    @Test
    public void testArrayOfReferenceTypeNames() {
        Class<?> c0 = String[].class;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "String[]",
                "[Ljava.lang.String;",
                "java.lang.String[]",
                "java.lang.String[]"));

        c0 = TestClassNames[].class;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "TestClassNames[]",
                "[L##PKG##TestClassNames;",
                "##PKG##TestClassNames[]",
                "##PKG##TestClassNames[]"));

        class LocalClass {
        }

        c0 = LocalClass[].class;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "LocalClass[]",
                "[L##PKG##TestClassNames$2LocalClass;",
                "##PKG##TestClassNames$2LocalClass[]",
                null));

        c0 = InnerClass[].class;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "InnerClass[]",
                "[L##PKG##TestClassNames$InnerClass;",
                "##PKG##TestClassNames$InnerClass[]",
                "##PKG##TestClassNames.InnerClass[]"));

        c0 = NestedClass[].class;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "NestedClass[]",
                "[L##PKG##TestClassNames$NestedClass;",
                "##PKG##TestClassNames$NestedClass[]",
                "##PKG##TestClassNames.NestedClass[]"));

        c0 = NestedNestedClass[].class;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "NestedNestedClass[]",
                "[L##PKG##TestClassNames$NestedClass$NestedNestedClass;",
                "##PKG##TestClassNames$NestedClass$NestedNestedClass[]",
                "##PKG##TestClassNames.NestedClass.NestedNestedClass[]"));

        c0 = InnerInterface[].class;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "InnerInterface[]",
                "[L##PKG##TestClassNames$InnerInterface;",
                "##PKG##TestClassNames$InnerInterface[]",
                "##PKG##TestClassNames.InnerInterface[]"));

        c0 = NestedClassA.InnerClassA[].class;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "InnerClassA[]",
                "[L##PKG##TestClassNames$NestedClassA$InnerClassA;",
                "##PKG##TestClassNames$NestedClassA$InnerClassA[]",
                "##PKG##TestClassNames.NestedClassA.InnerClassA[]"));

        c0 = NestedClassB.InnerClassA[].class;
        assertThat(ClassNames.of(c0)).isEqualTo(ClassNames.of(
                "InnerClassA[]",
                "[L##PKG##TestClassNames$NestedClassA$InnerClassA;",
                "##PKG##TestClassNames$NestedClassA$InnerClassA[]",
                "##PKG##TestClassNames.NestedClassA.InnerClassA[]"));
    }

    Class<?> getClass(final String fqn) throws ClassNotFoundException {
        final String pkgprefix = ClassNames.class.getPackageName() + ".";
        return Class.forName(fqn.replace("##PKG##", pkgprefix));
    }

    static Stream<Arguments> provideFqnAndExpectedClass() {
        return Stream.of(
                arguments("java.lang.String", String.class),
                arguments(TestClassNames.class.getName(), TestClassNames.class),
                arguments(InnerClass.class.getName(), InnerClass.class),
                arguments(NestedClass.class.getName(), NestedClass.class),
                arguments(NestedNestedClass.class.getName(), NestedNestedClass.class),
                arguments(InnerInterface.class.getName(), InnerInterface.class),
                arguments(NestedClassA.InnerClassA.class.getName(), NestedClassA.InnerClassA.class),
                arguments(NestedClassA.InnerClassA.class.getName(), NestedClassB.InnerClassA.class));
    }

    /**
     * {@link Class#forName(String)}, {@link Class#forName(String, boolean, ClassLoader)}
     * のjavadocによれば、そのクラス名パラメータはFQN(完全限定名)を渡す、となっている。
     * よってこれに使えるのは {@link Class#getCanonicalName()} で取り出した型名となる。
     * 
     * ・・・はず、なのだが・・・
     * 実際に inner/nested まで試すと、canonical name では ClassNotFoundException が発生してしまう。
     * バイナリ名 ({@link Class#getName()}) を渡すことで inner/nested まで正常に動作したので、
     * 実際はバイナリ名を渡すのが正しいようだ。
     * 
     * 実のところ {@link ClassLoader#loadClass(String)} の javadoc では binary name と明記
     * されているので、バイナリ名が正と思われる。
     * 
     * @param getName {@link Class#forName(String)} に渡す文字列
     * @param expectedClass 期待される Class
     * @throws ClassNotFoundException class not found
     */
    @ParameterizedTest
    @MethodSource("provideFqnAndExpectedClass")
    public void testClassForNamesByGetName(final String getName, final Class<?> expectedClass)
            throws ClassNotFoundException {
        Class<?> c0 = getClass(getName);
        assertThat(c0).isEqualTo(expectedClass);
    }

    static Stream<String> provideCanonicalNames() {
        return Stream.of(
                InnerClass.class.getCanonicalName(),
                NestedClass.class.getCanonicalName(),
                NestedNestedClass.class.getCanonicalName(),
                InnerInterface.class.getCanonicalName(),
                NestedClassA.InnerClassA.class.getCanonicalName(),
                NestedClassB.InnerClassA.class.getCanonicalName());
    }

    /**
     * {@link Class#forName(String)} に {@link Class#getCanonicalName()} (= 正規名) を渡しても、
     * inner/nested クラス系で ClassNotFoundException が発生してしまうことの確認テスト。 
     * 
     * @param canonicalName {@link Class#getCanonicalName()} で取得した正規名 (= FQN)
     */
    @ParameterizedTest
    @MethodSource("provideCanonicalNames")
    void implicitMethodSourceResolutionDemo(final String canonicalName) {
        assertThrows(ClassNotFoundException.class, () -> Class.forName(canonicalName));
    }
}
