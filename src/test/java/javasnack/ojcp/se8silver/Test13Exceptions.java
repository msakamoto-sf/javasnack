package javasnack.ojcp.se8silver;

import static javasnack.ojcp.OjcpTools.assertLines;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import javasnack.tool.StringWriterPrinter;

public class Test13Exceptions {
    /* >#>POINT<#<:
     * Throwable - Error : unchecked 例外(catchは任意)
     *           - Exception : checked 例外(catch必須)
     *             - RuntimeException : unchecked 例外
     */

    String toLower(String s) {
        // NPE
        return s.toLowerCase();
    }

    // 非チェック例外は throws に書いてもOK.
    int div(int x, int y) throws ArithmeticException {
        return x / y;
    }

    void callDiv() {
        /* >#>POINT<#< : 
         * 非チェック例外をthrowsしているメソッドを呼ぶ場合は、
         * try-catch で囲む必要は無い。(= catch は任意)
         */
        System.out.println(div(1, 0));
    }

    // チェック例外を投げる場合は、throws 必須
    //void throwEx(int x) {
    void throwEx(int x) throws Exception {
        if (x > 5) {
            throw new Exception();
        }
    }

    // throw もしておらず、無関係のチェック例外を throw に置くことができる。
    void throwEx2(int x) throws IOException, FileNotFoundException, SQLException {
        if (x < 10) {
            throw new IOException();
        } else if (x < 20) {
            // IOException の派生クラス
            throw new FileNotFoundException();
        } else if (x < 30) {
            // IOException の派生クラスなので、throw IOException だけでカバーできる。
            throw new SocketTimeoutException();
        } else if (x < 40) {
            // 非チェック例外については throws に明示する必要が無い。
            throw new UnsupportedOperationException();
        }
    }

    void callThrowEx2(final StringWriterPrinter swp, int x) {
        swp.clear();
        try {
            throwEx2(x);
            swp.out.print("aa");
            /* >#>POINT<#<: throw するcheck例外に継承関係がある場合、
             * 派生先のクラスの catch から先に書かないと compile error
            } catch (IOException e) {
            swp.out.print("cc");
            */
        } catch (FileNotFoundException e) {
            swp.out.print("bb");
        } catch (IOException e) {
            swp.out.print("cc");
        } catch (SQLException e) {
            swp.out.print("dd");
        } catch (RuntimeException e) {
            swp.out.print("ee");
        }
    }

    @Test
    public void testTryCatch() {
        assertThatThrownBy(() -> {
            // 非チェック例外はtry-catchで補足しなくて良い(任意)
            toLower(null);
        }).isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> {
            // 非チェック例外はthrowにリストされていても、try-catchで補足しなくて良い(任意)
            div(3, 0);
        }).isInstanceOf(ArithmeticException.class);

        final StringWriterPrinter swp = new StringWriterPrinter();
        // typical try-catch
        try {
            throwEx(10);
            swp.out.print("aaa");
        } catch (Exception e) {
            swp.out.print("bbb");
        } finally {
            swp.out.print("ccc");
        }
        assertLines(swp, "bbbccc");

        /* >#>POINT<#<: try{} だけは compile error
        try {
            throwEx(10);
        }
        */

        callThrowEx2(swp, 5);
        assertLines(swp, "cc");
        callThrowEx2(swp, 15);
        assertLines(swp, "bb");
        // SocketTimeoutException -> IOException として catch
        callThrowEx2(swp, 25);
        assertLines(swp, "cc");
        callThrowEx2(swp, 35);
        // 非チェック例外の任意 catch
        assertLines(swp, "ee");
    }

    /* >#>POINT<#<:
     * メソッド override 時の throws の扱い:
     * - throws にはスーパークラスのメソッドが throws に指定した例外クラスと、
     *   そのサブクラスを指定できる。
     * - ただし、RuntimeException クラスおよびそのサブクラスは、
     *   制約なしに throws に指定できる。
     * - スーパークラスのメソッドに throws があっても、
     *   throws を指定しなくてもよい。
     * (from オラクル認定資格教科書 Javaプログラマ Silver SE 8)
     * 
     * メソッドを利用する観点で整理してみる。
     * 1. unchecked 例外を投げる可能性のあるメソッド m1, m2 があるとする。
     * class C1 {
     *   void m1() { throw new NullPointerException(); }
     *   void m2() throws NullPointerException {
     *     throw new NullPointerException();
     *   }
     * }
     * メソッドm1, m2を利用する側では、try-catchを使わなくて良い。
     * つまり以下のような利用が考えられる。
     * void useC1methods(C1 obj1) {
     *   obj1.m1();
     *   obj1.m2();
     * }
     * 
     * ここで、 useC1methods() に C1 を継承したC2インスタンスを渡すケースを考える。
     * class C2 extends C1 {
     *   @Override
     *   void m1() ...
     *   @Override
     *   void m2() ...
     * }
     * 
     * C1 obj2 = new C2();
     * useC1methods(obj2);
     * -> 例えば自分が外部ライブラリC1を使っていて、それをカスタマイズしようとして
     * C2を作ることを想像してみる。
     * すでに他にも C1 の利用者は多く、 useC1methods() のようなコードが広まっている。
     * そのような中で、C2 を使ってもらうときに m1, m2 の override はどうなると良いか？
     * -> 非チェック例外はcatchが任意であるため、m1, m2 を override するときも同様に適用されるのがベスト。
     * つまり C2 を自分が作るときに、m1 に NPE の throws を増やしたり m2 の throws を削除しても、
     * すでに C1 の m1/m2 を catch なしで利用しているコードがコンパイルエラーにならないのが良い。
     * ということで、実際にそうなっていると考えられる。
     * 
     * 2. 同様のシチュエーションで m1, m2 が以下のようだったとする。
     * class C1 {
     *   void m1() {}
     *   void m2() throws IOException {}
     * }
     * C1を利用する以下のようなコードが考えられる。
     * void useC1methods(C1 obj1) {
     *   m1();
     *   try { m2(); } catch (IO Exception e) {}
     * }
     * 
     * ここで 1. と同様にC1の利用コードに影響を与えず、継承先の C2 側で m1,m2 を override するには
     * どのようなルールがベストか、想像してみる。
     * 
     * ケース1: throws なしの m1 を、throws checked 例外ありで override できるとする。
     * class C2 extends C1 {
     *   @Override
     *   void m1() throws SQLException {}
     * }
     * -> このルールが許されてしまうと、
     * C1 obj2 = new C2();
     * useC1methods(obj2);
     * したときに、try-catch で囲っていない useC1methods() の m1() 呼び出しをどう扱えばよいか悩ましい。
     * 
     * ケース2: checked例外を throws している m2 を、throws なしで override できるとする。
     * class C2 extends C1 {
     *   @Override
     *   void m2() {}
     * }
     * -> このルールが許されるとしても、useC1methods() に C2 を渡しても m2() 呼び出しはすでに
     * C1 用に try-catch で囲まれている。そうなれば単に C2#m2() が呼ばれても checked 例外が
     * throw されなくなるだけなので、useC1methods() がコンパイルエラーになることはなく、
     * 単純に考えても不整合が起きるとは考えられない。
     * 実際、このルールは許されている。
     * 
     * ケース3: checked例外を throws している m2 を、その例外のsuper classのthrowsで override できるとする。
     * class C2 extends C1 {
     *   @Override
     *   void m2() throws Exception {}
     * }
     * -> このルールが許されてしまうと、useC1Methods() では m2() を元のIOExceptionでcatchしているため、
     * そちらとの不整合が発生することになる。
     * 
     * ケース4: ケース3とは逆にサブクラスのchecked例外をthrowsでoverrideできるとする。
     * class C2 extends C1 {
     *   @Override
     *   void m2() throws FileNotFoundException {}
     * }
     * -> この場合、useC1Methods() では元のIOExceptionでcatchしているので
     * サブクラスもそれでcatchできると考えられる。
     * よって useC1meethodsがコンパイルエラーになることはなく、
     * 単純に考えても不整合が起きるとは考えられない。
     * 
     * ケース3, 4をまとめると、実際のルールが不整合が起きない状態となっていることが理解できる。
     * 
     * 以上のように、例外の throws に関する override ルールはそのメソッドを利用するコードを考え、
     * 将来クラスの継承が発生しても元のメソッドを想定した以前のコードがコンパイルエラーや
     * 直感的な不整合が起きないようなルールになっていると考えれば、理解の一助になりそう。
     */

    static class C1 {
        String unchecked1(String s) {
            return s.toLowerCase();
        }

        String unchecked2(String s) throws NullPointerException {
            return s.toLowerCase();
        }

        void checked(int x) throws IOException {
            if (x < 10) {
                throw new IOException();
            } else if (x < 20) {
                throw new UnsupportedOperationException();
            }
        }
    }

    static class C2a extends C1 {
        // 親メソッドで throws が無い -> unchecked 例外を override 側で throws に追加できる。
        @Override
        String unchecked1(String s) throws NullPointerException, ArithmeticException {
            return s.toLowerCase();
        }

        // 親メソッドで throws があっても、override 側では省略できる。

        @Override
        String unchecked2(String s) {
            return s.toLowerCase();
        }

        @Override
        void checked(int x) {
        }

        static void foo() {
            // -> override 側では throws が最初から無いものとして扱える。
            new C2a().checked(10);
        }
    }

    static class C2b extends C1 {
        // 親メソッドのthrowsの派生クラスに拡張できる。
        @Override
        void checked(int x) throws FileNotFoundException {
        }
    }

    /*
    static class C2c extends C1 {
        // 親メソッドのthrowsの親クラスを throws で override すると compile error
        @Override
        void checked(int x) throws Exception {
        }
    }
    static class C2d extends C1 {
        // 親メソッドのthrowsの親クラスと関係ない throws で override すると compile error
        @Override
        void checked(int x) throws SQLException {
        }
    }
    */

    static class C2e extends C1 {
        // 親メソッドで throws が無い
        // -> checked 例外を override 側で throws すると compile error
        /*
        @Override
        String unchecked1(String s) throws IOException {
            return s.toLowerCase();
        }
        */

        // 親メソッドで unchecked例外をthrows
        // -> checked 例外を override 側で throws すると compile error
        /*
        @Override
        String unchecked2(String s) throws IOException {
            return s.toLowerCase();
        }
        */

        // 親メソッドで checked 例外を throws
        // -> unchekced 例外を override 側で throws に override 可能
        @Override
        void checked(int x) throws NullPointerException {
        }
    }
}
