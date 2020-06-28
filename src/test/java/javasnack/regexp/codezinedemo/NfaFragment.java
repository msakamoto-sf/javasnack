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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import org.apache.commons.lang3.tuple.Pair;

public class NfaFragment {
    /** starting state */
    public int start = 0;
    /** set of acceptable states */
    public Set<Integer> accept = Collections.emptySet();

    /**
     * TODO
     * self.mapは、(状態, 入力文字)のタプルをキーとし、
     * 次に遷移する状態の集合を値として持ちます。
     * これはちょうどNFAの遷移関数の引数と戻り値と一致する定義になってますので、
     * self.mapをnfa.transitionの関数の形に変更するのは簡単です。
     */
    private Map<Pair<Integer, Optional<Character>>, Set<Integer>> map = new HashMap<>();

    public void connect(final int stateFrom, final Optional<Character> c, final int stateTo) {
        final var key = Pair.of(stateFrom, c);
        final Set<Integer> transitionSet = this.map.getOrDefault(key, new HashSet<>());
        transitionSet.add(stateTo);
        this.map.put(key, transitionSet);
    }

    public NfaFragment createSkelton() {
        final NfaFragment r = new NfaFragment();
        for (final Entry<Pair<Integer, Optional<Character>>, Set<Integer>> e : this.map.entrySet()) {
            r.map.put(e.getKey(), new HashSet<>(e.getValue()));
        }
        return r;
    }

    public NfaFragment createOr(final NfaFragment fragment) {
        final NfaFragment r = this.createSkelton();
        for (final Entry<Pair<Integer, Optional<Character>>, Set<Integer>> e : fragment.map.entrySet()) {
            r.map.put(e.getKey(), new HashSet<>(e.getValue()));
        }
        return r;
    }

    public Nfa build() {
        final Map<Pair<Integer, Optional<Character>>, Set<Integer>> mapref = this.map;
        final BiFunction<Integer, Optional<Character>, Set<Integer>> transition = (final Integer state,
                final Optional<Character> c) -> {
            return Collections.unmodifiableSet(mapref.getOrDefault(Pair.of(state, c), Collections.emptySet()));
        };
        return Nfa.of(transition, this.start, this.accept);
    }
}
