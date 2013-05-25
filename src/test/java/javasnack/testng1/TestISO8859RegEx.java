package javasnack.testng1;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javasnack.tool.CharsetTool;
import javasnack.tool.UnsignedByte;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestISO8859RegEx {

    @Test
    public void randomByteRegExDemo1() throws UnsupportedEncodingException {
        // prepare random byte string including "needle"
        ByteArrayOutputStream o = new ByteArrayOutputStream(100);
        byte[] r = UnsignedByte.random(100);
        o.write(r, 0, 10);
        o.write(UnsignedByte.from(0));
        byte[] needle = "Hello, Foo Bar!".getBytes(CharsetTool.BINARY);
        o.write(needle, 0, needle.length);
        o.write(UnsignedByte.from(0));
        o.write(r, 11, 10);

        // simple String#indexOf() matching
        byte[] d = o.toByteArray();
        String haystack = new String(d, CharsetTool.BINARY);
        int pos = haystack.indexOf("Foo");
        Assert.assertEquals(pos, 18);
        String haystack2 = haystack.replaceFirst("Foo", "Bar");
        Assert.assertEquals(haystack2.indexOf("Foo"), -1);
        Assert.assertEquals(haystack2.indexOf("Bar"), 18);

        // regex pattern matching
        Pattern p = Pattern.compile("Bar");
        Matcher m = p.matcher(haystack2);
        Assert.assertTrue(m.find());
        // regex pattern matching by hex expression
        Pattern p2 = Pattern.compile("\\x42\\x61\\x72");
        Matcher m2 = p2.matcher(haystack2);
        Assert.assertTrue(m2.find());
    }

    @Test
    public void randomByteRegExDemo2() throws UnsupportedEncodingException {
        String needle_japanese_aiueo = "\u3042\u3044\u3046\u3048\u304A";
        byte[] japanese_aiueo_utf8 = needle_japanese_aiueo
                .getBytes(CharsetTool.UTF8);
        String japanese_aiueo_utf8_binary = new String(japanese_aiueo_utf8,
                CharsetTool.CS_BINARY);

        // prepare random byte string including "needle"
        ByteArrayOutputStream o = new ByteArrayOutputStream(100);
        byte[] r = UnsignedByte.random(100);
        o.write(r, 0, 10);
        o.write(UnsignedByte.from(0));
        byte[] needle = ("Hello, " + needle_japanese_aiueo + " Bar!")
                .getBytes(CharsetTool.UTF8);
        o.write(needle, 0, needle.length);
        o.write(UnsignedByte.from(0));
        o.write(r, 11, 10);

        // simple String#indexOf() matching
        byte[] d = o.toByteArray();
        String haystack = new String(d, CharsetTool.BINARY);
        int pos = haystack.indexOf(japanese_aiueo_utf8_binary);
        Assert.assertEquals(pos, 18);

        String japanese_aiueo_utf8_regex = UnsignedByte.hex("\\x", japanese_aiueo_utf8);
        // regex pattern matching
        Pattern p = Pattern.compile(japanese_aiueo_utf8_regex);
        Matcher m = p.matcher(haystack);
        Assert.assertTrue(m.find());
    }

}
