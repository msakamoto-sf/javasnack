package javasnack.ojcp.se8silver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class Test12ApiDemoArrayUtil {
    @SuppressWarnings("unused")
    @Test
    public void testArrayUtils() {
        final int[] ints = { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 };
        final int[] intd1 = new int[5];
        // ints[2]から3要素を、intd[1] からコピー
        System.arraycopy(ints, 2, intd1, 1, 3);
        assertThat(intd1[0]).isEqualTo(0);
        assertThat(intd1[1]).isEqualTo(30);
        assertThat(intd1[2]).isEqualTo(40);
        assertThat(intd1[3]).isEqualTo(50);
        assertThat(intd1[4]).isEqualTo(0);

        assertThatThrownBy(() -> {
            // コピー元の開始位置が範囲外
            System.arraycopy(ints, 10, intd1, 0, 1);
        }).isInstanceOf(ArrayIndexOutOfBoundsException.class);

        assertThatThrownBy(() -> {
            // 宛先のサイズ以上をコピー
            System.arraycopy(ints, 0, intd1, 0, 6);
        }).isInstanceOf(ArrayIndexOutOfBoundsException.class);

        assertThatThrownBy(() -> {
            // 宛先の開始位置が範囲外
            System.arraycopy(ints, 0, intd1, 5, 3);
        }).isInstanceOf(ArrayIndexOutOfBoundsException.class);

        // >#>POINT<#<: System.arraycopyでは src と dst が同じ配列でもOK.
        System.arraycopy(intd1, 1, intd1, 2, 2);
        assertThat(intd1[0]).isEqualTo(0);
        assertThat(intd1[1]).isEqualTo(30);
        assertThat(intd1[2]).isEqualTo(30);
        assertThat(intd1[3]).isEqualTo(40);
        assertThat(intd1[4]).isEqualTo(0);

        assertThat(ints.getClass().isArray()).isTrue();
        assertThat(Integer.class.isArray()).isFalse();

        final ArrayList<Integer> li1 = new ArrayList<>(List.of(1, 2, 3));
        final Object[] objects = li1.toArray();
        // 実際は...
        final Integer[] ints2 = li1.toArray(new Integer[0]);
        assertThat(ints2[0]).isEqualTo(1);
        assertThat(ints2[1]).isEqualTo(2);
        assertThat(ints2[2]).isEqualTo(3);

        final int[] ints3 = { 5, 1, 4, 2, 3 };
        Arrays.sort(ints3);
        assertThat(ints3[0]).isEqualTo(1);
        assertThat(ints3[1]).isEqualTo(2);
        assertThat(ints3[2]).isEqualTo(3);
        assertThat(ints3[3]).isEqualTo(4);
        assertThat(ints3[4]).isEqualTo(5);
        final String[] strings1 = { "cc", "AA", "bb" };
        Arrays.sort(strings1);
        assertThat(strings1[0]).isEqualTo("AA");
        assertThat(strings1[1]).isEqualTo("bb");
        assertThat(strings1[2]).isEqualTo("cc");
    }

    @Test
    public void testAsListDemo() {
        final String[] arrs = { "aa", "bb", "cc" };
        // >#>POINT<#<: Arrays.asList() の戻り値型は List なので、ArrayListには代入できない。
        //final ArrayList<String> al1 = Arrays.asList(arrs);
        List<String> lstr1 = Arrays.asList(arrs);
        assertThat(lstr1.get(0)).isEqualTo("aa");
        assertThat(lstr1.get(1)).isEqualTo("bb");
        assertThat(lstr1.get(2)).isEqualTo("cc");

        assertThatThrownBy(() -> {
            // >#>POINT<#<: asList() の結果には要素追加できない。
            lstr1.add("dd");
        }).isInstanceOf(UnsupportedOperationException.class);

        assertThatThrownBy(() -> {
            // >#>POINT<#<: asList() の結果は要素削除もできない。
            lstr1.remove(0);
        }).isInstanceOf(UnsupportedOperationException.class);

        // 変更はできるので、immutable というわけでもない。
        lstr1.set(0, "AA");
        assertThat(lstr1.get(0)).isEqualTo("AA");
        assertThat(lstr1.get(1)).isEqualTo("bb");
        assertThat(lstr1.get(2)).isEqualTo("cc");

        // 変更したかったら、new ArrayList() でラップする。
        List<String> lstr2 = new ArrayList<>(Arrays.asList("aa", "bb", "cc"));
        assertThat(lstr2.get(0)).isEqualTo("aa");
        assertThat(lstr2.get(1)).isEqualTo("bb");
        assertThat(lstr2.get(2)).isEqualTo("cc");
        lstr2.add("dd");
        lstr2.add(1, "ee");
        lstr2.remove(2);
        assertThat(lstr2.get(0)).isEqualTo("aa");
        assertThat(lstr2.get(1)).isEqualTo("ee");
        assertThat(lstr2.get(2)).isEqualTo("cc");
        assertThat(lstr2.get(3)).isEqualTo("dd");

        //>#>POINT<#< : primitive型の配列は受け付けられない -> compile error
        //final int[] arri1 = { 10, 20 };
        //final List<Integer> lint1 = Arrays.asList(arri1);
        // -> ラッパー型で指定する。
        final List<Integer> lint1 = Arrays.asList(Integer.valueOf(10), Integer.valueOf(20));
        assertThat(lint1.get(0)).isEqualTo(10);
        assertThat(lint1.get(1)).isEqualTo(20);
        final Integer[] arri2 = { Integer.valueOf(30), Integer.valueOf(40) };
        final List<Integer> lint2 = Arrays.asList(arri2);
        assertThat(lint2.get(0)).isEqualTo(30);
        assertThat(lint2.get(1)).isEqualTo(40);

        // これは auto boxing で変換されるため compile ok.
        final List<Integer> lint3 = Arrays.asList(50, 60);
        assertThat(lint3.get(0)).isEqualTo(50);
        assertThat(lint3.get(1)).isEqualTo(60);
    }
}
