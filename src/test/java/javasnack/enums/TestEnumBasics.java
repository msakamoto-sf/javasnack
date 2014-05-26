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

import static org.testng.Assert.*;

import org.testng.annotations.Test;

/**
 * enum demos by test cases.
 * 
 * @see http://docs.oracle.com/javase/tutorial/java/javaOO/enum.html
 * @see http://www.atmarkit.co.jp/ait/articles/1103/03/news107.html
 * @see http://d.hatena.ne.jp/amachang/20100225/1267114471
 * @see http://d.hatena.ne.jp/ashigeru/20090116/1232128313
 * @see http://d.hatena.ne.jp/ashigeru/20090119/1232365391
 * @author "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
        assertEquals(NumericEnums.valueOf("ONE"), NumericEnums.ONE);

        assertEquals(numericEnumsToInt(NumericEnums.ONE), 1);
        assertEquals(numericEnumsToInt(NumericEnums.TWO), 2);
        assertEquals(numericEnumsToInt(NumericEnums.THREE), 3);
        assertEquals(numericEnumsToInt(NumericEnums.FOUR), 4);
        assertEquals(numericEnumsToInt(NumericEnums.FIVE), 5);
    }

    @Test(expectedExceptions = { java.lang.IllegalArgumentException.class })
    public void errorneousValueOf() {
        assertEquals(NumericEnums.valueOf("one"), NumericEnums.ONE);
    }

    // --------------------------------------------------------
    /*
     * enum with Constructor and public method demo
     */

    enum ConstructableEnums {
        ABC(10, "abc"), DEF(20, "def"), GHI(30, "ghi"), JKL(40, "jkl");
        final int num;
        final String name;

        ConstructableEnums(int _num, String _name) {
            this.num = _num;
            this.name = _name;
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
        assertEquals(ConstructableEnums.valueOf("DEF"), ConstructableEnums.DEF);

        assertEquals(ConstructableEnums.DEF.getContent(),
                "num=[20], name=[def]");
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

        EnumsWithMethod(String _greeting) {
            this.greeting = _greeting;
        }

        abstract String hello(String yourName);

        String getGreeting() {
            return greeting;
        }
    }

    @Test
    public void enumsWithMethod() {
        assertEquals(EnumsWithMethod.MORNING.hello("foo"),
                "Good Morning, foo. zzz...");
        assertEquals(EnumsWithMethod.AFTERNOON.hello("bar"),
                "Good Afternoon, bar. tea or coffee ?");
        assertEquals(EnumsWithMethod.EVENING.hello("baz"),
                "Good Evening, baz. sleep, sleep.");
        assertEquals(EnumsWithMethod.MORNING.getGreeting(), "Good Morning");
        assertEquals(EnumsWithMethod.AFTERNOON.getGreeting(),
                "Good Afternoon, but sleepy...");
        assertEquals(EnumsWithMethod.EVENING.getGreeting(), "Good Evening");
    }

    // --------------------------------------------------------
    /*
     * enum can implements interface method
     */

    interface HelloInterface {
        public String sayHello(String yourName);
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

        EnumWithInterface(String _myName) {
            myName = _myName;
        }
    }

    @Test
    public void enumWithInterface() {
        assertEquals(EnumWithInterface.ME_THEN_YOU.sayHello("abc"),
                "I'm foo, you're abc.");
        assertEquals(EnumWithInterface.YOU_THEN_ME.sayHello("def"),
                "You're def, I'm bar.");
    }
}
