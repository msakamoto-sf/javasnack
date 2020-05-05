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

import static javasnack.regexp.StartEnd.se;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;

public class TestRe2jQuantifiersDemo {
    @Test
    public void testRe2jNotSupportPossesiveQuantifier() {
        // 強欲な数量子はre2jではサポートしていない。
        assertThatThrownBy(() -> {
            Pattern.compile("a*+");
        }).isInstanceOf(com.google.re2j.PatternSyntaxException.class)
                .hasMessage("error parsing regexp: invalid nested repetition operator: `*+`");
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
    }
}
