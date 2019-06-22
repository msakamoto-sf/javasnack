/*
 * Copyright 2019 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
package javasnack.langspecs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import javasnack.tool.StreamTool;
import javasnack.tool.UnsignedByte;

public class TestByteRanges {

    @Test
    public void test0x00To0xFF() throws IOException {
        byte b = 0;
        final var sb = new StringBuilder();
        final byte[] bytes = new byte[256];
        for (short s = 0x00; s <= 0xFF; s++, b++) {
            sb.append(Byte.toString(b));
            sb.append("(" + Integer.toHexString(Byte.toUnsignedInt(b)) + ")");
            sb.append("\n");
            bytes[s] = b;
        }
        final var expected = StreamTool.res2str("testdata/print_0x00_to_0xff_bytes.txt");
        assertThat(sb.toString()).isEqualToIgnoringNewLines(expected);
        final var baos = new ByteArrayOutputStream(256);
        baos.write(bytes);
        assertThat(baos.toByteArray()).isEqualTo(bytes);
        final byte[] bytes2 = new byte[256];
        for (short s = 0x00; s <= 0xFF; s++) {
            b = UnsignedByte.from(s);
            bytes2[s] = b;
        }
        assertThat(baos.toByteArray()).isEqualTo(bytes2);
    }

    @Test
    public void test0x00To0xFFwithUnsignedByte() throws IOException {
        final var sb = new StringBuilder();
        final byte[] bytes = new byte[256];
        for (short s = 0x00; s <= 0xFF; s++) {
            byte b = UnsignedByte.from(s);
            sb.append(Byte.toString(b));
            sb.append("(" + Integer.toHexString(Byte.toUnsignedInt(b)) + ")");
            sb.append("\n");
            bytes[s] = b;
        }
        final var expected = StreamTool.res2str("testdata/print_0x00_to_0xff_bytes.txt");
        assertThat(sb.toString()).isEqualToIgnoringNewLines(expected);
        final var baos = new ByteArrayOutputStream(256);
        baos.write(bytes);
        assertThat(baos.toByteArray()).isEqualTo(bytes);
        final byte[] bytes2 = new byte[256];
        byte b2 = 0;
        for (short s = 0x00; s <= 0xFF; s++, b2++) {
            bytes2[s] = b2;
        }
        assertThat(baos.toByteArray()).isEqualTo(bytes2);
    }
}
