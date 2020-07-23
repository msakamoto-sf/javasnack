package javasnack.ojcp.se8silver;

import static javasnack.ojcp.OjcpTools.assertLines;

import org.junit.jupiter.api.Test;

import javasnack.tool.StringWriterPrinter;

public class Test06SwitchDemos {

    @Test
    public void testAcceptableTypes() {
        StringWriterPrinter swp = new StringWriterPrinter();
        byte byte1 = 12;
        switch (byte1) {
        case 12:
            swp.out.println("byte1=12");
        }
        char char1 = 'A';
        switch (char1) {
        case 'A':
            swp.out.println("char1=A");
        }
        short short1 = 34;
        switch (short1) {
        case 34:
            swp.out.println("short1=34");
        }
        int int1 = 56;
        switch (int1) {
        case 56:
            swp.out.println("int1=56");
        }
        String str1 = "hello";
        switch (str1) {
        case "hello":
            swp.out.println("hello");
        }
        assertLines(swp, "byte1=12", "char1=A", "short1=34", "int1=56", "hello");
        swp.clear();

        // >#>POINT<#< : switch では long, float, double, boolean は使えない。
        long long1 = 78;
        //switch (long1) {
        //}
        float float1 = 3.14f;
        //switch (float1) {
        //}
        double double1 = 3.14;
        //switch (double1) {
        //}
        boolean boolean1 = true;
        //switch (boolean1) {
        //}

        // >#>POINT<#< : switch はラッパークラスを受け付けることができる。
        Byte byte2 = Byte.valueOf((byte) 12);
        switch (byte2) {
        case 12:
            swp.out.println("byte2=12");
        }
        Short short2 = Short.valueOf((short) 34);
        switch (short2) {
        case 34:
            swp.out.println("short2=34");
        }
        Integer int2 = Integer.valueOf(56);
        switch (int2) {
        case 56:
            swp.out.println("int2=56");
        }
        assertLines(swp, "byte2=12", "short2=34", "int2=56");
    }

    @Test
    public void testAcceptableExpression() {
        StringWriterPrinter swp = new StringWriterPrinter();

        // >#>POINT<#< : switch()の中で変数宣言すると compile error
        //switch (int n1 = 10) {
        //}

        int n1 = 1;
        // 代入演算子は使える
        switch (n1 += 2) {
        case 3:
            swp.out.println("aaa");
        }
        // 単項演算子を使う場合は評価と演算の順序に注意
        switch (n1++) {
        case 3:
            swp.out.println("bbb");
        }
        // n1 == 4
        switch (++n1) {
        case 5:
            swp.out.println("ccc");
        }
        assertLines(swp, "aaa", "bbb", "ccc");
    }

    @Test
    public void testDefaultUsage() {
        StringWriterPrinter swp = new StringWriterPrinter();
        int n1 = 10;
        switch (n1) {
        default:
            swp.out.println("aaa");
            // >#>POINT<#< : default はどこにでもおけるが、breakのルールも他と同様になる。
        case 1:
            swp.out.println("bbb");
        }
        assertLines(swp, "aaa", "bbb");
        swp.clear();

        switch (n1) {
        default:
            swp.out.println("aaa");
            break; // break があればそこで終了。
        case 1:
            swp.out.println("bbb");
        }
        assertLines(swp, "aaa");
    }
}
