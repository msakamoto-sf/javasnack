/*
 * Copyright 2014 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
package javasnack.testng1;

import static org.testng.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestJavaRegexp01 {

    @Test
    public void TestPatternCompileAndFlags() {
        String regexp1 = "hello,\\s+\"\\w+\". //comment";
        Pattern p = Pattern.compile(regexp1);
        assertEquals(p.flags(), 0);
        assertEquals(p.pattern(), regexp1);

        String regexp2 = "\\s{2}";
        p = Pattern.compile(regexp2, Pattern.UNIX_LINES
                + Pattern.CASE_INSENSITIVE + Pattern.COMMENTS
                + Pattern.MULTILINE);
        int f = p.flags();
        assertEquals(f & Pattern.UNIX_LINES, Pattern.UNIX_LINES);
        assertEquals(f & Pattern.CASE_INSENSITIVE, Pattern.CASE_INSENSITIVE);
        assertEquals(f & Pattern.COMMENTS, Pattern.COMMENTS);
        assertEquals(f & Pattern.MULTILINE, Pattern.MULTILINE);
        assertEquals(p.pattern(), regexp2);

        String regexp3 = "(?dixm)\\s{2}";
        p = Pattern.compile(regexp3);
        f = p.flags();
        assertEquals(f & Pattern.UNIX_LINES, Pattern.UNIX_LINES);
        assertEquals(f & Pattern.CASE_INSENSITIVE, Pattern.CASE_INSENSITIVE);
        assertEquals(f & Pattern.COMMENTS, Pattern.COMMENTS);
        assertEquals(f & Pattern.MULTILINE, Pattern.MULTILINE);
        assertEquals(p.pattern(), regexp3);
    }

    @Test
    public void TestSplitByPattern() {
        Pattern p = Pattern.compile("\n");
        String[] r = p.split("abc\ndef\nghi");
        assertEquals(r, new String[] { "abc", "def", "ghi" });

        p = Pattern.compile("\n");
        String t = "abc\ndef\nghi\njkl";
        r = p.split(t, 1);
        assertEquals(r, new String[] { "abc\ndef\nghi\njkl" });
        r = p.split(t, 2);
        assertEquals(r, new String[] { "abc", "def\nghi\njkl" });
        r = p.split(t, 3);
        assertEquals(r, new String[] { "abc", "def", "ghi\njkl" });
        r = p.split(t, 4);
        assertEquals(r, new String[] { "abc", "def", "ghi", "jkl" });
        r = p.split(t, 10);
        assertEquals(r, new String[] { "abc", "def", "ghi", "jkl" });

        t = "111abc222ABd333aBC444AbD555";
        r = Pattern.compile("ab[cd]").split(t);
        assertEquals(r, new String[] { "111", "222ABd333aBC444AbD555" });
        r = Pattern.compile("ab[cd]", Pattern.CASE_INSENSITIVE).split(t);
        assertEquals(r, new String[] { "111", "222", "333", "444", "555" });
        r = Pattern.compile("(?i)ab[cd]").split(t);
        assertEquals(r, new String[] { "111", "222", "333", "444", "555" });
    }

    @Test
    public void TestQuote() {
        String regexp = "\\Qabc\\ndef\\ghi\\E";
        Pattern p = Pattern.compile(regexp);
        assertEquals(p.pattern(), regexp);
        Matcher m = p.matcher("foo abc\\ndef\\ghi bar");
        assertTrue(m.find());

        String quoted = Pattern.quote(regexp);
        p = Pattern.compile(quoted);
        m = p.matcher("foo \\Qabc\\ndef\\ghi\\E bar");
        assertTrue(m.find());
    }

    @DataProvider(name = "providesUnixLineFlag")
    public Object[][] providesUnixLineFlag() {
        return new Object[][] { { 0, "111a\nb222", true },
                { 0, "111a" + '\r' + "b222", true },
                { 0, "111a" + '\r' + '\n' + "b222", true },
                { 0, "111a" + '\u0085' + "b222", true },
                { 0, "111a" + '\u2028' + "b222", true },
                { 0, "111a" + '\u2029' + "b222", true },
                { Pattern.UNIX_LINES, "111a\nb222", true },
                { Pattern.UNIX_LINES, "111a" + '\r' + "b222", false },
                { Pattern.UNIX_LINES, "111a" + '\r' + '\n' + "b222", false },
                { Pattern.UNIX_LINES, "111a" + '\u0085' + "b222", false },
                { Pattern.UNIX_LINES, "111a" + '\u2028' + "b222", false },
                { Pattern.UNIX_LINES, "111a" + '\u2029' + "b222", false } };
    }

    @Test(dataProvider = "providesUnixLineFlag")
    public void TestUnixLineFlag(int pflag, String target, boolean expected) {
        String regexp = "(?m)a$";
        Pattern p = Pattern.compile(regexp, pflag);
        Matcher m = p.matcher(target);
        assertEquals(m.find(), expected);
    }

    @Test
    public void TestMatcherBasicUsage1() {
        Pattern p = Pattern.compile("abc");
        Matcher m = p.matcher("abc");
        assertEquals(m.groupCount(), 0);
        assertTrue(m.find());
        assertTrue(m.matches());
        assertFalse(m.find()); // CAUTION!
        assertTrue(m.lookingAt());
        m.reset();
        assertTrue(m.find()); // CAUTION!
        assertFalse(m.find());
        assertTrue(m.lookingAt());
    }

    @Test
    public void TestMatcherBasicUsage2() {
        Pattern p = Pattern.compile("abc");
        Matcher m = p.matcher("abc def ghi");
        assertEquals(m.groupCount(), 0);
        assertTrue(m.find());
        assertFalse(m.matches()); // CAUTION!
        assertFalse(m.find()); // CAUTION!
        assertTrue(m.lookingAt());
        m.reset();
        assertTrue(m.find()); // CAUTION!
        assertFalse(m.find());
        assertTrue(m.lookingAt());
    }

    @Test
    public void TestMatcherBasicUsage3() {
        Pattern p = Pattern.compile("abc");
        Matcher m = p.matcher("aaa abc bbb abc ccc abc\nddd abc\nabc");
        assertFalse(m.matches()); // CAUTION!
        assertFalse(m.lookingAt()); // CAUTION!
        assertEquals(m.groupCount(), 0);
        assertTrue(m.find());
        assertEquals(m.start(), 4);
        assertEquals(m.end(), 7);
        assertTrue(m.find());
        assertEquals(m.start(), 12);
        assertEquals(m.end(), 15);
        assertTrue(m.find());
        assertEquals(m.start(), 20);
        assertEquals(m.end(), 23);
        assertTrue(m.find());
        assertEquals(m.start(), 28);
        assertEquals(m.end(), 31);
        assertTrue(m.find());
        assertEquals(m.start(), 32);
        assertEquals(m.end(), 35);
        assertFalse(m.find());
        m.reset();
        assertTrue(m.find());
        assertEquals(m.start(), 4);
        assertEquals(m.end(), 7);
    }

    @Test
    public void TestGroupMatch1() {
        Pattern p = Pattern.compile("(ab)");
        Matcher m = p.matcher("aaa ab ccc\nddd ab eee");
        assertFalse(m.matches());
        assertFalse(m.lookingAt());
        assertEquals(m.groupCount(), 1);
        assertTrue(m.find());
        assertEquals(m.start(), 4);
        assertEquals(m.end(), 6);
        assertEquals(m.group(), "ab");
        assertEquals(m.group(0), "ab");
        assertEquals(m.start(0), 4);
        assertEquals(m.end(0), 6);
        assertTrue(m.find());
        assertEquals(m.start(), 15);
        assertEquals(m.end(), 17);
        assertEquals(m.group(), "ab");
        assertEquals(m.group(0), "ab");
        assertEquals(m.start(0), 15);
        assertEquals(m.end(0), 17);
        assertFalse(m.find());
    }

    @Test
    public void TestGroupMatch2() {
        Pattern p = Pattern.compile("(ab)(cd)");
        Matcher m = p.matcher("aaa abcd ccc\nddd abcd eee");
        assertFalse(m.matches());
        assertFalse(m.lookingAt());
        assertEquals(m.groupCount(), 2);
        assertTrue(m.find());
        assertEquals(m.start(), 4);
        assertEquals(m.end(), 8);
        assertEquals(m.group(), "abcd");
        assertEquals(m.group(0), "abcd");
        assertEquals(m.group(1), "ab");
        assertEquals(m.group(2), "cd");
        assertEquals(m.start(0), 4);
        assertEquals(m.end(0), 8);
        assertEquals(m.start(1), 4);
        assertEquals(m.end(1), 6);
        assertEquals(m.start(2), 6);
        assertEquals(m.end(2), 8);
        assertTrue(m.find());
        assertEquals(m.start(), 17);
        assertEquals(m.end(), 21);
        assertEquals(m.group(), "abcd");
        assertEquals(m.group(0), "abcd");
        assertEquals(m.group(1), "ab");
        assertEquals(m.group(2), "cd");
        assertEquals(m.start(0), 17);
        assertEquals(m.end(0), 21);
        assertEquals(m.start(1), 17);
        assertEquals(m.end(1), 19);
        assertEquals(m.start(2), 19);
        assertEquals(m.end(2), 21);
        assertFalse(m.find());
    }

    @Test
    public void TestGroupMatch3() {
        Pattern p = Pattern.compile("((ab)(cd))");
        Matcher m = p.matcher("aaa abcd ccc\nddd abcd eee");
        assertFalse(m.matches());
        assertFalse(m.lookingAt());
        assertEquals(m.groupCount(), 3);
        assertTrue(m.find());
        assertEquals(m.start(), 4);
        assertEquals(m.end(), 8);
        assertEquals(m.group(), "abcd");
        assertEquals(m.group(0), "abcd");
        assertEquals(m.group(1), "abcd");
        assertEquals(m.group(2), "ab");
        assertEquals(m.group(3), "cd");
        assertEquals(m.start(0), 4);
        assertEquals(m.end(0), 8);
        assertEquals(m.start(1), 4);
        assertEquals(m.end(1), 8);
        assertEquals(m.start(2), 4);
        assertEquals(m.end(2), 6);
        assertEquals(m.start(3), 6);
        assertEquals(m.end(3), 8);
        assertTrue(m.find());
        assertEquals(m.start(), 17);
        assertEquals(m.end(), 21);
        assertEquals(m.group(), "abcd");
        assertEquals(m.group(0), "abcd");
        assertEquals(m.group(1), "abcd");
        assertEquals(m.group(2), "ab");
        assertEquals(m.group(3), "cd");
        assertEquals(m.start(0), 17);
        assertEquals(m.end(0), 21);
        assertEquals(m.start(1), 17);
        assertEquals(m.end(1), 21);
        assertEquals(m.start(2), 17);
        assertEquals(m.end(2), 19);
        assertEquals(m.start(3), 19);
        assertEquals(m.end(3), 21);
        assertFalse(m.find());
    }

    @Test
    public void TestGroupMatch4() {
        Pattern p = Pattern.compile("((ab)cd(ef))");
        Matcher m = p.matcher("aaa abcdef ccc\nddd abcdef eee");
        assertFalse(m.matches());
        assertFalse(m.lookingAt());
        assertEquals(m.groupCount(), 3);
        assertTrue(m.find());
        assertEquals(m.start(), 4);
        assertEquals(m.end(), 10);
        assertEquals(m.group(), "abcdef");
        assertEquals(m.group(0), "abcdef");
        assertEquals(m.group(1), "abcdef");
        assertEquals(m.group(2), "ab");
        assertEquals(m.group(3), "ef");
        assertEquals(m.start(0), 4);
        assertEquals(m.end(0), 10);
        assertEquals(m.start(1), 4);
        assertEquals(m.end(1), 10);
        assertEquals(m.start(2), 4);
        assertEquals(m.end(2), 6);
        assertEquals(m.start(3), 8);
        assertEquals(m.end(3), 10);
        assertTrue(m.find());
        assertEquals(m.start(), 19);
        assertEquals(m.end(), 25);
        assertEquals(m.group(), "abcdef");
        assertEquals(m.group(0), "abcdef");
        assertEquals(m.group(1), "abcdef");
        assertEquals(m.group(2), "ab");
        assertEquals(m.group(3), "ef");
        assertEquals(m.start(0), 19);
        assertEquals(m.end(0), 25);
        assertEquals(m.start(1), 19);
        assertEquals(m.end(1), 25);
        assertEquals(m.start(2), 19);
        assertEquals(m.end(2), 21);
        assertEquals(m.start(3), 23);
        assertEquals(m.end(3), 25);
        assertFalse(m.find());
    }

    @Test
    public void TestGroupMatch5() {
        Pattern p = Pattern.compile("((ab)cd(?:ef|EF))");
        Matcher m = p.matcher("aaa abcdef ccc\nddd abcdEF eee");
        assertFalse(m.matches());
        assertFalse(m.lookingAt());
        assertEquals(m.groupCount(), 2);
        assertTrue(m.find());
        assertEquals(m.start(), 4);
        assertEquals(m.end(), 10);
        assertEquals(m.group(), "abcdef");
        assertEquals(m.group(0), "abcdef");
        assertEquals(m.group(1), "abcdef");
        assertEquals(m.group(2), "ab");
        assertEquals(m.start(0), 4);
        assertEquals(m.end(0), 10);
        assertEquals(m.start(1), 4);
        assertEquals(m.end(1), 10);
        assertEquals(m.start(2), 4);
        assertEquals(m.end(2), 6);
        assertTrue(m.find());
        assertEquals(m.start(), 19);
        assertEquals(m.end(), 25);
        assertEquals(m.group(), "abcdEF");
        assertEquals(m.group(0), "abcdEF");
        assertEquals(m.group(1), "abcdEF");
        assertEquals(m.group(2), "ab");
        assertEquals(m.start(0), 19);
        assertEquals(m.end(0), 25);
        assertEquals(m.start(1), 19);
        assertEquals(m.end(1), 25);
        assertEquals(m.start(2), 19);
        assertEquals(m.end(2), 21);
        assertFalse(m.find());
    }

    @Test
    public void TestGroupMatch6() {
        Pattern p = Pattern.compile("<(div|p)>.*<(h1|h2)>.*<\\/\\2>.*<\\/\\1>");
        Matcher m = p
                .matcher("abc<div>def<h2>heading1</h2>ghi</div><p>jkl<h1>mno</h1>pqr</p>");
        assertFalse(m.matches());
        assertFalse(m.lookingAt());
        assertEquals(m.groupCount(), 2);
        assertTrue(m.find());
        assertEquals(m.start(), 3);
        assertEquals(m.end(), 37);
        assertEquals(m.group(), "<div>def<h2>heading1</h2>ghi</div>");
        assertEquals(m.group(0), "<div>def<h2>heading1</h2>ghi</div>");
        assertEquals(m.group(1), "div");
        assertEquals(m.group(2), "h2");
        assertEquals(m.start(0), 3);
        assertEquals(m.end(0), 37);
        assertEquals(m.start(1), 4);
        assertEquals(m.end(1), 7);
        assertEquals(m.start(2), 12);
        assertEquals(m.end(2), 14);
        assertTrue(m.find());
        assertEquals(m.start(), 37);
        assertEquals(m.end(), 62);
        assertEquals(m.group(), "<p>jkl<h1>mno</h1>pqr</p>");
        assertEquals(m.group(0), "<p>jkl<h1>mno</h1>pqr</p>");
        assertEquals(m.group(1), "p");
        assertEquals(m.group(2), "h1");
        assertEquals(m.start(0), 37);
        assertEquals(m.end(0), 62);
        assertEquals(m.start(1), 38);
        assertEquals(m.end(1), 39);
        assertEquals(m.start(2), 44);
        assertEquals(m.end(2), 46);
        assertFalse(m.find());
    }

    @Test
    public void TestMultiLine1() {
        Pattern p = Pattern.compile("^abc");
        Matcher m = p.matcher("abc aaa abc\nabc bbb\nccc abc\nabc");
        assertFalse(m.matches());
        assertTrue(m.lookingAt()); // CAUTION!
        assertFalse(m.find()); // CAUTION!
        m.reset(); // CAUTION!
        assertTrue(m.find()); // CAUTION!
        assertEquals(m.groupCount(), 0);
        assertEquals(m.start(), 0);
        assertEquals(m.end(), 3);
        assertEquals(m.group(), "abc");
        assertFalse(m.find());
    }

    @Test
    public void TestMultiLine2() {
        Pattern p = Pattern.compile("^abc", Pattern.MULTILINE);
        Matcher m = p.matcher("abc aaa abc\nabc bbb\nccc abc\nabc");
        assertFalse(m.matches());
        assertTrue(m.lookingAt()); // CAUTION!
        assertTrue(m.find()); // CAUTION!
        assertEquals(m.groupCount(), 0);
        assertEquals(m.start(), 12); // CAUTION!
        assertEquals(m.end(), 15);
        assertEquals(m.group(), "abc");
        assertTrue(m.lookingAt()); // CAUTION!
        m.reset();
        assertTrue(m.find());
        assertEquals(m.groupCount(), 0);
        assertEquals(m.start(), 0);
        assertEquals(m.end(), 3);
        assertEquals(m.group(), "abc");
        assertTrue(m.find());
        assertEquals(m.groupCount(), 0);
        assertEquals(m.start(), 12);
        assertEquals(m.end(), 15);
        assertEquals(m.group(), "abc");
        assertTrue(m.find());
        assertEquals(m.groupCount(), 0);
        assertEquals(m.start(), 28);
        assertEquals(m.end(), 31);
        assertEquals(m.group(), "abc");
    }

    @Test
    public void TestMultiLine3() {
        Pattern p = Pattern.compile("(?m)^abc");
        Matcher m = p.matcher("abc aaa abc\nabc bbb\nccc abc\nabc");
        assertTrue(m.find());
        assertEquals(m.groupCount(), 0);
        assertEquals(m.start(), 0);
        assertEquals(m.end(), 3);
        assertEquals(m.group(), "abc");
        assertTrue(m.find());
        assertEquals(m.groupCount(), 0);
        assertEquals(m.start(), 12);
        assertEquals(m.end(), 15);
        assertEquals(m.group(), "abc");
        assertTrue(m.find());
        assertEquals(m.groupCount(), 0);
        assertEquals(m.start(), 28);
        assertEquals(m.end(), 31);
        assertEquals(m.group(), "abc");
    }

    @Test
    public void TestReplaceFirst() {
        Pattern p = Pattern.compile("abc");
        Matcher m = p.matcher("abc def\nabc ghi\nabc jkl");
        assertEquals(m.replaceFirst("AAA"), "AAA def\nabc ghi\nabc jkl");
        assertEquals(m.replaceFirst("BBB"), "BBB def\nabc ghi\nabc jkl");
        assertEquals(m.replaceFirst("CCC"), "CCC def\nabc ghi\nabc jkl");
    }

    @Test
    public void TestReplaceAll() {
        Pattern p = Pattern.compile("abc");
        Matcher m = p.matcher("abc def\nabc ghi\nabc jkl");
        assertEquals(m.replaceAll("AAA"), "AAA def\nAAA ghi\nAAA jkl");
        assertEquals(m.replaceAll("BBB"), "BBB def\nBBB ghi\nBBB jkl");
    }

    @Test
    public void TestReplaceWithDollarReference() {
        Pattern p = Pattern.compile("aaa((ab|AB)(cd|CD)ef)bbb");
        Matcher m = p
                .matcher("AAA aaaABcdefbbb BBB\nCCC aaaabCDefbbb DDD\naaaABCDefbbb");
        assertEquals(m.replaceFirst("([0]=$0;[1]=$1\\\\[2]=$2\\$3=$3)"),
                "AAA ([0]=aaaABcdefbbb;[1]=ABcdef\\[2]=AB$3=cd) BBB\n"
                        + "CCC aaaabCDefbbb DDD\naaaABCDefbbb");
        assertEquals(
                m.replaceAll("([0]=$0;[1]=$1\\\\[2]=$2\\$3=$3)"),
                "AAA ([0]=aaaABcdefbbb;[1]=ABcdef\\[2]=AB$3=cd) BBB\n"
                        + "CCC ([0]=aaaabCDefbbb;[1]=abCDef\\[2]=ab$3=CD) DDD\n"
                        + "([0]=aaaABCDefbbb;[1]=ABCDef\\[2]=AB$3=CD)");
    }
}
