package javasnack.testng1;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import javasnack.tool.CharsetTool;
import javasnack.tool.UnsignedByte;

import org.testng.annotations.Test;

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
            assertThat(dst, is(src));
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
            assertThat(dst, is(src));
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
            assertThat(dst, is(src));
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
            assertThat(dst, is(src));
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
            assertThat(dst, is(src));
        } catch (UnsupportedEncodingException ignore) {
            ignore.printStackTrace();
        }
    }

    @Test
    public void errorCharacters() throws UnsupportedEncodingException {
        String errs = "こんにちは, これはJapanese Characterです。";
        byte[] dst1 = errs.getBytes(CharsetTool.LATIN1);
        String r = new String(dst1, CharsetTool.LATIN1);
        assertNotEquals(r, errs);
    }

}
