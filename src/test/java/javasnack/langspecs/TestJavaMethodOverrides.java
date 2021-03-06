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

package javasnack.langspecs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class TestJavaMethodOverrides {

    class Foo {
        private int m1a(int a, int b) {
            return a + b;
        }

        int m1b(int a, int b) {
            return a - b;
        }

        int i1a(int a, int b) {
            return m1a(a, b);
        }

        int i1b(int a, int b) {
            return m1b(a, b);
        }
    }

    class Bar extends Foo {
        /*
         * Bar can NOT override int m1a(int, int).
         */

        int m1b(int a, int b) {
            return a * b;
        }

        int i1a(int a, int b) {
            // return m1a(a, b) + 10;
            return super.m1a(a, b) + 10;
        }

        int i1b(int a, int b) {
            return m1b(a, b) + 1;
        }
    }

    class Baz extends Bar {
        private int m1a(int a, int b) {
            return a * b + 1;
        }

        int m1b(int a, int b) {
            return a - b - 1;
        }

        int i1c(int a, int b) {
            return m1a(a, b);
        }

        int i1d(int a, int b) {
            return m1b(a, b) - 1;
        }
    }

    @Test
    public void testCallOverrideMethods() {
        Foo o1 = new Foo();
        assertThat(o1.i1a(1, 2)).isEqualTo(3);
        assertThat(o1.i1b(3, 2)).isEqualTo(1);

        Bar o2 = new Bar();
        assertThat(o2.i1a(1, 2)).isEqualTo(13);
        // Bar::i1b() -> Bar::m1b()
        assertThat(o2.i1b(3, 2)).isEqualTo(7);

        o1 = o2;
        // Bar::i1a() as Foo -> Bar::i1a()
        assertThat(o1.i1a(1, 2)).isEqualTo(13);
        // Bar::i1b() as Foo -> Bar::i1b() -> Bar::m1b()
        assertThat(o1.i1b(3, 2)).isEqualTo(7);

        Baz o3 = new Baz();
        // Baz::i1a() = Bar::i1a()
        assertThat(o3.i1a(1, 2)).isEqualTo(13);
        // Baz::i1b() = Bar::i1b() -> [[Baz]]::m1b()
        assertThat(o3.i1b(3, 2)).isEqualTo(1);

        o1 = o3;
        // Baz::i1a() as Foo = Bar::i1a()
        assertThat(o1.i1a(1, 2)).isEqualTo(13);
        // Baz::i1b() as Foo = Bar::i1b() -> [[Baz]]::m1b()
        assertThat(o1.i1b(3, 2)).isEqualTo(1);
    }
}
