package javasnack.testng1;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

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
        assertEquals(o1.i1a(1, 2), 3);
        assertEquals(o1.i1b(3, 2), 1);

        Bar o2 = new Bar();
        assertEquals(o2.i1a(1, 2), 13);
        // Bar::i1b() -> Bar::m1b()
        assertEquals(o2.i1b(3, 2), 7);

        o1 = o2;
        // Bar::i1a() as Foo -> Bar::i1a()
        assertEquals(o1.i1a(1, 2), 13);
        // Bar::i1b() as Foo -> Bar::i1b() -> Bar::m1b()
        assertEquals(o1.i1b(3, 2), 7);

        Baz o3 = new Baz();
        // Baz::i1a() = Bar::i1a()
        assertEquals(o3.i1a(1, 2), 13);
        // Baz::i1b() = Bar::i1b() -> [[Baz]]::m1b()
        assertEquals(o3.i1b(3, 2), 1);

        o1 = o3;
        // Baz::i1a() as Foo = Bar::i1a()
        assertEquals(o1.i1a(1, 2), 13);
        // Baz::i1b() as Foo = Bar::i1b() -> [[Baz]]::m1b()
        assertEquals(o1.i1b(3, 2), 1);
    }
}
