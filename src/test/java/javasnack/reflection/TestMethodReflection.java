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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class TestMethodReflection {
    public static class Base {
        int a;

        public Base(int a) {
            this.a = a;
        }

        public int getA() {
            return a;
        }

        public void none() {
            // nothing to do
        }

        protected int minus(int b) {
            return a - b;
        }

        public int multiply(int b) {
            return a * b;
        }
    }

    public static class Ext extends Base {
        int b;

        public Ext(int a, int b) {
            super(a);
            this.b = b;
        }

        public int plus(int c) {
            return a + b + c;
        }

        @SuppressWarnings("unused")
        private int div() {
            return a / b;
        }
    }

    @Test
    public void testDeclaredMethodInfoAndCallNonPublicMethods()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> c0 = Base.class;
        Base o0 = new Base(10);
        Method[] methods = c0.getDeclaredMethods();
        assertEquals(4, methods.length);
        // (? ?) sort order of getDeclaredMethods() return array is not same between several times.
        // -> convert to map for stripping sort order effection.
        Map<String, Method> n2m = new HashMap<>();
        for (Method m0 : methods) {
            n2m.put(m0.getName(), m0);
        }
        Method m0 = n2m.get("getA");
        assertEquals("public", Modifier.toString(m0.getModifiers()));
        assertEquals(int.class, m0.getReturnType());
        assertArrayEquals(new Class<?>[] {}, m0.getParameterTypes());
        assertEquals(10, (int) m0.invoke(o0));
        m0 = n2m.get("none");
        assertEquals("public", Modifier.toString(m0.getModifiers()));
        assertEquals(void.class, m0.getReturnType());
        assertArrayEquals(new Class<?>[] {}, m0.getParameterTypes());
        m0.invoke(o0);
        m0 = n2m.get("minus");
        // NOTE : we can access & call protected method :)
        assertEquals("protected", Modifier.toString(m0.getModifiers()));
        assertEquals(int.class, m0.getReturnType());
        assertArrayEquals(new Class<?>[] { int.class }, m0.getParameterTypes());
        assertEquals(-10, (int) m0.invoke(o0, 20));
        m0 = n2m.get("multiply");
        assertEquals("public", Modifier.toString(m0.getModifiers()));
        assertEquals(int.class, m0.getReturnType());
        assertArrayEquals(new Class<?>[] { int.class }, m0.getParameterTypes());
        assertEquals(30, (int) m0.invoke(o0, 3));

        c0 = Ext.class;
        Ext o1 = new Ext(30, 10);
        methods = c0.getDeclaredMethods();
        assertEquals(2, methods.length);
        n2m = new HashMap<>();
        for (Method m1 : methods) {
            n2m.put(m1.getName(), m1);
        }
        Method m1 = n2m.get("plus");
        assertEquals("public", Modifier.toString(m1.getModifiers()));
        assertEquals(int.class, m1.getReturnType());
        assertArrayEquals(new Class<?>[] { int.class }, m1.getParameterTypes());
        assertEquals(80, (int) m1.invoke(o1, 40));
        m1 = n2m.get("div");
        assertEquals("private", Modifier.toString(m1.getModifiers()));
        assertEquals(int.class, m1.getReturnType());
        assertArrayEquals(new Class<?>[] {}, m1.getParameterTypes());
        // NOTE we can call private method :)
        m1.setAccessible(true);
        assertEquals(3, (int) m1.invoke(o1));
    }
}
