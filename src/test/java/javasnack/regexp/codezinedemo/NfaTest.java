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

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class NfaTest {
    @Test
    public void testExpandEpsilon0() {
        final NfaStateTransitFunction f0 = (int start, Optional<Character> input) -> {
            return Collections.emptySet();
        };
        final Nfa nfa0 = Nfa.of(f0, 1, Collections.emptySet());
        Set<Integer> r = nfa0.expandEpsilon(Collections.emptySet());
        assertThat(r).isEmpty();

        r = nfa0.expandEpsilon(Set.of(1));
        assertThat(r).isEqualTo(Set.of(1));

        r = nfa0.expandEpsilon(Set.of(1, 2));
        assertThat(r).isEqualTo(Set.of(1, 2));

        r = nfa0.expandEpsilon(Set.of(1, 2, 3));
        assertThat(r).isEqualTo(Set.of(1, 2, 3));
    }

    @Test
    public void testExpandEpsilon1() {
        final NfaStateTransitFunction f0 = (int start, Optional<Character> input) -> {
            if (input.isPresent()) {
                // このテストケースでの呼び出しにおいては、空文字(ε)以外は渡されない前提。
                throw new IllegalStateException("illegal input character: " + input.get());
            }
            switch (start) {
            case 1:
                return Set.of(10, 11);
            case 2:
                return Set.of(20, 21);
            case 10:
                return Set.of(100, 101, 300);
            case 20:
                return Set.of(200, 201, 300);
            default:
                return Collections.emptySet();
            }
        };
        final Nfa nfa0 = Nfa.of(f0, 1, Collections.emptySet());
        Set<Integer> r = nfa0.expandEpsilon(Collections.emptySet());
        assertThat(r).isEmpty();

        r = nfa0.expandEpsilon(Set.of(1));
        assertThat(r).isEqualTo(Set.of(1, 10, 11, 100, 101, 300));

        r = nfa0.expandEpsilon(Set.of(2));
        assertThat(r).isEqualTo(Set.of(2, 20, 21, 200, 201, 300));

        r = nfa0.expandEpsilon(Set.of(1, 2));
        assertThat(r).isEqualTo(Set.of(1, 10, 11, 100, 101, 2, 20, 21, 200, 201, 300));

        r = nfa0.expandEpsilon(Set.of(1, 2, 3));
        assertThat(r).isEqualTo(Set.of(1, 10, 11, 100, 101, 2, 20, 21, 200, 201, 300, 3));
    }
}
