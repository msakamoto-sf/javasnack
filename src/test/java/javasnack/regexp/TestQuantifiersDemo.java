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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import lombok.Value;

public class TestQuantifiersDemo {

    @Value(staticConstructor = "of")
    static class StartEnd {
        private final int start;
        private final int end;
        private final int groupIndex;
        private final String groupText;
    }

    static StartEnd se(final int start, final int end, final int groupIndex, final String groupText) {
        return StartEnd.of(start, end, groupIndex, groupText);
    }

    static StartEnd se(final int start, final int end) {
        return StartEnd.of(start, end, -1, "");
    }

    static StartEnd se(final Matcher m) {
        return StartEnd.of(m.start(), m.end(), -1, "");
    }

    static StartEnd se(final Matcher m, final int groupIndex) {
        return StartEnd.of(m.start(groupIndex), m.end(groupIndex), groupIndex, m.group(groupIndex));
    }

    @Test
    public void testZeroOrMoreTimesDemo() {
        // 最長一致数量子
        final Pattern greedy1 = Pattern.compile("((a*)(a))");
        Matcher m = greedy1.matcher("aaaaa");
        assertThat(m.find()).isTrue();
        assertThat(se(m)).isEqualTo(se(0, 5));
        assertThat(m.groupCount()).isEqualTo(3);
        assertThat(se(m, 0)).isEqualTo(se(0, 5, 0, "aaaaa"));
        assertThat(se(m, 1)).isEqualTo(se(0, 5, 1, "aaaaa"));
        assertThat(se(m, 2)).isEqualTo(se(0, 4, 2, "aaaa"));
        assertThat(se(m, 3)).isEqualTo(se(4, 5, 3, "a"));

        // 最短一致数量子
        final Pattern reluctant1 = Pattern.compile("((a*?)(a))");
        m = reluctant1.matcher("aaaaa");
        assertThat(m.find()).isTrue();
        assertThat(se(m)).isEqualTo(se(0, 1));
        assertThat(m.groupCount()).isEqualTo(3);
        assertThat(se(m, 0)).isEqualTo(se(0, 1, 0, "a"));
        assertThat(se(m, 1)).isEqualTo(se(0, 1, 1, "a"));
        assertThat(se(m, 2)).isEqualTo(se(0, 0, 2, ""));
        assertThat(se(m, 3)).isEqualTo(se(0, 1, 3, "a"));

        // 強欲な数量子
        final Pattern possessive1 = Pattern.compile("((a*+)(a))");
        m = possessive1.matcher("aaaaa");
        assertThat(m.find()).isFalse();
        final Pattern possessive2 = Pattern.compile("(a*+)");
        m = possessive2.matcher("aaaaa");
        assertThat(m.find()).isTrue();
        assertThat(se(m)).isEqualTo(se(0, 5));
        assertThat(m.groupCount()).isEqualTo(1);
        assertThat(se(m, 0)).isEqualTo(se(0, 5, 0, "aaaaa"));
        assertThat(se(m, 1)).isEqualTo(se(0, 5, 1, "aaaaa"));
    }

    @Test
    public void testOneOrMoreTimesDemo() {
        // 最長一致数量子
        final Pattern greedy1 = Pattern.compile("((a+)(a))");
        Matcher m = greedy1.matcher("aaaaa");
        assertThat(m.find()).isTrue();
        assertThat(se(m)).isEqualTo(se(0, 5));
        assertThat(m.groupCount()).isEqualTo(3);
        assertThat(se(m, 0)).isEqualTo(se(0, 5, 0, "aaaaa"));
        assertThat(se(m, 1)).isEqualTo(se(0, 5, 1, "aaaaa"));
        assertThat(se(m, 2)).isEqualTo(se(0, 4, 2, "aaaa"));
        assertThat(se(m, 3)).isEqualTo(se(4, 5, 3, "a"));

        // 最短一致数量子
        final Pattern reluctant1 = Pattern.compile("((a+?)(a))");
        m = reluctant1.matcher("aaaaa");
        assertThat(m.find()).isTrue();
        assertThat(se(m)).isEqualTo(se(0, 2));
        assertThat(m.groupCount()).isEqualTo(3);
        assertThat(se(m, 0)).isEqualTo(se(0, 2, 0, "aa"));
        assertThat(se(m, 1)).isEqualTo(se(0, 2, 1, "aa"));
        assertThat(se(m, 2)).isEqualTo(se(0, 1, 2, "a"));
        assertThat(se(m, 3)).isEqualTo(se(1, 2, 3, "a"));

        // 強欲な数量子
        final Pattern possessive1 = Pattern.compile("((a++)(a))");
        m = possessive1.matcher("aaaaa");
        assertThat(m.find()).isFalse();
        final Pattern possessive2 = Pattern.compile("(a++)");
        m = possessive2.matcher("aaaaa");
        assertThat(m.find()).isTrue();
        assertThat(se(m)).isEqualTo(se(0, 5));
        assertThat(m.groupCount()).isEqualTo(1);
        assertThat(se(m, 0)).isEqualTo(se(0, 5, 0, "aaaaa"));
        assertThat(se(m, 1)).isEqualTo(se(0, 5, 1, "aaaaa"));
    }

    @Test
    public void testZeroOrMoreTimesDemo2() {
        // 最長一致数量子
        final Pattern greedy1 = Pattern.compile("(,(a*),(a*),(a*),)");
        Matcher m = greedy1.matcher(",,,a,,aa,,,");
        assertThat(m.find()).isTrue();
        assertThat(se(m)).isEqualTo(se(0, 5));
        assertThat(m.groupCount()).isEqualTo(4);
        assertThat(se(m, 0)).isEqualTo(se(0, 5, 0, ",,,a,"));
        assertThat(se(m, 1)).isEqualTo(se(0, 5, 1, ",,,a,"));
        assertThat(se(m, 2)).isEqualTo(se(1, 1, 2, ""));
        assertThat(se(m, 3)).isEqualTo(se(2, 2, 3, ""));
        assertThat(se(m, 4)).isEqualTo(se(3, 4, 4, "a"));
        assertThat(m.find()).isTrue();
        assertThat(se(m)).isEqualTo(se(5, 11));
        assertThat(m.groupCount()).isEqualTo(4);
        assertThat(se(m, 0)).isEqualTo(se(5, 11, 0, ",aa,,,"));
        assertThat(se(m, 1)).isEqualTo(se(5, 11, 1, ",aa,,,"));
        assertThat(se(m, 2)).isEqualTo(se(6, 8, 2, "aa"));
        assertThat(se(m, 3)).isEqualTo(se(9, 9, 3, ""));
        assertThat(se(m, 4)).isEqualTo(se(10, 10, 4, ""));
        assertThat(m.find()).isFalse();

        // 最短一致数量子
        final Pattern reluctant1 = Pattern.compile("(,(a*?),(a*?),(a*?),)");
        m = reluctant1.matcher(",,,a,,aa,,,");
        assertThat(m.find()).isTrue();
        assertThat(se(m)).isEqualTo(se(0, 5));
        assertThat(m.groupCount()).isEqualTo(4);
        assertThat(se(m, 0)).isEqualTo(se(0, 5, 0, ",,,a,"));
        assertThat(se(m, 1)).isEqualTo(se(0, 5, 1, ",,,a,"));
        assertThat(se(m, 2)).isEqualTo(se(1, 1, 2, ""));
        assertThat(se(m, 3)).isEqualTo(se(2, 2, 3, ""));
        assertThat(se(m, 4)).isEqualTo(se(3, 4, 4, "a"));
        assertThat(m.find()).isTrue();
        assertThat(se(m)).isEqualTo(se(5, 11));
        assertThat(m.groupCount()).isEqualTo(4);
        assertThat(se(m, 0)).isEqualTo(se(5, 11, 0, ",aa,,,"));
        assertThat(se(m, 1)).isEqualTo(se(5, 11, 1, ",aa,,,"));
        assertThat(se(m, 2)).isEqualTo(se(6, 8, 2, "aa"));
        assertThat(se(m, 3)).isEqualTo(se(9, 9, 3, ""));
        assertThat(se(m, 4)).isEqualTo(se(10, 10, 4, ""));
        assertThat(m.find()).isFalse();

        // 強欲な数量子
        final Pattern possessive1 = Pattern.compile("(,(a*+),(a*+),(a*+),)");
        m = possessive1.matcher(",,,a,,aa,,,");
        assertThat(m.find()).isTrue();
        assertThat(se(m)).isEqualTo(se(0, 5));
        assertThat(m.groupCount()).isEqualTo(4);
        assertThat(se(m, 0)).isEqualTo(se(0, 5, 0, ",,,a,"));
        assertThat(se(m, 1)).isEqualTo(se(0, 5, 1, ",,,a,"));
        assertThat(se(m, 2)).isEqualTo(se(1, 1, 2, ""));
        assertThat(se(m, 3)).isEqualTo(se(2, 2, 3, ""));
        assertThat(se(m, 4)).isEqualTo(se(3, 4, 4, "a"));
        assertThat(m.find()).isTrue();
        assertThat(se(m)).isEqualTo(se(5, 11));
        assertThat(m.groupCount()).isEqualTo(4);
        assertThat(se(m, 0)).isEqualTo(se(5, 11, 0, ",aa,,,"));
        assertThat(se(m, 1)).isEqualTo(se(5, 11, 1, ",aa,,,"));
        assertThat(se(m, 2)).isEqualTo(se(6, 8, 2, "aa"));
        assertThat(se(m, 3)).isEqualTo(se(9, 9, 3, ""));
        assertThat(se(m, 4)).isEqualTo(se(10, 10, 4, ""));
        assertThat(m.find()).isFalse();
    }
}
