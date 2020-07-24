package javasnack.ojcp.se8silver;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/* OOP関連は一般的な入門書で一通りは解説されているため、
 * override や this/super の細かいところを主に試す。
 */

// メソッド戻り値型も公開範囲も変化しない、シンプルな override のデモ用クラス
class Test09_Base1 {
    private int privateField = 10;
    int packageField = 20;
    protected int protectedField = 30;
    public int publicField = 40;

    private static int privateStaticField = 11;
    static int packageStaticField = 22;
    protected static int protectedStaticField = 33;
    public static int publicStaticField = 44;

    private int privateMethod(int x, int y) {
        return x + y;
    }

    String packageMethod(String x, String y) {
        return x + y;
    }

    protected int protectedMethod(int n) {
        return n * 2;
    }

    public String publicMethod(String x, int n) {
        return x + n;
    }

    public int usePrivateMembers(int x, int y) {
        return privateMethod(x, y) + privateField;
    }

    public String usePublicMembers(String x, int n) {
        return publicMethod(x, n) + publicField;
    }

    private static int privateStaticMethod(int x, int y) {
        return x + y + 1;
    }

    static String packageStaticMethod(String x, String y) {
        return x + y + "static";
    }

    protected static int protectedStaticMethod(int n) {
        return n * 3;
    }

    public static String publicStaticMethod(String x, int n) {
        return x + n + "static";
    }

    public static int useStaticPrivateMembers(int x, int y) {
        return privateStaticMethod(x, y) + privateStaticField;
    }

    public static String useStaticPublicMembers(String x, int n) {
        return publicStaticMethod(x, n) + publicStaticField;
    }
}

class Test09_Ext1 extends Test09_Base1 {
    // >#>POINT<#<: フィールドメンバはoverrideできない。
    // 実際、以下に @Override アノテーションを付けると compile error
    // ではどうなるかというと、別個のメンバとして扱われ、this/superで参照を使い分ける(後述)
    private int privateField = 100;
    //@Override
    int packageField = 200;
    protected int protectedField = 300;
    public int publicField = 400;

    private static int privateStaticField = 110;
    static int packageStaticField = 220;
    protected static int protectedStaticField = 330;
    public static int publicStaticField = 440;

    // >#>POINT<#<: private メソッドは override できない。
    //@Override
    //private int privateMethod(int x, int y) {
    //    return x + y + 1;
    //}

    @Override
    String packageMethod(String x, String y) {
        return x + y + "package2";
    }

    @Override
    protected int protectedMethod(int n) {
        return n * 3;
    }

    @Override
    public String publicMethod(String x, int n) {
        return x + n + "public2";
    }

    // static である + private なので override できない。
    // @Overrideをコメントアウトすればエラーは消えるが、
    // override できたわけではなく、単にそのクラス自身の static メソッドとなる。
    //@Override
    private static int privateStaticMethod(int x, int y) {
        return x + y + 2;
    }

    // static であるので override できない。
    // @Overrideをコメントアウトすればエラーは消えるが、
    // override できたわけではなく、単にそのクラス自身の static メソッドとなる。

    //@Override
    static String packageStaticMethod(String x, String y) {
        return x + y + "static2";
    }

    //@Override
    protected static int protectedStaticMethod(int n) {
        return n * 4;
    }

    //@Override
    public static String publicStaticMethod(String x, int n) {
        return x + n + "static2";
    }
}

// override で公開範囲を広げることができるデモ + 戻り値の互換性

class Test09_Base2 {
    protected int protectedMethod(int x, int y) {
        return x + y;
    }

    protected List<String> toList(String x, String y, String z) {
        return Arrays.asList(x, y, z);
    }

    public String listToString(String x, String y, String z) {
        String s = "";
        List<String> strings = toList(x, y, z);
        for (String e : strings) {
            s = s + e;
        }
        return s;
    }

    public final int finalMethod(int x, int y) {
        return x + y;
    }
}

class Test09_Ext2 extends Test09_Base2 {
    @Override
    public int protectedMethod(int x, int y) {
        return x + y + 3;
    }

    // >#>POINT<#<: override する場合に、戻り値の型のサブクラスに拡張することができる。
    // (利用者視点でみればsuper class/instanceで扱えるので問題ない)
    @Override
    protected ArrayList<String> toList(String x, String y, String z) {
        return new ArrayList<>(Arrays.asList(x, y, z, x, y, z));
    }

    // >#>POINT<#<: final method は override できない。(compile error)
    /*
    public final int finalMethod(int x, int y) {
        return x + y;
    }
    */
}

final class Test09_Ext2b extends Test09_Base2 {
    // >#>POINT<#< : method override ではアクセス範囲を狭くするoverrideは compile error
    /*
    private int protectedMethod(int x, int y) {
        return x + y + 3;
    }
    
    private static int protectedStaticMethod(int x, int y) {
        return x + y + 4;
    }
    */
}

// >#>POINT<#<: final class は継承できない。
//class Test09_Ext2c extends Test09_Ext2b {
//}

public class Test09ClassExtends {
    @Test
    public void testOverrideDemos1_Basics() {
        Test09_Ext1 o1 = new Test09_Ext1();
        assertThat(o1.packageField).isEqualTo(200);
        assertThat(o1.protectedField).isEqualTo(300);
        assertThat(o1.publicField).isEqualTo(400);
        assertThat(o1.packageMethod("xx", "yy")).isEqualTo("xxyypackage2");
        assertThat(o1.protectedMethod(10)).isEqualTo(30);
        assertThat(o1.publicMethod("aa", 10)).isEqualTo("aa10public2");
        /* >#>POINT<#<: Ext 側で privateMethod() を定義しているが、
         * 実際に使われているのは Base 側のメソッドとフィールド。
         * private は override できないため。
         * コンパイルエラーになっていないのは、単に Ext 側自身の
         * フィールドとメソッドとして処理されているから。
         * (@Overrideを付けると compile error)
         */
        assertThat(o1.usePrivateMembers(10, 20)).isEqualTo(40);

        /* >#>POINT<#<: public method は override したものに切り替わっているが、
         * publicField が Base クラス側の値を参照している。
         * -> usePublicMembers() は Base クラスで定義されているため、
         * this.publicField 参照が Base クラス側のインスタンス変数を参照している。
         */
        assertThat(o1.usePublicMembers("hello", 30)).isEqualTo("hello30public240");

        assertThat(Test09_Ext1.packageStaticField).isEqualTo(220);
        assertThat(Test09_Ext1.protectedStaticField).isEqualTo(330);
        assertThat(Test09_Ext1.publicStaticField).isEqualTo(440);
        assertThat(Test09_Ext1.packageStaticMethod("xx", "yy")).isEqualTo("xxyystatic2");
        assertThat(Test09_Ext1.protectedStaticMethod(10)).isEqualTo(40);
        assertThat(Test09_Ext1.publicStaticMethod("aa", 10)).isEqualTo("aa10static2");
    }

    @Test
    public void testOverrideDemos2_AccessScope() {
        Test09_Ext2 o1 = new Test09_Ext2();
        // いずれも override したメソッドが呼ばれている。
        assertThat(o1.protectedMethod(10, 20)).isEqualTo(33);
        assertThat(o1.listToString("aa", "bb", "cc")).isEqualTo("aabbccaabbcc");
    }

    static class Test09_ThisBase1 {
        public int publicField = 10;

        public int getPublicField() {
            return this.publicField;
        }
    }

    static class Test09_ThisExt1 extends Test09_ThisBase1 {
        // 同じ名前のフィールドがあるとどうなる??
        public int publicField = 20;

        public int getPublicField() {
            return publicField;
        }

        public int getSuperPublicField() {
            return super.publicField;
        }

        public int getPublicFieldSuper() {
            return super.getPublicField();
        }
    }

    @Test
    public void testThisAndSuperAccessDemo() {
        Test09_ThisExt1 o1 = new Test09_ThisExt1();
        // Ext側のフィールドが参照される。
        assertThat(o1.publicField).isEqualTo(20);
        assertThat(o1.getPublicField()).isEqualTo(20);
        // super を明示しているので、Base側のフィールドが参照される。
        assertThat(o1.getSuperPublicField()).isEqualTo(10);
        assertThat(o1.getPublicFieldSuper()).isEqualTo(10);
    }

    static class Test09_ThisConstructorDemo1 {
        final int n;
        final String s;

        Test09_ThisConstructorDemo1(int n, String s) {
            this.n = n;
            this.s = s;
        }

        Test09_ThisConstructorDemo1(int n) {
            // >#>POINT<#<: this() は先頭で呼ばないと compile error
            //System.out.println("test");
            this(n, "hello");
            System.out.println("test");
        }

        Test09_ThisConstructorDemo1() {
            this(10, "hello");
        }
    }

    @Test
    public void testThisConstructorDemo1() {
        Test09_ThisConstructorDemo1 o1 = new Test09_ThisConstructorDemo1();
        assertThat(o1.n).isEqualTo(10);
        assertThat(o1.s).isEqualTo("hello");
        Test09_ThisConstructorDemo1 o2 = new Test09_ThisConstructorDemo1(100);
        assertThat(o2.n).isEqualTo(100);
        assertThat(o2.s).isEqualTo("hello");
        Test09_ThisConstructorDemo1 o3 = new Test09_ThisConstructorDemo1(200, "aaa");
        assertThat(o3.n).isEqualTo(200);
        assertThat(o3.s).isEqualTo("aaa");
    }

    static class Test09_ThisSuperConstructorDemo_Base1 {
        final StringBuilder sb = new StringBuilder();

        Test09_ThisSuperConstructorDemo_Base1() {
            this.sb.append("base1");
        }

        Test09_ThisSuperConstructorDemo_Base1(String s) {
            this.sb.append(s);
        }
    }

    static class Test09_ThisSuperConstructorDemo_Ext1 extends Test09_ThisSuperConstructorDemo_Base1 {
    }

    static class Test09_ThisSuperConstructorDemo_Ext2 extends Test09_ThisSuperConstructorDemo_Base1 {
        Test09_ThisSuperConstructorDemo_Ext2() {
            this.sb.append(",ext2");
        }

        Test09_ThisSuperConstructorDemo_Ext2(String s) {
            this.sb.append(s);
            this.sb.append(",ext2a");
        }

        Test09_ThisSuperConstructorDemo_Ext2(String s, String s2) {
            // >#>POINT<#<: super() は先頭に置かないと compile error
            //this.sb.append(s2);
            //this.sb.append(",ext2b");
            super(s);
            this.sb.append(s2);
            this.sb.append(",ext2b");
        }
    }

    @Test
    public void testThisSuperConstructorDemos() {
        Test09_ThisSuperConstructorDemo_Ext1 o1 = new Test09_ThisSuperConstructorDemo_Ext1();
        // >#>POINT<#< : default constructor から、
        // 暗黙的に super class の default constructor が呼ばれている。
        assertThat(o1.sb.toString()).isEqualTo("base1");

        Test09_ThisSuperConstructorDemo_Ext2 o2a = new Test09_ThisSuperConstructorDemo_Ext2();
        // >#>POINT<#< : 暗黙的に super class の default constructor が呼ばれてから、
        // 継承先クラスの constructor が呼ばれる。
        assertThat(o2a.sb.toString()).isEqualTo("base1,ext2");

        Test09_ThisSuperConstructorDemo_Ext2 o2b = new Test09_ThisSuperConstructorDemo_Ext2("xxx");
        // 暗黙的にsuper()が呼ばれてから引数のある constructor が呼ばれる。
        assertThat(o2b.sb.toString()).isEqualTo("base1xxx,ext2a");

        Test09_ThisSuperConstructorDemo_Ext2 o2c = new Test09_ThisSuperConstructorDemo_Ext2("xx", "yy");
        // 明示的にsuper(String)を呼んでいる。この場合は super()(引数無し)は呼ばれない。
        assertThat(o2c.sb.toString()).isEqualTo("xxyy,ext2b");
        /* >#>POINT<#< : まとめると...
         * 1. 明示的に super class の constructor を選択したい場合だけ super(...) を先頭で呼び出す。
         * 2. それ以外は、super()を書く必要はない。暗黙的に super class の default constructor が呼ばれる。
         */
    }
}
