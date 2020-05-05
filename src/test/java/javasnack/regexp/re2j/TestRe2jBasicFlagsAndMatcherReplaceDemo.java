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

package javasnack.regexp.re2j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;

public class TestRe2jBasicFlagsAndMatcherReplaceDemo {

    @Test
    public void testPatternCompileAndFlags() {
        String regexp1 = "hello,\\s+\"\\w+\". //comment";
        Pattern p = Pattern.compile(regexp1);
        assertThat(p.flags()).isEqualTo(0);
        assertThat(p.pattern()).isEqualTo(regexp1);

        // re2j では UNIX_LINES, COMMENTS, CANON_EQ, LITERALなどのフラグが使えない。
        String regexp2 = "\\s{2}";
        p = Pattern.compile(regexp2, Pattern.CASE_INSENSITIVE + Pattern.MULTILINE + Pattern.DOTALL);
        int f = p.flags();
        assertThat(f & Pattern.CASE_INSENSITIVE).isEqualTo(Pattern.CASE_INSENSITIVE);
        assertThat(f & Pattern.MULTILINE).isEqualTo(Pattern.MULTILINE);
        assertThat(f & Pattern.DOTALL).isEqualTo(Pattern.DOTALL);
        assertThat(p.pattern()).isEqualTo(regexp2);

        String regexp3 = "(?ims)\\s{2}";
        p = Pattern.compile(regexp3);
        f = p.flags();
        // re2j では (?...) フラグで指定したものを Pattern#flags() で取り出せない。
        // (他のテストケース挙動を見るに、動作自体は問題ない)
        assertThat(f).isEqualTo(0);
        //assertThat(f & Pattern.CASE_INSENSITIVE).isEqualTo(Pattern.CASE_INSENSITIVE);
        //assertThat(f & Pattern.MULTILINE).isEqualTo(Pattern.MULTILINE);
        //assertThat(f & Pattern.DOTALL).isEqualTo(Pattern.DOTALL);
        assertThat(p.pattern()).isEqualTo(regexp3);
    }

    @Test
    public void testSplitByPattern() {
        Pattern p = Pattern.compile("\n");
        String[] r = p.split("abc\ndef\nghi");
        assertThat(r).isEqualTo(new String[] { "abc", "def", "ghi" });

        p = Pattern.compile("\n");
        String t = "abc\ndef\nghi\njkl";
        r = p.split(t, 1);
        assertThat(r).isEqualTo(new String[] { "abc\ndef\nghi\njkl" });
        r = p.split(t, 2);
        assertThat(r).isEqualTo(new String[] { "abc", "def\nghi\njkl" });
        r = p.split(t, 3);
        assertThat(r).isEqualTo(new String[] { "abc", "def", "ghi\njkl" });
        r = p.split(t, 4);
        assertThat(r).isEqualTo(new String[] { "abc", "def", "ghi", "jkl" });
        r = p.split(t, 10);
        assertThat(r).isEqualTo(new String[] { "abc", "def", "ghi", "jkl" });

        t = "111abc222ABd333aBC444AbD555";
        r = Pattern.compile("ab[cd]").split(t);
        assertThat(r).isEqualTo(new String[] { "111", "222ABd333aBC444AbD555" });
        r = Pattern.compile("ab[cd]", Pattern.CASE_INSENSITIVE).split(t);
        assertThat(r).isEqualTo(new String[] { "111", "222", "333", "444", "555" });
        r = Pattern.compile("(?i)ab[cd]").split(t);
        assertThat(r).isEqualTo(new String[] { "111", "222", "333", "444", "555" });
    }

    @Test
    public void testQuote() {
        String regexp = "\\Qabc\\ndef\\ghi\\E";
        Pattern p = Pattern.compile(regexp);
        assertThat(p.pattern()).isEqualTo(regexp);
        Matcher m = p.matcher("foo abc\\ndef\\ghi bar");
        assertTrue(m.find());

        String quoted = Pattern.quote(regexp);
        p = Pattern.compile(quoted);
        m = p.matcher("foo \\Qabc\\ndef\\ghi\\E bar");
        assertTrue(m.find());
    }

    static Stream<Arguments> providesUnixLineFlag() {
        return Stream.of(
        //@formatter:off
        arguments(0, "111a\nb222", true),
        arguments(0, "111a" + '\n' + "b222", true),
        /* re2jの場合、UNIX_LINESフラグが無い影響で \n しか改行として扱われない。
         * -> このため以下のパターンはすべてマッチしなくなる。
         */
        arguments(0, "111a" + '\r' + "b222", false),
        arguments(0, "111a" + '\r' + '\n' + "b222", false),
        arguments(0, "111a" + '\u0085' + "b222", false),
        arguments(0, "111a" + '\u2028' + "b222", false),
        arguments(0, "111a" + '\u2029' + "b222", false)
        // @formatter:on
        );
    }

    @ParameterizedTest
    @MethodSource("providesUnixLineFlag")
    public void testUnixLineFlag(int pflag, String target, boolean expected) {
        String regexp = "(?m)a$";
        Pattern p = Pattern.compile(regexp, pflag);
        Matcher m = p.matcher(target);
        assertThat(m.find()).isEqualTo(expected);
    }

    @Test
    public void testMatcherBasicUsage1() {
        Pattern p = Pattern.compile("abc");
        Matcher m = p.matcher("abc");
        assertThat(m.groupCount()).isEqualTo(0);
        // matches to whole input
        assertTrue(m.matches());
        m.reset(); // reset internal start/end position

        // scans next subsequence for input 
        assertTrue(m.find());
        // scans next -> no matching input, return false
        assertFalse(m.find());
        m.reset(); // reset internal start/end position

        // matches from beginning of input
        assertTrue(m.lookingAt());
        m.reset(); // reset internal start/end position

        // matches to whole input -> consume entire input sequence
        assertTrue(m.matches());
        // match from current position -> no input remains, no-match
        assertFalse(m.find());
        // find() consumption does not affect to lookingAt() 
        assertTrue(m.lookingAt());
        m.reset(); // reset internal start/end position

        // lookingAt() consumption affect to find()
        assertTrue(m.lookingAt());
        assertFalse(m.find());
        m.reset(); // reset internal start/end position

        assertTrue(m.find());
        assertFalse(m.find());
        // find() consumption does not affect to match() and lookingAt()
        assertTrue(m.matches());
        assertTrue(m.lookingAt());
    }

    @Test
    public void testMatcherBasicUsage2() {
        Pattern p = Pattern.compile("abc");
        Matcher m = p.matcher("abc def ghi");
        assertThat(m.groupCount()).isEqualTo(0);

        // not match to whole input
        assertFalse(m.matches());
        m.reset(); // reset internal start/end position

        // matches to subsequence of input
        assertTrue(m.find());
        assertFalse(m.find());
        m.reset(); // reset internal start/end position

        // matches from beginning of input
        assertTrue(m.lookingAt());
    }

    @Test
    public void testMatcherBasicUsage3() {
        Pattern p = Pattern.compile("abc");
        Matcher m = p.matcher("aaa abc bbb abc ccc abc\nddd abc\nabc");
        // not match to whole input
        assertFalse(m.matches());
        m.reset();

        // not match from beginning of input
        assertFalse(m.lookingAt());
        m.reset();

        assertThat(m.groupCount()).isEqualTo(0);
        // match to subsequence of input
        assertTrue(m.find());
        assertThat(m.start()).isEqualTo(4);
        assertThat(m.end()).isEqualTo(7);
        assertTrue(m.find());
        assertThat(m.start()).isEqualTo(12);
        assertThat(m.end()).isEqualTo(15);
        assertTrue(m.find());
        assertThat(m.start()).isEqualTo(20);
        assertThat(m.end()).isEqualTo(23);
        assertTrue(m.find());
        assertThat(m.start()).isEqualTo(28);
        assertThat(m.end()).isEqualTo(31);
        assertTrue(m.find());
        assertThat(m.start()).isEqualTo(32);
        assertThat(m.end()).isEqualTo(35);
        assertFalse(m.find());
        m.reset();
        assertTrue(m.find());
        assertThat(m.start()).isEqualTo(4);
        assertThat(m.end()).isEqualTo(7);
    }

    @Test
    public void testGroupMatch1() {
        Pattern p = Pattern.compile("(ab)");
        Matcher m = p.matcher("aaa ab ccc\nddd ab eee");
        assertFalse(m.matches());
        assertFalse(m.lookingAt());
        assertThat(m.groupCount()).isEqualTo(1);
        assertTrue(m.find());
        assertThat(m.start()).isEqualTo(4);
        assertThat(m.end()).isEqualTo(6);
        assertThat(m.group()).isEqualTo("ab");
        assertThat(m.group(0)).isEqualTo("ab");
        assertThat(m.start(0)).isEqualTo(4);
        assertThat(m.end(0)).isEqualTo(6);
        assertTrue(m.find());
        assertThat(m.start()).isEqualTo(15);
        assertThat(m.end()).isEqualTo(17);
        assertThat(m.group()).isEqualTo("ab");
        assertThat(m.group(0)).isEqualTo("ab");
        assertThat(m.start(0)).isEqualTo(15);
        assertThat(m.end(0)).isEqualTo(17);
        assertFalse(m.find());
    }

    @Test
    public void testGroupMatch2() {
        Pattern p = Pattern.compile("(ab)(cd)");
        Matcher m = p.matcher("aaa abcd ccc\nddd abcd eee");
        assertFalse(m.matches());
        assertFalse(m.lookingAt());
        assertThat(m.groupCount()).isEqualTo(2);
        assertTrue(m.find());
        assertThat(m.start()).isEqualTo(4);
        assertThat(m.end()).isEqualTo(8);
        assertThat(m.group()).isEqualTo("abcd");
        assertThat(m.group(0)).isEqualTo("abcd");
        assertThat(m.group(1)).isEqualTo("ab");
        assertThat(m.group(2)).isEqualTo("cd");
        assertThat(m.start(0)).isEqualTo(4);
        assertThat(m.end(0)).isEqualTo(8);
        assertThat(m.start(1)).isEqualTo(4);
        assertThat(m.end(1)).isEqualTo(6);
        assertThat(m.start(2)).isEqualTo(6);
        assertThat(m.end(2)).isEqualTo(8);
        assertTrue(m.find());
        assertThat(m.start()).isEqualTo(17);
        assertThat(m.end()).isEqualTo(21);
        assertThat(m.group()).isEqualTo("abcd");
        assertThat(m.group(0)).isEqualTo("abcd");
        assertThat(m.group(1)).isEqualTo("ab");
        assertThat(m.group(2)).isEqualTo("cd");
        assertThat(m.start(0)).isEqualTo(17);
        assertThat(m.end(0)).isEqualTo(21);
        assertThat(m.start(1)).isEqualTo(17);
        assertThat(m.end(1)).isEqualTo(19);
        assertThat(m.start(2)).isEqualTo(19);
        assertThat(m.end(2)).isEqualTo(21);
        assertFalse(m.find());
    }

    @Test
    public void testGroupMatch3() {
        Pattern p = Pattern.compile("((ab)(cd))");
        Matcher m = p.matcher("aaa abcd ccc\nddd abcd eee");
        assertFalse(m.matches());
        assertFalse(m.lookingAt());
        assertThat(m.groupCount()).isEqualTo(3);
        assertTrue(m.find());
        assertThat(m.start()).isEqualTo(4);
        assertThat(m.end()).isEqualTo(8);
        assertThat(m.group()).isEqualTo("abcd");
        assertThat(m.group(0)).isEqualTo("abcd");
        assertThat(m.group(1)).isEqualTo("abcd");
        assertThat(m.group(2)).isEqualTo("ab");
        assertThat(m.group(3)).isEqualTo("cd");
        assertThat(m.start(0)).isEqualTo(4);
        assertThat(m.end(0)).isEqualTo(8);
        assertThat(m.start(1)).isEqualTo(4);
        assertThat(m.end(1)).isEqualTo(8);
        assertThat(m.start(2)).isEqualTo(4);
        assertThat(m.end(2)).isEqualTo(6);
        assertThat(m.start(3)).isEqualTo(6);
        assertThat(m.end(3)).isEqualTo(8);
        assertTrue(m.find());
        assertThat(m.start()).isEqualTo(17);
        assertThat(m.end()).isEqualTo(21);
        assertThat(m.group()).isEqualTo("abcd");
        assertThat(m.group(0)).isEqualTo("abcd");
        assertThat(m.group(1)).isEqualTo("abcd");
        assertThat(m.group(2)).isEqualTo("ab");
        assertThat(m.group(3)).isEqualTo("cd");
        assertThat(m.start(0)).isEqualTo(17);
        assertThat(m.end(0)).isEqualTo(21);
        assertThat(m.start(1)).isEqualTo(17);
        assertThat(m.end(1)).isEqualTo(21);
        assertThat(m.start(2)).isEqualTo(17);
        assertThat(m.end(2)).isEqualTo(19);
        assertThat(m.start(3)).isEqualTo(19);
        assertThat(m.end(3)).isEqualTo(21);
        assertFalse(m.find());
    }

    @Test
    public void testGroupMatch4() {
        Pattern p = Pattern.compile("xx ((ab)(?:cd)(ef)) yy");
        Matcher m = p.matcher("aaa xx abcdef yy ccc\nddd xx abcdef yy eee");
        assertFalse(m.matches());
        assertFalse(m.lookingAt());
        // outer () : +1, (ab) : +1, (?:cd) : 0, (ef) : +1 
        assertThat(m.groupCount()).isEqualTo(3);
        assertTrue(m.find());
        assertThat(m.start()).isEqualTo(4);
        assertThat(m.end()).isEqualTo(16);
        assertThat(m.group()).isEqualTo("xx abcdef yy");
        assertThat(m.group(0)).isEqualTo("xx abcdef yy");
        assertThat(m.group(1)).isEqualTo("abcdef");
        assertThat(m.group(2)).isEqualTo("ab");
        assertThat(m.group(3)).isEqualTo("ef");
        assertThat(m.start(0)).isEqualTo(4);
        assertThat(m.end(0)).isEqualTo(16);
        assertThat(m.start(1)).isEqualTo(7);
        assertThat(m.end(1)).isEqualTo(13);
        assertThat(m.start(2)).isEqualTo(7);
        assertThat(m.end(2)).isEqualTo(9);
        assertThat(m.start(3)).isEqualTo(11);
        assertThat(m.end(3)).isEqualTo(13);
        assertTrue(m.find());
        assertThat(m.start()).isEqualTo(25);
        assertThat(m.end()).isEqualTo(37);
        assertThat(m.group()).isEqualTo("xx abcdef yy");
        assertThat(m.group(0)).isEqualTo("xx abcdef yy");
        assertThat(m.group(1)).isEqualTo("abcdef");
        assertThat(m.group(2)).isEqualTo("ab");
        assertThat(m.group(3)).isEqualTo("ef");
        assertThat(m.start(0)).isEqualTo(25);
        assertThat(m.end(0)).isEqualTo(37);
        assertThat(m.start(1)).isEqualTo(28);
        assertThat(m.end(1)).isEqualTo(34);
        assertThat(m.start(2)).isEqualTo(28);
        assertThat(m.end(2)).isEqualTo(30);
        assertThat(m.start(3)).isEqualTo(32);
        assertThat(m.end(3)).isEqualTo(34);
        assertFalse(m.find());
    }

    @Test
    public void testGroupMatch5() {
        Pattern p = Pattern.compile("((ab)cd(ef))");
        Matcher m = p.matcher("aaa abcdef ccc\nddd abcdef eee");
        assertFalse(m.matches());
        assertFalse(m.lookingAt());
        assertThat(m.groupCount()).isEqualTo(3);
        assertTrue(m.find());
        assertThat(m.start()).isEqualTo(4);
        assertThat(m.end()).isEqualTo(10);
        assertThat(m.group()).isEqualTo("abcdef");
        assertThat(m.group(0)).isEqualTo("abcdef");
        assertThat(m.group(1)).isEqualTo("abcdef");
        assertThat(m.group(2)).isEqualTo("ab");
        assertThat(m.group(3)).isEqualTo("ef");
        assertThat(m.start(0)).isEqualTo(4);
        assertThat(m.end(0)).isEqualTo(10);
        assertThat(m.start(1)).isEqualTo(4);
        assertThat(m.end(1)).isEqualTo(10);
        assertThat(m.start(2)).isEqualTo(4);
        assertThat(m.end(2)).isEqualTo(6);
        assertThat(m.start(3)).isEqualTo(8);
        assertThat(m.end(3)).isEqualTo(10);
        assertTrue(m.find());
        assertThat(m.start()).isEqualTo(19);
        assertThat(m.end()).isEqualTo(25);
        assertThat(m.group()).isEqualTo("abcdef");
        assertThat(m.group(0)).isEqualTo("abcdef");
        assertThat(m.group(1)).isEqualTo("abcdef");
        assertThat(m.group(2)).isEqualTo("ab");
        assertThat(m.group(3)).isEqualTo("ef");
        assertThat(m.start(0)).isEqualTo(19);
        assertThat(m.end(0)).isEqualTo(25);
        assertThat(m.start(1)).isEqualTo(19);
        assertThat(m.end(1)).isEqualTo(25);
        assertThat(m.start(2)).isEqualTo(19);
        assertThat(m.end(2)).isEqualTo(21);
        assertThat(m.start(3)).isEqualTo(23);
        assertThat(m.end(3)).isEqualTo(25);
        assertFalse(m.find());
    }

    @Test
    public void testGroupMatch6() {
        Pattern p = Pattern.compile("((ab)cd(?:ef|EF))");
        Matcher m = p.matcher("aaa abcdef ccc\nddd abcdEF eee");
        assertFalse(m.matches());
        assertFalse(m.lookingAt());
        assertThat(m.groupCount()).isEqualTo(2);
        assertTrue(m.find());
        assertThat(m.start()).isEqualTo(4);
        assertThat(m.end()).isEqualTo(10);
        assertThat(m.group()).isEqualTo("abcdef");
        assertThat(m.group(0)).isEqualTo("abcdef");
        assertThat(m.group(1)).isEqualTo("abcdef");
        assertThat(m.group(2)).isEqualTo("ab");
        assertThat(m.start(0)).isEqualTo(4);
        assertThat(m.end(0)).isEqualTo(10);
        assertThat(m.start(1)).isEqualTo(4);
        assertThat(m.end(1)).isEqualTo(10);
        assertThat(m.start(2)).isEqualTo(4);
        assertThat(m.end(2)).isEqualTo(6);
        assertTrue(m.find());
        assertThat(m.start()).isEqualTo(19);
        assertThat(m.end()).isEqualTo(25);
        assertThat(m.group()).isEqualTo("abcdEF");
        assertThat(m.group(0)).isEqualTo("abcdEF");
        assertThat(m.group(1)).isEqualTo("abcdEF");
        assertThat(m.group(2)).isEqualTo("ab");
        assertThat(m.start(0)).isEqualTo(19);
        assertThat(m.end(0)).isEqualTo(25);
        assertThat(m.start(1)).isEqualTo(19);
        assertThat(m.end(1)).isEqualTo(25);
        assertThat(m.start(2)).isEqualTo(19);
        assertThat(m.end(2)).isEqualTo(21);
        assertFalse(m.find());
    }

    // re2j では後方参照(backreference) は実装されていない。
    /*
    @Test
    public void testGroupMatch7() {
        Pattern p = Pattern.compile("<(div|p)>.*<(h1|h2)>.*<\\/\\2>.*<\\/\\1>");
        Matcher m = p.matcher("abc<div>def<h2>heading1</h2>ghi</div><p>jkl<h1>mno</h1>pqr</p>");
        assertFalse(m.matches());
        assertFalse(m.lookingAt());
        assertThat(m.groupCount()).isEqualTo(2);
        assertTrue(m.find());
        assertThat(m.start()).isEqualTo(3);
        assertThat(m.end()).isEqualTo(37);
        assertThat(m.group()).isEqualTo("<div>def<h2>heading1</h2>ghi</div>");
        assertThat(m.group(0)).isEqualTo("<div>def<h2>heading1</h2>ghi</div>");
        assertThat(m.group(1)).isEqualTo("div");
        assertThat(m.group(2)).isEqualTo("h2");
        assertThat(m.start(0)).isEqualTo(3);
        assertThat(m.end(0)).isEqualTo(37);
        assertThat(m.start(1)).isEqualTo(4);
        assertThat(m.end(1)).isEqualTo(7);
        assertThat(m.start(2)).isEqualTo(12);
        assertThat(m.end(2)).isEqualTo(14);
        assertTrue(m.find());
        assertThat(m.start()).isEqualTo(37);
        assertThat(m.end()).isEqualTo(62);
        assertThat(m.group()).isEqualTo("<p>jkl<h1>mno</h1>pqr</p>");
        assertThat(m.group(0)).isEqualTo("<p>jkl<h1>mno</h1>pqr</p>");
        assertThat(m.group(1)).isEqualTo("p");
        assertThat(m.group(2)).isEqualTo("h1");
        assertThat(m.start(0)).isEqualTo(37);
        assertThat(m.end(0)).isEqualTo(62);
        assertThat(m.start(1)).isEqualTo(38);
        assertThat(m.end(1)).isEqualTo(39);
        assertThat(m.start(2)).isEqualTo(44);
        assertThat(m.end(2)).isEqualTo(46);
        assertFalse(m.find());
    }
    */

    @Test
    public void testMultiLine1() {
        Pattern p = Pattern.compile("^abc");
        Matcher m = p.matcher("abc aaa abc\nabc bbb\nccc abc\nabc");
        assertFalse(m.matches());
        assertTrue(m.lookingAt()); // CAUTION!
        assertFalse(m.find()); // CAUTION!
        m.reset(); // CAUTION!
        assertTrue(m.find()); // CAUTION!
        assertThat(m.groupCount()).isEqualTo(0);
        assertThat(m.start()).isEqualTo(0);
        assertThat(m.end()).isEqualTo(3);
        assertThat(m.group()).isEqualTo("abc");
        assertFalse(m.find());
    }

    @Test
    public void testMultiLine2() {
        Pattern p = Pattern.compile("^abc", Pattern.MULTILINE);
        Matcher m = p.matcher("abc aaa abc\nabc bbb\nccc abc\nabc");
        assertFalse(m.matches());
        assertTrue(m.lookingAt()); // CAUTION!
        assertTrue(m.find()); // CAUTION!
        assertThat(m.groupCount()).isEqualTo(0);
        assertThat(m.start()).isEqualTo(12); // CAUTION!
        assertThat(m.end()).isEqualTo(15);
        assertThat(m.group()).isEqualTo("abc");
        assertTrue(m.lookingAt()); // CAUTION!
        m.reset();
        assertTrue(m.find());
        assertThat(m.groupCount()).isEqualTo(0);
        assertThat(m.start()).isEqualTo(0);
        assertThat(m.end()).isEqualTo(3);
        assertThat(m.group()).isEqualTo("abc");
        assertTrue(m.find());
        assertThat(m.groupCount()).isEqualTo(0);
        assertThat(m.start()).isEqualTo(12);
        assertThat(m.end()).isEqualTo(15);
        assertThat(m.group()).isEqualTo("abc");
        assertTrue(m.find());
        assertThat(m.groupCount()).isEqualTo(0);
        assertThat(m.start()).isEqualTo(28);
        assertThat(m.end()).isEqualTo(31);
        assertThat(m.group()).isEqualTo("abc");
    }

    @Test
    public void testMultiLine3() {
        Pattern p = Pattern.compile("(?m)^abc");
        Matcher m = p.matcher("abc aaa abc\nabc bbb\nccc abc\nabc");
        assertTrue(m.find());
        assertThat(m.groupCount()).isEqualTo(0);
        assertThat(m.start()).isEqualTo(0);
        assertThat(m.end()).isEqualTo(3);
        assertThat(m.group()).isEqualTo("abc");
        assertTrue(m.find());
        assertThat(m.groupCount()).isEqualTo(0);
        assertThat(m.start()).isEqualTo(12);
        assertThat(m.end()).isEqualTo(15);
        assertThat(m.group()).isEqualTo("abc");
        assertTrue(m.find());
        assertThat(m.groupCount()).isEqualTo(0);
        assertThat(m.start()).isEqualTo(28);
        assertThat(m.end()).isEqualTo(31);
        assertThat(m.group()).isEqualTo("abc");
    }

    @Test
    public void testDotAll1() {
        Pattern p = Pattern.compile("ab.*g");
        Matcher m = p.matcher("123abcdefghi\nabc\ndefghi");
        assertFalse(m.matches()); // not match to whole input
        assertFalse(m.lookingAt()); // not match from beginning of input
        assertTrue(m.find()); // match to subsequence of input
        m.reset();
        assertTrue(m.find());
        assertThat(m.groupCount()).isEqualTo(0);
        assertThat(m.start()).isEqualTo(3);
        assertThat(m.end()).isEqualTo(10);
        assertThat(m.group()).isEqualTo("abcdefg");
        assertFalse(m.find());
    }

    @Test
    public void testDotAll2() {
        // see: http://stackoverflow.com/questions/3651725/match-multiline-text-using-regular-expression
        Pattern p = Pattern.compile("ab.*g", Pattern.DOTALL);
        Matcher m = p.matcher("123abcdefghi\nabc\ndefghi");
        assertFalse(m.matches()); // not match to whole input
        assertFalse(m.lookingAt()); // not match from beginning of input
        assertTrue(m.find()); // match to subsequence of input
        m.reset();
        assertTrue(m.find());
        assertThat(m.groupCount()).isEqualTo(0);
        assertThat(m.start()).isEqualTo(3);
        assertThat(m.end()).isEqualTo(21);
        assertThat(m.group()).isEqualTo("abcdefghi\nabc\ndefg");
        assertFalse(m.find());
    }

    @Test
    public void testDotAll3() {
        // reluctant quantifier
        // see: http://docs.oracle.com/javase/tutorial/essential/regex/quant.html
        // see: http://stackoverflow.com/questions/5319840/greedy-vs-reluctant-vs-possessive-quantifiers
        Pattern p = Pattern.compile("(?s)ab.*?g");
        Matcher m = p.matcher("123abcdefghi\nabc\ndefghi");
        assertFalse(m.matches()); // not match to whole input
        assertFalse(m.lookingAt()); // not match from beginning of input
        assertTrue(m.find()); // match to subsequence of input
        m.reset();
        assertTrue(m.find());
        assertThat(m.groupCount()).isEqualTo(0);
        assertThat(m.start()).isEqualTo(3);
        assertThat(m.end()).isEqualTo(10);
        assertThat(m.group()).isEqualTo("abcdefg");
        assertTrue(m.find());
        assertThat(m.groupCount()).isEqualTo(0);
        assertThat(m.start()).isEqualTo(13);
        assertThat(m.end()).isEqualTo(21);
        assertThat(m.group()).isEqualTo("abc\ndefg");
    }

    @Test
    public void testReplaceFirst() {
        Pattern p = Pattern.compile("abc");
        Matcher m = p.matcher("abc def\nabc ghi\nabc jkl");
        assertThat(m.replaceFirst("AAA")).isEqualTo("AAA def\nabc ghi\nabc jkl");
        assertThat(m.replaceFirst("BBB")).isEqualTo("BBB def\nabc ghi\nabc jkl");
        assertThat(m.replaceFirst("CCC")).isEqualTo("CCC def\nabc ghi\nabc jkl");
    }

    @Test
    public void testReplaceAll() {
        Pattern p = Pattern.compile("abc");
        Matcher m = p.matcher("abc def\nabc ghi\nabc jkl");
        assertThat(m.replaceAll("AAA")).isEqualTo("AAA def\nAAA ghi\nAAA jkl");
        assertThat(m.replaceAll("BBB")).isEqualTo("BBB def\nBBB ghi\nBBB jkl");
    }

    @Test
    public void testReplaceWithDollarReference() {
        Pattern p = Pattern.compile("aaa((ab|AB)(cd|CD)ef)bbb");
        Matcher m = p.matcher("AAA aaaABcdefbbb BBB\nCCC aaaabCDefbbb DDD\naaaABCDefbbb");
        assertThat(m.replaceFirst("([0]=$0;[1]=$1\\\\[2]=$2\\$3=$3)")).isEqualTo(
                "AAA ([0]=aaaABcdefbbb;[1]=ABcdef\\[2]=AB$3=cd) BBB\n"
                        + "CCC aaaabCDefbbb DDD\naaaABCDefbbb");
        assertThat(
                m.replaceAll("([0]=$0;[1]=$1\\\\[2]=$2\\$3=$3)")).isEqualTo(
                        "AAA ([0]=aaaABcdefbbb;[1]=ABcdef\\[2]=AB$3=cd) BBB\n"
                                + "CCC ([0]=aaaabCDefbbb;[1]=abCDef\\[2]=ab$3=CD) DDD\n"
                                + "([0]=aaaABCDefbbb;[1]=ABCDef\\[2]=AB$3=CD)");
    }
}
