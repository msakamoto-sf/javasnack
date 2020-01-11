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

package javasnack.functionlambda;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntSupplier;

import org.junit.jupiter.api.Test;

/* see:
 * http://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html
 */
public class TestMethodRefDemo {

    static class RefDemo {
        public static int STATIC_INT = 10;
        public int instanceInt = 20;

        String name;
        int age;

        public RefDemo(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public static int getStaticInt() {
            return STATIC_INT;
        }

        public int getInstanceInt() {
            return this.instanceInt;
        }

        public String whoAreYou() {
            return "My name is " + this.name + ", " + this.age + " years old.";
        }
    }

    @Test
    public void basicMethodRef() throws Exception {
        BiFunction<String, Integer, RefDemo> bif1 = RefDemo::new;
        RefDemo rd1 = bif1.apply("Jon", 10);
        assertThat(rd1.name).isEqualTo("Jon");
        assertThat(rd1.age).isEqualTo(10);

        RefDemo.STATIC_INT = 100;
        rd1.instanceInt = 200;
        IntSupplier sup1 = RefDemo::getStaticInt;
        IntSupplier sup2 = rd1::getInstanceInt;
        assertThat(sup1.getAsInt()).isEqualTo(100);
        assertThat(sup2.getAsInt()).isEqualTo(200);

        Function<RefDemo, String> greeting = RefDemo::whoAreYou;
        assertThat(greeting.apply(rd1)).isEqualTo("My name is Jon, 10 years old.");
    }
}
