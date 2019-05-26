/*
 * Copyright 2016 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @see https://docs.oracle.com/javase/jp/8/docs/api/java/util/regex/Pattern.html
 * @see https://docs.oracle.com/javase/jp/8/docs/api/java/util/regex/Matcher.html
 * @see http://www.ne.jp/asahi/hishidama/home/tech/java/regexp.html
 */
public class TestRegexp1 {
    @Test
    public void typicalPatternMatch() {
        Pattern p = Pattern.compile("hello");
        Matcher m = p.matcher("abc hello def");
        assertTrue(m.find());
        assertThat("hello").isEqualTo(m.group());
        assertThat(4).isEqualTo(m.start());
        assertThat(9).isEqualTo(m.end());
        m = p.matcher("abc hell def");
        Assertions.assertFalse(m.find());
    }

    @Test
    public void typicalPatternEscape() {
        Pattern p = Pattern.compile("\\d{3}");
        Matcher m = p.matcher("abc 123 def");
        assertTrue(m.find());
        assertThat("123").isEqualTo(m.group());
        assertThat(4).isEqualTo(m.start());
        assertThat(7).isEqualTo(m.end());
        m = p.matcher("abc 12 def");
        Assertions.assertFalse(m.find());
    }

    @Test
    public void typicalPatternEscapeQE() {
        Pattern p = Pattern.compile("\\Q\\d{3}\\E");
        Matcher m = p.matcher("abc \\d{3} def");
        assertTrue(m.find());
        assertThat("\\d{3}").isEqualTo(m.group());
        assertThat(4).isEqualTo(m.start());
        assertThat(9).isEqualTo(m.end());
    }

    @Test
    public void embeddedFlagDemo() {
        Pattern p = Pattern.compile("(?im)(^abc.*def$)");
        Matcher m = p.matcher("ABCxxxDEF\nabc123def\nab99ef\naBcDeF");
        assertTrue(m.find());
        assertThat("ABCxxxDEF").isEqualTo(m.group());
        assertThat(0).isEqualTo(m.start());
        assertThat(9).isEqualTo(m.end());
        assertTrue(m.find());
        assertThat("abc123def").isEqualTo(m.group());
        assertThat(10).isEqualTo(m.start());
        assertThat(19).isEqualTo(m.end());
        assertTrue(m.find());
        assertThat("aBcDeF").isEqualTo(m.group());
        assertThat(27).isEqualTo(m.start());
        assertThat(33).isEqualTo(m.end());
    }

    @Test
    public void quoteDemo() {
        assertThat("\\Q\\d{3}\\E").isEqualTo(Pattern.quote("\\d{3}"));
        assertThat("\\Q(?im)(^abc.*def$)\\E").isEqualTo(Pattern.quote("(?im)(^abc.*def$)"));
    }
}
