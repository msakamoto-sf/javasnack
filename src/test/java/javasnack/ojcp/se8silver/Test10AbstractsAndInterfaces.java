package javasnack.ojcp.se8silver;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class Test10AbstractsAndInterfaces {

    /* >#>POINT<#<: compile error になる abstract キーワードの場所
    class abstract Demo1{} // class のあとに abstract は compile error
    abstract class Demo2 {
        void abstract m1(); // 戻り値型のあとに abstract は compile error
        abstract void m2() {} // メソッドボディがある abstract は compile error
    }
    */

    static abstract class Base1 {
        abstract protected String m1(String s, int n);
    }

    static abstract class Base2 extends Base1 {
        public String callM1(String s, int n) {
            return m1(s + "xx", n += 3);
        }
    }

    static class Impl1 extends Base2 {
        // >#>POINT<#< : 公開範囲を拡張することができる。
        //private String m1(String s, int n) { // 狭くするのは compile error
        public String m1(String s, int n) {
            return s + n;
        }
    }

    @Test
    public void testAbstractBasics() {
        Impl1 o1 = new Impl1();
        assertThat(o1.callM1("hello", 10)).isEqualTo("helloxx13");
        // Impl1 で参照すれば、m1() は public メソッドとして参照可能
        assertThat(o1.m1("yy", 20)).isEqualTo("yy20");
    }

    interface IBase3 {
        /* >#>POINT<#<: interface でのフィールドは、
         * 全て暗黙的に public static final が付与される。
         * そのため、初期化をしないと compile error.
         */
        //int intv;
        int intv = 10;
        static int sintv = 11;
        // public 以外を指定すると compile error
        //private int intv1 = 10;
        //protected int intv2 = 20;
        //private static sintv3 = 30;
        //protected static sintv4 = 40;

        // >#>POINT<#<: interface でのメソッドは暗黙的に public abstract が強制される。
        int m1(int x, int y);
        //private int m2(int x); // compile error
        //protected int m2(int x); // compile error
        //int m2(int x) {} // compile error

        // >#>POINT<#<: default method は暗黙的に public が強制される。
        default int defaultm1(int x, int y) {
            return x * y + intv;
        }

        // static にはならない。-> 実装クラスの処理を参照できる。
        public default int defaultm2(int x, int y) {
            return this.m1(x, y) + 100;
        }
        /* public 以外を指定すると compile error
        private default void m2b() {
        }
        protected default void m2c() {
        }
         */

        /* >#>POINT<#<: Object#equals(),hashCode(),toString() は
         * default method にできない。(compile error)
        public default boolean equals(Object obj) {
            return true;
        }
        public default int hashCode() {
            return 1;
        }
        public default String toString() {
            return "";
        }
        */
    }

    interface IBase4a {
        String greet();
    }

    interface IBase4b {
        String yourName();
    }

    // >#>POINT<#<: interface は複数のinterfaceを継承できる。
    interface IBase5 extends IBase4a, IBase4b {
        String hobby();
    }

    static abstract class Base3 {
        abstract int age();
    }

    // >#>POINT<#<: extends -> implements の順でないと compile error
    //static class Impl345 implements IBase3, IBase5 extends Base3 {
    static class Impl345 extends Base3 implements IBase3, IBase5 {
        @Override
        public int m1(int x, int y) {
            return x + y;
        }

        @Override
        public String greet() {
            return "hello";
        }

        @Override
        public String yourName() {
            return "abc";
        }

        @Override
        public String hobby() {
            return "cooking";
        }

        @Override
        int age() {
            return 10;
        }
    }

    @Test
    public void testInterfaceImplDemo() {
        // interface のフィールドはfinal なので、再代入は compile error
        //IBase3.intv = 20;
        //IBase3.sintv = 22;

        Impl345 o1 = new Impl345();
        Base3 base3 = o1;
        assertThat(base3.age()).isEqualTo(10);
        IBase3 ibase3 = o1;
        assertThat(ibase3.m1(10, 20)).isEqualTo(30);
        assertThat(ibase3.defaultm1(10, 20)).isEqualTo(210);
        assertThat(ibase3.defaultm2(10, 20)).isEqualTo(130);
        IBase4a base4a = o1;
        assertThat(base4a.greet()).isEqualTo("hello");
        IBase4b base4b = o1;
        assertThat(base4b.yourName()).isEqualTo("abc");
        IBase5 base5 = o1;
        assertThat(base5.greet()).isEqualTo("hello");
        assertThat(base5.yourName()).isEqualTo("abc");
        assertThat(base5.hobby()).isEqualTo("cooking");
    }
}
