package javasnack.langspecs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class TestArrayToListToArray {
    /* see-also:
     * 【Java】List型・Array型の変換 - Qiita
     * https://qiita.com/s-nkmr/items/0e58a4555ccce1344a44
     * 
     * Java - Arrays.asList の注意点 - Qiita
     * https://qiita.com/HomMarkHunt/items/c0172bd6c9801c5768d2
     * 
     * 【Java】配列からListへの変換 - Qiita
     * https://qiita.com/OsakaKaiyukan/items/e5afe1f1401877bd26f8
     * 
     * List と 配列の相互変換 - Qiita
     * https://qiita.com/kics/items/a1f002a303298061febf
     * 
     * Converting array to list in Java - Stack Overflow
     * https://stackoverflow.com/questions/2607289/converting-array-to-list-in-java
     * 
     * Converting String array to java.util.List - Stack Overflow
     * https://stackoverflow.com/questions/6026813/converting-string-array-to-java-util-list
     * 
     * arrays - How to convert int[] into List<Integer> in Java? - Stack Overflow
     * https://stackoverflow.com/questions/1073919/how-to-convert-int-into-listinteger-in-java
     * 
     * arrays - How to convert List<Integer> to int[] in Java? - Stack Overflow
     * https://stackoverflow.com/questions/960431/how-to-convert-listinteger-to-int-in-java
     */

    @Test
    public void testPrimitiveConversion() {
        final int[] intarr1 = { 3, 4, 5, };
        // int[] -> List<Integer>
        final List<Integer> intlist1 = Arrays.stream(intarr1).boxed().collect(Collectors.toList());

        // 変換したあとに、元の配列を変更してみる。
        intarr1[0] = 9;

        // 上のやり方なら、コピーされたListになるため元配列の変更は反映されない。
        assertThat(intlist1).hasSize(3);
        assertThat(intlist1.get(0)).isEqualTo(Integer.valueOf(3));
        assertThat(intlist1.get(1)).isEqualTo(Integer.valueOf(4));
        assertThat(intlist1.get(2)).isEqualTo(Integer.valueOf(5));

        // Collectors.toList() の戻り値は immutable かどうか保証されてない。
        //intlist1.add(9);

        // collect先のインスタンスをカスタマイズするには Collectors.toCollection(Supplier) を使う。
        final List<Integer> intlist2 = Arrays.stream(intarr1).boxed().collect(Collectors.toCollection(ArrayList::new));
        intlist2.set(1, 11);
        intlist2.add(10);
        assertThat(intlist2).hasSize(4);
        assertThat(intlist2.get(0)).isEqualTo(Integer.valueOf(9));
        assertThat(intlist2.get(1)).isEqualTo(Integer.valueOf(11));
        assertThat(intlist2.get(2)).isEqualTo(Integer.valueOf(5));
        assertThat(intlist2.get(3)).isEqualTo(Integer.valueOf(10));

        // 確実に immutable list に変換したい場合は Collectors.toUnmodifiableList() を使う。
        final List<Integer> intlist3 = Arrays.stream(intarr1).boxed().collect(Collectors.toUnmodifiableList());
        assertThat(intlist3).hasSize(3);
        assertThat(intlist3.get(0)).isEqualTo(Integer.valueOf(9));
        assertThat(intlist3.get(1)).isEqualTo(Integer.valueOf(4));
        assertThat(intlist3.get(2)).isEqualTo(Integer.valueOf(5));
        try {
            intlist3.add(7);
            fail("Collectors.toUnmodifiableList() collect to immutable list.");
        } catch (UnsupportedOperationException expected) {
            // Collectors.toUnmodifiableList() の結果はimmutable list
        }

        // List<Integer> -> int[]
        final int[] intarr2 = intlist2.stream().mapToInt(Integer::intValue).toArray();
        // 変換後に元のListを変更してみる。
        intlist2.set(2, 8);
        intlist2.add(12);
        // 元のListへの変更は反映されない。
        assertThat(intarr2).isEqualTo(new int[] { 9, 11, 5, 10 });
    }

    @Test
    public void testNonPrimitiveConversion() {
        final String[] strarr1 = { "aaa", "bbb", "ccc" };
        // String[] -> List<String>
        final List<String> strlist1 = Arrays.asList(strarr1);

        // 変換したあとに、元の配列を変更してみる。
        strarr1[0] = "xxx";

        assertThat(strlist1).hasSize(3);
        assertThat(strlist1.get(0)).isEqualTo("xxx");
        // -> 変更がListにも反映されている = Listへのラップになってる。
        assertThat(strlist1.get(1)).isEqualTo("bbb");
        assertThat(strlist1.get(2)).isEqualTo("ccc");
        try {
            strlist1.add("ddd");
            fail("Arrays.asList() returns immutable list.");
        } catch (UnsupportedOperationException expected) {
            // Arrays.asList() の結果は固定サイズになっていて追加/削除はできない。
        }

        // Listへの変更は、元の配列にも反映される。
        strlist1.set(0, "yyy");

        // Arrays.asList() の結果を変更しようとするなら、さらに ArrayList() にラップする。
        List<String> strlist2 = new ArrayList<>(strlist1);
        strlist2.add("ddd");
        assertThat(strlist2).isEqualTo(List.of("yyy", "bbb", "ccc", "ddd"));

        // List<String> -> String[] : 第一引数に対象となる型の配列を指定する必要があるが、空っぽの配列でOK.
        final String[] strarr2 = strlist2.toArray(new String[0]);

        // 変換したあとに、元のリストを変更してみる。
        strlist2.set(0, "zzz");
        strlist2.add("eee");

        // 元のListを変更しても、変換後の配列には反映されない。
        assertThat(strarr2).hasSize(4);
        assertThat(strarr2[0]).isEqualTo("yyy");
        assertThat(strarr2[1]).isEqualTo("bbb");
        assertThat(strarr2[2]).isEqualTo("ccc");
        assertThat(strarr2[3]).isEqualTo("ddd");
    }
}
