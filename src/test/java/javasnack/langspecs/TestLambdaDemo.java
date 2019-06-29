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

import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntSupplier;

import org.junit.jupiter.api.Test;

/* see:
 * http://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
 */
public class TestLambdaDemo {

    @Test
    public void basicLambda() throws Exception {
        Callable<Integer> call0 = () -> 1 + 2 + 3;
        assertThat(call0.call().intValue()).isEqualTo(6);

        Callable<String> call1 = () -> "Hello";
        assertThat(call1.call()).isEqualTo("Hello");

        Callable<String> call2 = () -> {
            return "Hello";
        };
        assertThat(call2.call()).isEqualTo("Hello");

        Function<String, String> f1 = who -> "Hello, " + who;
        assertThat(f1.apply("Jon")).isEqualTo("Hello, Jon");

        BiFunction<String, Integer, String> repeater1 = (String src, Integer cnt) -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cnt; i++) {
                sb.append(src);
            }
            return sb.toString();
        };
        assertThat(repeater1.apply("ABC", 3)).isEqualTo("ABCABCABC");

        BiFunction<String, Integer, String> repeater2 = (src, cnt) -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cnt; i++) {
                sb.append(src);
            }
            return sb.toString();
        };
        assertThat(repeater2.apply("ABC", 3)).isEqualTo("ABCABCABC");
    }

    int c = 3;

    @Test
    public void lambdaScope() throws Exception {
        IntSupplier cycle123a = new IntSupplier() {
            int c = 2;

            @Override
            public int getAsInt() {
                this.c++;
                return (this.c % 3) + 1;
            }
        };
        IntSupplier cycle123b = () -> (this.c++ % 3) + 1;
        assertThat(cycle123a.getAsInt()).isEqualTo(1);
        assertThat(cycle123a.getAsInt()).isEqualTo(2);
        assertThat(cycle123a.getAsInt()).isEqualTo(3);
        assertThat(cycle123a.getAsInt()).isEqualTo(1);
        assertThat(cycle123a.getAsInt()).isEqualTo(2);
        assertThat(cycle123a.getAsInt()).isEqualTo(3);
        assertThat(cycle123b.getAsInt()).isEqualTo(1);
        assertThat(cycle123b.getAsInt()).isEqualTo(2);
        assertThat(cycle123b.getAsInt()).isEqualTo(3);
        assertThat(cycle123b.getAsInt()).isEqualTo(1);
        assertThat(cycle123b.getAsInt()).isEqualTo(2);
        assertThat(cycle123b.getAsInt()).isEqualTo(3);
        assertThat(this.c).isEqualTo(9);
    }
}
