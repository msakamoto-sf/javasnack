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

package javasnack.functionlambdastream;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.Test;

/* see:
 * http://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html
 * http://www.ne.jp/asahi/hishidama/home/tech/java/methodreference.html
 */
public class TestMethodRefDemo {

    static class WithNoArg {
        public final String s;

        public WithNoArg() {
            s = "hello";
        }

        public static int staticMethod() {
            return 10;
        }

        public String instanceMethod() {
            return s.toUpperCase();
        }
    }

    @Test
    public void basicMethodRefWithNoArg() throws Exception {
        // constructor reference
        final Supplier<WithNoArg> sup1 = WithNoArg::new;
        final WithNoArg o0 = sup1.get();
        assertThat(o0.s).isEqualTo("hello");

        // static method reference
        IntSupplier sup2 = WithNoArg::staticMethod;
        assertThat(sup2.getAsInt()).isEqualTo(10);

        // instance method reference as instance::method
        Supplier<String> sup3 = o0::instanceMethod;
        assertThat(sup3.get()).isEqualTo("HELLO");

        // instance method reference as class::method
        Function<WithNoArg, String> f1 = WithNoArg::instanceMethod;
        assertThat(f1.apply(o0)).isEqualTo("HELLO");
    }

    static class WithUniArg {
        public final String s;

        public WithUniArg(final String s) {
            this.s = s;
        }

        public static int staticMethod(final int a) {
            return a * 2;
        }

        public String instanceMethod(final String a1) {
            return this.s + ":" + a1;
        }
    }

    @Test
    public void basicMethodRefWithUniArg() throws Exception {
        // constructor reference
        final Function<String, WithUniArg> gen1 = WithUniArg::new;
        final WithUniArg o1 = gen1.apply("xxx");
        assertThat(o1.s).isEqualTo("xxx");

        // static method reference
        Function<Integer, Integer> gen2 = WithUniArg::staticMethod;
        assertThat(gen2.apply(10)).isEqualTo(20);

        // instance method reference as instance::method
        UnaryOperator<String> gen3 = o1::instanceMethod;
        assertThat(gen3.apply("yyy")).isEqualTo("xxx:yyy");

        // instance method reference as class::method
        BiFunction<WithUniArg, String, String> gen4 = WithUniArg::instanceMethod;
        assertThat(gen4.apply(o1, "yyy")).isEqualTo("xxx:yyy");
    }

    static class WithBiArg {
        public final String s;
        public final int n;

        public WithBiArg(final String s, final int n) {
            this.s = s;
            this.n = n;
        }

        public static String staticMethod(final String a, final int b) {
            return a.repeat(b);
        }

        public String instanceMethod(final String a1, final String b1) {
            return (this.s + ":" + a1 + ":" + b1).repeat(this.n);
        }
    }

    @Test
    public void basicMethodRefWithBiArg() throws Exception {
        // constructor reference
        final BiFunction<String, Integer, WithBiArg> gen1 = WithBiArg::new;
        final WithBiArg o1 = gen1.apply("xxx", 3);
        assertThat(o1.s).isEqualTo("xxx");
        assertThat(o1.n).isEqualTo(3);

        // static method reference
        BiFunction<String, Integer, String> gen2 = WithBiArg::staticMethod;
        assertThat(gen2.apply("abc", 3)).isEqualTo("abcabcabc");

        // instance method reference as instance::method
        BiFunction<String, String, String> gen3 = o1::instanceMethod;
        assertThat(gen3.apply("aaa", "bbb")).isEqualTo("xxx:aaa:bbb".repeat(3));

        // instance method reference as class::method
        //BiFunction<WithBiArg, String, String, String> gen4 = WithBiArg::instanceMethod;
        //assertThat(gen4.apply(o1, "aaa", "bbb")).isEqualTo("xxx:aaa:bbb".repeat(3));
    }
}
