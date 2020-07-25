package javasnack.ojcp.se8silver;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

public class Test12ApiDemoObject {

    static class C1 {
        public String toString() {
            return "hello";
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testObjectEquals() {
        C1 o1a = new C1();
        C1 o1b = new C1();
        assertThat(o1a == o1b).isFalse();
        assertThat(o1a.equals(o1b)).isFalse();

        String s1 = new String("hello");
        String s2 = new String("hello");
        assertThat(s1 == s2).isFalse();
        assertThat(s1.equals(s2)).isTrue();

        StringBuilder sb1 = new StringBuilder("hello");
        StringBuilder sb2 = new StringBuilder("hello");
        // >#>POINT<#<: StringBuilderは equals() を override していない。
        assertThat(sb1.equals(sb2)).isFalse();
        // -> toString() すれば中身を比較できる。
        assertThat(sb1.toString().equals(sb2.toString())).isTrue();

        /* >#>POINT<#<:
         * Auto Boxing は valueOf() を使う。
         * Integer.valueOf() では -128 から 127 までの範囲ですでにメモリ上に値があればそれを利用する。
        */
        final Integer wi1 = 127;
        final Integer wi2 = 127;
        // -> 同じラッパーオブジェクトを参照する。
        assertThat(wi1 == wi2).isTrue();
        assertThat(wi1.equals(wi2)).isTrue();
        // -> 範囲を越えたので、それぞれ別のラッパーオブジェクトを生成して参照する。
        final Integer wi3 = 128;
        final Integer wi4 = 128;
        assertThat(wi3 == wi4).isFalse();
        assertThat(wi3.equals(wi4)).isTrue();

        // >#>POINT<#<: -128 to 127 の範囲でも、new したものはインスタンスが分かれる。
        final Integer wi5 = new Integer(127);
        assertThat(wi1 == wi5).isFalse();
        assertThat(wi1.equals(wi5)).isTrue();
        // >#>POINT<#<: -128 to 127 の範囲だと、Integer.valueOf() すれば同じインスタンスを使う。
        final Integer wi6 = Integer.valueOf(127);
        assertThat(wi1 == wi6).isTrue();
        assertThat(wi1.equals(wi6)).isTrue();
        assertThat(wi5 == wi6).isFalse();
        // -> 範囲外なので、インスタンスが分かれる。
        // JavaDoc によると頻繁に使う値だとキャッシュすることもある。
        final Integer wi7 = Integer.valueOf(128);
        final Integer wi8 = Integer.valueOf(128);
        assertThat(wi7 == wi8).isFalse();
        assertThat(wi7.equals(wi8)).isTrue();

        final Long wl1 = 127L;
        final Long wl2 = 127L;
        // -> 同じインスタンス
        assertThat(wl1 == wl2).isTrue();
        assertThat(wl1.equals(wl2)).isTrue();
        final Long wl3 = 128L;
        final Long wl4 = 128L;
        // -> インスタンスが分かれる。
        assertThat(wl3 == wl4).isFalse();
        assertThat(wl3.equals(wl4)).isTrue();
        // Long.valueOf() では Integer.valueOf() と同様、同じインスタンスを参照することがある。
        final Long wl5 = Long.valueOf(127L);
        assertThat(wl1 == wl5).isTrue();
        assertThat(wl1.equals(wl5)).isTrue();
        // new するとインスタンスは分かれる。
        final Long wl6 = new Long(127L);
        assertThat(wl1 == wl6).isFalse();
        assertThat(wl1.equals(wl6)).isTrue();
        assertThat(wl5.equals(wl6)).isTrue();
        // JavaDoc によると頻繁に使う値だとキャッシュすることもある。
        final Long wl7 = Long.valueOf(128L);
        assertThat(wl3 == wl7).isFalse();
        assertThat(wl3.equals(wl7)).isTrue();
        final Long wl8 = Long.valueOf(128L);
        assertThat(wl3 == wl8).isFalse();
        assertThat(wl3.equals(wl8)).isTrue();
        assertThat(wl7 == wl8).isFalse();

        // 小数点だと、 -128 から 127 の範囲の再利用は無い。
        final Float wf1 = 127.0f;
        final Float wf2 = 127.0f;
        assertThat(wf1 == wf2).isFalse();
        assertThat(wf1.equals(wf2)).isTrue();
        final Double wd1 = 127.0;
        final Double wd2 = 127.0;
        assertThat(wd1 == wd2).isFalse();
        assertThat(wd1.equals(wd2)).isTrue();

        // >#>POINT<#<: 異なるラッパークラスでの equals() は false になる。
        final Integer wi10 = 10;
        final Long wl10 = 10L;
        final Double wd10 = 10.0;
        assertThat(wi10.equals(wl10)).isFalse();
        assertThat(wi10.equals(wd10)).isFalse();
        assertThat(wl10.equals(wd10)).isFalse();
        // intValue() を使えば、intに揃えることができる。
        assertThat(wi10.intValue() == wl10.intValue()).isTrue();
        assertThat(wl10.intValue() == wd10.intValue()).isTrue();
    }

    @Test
    public void testObjectToString() {
        final C1 o1 = new C1();
        assertThat(o1 + "").isEqualTo("hello");
        assertThat(o1.toString()).isEqualTo("hello");

        final String s1 = "world";
        assertThat(s1.toString()).isEqualTo("world");

        final int[] ints = { 1, 2, 3, 4 };
        assertThat(ints.toString()).startsWith("[I@");

        final String[] strings = { "aa", "bb", "cc" };
        assertThat(strings.toString()).startsWith("[Ljava.lang.String;@");

        final C1[] objects = { new C1(), new C1() };
        assertThat(objects.toString()).startsWith("[Ljavasnack.ojcp.se8silver.Test12ApiDemoObject$C1;@");

        final List<Integer> ints2 = List.of(1, 2, 3);
        assertThat(ints2.toString()).isEqualTo("[1, 2, 3]");

        final List<String> strings2 = List.of("aa", "bb", "cc");
        assertThat(strings2.toString()).isEqualTo("[aa, bb, cc]");

        final List<C1> objects2 = List.of(new C1(), new C1());
        assertThat(objects2.toString()).isEqualTo("[hello, hello]");
    }
}
