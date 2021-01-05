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

package javasnack.regexp.codezinedemo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import javasnack.regexp.codezinedemo.Regexp.RegexpOption;

public class RegexpTest {
    @Test
    public void testNfa2Dfa() {
        Regexp r = Regexp.compileNfa2Dfa("a");
        assertFalse(r.match(""));
        assertTrue(r.match("a"));
        assertFalse(r.match("x"));

        System.out.println(">>>> ------------- test NFA to DFA");
        r = Regexp.compileNfa2Dfa("ab", RegexpOption.DEBUG_LOG);
        assertFalse(r.match(""));
        assertFalse(r.match("a"));
        assertTrue(r.match("ab"));
        assertFalse(r.match("x"));
        System.out.println("<<<<");

        r = Regexp.compileNfa2Dfa("ab*");
        assertFalse(r.match(""));
        assertTrue(r.match("a"));
        assertTrue(r.match("ab"));
        assertTrue(r.match("abb"));
        assertFalse(r.match("ac"));
        assertFalse(r.match("x"));

        r = Regexp.compileNfa2Dfa("ab*c");
        assertFalse(r.match(""));
        assertFalse(r.match("a"));
        assertTrue(r.match("abc"));
        assertTrue(r.match("abbc"));
        assertTrue(r.match("ac"));
        assertFalse(r.match("x"));

        r = Regexp.compileNfa2Dfa("a|b");
        assertFalse(r.match(""));
        assertTrue(r.match("a"));
        assertTrue(r.match("b"));
        assertFalse(r.match("x"));

        r = Regexp.compileNfa2Dfa("ab|cd");
        assertFalse(r.match(""));
        assertFalse(r.match("a"));
        assertTrue(r.match("ab"));
        assertFalse(r.match("bc"));
        assertFalse(r.match("c"));
        assertTrue(r.match("cd"));
        assertFalse(r.match("x"));

        r = Regexp.compileNfa2Dfa("(ab*)|(cd*)");
        assertFalse(r.match(""));
        assertTrue(r.match("a"));
        assertTrue(r.match("ab"));
        assertTrue(r.match("abb"));
        assertFalse(r.match("bc"));
        assertTrue(r.match("c"));
        assertTrue(r.match("cd"));
        assertTrue(r.match("cdd"));
        assertFalse(r.match("x"));

        r = Regexp.compileNfa2Dfa("(ab*|cd*)*ef*");
        assertFalse(r.match(""));
        assertTrue(r.match("e"));
        assertTrue(r.match("abe"));
        assertTrue(r.match("abbefff"));
        assertTrue(r.match("abcde"));
        assertTrue(r.match("abbbcdddde"));
        assertTrue(r.match("ababe"));
        assertTrue(r.match("abbbabbbbefff"));
        assertTrue(r.match("e"));
        assertTrue(r.match("cde"));
        assertTrue(r.match("cdabefff"));
    }

    @Test
    public void testNfa() {
        Regexp r = Regexp.compileNfa("a");
        assertFalse(r.match(""));
        assertTrue(r.match("a"));
        assertFalse(r.match("x"));

        System.out.println(">>>> ------------- test NFA");
        r = Regexp.compileNfa("ab", RegexpOption.DEBUG_LOG);
        assertFalse(r.match(""));
        assertFalse(r.match("a"));
        assertTrue(r.match("ab"));
        assertFalse(r.match("x"));
        System.out.println("<<<<");

        r = Regexp.compileNfa("ab*");
        assertFalse(r.match(""));
        assertTrue(r.match("a"));
        assertTrue(r.match("ab"));
        assertTrue(r.match("abb"));
        assertFalse(r.match("ac"));
        assertFalse(r.match("x"));

        r = Regexp.compileNfa("ab*c");
        assertFalse(r.match(""));
        assertFalse(r.match("a"));
        assertTrue(r.match("abc"));
        assertTrue(r.match("abbc"));
        assertTrue(r.match("ac"));
        assertFalse(r.match("x"));

        r = Regexp.compileNfa("a|b");
        assertFalse(r.match(""));
        assertTrue(r.match("a"));
        assertTrue(r.match("b"));
        assertFalse(r.match("x"));

        r = Regexp.compileNfa("ab|cd");
        assertFalse(r.match(""));
        assertFalse(r.match("a"));
        assertTrue(r.match("ab"));
        assertFalse(r.match("bc"));
        assertFalse(r.match("c"));
        assertTrue(r.match("cd"));
        assertFalse(r.match("x"));

        r = Regexp.compileNfa("(ab*)|(cd*)");
        assertFalse(r.match(""));
        assertTrue(r.match("a"));
        assertTrue(r.match("ab"));
        assertTrue(r.match("abb"));
        assertFalse(r.match("bc"));
        assertTrue(r.match("c"));
        assertTrue(r.match("cd"));
        assertTrue(r.match("cdd"));
        assertFalse(r.match("x"));

        r = Regexp.compileNfa("(ab*|cd*)*ef*");
        assertFalse(r.match(""));
        assertTrue(r.match("e"));
        assertTrue(r.match("abe"));
        assertTrue(r.match("abbefff"));
        assertTrue(r.match("abcde"));
        assertTrue(r.match("abbbcdddde"));
        assertTrue(r.match("ababe"));
        assertTrue(r.match("abbbabbbbefff"));
        assertTrue(r.match("e"));
        assertTrue(r.match("cde"));
        assertTrue(r.match("cdabefff"));
    }

    @Test
    public void testNfaBackTrack() {
        Regexp r = Regexp.compileNfa("a", RegexpOption.NFA_BACKTRACK);
        assertFalse(r.match(""));
        assertTrue(r.match("a"));
        assertFalse(r.match("x"));

        System.out.println(">>>> ------------- test NFA with BackTrack");
        r = Regexp.compileNfa("ab",
                RegexpOption.DEBUG_LOG,
                RegexpOption.NFA_BACKTRACK,
                RegexpOption.DISABLE_NFA_TRACED_BACKTRACK_SKIPPING);
        assertFalse(r.match(""));
        assertFalse(r.match("a"));
        assertTrue(r.match("ab"));
        assertFalse(r.match("x"));
        System.out.println("<<<<");

        r = Regexp.compileNfa("ab*", RegexpOption.NFA_BACKTRACK);
        assertFalse(r.match(""));
        assertTrue(r.match("a"));
        assertTrue(r.match("ab"));
        assertTrue(r.match("abb"));
        assertFalse(r.match("ac"));
        assertFalse(r.match("x"));

        r = Regexp.compileNfa("ab*c", RegexpOption.NFA_BACKTRACK);
        assertFalse(r.match(""));
        assertFalse(r.match("a"));
        assertTrue(r.match("abc"));
        assertTrue(r.match("abbc"));
        assertTrue(r.match("ac"));
        assertFalse(r.match("x"));

        r = Regexp.compileNfa("a|b", RegexpOption.NFA_BACKTRACK);
        assertFalse(r.match(""));
        assertTrue(r.match("a"));
        assertTrue(r.match("b"));
        assertFalse(r.match("x"));

        r = Regexp.compileNfa("ab|cd", RegexpOption.NFA_BACKTRACK);
        assertFalse(r.match(""));
        assertFalse(r.match("a"));
        assertTrue(r.match("ab"));
        assertFalse(r.match("bc"));
        assertFalse(r.match("c"));
        assertTrue(r.match("cd"));
        assertFalse(r.match("x"));

        r = Regexp.compileNfa("(ab*)|(cd*)", RegexpOption.NFA_BACKTRACK);
        assertFalse(r.match(""));
        assertTrue(r.match("a"));
        assertTrue(r.match("ab"));
        assertTrue(r.match("abb"));
        assertFalse(r.match("bc"));
        assertTrue(r.match("c"));
        assertTrue(r.match("cd"));
        assertTrue(r.match("cdd"));
        assertFalse(r.match("x"));

        r = Regexp.compileNfa("(ab*|cd*)*ef*", RegexpOption.NFA_BACKTRACK);
        assertFalse(r.match(""));
        assertTrue(r.match("e"));
        assertTrue(r.match("abe"));
        assertTrue(r.match("abbefff"));
        assertTrue(r.match("abcde"));
        assertTrue(r.match("abbbcdddde"));
        assertTrue(r.match("ababe"));
        assertTrue(r.match("abbbabbbbefff"));
        assertTrue(r.match("e"));
        assertTrue(r.match("cde"));
        assertTrue(r.match("cdabefff"));
    }
}
