package javasnack.ojcp.se8silver;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

@SuppressWarnings("unused")
public class Test07Loops {
    @Test
    public void testWhileLoop() {
        StringBuilder sb1 = new StringBuilder();
        int n1 = 5;
        // >#>POINT<#< : while() の中はboolean値として評価されないと compile error
        //while (n1--) {
        //}
        while (n1 > 0) {
            sb1.append(n1);
            n1--;
        }
        assertThat(n1).isEqualTo(0);
        assertThat(sb1.toString()).isEqualTo("54321");

        sb1 = new StringBuilder();
        n1 = 5;
        while (n1-- > 0) {
            // ここに来た段階では、すでに n1 = n1 - 1 された状態
            sb1.append(n1);
        }
        // n1 が 0 のとき、 0 > 0 で false となり、その後に -- が評価されて -1 になる。
        assertThat(n1).isEqualTo(-1);
        assertThat(sb1.toString()).isEqualTo("43210");

        // >#>POINT<#< : while() の中で変数宣言すると compile error
        //while (boolean b1 = true) {
        //}
        sb1 = new StringBuilder();
        List<String> strings = List.of("aa", "bb", "cc");
        Iterator<String> iter = strings.iterator();

        // >#>POINT<#< : boolean 型であればwhile()中で代入演算子を使える。
        // (最終的にboolean値として評価されるので)
        boolean hasNext = true;
        while (hasNext = iter.hasNext()) {
            sb1.append(iter.next());
        }
        assertThat(sb1.toString()).isEqualTo("aabbcc");

        // >#>POINT<#< : 無限ループは書けるが、while(false)は compile error
        //while (true);
        // while (false);
        // while(true) のコメントを外すとここで unreachable code の compile error.
        hasNext = false;
        while (hasNext)
            ; // 変数代入した while(false) は compile ok.
    }

    @Test
    public void testDoWhileLoop() {
        StringBuilder sb1 = new StringBuilder();
        int n1 = 5;
        // >#>POINT<#< : while() の中はboolean値として評価されないと compile error
        //do {
        //} while (n1--);
        do {
            sb1.append(n1);
            n1--;
        } while (n1 > 0);
        assertThat(n1).isEqualTo(0);
        assertThat(sb1.toString()).isEqualTo("54321");

        sb1 = new StringBuilder();
        n1 = 5;
        do {
            sb1.append(n1);
            // n1 = 1 のとき、1 > 0 が true となってから減算されるため、"0"がappend()される。
            // n1 = 0 のとき、0 > 0 が false となってから減算 -> loop後は -1 になる。
        } while (n1-- > 0);
        assertThat(n1).isEqualTo(-1);
        assertThat(sb1.toString()).isEqualTo("543210");

        // do {} だけで while() が無いと compile error
        //do {
        //}

        // while() の後ろに semi-colon が無いと compile error
        //do {
        //} while (true)

        // >#>POINT<#< : while() の中で変数宣言すると compile error
        //do {
        //} while (boolean b1 = true);
        //while (boolean b1 = true) {
        //}
        sb1 = new StringBuilder();
        List<String> strings = List.of("aa", "bb", "cc");
        Iterator<String> iter = strings.iterator();

        // >#>POINT<#< : boolean 型であればwhile()中で代入演算子を使える。
        // (最終的にboolean値として評価されるので)
        boolean hasNext = true;
        do {
            sb1.append(iter.next());
        } while (hasNext = iter.hasNext());
        assertThat(sb1.toString()).isEqualTo("aabbcc");

        // >#>POINT<#< : do-whileの場合は while(false) でも compile ok.
        do {
        } while (false);
        //do {
        //} while (true);
        // while(true) のコメントを外すとここで unreachable code の compile error.
        hasNext = false;
    }

    @Test
    public void testForLoop() {
        StringBuilder sb1 = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb1.append(i);
        }
        // for() の先頭で変数宣言されたスコープはfor()の中だけ。
        //assertThat(i).isEqualTo(6);
        assertThat(sb1.toString()).isEqualTo("01234");

        sb1 = new StringBuilder();
        int i = 0;
        //for () { // for() の中空っぽだと compile error
        // >#>POINT<#< : for () の各項は全て省略できる = while(true)
        for (;;) {
            sb1.append(i);
            i++;
            if (i > 5) {
                break;
            }
        }
        assertThat(i).isEqualTo(6);
        assertThat(sb1.toString()).isEqualTo("012345");
        // for の第一項は宣言済みの変数への代入だけでもよい。
        for (i = 0; i < 5; i++) {
            sb1.append(i);
        }
        assertThat(i).isEqualTo(5);
        assertThat(sb1.toString()).isEqualTo("01234501234");

        // >#>POINT<#< : for の先頭は変数宣言の statement(文) でもよい。
        // -> 変数宣言なので "," で連結できる。
        // -> "," で代入とも混在できるが、いくつかのパターンでは compile error
        // (a) type name = v, name = v, ... になってない
        //for (int j = 0, int k = 0;;) {
        //}
        // (b) 代入, 変数宣言の順は compile error
        //for (i = 1, int j = 1;;) {
        //}
        // (c) i が定義済みなので変数宣言と衝突して compile error
        //for (int j = 1, i = 1;; i++) {
        //}
        i = 0;
        for (int j = 1, k = 1; i < 5; i++) {
            // これはj,kが普通の変数宣言なので compile ok.
        }

        // for() の先頭で、演算子無しの値だけは compile error
        //for (i;;) {
        //}

        sb1 = new StringBuilder();
        int i2 = 0, j2 = 0, k2 = 0, l2 = 0, m2 = 0;
        for (i2 += 1, j2--, k2++, l2 -= 1, m2 += 1; i2 < 5; i2++) {
            // for() の先頭には代入演算子や単項演算子を複数設定できる。
            sb1.append("i2=" + i2 + ",j2=" + j2 + ",");
        }
        assertThat(i2).isEqualTo(5);
        assertThat(j2).isEqualTo(-1);
        assertThat(k2).isEqualTo(1);
        assertThat(l2).isEqualTo(-1);
        assertThat(m2).isEqualTo(1);
        assertThat(sb1.toString()).isEqualTo(
                "i2=1,j2=-1,i2=2,j2=-1,i2=3,j2=-1,i2=4,j2=-1,");

        sb1 = new StringBuilder();
        int i3 = 0, j3 = 0, k3 = 0, l3 = 0, m3 = 0;
        for (; i3 < 5; i3++, j3 = 10, k3 += 1, l3--, m3++) {
            // >#>POINT<#< : for() の先頭は省略できる。
            // for() の最後には代入演算子や単項演算子を複数設定できる。
            sb1.append("i3=" + i3 + ",j3=" + j3 + ",");
        }
        assertThat(i3).isEqualTo(5);
        assertThat(j3).isEqualTo(10);
        assertThat(k3).isEqualTo(5);
        assertThat(l3).isEqualTo(-5);
        assertThat(m3).isEqualTo(5);
        assertThat(sb1.toString()).isEqualTo(
                "i3=0,j3=0,i3=1,j3=10,i3=2,j3=10,i3=3,j3=10,i3=4,j3=10,");

        sb1 = new StringBuilder();
        int i4 = 0, j4 = 0, k4 = 0;
        for (i4++; j4++ < 10; j4++, k4++) {
            /* >#>POINT<#< : for (a ; b ; c) {} の評価順序に注意。
             * -> 実質以下と同じ。
             * [a]
             * while ( [b] ) {
             *   ...
             *   [c]
             * }
             */
            sb1.append("[" + i4 + "|" + j4 + "|" + k4 + "],");
        }
        assertThat(i4).isEqualTo(1);
        assertThat(j4).isEqualTo(11);
        assertThat(k4).isEqualTo(5);
        assertThat(sb1.toString()).isEqualTo(
                "[1|1|0],[1|3|1],[1|5|2],[1|7|3],[1|9|4],");
    }

    @Test
    public void testExpandedForLoop() {
        String s = "";
        int[] ary1 = { 1, 2, 3 };
        for (int i : ary1) {
            s += i;
        }
        assertThat(s).isEqualTo("123");

        s = "";
        for (Integer i : ary1) {
            s += i;
        }
        assertThat(s).isEqualTo("123");

        s = "";
        String[] ary2 = { "aa", "bb", "cc" };
        for (String e : ary2) {
            s += e;
        }
        assertThat(s).isEqualTo("aabbcc");

        s = "";
        List<String> strings = List.of("aa", "bb", "cc");
        for (String e : strings) {
            s += e;
        }
        assertThat(s).isEqualTo("aabbcc");

        String s2 = "";
        // >#>POINT<#< : 拡張for文は変数宣言が必須、省略すると compile error
        //for (s2 : strings) {
        //}
    }

    @Test
    public void testBreakContinueDemo() {
        String s = "";
        int n1 = 0;
        while (n1++ < 20) {
            s += n1;
            if (n1 % 2 == 0) {
                continue;
            }
            s += "*";
            if (n1 > 10) {
                // break 後は while () の条件式を評価しない。
                break;
            }
            s += ":";
        }
        assertThat(n1).isEqualTo(11);
        assertThat(s).isEqualTo("1*:23*:45*:67*:89*:1011*");

        s = "";
        for (n1 = 0; n1 < 10; n1++) {
            s += n1;
            if (n1 % 2 == 0) {
                continue;
            }
            s += "*";
            if (n1 > 5) {
                // break 後は for() の最後の項は実行しない。
                break;
            }
            s += ":";
        }
        assertThat(n1).isEqualTo(7);
        assertThat(s).isEqualTo("01*:23*:45*:67*");
    }

    @Test
    public void testBreakWithLabels() {
        String s = "";
        // nested loop を大域脱出(break)例
        loop1: for (int i = 0; i < 3; i++) {
            s += i + ":";
            for (int j = 0; j < 3; j++) {
                s += j;
                if (i == 1 && j == 1) {
                    s += "*";
                    break loop1;
                }
            }
            s += ".";
        }
        assertThat(s).isEqualTo("0:012.1:01*");

        s = "";
        // 同じラベルを再利用できる。
        // nested loop + continue だとlabelのあるloopに戻ってくる例
        loop1: for (int i = 0; i < 3; i++) {
            s += i + ":";
            for (int j = 0; j < 3; j++) {
                s += j;
                if (i == 1 && j == 1) {
                    s += "*";
                    continue loop1;
                }
            }
            s += ".";
        }
        assertThat(s).isEqualTo("0:012.1:01*2:012.");

        // 任意の場所にlabelを置くだけなら compile ok.
        loop1: if (s.length() > 0) {
            s = "";
        }

        s = "";
        int x = 1, y = 2, z = 3;
        if (x > 0) {
            s += "x";
            if (y > 0) {
                s += "y";
                if (z > 0) {
                    s += "z";
                }
                s += "Y";
            }
            s += "X";
        }
        assertThat(s).isEqualTo("xyzYX");
        s = "";
        // >#>POINT<#< : loop 外でのif文からの continue + label は compile error.
        // ただし break + label は compile ok で大域脱出に使える。
        loop1: if (x > 0) {
            //continue loop1;
            s += "x";
            if (y > 0) {
                s += "y";
                if (z > 0) {
                    s += "z";
                    break loop1;
                }
                s += "Y";
            }
            s += "X";
        }
        assertThat(s).isEqualTo("xyz");

        // nested loop 中で同じlabelは compile error
        /*
        loop1: for (int i = 0; i < 3; i++) {
            loop1: for (int j = 0; j < 3; j++) {
            }
        }
        */

        // 存在しない label への continue/break は compile error
        /*
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                break loopx;
            }
            if (i == 1) {
                continue loopx;
            }
        }
        */

        // 対応しない label への continue/break は compile error
        /*
        loop1: for (int i = 0; i < 3; i++) {
            loop2: for (int j = 0; j < 3; j++) {
            }
            if (i == 0) {
                break loop2;
            }
            if (i == 1) {
                continue loop2;
            }
        }
        */
    }
}
