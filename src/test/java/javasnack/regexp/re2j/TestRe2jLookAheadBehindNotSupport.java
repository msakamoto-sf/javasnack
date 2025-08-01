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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.google.re2j.Pattern;

public class TestRe2jLookAheadBehindNotSupport {
    @Test
    public void testPositiveLookAheadDemo() {
        assertThatThrownBy(() -> {
            Pattern.compile("(?=xxx)");
        }).isInstanceOf(com.google.re2j.PatternSyntaxException.class)
                .hasMessage("error parsing regexp: invalid or unsupported Perl syntax: `(?=`");
    }

    @Test
    public void testRe2jNotSupportNegativeLookAhead() {
        assertThatThrownBy(() -> {
            Pattern.compile("(?!xxx)");
        }).isInstanceOf(com.google.re2j.PatternSyntaxException.class)
                .hasMessage("error parsing regexp: invalid or unsupported Perl syntax: `(?!`");
    }

    @Test
    public void testRe2jNotSupportPositiveLookBehind() {
        assertThatThrownBy(() -> {
            Pattern.compile("(?<=xxx) aaa");
        }).isInstanceOf(com.google.re2j.PatternSyntaxException.class)
                .hasMessage("error parsing regexp: invalid named capture: `(?<=xxx) aaa`");
    }

    @Test
    public void testRe2jNotSupportNegativeLookBehind() {
        assertThatThrownBy(() -> {
            Pattern.compile("(?<!xxx)");
        }).isInstanceOf(com.google.re2j.PatternSyntaxException.class)
                .hasMessage("error parsing regexp: invalid named capture: `(?<!xxx)`");
    }
}
