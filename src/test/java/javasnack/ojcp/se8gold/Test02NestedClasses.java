package javasnack.ojcp.se8gold;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class Test02NestedClasses {
    /* "The Java(TM) Tutorials" の以下チュートリアルも参照
     * https://docs.oracle.com/javase/tutorial/java/javaOO/nested.html
     * 
     * Nested Class
     * -> Inner Class (non-static)
     * -> Static Nested Class (static)
     */

    private int num1 = 100;
    private static int SNUM1 = 200;

    /* 外側のクラスと同じ名前は使えない
    class Test02NestedClasses {
    }
    static class Test02NestedClasses {
    }
    */

    class InnerClass1 {
        int x = 10;

        int m1(int y) {
            // inner-class からは outer-class の instance/static member を参照できる。
            return x + y + num1 + SNUM1; // outer-class の private member を参照可能
        }

        /* constructor も定義できる。
         * ただし inner class のconstructorについては、実際は java compiler 側で
         * 独自のconstructorを作成する (synthesis) など特殊な処理が加わる。
         * そのため inner class で constructor で特殊なことをしたり、あるいは
         * serialize/deserilize をするのは事前に十分な調査と検証を行うこと。
         * 
         * ref1: https://docs.oracle.com/javase/tutorial/java/javaOO/nested.html#serialization
         * ref2: https://stackoverflow.com/questions/10135910/is-there-a-constructor-associated-with-nested-classes
         * ref3: https://stackoverflow.com/questions/2883181/why-is-an-anonymous-inner-class-containing-nothing-generated-from-this-code
         */
        InnerClass1() {
        }

        InnerClass1(int z) {
            x = z;
        }

        /* inner-class には static field/method を定義できない(compile error)
        static int Y = 30;
        static int m2(int z) {
            return z;
        }
        */
    }

    static class StaticNestedClass1 {
        int x = 10;

        // 普通のクラス同様に constructor も作成できる。
        StaticNestedClass1() {
        }

        StaticNestedClass1(int z) {
            this.x = z;
        }

        int m1(int y) {
            // >#>POINT<#<:
            // static nested class から outer-class の instance member を参照できない(compile error)
            //return x + y + num1 + SNUM1;
            return x + y + SNUM1; // private static member は参照可能
        }

        // static nested class では static field/method を定義できる。
        static int Y = 30;

        static int m2(int z) {
            return Y + z;
        }
    }

    static void referNestedClassFromStaticContext(Test02NestedClasses outer) {
        // InnerClass1 o1a = new InnerClass1(); // compile error
        InnerClass1 o1a = outer.new InnerClass1();
        assertThat(o1a.m1(10)).isEqualTo(o1a.x + 10 + outer.num1 + SNUM1);
        StaticNestedClass1 o2a = new StaticNestedClass1();
        assertThat(o2a.m1(10)).isEqualTo(o2a.x + 10 + SNUM1);
    }

    @Test
    public void testNestedClassDemo1() {
        this.num1 = 100;
        SNUM1 = 200;

        // inner class の instance 生成
        InnerClass1 o1a = new InnerClass1();
        //InnerClass1 o1a = this.new InnerClass1(); // これと同義
        //InnerClass1 o1x = new Test02NestedClasses.InnerClass1(); // これもok.
        assertThat(o1a.m1(20)).isEqualTo(330);

        // outer-class の instance を分けて生成する例
        Test02NestedClasses o0 = new Test02NestedClasses();
        o0.num1 = 200;
        InnerClass1 o1b = o0.new InnerClass1(15);
        // 生成元の outer-class instance の instance field を参照
        assertThat(o1b.m1(20)).isEqualTo(435);

        // static context = 外部からinner/nested static classを生成してみる
        referNestedClassFromStaticContext(o0);

        // outer class の instance field を変更
        this.num1 = 10;
        SNUM1 = 20; // static field も変更
        // -> 即座に反映される。
        assertThat(o1a.m1(20)).isEqualTo(60);
        assertThat(o1b.m1(20)).isEqualTo(255); // これは outer-class instance が別なので、一部そのまま

        StaticNestedClass1 o2a = new StaticNestedClass1();
        StaticNestedClass1 o2b = new Test02NestedClasses.StaticNestedClass1(20);
        SNUM1 = 30;
        assertThat(o2a.m1(30)).isEqualTo(70);
        assertThat(o2b.m1(30)).isEqualTo(80);
        StaticNestedClass1.Y = 40;
        assertThat(StaticNestedClass1.m2(40)).isEqualTo(80);
    }

    class InnerClass2 {
        public int num1 = 20;

        // "shadowing"
        // ref: https://docs.oracle.com/javase/tutorial/java/javaOO/nested.html#shadowing
        String m1(int num1) {
            return "(local arg) num1=" + num1
                    + ", this.num1=" + this.num1
                    + ", outer.this.num1=" + Test02NestedClasses.this.num1;
        }
    }

    @Test
    public void testInnerClassShadowingDemo() {
        this.num1 = 100;
        SNUM1 = 200;

        InnerClass2 o2 = this.new InnerClass2();
        assertThat(o2.m1(5)).isEqualTo("(local arg) num1=5, this.num1=20, outer.this.num1=100");
    }

    // inner class で abstract class 作れる + visibility scope も一通り使える
    public abstract class InnerClass3a {
    }

    protected abstract class InnerClass3b {
    }

    abstract class InnerClass3c {
    }

    private abstract class InnerClass3d {
    }

    // inner class を 同じ outer-class 内の inner-class として extends できる
    class InnerClass3e extends InnerClass3d {
    }

    // inner class として interface も作れる。
    interface InnerInterface1a {
    }

    // static nested な interface も作れる。
    static interface InnerInterface1b {
    }

    // static nested な abstract class も作れる。
    static abstract class InnerClass3f {
    }

    @Test
    public void testLocalClassDemo() {
        this.num1 = 100;
        SNUM1 = 200;

        final int num2 = 10;
        int num3 = 20;

        /* local class の制限
         * - access scope 指定不可
         * - static NG
         * - abstract, final は OK.
         * - outer class の member 参照 OK.
         * - outer scope の final, 実質final (from java8) 参照OK.
         * 
         * ref: https://docs.oracle.com/javase/tutorial/java/javaOO/localclasses.html
         */
        class LocalClass1 {
            //static int SNUM4 = 10; // compile error
            final int num1 = 500; // わざと outer class 側のmember名と衝突させる。
            final int num4;

            LocalClass1(int num4) {
                this.num4 = num4;
            }

            String m1(int num5) {
                // outer class 側のmemberを参照するときの方法(メンバ名が衝突していなければ省略可)
                return "outer.num1=" + Test02NestedClasses.this.num1 // private 参照ok.
                        + ", num1=" + num1 // local class 自身のmember
                        + ", num2=" + num2 // outer scope の final var 参照ok.
                        + ", num3=" + num3 // outer scope の 実質final参照ok.
                        + ", num4=" + num4
                        + ", num5=" + num5
                        + ", SNUM1=" + SNUM1;
            }
        }
        // num3 = 99; // 実質finalを崩す代入を行うと、local classの参照箇所で compile error
        LocalClass1 o1 = new LocalClass1(30);
        assertThat(o1.m1(40)).isEqualTo("outer.num1=100, num1=500, num2=10, num3=20, num4=30, num5=40, SNUM1=200");
    }

    interface SomeInterface1 {
        String hello(String name);
    }

    @Test
    public void testAnonymousClassDemo() {
        this.num1 = 100;
        SNUM1 = 200;

        final String name1 = "aaa";
        String name2 = "bbb";

        /* anonymous (匿名) class の制限
         * - access scope 指定不可
         * - static NG
         * - abstract, final NG
         * - outer class の member 参照 OK.
         * - outer scope の final, 実質final (from java8) 参照OK.
         * - constructoer 定義不可
         * 
         * ref: https://docs.oracle.com/javase/tutorial/java/javaOO/anonymousclasses.html
         */
        SomeInterface1 o1 = new SomeInterface1() {
            @Override
            public String hello(String name) {
                // 外のscopeの(実質)final 変数と outer class のmemberを参照できる。
                return "hello, " + name + ", " + name1 + ", " + name2 + ", " + num1;
            }
        };
        // name2 = "ccc"; // 実質finalを崩す代入を行うと、匿名classの参照箇所で compile error
        assertThat(o1.hello("world")).isEqualTo("hello, world, aaa, bbb, 100");
    }
}
