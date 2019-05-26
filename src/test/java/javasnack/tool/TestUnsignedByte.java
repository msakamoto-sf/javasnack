/*
 * Copyright 2013 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
package javasnack.tool;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

public class TestUnsignedByte {
    @Test
    public void read0x00to0xFFRawBinaryToByteArray() throws IOException {
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        InputStream is = getClass().getResourceAsStream(
                "/testdata/0x00_to_0xFF.binarydata");
        int b = 0;
        while ((b = is.read()) != -1) {
            bas.write(b);
        }
        is.close();
        bas.flush();
        bas.close();

        byte[] r = bas.toByteArray();

        byte[] expected = new byte[r.length];
        for (int i = 0; i < r.length; i++) {
            expected[i] = UnsignedByte.from(i);
        }
        assertThat(r).isEqualTo(expected);
    }

    @Test
    public void hex() {
        byte[] b1 = new byte[0];
        assertThat(UnsignedByte.hex("", b1)).isEqualTo( "");
        assertThat(UnsignedByte.hex("a", b1)).isEqualTo( "");
        assertThat(UnsignedByte.hex("ab", b1)).isEqualTo( "");

        b1 = new byte[1];
        b1[0] = 0x0;
        assertThat(UnsignedByte.hex("", b1)).isEqualTo( "0");
        assertThat(UnsignedByte.hex("a", b1)).isEqualTo( "a0");
        assertThat(UnsignedByte.hex("ab", b1)).isEqualTo( "ab0");
        b1[0] = Byte.MAX_VALUE;
        assertThat(UnsignedByte.hex("", b1)).isEqualTo("7f");
        assertThat(UnsignedByte.hex("a", b1)).isEqualTo("a7f");
        assertThat(UnsignedByte.hex("ab", b1)).isEqualTo("ab7f");

        b1 = new byte[2];
        b1[0] = 0x0;
        b1[1] = 0x0;
        assertThat(UnsignedByte.hex("", b1)).isEqualTo("00");
        assertThat(UnsignedByte.hex("a", b1)).isEqualTo("a0a0");
        assertThat(UnsignedByte.hex("ab", b1)).isEqualTo("ab0ab0");
        b1[0] = Byte.MAX_VALUE;
        b1[1] = Byte.MIN_VALUE;
        assertThat(UnsignedByte.hex("", b1)).isEqualTo("7f80");
        assertThat(UnsignedByte.hex("a", b1)).isEqualTo("a7fa80");
        assertThat(UnsignedByte.hex("ab", b1)).isEqualTo("ab7fab80");

        b1 = new byte[3];
        b1[0] = Byte.MAX_VALUE;
        b1[1] = Byte.MIN_VALUE;
        b1[2] = 0x0;
        assertThat(UnsignedByte.hex("", b1)).isEqualTo("7f800");
        assertThat(UnsignedByte.hex("a", b1)).isEqualTo("a7fa80a0");
        assertThat(UnsignedByte.hex("ab", b1)).isEqualTo("ab7fab80ab0");
    }

    @Test
    public void bits() {
        byte[] b1 = new byte[0];
        assertThat(UnsignedByte.bits(b1)).isEqualTo("");

        b1 = new byte[1];
        b1[0] = 0x0;
        assertThat(UnsignedByte.bits(b1)).isEqualTo("00000000");
        b1[0] = 0x1;
        assertThat(UnsignedByte.bits(b1)).isEqualTo("00000001");
        b1[0] = Byte.MAX_VALUE;
        assertThat(UnsignedByte.bits(b1)).isEqualTo("01111111");
        b1[0] = Byte.MIN_VALUE;
        assertThat(UnsignedByte.bits(b1)).isEqualTo("10000000");

        b1 = new byte[2];
        b1[0] = 0x0;
        b1[1] = 0x0;
        assertThat(UnsignedByte.bits(b1)).isEqualTo("0000000000000000");
        b1[0] = Byte.MAX_VALUE;
        b1[1] = Byte.MIN_VALUE;
        assertThat(UnsignedByte.bits(b1)).isEqualTo("0111111110000000");
        b1 = new byte[3];
        b1[0] = 0x0;
        b1[1] = Byte.MAX_VALUE;
        b1[2] = Byte.MIN_VALUE;
        assertThat(UnsignedByte.bits(b1)).isEqualTo("000000000111111110000000");
    }
}
