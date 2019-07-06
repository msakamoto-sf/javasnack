// APL 2.0 / MIT dual license.
/*
 * Copyright 2017 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
/*
 * MIT License
 * 
 * Copyright (c) 2017 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package javasnack.reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TestClassMetaInfo {

    public static class TypicalClassMetaInfo {
        final String name;
        final String canonicalName;
        final String simpleName;
        final String typeName;
        final String packageName;
        final String modifiers;
        final boolean isPrimitive;
        final boolean isInterface;
        final boolean isArray;

        public TypicalClassMetaInfo(String name, String canonicalName, String simpleName, String typeName,
                String packageName, String modifiers, boolean isPrimitive, boolean isInterface, boolean isArray) {
            super();
            this.name = name;
            this.canonicalName = canonicalName;
            this.simpleName = simpleName;
            this.typeName = typeName;
            this.packageName = packageName;
            this.modifiers = modifiers;
            this.isPrimitive = isPrimitive;
            this.isInterface = isInterface;
            this.isArray = isArray;
        }
    }

    public static final class PublicStaticFinalClass {
    }

    public abstract static class PublicStaticAbstractClass {
    }

    public static interface PublicStaticInterface {
    }

    private static final class PrivateStaticFinalClass {
    }

    protected static final class ProtectedStaticFinalClass {
    }

    static class PackagedStaticClass {
    }

    static Stream<Arguments> provideTypicalClassMetaInfoArguments() {
        final String pkg = TestClassMetaInfo.class.getPackage().getName();
        final String staticPrefix = TestClassMetaInfo.class.getName();
        final List<String> stringList = new ArrayList<>();
        return Stream.of(
        // @formatter:off
        arguments(
            Object.class,
            new TypicalClassMetaInfo(
                    "java.lang.Object",
                    "java.lang.Object",
                    "Object",
                    "java.lang.Object",
                    "java.lang", "public",
                    false, false, false)),
        arguments(
            String.class,
            new TypicalClassMetaInfo(
                    "java.lang.String",
                    "java.lang.String",
                    "String",
                    "java.lang.String",
                    "java.lang", "public final",
                    false, false, false)),
        arguments(
            Integer.class,
            new TypicalClassMetaInfo(
                    "java.lang.Integer",
                    "java.lang.Integer",
                    "Integer",
                    "java.lang.Integer",
                    "java.lang", "public final",
                    false, false, false)),
        arguments(
            byte[].class,
            new TypicalClassMetaInfo(
                    "[B",
                    "byte[]",
                    "byte[]",
                    "byte[]",
                    "<null>", "public abstract final",
                    false, false, true)),
        arguments(
            String[].class,
            new TypicalClassMetaInfo(
                    "[Ljava.lang.String;",
                    "java.lang.String[]",
                    "String[]",
                    "java.lang.String[]",
                    "<null>", "public abstract final",
                    false, false, true)),
        arguments(
            List.class,
            new TypicalClassMetaInfo(
                    "java.util.List",
                    "java.util.List",
                    "List",
                    "java.util.List",
                    "java.util", "public abstract interface",
                    false, true, false)),
        arguments(
            stringList.getClass(),
            new TypicalClassMetaInfo(
                    "java.util.ArrayList",
                    "java.util.ArrayList",
                    "ArrayList",
                    "java.util.ArrayList",
                    "java.util", "public",
                    false, false, false)),
        arguments(
            PublicStaticFinalClass.class,
            new TypicalClassMetaInfo(
                    staticPrefix + "$PublicStaticFinalClass",
                    staticPrefix + ".PublicStaticFinalClass",
                    "PublicStaticFinalClass",
                    staticPrefix + "$PublicStaticFinalClass",
                    pkg, "public static final",
                    false, false, false)),
        arguments(
            PublicStaticAbstractClass.class,
            new TypicalClassMetaInfo(
                    staticPrefix + "$PublicStaticAbstractClass",
                    staticPrefix + ".PublicStaticAbstractClass",
                    "PublicStaticAbstractClass",
                    staticPrefix + "$PublicStaticAbstractClass",
                    pkg, "public abstract static",
                    false, false, false)),
        arguments(
            PublicStaticInterface.class,
            new TypicalClassMetaInfo(
                    staticPrefix + "$PublicStaticInterface",
                    staticPrefix + ".PublicStaticInterface",
                    "PublicStaticInterface",
                    staticPrefix + "$PublicStaticInterface",
                    pkg, "public abstract static interface",
                    false, true, false)),
        arguments(
            PrivateStaticFinalClass.class,
            new TypicalClassMetaInfo(
                    staticPrefix + "$PrivateStaticFinalClass",
                    staticPrefix + ".PrivateStaticFinalClass",
                    "PrivateStaticFinalClass",
                    staticPrefix + "$PrivateStaticFinalClass",
                    pkg, "private static final",
                    false, false, false)),
        arguments(
            ProtectedStaticFinalClass.class,
            new TypicalClassMetaInfo(
                    staticPrefix + "$ProtectedStaticFinalClass",
                    staticPrefix + ".ProtectedStaticFinalClass",
                    "ProtectedStaticFinalClass",
                    staticPrefix + "$ProtectedStaticFinalClass",
                    pkg, "protected static final",
                    false, false, false)),
        arguments(
            PackagedStaticClass.class,
            new TypicalClassMetaInfo(
                    staticPrefix + "$PackagedStaticClass",
                    staticPrefix + ".PackagedStaticClass",
                    "PackagedStaticClass",
                    staticPrefix + "$PackagedStaticClass",
                    pkg, "static",
                    false, false, false))
        // @formatter:on
        );
    }

    @ParameterizedTest
    @MethodSource("provideTypicalClassMetaInfoArguments")
    public void testTypicalClassMetaInfo(final Class<?> c, final TypicalClassMetaInfo expected) {
        TypicalClassMetaInfo actual = new TypicalClassMetaInfo(
        // @formatter:off
            c.getName(),
            c.getCanonicalName(),
            c.getSimpleName(),
            c.getTypeName(),
            (Objects.isNull(c.getPackage()) ? "<null>" : c.getPackage().getName()),
            Modifier.toString(c.getModifiers()),
            c.isPrimitive(),
            c.isInterface(),
            c.isArray()
            // @formatter:on
        );
        assertEquals(expected.name, actual.name);
        assertEquals(expected.canonicalName, actual.canonicalName);
        assertEquals(expected.simpleName, actual.simpleName);
        assertEquals(expected.typeName, actual.typeName);
        assertEquals(expected.packageName, actual.packageName);
        assertEquals(expected.modifiers, actual.modifiers);
        assertEquals(expected.isPrimitive, actual.isPrimitive);
        assertEquals(expected.isInterface, actual.isInterface);
        assertEquals(expected.isArray, actual.isArray);
    }

    @Test
    public void testTypeParameters() {
        Object o0 = new Object();
        Class<?> c0 = o0.getClass();
        TypeVariable<? extends GenericDeclaration>[] tv = c0.getTypeParameters();
        assertEquals(0, tv.length);
        c0 = List.class;
        tv = c0.getTypeParameters();
        assertEquals(1, tv.length);
        assertEquals("E", tv[0].getName());
        c0 = Map.class;
        tv = c0.getTypeParameters();
        assertEquals(2, tv.length);
        assertEquals("K", tv[0].getName());
        assertEquals("V", tv[1].getName());
    }

    @Test
    public void testImplementedInterfaces() {
        Object o0 = new Object();
        Class<?> c0 = o0.getClass();
        Class<?>[] ifs = c0.getInterfaces();
        Type[] gifs = c0.getGenericInterfaces();
        assertEquals(0, ifs.length);
        assertEquals(0, gifs.length);

        c0 = ArrayList.class;
        ifs = c0.getInterfaces();
        gifs = c0.getGenericInterfaces();
        assertEquals(4, ifs.length);
        assertEquals("List", ifs[0].getSimpleName());
        assertEquals("RandomAccess", ifs[1].getSimpleName());
        assertEquals("Cloneable", ifs[2].getSimpleName());
        assertEquals("Serializable", ifs[3].getSimpleName());
        assertEquals(4, gifs.length);
        assertEquals("java.util.List<E>", gifs[0].getTypeName());
        assertEquals("java.util.RandomAccess", gifs[1].getTypeName());
        assertEquals("java.lang.Cloneable", gifs[2].getTypeName());
        assertEquals("java.io.Serializable", gifs[3].getTypeName());

        c0 = LinkedHashMap.class;
        ifs = c0.getInterfaces();
        gifs = c0.getGenericInterfaces();
        assertEquals(1, ifs.length);
        assertEquals("Map", ifs[0].getSimpleName());
        assertEquals(1, gifs.length);
        assertEquals("java.util.Map<K, V>", gifs[0].getTypeName());

        byte[] ba = new byte[] {};
        c0 = ba.getClass();
        ifs = c0.getInterfaces();
        gifs = c0.getGenericInterfaces();
        assertEquals(2, ifs.length);
        assertEquals("Cloneable", ifs[0].getSimpleName());
        assertEquals("Serializable", ifs[1].getSimpleName());
        assertEquals(2, gifs.length);
        assertEquals("java.lang.Cloneable", gifs[0].getTypeName());
        assertEquals("java.io.Serializable", gifs[1].getTypeName());

        String[] sa = new String[] {};
        c0 = sa.getClass();
        ifs = c0.getInterfaces();
        gifs = c0.getGenericInterfaces();
        assertEquals(2, ifs.length);
        assertEquals("Cloneable", ifs[0].getSimpleName());
        assertEquals("Serializable", ifs[1].getSimpleName());
        assertEquals(2, gifs.length);
        assertEquals("java.lang.Cloneable", gifs[0].getTypeName());
        assertEquals("java.io.Serializable", gifs[1].getTypeName());
    }

    public static interface BaseInterface0 {
    }

    public static interface ExtendInterface1 extends BaseInterface0 {
    }

    public static interface ExtendInterface2 extends ExtendInterface1 {
    }

    public static class Base0 implements BaseInterface0 {
    }

    public static class Extend1 extends Base0 implements ExtendInterface1 {
    }

    public static class Extend2 extends Extend1 implements ExtendInterface2 {
    }

    @Test
    public void testSuperclass() {
        final String staticPrefix = this.getClass().getName();
        Class<?> c0 = Base0.class;
        assertEquals("java.lang.Object", c0.getSuperclass().getName());
        assertEquals("java.lang.Object", c0.getGenericSuperclass().getTypeName());
        c0 = Extend1.class;
        assertEquals(staticPrefix + "$Base0", c0.getSuperclass().getName());
        assertEquals(staticPrefix + "$Base0", c0.getGenericSuperclass().getTypeName());
        c0 = Extend2.class;
        assertEquals(staticPrefix + "$Extend1", c0.getSuperclass().getName());
        assertEquals(staticPrefix + "$Extend1", c0.getGenericSuperclass().getTypeName());
        c0 = ExtendInterface1.class;
        assertNull(c0.getSuperclass());
        assertNull(c0.getGenericSuperclass());
        c0 = ExtendInterface2.class;
        assertNull(c0.getSuperclass());
        assertNull(c0.getGenericSuperclass());

        c0 = byte[].class;
        assertEquals("java.lang.Object", c0.getSuperclass().getName());
        assertEquals("java.lang.Object", c0.getGenericSuperclass().getTypeName());
        c0 = String[].class;
        assertEquals("java.lang.Object", c0.getSuperclass().getName());
        assertEquals("java.lang.Object", c0.getGenericSuperclass().getTypeName());

        c0 = LinkedHashMap.class;
        assertEquals("java.util.HashMap", c0.getSuperclass().getName());
        assertEquals("java.util.HashMap<K, V>", c0.getGenericSuperclass().getTypeName());
    }

    @Test
    public void testAssignableFrom() {
        Base0 o0 = new Base0();
        assertTrue(o0 instanceof Base0);
        assertFalse(o0 instanceof Extend1);
        assertFalse(o0 instanceof Extend2);
        assertTrue(o0 instanceof BaseInterface0);
        assertFalse(o0 instanceof ExtendInterface1);
        assertFalse(o0 instanceof ExtendInterface2);

        Extend2 o1 = new Extend2();
        assertTrue(o1 instanceof Base0);
        assertTrue(o1 instanceof Extend1);
        assertTrue(o1 instanceof Extend2);
        assertTrue(o1 instanceof BaseInterface0);
        assertTrue(o1 instanceof ExtendInterface1);
        assertTrue(o1 instanceof ExtendInterface2);

        assertFalse(Base0.class.isAssignableFrom(BaseInterface0.class));
        assertTrue(BaseInterface0.class.isAssignableFrom(Base0.class));
        assertTrue(BaseInterface0.class.isAssignableFrom(Extend1.class));
        assertTrue(BaseInterface0.class.isAssignableFrom(Extend2.class));
        assertTrue(BaseInterface0.class.isAssignableFrom(BaseInterface0.class));
        assertTrue(BaseInterface0.class.isAssignableFrom(ExtendInterface1.class));
        assertTrue(BaseInterface0.class.isAssignableFrom(ExtendInterface2.class));
    }
}
