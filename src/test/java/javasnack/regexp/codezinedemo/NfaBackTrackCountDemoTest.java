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

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

public class NfaBackTrackCountDemoTest {

    // このテストケースは O(N) での動きになるため、ある程度大きな値で実験してもOK.
    static final int MAX_LENGTH = 10;

    static void countup(final String regexp, final boolean countToMatchedResult) {
        final Lexer lex0 = new Lexer(regexp);
        final Parser parser0 = new Parser(lex0);
        final Nfa nfa = parser0.expression();
        System.out.println("regexp,len,countOfBackTracked");
        IntStream.range(1, MAX_LENGTH + 1).forEach((len) -> {
            final String matchTo = "a".repeat(len) + (countToMatchedResult ? "" : "b");
            final NfaBackTrackRuntime nfaBackTrackRuntime = new NfaBackTrackRuntime(nfa);
            if (countToMatchedResult) {
                assertThat(nfaBackTrackRuntime.accept(matchTo)).isTrue();
            } else {
                assertThat(nfaBackTrackRuntime.accept(matchTo)).isFalse();
            }
            System.out.println(regexp + "," + len + "," + nfaBackTrackRuntime.getCountOfBackTracked());
        });
    }

    @Test
    public void testExponentialDegreeOfAmbiguityDemo1NonMatch() {
        System.out.println(">>>> count of backtracked demo for EDA : Exponential Degree of Ambiguity(1-not-match)");
        countup("(a|a)*", false);
        System.out.println("<<<<");
    }

    @Test
    public void testExponentialDegreeOfAmbiguityDemo1Match() {
        System.out.println(">>>> count of backtracked demo for EDA : Exponential Degree of Ambiguity(1-match)");
        countup("(a|a)*", true);
        System.out.println("<<<<");
    }

    @Test
    public void testExponentialDegreeOfAmbiguityDemo2NonMatch() {
        System.out.println(">>>> count of backtracked demo for EDA : Exponential Degree of Ambiguity(2-not-match)");
        countup("(a*)*", false);
        System.out.println("<<<<");
    }

    @Test
    public void testExponentialDegreeOfAmbiguityDemo2Match() {
        System.out.println(">>>> count of backtracked demo for EDA : Exponential Degree of Ambiguity(2-match)");
        countup("(a*)*", true);
        System.out.println("<<<<");
    }

    @Test
    public void testInfiniteDegreeOfAmgibuityDemoNonMatch() {
        System.out
                .println(">>>> count of backtracked demo for IDA : Infinite Degree of Amgibuity(Polynomial)(not-match");
        countup("a*a*", false);
        System.out.println("<<<<");
    }

    @Test
    public void testInfiniteDegreeOfAmgibuityDemoMatch() {
        System.out.println(">>>> count of backtracked demo for IDA : Infinite Degree of Amgibuity(Polynomial)(match)");
        countup("a*a*", true);
        System.out.println("<<<<");
    }
}
