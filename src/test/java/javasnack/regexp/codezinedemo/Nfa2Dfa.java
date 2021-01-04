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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * NFA to DFA representation
 * 
 * 詳細な解説は同ディレクトリ中の README.md の [4] 参照
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor(staticName = "of")
public class Nfa2Dfa {
    /** transition function */
    public final Nfa2DfaStateTransitFunction transition;
    /** starting state */
    public final Set<Integer> start;
    /** set of acceptable states */
    //public final Set<Set<Integer>> accept;
    // なぜ上ではなく下の定義にしたのかについては、同ディレクトリ中の README.md の [4] 参照
    public final Set<Integer> nfaAcceptableStateSet;

    public static Nfa2Dfa from(final Nfa nfa, final boolean enableTraceLog, final boolean enableTransitionCache) {
        // パフォーマンス向上のため、遷移関数の引数と値ペアをキャッシュしておくmap
        final Map<StateSetAndInputCharacter, Set<Integer>> memocache = new HashMap<>();

        final Nfa2DfaStateTransitFunction transition0 = (
                final Set<Integer> setOfCurrentState,
                final char character) -> {

            // キャッシュ用のkey
            final StateSetAndInputCharacter cacheKey = StateSetAndInputCharacter.of(setOfCurrentState, character);
            if (enableTransitionCache && memocache.containsKey(cacheKey)) {
                // キャッシュ有効でキャッシュhitしたときは、その値を返す。
                return memocache.get(cacheKey);
            }

            final Set<Integer> setOfNextState = new HashSet<>();
            for (int currentState : setOfCurrentState) {
                final Set<Integer> nfaResult = nfa.transition.apply(currentState, Optional.of(character));
                setOfNextState.addAll(nfaResult);
            }
            final Set<Integer> r = nfa.expandEpsilon(setOfNextState);
            if (enableTraceLog) {
                System.out.println("NFA2DFA TRANSITION: (" + setOfCurrentState + ", '" + character + "') => " + r);
            }

            if (enableTransitionCache) {
                // キャッシュ有効のときはキャッシュに保存する。
                memocache.put(cacheKey, r);
            }
            return r;
        };
        // 元のNFAの初期状態から空文字(ε)遷移可能な状態も集約した集合を初期状態とする。
        final Set<Integer> setOfInitialState = nfa.expandEpsilon(Set.of(nfa.start));
        // 受理可能状態についてはパフォーマンスを考慮して元のNFAの受理可能状態の集合をそのまま使う。
        return Nfa2Dfa.of(transition0, setOfInitialState, nfa.accept);
    }

    public static Nfa2Dfa from(final Nfa nfa) {
        return from(nfa, false, false);
    }
}
