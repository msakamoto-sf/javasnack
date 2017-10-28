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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

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
        Assert.assertEquals(fields.length, 2);
        Field f0 = fields[0];
        Assert.assertEquals(f0.getName(), "publicLongField");
        Assert.assertEquals(f0.getType().getName(), "long");
        Assert.assertEquals(f0.getGenericType().getTypeName(), "long");
        Assert.assertEquals(Modifier.toString(f0.getModifiers()), "public");
        Assert.assertEquals(f0.getLong(o0), 4L);
        f0.setLong(o0, 5L);
        Assert.assertEquals(f0.getLong(o0), 5L);
        f0 = fields[1];
        Assert.assertEquals(f0.getName(), "PUBLIC_FINAL_STR");
        Assert.assertEquals(f0.getType().getName(), "java.lang.String");
        Assert.assertEquals(f0.getGenericType().getTypeName(), "java.lang.String");
        Assert.assertEquals(Modifier.toString(f0.getModifiers()), "public static final");
        Assert.assertEquals(f0.get(o0), Base.PUBLIC_FINAL_STR); // from instance object
        Assert.assertEquals(f0.get(c0), Base.PUBLIC_FINAL_STR); // from class object

        c0 = Ext.class;
        Ext o1 = new Ext();
        fields = c0.getFields();
        Assert.assertEquals(fields.length, 4);
        f0 = fields[0];
        Assert.assertEquals(f0.getName(), "publicCharField");
        Assert.assertEquals(f0.getType().getName(), "char");
        Assert.assertEquals(f0.getGenericType().getTypeName(), "char");
        Assert.assertEquals(Modifier.toString(f0.getModifiers()), "public");
        Assert.assertEquals(f0.getChar(o1), 'x');
        f0.setChar(o1, 'y');
        Assert.assertEquals(f0.getLong(o1), 'y');
        f0 = fields[1];
        Assert.assertEquals(f0.getName(), "staticFloatField");
        Assert.assertEquals(f0.getType().getName(), "float");
        Assert.assertEquals(f0.getGenericType().getTypeName(), "float");
        Assert.assertEquals(Modifier.toString(f0.getModifiers()), "public static");
        Assert.assertEquals(f0.getFloat(o1), 1.0f); // from instance object
        Assert.assertEquals(f0.getFloat(c0), 1.0f); // from class object
        f0.setFloat(c0, 2.0f);
        Assert.assertEquals(f0.getFloat(c0), 2.0f);
        f0.setFloat(c0, 1.0f);
        Assert.assertEquals(f0.getFloat(c0), 1.0f); // from class object
        f0 = fields[2];
        // NOTE : getFields() can access superclass' public fields.
        Assert.assertEquals(f0.getName(), "publicLongField");
        Assert.assertEquals(f0.getType().getName(), "long");
        Assert.assertEquals(f0.getGenericType().getTypeName(), "long");
        Assert.assertEquals(Modifier.toString(f0.getModifiers()), "public");
        Assert.assertEquals(f0.getLong(o1), 4L);
        f0.setLong(o0, 5L);
        Assert.assertEquals(f0.getLong(o0), 5L);
        f0 = fields[3];
        Assert.assertEquals(f0.getName(), "PUBLIC_FINAL_STR");
        Assert.assertEquals(f0.getType().getName(), "java.lang.String");
        Assert.assertEquals(f0.getGenericType().getTypeName(), "java.lang.String");
        Assert.assertEquals(Modifier.toString(f0.getModifiers()), "public static final");
        Assert.assertEquals(f0.get(o1), Base.PUBLIC_FINAL_STR); // from instance object
        Assert.assertEquals(f0.get(c0), Base.PUBLIC_FINAL_STR); // from class object
    }

    @Test(expectedExceptions = NoSuchFieldException.class)
    public void testUnknownFiledName() throws NoSuchFieldException, SecurityException {
        Class<?> c0 = Base.class;
        c0.getField("xxxxx");
        Assert.fail();
    }

    @Test(expectedExceptions = NoSuchFieldException.class)
    public void testNonPublicFiled() throws NoSuchFieldException, SecurityException {
        Class<?> c0 = Base.class;
        c0.getField("protectedIntField");
        Assert.fail();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFiledGetTypeMismatch()
            throws IllegalArgumentException, NoSuchFieldException, SecurityException, IllegalAccessException {
        Class<?> c0 = Base.class;
        Base b0 = new Base();
        Field f0 = c0.getField("publicLongField");
        Assert.assertEquals(f0.getBoolean(b0), 1L);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testReadInstanceFieldFromClass()
            throws IllegalArgumentException, NoSuchFieldException, SecurityException, IllegalAccessException {
        Class<?> c0 = Base.class;
        Field f0 = c0.getField("publicLongField");
        Assert.assertEquals(f0.getLong(c0), 1L);
    }

    @Test(expectedExceptions = IllegalAccessException.class)
    public void testReadNonPublicFieldValue()
            throws IllegalArgumentException, NoSuchFieldException, SecurityException, IllegalAccessException {
        Class<?> c0 = Base.class;
        Base b0 = new Base();
        Field f0 = c0.getDeclaredField("privateShortField");
        f0.getShort(b0);
    }

    @Test(expectedExceptions = IllegalAccessException.class)
    public void testWriteToFinalField()
            throws IllegalArgumentException, NoSuchFieldException, SecurityException, IllegalAccessException {
        Class<?> c0 = Base.class;
        Base b0 = new Base();
        Field f0 = c0.getDeclaredField("finalStringField");
        f0.set(b0, "aaa");
    }

    @Test
    public void testNonPublicFieldReadWrite() throws IllegalArgumentException, IllegalAccessException {
        Class<?> c0 = Base.class;
        Base o0 = new Base();
        Field[] fields = c0.getDeclaredFields();
        Assert.assertEquals(fields.length, 7);

        Field f0 = fields[0];
        Assert.assertEquals(f0.getName(), "packagedByteField");
        Assert.assertEquals(f0.getType().getName(), "byte");
        Assert.assertEquals(f0.getGenericType().getTypeName(), "byte");
        Assert.assertEquals(Modifier.toString(f0.getModifiers()), "");
        Assert.assertEquals(f0.getByte(o0), 1);
        f0.setByte(o0, (byte) 2);
        Assert.assertEquals(f0.getByte(o0), 2);
        f0 = fields[1];
        Assert.assertEquals(f0.getName(), "privateShortField");
        Assert.assertEquals(f0.getType().getName(), "short");
        Assert.assertEquals(f0.getGenericType().getTypeName(), "short");
        Assert.assertEquals(Modifier.toString(f0.getModifiers()), "private");
        f0.setAccessible(true);
        Assert.assertEquals(f0.getShort(o0), 2);
        f0.setShort(o0, (short) 20);
        Assert.assertEquals(f0.getShort(o0), 20);
        f0 = fields[2];
        Assert.assertEquals(f0.getName(), "protectedIntField");
        Assert.assertEquals(f0.getType().getName(), "int");
        Assert.assertEquals(f0.getGenericType().getTypeName(), "int");
        Assert.assertEquals(Modifier.toString(f0.getModifiers()), "protected");
        f0.setAccessible(true);
        Assert.assertEquals(f0.getInt(o0), 3);
        f0.setInt(o0, 30);
        Assert.assertEquals(f0.getInt(o0), 30);
        f0 = fields[3];
        Assert.assertEquals(f0.getName(), "publicLongField");
        Assert.assertEquals(f0.getType().getName(), "long");
        Assert.assertEquals(f0.getGenericType().getTypeName(), "long");
        Assert.assertEquals(Modifier.toString(f0.getModifiers()), "public");
        Assert.assertEquals(f0.getLong(o0), 4L);
        f0.setLong(o0, 5L);
        Assert.assertEquals(f0.getLong(o0), 5L);
        f0 = fields[4];
        Assert.assertEquals(f0.getName(), "volatileBooleanField");
        Assert.assertEquals(f0.getType().getName(), "boolean");
        Assert.assertEquals(f0.getGenericType().getTypeName(), "boolean");
        Assert.assertEquals(Modifier.toString(f0.getModifiers()), "volatile");
        Assert.assertEquals(f0.getBoolean(o0), true);
        f0.setBoolean(o0, false);
        Assert.assertEquals(f0.getBoolean(o0), false);
        f0 = fields[5];
        Assert.assertEquals(f0.getName(), "finalStringField");
        Assert.assertEquals(f0.getType().getName(), "java.lang.String");
        Assert.assertEquals(f0.getGenericType().getTypeName(), "java.lang.String");
        Assert.assertEquals(Modifier.toString(f0.getModifiers()), "final");
        Assert.assertEquals(f0.get(o0), "abc");
        f0 = fields[6];
        Assert.assertEquals(f0.getName(), "PUBLIC_FINAL_STR");
        Assert.assertEquals(f0.getType().getName(), "java.lang.String");
        Assert.assertEquals(f0.getGenericType().getTypeName(), "java.lang.String");
        Assert.assertEquals(Modifier.toString(f0.getModifiers()), "public static final");
        Assert.assertEquals(f0.get(o0), Base.PUBLIC_FINAL_STR); // from instance object
        Assert.assertEquals(f0.get(c0), Base.PUBLIC_FINAL_STR); // from class object

        c0 = Ext.class;
        Ext o1 = new Ext();
        fields = c0.getDeclaredFields();
        Assert.assertEquals(fields.length, 4);

        // NOTE : getDeclaredFields() can ONLY access class itself's fields.
        // opposite to getFields(), can NOT access superclass' fields
        f0 = fields[0];
        Assert.assertEquals(f0.getName(), "publicCharField");
        Assert.assertEquals(f0.getType().getName(), "char");
        Assert.assertEquals(f0.getGenericType().getTypeName(), "char");
        Assert.assertEquals(Modifier.toString(f0.getModifiers()), "public");
        Assert.assertEquals(f0.getChar(o1), 'x');
        f0.setChar(o1, 'y');
        Assert.assertEquals(f0.getLong(o1), 'y');
        f0 = fields[1];
        Assert.assertEquals(f0.getName(), "staticFloatField");
        Assert.assertEquals(f0.getType().getName(), "float");
        Assert.assertEquals(f0.getGenericType().getTypeName(), "float");
        Assert.assertEquals(Modifier.toString(f0.getModifiers()), "public static");
        Assert.assertEquals(f0.getFloat(o1), 1.0f); // from instance object
        Assert.assertEquals(f0.getFloat(c0), 1.0f); // from class object
        f0.setFloat(c0, 2.0f);
        Assert.assertEquals(f0.getFloat(c0), 2.0f);
        f0.setFloat(c0, 1.0f);
        Assert.assertEquals(f0.getFloat(c0), 1.0f); // from class object
        f0 = fields[2];
        Assert.assertEquals(f0.getName(), "PRIVATE_FINAL_DOUBLE");
        Assert.assertEquals(f0.getType().getName(), "double");
        Assert.assertEquals(f0.getGenericType().getTypeName(), "double");
        Assert.assertEquals(Modifier.toString(f0.getModifiers()), "private static");
        f0.setAccessible(true);
        Assert.assertEquals(f0.getDouble(o1), 0.1); // from instance object
        Assert.assertEquals(f0.getDouble(c0), 0.1); // from class object
        f0.setDouble(c0, 0.2);
        Assert.assertEquals(f0.getDouble(c0), 0.2);
        f0 = fields[3];
        Assert.assertEquals(f0.getName(), "stringlist");
        Assert.assertEquals(f0.getType().getName(), "java.util.List");
        Assert.assertEquals(f0.getGenericType().getTypeName(), "java.util.List<java.lang.String>");
        Assert.assertEquals(Modifier.toString(f0.getModifiers()), "");
        Assert.assertNull(f0.get(o1));
        f0.set(o1, new ArrayList<>(Arrays.asList("abc", "def")));
        @SuppressWarnings("unchecked")
        List<String> r = (List<String>) f0.get(o1);
        Assert.assertEquals(r.get(0), "abc");
        Assert.assertEquals(r.get(1), "def");
    }
}
