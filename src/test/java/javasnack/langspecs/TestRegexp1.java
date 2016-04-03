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

import static org.testng.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.Test;

public class TestRegexp1 {
    @Test
    public void typicalPatternMatch() {
        Pattern p = Pattern.compile("hello");
        Matcher m = p.matcher("abc hello def");
        assertTrue(m.find());
        assertEquals("hello", m.group());
        assertEquals(4, m.start());
        assertEquals(9, m.end());
        m = p.matcher("abc hell def");
        assertFalse(m.find());
    }

    @Test
    public void typicalPatternEscape() {
        Pattern p = Pattern.compile("\\d{3}");
        Matcher m = p.matcher("abc 123 def");
        assertTrue(m.find());
        assertEquals("123", m.group());
        assertEquals(4, m.start());
        assertEquals(7, m.end());
        m = p.matcher("abc 12 def");
        assertFalse(m.find());
    }

    @Test
    public void typicalPatternEscapeQE() {
        Pattern p = Pattern.compile("\\Q\\d{3}\\E");
        Matcher m = p.matcher("abc \\d{3} def");
        assertTrue(m.find());
        assertEquals("\\d{3}", m.group());
        assertEquals(4, m.start());
        assertEquals(9, m.end());
    }

    @Test
    public void embeddedFlagDemo() {
        Pattern p = Pattern.compile("(?im)(^abc.*def$)");
        Matcher m = p.matcher("ABCxxxDEF\nabc123def\nab99ef\naBcDeF");
        assertTrue(m.find());
        assertEquals("ABCxxxDEF", m.group());
        assertEquals(0, m.start());
        assertEquals(9, m.end());
        assertTrue(m.find());
        assertEquals("abc123def", m.group());
        assertEquals(10, m.start());
        assertEquals(19, m.end());
        assertTrue(m.find());
        assertEquals("aBcDeF", m.group());
        assertEquals(27, m.start());
        assertEquals(33, m.end());
    }

    @Test
    public void quoteDemo() {
        assertEquals("\\Q\\d{3}\\E", Pattern.quote("\\d{3}"));
        assertEquals("\\Q(?im)(^abc.*def$)\\E", Pattern.quote("(?im)(^abc.*def$)"));
    }
}
