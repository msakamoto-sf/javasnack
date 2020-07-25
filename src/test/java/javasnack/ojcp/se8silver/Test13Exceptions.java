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
