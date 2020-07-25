package javasnack.ojcp.se8silver;

import static javasnack.ojcp.OjcpTools.assertLines;
import static javasnack.ojcp.OjcpTools.returnFalse;
import static javasnack.ojcp.OjcpTools.returnTrue;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import javasnack.tool.StringWriterPrinter;

public class Test05IfElseTernaryOps {

    @Test
    public void testIfElseDemo() {
        StringWriterPrinter swp = new StringWriterPrinter();
        if (returnTrue(swp, "aaa")) {
            swp.out.println("xxx");
        } else if (returnTrue(swp, "bbb")) {
            swp.out.println("yyy");
        } else {
            swp.out.println("zzz");
        }
        assertLines(swp, "aaa", "xxx");
        swp.clear();

        if (returnFalse(swp, "aaa")) {
            swp.out.println("xxx");
        } else if (returnTrue(swp, "bbb")) {
            swp.out.println("yyy");
        } else {
            swp.out.println("zzz");
        }
        assertLines(swp, "aaa", "bbb", "yyy");
        swp.clear();

        if (returnFalse(swp, "aaa")) {
            swp.out.println("xxx");
        } else if (returnFalse(swp, "bbb")) {
            swp.out.println("yyy");
        } else {
            swp.out.println("zzz");
        }
        assertLines(swp, "aaa", "bbb", "zzz");
        swp.clear();

        int a = 10;
        int b = 10;
        // if()の中身は boolean として評価される必要がある。
        // -> boolean 以外の型で代入演算子を使うと compile error
        //if (a = b) {
        if (a == b) {
            swp.out.println("aaa");
        }
        @SuppressWarnings("unused")
        boolean c = false;
        boolean d = true;
        // 代入演算子だが boolean 型なので compile ok.
        if (c = d) {
            swp.out.println("bbb");
        }
        assertLines(swp, "aaa", "bbb");
    }

    @Test
    public void testTernaryOperators() {
        /* 日本語の "三項演算子" は "Ternary Operator" の直訳。
         * 他に Conditional Operator という表現もある。
         * ref: https://docs.oracle.com/javase/tutorial/java/nutsandbolts/op2.html
         */
        StringWriterPrinter swp = new StringWriterPrinter();
        final boolean b1 = returnTrue(swp, "aaa") ? returnTrue(swp, "bbb") : returnFalse(swp, "ccc");
        assertThat(b1).isTrue();
        assertLines(swp, "aaa", "bbb");
        swp.clear();
        final boolean b2 = returnFalse(swp, "aaa") ? returnTrue(swp, "bbb") : returnFalse(swp, "ccc");
        assertThat(b2).isFalse();
        assertLines(swp, "aaa", "ccc");
        swp.clear();

        // 多段の三項演算子デモ
        final boolean b3 = returnTrue(swp, "aaa")
                ? returnTrue(swp, "bbb") ? returnTrue(swp, "ccc") : returnFalse(swp, "ddd")
                : returnFalse(swp, "eee");
        assertThat(b3).isTrue();
        assertLines(swp, "aaa", "bbb", "ccc");
        swp.clear();

        final boolean b4 = returnTrue(swp, "aaa")
                ? returnFalse(swp, "bbb") ? returnTrue(swp, "ccc") : returnFalse(swp, "ddd")
                : returnFalse(swp, "eee");
        assertThat(b4).isFalse();
        assertLines(swp, "aaa", "bbb", "ddd");

        /* >#>POINT<#<: 三項演算子を多段でネストする際、
         * それぞれの評価式の型は異なっていてもよい。
         * 全体としての評価式の型は Object 型になり、
         * 各条件判断に応じて、実際に最後に評価された式の型で返される。
         */
        boolean b5a = true; // -> b5b が評価値になる。
        boolean b5b = true;
        boolean b5c = true;
        int i5a = 100;
        int i5b = 200;
        Object x = b5a ? b5b : b5c ? i5a : i5b;
        assertThat(x instanceof Boolean).isTrue();
        assertThat(x).isEqualTo(Boolean.TRUE);
        b5a = false; // -> b5c 以降が評価される。
        Object y = b5a ? b5b : b5c ? i5a : i5b;
        assertThat(y instanceof Integer).isTrue();
        assertThat(y).isEqualTo(100);
    }
}
