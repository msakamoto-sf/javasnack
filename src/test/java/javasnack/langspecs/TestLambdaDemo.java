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

import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntSupplier;

import org.testng.annotations.Test;

/**
 * @see http://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
 */
public class TestLambdaDemo {

    @Test
    public void basicLambda() throws Exception {
        Callable<Integer> call0 = () -> 1 + 2 + 3;
        assertEquals(call0.call().intValue(), 6);

        Callable<String> call1 = () -> "Hello";
        assertEquals(call1.call(), "Hello");

        Callable<String> call2 = () -> {
            return "Hello";
        };
        assertEquals(call2.call(), "Hello");

        Function<String, String> f1 = who -> "Hello, " + who;
        assertEquals(f1.apply("Jon"), "Hello, Jon");

        BiFunction<String, Integer, String> repeater1 = (String src, Integer cnt) -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cnt; i++) {
                sb.append(src);
            }
            return sb.toString();
        };
        assertEquals(repeater1.apply("ABC", 3), "ABCABCABC");

        BiFunction<String, Integer, String> repeater2 = (src, cnt) -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cnt; i++) {
                sb.append(src);
            }
            return sb.toString();
        };
        assertEquals(repeater2.apply("ABC", 3), "ABCABCABC");
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
        assertEquals(cycle123a.getAsInt(), 1);
        assertEquals(cycle123a.getAsInt(), 2);
        assertEquals(cycle123a.getAsInt(), 3);
        assertEquals(cycle123a.getAsInt(), 1);
        assertEquals(cycle123a.getAsInt(), 2);
        assertEquals(cycle123a.getAsInt(), 3);
        assertEquals(cycle123b.getAsInt(), 1);
        assertEquals(cycle123b.getAsInt(), 2);
        assertEquals(cycle123b.getAsInt(), 3);
        assertEquals(cycle123b.getAsInt(), 1);
        assertEquals(cycle123b.getAsInt(), 2);
        assertEquals(cycle123b.getAsInt(), 3);
        assertEquals(this.c, 9);
    }
}
