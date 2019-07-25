/*
 * Copyright 2013 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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

package javasnack.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * @author "Masahiko Sakamoto"(msakamoto-sf, sakamoto.gsyc.3s@gmail.com)
 */
/* enum demos by test cases.
 * 
 * see:
 * http://docs.oracle.com/javase/tutorial/java/javaOO/enum.html
 * http://www.atmarkit.co.jp/ait/articles/1103/03/news107.html
 * http://d.hatena.ne.jp/amachang/20100225/1267114471
 * http://d.hatena.ne.jp/ashigeru/20090116/1232128313
 * http://d.hatena.ne.jp/ashigeru/20090119/1232365391
 */
public class TestEnumBasics {

    // --------------------------------------------------------
    /*
     * Simple enum demo
     */

    enum NumericEnums {
        ONE, TWO, THREE, FOUR, FIVE,
    }

    int numericEnumsToInt(NumericEnums e) {
        switch (e) {
        case ONE:
            return 1;
        case TWO:
            return 2;
        case THREE:
            return 3;
        case FOUR:
            return 4;
        case FIVE:
            return 5;
        default:
            return 0;
        }
    }

    @Test
    public void numericEnums() {
        System.out.println("printing NumericEnums.values():");
        for (NumericEnums e : NumericEnums.values()) {
            System.out.println(e.toString());
        }
        assertEquals(NumericEnums.ONE, NumericEnums.valueOf("ONE"));

        assertEquals(1, numericEnumsToInt(NumericEnums.ONE));
        assertEquals(2, numericEnumsToInt(NumericEnums.TWO));
        assertEquals(3, numericEnumsToInt(NumericEnums.THREE));
        assertEquals(4, numericEnumsToInt(NumericEnums.FOUR));
        assertEquals(5, numericEnumsToInt(NumericEnums.FIVE));
    }

    @Test
    public void errorneousValueOf() {
        assertThrows(IllegalArgumentException.class, () -> {
            NumericEnums.valueOf("one");
        });
    }

    // --------------------------------------------------------
    /*
     * enum with Constructor and public method demo
     */

    enum ConstructableEnums {
        ABC(10, "abc"), DEF(20, "def"), GHI(30, "ghi"), JKL(40, "jkl");
        final int num;
        final String name;

        ConstructableEnums(int num, String name) {
            this.num = num;
            this.name = name;
        }

        String getContent() {
            return String.format("num=[%d], name=[%s]", num, name);
        }
    }

    @Test
    public void constractableEnums() {
        System.out.println("printing ConstructableEnums.values():");
        for (ConstructableEnums e : ConstructableEnums.values()) {
            System.out.println(e.toString());
        }
        assertEquals(ConstructableEnums.DEF, ConstructableEnums.valueOf("DEF"));

        assertEquals("num=[20], name=[def]", ConstructableEnums.DEF.getContent());
    }

    // --------------------------------------------------------
    /*
     * enum with abstract method and method override
     */

    enum EnumsWithMethod {
        MORNING("Good Morning") {
            @Override
            String hello(String yourName) {
                return greeting + ", " + yourName + ". zzz...";
            }
        },
        AFTERNOON("Good Afternoon") {
            @Override
            String hello(String yourName) {
                return greeting + ", " + yourName + ". tea or coffee ?";
            }

            @Override
            String getGreeting() {
                return super.getGreeting() + ", but sleepy...";
            }
        },
        EVENING("Good Evening") {
            @Override
            String hello(String yourName) {
                return greeting + ", " + yourName + ". sleep, sleep.";
            }
        };
        final String greeting;

        EnumsWithMethod(String greeting) {
            this.greeting = greeting;
        }

        abstract String hello(String yourName);

        String getGreeting() {
            return greeting;
        }
    }

    @Test
    public void enumsWithMethod() {
        assertEquals("Good Morning, foo. zzz...", EnumsWithMethod.MORNING.hello("foo"));
        assertEquals("Good Afternoon, bar. tea or coffee ?", EnumsWithMethod.AFTERNOON.hello("bar"));
        assertEquals("Good Evening, baz. sleep, sleep.", EnumsWithMethod.EVENING.hello("baz"));
        assertEquals("Good Morning", EnumsWithMethod.MORNING.getGreeting());
        assertEquals("Good Afternoon, but sleepy...", EnumsWithMethod.AFTERNOON.getGreeting());
        assertEquals("Good Evening", EnumsWithMethod.EVENING.getGreeting());
    }

    // --------------------------------------------------------
    /*
     * enum can implements interface method
     */

    interface HelloInterface {
        String sayHello(String yourName);
    }

    enum EnumWithInterface implements HelloInterface {
        ME_THEN_YOU("foo") {
            public String sayHello(String yourName) {
                return String.format("I'm %s, you're %s.", myName, yourName);
            }
        },
        YOU_THEN_ME("bar") {
            public String sayHello(String yourName) {
                return String.format("You're %s, I'm %s.", yourName, myName);
            }
        };
        final String myName;

        EnumWithInterface(String myName) {
            this.myName = myName;
        }
    }

    @Test
    public void enumWithInterface() {
        assertEquals("I'm foo, you're abc.", EnumWithInterface.ME_THEN_YOU.sayHello("abc"));
        assertEquals("You're def, I'm bar.", EnumWithInterface.YOU_THEN_ME.sayHello("def"));
    }
}
