package javasnack.ojcp.se8silver;

import static javasnack.ojcp.OjcpTools.assertLines;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import javasnack.tool.StringWriterPrinter;

/* >#>POINT<#<: .java のファイル名と、中身のクラスの組み合わせ:
 * 
 * [1] Test.java で保存, class Foo{} class Bar{} -> コンパイルOK
 *     -> .java のbasenameと同じpublic classが無くても良い。
 *     -> public でない class なら、.java のbasenameと無関係な名前のclassを宣言できる。
 * 
 * [2] Foo.java で保存, public class Foo{} class Bar{} -> コンパイルOK
 * [3] Foo.java で保存, public class Foo{} public class Bar{} -> コンパイルエラー
 *     -> .java のbasenameと同じ public class は一つだけ宣言できる。
 *        (2つ以上はコンパイルエラー)
 * 
 * [4] Foo.java で保存, class Foo{} public class Bar{} -> コンパイルエラー
 *     -> public class 宣言を含めるときは、.javaのbasenameと同じクラス名であること。
 *        (異なる場合はコンパイルエラー)
 */
// public でない class 宣言を複数含めることができる。
// -> .java での class 宣言では public か無印(= 実質 package scope)のみ指定可能
class Test01BasicsSub0 {
}

class Test01BasicsSub1 {
}

//ここのコメントアウトを外すと、public class が複数宣言されるためコンパイルエラーとなる。
//public class Test01BasicsE1 {
//}

// >#>POINT<#<: クラス名(識別子)の先頭や途中には $, _ が使える。
class $Test01Basics_Sub2 {
}

class _Test01Basics$Sub3 {
}

// 識別子の先頭を数字で始めるのはコンパイルエラー。
//class 9Test01BasicsSub4 {
//}

// 少なくともクラス名については日本語で始めてもいいらしい。
class 日本語Test01Basicsおはよう {
}

@SuppressWarnings("unused")
public class Test01Basics {

    @Test
    public void testLiterals() {
        StringWriterPrinter swp = new StringWriterPrinter();
        swp.out.println(11);
        swp.out.println(0b11); // 3
        swp.out.println(011); // 9
        swp.out.println(0x11); // 17
        swp.out.println(0X12); // 18
        swp.out.println(12.34);
        swp.out.println(3e4);
        swp.out.println('A');
        swp.out.println('\u3042'); // 「あ」のUnicodeコードポイント
        swp.out.println("Hello\u3042");
        swp.out.println(true);
        assertLines(swp, "11", "3", "9", "17", "18",
                "12.34", "30000.0", "A", "あ", "Helloあ", "true");
    }

    @Test
    public void testNumericLiteralsWithUnderScore() {
        //float x1 = 3_.1415F; // compile error
        //float x2 = 3._1415F; // compile error
        //long x3 = 999_999_L; // compile error
        //int x4 = _123; // compile error
        int x5 = 12_3; // ok
        //int x6 = 123_; // compile error
        int x7 = 1___2____3; // ok
        //int x8 = 0_x12; // compile error
        //int x9 = 0x_12; // compile error
        int x10 = 0x1_2; // ok
        int x11 = 0_11; // ok (octet)
        /* >#>POINT<#<: 
         * 「リテラルの先頭と末尾、記号の前後は "_" 使用不可(compile error)」
         * (あくまでも「記号」の前後なので、octet表記のための0の直後はOK)
         */

        int x12 = -1;
        //int x13 = --1; // compile error
        int x13 = -(-1);
        int x14 = -x12;
        int x15 = --x12; // これは単なるdecrement演算子
        //x15 = ---x12; // compile error
        int x16 = +1;
        //int x17 = ++1; // compile error
        int x17 = +(+1);
        int x18 = +x17;
        int x19 = ++x18; // これは単なるincrement演算子
        //x19 = +++x18; // compile error
    }

    @Test
    public void testNumericVarInits() {
        byte b1 = 1;
        byte b2 = 2, b3 = 3; // 同じ型であれば "," で複数宣言できる。
        short s1 = 4;
        //short s2 = 5, short s3 = 6; // "," 連結では型指定を挟めない(compile error)
        // @formatter:off
        short s2 = 5; short s3 = 6; // semicolonで区切ればよい。
        // @formatter:on
        int i1 = 10;
        // 整数リテラルがint範囲の場合は、"L/l" を付けなくてもlongへの代入は可能。
        long l1 = 20;
        //long l2 = 1_000_000_000_000;
        // >#>POINT<#<: 整数リテラルがint範囲を超えるとcompile errorになる。
        // -> "L/l" を付けてlong値として認識させればOK.
        long l2 = 1_000_000_000_000L;
        long l3 = 30L, l4 = 40l;

        double d1 = 1.2;
        // float f1 = 1.2;
        // >#>POINT<#<: 小数点リテラルはdouble型(64bit)となるため、
        // より精度の少ないfloat(32bit)にはそのまま代入できない(compile error)
        float f1 = 3.4F, f2 = 5.6f; // -> "F/f" を付けることで float に代入できる。

        StringWriterPrinter swp = new StringWriterPrinter();
        swp.out.println("b1=" + b1);
        swp.out.println("b2=" + b2);
        swp.out.println("b3=" + b3);
        swp.out.println("s1=" + s1);
        swp.out.println("s2=" + s2);
        swp.out.println("s3=" + s3);
        swp.out.println("i1=" + i1);
        swp.out.println("l1=" + l1);
        swp.out.println("l2=" + l2);
        swp.out.println("l3=" + l3);
        swp.out.println("l4=" + l4);
        swp.out.println("d1=" + d1);
        swp.out.println("f1=" + f1);
        swp.out.println("f2=" + f2);
        assertLines(swp,
                "b1=1", "b2=2", "b3=3",
                "s1=4", "s2=5", "s3=6",
                "i1=10",
                "l1=20", "l2=1000000000000", "l3=30", "l4=40",
                "d1=1.2",
                "f1=3.4", "f2=5.6");
    }

    @Test
    public void testFinalDefinitions() {
        StringWriterPrinter swp = new StringWriterPrinter();

        final int i1 = 10;
        //i1 = 20; // final 宣言した変数への再代入は compile error
        swp.out.println("i1=" + i1);

        final int i2; // 初期化無しのfinal 宣言だけなら compile ok
        int i3;
        // >#>POINT<#< : 初期化されていない/前の変数を参照しようとすると compile error
        //swp.out.println("i2=" + i2);
        //swp.out.println("i3=" + i3);
        i2 = 20;
        i3 = 30;
        swp.out.println("i2=" + i2);
        swp.out.println("i3=" + i3);
        assertLines(swp, "i1=10", "i2=20", "i3=30");
    }

    @Test
    public void testNumericTypeMaxMinSize() {
        assertThat(Byte.MAX_VALUE).isEqualTo((byte) 127);
        assertThat(Byte.MIN_VALUE).isEqualTo((byte) -128);
        assertThat(Byte.SIZE).isEqualTo(8);
        assertThat(Short.MAX_VALUE).isEqualTo((short) 32767);
        assertThat(Short.MIN_VALUE).isEqualTo((short) -32768);
        assertThat(Short.SIZE).isEqualTo(16);
        assertThat(Integer.MAX_VALUE).isEqualTo(2_147_483_647);
        assertThat(Integer.MIN_VALUE).isEqualTo(-2_147_483_648);
        assertThat(Integer.SIZE).isEqualTo(32);
        assertThat(Long.MAX_VALUE).isEqualTo(9_223_372_036_854_775_807L);
        assertThat(Long.MIN_VALUE).isEqualTo(-9_223_372_036_854_775_808L);
        assertThat(Long.SIZE).isEqualTo(64);
        assertThat(Float.SIZE).isEqualTo(32);
        assertThat(Double.SIZE).isEqualTo(64);
    }
}
