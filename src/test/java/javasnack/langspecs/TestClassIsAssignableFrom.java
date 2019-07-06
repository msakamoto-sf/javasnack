/*
 * Copyright 2015 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestClassIsAssignableFrom {

    class P1 {
    }

    class C1 extends P1 {
    }

    class C1C1 extends C1 {
    }

    class P2 {
    }

    class C2 extends P2 {
    }

    class C2C1 extends C2 {
    }

    interface IP1 {
    }

    interface IC1 extends IP1 {
    }

    class IMPIC1 implements IC1 {
    }

    interface IP2 {
    }

    interface IC2 extends IP2 {
    }

    class IMPIC2 implements IC2 {
    }

    @Test
    public void testIsAssignableFrom() {
        Object o1 = new C1C1();
        Assertions.assertTrue(o1.getClass().isAssignableFrom(C1C1.class));
        Assertions.assertFalse(o1.getClass().isAssignableFrom(C1.class));
        Assertions.assertFalse(o1.getClass().isAssignableFrom(P1.class));
        Assertions.assertFalse(o1.getClass().isAssignableFrom(C2.class));
        Assertions.assertFalse(o1.getClass().isAssignableFrom(C2C1.class));
        Assertions.assertFalse(o1.getClass().isAssignableFrom(P2.class));
        Assertions.assertTrue(C1C1.class.isAssignableFrom(o1.getClass()));
        Assertions.assertTrue(C1.class.isAssignableFrom(o1.getClass()));
        Assertions.assertTrue(P1.class.isAssignableFrom(o1.getClass()));
        Assertions.assertFalse(C2C1.class.isAssignableFrom(o1.getClass()));
        Assertions.assertFalse(C2.class.isAssignableFrom(o1.getClass()));
        Assertions.assertFalse(P2.class.isAssignableFrom(o1.getClass()));

        Object ip1 = new IMPIC1();
        Assertions.assertTrue(ip1.getClass().isAssignableFrom(IMPIC1.class));
        Assertions.assertFalse(ip1.getClass().isAssignableFrom(IP1.class));
        Assertions.assertFalse(ip1.getClass().isAssignableFrom(IC1.class));
        Assertions.assertFalse(ip1.getClass().isAssignableFrom(IP2.class));
        Assertions.assertFalse(ip1.getClass().isAssignableFrom(IC2.class));
        Assertions.assertFalse(ip1.getClass().isAssignableFrom(IMPIC2.class));
        Assertions.assertTrue(IMPIC1.class.isAssignableFrom(ip1.getClass()));
        Assertions.assertTrue(IC1.class.isAssignableFrom(ip1.getClass()));
        Assertions.assertTrue(IP1.class.isAssignableFrom(ip1.getClass()));
        Assertions.assertFalse(IMPIC2.class.isAssignableFrom(ip1.getClass()));
        Assertions.assertFalse(IC2.class.isAssignableFrom(ip1.getClass()));
        Assertions.assertFalse(IP2.class.isAssignableFrom(ip1.getClass()));
    }

    @Test
    public void testInstanceOfOp() {
        // see
        // http://stackoverflow.com/questions/496928/what-is-the-difference-between-instanceof-and-class-isassignablefrom

        Object o1 = new C1C1();
        Assertions.assertTrue(o1 instanceof C1C1);
        // we can't write like:
        // Assertions.assertTrue(o1 instanceof C1C1.class); // syntax error
        // we can't write like:
        // Assertions.assertTrue(o1.getClass() instanceof clazz); // syntax error
        Assertions.assertTrue(o1 instanceof C1);
        Assertions.assertTrue(o1 instanceof P1);
        Assertions.assertFalse(o1 instanceof C2C1);
        Assertions.assertFalse(o1 instanceof C2);
        Assertions.assertFalse(o1 instanceof P2);

        Object ip1 = new IMPIC1();
        Assertions.assertTrue(ip1 instanceof IMPIC1);
        Assertions.assertTrue(ip1 instanceof IC1);
        Assertions.assertTrue(ip1 instanceof IP1);
        Assertions.assertFalse(ip1 instanceof IMPIC2);
        Assertions.assertFalse(ip1 instanceof IC2);
        Assertions.assertFalse(ip1 instanceof IP2);
    }
}
