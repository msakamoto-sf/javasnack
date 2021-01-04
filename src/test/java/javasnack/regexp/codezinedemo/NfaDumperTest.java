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

public class NfaDumperTest {
    @Test
    public void testDump() {
        final Map<StateAndInputCharacter, Set<Integer>> tbl0 = new HashMap<>();
        tbl0.put(StateAndInputCharacter.of(1, Optional.of('a')), Set.of(2, 3));
        tbl0.put(StateAndInputCharacter.of(1, Optional.of('c')), Set.of(4, 5));
        tbl0.put(StateAndInputCharacter.of(2, Optional.of('b')), Set.of(6));
        tbl0.put(StateAndInputCharacter.of(2, Optional.empty()), Set.of(3));
        tbl0.put(StateAndInputCharacter.of(3, Optional.of('B')), Set.of(7));
        tbl0.put(StateAndInputCharacter.of(4, Optional.of('d')), Set.of(8));
        tbl0.put(StateAndInputCharacter.of(4, Optional.empty()), Set.of(5));
        tbl0.put(StateAndInputCharacter.of(5, Optional.of('D')), Set.of(9));
        final NfaStateTransitFunction f0 = (int start, Optional<Character> input) -> {
            final StateAndInputCharacter key = StateAndInputCharacter.of(start, input);
            return tbl0.getOrDefault(key, Collections.emptySet());
        };
        final Nfa nfa0 = Nfa.of(f0, 1, Set.of(6, 7, 8, 9));
        final NfaDumper dumper = new NfaDumper(nfa0);
        assertThat(dumper.dump()).isEqualTo("start-state: 1\n"
                + "    'a' -> 2\n"
                + "    'a' -> 3\n"
                + "    'c' -> 4\n"
                + "    'c' -> 5\n"
                + "state: 2\n"
                + "    (ε) -> 3\n"
                + "    'b' -> 6\n"
                + "state: 3\n"
                + "    'B' -> 7\n"
                + "state: 4\n"
                + "    (ε) -> 5\n"
                + "    'd' -> 8\n"
                + "state: 5\n"
                + "    'D' -> 9\n"
                + "state: 3\n"
                + "    'B' -> 7\n"
                + "acceptable-state: 6\n"
                + "acceptable-state: 7\n"
                + "state: 5\n"
                + "    'D' -> 9\n"
                + "acceptable-state: 8\n"
                + "acceptable-state: 9\n"
                + "acceptable-state: 7\n"
                + "acceptable-state: 9\n");
    }
}