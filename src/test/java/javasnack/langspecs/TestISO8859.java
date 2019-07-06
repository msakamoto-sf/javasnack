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

package javasnack.langspecs;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import javasnack.tool.CharsetTool;
import javasnack.tool.UnsignedByte;

public class TestISO8859 {

    @Test
    public void convertAndRevertC0() {
        ByteBuffer bf = ByteBuffer.allocate(255);
        for (byte s1 = 0x00; s1 < 0x1F; s1++) {
            bf.put(s1);
        }
        bf.flip();
        byte[] src = new byte[bf.limit()];
        bf.get(src);
        try {
            String s = new String(src, CharsetTool.BINARY);
            byte[] dst = s.getBytes(CharsetTool.BINARY);
            assertArrayEquals(src, dst);
        } catch (UnsupportedEncodingException ignore) {
            ignore.printStackTrace();
        }
    }

    @Test
    public void convertAndRevertG0() {
        ByteBuffer bf = ByteBuffer.allocate(255);
        for (byte s1 = 0x20; s1 < 0x7F; s1++) {
            bf.put(s1);
        }
        bf.flip();
        byte[] src = new byte[bf.limit()];
        bf.get(src);
        try {
            String s = new String(src, CharsetTool.BINARY);
            byte[] dst = s.getBytes(CharsetTool.BINARY);
            assertArrayEquals(src, dst);
        } catch (UnsupportedEncodingException ignore) {
            ignore.printStackTrace();
        }
    }

    @Test
    public void convertAndRevertC1() {
        ByteBuffer bf = ByteBuffer.allocate(255);
        for (int d = 0x1F; d <= 0x9F; d++) {
            bf.put(UnsignedByte.from(d));
        }
        bf.flip();
        byte[] src = new byte[bf.limit()];
        bf.get(src);
        try {
            String s = new String(src, CharsetTool.BINARY);
            byte[] dst = s.getBytes(CharsetTool.BINARY);
            assertArrayEquals(src, dst);
        } catch (UnsupportedEncodingException ignore) {
            ignore.printStackTrace();
        }
    }

    @Test
    public void convertAndRevertG1() {
        ByteBuffer bf = ByteBuffer.allocate(255);
        for (int d = 0xA0; d <= 0xFF; d++) {
            bf.put(UnsignedByte.from(d));
        }
        bf.flip();
        byte[] src = new byte[bf.limit()];
        bf.get(src);
        try {
            String s = new String(src, CharsetTool.BINARY);
            byte[] dst = s.getBytes(CharsetTool.BINARY);
            assertArrayEquals(src, dst);
        } catch (UnsupportedEncodingException ignore) {
            ignore.printStackTrace();
        }
    }

    @Test
    public void convertAndRevert0x00to0xFF() {
        ByteBuffer bf = ByteBuffer.allocate(256);
        for (int d = 0x00; d <= 0xFF; d++) {
            bf.put(UnsignedByte.from(d));
        }
        bf.flip();
        byte[] src = new byte[bf.limit()];
        bf.get(src);
        try {
            String s = new String(src, CharsetTool.BINARY);
            byte[] dst = s.getBytes(CharsetTool.BINARY);
            assertArrayEquals(src, dst);
        } catch (UnsupportedEncodingException ignore) {
            ignore.printStackTrace();
        }
    }

    @Test
    public void errorCharacters() throws UnsupportedEncodingException {
        String errs = "こんにちは, これはJapanese Characterです。";
        byte[] dst1 = errs.getBytes(CharsetTool.LATIN1);
        String r = new String(dst1, CharsetTool.LATIN1);
        assertNotEquals(errs, r);
    }

    @Test
    public void replace0x00ToEmpty() {
        char[] data = new char[] { 'a', 'b', '\0', 'c', 'd', '\0', 'e', 'f' };
        String data2 = new String(data);
        String expected1 = "ab" + "\0" + "cd" + "\0" + "ef";
        assertEquals(expected1, data2);
        String data3 = data2.replaceAll("\0", "");
        assertEquals("abcdef", data3);
    }
}
