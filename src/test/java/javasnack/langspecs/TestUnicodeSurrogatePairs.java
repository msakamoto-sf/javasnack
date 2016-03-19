/*
 * Copyright 2015 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

/**
 * Unicode Surrogate Pair exercise (include JIS X 2013:2004)
 * 
 * @see http://codezine.jp/article/detail/1592
 * @see http://maplesystems.co.jp/blog/all/programming/4709.html
 * @see "JIS X 0213:2004 / Unicode 実装ガイド - Microsoft"
 * @see https://www.ibm.com/developerworks/jp/ysl/library/java/j-unicode_surrogate/
 * @see https://jsfiddle.net/msakamoto_sf/xp9mzzxb/
 * @see http://qiita.com/masakielastic/items/ec483b00ff6337a02878
 * @see http://www.w3.org/TR/html4/charset.html
 * @see https://developer.mozilla.org/ja/docs/Web/JavaScript/Guide/Values,_variables,_and_literals#Unicode
 * @see http://0xcc.net/jsescape/
 * @see http://blog.ohgaki.net/javascript-string-escape
 * @see https://mathiasbynens.be/notes/javascript-escapes
 * @see http://blog.ohgaki.net/json-escape
 * @see http://json.org/
 */
public class TestUnicodeSurrogatePairs {

    @Test
    public void testUnicodeEscape() {
        char c1 = '\u3042'; // HIRAGANA LETTER A, cp=12354
        char c2 = '\uD842'; // tuchi-yoshi (high), cp=134071
        char c3 = '\uDFB7'; // tuchi-yoshi (low), cp=134071
        char c4 = '\u30D5'; // katakana fu, cp=12501
        char c5 = '\u309A'; // handakuten, cp=12442
        char c6 = '\uD842'; // kuchi + shichi (high), cp=134047
        char c7 = '\uDF9F'; // kuchi + shichi (low), cp=134047
        String s = new String(new char[] { c1, c2, c3, c4, c5, c6, c7 });
        assertEquals(s, "\u3042\uD842\uDFB7\u30D5\u309A\uD842\uDF9F");

        int len = s.length();
        assertEquals(len, 7); // ignores surrogate pair :P
        int[] actualCps = new int[len];
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            actualCps[i] = (int) c;
        }
        // Ignores surrogate pairs... :(
        // BUT JavaScript unicode escape in browser accepts this format...:(
        assertEquals(actualCps, new int[] { 0x3042, 0xD842, 0xDFB7, 0x30D5, 0x309A, 0xD842, 0xDF9F });

        int countOfCp = s.codePointCount(0, len);
        assertEquals(countOfCp, 5); // GOOD.

        actualCps = new int[countOfCp];
        for (int i = 0, j = 0, cp; i < len; i += Character.charCount(cp)) {
            cp = s.codePointAt(i);
            actualCps[j++] = cp;
        }
        // GOOD.
        assertEquals(actualCps, new int[] { 0x3042, 0x20BB7, 0x30D5, 0x309A, 0x20B9F });
    }
}