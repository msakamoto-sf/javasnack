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
package javasnack.langspecs;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.testng.annotations.Test;

/**
 * @see http://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
 * @see https://docs.oracle.com/javase/jp/8/docs/api/java/util/function/package-summary.html
 */
public class TestFunctionalInterface {

    @Test
    public void basicSupplier() {
        Supplier<String> sup0 = () -> "Hello, Supplier";
        assertEquals(sup0.get(), "Hello, Supplier");

        Supplier<String> sup1 = new Supplier<String>() {
            private String msg1 = "Hello";
            private String msg2 = "Bonjour";
            int c = 0;

            @Override
            public String get() {
                c++;
                return (c % 2 == 1) ? msg1 : msg2;
            }
        };
        assertEquals(sup1.get(), "Hello");
        assertEquals(sup1.get(), "Bonjour");
        assertEquals(sup1.get(), "Hello");
        assertEquals(sup1.get(), "Bonjour");

        IntSupplier cycle123 = new IntSupplier() {
            int c = 2;

            @Override
            public int getAsInt() {
                c++;
                return (c % 3) + 1;
            }
        };
        assertEquals(cycle123.getAsInt(), 1);
        assertEquals(cycle123.getAsInt(), 2);
        assertEquals(cycle123.getAsInt(), 3);
        assertEquals(cycle123.getAsInt(), 1);
        assertEquals(cycle123.getAsInt(), 2);
        assertEquals(cycle123.getAsInt(), 3);
    }

    @Test
    public void basicFunction() {
        Function<String, String> fi0 = Function.identity();
        assertEquals(fi0.apply("Hello"), "Hello");

        Function<String, String> f1 = who -> "Hello, " + who;
        assertEquals(f1.apply("Jon"), "Hello, Jon");

        Function<String, Integer> strlen = s -> {
            return s.length();
        };
        Function<String, String> strlenis = strlen.andThen(len -> "Length is " + len);
        assertEquals(strlenis.apply("abcdef"), "Length is 6");
        Function<String, Integer> strlen2 = strlen.compose(f1);
        assertEquals(strlen2.apply("Alice").intValue(), "Hello, Alice".length());

        IntFunction<String> repeater1 = cnt -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cnt; i++) {
                sb.append("Hello");
            }
            return sb.toString();
        };
        assertEquals(repeater1.apply(3), "HelloHelloHello");

        BiFunction<String, Integer, String> repeater2 = (src, cnt) -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cnt; i++) {
                sb.append(src);
            }
            return sb.toString();
        };
        assertEquals(repeater2.apply("ABC", 3), "ABCABCABC");
    }

    @Test
    public void basicOperators() {
        UnaryOperator<String> unop0 = s -> "Hello, " + s;
        assertEquals(unop0.apply("Jon"), "Hello, Jon");

        BinaryOperator<Integer> biop0 = (a, b) -> a + b;
        assertEquals(biop0.apply(2, 3).intValue(), 5);

        IntUnaryOperator miby2 = d -> d * 2;
        assertEquals(miby2.applyAsInt(5), 10);

        IntBinaryOperator miby = (d1, d2) -> d1 * d2;
        assertEquals(miby.applyAsInt(2, 3), 6);
    }

    @Test
    public void basicPredicate() {
        Predicate<String> isHello = s -> "Hello".equals(s);
        assertTrue(isHello.test("Hello"));
        assertFalse(isHello.test("aaaa"));
        assertFalse(isHello.test(null));
        Predicate<String> notHello = isHello.negate();
        assertFalse(notHello.test("Hello"));
        assertTrue(notHello.test("aaaa"));
        assertTrue(notHello.test(null));

        IntPredicate biggerThan5 = d -> d > 5;
        assertTrue(biggerThan5.test(6));
        assertFalse(biggerThan5.test(5));
        assertFalse(biggerThan5.test(4));

        IntPredicate divBy3eq0 = d -> (d % 3 == 0);
        assertTrue(divBy3eq0.test(6));
        IntPredicate divBy4eq0 = d -> (d % 4 == 0);
        assertTrue(divBy4eq0.test(8));
        IntPredicate divBy3and4eq0 = divBy3eq0.and(divBy4eq0);
        assertFalse(divBy3and4eq0.test(6));
        assertFalse(divBy3and4eq0.test(8));
        assertTrue(divBy3and4eq0.test(12));
        assertTrue(divBy3and4eq0.test(24));
        IntPredicate divBy3or4eq0 = divBy3eq0.or(divBy4eq0);
        assertTrue(divBy3or4eq0.test(6));
        assertTrue(divBy3or4eq0.test(8));

        BiPredicate<String, Integer> lenmatcher = (s, len) -> s.length() == len;
        assertTrue(lenmatcher.test("abc", 3));
        assertFalse(lenmatcher.test("abcdefg", 1));
    }

    @Test
    public void basicConsumer() {
        List<String> data = new ArrayList<>();
        Consumer<String> hello = s -> data.add("Hello, " + s);
        Consumer<String> helloAndBonjour = hello.andThen(s -> data.add("Bonjour, " + s));
        hello.accept("Bob");
        helloAndBonjour.accept("Jon");
        assertEquals(data.size(), 3);
        assertEquals(data.get(0), "Hello, Bob");
        assertEquals(data.get(1), "Hello, Jon");
        assertEquals(data.get(2), "Bonjour, Jon");

        List<Integer> data2 = new ArrayList<>();
        IntConsumer iadd1 = d -> data2.add(d);
        IntConsumer iadd2 = iadd1.andThen(d -> data2.add(d * 2));
        iadd1.accept(10);
        iadd2.accept(20);
        assertEquals(data2.size(), 3);
        assertEquals(data2.get(0).intValue(), 10);
        assertEquals(data2.get(1).intValue(), 20);
        assertEquals(data2.get(2).intValue(), 40);

        Map<String, Integer> data3 = new HashMap<>();
        BiConsumer<String, Integer> put1 = (k, v) -> data3.put(k, v);
        BiConsumer<String, Integer> put2 = put1.andThen((k, v) -> data3.put(k + "_mul3", v * 3));
        put1.accept("i01", 10);
        put1.accept("i01", 20);
        put2.accept("i02", 30);
        assertEquals(data3.keySet().size(), 3);
        assertEquals(data3.get("i01").intValue(), 20);
        assertEquals(data3.get("i02").intValue(), 30);
        assertEquals(data3.get("i02_mul3").intValue(), 90);
    }
}
