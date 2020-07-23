package javasnack.ojcp.se8silver;

import static javasnack.ojcp.OjcpTools.assertLines;
import static javasnack.ojcp.OjcpTools.returnFalse;
import static javasnack.ojcp.OjcpTools.returnTrue;
import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import javasnack.tool.StringWriterPrinter;

public class Test03Operators {
    @Test
    public void testArithmeticOperators() {
        assertThat(10 + 3).isEqualTo(13);
        assertThat(10 - 3).isEqualTo(7);
        assertThat(10 * 3).isEqualTo(30);
        assertThat(10 / 3).isEqualTo(3);
        assertThat(10 % 3).isEqualTo(1);

        // 単項演算子の順序は前置が「演算したあと評価」、後置が「評価したあと演算」
        int n1 = 10;
        assertThat(n1++).isEqualTo(10);
        assertThat(n1).isEqualTo(11);
        assertThat(++n1).isEqualTo(12);
        assertThat(n1).isEqualTo(12);

        assertThat(n1--).isEqualTo(12);
        assertThat(n1).isEqualTo(11);
        assertThat(--n1).isEqualTo(10);
        assertThat(n1).isEqualTo(10);

        int n2 = 10 / 3;
        assertThat(n2).isEqualTo(3);
        // 片方が小数点リテラル(double)だと全体がdoubleになるため、intで受けると compile error
        //int n3 = 10 / 3.0;
        double n3 = 10 / 3.0;
        assertThat(n3).isEqualTo(3.333, Offset.offset(0.001));

        // 文字列結合の方が数値演算より優先度高くなる。
        int a = 10;
        int b = 20;
        assertThat("hello" + a).isEqualTo("hello10");
        assertThat("hello" + a + b).isEqualTo("hello1020");
        assertThat("hello" + (a + b)).isEqualTo("hello30");
        assertThat(a + b + "hello").isEqualTo("30hello");
    }

    @Test
    public void testAssignmentOperators() {
        int a = 10;
        int b = 20;
        // >#>POINT<#< : 代入演算子は最終的に値として評価できる。
        int c = (a = b);
        assertThat(a).isEqualTo(20);
        assertThat(c).isEqualTo(20);
        a = 10;
        c = (a += b);
        assertThat(a).isEqualTo(30);
        assertThat(c).isEqualTo(30);
        a = 10;
        c = (a -= b);
        assertThat(a).isEqualTo(-10);
        assertThat(c).isEqualTo(-10);
        a = 10;
        c = (a *= b);
        assertThat(a).isEqualTo(200);
        assertThat(c).isEqualTo(200);
        a = 10;
        c = (b /= a);
        assertThat(a).isEqualTo(10);
        assertThat(b).isEqualTo(2);
        assertThat(c).isEqualTo(2);
        a = 3;
        b = 10;
        c = (b %= a);
        assertThat(a).isEqualTo(3);
        assertThat(b).isEqualTo(1);
        assertThat(c).isEqualTo(1);

        String d = "hello";
        String e = (d += "world");
        assertThat(d).isEqualTo("helloworld");
        assertThat(e).isEqualTo("helloworld");
        /* String 型については += 以外は compile error
        e = (d -= "world");
        e = (d *= "world");
        e = (d /= "world");
        e = (d %= "world");
        */
    }

    @Test
    public void testComparisonOperators() {
        int a = 10;
        int b = 10;
        assertThat(a++ == ++b).isFalse();
        assertThat(a).isEqualTo(11);
        assertThat(b).isEqualTo(11);

        assertThat(a++ > ++b).isFalse(); // 11 > 12
        assertThat(a).isEqualTo(12);
        assertThat(b).isEqualTo(12);

        assertThat(++a > b++).isTrue(); // 13 > 12
        assertThat(a).isEqualTo(13);
        assertThat(b).isEqualTo(13);

        boolean b1 = a++ >= b++;
        assertThat(b1).isTrue(); // 14 >= 14
        assertThat(a).isEqualTo(14);
        assertThat(b).isEqualTo(14);

        assertThat(++a != b++).isTrue(); // 15 != 14
        assertThat(a).isEqualTo(15);
        assertThat(b).isEqualTo(15);

        assertThat(++a <= b++).isFalse(); // 16 <= 15
        assertThat(a).isEqualTo(16);
        assertThat(b).isEqualTo(16);

        assertThat(a++ < ++b).isTrue(); // 16 < 17
        assertThat(a).isEqualTo(17);
        assertThat(b).isEqualTo(17);

        assertThat(a++ != ++b).isTrue(); // 17 != 18
        assertThat(a).isEqualTo(18);
        assertThat(b).isEqualTo(18);
    }

    @Test
    public void testLogicalOperators() {
        StringWriterPrinter swp = new StringWriterPrinter();

        // expr1 && expr2 は、 expr1 が false であれば expr2 を評価しない。
        assertThat(returnTrue(swp, "aaa") && returnTrue(swp, "bbb")).isTrue();
        assertLines(swp, "aaa", "bbb");
        swp.clear();
        assertThat(returnTrue(swp, "aaa") && returnFalse(swp, "bbb")).isFalse();
        assertLines(swp, "aaa", "bbb");
        swp.clear();
        assertThat(returnFalse(swp, "aaa") && returnTrue(swp, "bbb")).isFalse();
        assertLines(swp, "aaa");
        swp.clear();
        assertThat(returnFalse(swp, "aaa") && returnFalse(swp, "bbb")).isFalse();
        assertLines(swp, "aaa");
        swp.clear();

        // >#>POINT<#<:
        // expr1 & expr2 は、 expr1 が false であっても、expr2 を評価する。
        assertThat(returnTrue(swp, "aaa") & returnTrue(swp, "bbb")).isTrue();
        assertLines(swp, "aaa", "bbb");
        swp.clear();
        assertThat(returnTrue(swp, "aaa") & returnFalse(swp, "bbb")).isFalse();
        assertLines(swp, "aaa", "bbb");
        swp.clear();
        assertThat(returnFalse(swp, "aaa") & returnTrue(swp, "bbb")).isFalse();
        assertLines(swp, "aaa", "bbb");
        swp.clear();
        assertThat(returnFalse(swp, "aaa") & returnFalse(swp, "bbb")).isFalse();
        assertLines(swp, "aaa", "bbb");
        swp.clear();

        // expr1 || expr2 は、どちらかが先にtrueとなるまで評価し、それ以降は評価しない。
        assertThat(returnTrue(swp, "aaa") || returnTrue(swp, "bbb")).isTrue();
        assertLines(swp, "aaa");
        swp.clear();
        assertThat(returnTrue(swp, "aaa") || returnFalse(swp, "bbb")).isTrue();
        assertLines(swp, "aaa");
        swp.clear();
        assertThat(returnFalse(swp, "aaa") || returnTrue(swp, "bbb")).isTrue();
        assertLines(swp, "aaa", "bbb");
        swp.clear();
        assertThat(returnFalse(swp, "aaa") || returnFalse(swp, "bbb")).isFalse();
        assertLines(swp, "aaa", "bbb");
        swp.clear();

        // >#>POINT<#<:
        // expr1 | expr2 は、必ず両方とも評価する。
        assertThat(returnTrue(swp, "aaa") | returnTrue(swp, "bbb")).isTrue();
        assertLines(swp, "aaa", "bbb");
        swp.clear();
        assertThat(returnTrue(swp, "aaa") | returnFalse(swp, "bbb")).isTrue();
        assertLines(swp, "aaa", "bbb");
        swp.clear();
        assertThat(returnFalse(swp, "aaa") | returnTrue(swp, "bbb")).isTrue();
        assertLines(swp, "aaa", "bbb");
        swp.clear();
        assertThat(returnFalse(swp, "aaa") | returnFalse(swp, "bbb")).isFalse();
        assertLines(swp, "aaa", "bbb");
        swp.clear();

        // XOR
        assertThat(returnTrue(swp, "aaa") ^ returnTrue(swp, "bbb")).isFalse();
        assertLines(swp, "aaa", "bbb");
        swp.clear();
        assertThat(returnTrue(swp, "aaa") ^ returnFalse(swp, "bbb")).isTrue();
        assertLines(swp, "aaa", "bbb");
        swp.clear();
        assertThat(returnFalse(swp, "aaa") ^ returnTrue(swp, "bbb")).isTrue();
        assertLines(swp, "aaa", "bbb");
        swp.clear();
        assertThat(returnFalse(swp, "aaa") ^ returnFalse(swp, "bbb")).isFalse();
        assertLines(swp, "aaa", "bbb");
        swp.clear();

        // NOT
        assertThat(!returnTrue(swp, "aaa")).isFalse();
        assertLines(swp, "aaa");
        swp.clear();
        assertThat(!returnFalse(swp, "aaa")).isTrue();
        assertLines(swp, "aaa");
        swp.clear();
    }

    @Test
    public void testBitOperators() {
        // Java SE 8 Silver の範囲外っぽいけどざっくり見ておく。
        int a = 0b1100;
        int b = 0b1010;
        int c = a & b;
        assertThat(c).isEqualTo(0b1000);
        c = a | b;
        assertThat(c).isEqualTo(0b1110);
        c = a ^ b; // XOR
        assertThat(c).isEqualTo(0b110);
        c = ~a; // NOT = NOT 0b0000_0000_0000_0000_0000_0000_0000_1100
        assertThat(c).isEqualTo(0b1111_1111_1111_1111_1111_1111_1111_0011); // -13

        a = 0b1100;
        //a << 2; // compile error
        c = a << 2;
        assertThat(c).isEqualTo(0b110000);
        a = 0b1000_0000_1000_0000_1000_0000_1000_0001;
        c = a << 2;
        assertThat(c).isEqualTo(0b00_0000_1000_0000_1000_0000_1000_0001_00);
        //c = a <<< 2; // compile error (not defined)

        // signed right shift
        a = 0b1100;
        //a >> 2; // compile error
        c = a >> 2;
        assertThat(c).isEqualTo(0b11);
        a = 0b1000_0000_1000_0000_1000_0000_1000_0001;
        c = a >> 2; // >#>POINT<#< : 最左bitが補完される = 符号が維持される。
        assertThat(c).isEqualTo(0b111000_0000_1000_0000_1000_0000_1000_00);

        // unsigned right shift
        c = a >>> 2;
        a = 0b1100;
        //a >>> 2; // compile error
        c = a >>> 2;
        assertThat(c).isEqualTo(0b11);
        a = 0b1000_0000_1000_0000_1000_0000_1000_0001;
        c = a >>> 2; // >#>POINT<#< : 最左bitは補完されない = 符号は維持されない。
        assertThat(c).isEqualTo(0b001000_0000_1000_0000_1000_0000_1000_00);
    }
}
