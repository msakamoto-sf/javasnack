package javasnack.ojcp.se8silver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class Test04StringOps {
    static {
        System.out.println("System.out.println() no args demo (1)");
        System.out.println();
        System.out.println("System.out.println() no args demo (2)");
    }

    /* >#>POINT<#< : 位置を2つ取るメソッドはほとんどが(start, end).
     * start は開始位置のインデックスそのものだが、
     * end は「end位置の一つ前まで」で統一されている。
     * 
     * >#>POINT<#< : String クラスは immutable.
     * -> concat, replace, trim, substring は元の文字列は変えず、
     * 操作結果を表す新しい String を返す。
     */

    @Test
    public void testStringDemos() {
        final String s1 = "abc";
        assertThat(s1.charAt(0)).isEqualTo('a');
        assertThat(s1.charAt(1)).isEqualTo('b');
        assertThat(s1.charAt(2)).isEqualTo('c');
        assertThatThrownBy(() -> {
            System.out.println(s1.charAt(3));
        }).isInstanceOf(StringIndexOutOfBoundsException.class);

        final String s2 = s1.concat("def");
        assertThat(s1).isEqualTo("abc"); // s1 は変化しない。
        assertThat(s2).isEqualTo("abcdef");

        assertThat(s2.startsWith("abc")).isTrue();
        assertThat(s2.endsWith("def")).isTrue();
        assertThat(s2.startsWith("cd", 2)).isTrue(); // 開始位置を指定可能

        assertThat("abcabc".length()).isEqualTo(6);
        assertThat("abcabc".indexOf('a')).isEqualTo(0);
        assertThat("abcabc".lastIndexOf('a')).isEqualTo(3);
        assertThat("abcabc".indexOf('c')).isEqualTo(2);
        assertThat("abcabc".lastIndexOf('c')).isEqualTo(5);

        String s3a = "abc";
        String s3b = "abc";
        // >#>POINT<#< : newを使わない場合、メモリ上にあれば同じ文字列を参照する。
        assertThat(s3a == s3b).isTrue();
        // new String() でラップすれば、別インスタンスへの参照となる。
        s3a = new String("abc");
        s3b = new String("abc");
        assertThat(s3a == s3b).isFalse();
        assertThat(s3a.equals(s3b)).isTrue();
        // 片方をラップしただけでもやはり別インスタンスへの参照となる。
        s3a = "abc";
        s3b = new String("abc");
        assertThat(s3a == s3b).isFalse();
        assertThat(s3a.equals(s3b)).isTrue();

        assertThat("ABC".equals("abc".toUpperCase())).isTrue();
        assertThat("abc".equals("ABC".toLowerCase())).isTrue();
        assertThat("abc".equalsIgnoreCase("ABC")).isTrue();

        final String s4 = "abcabc";
        final String s5 = s4.replace('a', 'x');
        assertThat(s4).isEqualTo("abcabc"); // s4 は変化しない。
        assertThat(s5).isEqualTo("xbcxbc");
        final String s6 = s4.replace("ab", "xy");
        assertThat(s4).isEqualTo("abcabc"); // s4 は変化しない。
        assertThat(s6).isEqualTo("xycxyc");

        final String s7 = "  abc  ";
        final String s8 = s7.trim();
        assertThat(s7).isEqualTo("  abc  "); // s7 は変化しない。
        assertThat(s8).isEqualTo("abc");

        final String s9 = "hello, world";
        assertThat(s9.substring(0)).isEqualTo(s9);
        assertThat(s9.substring(2)).isEqualTo("llo, world");
        assertThat(s9.substring(2, 8)).isEqualTo("llo, w"); // end(8) の一つ前まで

        final String s10 = new String("null");
        assertThat(s10.equals("null")).isTrue();
        assertThat(s10.equals(null)).isFalse();

        assertThat(" ".isEmpty()).isFalse();
        assertThat("".isEmpty()).isTrue();
        assertThat(" ".isBlank()).isTrue();
        assertThat("".isBlank()).isTrue();

        // コンストラクタが曖昧なため compile error
        //String s11 = new String(null);
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testStringBuilderDemos() {
        // 初期容量は16だが、中身の文字列長はまだ空なので0
        StringBuilder sb1a = new StringBuilder();
        assertThat(sb1a.length()).isEqualTo(0);
        assertThat(sb1a.toString()).isEqualTo(""); // 空文字列扱い
        // 初期容量を指定しても、中身の文字列長はまだ空なので0
        sb1a = new StringBuilder(256);
        assertThat(sb1a.length()).isEqualTo(0);
        assertThat(sb1a.toString()).isEqualTo(""); // 空文字列扱い
        sb1a = new StringBuilder("abc");
        assertThat(sb1a.length()).isEqualTo(3);

        // StringBuilder は length() で長さを取る。size() は無い。
        //sb1a.size();

        StringBuilder sb1b = new StringBuilder("abc");
        // >#>POINT<#< StringBuilder#equals() は中身まではチェックしない。
        assertThat(sb1a.equals(sb1b)).isFalse();
        // 以下は "unlikely-arg-type" warning を生成する。
        assertThat(sb1a.equals(sb1b.toString())).isFalse();
        assertThat(sb1a.toString().equals(sb1b)).isFalse();

        // 両方を toString() することで、文字列として比較できる。
        assertThat(sb1a.toString().equals(sb1b.toString())).isTrue();

        // String と文字列結合すれば toString() される。
        assertThat("" + sb1b).isEqualTo("abc");

        /* >#>POINT<#<
         * StringBuilder は mutable で、文字列を編集する操作では元のデータを変更する。
         * 以下は戻り値で元の StringBuilder 参照を返すので、method-chainを使える。
         * - insert()
         * - append()
         * - reverse()
         * - replace()
         * - delete()
         * - deleteCharAt()
         * 
         * 以下は戻り値がvoid
         * - setCharAt()
         * 
         * 以下はmutableではなく、substring()結果のStringを返す。
         * - substring()
         */
        final StringBuilder sb2 = sb1a.append("def");
        assertThat(sb1a == sb2).isTrue(); // 同じ参照を返す。
        assertThat(sb1a.toString()).isEqualTo("abcdef"); // 元のデータも変更される。
        assertThat(sb2.toString()).isEqualTo("abcdef");
        // append() には start-end を取るものもある。
        final StringBuilder sb2b = new StringBuilder(sb2.toString());
        sb2b.append("0123456789", 3, 7);
        assertThat(sb2b.toString()).isEqualTo("abcdef3456");

        final StringBuilder sb3 = sb1a.reverse();
        assertThat(sb1a == sb3).isTrue(); // 同じ参照を返す。
        assertThat(sb1a.toString()).isEqualTo("fedcba"); // 元のデータも変更される。
        assertThat(sb3.toString()).isEqualTo("fedcba");

        final StringBuilder sb4 = new StringBuilder("abcabc");
        final StringBuilder sb5 = sb4.replace(2, 4, "XYZ");
        assertThat(sb4 == sb5).isTrue(); // 同じ参照を返す。
        assertThat(sb4.toString()).isEqualTo("abXYZbc"); // 元のデータも変更される。
        assertThat(sb5.toString()).isEqualTo("abXYZbc");

        final StringBuilder sb6 = sb4.delete(2, 4);
        assertThat(sb4 == sb6).isTrue(); // 同じ参照を返す。
        assertThat(sb4.toString()).isEqualTo("abZbc"); // 元のデータも変更される。
        assertThat(sb6.toString()).isEqualTo("abZbc");

        final StringBuilder sb7 = sb4.deleteCharAt(1);
        assertThat(sb4 == sb7).isTrue(); // 同じ参照を返す。
        assertThat(sb4.toString()).isEqualTo("aZbc"); // 元のデータも変更される。
        assertThat(sb7.toString()).isEqualTo("aZbc");

        sb4.setCharAt(2, 'W'); // これの戻り値型は void
        assertThat(sb4.toString()).isEqualTo("aZWc");

        final StringBuilder sb8 = sb4.append("ABCDEF", 1, 4);
        assertThat(sb4 == sb8).isTrue(); // 同じ参照を返す。
        assertThat(sb4.toString()).isEqualTo("aZWcBCD"); // 元のデータも変更される。
        assertThat(sb8.toString()).isEqualTo("aZWcBCD");

        assertThat(sb4.substring(2)).isEqualTo("WcBCD");
        assertThat(sb4.substring(2, 5)).isEqualTo("WcB");
        // substring については元データ変更は無し。
        assertThat(sb4.toString()).isEqualTo("aZWcBCD");

        // StringBuilder には以下のメソッドは存在しない。
        /*
        sb4.concat();
        sb4.delete();
        sb4.deleteAll();
        sb4.remove();
        sb4.removeAll();
        */
    }

    @Test
    public void testStringBuilderInsertion() {
        final StringBuilder sb1 = new StringBuilder("abcdef");
        sb1.insert(0, "xy");
        assertThat(sb1.toString()).isEqualTo("xyabcdef");
        sb1.insert(7, "XX");
        assertThat(sb1.toString()).isEqualTo("xyabcdeXXf");
        sb1.insert(10, "YY"); // sb1.length()
        assertThat(sb1.toString()).isEqualTo("xyabcdeXXfYY");
        String s1 = null;
        sb1.insert(0, s1);
        assertThat(sb1.toString()).isEqualTo("nullxyabcdeXXfYY");
        sb1.append(s1);
        assertThat(sb1.toString()).isEqualTo("nullxyabcdeXXfYYnull");
        sb1.insert(1, new char[] { 'A', 'B', 'C', 'D', 'E' }, 1, 3);
        assertThat(sb1.toString()).isEqualTo("nBCDullxyabcdeXXfYYnull");
        sb1.insert(2, "helloworld", 3, 8);
        assertThat(sb1.toString()).isEqualTo("nBloworCDullxyabcdeXXfYYnull");
    }
}
