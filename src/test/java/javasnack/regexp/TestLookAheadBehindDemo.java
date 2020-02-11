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

package javasnack.regexp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

/* reference:
 * - 正規表現（肯定先読み、否定先読み、肯定戻り読み、否定戻り読み） - satosystemsの日記
 *   - https://satosystems.hatenablog.com/entry/20100519/1274237784
 * - 正規表現の先読み・後読みを極める！ - あらびき日記
 *   - https://abicky.net/2010/05/30/135112/
 * - 先読みと後読み - Java正規表現の使い方
 *   - https://www.javadrive.jp/start/regex/lookahead/
 */
public class TestLookAheadBehindDemo {

    void assertNextGroup(final Matcher m, final int start, final int end, final String... groups) {
        assertTrue(m.find());
        assertThat(m.start()).isEqualTo(start);
        assertThat(m.end()).isEqualTo(end);
        final int expectedCountOfGroups = groups.length - 1;
        assertThat(m.groupCount()).isEqualTo(expectedCountOfGroups);
        for (int i = 0; i < groups.length; i++) {
            assertThat(m.group(i)).isEqualTo(groups[i]);
        }
    }

    @Test
    public void testPositiveLookAheadDemo() {
        /* Japanese : "肯定先読み"
         * (?=regexp) -> regexp が始まる位置にマッチする。 
         */

        Pattern p = Pattern.compile("(?i)(?=xxx)");
        Matcher m = p.matcher("aaa xxx bbb XXX ccc xxx");
        assertFalse(m.matches());
        assertFalse(m.lookingAt());
        m.reset();
        assertNextGroup(m, 4, 4, "");
        assertNextGroup(m, 12, 12, "");
        assertNextGroup(m, 20, 20, "");
        assertFalse(m.find());

        p = Pattern.compile("(?i)aaa (?=xxx)");
        m = p.matcher("aaa xxx bbb XXX ccc xxx AAA xxx");
        assertFalse(m.matches());
        assertTrue(m.lookingAt());
        m.reset();
        assertNextGroup(m, 0, 4, "aaa ");
        assertNextGroup(m, 24, 28, "AAA ");
        assertFalse(m.find());

        p = Pattern.compile("(?i)aaa|bbb (?=xxx)");
        m = p.matcher("aaa xxx bbb XXX ccc xxx AAA xxx");
        assertFalse(m.matches());
        assertTrue(m.lookingAt());
        m.reset();
        assertNextGroup(m, 0, 3, "aaa");
        assertNextGroup(m, 8, 12, "bbb ");
        assertNextGroup(m, 24, 27, "AAA");
        assertFalse(m.find());

        p = Pattern.compile("(?i)(aaa|bbb) (?=xxx)");
        m = p.matcher("aaa xxx bbb XXX ccc xxx AAA xxx");
        assertFalse(m.matches());
        assertTrue(m.lookingAt());
        m.reset();
        assertNextGroup(m, 0, 4, "aaa ", "aaa");
        assertNextGroup(m, 8, 12, "bbb ", "bbb");
        assertNextGroup(m, 24, 28, "AAA ", "AAA");
        assertFalse(m.find());

        p = Pattern.compile("(?i)(?:aaa|bbb) (?=xxx)");
        m = p.matcher("aaa xxx bbb XXX ccc xxx AAA xxx");
        assertFalse(m.matches());
        assertTrue(m.lookingAt());
        m.reset();
        assertNextGroup(m, 0, 4, "aaa ");
        assertNextGroup(m, 8, 12, "bbb ");
        assertNextGroup(m, 24, 28, "AAA ");
        assertFalse(m.find());

        p = Pattern.compile("(?i)(?=xxx) (bbb|ccc)");
        m = p.matcher("aaa xxx bbb XXX ccc xxx AAA xxx");
        assertFalse(m.matches());
        assertFalse(m.lookingAt());
        assertFalse(m.find());
    }

    @Test
    public void testNegativeLookAheadDemo() {
        /* Japanese : "否定先読み" 
         * (?!regexp) -> regexp が始まらない位置にマッチする。
         */

        Pattern p = Pattern.compile("(?i)(?!xxx)");
        Matcher m = p.matcher("aaa xxx bbb XXX ccc xxx");
        assertFalse(m.matches());
        assertTrue(m.lookingAt());
        m.reset();
        assertNextGroup(m, 0, 0, "");
        assertNextGroup(m, 1, 1, "");
        assertNextGroup(m, 2, 2, "");
        assertNextGroup(m, 3, 3, "");
        // ... xxx で始まらない位置にマッチする = xxx で始まる位置[4]がマッチしない。
        assertNextGroup(m, 5, 5, "");
        assertNextGroup(m, 6, 6, "");
        assertNextGroup(m, 7, 7, "");
        // ... (snip) ...

        p = Pattern.compile("(?i)aaa (?!xxx)");
        m = p.matcher("aaa yyy AAA XXX aaa xxx AAA yyy");
        assertFalse(m.matches());
        assertTrue(m.lookingAt());
        m.reset();
        assertNextGroup(m, 0, 4, "aaa ");
        assertNextGroup(m, 24, 28, "AAA ");
        assertFalse(m.find());

        p = Pattern.compile("(?i)(aaa|bbb) (?!xxx)");
        m = p.matcher("aaa yyy AAA XXX bbb YYY AAA yyy");
        assertFalse(m.matches());
        assertTrue(m.lookingAt());
        m.reset();
        assertNextGroup(m, 0, 4, "aaa ", "aaa");
        assertNextGroup(m, 16, 20, "bbb ", "bbb");
        assertNextGroup(m, 24, 28, "AAA ", "AAA");
        assertFalse(m.find());

        p = Pattern.compile("(?i)(?:aaa|bbb) (?!xxx)");
        m = p.matcher("aaa yyy AAA XXX bbb YYY AAA yyy");
        assertFalse(m.matches());
        assertTrue(m.lookingAt());
        m.reset();
        assertNextGroup(m, 0, 4, "aaa ");
        assertNextGroup(m, 16, 20, "bbb ");
        assertNextGroup(m, 24, 28, "AAA ");
        assertFalse(m.find());
    }

    @Test
    public void testPositiveLookBehindDemo() {
        /* Japanese : "肯定後読み" or "肯定戻り読み" 
         * (?<=regexp) -> regexp が終わる位置にマッチする。
         */

        Pattern p = Pattern.compile("(?i)(?<=xxx) aaa");
        Matcher m = p.matcher("xxx aaa yyy AAA XXX AAA XXX bbb");
        assertFalse(m.matches());
        assertFalse(m.lookingAt()); // !?
        m.reset();
        assertNextGroup(m, 3, 7, " aaa");
        assertNextGroup(m, 19, 23, " AAA");
        assertFalse(m.find());

        p = Pattern.compile("(?i)(?<=xxx) (aaa|bbb)");
        m = p.matcher("xxx aaa yyy AAA XXX AAA XXX bbb");
        assertFalse(m.matches());
        assertFalse(m.lookingAt()); // !?
        m.reset();
        assertNextGroup(m, 3, 7, " aaa", "aaa");
        assertNextGroup(m, 19, 23, " AAA", "AAA");
        assertNextGroup(m, 27, 31, " bbb", "bbb");
        assertFalse(m.find());
    }

    @Test
    public void testNegativeLookBehindDemo() {
        /* Japanese : "否定後読み" or "否定戻り読み"
         * (?<!regexp) -> regexp で終わらない位置にマッチする。 
         */
        Pattern p = Pattern.compile("(?i)(?<!xxx)");
        Matcher m = p.matcher("xxx aaa yyy AAA XXX AAA XXX bbb");
        assertFalse(m.matches());
        assertTrue(m.lookingAt());
        m.reset();
        assertNextGroup(m, 0, 0, "");
        assertNextGroup(m, 1, 1, "");
        assertNextGroup(m, 2, 2, "");
        // ... xxx で終わらない位置にマッチする = xxx で終わる位置[3]がマッチしない。
        assertNextGroup(m, 4, 4, "");
        assertNextGroup(m, 5, 5, "");
        // ... (snip) ...

        p = Pattern.compile("(?i)(?<!xxx) aaa");
        m = p.matcher("xxx aaa yyy AAA XXX AAA AaA");
        assertFalse(m.matches());
        assertFalse(m.lookingAt()); // !?
        m.reset();
        assertNextGroup(m, 11, 15, " AAA");
        assertNextGroup(m, 23, 27, " AaA");
        assertFalse(m.find());

        p = Pattern.compile("(?i)(?<!xxx) (aaa|bbb)");
        m = p.matcher("xxx aaa yyy AAA XXX AAA bbb");
        assertFalse(m.matches());
        assertFalse(m.lookingAt()); // !?
        m.reset();
        assertNextGroup(m, 11, 15, " AAA", "AAA");
        assertNextGroup(m, 23, 27, " bbb", "bbb");
        assertFalse(m.find());
    }
}
