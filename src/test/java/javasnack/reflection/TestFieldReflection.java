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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TestFieldReflection {
    public static class Base {
        byte packagedByteField = 1;
        @SuppressWarnings("unused")
        private short privateShortField = 2;
        protected int protectedIntField = 3;
        public long publicLongField = 4L;
        volatile boolean volatileBooleanField = true;
        final String finalStringField = "abc";
        public static final String PUBLIC_FINAL_STR = "ABC";
    }

    public static class Ext extends Base {
        public char publicCharField = 'x';
        public static float staticFloatField = 1.0f;
        @SuppressWarnings("unused")
        private static double PRIVATE_FINAL_DOUBLE = 0.1;
        List<String> stringlist;
    }

    @Test
    public void testPublicFieldReadWrite() throws IllegalArgumentException, IllegalAccessException {
        Class<?> c0 = Base.class;
        Base o0 = new Base();
        Field[] fields = c0.getFields();
        assertEquals(2, fields.length);
        Field f0 = fields[0];
        assertEquals("publicLongField", f0.getName());
        assertEquals("long", f0.getType().getName());
        assertEquals("long", f0.getGenericType().getTypeName());
        assertEquals("public", Modifier.toString(f0.getModifiers()));
        assertEquals(4L, f0.getLong(o0));
        f0.setLong(o0, 5L);
        assertEquals(5L, f0.getLong(o0));
        f0 = fields[1];
        assertEquals("PUBLIC_FINAL_STR", f0.getName());
        assertEquals("java.lang.String", f0.getType().getName());
        assertEquals("java.lang.String", f0.getGenericType().getTypeName());
        assertEquals("public static final", Modifier.toString(f0.getModifiers()));
        assertEquals(Base.PUBLIC_FINAL_STR, f0.get(o0)); // from instance object
        assertEquals(Base.PUBLIC_FINAL_STR, f0.get(c0)); // from class object

        c0 = Ext.class;
        Ext o1 = new Ext();
        fields = c0.getFields();
        assertEquals(4, fields.length);
        f0 = fields[0];
        assertEquals("publicCharField", f0.getName());
        assertEquals("char", f0.getType().getName());
        assertEquals("char", f0.getGenericType().getTypeName());
        assertEquals("public", Modifier.toString(f0.getModifiers()));
        assertEquals('x', f0.getChar(o1));
        f0.setChar(o1, 'y');
        assertEquals('y', f0.getLong(o1));
        f0 = fields[1];
        assertEquals("staticFloatField", f0.getName());
        assertEquals("float", f0.getType().getName());
        assertEquals("float", f0.getGenericType().getTypeName());
        assertEquals("public static", Modifier.toString(f0.getModifiers()));
        assertEquals(1.0f, f0.getFloat(o1)); // from instance object
        assertEquals(1.0f, f0.getFloat(c0)); // from class object
        f0.setFloat(c0, 2.0f);
        assertEquals(2.0f, f0.getFloat(c0));
        f0.setFloat(c0, 1.0f);
        assertEquals(1.0f, f0.getFloat(c0)); // from class object
        f0 = fields[2];
        // NOTE : getFields() can access superclass' public fields.
        assertEquals("publicLongField", f0.getName());
        assertEquals("long", f0.getType().getName());
        assertEquals("long", f0.getGenericType().getTypeName());
        assertEquals("public", Modifier.toString(f0.getModifiers()));
        assertEquals(4L, f0.getLong(o1));
        f0.setLong(o0, 5L);
        assertEquals(5L, f0.getLong(o0));
        f0 = fields[3];
        assertEquals("PUBLIC_FINAL_STR", f0.getName());
        assertEquals("java.lang.String", f0.getType().getName());
        assertEquals("java.lang.String", f0.getGenericType().getTypeName());
        assertEquals("public static final", Modifier.toString(f0.getModifiers()));
        assertEquals(Base.PUBLIC_FINAL_STR, f0.get(o1)); // from instance object
        assertEquals(Base.PUBLIC_FINAL_STR, f0.get(c0)); // from class object
    }

    @Test
    public void testUnknownFiledName() throws NoSuchFieldException, SecurityException {
        Class<?> c0 = Base.class;
        assertThrows(NoSuchFieldException.class, () -> {
            c0.getField("xxxxx");
        });
    }

    @Test
    public void testNonPublicFiled() throws NoSuchFieldException, SecurityException {
        Class<?> c0 = Base.class;
        assertThrows(NoSuchFieldException.class, () -> {
            c0.getField("protectedIntField");
        });
    }

    @Test
    public void testFiledGetTypeMismatch()
            throws IllegalArgumentException, NoSuchFieldException, SecurityException, IllegalAccessException {
        Class<?> c0 = Base.class;
        Base b0 = new Base();
        Field f0 = c0.getField("publicLongField");
        assertThrows(IllegalArgumentException.class, () -> {
            f0.getBoolean(b0);
        });
    }

    @Test
    public void testReadInstanceFieldFromClass()
            throws IllegalArgumentException, NoSuchFieldException, SecurityException, IllegalAccessException {
        Class<?> c0 = Base.class;
        Field f0 = c0.getField("publicLongField");
        assertThrows(IllegalArgumentException.class, () -> {
            f0.getLong(c0);
        });
    }

    @Test
    public void testReadNonPublicFieldValueFromStaticInnerClass()
            throws IllegalArgumentException, NoSuchFieldException, SecurityException, IllegalAccessException {
        Class<?> c0 = Base.class;
        Base b0 = new Base();
        Field f0 = c0.getDeclaredField("privateShortField");
        // In Java11(may be 9 or 10 ??), no exception thrown when accessing private field in static inner class
        assertEquals(2, f0.getShort(b0));
    }

    @Test
    public void testReadNonPublicFieldValueFromOuterClass()
            throws IllegalArgumentException, NoSuchFieldException, SecurityException, IllegalAccessException {
        Class<?> c0 = SomeConcreteIndependentClass.class;
        SomeConcreteIndependentClass b0 = new SomeConcreteIndependentClass();
        Field f0 = c0.getDeclaredField("privateShortField");
        assertThrows(IllegalAccessException.class, () -> {
            f0.getShort(b0);
        });
    }

    @Test
    public void testWriteToFinalField()
            throws IllegalArgumentException, NoSuchFieldException, SecurityException, IllegalAccessException {
        Class<?> c0 = Base.class;
        Base b0 = new Base();
        Field f0 = c0.getDeclaredField("finalStringField");
        assertThrows(IllegalAccessException.class, () -> {
            f0.set(b0, "aaa");
        });
    }

    @Test
    public void testNonPublicFieldReadWrite() throws IllegalArgumentException, IllegalAccessException {
        Class<?> c0 = Base.class;
        Base o0 = new Base();
        Field[] fields = c0.getDeclaredFields();
        assertEquals(7, fields.length);

        Field f0 = fields[0];
        assertEquals("packagedByteField", f0.getName());
        assertEquals("byte", f0.getType().getName());
        assertEquals("byte", f0.getGenericType().getTypeName());
        assertEquals("", Modifier.toString(f0.getModifiers()));
        assertEquals(1, f0.getByte(o0));
        f0.setByte(o0, (byte) 2);
        assertEquals(2, f0.getByte(o0));
        f0 = fields[1];
        assertEquals("privateShortField", f0.getName());
        assertEquals("short", f0.getType().getName());
        assertEquals("short", f0.getGenericType().getTypeName());
        assertEquals("private", Modifier.toString(f0.getModifiers()));
        f0.setAccessible(true);
        assertEquals(2, f0.getShort(o0));
        f0.setShort(o0, (short) 20);
        assertEquals(20, f0.getShort(o0));
        f0 = fields[2];
        assertEquals("protectedIntField", f0.getName());
        assertEquals("int", f0.getType().getName());
        assertEquals("int", f0.getGenericType().getTypeName());
        assertEquals("protected", Modifier.toString(f0.getModifiers()));
        f0.setAccessible(true);
        assertEquals(3, f0.getInt(o0));
        f0.setInt(o0, 30);
        assertEquals(30, f0.getInt(o0));
        f0 = fields[3];
        assertEquals("publicLongField", f0.getName());
        assertEquals("long", f0.getType().getName());
        assertEquals("long", f0.getGenericType().getTypeName());
        assertEquals("public", Modifier.toString(f0.getModifiers()));
        assertEquals(4L, f0.getLong(o0));
        f0.setLong(o0, 5L);
        assertEquals(5L, f0.getLong(o0));
        f0 = fields[4];
        assertEquals("volatileBooleanField", f0.getName());
        assertEquals("boolean", f0.getType().getName());
        assertEquals("boolean", f0.getGenericType().getTypeName());
        assertEquals("volatile", Modifier.toString(f0.getModifiers()));
        assertEquals(true, f0.getBoolean(o0));
        f0.setBoolean(o0, false);
        assertEquals(false, f0.getBoolean(o0));
        f0 = fields[5];
        assertEquals("finalStringField", f0.getName());
        assertEquals("java.lang.String", f0.getType().getName());
        assertEquals("java.lang.String", f0.getGenericType().getTypeName());
        assertEquals("final", Modifier.toString(f0.getModifiers()));
        assertEquals("abc", f0.get(o0));
        f0 = fields[6];
        assertEquals("PUBLIC_FINAL_STR", f0.getName());
        assertEquals("java.lang.String", f0.getType().getName());
        assertEquals("java.lang.String", f0.getGenericType().getTypeName());
        assertEquals("public static final", Modifier.toString(f0.getModifiers()));
        assertEquals(Base.PUBLIC_FINAL_STR, f0.get(o0)); // from instance object
        assertEquals(Base.PUBLIC_FINAL_STR, f0.get(c0)); // from class object

        c0 = Ext.class;
        Ext o1 = new Ext();
        fields = c0.getDeclaredFields();
        assertEquals(4, fields.length);

        // NOTE : getDeclaredFields() can ONLY access class itself's fields.
        // opposite to getFields(), can NOT access superclass' fields
        f0 = fields[0];
        assertEquals("publicCharField", f0.getName());
        assertEquals("char", f0.getType().getName());
        assertEquals("char", f0.getGenericType().getTypeName());
        assertEquals("public", Modifier.toString(f0.getModifiers()));
        assertEquals('x', f0.getChar(o1));
        f0.setChar(o1, 'y');
        assertEquals('y', f0.getLong(o1));
        f0 = fields[1];
        assertEquals("staticFloatField", f0.getName());
        assertEquals("float", f0.getType().getName());
        assertEquals("float", f0.getGenericType().getTypeName());
        assertEquals("public static", Modifier.toString(f0.getModifiers()));
        assertEquals(1.0f, f0.getFloat(o1)); // from instance object
        assertEquals(1.0f, f0.getFloat(c0)); // from class object
        f0.setFloat(c0, 2.0f);
        assertEquals(2.0f, f0.getFloat(c0));
        f0.setFloat(c0, 1.0f);
        assertEquals(1.0f, f0.getFloat(c0)); // from class object
        f0 = fields[2];
        assertEquals("PRIVATE_FINAL_DOUBLE", f0.getName());
        assertEquals("double", f0.getType().getName());
        assertEquals("double", f0.getGenericType().getTypeName());
        assertEquals("private static", Modifier.toString(f0.getModifiers()));
        f0.setAccessible(true);
        assertEquals(0.1, f0.getDouble(o1)); // from instance object
        assertEquals(0.1, f0.getDouble(c0)); // from class object
        f0.setDouble(c0, 0.2);
        assertEquals(0.2, f0.getDouble(c0));
        f0 = fields[3];
        assertEquals("stringlist", f0.getName());
        assertEquals("java.util.List", f0.getType().getName());
        assertEquals("java.util.List<java.lang.String>", f0.getGenericType().getTypeName());
        assertEquals("", Modifier.toString(f0.getModifiers()));
        assertNull(f0.get(o1));
        f0.set(o1, new ArrayList<>(Arrays.asList("abc", "def")));
        @SuppressWarnings("unchecked")
        List<String> r = (List<String>) f0.get(o1);
        assertEquals("abc", r.get(0));
        assertEquals("def", r.get(1));
    }
}
