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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

public class TestConstructorReflection {

    public static class Demo1 {
        final int val1;
        final int val2;
        final String s1;

        private Demo1(int a0, int a1, String a2) {
            this.val1 = a0;
            this.val2 = a1;
            this.s1 = a2;
        }

        protected Demo1(int a0, String a2) {
            this(a0, 10, a2);
        }

        public Demo1(String a2) {
            this(10, 20, a2);
        }
    }

    @Test
    public void testConstructors() throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> c0 = Demo1.class;
        Constructor<?>[] cntrs = c0.getDeclaredConstructors();
        assertEquals(3, cntrs.length);
        Constructor<?> cntr = cntrs[0];
        assertEquals("private", Modifier.toString(cntr.getModifiers()));
        assertArrayEquals(new Class<?>[] { int.class, int.class, String.class }, cntr.getParameterTypes());
        cntr = cntrs[1];
        assertEquals("protected", Modifier.toString(cntr.getModifiers()));
        assertArrayEquals(new Class<?>[] { int.class, String.class }, cntr.getParameterTypes());
        cntr = cntrs[2];
        assertEquals("public", Modifier.toString(cntr.getModifiers()));
        assertArrayEquals(new Class<?>[] { String.class }, cntr.getParameterTypes());

        // NOTE : we can call private constructor :)
        Constructor<?> cntr1 = c0.getDeclaredConstructor(int.class, int.class, String.class);
        cntr1.setAccessible(true);
        Demo1 o1 = (Demo1) cntr1.newInstance(1, 2, "hello");
        assertEquals(1, o1.val1);
        assertEquals(2, o1.val2);
        assertEquals("hello", o1.s1);

        // NOTE : we can call protected constructor :)
        cntr1 = c0.getDeclaredConstructor(int.class, String.class);
        Demo1 o2 = (Demo1) cntr1.newInstance(3, "abc");
        assertEquals(3, o2.val1);
        assertEquals(10, o2.val2);
        assertEquals("abc", o2.s1);

        cntr1 = c0.getDeclaredConstructor(String.class);
        Demo1 o3 = (Demo1) cntr1.newInstance("def");
        assertEquals(10, o3.val1);
        assertEquals(20, o3.val2);
        assertEquals("def", o3.s1);
    }

    /* SPECIAL THANKS:
     * http://d.hatena.ne.jp/Nagise/20131121/1385046248
     * https://stackoverflow.com/questions/75175/create-instance-of-generic-type-in-java
     */

    public static class DemoPojo1 {
        int i0 = 100;
    }

    public static class DemoPojo2 {
        String s0 = "hello";
    }

    public static <T> T createPojo(Class<T> clazz) throws NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Constructor<T> c0 = clazz.getDeclaredConstructor(new Class<?>[] {});
        T r = c0.newInstance();
        return r;
    }

    @Test
    public void testPojoCreation() throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        DemoPojo1 pojo1 = createPojo(DemoPojo1.class);
        assertEquals(100, pojo1.i0);
        DemoPojo2 pojo2 = createPojo(DemoPojo2.class);
        assertEquals("hello", pojo2.s0);
    }

    public static class Demo2 {
        int i1;
        String s1;

        public Demo2(int a0, String a1) {
            this.i1 = a0;
            this.s1 = a1;
        }
    }

    public static class Demo3a extends Demo2 {
        public Demo3a(String a1) {
            super(10, a1);
        }
    }

    public static class Demo3b extends Demo2 {
        public Demo3b(String a1) {
            super(20, a1);
        }
    }

    public static class Demo3c extends Demo2 {
        public Demo3c(String a1) {
            super(30, a1);
        }
    }

    public static <T extends Demo2> T createDemo3(String a1, Class<T> clazz)
            throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Constructor<T> c0 = clazz.getDeclaredConstructor(new Class<?>[] { String.class });
        T r = c0.newInstance(a1);
        return r;
    }

    @Test
    public void testDemo3Creation() throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Demo3a d3a = createDemo3("abc", Demo3a.class);
        assertEquals(10, d3a.i1);
        assertEquals("abc", d3a.s1);
        Demo3b d3b = createDemo3("def", Demo3b.class);
        assertEquals(20, d3b.i1);
        assertEquals("def", d3b.s1);
        Demo3c d3c = createDemo3("ghi", Demo3c.class);
        assertEquals(30, d3c.i1);
        assertEquals("ghi", d3c.s1);
        Demo2 d2 = createDemo3("jkl", Demo3a.class);
        assertEquals(10, d2.i1);
        assertEquals("jkl", d2.s1);

        // createDemo3("xxx", String.class); // compile error :)
    }
}
