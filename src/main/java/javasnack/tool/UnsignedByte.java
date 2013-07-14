package javasnack.tool;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class UnsignedByte {
    public final static byte[] conv = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8,
            9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,
            26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42,
            43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
            60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76,
            77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93,
            94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107,
            108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120,
            121, 122, 123, 124, 125, 126, 127, -128, -127, -126, -125, -124,
            -123, -122, -121, -120, -119, -118, -117, -116, -115, -114, -113,
            -112, -111, -110, -109, -108, -107, -106, -105, -104, -103, -102,
            -101, -100, -99, -98, -97, -96, -95, -94, -93, -92, -91, -90, -89,
            -88, -87, -86, -85, -84, -83, -82, -81, -80, -79, -78, -77, -76,
            -75, -74, -73, -72, -71, -70, -69, -68, -67, -66, -65, -64, -63,
            -62, -61, -60, -59, -58, -57, -56, -55, -54, -53, -52, -51, -50,
            -49, -48, -47, -46, -45, -44, -43, -42, -41, -40, -39, -38, -37,
            -36, -35, -34, -33, -32, -31, -30, -29, -28, -27, -26, -25, -24,
            -23, -22, -21, -20, -19, -18, -17, -16, -15, -14, -13, -12, -11,
            -10, -9, -8, -7, -6, -5, -4, -3, -2, -1 };

    public static byte from(int v) {
        if (v < 0) {
            return (byte) 0;
        }
        if (v > 0xFF) {
            return (byte) -1;
        }
        return conv[v];
    }

    public static byte random() {
        int r = (int) Math.floor(Math.random() * (0xFF - 1));
        return UnsignedByte.from(r);
    }

    public static byte[] random(int size) {
        byte[] e = new byte[0];
        if (size <= 0) {
            return e;
        }
        byte[] r = new byte[size];
        for (int i = 0; i < size; i++) {
            r[i] = UnsignedByte.random();
        }
        return r;
    }

    public static String hex(String prefix, byte[] src) {
        int src_len = src.length;
        if (0 == src_len) {
            return "";
        }

        StringBuilder sb = new StringBuilder(src_len * (prefix.length() + 2));
        for (int i = 0; i < src_len; i++) {
            byte b = src[i];
            sb.append(prefix);
            sb.append(Integer.toHexString(b & 0xFF));
        }
        return sb.toString();
    }

    public static String bits(byte[] src) {
        StringBuilder sb = new StringBuilder(src.length * Byte.SIZE);
        for (int i = 0; i < Byte.SIZE * src.length; i++) {
            sb.append((src[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0'
                    : '1');
        }
        return sb.toString();
    }

    /**
     * create 256(0x100) byte buffer from 0x00 to 0xFF
     * 
     * @return
     */
    public static byte[] create0x00to0xFF() {
        ByteBuffer bf = ByteBuffer.allocate(256);
        for (int d = 0x00; d <= 0xFF; d++) {
            bf.put(UnsignedByte.from(d));
        }
        bf.flip();
        byte[] src = new byte[bf.limit()];
        bf.get(src);
        return src;
    }

    /**
     * create 256(0x100) String(LATIN1) from 0x00 to 0xFF
     * 
     * @return
     */
    public static String create0x00to0xFFString()
            throws UnsupportedEncodingException {
        return new String(create0x00to0xFF(), CharsetTool.BINARY);
    }
}
