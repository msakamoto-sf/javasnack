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

import static org.assertj.core.api.Assertions.assertThat;

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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @see http://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
 * @see https://docs.oracle.com/javase/jp/8/docs/api/java/util/function/package-summary.html
 */
public class TestFunctionalInterface {

    @Test
    public void basicSupplier() {
        Supplier<String> sup0 = () -> "Hello, Supplier";
        assertThat(sup0.get()).isEqualTo("Hello, Supplier");

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
        assertThat(sup1.get()).isEqualTo("Hello");
        assertThat(sup1.get()).isEqualTo("Bonjour");
        assertThat(sup1.get()).isEqualTo("Hello");
        assertThat(sup1.get()).isEqualTo("Bonjour");

        IntSupplier cycle123 = new IntSupplier() {
            int c = 2;

            @Override
            public int getAsInt() {
                c++;
                return (c % 3) + 1;
            }
        };
        assertThat(cycle123.getAsInt()).isEqualTo(1);
        assertThat(cycle123.getAsInt()).isEqualTo(2);
        assertThat(cycle123.getAsInt()).isEqualTo(3);
        assertThat(cycle123.getAsInt()).isEqualTo(1);
        assertThat(cycle123.getAsInt()).isEqualTo(2);
        assertThat(cycle123.getAsInt()).isEqualTo(3);
    }

    @Test
    public void basicFunction() {
        Function<String, String> fi0 = Function.identity();
        assertThat(fi0.apply("Hello")).isEqualTo("Hello");

        Function<String, String> f1 = who -> "Hello, " + who;
        assertThat(f1.apply("Jon")).isEqualTo("Hello, Jon");

        Function<String, Integer> strlen = s -> {
            return s.length();
        };
        Function<String, String> strlenis = strlen.andThen(len -> "Length is " + len);
        assertThat(strlenis.apply("abcdef")).isEqualTo("Length is 6");
        Function<String, Integer> strlen2 = strlen.compose(f1);
        assertThat(strlen2.apply("Alice").intValue()).isEqualTo("Hello, Alice".length());

        IntFunction<String> repeater1 = cnt -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cnt; i++) {
                sb.append("Hello");
            }
            return sb.toString();
        };
        assertThat(repeater1.apply(3)).isEqualTo("HelloHelloHello");

        BiFunction<String, Integer, String> repeater2 = (src, cnt) -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cnt; i++) {
                sb.append(src);
            }
            return sb.toString();
        };
        assertThat(repeater2.apply("ABC", 3)).isEqualTo("ABCABCABC");
    }

    @Test
    public void basicOperators() {
        UnaryOperator<String> unop0 = s -> "Hello, " + s;
        assertThat(unop0.apply("Jon")).isEqualTo("Hello, Jon");

        BinaryOperator<Integer> biop0 = (a, b) -> a + b;
        assertThat(biop0.apply(2, 3).intValue()).isEqualTo(5);

        IntUnaryOperator miby2 = d -> d * 2;
        assertThat(miby2.applyAsInt(5)).isEqualTo(10);

        IntBinaryOperator miby = (d1, d2) -> d1 * d2;
        assertThat(miby.applyAsInt(2, 3)).isEqualTo(6);
    }

    @Test
    public void basicPredicate() {
        Predicate<String> isHello = s -> "Hello".equals(s);
        Assertions.assertTrue(isHello.test("Hello"));
        Assertions.assertFalse(isHello.test("aaaa"));
        Assertions.assertFalse(isHello.test(null));
        Predicate<String> notHello = isHello.negate();
        Assertions.assertFalse(notHello.test("Hello"));
        Assertions.assertTrue(notHello.test("aaaa"));
        Assertions.assertTrue(notHello.test(null));

        IntPredicate biggerThan5 = d -> d > 5;
        Assertions.assertTrue(biggerThan5.test(6));
        Assertions.assertFalse(biggerThan5.test(5));
        Assertions.assertFalse(biggerThan5.test(4));

        IntPredicate divBy3eq0 = d -> (d % 3 == 0);
        Assertions.assertTrue(divBy3eq0.test(6));
        IntPredicate divBy4eq0 = d -> (d % 4 == 0);
        Assertions.assertTrue(divBy4eq0.test(8));
        IntPredicate divBy3and4eq0 = divBy3eq0.and(divBy4eq0);
        Assertions.assertFalse(divBy3and4eq0.test(6));
        Assertions.assertFalse(divBy3and4eq0.test(8));
        Assertions.assertTrue(divBy3and4eq0.test(12));
        Assertions.assertTrue(divBy3and4eq0.test(24));
        IntPredicate divBy3or4eq0 = divBy3eq0.or(divBy4eq0);
        Assertions.assertTrue(divBy3or4eq0.test(6));
        Assertions.assertTrue(divBy3or4eq0.test(8));

        BiPredicate<String, Integer> lenmatcher = (s, len) -> s.length() == len;
        Assertions.assertTrue(lenmatcher.test("abc", 3));
        Assertions.assertFalse(lenmatcher.test("abcdefg", 1));
    }

    @Test
    public void basicConsumer() {
        List<String> data = new ArrayList<>();
        Consumer<String> hello = s -> data.add("Hello, " + s);
        Consumer<String> helloAndBonjour = hello.andThen(s -> data.add("Bonjour, " + s));
        hello.accept("Bob");
        helloAndBonjour.accept("Jon");
        assertThat(data).hasSize(3);
        assertThat(data.get(0)).isEqualTo("Hello, Bob");
        assertThat(data.get(1)).isEqualTo("Hello, Jon");
        assertThat(data.get(2)).isEqualTo("Bonjour, Jon");

        List<Integer> data2 = new ArrayList<>();
        IntConsumer iadd1 = d -> data2.add(d);
        IntConsumer iadd2 = iadd1.andThen(d -> data2.add(d * 2));
        iadd1.accept(10);
        iadd2.accept(20);
        assertThat(data2).hasSize(3);
        assertThat(data2.get(0).intValue()).isEqualTo(10);
        assertThat(data2.get(1).intValue()).isEqualTo(20);
        assertThat(data2.get(2).intValue()).isEqualTo(40);

        Map<String, Integer> data3 = new HashMap<>();
        BiConsumer<String, Integer> put1 = (k, v) -> data3.put(k, v);
        BiConsumer<String, Integer> put2 = put1.andThen((k, v) -> data3.put(k + "_mul3", v * 3));
        put1.accept("i01", 10);
        put1.accept("i01", 20);
        put2.accept("i02", 30);
        assertThat(data3.keySet()).hasSize(3);
        assertThat(data3.get("i01").intValue()).isEqualTo(20);
        assertThat(data3.get("i02").intValue()).isEqualTo(30);
        assertThat(data3.get("i02_mul3").intValue()).isEqualTo(90);
    }
}
