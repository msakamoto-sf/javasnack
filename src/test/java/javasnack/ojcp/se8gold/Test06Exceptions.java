package javasnack.ojcp.se8gold;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javasnack.ojcp.se8silver.Test13Exceptions;

/**
 * Silverに含まれる範囲については {@link Test13Exceptions} 参照。
 * こちらでは Gold で新たに追加された範囲の差分だけ、テストコードで試している。
 */
public class Test06Exceptions {
    /* 参考:
     * https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
     * https://docs.oracle.com/javase/jp/7/technotes/guides/language/try-with-resources.html
     * https://www.baeldung.com/java-suppressed-exceptions
     * https://qiita.com/c-cho/items/a5ba13aaebb91bab98ab
     */

    @SuppressWarnings("resource")
    @Test
    public void testMulticatch() {
        String s1 = "A";
        int[] ints1 = { 10, 0 };
        // multicatch -> NumberFormatException
        try {
            System.out.println(Integer.parseInt(s1));
            fail("should not reach here.");
        } catch (NumberFormatException | ArithmeticException e) {
            assertThat(e instanceof NumberFormatException).isTrue();
        }
        // multicatch -> ArithmeticException, implicitly final
        try {
            ints1[0] = ints1[0] / ints1[1];
            fail("should not reach here.");
        } catch (NumberFormatException | ArithmeticException e) {
            assertThat(e instanceof ArithmeticException).isTrue();
            // multicatch の場合、catchした例外オブジェクトの参照は変更できない。(implicitly final)
            // e = null; // compile error
        } catch (Exception e) {
            // 通常のcatchであれば、catchした例外オブジェクト参照を更新可能。
            e = null;
        }

        try {
            new FileReader("foo.txt").read();
            // multicatchでは継承関係にある例外を列記できない -> compile error
            //} catch (FileNotFoundException | IOException e) {
            //} catch (IOException | FileNotFoundException e) {
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            // 普通のcatchでも、base -> extends の順ではcatchできない -> compile error
            //} catch (FileNotFoundException e) {
        }
    }

    static class MyExceptionA extends Exception {
        private static final long serialVersionUID = 1L;
        String msgA = "MyExceptionA";
    }

    static class MyExceptionB extends Exception {
        private static final long serialVersionUID = 1L;
        String msgB = "MyExceptionB";
    }

    static void m1(int x) throws MyExceptionA, MyExceptionB {
        try {
            if (x < 5) {
                throw new MyExceptionA();
            } else {
                throw new MyExceptionB();
            }
            /* Java 6 以前
            } catch (MyExceptionA e) {
            throw e;
            } catch (MyExceptionB e) {
            throw e;
            */

            // ↓ Java 7 以降。Java6以前だと compile error
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    public void testRethrowJava7Extended() {
        assertThatThrownBy(() -> {
            m1(1);
        }).isInstanceOf(MyExceptionA.class);

        assertThatThrownBy(() -> {
            m1(10);
        }).isInstanceOf(MyExceptionB.class);
    }

    static class MyExceptionC extends MyExceptionB {
        private static final long serialVersionUID = 1L;
        String msgC = "MyExceptionC";
    }

    static void m1_b(int x) throws MyExceptionA, MyExceptionB {
        try {
            if (x < 5) {
                throw new MyExceptionA();
            } else {
                throw new MyExceptionB();
            }
        } catch (MyExceptionA e) {
            // 以下は、eの型が異なりにそもそも代入できないので compile error
            //e = new Exception("xx");
            //e = new RuntimeException("yy");

            // re-throwする例外の型が異なる(MyException{A|B}かその派生になっていない)ため compile error
            // throw new Exception("xx");

            if (x < 5) {
                throw e;
            } else {
                // 実行時例外なら自由に直接 re-throw できる。
                throw new RuntimeException("yy");
            }
        } catch (MyExceptionB e) {
            // MyExceptionC は B の派生型なので代入してre-throwできる。
            e = new MyExceptionC();
            if (x < 5) {
                throw e;
            } else {
                // MyExceptionC は B の派生型なので直接代入re-throwできる。
                throw new MyExceptionC();
            }
        }
    }

    /* java.lang.AutoCloseable(#close() throws Exception) が全てのcloseableの super interface
     * close()は冪等である必要がなく、複数回呼び出されたときに副作用があっても良い。
     * (ただし実装するときは冪等推奨)
     */
    static class AutoCloseableResource implements AutoCloseable {
        final StringBuilder sb;
        final String closingMsg;
        final boolean throwOnClose;

        AutoCloseableResource(final StringBuilder sb, final String closingMsg) {
            this(sb, closingMsg, false);
        }

        AutoCloseableResource(final StringBuilder sb, final String closingMsg, final boolean throwOnClose) {
            this.sb = sb;
            this.closingMsg = closingMsg;
            this.throwOnClose = throwOnClose;
        }

        @Override
        public void close() throws Exception {
            sb.append(closingMsg);
            if (throwOnClose) {
                throw new Exception(closingMsg);
            }
        }
    }

    /* IOに特化したのが java.io.Closeable
     * close()は冪等であることが要求され、複数回呼び出されても例外発生などの副作用が無いようにする。
     * -> 内部的に一度closeしたのをマークしておくのが推奨されている。
     * 
     * 用途としては、単一の {Input|Output}Stream を複数種類でラップしたインスタンスを
     * try-with-resource で使う場面で、どれか1つのインスタンスの close() が走れば、
     * 残りのインスタンスの close() が走ってもエラーにならないなど、
     * おそらくそうした場面をイメージしているものと思われる。
     */
    static class CloseableIoResource implements Closeable {
        final StringBuilder sb;
        final String closingMsg;
        final boolean throwOnClose;

        CloseableIoResource(final StringBuilder sb, final String closingMsg) {
            this(sb, closingMsg, false);
        }

        CloseableIoResource(final StringBuilder sb, final String closingMsg, final boolean throwOnClose) {
            this.sb = sb;
            this.closingMsg = closingMsg;
            this.throwOnClose = throwOnClose;
        }

        @Override
        public void close() throws IOException {
            sb.append(closingMsg);
            if (throwOnClose) {
                throw new IOException(closingMsg);
            }
        }
    }

    @Test
    public void testTryWithResources() {
        StringBuilder sb = new StringBuilder();
        Exception caught = null;
        try (AutoCloseableResource res1 = new AutoCloseableResource(sb, "msg1");
                CloseableIoResource res2 = new CloseableIoResource(sb, "msg2");
                AutoCloseableResource res3 = new AutoCloseableResource(sb, "msg3");
                CloseableIoResource res4 = new CloseableIoResource(sb, "msg4")) {
            m1(1);
        } catch (Exception e) {
            caught = e;
            sb.append("caught");
        } finally {
            sb.append("finally");
        }
        assertThat(sb.toString()).isEqualTo("msg4msg3msg2msg1caughtfinally");
        assertThat(caught instanceof MyExceptionA).isTrue();
        Throwable[] suppressed = caught.getSuppressed();
        assertThat(suppressed.length).isEqualTo(0);

        sb = new StringBuilder();
        caught = null;
        try (AutoCloseableResource res1 = new AutoCloseableResource(sb, "msg1", true);
                CloseableIoResource res2 = new CloseableIoResource(sb, "msg2", true);
                AutoCloseableResource res3 = new AutoCloseableResource(sb, "msg3", true);
                CloseableIoResource res4 = new CloseableIoResource(sb, "msg4", true)) {
            m1(10);
        } catch (Exception e) {
            caught = e;
            sb.append("caught");
        } finally {
            sb.append("finally");
        }
        assertThat(sb.toString()).isEqualTo("msg4msg3msg2msg1caughtfinally");
        assertThat(caught instanceof MyExceptionB).isTrue();
        suppressed = caught.getSuppressed();
        assertThat(suppressed.length).isEqualTo(4);
        Throwable t = suppressed[0];
        assertThat(t instanceof IOException).isTrue();
        assertThat(t.getMessage()).isEqualTo("msg4");
        t = suppressed[1];
        assertThat(t instanceof Exception).isTrue();
        assertThat(t.getMessage()).isEqualTo("msg3");
        t = suppressed[2];
        assertThat(t instanceof IOException).isTrue();
        assertThat(t.getMessage()).isEqualTo("msg2");
        t = suppressed[3];
        assertThat(t instanceof Exception).isTrue();
        assertThat(t.getMessage()).isEqualTo("msg1");

        sb = new StringBuilder();
        caught = null;
        try (AutoCloseableResource res1 = new AutoCloseableResource(sb, "msg1", true);
                CloseableIoResource res2 = new CloseableIoResource(sb, "msg2", true);
                AutoCloseableResource res3 = new AutoCloseableResource(sb, "msg3", true);
                CloseableIoResource res4 = new CloseableIoResource(sb, "msg4", true)) {
        } catch (Exception e) {
            /* try 中で何も例外が発生しない場合、close()での例外は通常のcatchで補足される。
             * -> 複数の close() で発生した場合は、最後(?)に発生した例外が補足され、
             * その suppressed に他の例外が格納される。
             * 
             * もしもこのcatchを削除すれば、このtryブロックがあるメソッドに throws 
             * が必要となり、通常の例外と同様に上位にthrowされる。
             */
            caught = e;
        }
        assertThat(sb.toString()).isEqualTo("msg4msg3msg2msg1");
        assertThat(caught instanceof IOException).isTrue();
        assertThat(caught.getMessage()).isEqualTo("msg4");
        suppressed = caught.getSuppressed();
        assertThat(suppressed.length).isEqualTo(3);
        t = suppressed[0];
        assertThat(t instanceof Exception).isTrue();
        assertThat(t.getMessage()).isEqualTo("msg3");
        t = suppressed[1];
        assertThat(t instanceof IOException).isTrue();
        assertThat(t.getMessage()).isEqualTo("msg2");
        t = suppressed[2];
        assertThat(t instanceof Exception).isTrue();
        assertThat(t.getMessage()).isEqualTo("msg1");
    }

    static void m2(int x) {
        assert x < 10;
        assert x < 5 : "x must be smaller than 5";
    }

    /* assert のデモ:
     * 動かしたいときは Disabled annotation をコメントアウトして,
     * Eclipse等のIDEのプラグインから実行する オプション付きで実行。
     * or
     * ビルドツールからjava実行オプションの -ea 付きでテスト実行。
     * のいずれかで動作確認する。
     */
    @Test
    @Disabled
    public void testAssertion() {
        assertThatThrownBy(() -> {
            m2(11);
        }).isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> {
            m2(6);
        }).isInstanceOf(AssertionError.class).hasMessage("x must be smaller than 5");
    }
}
