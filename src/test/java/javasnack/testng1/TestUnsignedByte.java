package javasnack.testng1;

import static org.testng.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javasnack.tool.UnsignedByte;

import org.testng.annotations.Test;

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
        assertEquals(r, expected);
    }

    @Test
    public void hex() {
        byte[] b1 = new byte[0];
        assertEquals(UnsignedByte.hex("", b1), "");
        assertEquals(UnsignedByte.hex("a", b1), "");
        assertEquals(UnsignedByte.hex("ab", b1), "");

        b1 = new byte[1];
        b1[0] = 0x0;
        assertEquals(UnsignedByte.hex("", b1), "0");
        assertEquals(UnsignedByte.hex("a", b1), "a0");
        assertEquals(UnsignedByte.hex("ab", b1), "ab0");
        b1[0] = Byte.MAX_VALUE;
        assertEquals(UnsignedByte.hex("", b1), "7f");
        assertEquals(UnsignedByte.hex("a", b1), "a7f");
        assertEquals(UnsignedByte.hex("ab", b1), "ab7f");

        b1 = new byte[2];
        b1[0] = 0x0;
        b1[1] = 0x0;
        assertEquals(UnsignedByte.hex("", b1), "00");
        assertEquals(UnsignedByte.hex("a", b1), "a0a0");
        assertEquals(UnsignedByte.hex("ab", b1), "ab0ab0");
        b1[0] = Byte.MAX_VALUE;
        b1[1] = Byte.MIN_VALUE;
        assertEquals(UnsignedByte.hex("", b1), "7f80");
        assertEquals(UnsignedByte.hex("a", b1), "a7fa80");
        assertEquals(UnsignedByte.hex("ab", b1), "ab7fab80");

        b1 = new byte[3];
        b1[0] = Byte.MAX_VALUE;
        b1[1] = Byte.MIN_VALUE;
        b1[2] = 0x0;
        assertEquals(UnsignedByte.hex("", b1), "7f800");
        assertEquals(UnsignedByte.hex("a", b1), "a7fa80a0");
        assertEquals(UnsignedByte.hex("ab", b1), "ab7fab80ab0");
    }

    @Test
    public void bits() {
        byte[] b1 = new byte[0];
        assertEquals(UnsignedByte.bits(b1), "");

        b1 = new byte[1];
        b1[0] = 0x0;
        assertEquals(UnsignedByte.bits(b1), "00000000");
        b1[0] = 0x1;
        assertEquals(UnsignedByte.bits(b1), "00000001");
        b1[0] = Byte.MAX_VALUE;
        assertEquals(UnsignedByte.bits(b1), "01111111");
        b1[0] = Byte.MIN_VALUE;
        assertEquals(UnsignedByte.bits(b1), "10000000");

        b1 = new byte[2];
        b1[0] = 0x0;
        b1[1] = 0x0;
        assertEquals(UnsignedByte.bits(b1), "0000000000000000");
        b1[0] = Byte.MAX_VALUE;
        b1[1] = Byte.MIN_VALUE;
        assertEquals(UnsignedByte.bits(b1), "0111111110000000");
        b1 = new byte[3];
        b1[0] = 0x0;
        b1[1] = Byte.MAX_VALUE;
        b1[2] = Byte.MIN_VALUE;
        assertEquals(UnsignedByte.bits(b1), "000000000111111110000000");
    }
}
