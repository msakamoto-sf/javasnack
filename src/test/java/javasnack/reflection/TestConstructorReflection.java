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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.testng.Assert;
import org.testng.annotations.Test;

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
        Assert.assertEquals(cntrs.length, 3);
        Constructor<?> cntr = cntrs[0];
        Assert.assertEquals(Modifier.toString(cntr.getModifiers()), "private");
        Assert.assertEquals(cntr.getParameterTypes(), new Class<?>[] { int.class, int.class, String.class });
        cntr = cntrs[1];
        Assert.assertEquals(Modifier.toString(cntr.getModifiers()), "protected");
        Assert.assertEquals(cntr.getParameterTypes(), new Class<?>[] { int.class, String.class });
        cntr = cntrs[2];
        Assert.assertEquals(Modifier.toString(cntr.getModifiers()), "public");
        Assert.assertEquals(cntr.getParameterTypes(), new Class<?>[] { String.class });

        // NOTE : we can call private constructor :)
        Constructor<?> cntr1 = c0.getDeclaredConstructor(int.class, int.class, String.class);
        cntr1.setAccessible(true);
        Demo1 o1 = (Demo1) cntr1.newInstance(1, 2, "hello");
        Assert.assertEquals(o1.val1, 1);
        Assert.assertEquals(o1.val2, 2);
        Assert.assertEquals(o1.s1, "hello");

        // NOTE : we can call protected constructor :)
        cntr1 = c0.getDeclaredConstructor(int.class, String.class);
        Demo1 o2 = (Demo1) cntr1.newInstance(3, "abc");
        Assert.assertEquals(o2.val1, 3);
        Assert.assertEquals(o2.val2, 10);
        Assert.assertEquals(o2.s1, "abc");

        cntr1 = c0.getDeclaredConstructor(String.class);
        Demo1 o3 = (Demo1) cntr1.newInstance("def");
        Assert.assertEquals(o3.val1, 10);
        Assert.assertEquals(o3.val2, 20);
        Assert.assertEquals(o3.s1, "def");
    }
}
