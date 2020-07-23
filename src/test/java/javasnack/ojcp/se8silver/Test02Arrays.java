package javasnack.ojcp.se8silver;

import static javasnack.ojcp.OjcpTools.assertLines;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import javasnack.tool.StringWriterPrinter;

@SuppressWarnings({ "rawtypes", "unused" })
public class Test02Arrays {
    @Test
    public void testArrayDefinitions() {
        int[] ar1;
        // 宣言時のbracketの中にサイズ指定すると compile error
        //int[2] ar1;
        // サイズ未指定の配列確保は compile error
        //ar1 = new int[];
        ar1 = new int[3];

        // 配列確保(サイズ未指定) + 初期化
        ar1 = new int[] { 1, 2, 3 };
        assertThat(ar1[0]).isEqualTo(1);
        assertThat(ar1[1]).isEqualTo(2);
        assertThat(ar1[2]).isEqualTo(3);

        // 確保時のサイズと初期化データのサイズが違うと compile error
        //ar1 = new int[5] { 1, 2 };

        // 変数宣言時のみ、{...} による初期化も可能。
        int[] ar2 = { 1, 2, 3 };
        assertThat(ar2[0]).isEqualTo(1);
        assertThat(ar2[1]).isEqualTo(2);
        assertThat(ar2[2]).isEqualTo(3);
        // ar2 = {4, 5, 6}; // 代入時の {...} のみは compile error

        // bracketは変数名の後ろに置いても compile ok.
        int ar3[] = { 1, 2, 3 };
        // 宣言時のbracketの中にサイズ指定すると compile error
        //int ar3[3] = { 1, 2, 3 };
        assertThat(ar3[0]).isEqualTo(1);
        assertThat(ar3[1]).isEqualTo(2);
        assertThat(ar3[2]).isEqualTo(3);

        // bracketが無かったり、型名の前にbracketがあるのはさすがに compile error
        //int ar4 = new int[] { 1, 2, 3 };
        //[]int ar5 = new int[] { 1, 2, 3 };

        // >#>POINT<#< : 配列は参照型として扱われるので、nullを代入できる。
        ar3 = null;
        assertThat(ar3).isNull();
    }

    @Test
    public void testArrayImplicitInitialValues() {
        int[] ar1 = new int[2];
        assertThat(ar1[0]).isEqualTo(0);
        assertThat(ar1[1]).isEqualTo(0);
        long[] ar2 = new long[2];
        assertThat(ar2[0]).isEqualTo(0);
        assertThat(ar2[1]).isEqualTo(0);
        float[] ar3 = new float[2];
        assertThat(ar3[0]).isEqualTo(0.0f);
        assertThat(ar3[1]).isEqualTo(0.0f);
        double[] ar4 = new double[2];
        assertThat(ar4[0]).isEqualTo(0.0);
        assertThat(ar4[1]).isEqualTo(0.0);
        char[] ar5 = new char[2];
        assertThat(ar5[0]).isEqualTo('\u0000');
        assertThat(ar5[1]).isEqualTo('\u0000');
        boolean[] ar6 = new boolean[2];
        assertThat(ar6[0]).isEqualTo(false);
        assertThat(ar6[1]).isEqualTo(false);
        String[] ar7 = new String[2];
        assertThat(ar7[0]).isEqualTo(null);
        assertThat(ar7[1]).isEqualTo(null);
    }

    @Test
    public void testArray2d() {
        StringWriterPrinter swp = new StringWriterPrinter();

        int[][] ar1 = new int[2][3];
        ar1[0][0] = 1;
        ar1[0][2] = 2;
        ar1[1][1] = 3;
        for (int i = 0; i < ar1.length; i++) {
            for (int j = 0; j < ar1[i].length; j++) {
                swp.out.println("[" + i + "][" + j + "]=" + ar1[i][j]);
            }
        }
        assertLines(swp, "[0][0]=1", "[0][1]=0", "[0][2]=2", "[1][0]=0", "[1][1]=3", "[1][2]=0");
        swp.clear();

        /* 1次元配列として宣言した変数に、2次元配列の代入は compile error
        int[] ar2 = {
                { 10, 20, 30 },
                { 40, 50 },
        };
        */
        int[][] ar2 = {
                { 10, 20, 30 },
                // サイズが異る配列を混在できる。
                { 40, 50 },
        };
        for (int i = 0; i < ar2.length; i++) {
            for (int j = 0; j < ar2[i].length; j++) {
                swp.out.println("[" + i + "][" + j + "]=" + ar2[i][j]);
            }
        }
        assertLines(swp, "[0][0]=10", "[0][1]=20", "[0][2]=30", "[1][0]=40", "[1][1]=50");
    }

    @Test
    public void testArrayListDemo() {
        ArrayList<String> al1 = new ArrayList<>();
        al1.add("aaa");
        al1.add("bbb");
        al1.add("ccc");
        al1.add(0, "ddd");
        assertThat(al1.get(0)).isEqualTo("ddd");
        assertThat(al1.get(1)).isEqualTo("aaa");
        assertThat(al1.get(2)).isEqualTo("bbb");
        assertThat(al1.get(3)).isEqualTo("ccc");
        al1.add(2, "eee");
        assertThat(al1.get(0)).isEqualTo("ddd");
        assertThat(al1.get(1)).isEqualTo("aaa");
        assertThat(al1.get(2)).isEqualTo("eee");
        assertThat(al1.get(3)).isEqualTo("bbb");
        assertThat(al1.get(4)).isEqualTo("ccc");
        assertThat(al1.size()).isEqualTo(5);

        // >#>POINT<#<: 範囲外へのget(int)は例外発生。
        assertThatThrownBy(() -> {
            System.out.println(al1.get(6));
        }).isInstanceOf(IndexOutOfBoundsException.class);

        // >#>POINT<#<: 範囲外へのadd(int, val)は例外発生。
        assertThatThrownBy(() -> {
            al1.add(6, "eee");
        }).isInstanceOf(IndexOutOfBoundsException.class);

        // 要素の更新
        al1.set(0, "DDD");
        al1.set(2, "fff");
        assertThat(al1.get(0)).isEqualTo("DDD");
        assertThat(al1.get(1)).isEqualTo("aaa");
        assertThat(al1.get(2)).isEqualTo("fff");
        assertThat(al1.get(3)).isEqualTo("bbb");
        assertThat(al1.get(4)).isEqualTo("ccc");
        // 要素の削除
        al1.remove(1);
        assertThat(al1.size()).isEqualTo(4);
        assertThat(al1.get(0)).isEqualTo("DDD");
        assertThat(al1.get(1)).isEqualTo("fff");
        assertThat(al1.get(2)).isEqualTo("bbb");
        assertThat(al1.get(3)).isEqualTo("ccc");
        al1.remove(3);
        assertThat(al1.size()).isEqualTo(3);
        assertThat(al1.get(0)).isEqualTo("DDD");
        assertThat(al1.get(1)).isEqualTo("fff");
        assertThat(al1.get(2)).isEqualTo("bbb");

        // >#>POINT<#< : ArrayList初期化時にサイズを指定しても、配列とは異なり要素は初期化されない。
        ArrayList<String> al2 = new ArrayList<>(2);
        assertThatThrownBy(() -> {
            System.out.println(al2.get(0));
        }).isInstanceOf(IndexOutOfBoundsException.class);
        // まだサイズは0のまま。
        assertThat(al2.size()).isEqualTo(0);
        al2.add("AA");
        al2.add("BB");
        al2.add("CC"); // 初期指定したサイズを越えても要素を追加できる。
        al2.add("DD");
        assertThat(al2.size()).isEqualTo(4);
        assertThat(al2.get(0)).isEqualTo("AA");
        assertThat(al2.get(1)).isEqualTo("BB");
        assertThat(al2.get(2)).isEqualTo("CC");
        assertThat(al2.get(3)).isEqualTo("DD");
    }

    @Test
    public void testArrayListAutoBoxingDemo() {
        // 下記は全て compile error
        //ArrayList<int> ali1 = new ArrayList<int>();
        //ArrayList<Integer> ali1 = new ArrayList<int>();
        //ArrayList<int> ali1 = new ArrayList<Integer>();

        ArrayList<Integer> ali1 = new ArrayList<Integer>();
        ali1.add(Integer.valueOf(10));
        ali1.add(20);
        assertThat(ali1.size()).isEqualTo(2);
        assertThat(ali1.get(0)).isEqualTo(10);
        assertThat(ali1.get(1)).isEqualTo(20);

        // ArrayList初期化時にサイズを指定しても、配列とは異なり要素は初期化されない。
        // ... のは、数値系のラッパークラスでも同様。
        ArrayList<Integer> ali2 = new ArrayList<>(2);
        assertThatThrownBy(() -> {
            System.out.println(ali2.get(0));
        }).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> {
            System.out.println(ali2.get(1));
        }).isInstanceOf(IndexOutOfBoundsException.class);

        // 以下は compile ok.
        ArrayList alx1 = new ArrayList<String>(); // コンストラクタ側でジェネリクス使用
        // >#>POINT<#<: 以下は compile error (ジェネリクスの中身は非変)
        //ArrayList<Object> alx2 = new ArrayList<String>();
        //List<Object> alx3 = new ArrayList<String>();

        // List でも受けられる。
        List<String> alx4 = new ArrayList<String>();
        alx4.add("hello");
        assertThat(alx4.size()).isEqualTo(1);
        assertThat(alx4.get(0)).isEqualTo("hello");
    }

}
