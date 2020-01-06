package javasnack.junit;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/* see: https://junit.org/junit5/docs/current/user-guide/#writing-tests-built-in-extensions-TempDirectory
 * see: https://junit.org/junit5/docs/current/api/org/junit/jupiter/api/io/TempDir.html
 */
public class TestJUnit5TempDirStaticFieldInjection {

    // non-private File or Path static field -> shared for each test method execution.
    @TempDir
    static Path tempDir;

    /* tempDir is shared for each test execution.
     * => CREATE_NEW and APPEND result conflict.
     * so, we use CREATE and TRUNCATE_EXISTING for multiple test method.
     */

    @Test
    public void demoTempDirMethodParameter1() throws IOException {
        System.out.println("tempDir1=[" + tempDir + "]");
        final Path tempFile = Path.of(tempDir.toString(), "aaa.txt");
        System.out.println("tempFile1=[" + tempFile + "]");
        Files.writeString(tempFile, "こんにちは",
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);
        final String data = Files.readString(tempFile, StandardCharsets.UTF_8);
        assertThat(data).isEqualTo("こんにちは");
    }

    @Test
    public void demoTempDirMethodParameter2() throws IOException {
        System.out.println("tempDir2=[" + tempDir + "]");
        final Path tempFile = Path.of(tempDir.toString(), "aaa.txt");
        System.out.println("tempFile2=[" + tempFile + "]");
        Files.writeString(tempFile, "あいうえお",
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);
        final String data = Files.readString(tempFile, StandardCharsets.UTF_8);
        assertThat(data).isEqualTo("あいうえお");
    }
}
