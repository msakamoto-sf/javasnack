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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class Nfa2DfaRuntimeTest {
    @Test
    public void testNfa2DfaRuntimeAcceptance() {
        final Map<StateSetAndInputCharacter, Set<Integer>> tbl0 = new HashMap<>();
        tbl0.put(StateSetAndInputCharacter.of(Set.of(0, 1), 'a'), Set.of(1, 2));
        tbl0.put(StateSetAndInputCharacter.of(Set.of(0, 1), 'b'), Set.of(3, 4));
        tbl0.put(StateSetAndInputCharacter.of(Set.of(0, 1), 'c'), Set.of(5, 6));
        tbl0.put(StateSetAndInputCharacter.of(Set.of(1, 2), 'b'), Set.of(2, 3));
        tbl0.put(StateSetAndInputCharacter.of(Set.of(3, 4), 'a'), Set.of(4, 5));
        tbl0.put(StateSetAndInputCharacter.of(Set.of(5, 6), 'd'), Set.of(6, 7));

        final Nfa2DfaStateTransitFunction transition0 = (
                final Set<Integer> setOfCurrentState,
                final char character) -> {
            return tbl0.getOrDefault(
                    StateSetAndInputCharacter.of(setOfCurrentState, character),
                    Collections.emptySet());
        };

        /* Set.of() で unmodifiable な Set になる。
         * -> 受理可能かの判定処理において、元の受理可能状態の集合が誤って変更されていないことをチェックできる。
         */
        final Set<Integer> acceptableStateSet = Set.of(3, 4, 5);
        final var nfa2dfa = Nfa2Dfa.of(transition0, Set.of(0, 1), acceptableStateSet);
        var runtime0 = new Nfa2DfaRuntime(nfa2dfa);
        assertTrue(runtime0.accept("ab"));
        runtime0 = new Nfa2DfaRuntime(nfa2dfa);
        assertTrue(runtime0.accept("ba"));
        runtime0 = new Nfa2DfaRuntime(nfa2dfa);
        assertFalse(runtime0.accept("cd"));
        runtime0 = new Nfa2DfaRuntime(nfa2dfa);
        assertFalse(runtime0.accept("xxxxx"));

        // 念の為確認
        assertThat(acceptableStateSet).isEqualTo(Set.of(3, 4, 5));
    }
}
