package javasnack.langspecs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

public class TestClassLoaderResources {
    @Test
    public void testGetResourceDemo() throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("javasnack/hello.txt");
        byte[] bytes = is.readAllBytes();
        assertThat(new String(bytes, StandardCharsets.ISO_8859_1)).isEqualTo("hello, world");

        // start "/"
        is = this.getClass().getClassLoader().getResourceAsStream("/javasnack/hello.txt");
        assertThat(is).isNull();

        // "/" 終端のディレクトリを指定すると、ディレクトリ内のエントリ一覧がテキストとして読み出される。
        is = this.getClass().getClassLoader().getResourceAsStream("javasnack/");
        bytes = is.readAllBytes();
        String s1 = new String(bytes, StandardCharsets.ISO_8859_1);
        assertThat(s1.contains("hello.txt\n")).isTrue();
        System.out.println("--- see console log ---");
        System.out.println(s1);

        // "/" 終端でなくても、ディレクトリ内のエントリ一覧がテキストとして読み出される。
        is = this.getClass().getClassLoader().getResourceAsStream("javasnack");
        bytes = is.readAllBytes();
        s1 = new String(bytes, StandardCharsets.ISO_8859_1);
        assertThat(s1.contains("hello.txt\n")).isTrue();
        System.out.println("--- see console log ---");
        System.out.println(s1);

        /* -> getResourceAsStream() による InputStream も、
         * getResource() による URL も、いずれも「ディレクトリであるか」を判別できるインターフェイスが無い。
         * そのため、この入口からでは、リソース内の実ファイルエントリーを再帰的にtraverseするアプローチは取れない。
         * 
         * TODO : client系のAPI技法を使って実jar / class-pathにアクセスするアプローチのテストコードデモ
         */
    }
}
