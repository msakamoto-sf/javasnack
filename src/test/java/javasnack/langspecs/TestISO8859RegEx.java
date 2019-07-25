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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javasnack.tool.CharsetTool;
import javasnack.tool.UnsignedByte;

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
        assertThat(pos).isEqualTo(18);
        String haystack2 = haystack.replaceFirst("Foo", "Bar");
        assertThat(haystack2.indexOf("Foo")).isEqualTo(-1);
        assertThat(haystack2.indexOf("Bar")).isEqualTo(18);

        // regex pattern matching
        Pattern p = Pattern.compile("Bar");
        Matcher m = p.matcher(haystack2);
        Assertions.assertTrue(m.find());
        // regex pattern matching by hex expression
        Pattern p2 = Pattern.compile("\\x42\\x61\\x72");
        Matcher m2 = p2.matcher(haystack2);
        Assertions.assertTrue(m2.find());
    }

    @Test
    public void randomByteRegExDemo2() throws UnsupportedEncodingException {
        String needleForJapaneseAiueo = "\u3042\u3044\u3046\u3048\u304A";
        byte[] japaneseAiueoUtf8 = needleForJapaneseAiueo
                .getBytes(CharsetTool.UTF8);
        String japaneseAiueoUtf8Binary = new String(japaneseAiueoUtf8,
                CharsetTool.CS_BINARY);

        // prepare random byte string including "needle"
        ByteArrayOutputStream o = new ByteArrayOutputStream(100);
        byte[] r = UnsignedByte.random(100);
        o.write(r, 0, 10);
        o.write(UnsignedByte.from(0));
        byte[] needle = ("Hello, " + needleForJapaneseAiueo + " Bar!")
                .getBytes(CharsetTool.UTF8);
        o.write(needle, 0, needle.length);
        o.write(UnsignedByte.from(0));
        o.write(r, 11, 10);

        // simple String#indexOf() matching
        byte[] d = o.toByteArray();
        String haystack = new String(d, CharsetTool.BINARY);
        int pos = haystack.indexOf(japaneseAiueoUtf8Binary);
        assertThat(pos).isEqualTo(18);

        String japaneseAiueoUtf8Regex = UnsignedByte.hex("\\x", japaneseAiueoUtf8);
        // regex pattern matching
        Pattern p = Pattern.compile(japaneseAiueoUtf8Regex);
        Matcher m = p.matcher(haystack);
        Assertions.assertTrue(m.find());
    }

}
