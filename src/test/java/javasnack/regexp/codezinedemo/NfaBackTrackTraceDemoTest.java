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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import javasnack.regexp.codezinedemo.Regexp.RegexpOption;

public class NfaBackTrackTraceDemoTest {

    static final int LENGTH = 3;

    @Test
    public void testExponentialDegreeOfAmbiguityDemo1NonMatch() {
        System.out.println(">>>> benchmark demo for EDA : Exponential Degree of Ambiguity(1-not-match)");
        final Regexp r = Regexp.compileNfa("(a|a)*", RegexpOption.DEBUG_LOG, RegexpOption.NFA_BACKTRACK);
        final String matchTo = "a".repeat(LENGTH) + "b";
        assertThat(r.match(matchTo)).isFalse();
        System.out.println("<<<<");
    }

    @Test
    public void testExponentialDegreeOfAmbiguityDemo1Match() {
        System.out.println(">>>> benchmark demo for EDA : Exponential Degree of Ambiguity(1-match)");
        final Regexp r = Regexp.compileNfa("(a|a)*", RegexpOption.DEBUG_LOG, RegexpOption.NFA_BACKTRACK);
        final String matchTo = "a".repeat(LENGTH);
        assertThat(r.match(matchTo)).isTrue();
        System.out.println("<<<<");
    }

    @Test
    public void testExponentialDegreeOfAmbiguityDemo2NonMatch() {
        System.out.println(">>>> benchmark demo for EDA : Exponential Degree of Ambiguity(2-not-match)");
        final Regexp r = Regexp.compileNfa("(a*)*", RegexpOption.DEBUG_LOG, RegexpOption.NFA_BACKTRACK);
        final String matchTo = "a".repeat(LENGTH) + "b";
        assertThat(r.match(matchTo)).isFalse();
        System.out.println("<<<<");
    }

    @Test
    public void testExponentialDegreeOfAmbiguityDemo2Match() {
        System.out.println(">>>> benchmark demo for EDA : Exponential Degree of Ambiguity(2-match)");
        final Regexp r = Regexp.compileNfa("(a*)*", RegexpOption.DEBUG_LOG, RegexpOption.NFA_BACKTRACK);
        final String matchTo = "a".repeat(LENGTH);
        assertThat(r.match(matchTo)).isTrue();
        System.out.println("<<<<");
    }

    @Test
    public void testInfiniteDegreeOfAmgibuityDemoNonMatch() {
        System.out.println(">>>> benchmark demo for IDA : Infinite Degree of Amgibuity(Polynomial)(not-match");
        final Regexp r = Regexp.compileNfa("a*a*", RegexpOption.DEBUG_LOG, RegexpOption.NFA_BACKTRACK);
        final String matchTo = "a".repeat(LENGTH) + "b";
        assertThat(r.match(matchTo)).isFalse();
        System.out.println("<<<<");
    }

    @Test
    public void testInfiniteDegreeOfAmgibuityDemoMatch() {
        System.out.println(">>>> benchmark demo for IDA : Infinite Degree of Amgibuity(Polynomial)(match)");
        final Regexp r = Regexp.compileNfa("a*a*", RegexpOption.DEBUG_LOG, RegexpOption.NFA_BACKTRACK);
        final String matchTo = "a".repeat(LENGTH);
        assertThat(r.match(matchTo)).isTrue();
        System.out.println("<<<<");
    }
}
