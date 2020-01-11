/*
 * Copyright 2020 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
public class TestJUnit5TempDirMethodParameter {

    // File type is available, too.

    @Test
    public void demoTempDirMethodParameter1(@TempDir final Path tempDir) throws IOException {
        System.out.println("tempDir1=[" + tempDir + "]");
        final Path tempFile = Path.of(tempDir.toString(), "aaa.txt");
        System.out.println("tempFile1=[" + tempFile + "]");
        Files.writeString(tempFile, "こんにちは",
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND);
        final String data = Files.readString(tempFile, StandardCharsets.UTF_8);
        assertThat(data).isEqualTo("こんにちは");
    }

    @Test
    public void demoTempDirMethodParameter2(@TempDir final Path tempDir) throws IOException {
        System.out.println("tempDir2=[" + tempDir + "]");
        final Path tempFile = Path.of(tempDir.toString(), "aaa.txt");
        System.out.println("tempFile2=[" + tempFile + "]");
        Files.writeString(tempFile, "あいうえお",
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND);
        final String data = Files.readString(tempFile, StandardCharsets.UTF_8);
        assertThat(data).isEqualTo("あいうえお");
    }
}
