package javasnack.ojcp.se8gold;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import javasnack.ojcp.se8silver.Test08ClassBasics;
import javasnack.ojcp.se8silver.Test09ClassExtends;
import javasnack.ojcp.se8silver.Test10AbstractsAndInterfaces;
import javasnack.ojcp.se8silver.Test11TypeCastsAndAutoBoxings;

/**
 * class system については Silver でも広くカバーされている。
 * オーソドックスな継承/インターフェイス/抽象クラス/型変換/auto boxing
 * の使い方や特徴についてはSilver 側のサンプルテストコードを参照のこと:
 * {@link Test08ClassBasics}, {@link Test09ClassExtends},
 * {@link Test10AbstractsAndInterfaces}, {@link Test11TypeCastsAndAutoBoxings}
 * 
 * こちらでは Silver でカバーされていない、Gold側の差分に注目したサンプルテストコードにまとめている。
 */
public class Test02ClassSystems {
    static class VarargDemo1 {
        static String vararg1(String hello, int... a) {
            StringBuilder sb = new StringBuilder();
            sb.append("hello, " + hello + ", a[].length=" + a.length);
            for (int i : a) {
                sb.append(", " + i);
            }
            return sb.toString();
        }

        static String vararg2(String... strings) {
            StringBuilder sb = new StringBuilder("strings[].length=" + strings.length);
            for (String s : strings) {
                sb.append(", " + s);
            }
            return sb.toString();
        }

        static String vararg3(int x) {
            return "x=" + x;
        }

        static String vararg3(int... ints) {
            return "ints[].length=" + ints.length;
        }

        // int... と Integer... で overload自体はコンパイルできるが、
        // 呼び出す側で compile error になる。(どちらを呼べばいいか曖昧になるため)
        static String vararg3(Integer... ints) {
            return "Integer[].length=" + ints.length;
        }

        /* これは compile error (引数が varargs 版と重複とみなされる)
        static String vararg3(int[] ints) {
            return "ints[].length=" + ints.length;
        }
        */
        /* これは compile error (引数が varargs 版と重複とみなされる)
        static String vararg3(Integer[] ints) {
            return "ints[].length=" + ints.length;
        }
        */
    }

    // 可変長引数(varargs)のデモ
    // ref: https://docs.oracle.com/javase/8/docs/technotes/guides/language/varargs.html
    @Test
    public void testVarargDemo1() {
        assertThat(VarargDemo1.vararg1("world")).isEqualTo("hello, world, a[].length=0");
        assertThat(VarargDemo1.vararg1("world", 1)).isEqualTo("hello, world, a[].length=1, 1");
        assertThat(VarargDemo1.vararg1("world", 2, 3)).isEqualTo("hello, world, a[].length=2, 2, 3");
        assertThat(VarargDemo1.vararg1("world", 4, 5, 6)).isEqualTo("hello, world, a[].length=3, 4, 5, 6");

        // 可変長引数は普通の配列として渡すこともできる。
        assertThat(VarargDemo1.vararg1("world", new int[] { 7, 8, 9 }))
                .isEqualTo("hello, world, a[].length=3, 7, 8, 9");

        assertThatThrownBy(() -> {
            final int[] ints = null;
            VarargDemo1.vararg1("world", ints);
        }).isInstanceOf(NullPointerException.class);

        assertThat(VarargDemo1.vararg2()).isEqualTo("strings[].length=0");
        assertThat(VarargDemo1.vararg2("aa")).isEqualTo("strings[].length=1, aa");
        String[] strings = { "xx", "yy", "zz" };
        assertThat(VarargDemo1.vararg2(strings)).isEqualTo("strings[].length=3, xx, yy, zz");
        strings = new String[] { null };
        assertThat(VarargDemo1.vararg2(strings)).isEqualTo("strings[].length=1, null");

        assertThatThrownBy(() -> {
            VarargDemo1.vararg2(null);
        }).isInstanceOf(NullPointerException.class);

        assertThat(VarargDemo1.vararg2(null, null, null)).isEqualTo("strings[].length=3, null, null, null");

        // overload の場合は、完全一致が優先される。
        assertThat(VarargDemo1.vararg3(100)).isEqualTo("x=100");
        assertThat(VarargDemo1.vararg3(Integer.valueOf(100))).isEqualTo("x=100");
        // これは、int... と Integer... で曖昧になるせいか compile error
        //assertThat(VarargDemo1.vararg3(100, 200)).isEqualTo("ints[].length=2");
        //assertThat(VarargDemo1.vararg3(Integer.valueOf(100), Integer.valueOf(200))).isEqualTo("Integer[].length=2");
    }

    static abstract class AbstractDemo1 {
        static String hello = "world";

        static String staticMethod1() {
            return "static1";
        }

        // static abstract method は定義できない(visibility は public or protected だけと compile error)
        //public static abstract String staticMethod2();
        abstract String method1();
        // private abstract method は定義できない(visibility は public or protected だけと compile error)
        //private abstract String methodx();
    }

    // abstract は継承して使うものなので、継承を禁止する final 指定不可 : compile error
    //static final abstract class AbstractDemo1a {
    //}

    static class AbstractImplementDemo1 extends AbstractDemo1 {
        static String hello = "WORLD";

        static String staticMethod1() {
            return "STATIC1";
        }

        @Override
        String method1() {
            return "method1";
        }

        // 間違ったメソッドで @Override を使うと compile error で教えてくれる。
        //@Override
        String method2() {
            return "method2";
        }
    }

    @SuppressWarnings("static-access")
    @Test
    public void testAbstractStaticDemo() {
        // 抽象クラスでも static field/method をそのまま呼べる。
        assertThat(AbstractDemo1.hello).isEqualTo("world");
        assertThat(AbstractDemo1.staticMethod1()).isEqualTo("static1");

        AbstractImplementDemo1 o1 = new AbstractImplementDemo1();
        assertThat(o1.method1()).isEqualTo("method1");
        // インスタンス経由で static field/method を参照すると、継承先で再定義したものを参照する。
        assertThat(o1.hello).isEqualTo("WORLD");
        assertThat(o1.staticMethod1()).isEqualTo("STATIC1");
        AbstractDemo1 o2 = o1;
        assertThat(o2.method1()).isEqualTo("method1");
        // 親クラスとして参照すると、static field/method は親クラスの定義を参照する。
        assertThat(o2.hello).isEqualTo("world");
        assertThat(o2.staticMethod1()).isEqualTo("static1");
    }

    // interface も abstract と同様、継承を禁止する final 指定不可 : compile error
    //static final interface SomeInterface0 {
    //}

    static interface SomeInterface1 {
        static int x = 10; // final 扱い

        static String m1() {
            return "hello";
        }
    }

    static interface SomeInterface2 extends SomeInterface1 {
        static int x = 100; // final 扱い

        static String m1() {
            return "HELLO";
        }
    }

    static class SomeClass implements SomeInterface2 {
    }

    @Test
    public void testInterfaceWithStaticFieldMethodDemo() {
        assertThat(SomeInterface1.x).isEqualTo(10);
        //SomeInterface1.x = 11; // final 扱いのため compile error
        assertThat(SomeInterface1.m1()).isEqualTo("hello");
        assertThat(SomeInterface2.x).isEqualTo(100);
        //SomeInterface2.x = 11; // final 扱いのため compile error
        assertThat(SomeInterface2.m1()).isEqualTo("HELLO");

        SomeClass o1 = new SomeClass();
        assertThat(o1.x).isEqualTo(100);
        // interface の static method については、instance 経由の呼び出しは compile error (undefined扱い)
        //assertThat(o1.m1()).isEqualTo("HELLO");
        SomeInterface1 o11 = o1;
        // static field については instance 経由でアクセスでき、どの型で受けたかにより参照先が変わる。
        assertThat(o11.x).isEqualTo(10);
        //assertThat(o11.m1()).isEqualTo("HELLO");
        SomeInterface2 o12 = o1;
        assertThat(o12.x).isEqualTo(100);
        //assertThat(o12.m1()).isEqualTo("HELLO");
    }

    static interface SomeInterface3 {
        String m1();

        default String dm1() {
            return "dm1";
        }

        default String dm2() {
            // default method 内から abstract method を参照できる。
            return m1() + ":dm2";
        }

        /* >#>POINT<#< : 以下のメソッドは default method として override できない。
        default boolean equals(Object o) {
            return true;
        }
        default int hashCode() {
            return 1;
        }
        default String toString() {
            return "";
        }
        */
    }

    static class SomeClass3a implements SomeInterface3 {
        @Override
        public String m1() {
            return "hello";
        }
    }

    // default method を override するデモ
    static class SomeClass3b implements SomeInterface3 {
        @Override
        public String m1() {
            return "HELLO";
        }

        @Override
        public String dm1() {
            return "DM1";
        }

        @Override
        public String dm2() {
            return "DM2";
        }
    }

    @Test
    public void testInterfaceDefaultMethodDemo1() {
        SomeClass3a o1a = new SomeClass3a();
        assertThat(o1a.dm1()).isEqualTo("dm1");
        // default method 中からimplement された abstract method を参照できている。
        assertThat(o1a.dm2()).isEqualTo("hello:dm2");

        SomeClass3b o1b = new SomeClass3b();
        // override された default method が参照される。
        assertThat(o1b.dm1()).isEqualTo("DM1");
        assertThat(o1b.dm2()).isEqualTo("DM2");

        SomeInterface3 o1c = o1b;
        // interface 型で受けても、通常のメソッド同様、実体としてのimplementsクラス側のメソッドを参照する。
        assertThat(o1c.dm1()).isEqualTo("DM1");
        assertThat(o1c.dm2()).isEqualTo("DM2");
    }

    static interface SomeInterface4 {
        default String dm1() {
            return "dm1";
        }
    }

    static interface SomeInterface4a extends SomeInterface4 {
        @Override
        default String dm1() {
            return "dm1a";
        }
    }

    static interface SomeInterface4b extends SomeInterface4 {
        @Override
        default String dm1() {
            return "dm1b";
        }
    }

    // -> ...4a, 4b 両方とも default method を独自にoverrideしている。
    // このため、両方を実装するクラスを作ろうとすると基本的には compile error
    /*
    static class SomeClass4a implements SomeInterface4a, SomeInterface4b {
    }
    */
    // ただし default method を override してあげれば compile ok.
    static class SomeClass4a implements SomeInterface4a, SomeInterface4b {
        @Override
        public String dm1() {
            return "DM1";
        }
    }

    static class SomeClass4b implements SomeInterface4a, SomeInterface4b {
        @Override
        public String dm1() {
            // Interface名.super.(default method) という参照も可能。
            return SomeInterface4a.super.dm1();
        }

        public String m1() {
            // default method の override に限らずこの参照方式は使える。
            return SomeInterface4b.super.dm1();
        }
    }

    static class SomeClass4c implements SomeInterface4 {
        @Override
        public String dm1() {
            return "dm1c";
        }
    }

    // default method を override した実装クラスから継承した場合
    // -> 衝突しない。実装クラス側で override されたものを優先して参照する。
    static class SomeClass4d extends SomeClass4c implements SomeInterface4a {
    }

    @Test
    public void testInterfaceDefaultMethodDemo2() {
        SomeClass4a o1 = new SomeClass4a();
        assertThat(o1.dm1()).isEqualTo("DM1");
        SomeClass4b o2 = new SomeClass4b();
        assertThat(o2.dm1()).isEqualTo("dm1a");
        assertThat(o2.m1()).isEqualTo("dm1b");
        SomeClass4d o3 = new SomeClass4d();
        assertThat(o3.dm1()).isEqualTo("dm1c");
    }

    /* メソッド名・引数は同じでも返り値の型が異なる interface を両方 implement した 
     * class を作ろうとすると compile error 
    static interface SomeInterface5a {
        String dm1();
    }
    
    static interface SomeInterface5b {
        void dm1();
    }
    
    static class SomeClass5 implements SomeInterface5a, SomeInterface5b {
        @Override
        public String dm1() {
        }
        @Override
        public void dm1() {
        }
    }
    */

    static interface SomeInterface5a {
        String dm1();
    }

    static interface SomeInterface5b {
        String dm1();
    }

    static class SomeClass5 implements SomeInterface5a, SomeInterface5b {
        // default メソッドでない、普通の interface method (実質 public abstract) は衝突しても問題ない
        // @Override も不要。
        public String dm1() {
            return "hello";
        }
    }

    @Test
    public void testInterfaceMethodConflictionDemo() {
        SomeClass5 o1 = new SomeClass5();
        assertThat(o1.dm1()).isEqualTo("hello");
    }

    // 微妙なケース
    static class SimpleClassX {
        final String name;

        SimpleClassX(final String name) {
            this.name = name;
        }
    }

    @Test
    public void testSimpleClassEquality() {
        final Object o0a = new Object();
        final Object o0b = o0a;
        assertThat(o0a.equals(o0b)).isTrue();

        final SimpleClassX o1 = new SimpleClassX("hello");
        final SimpleClassX o2 = o1;
        assertThat(o2.equals(o1)).isTrue();
        // -> Object#equals() は参照が一緒なら true

        final SimpleClassX o3 = new SimpleClassX("hello");
        assertThat(o3.equals(o2)).isFalse();
        // 参照が違う && equals() override無しなら false
    }
}
