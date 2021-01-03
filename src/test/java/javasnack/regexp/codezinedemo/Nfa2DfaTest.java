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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class Nfa2DfaTest {
    @Test
    public void testNfa2Dfa() {
        final Map<StateAndInputCharacter, Set<Integer>> tbl0 = new HashMap<>();
        tbl0.put(StateAndInputCharacter.of(0, Optional.of('a')), Set.of(1));
        tbl0.put(StateAndInputCharacter.of(1, Optional.of('b')), Set.of(3)); // ab
        tbl0.put(StateAndInputCharacter.of(0, Optional.of('c')), Set.of(4));
        tbl0.put(StateAndInputCharacter.of(4, Optional.of('d')), Set.of(5)); // cd
        tbl0.put(StateAndInputCharacter.of(0, Optional.empty()), Set.of(6));
        tbl0.put(StateAndInputCharacter.of(6, Optional.of('e')), Set.of(7));
        tbl0.put(StateAndInputCharacter.of(7, Optional.of('f')), Set.of(8)); // ef
        tbl0.put(StateAndInputCharacter.of(7, Optional.empty()), Set.of(9)); // e = ef*

        final NfaStateTransitFunction f0 = (int start, Optional<Character> input) -> {
            return tbl0.getOrDefault(StateAndInputCharacter.of(start, input), Collections.emptySet());
        };

        final Nfa nfa0 = Nfa.of(f0, 0, Set.of(3, 5, 8, 9));
        final Nfa2Dfa nfa2dfa = Nfa2Dfa.from(nfa0);

        assertThat(nfa2dfa.start).isEqualTo(Set.of(0, 6));
        assertThat(nfa2dfa.nfaAcceptableStateSet).isEqualTo(Set.of(3, 5, 8, 9));

        Set<Integer> r = nfa2dfa.transition.apply(Set.of(0, 6), 'a');
        assertThat(r).isEqualTo(Set.of(1));
        r = nfa2dfa.transition.apply(r, 'b');
        assertThat(r).isEqualTo(Set.of(3)); // ab

        r = nfa2dfa.transition.apply(Set.of(0, 6), 'c');
        assertThat(r).isEqualTo(Set.of(4));
        r = nfa2dfa.transition.apply(r, 'd');
        assertThat(r).isEqualTo(Set.of(5)); // cd

        r = nfa2dfa.transition.apply(Set.of(0, 6), 'e');
        assertThat(r).isEqualTo(Set.of(7, 9)); // ef or e
        r = nfa2dfa.transition.apply(Set.of(7, 9), 'f');
        assertThat(r).isEqualTo(Set.of(8)); // ef
    }
}
