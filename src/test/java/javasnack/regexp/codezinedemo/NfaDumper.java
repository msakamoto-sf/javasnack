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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NfaDumper {

    private final Nfa nfa;
    private final Queue<Integer> queue = new ArrayDeque<>();
    private final Set<Integer> done = new HashSet<>();
    private int currentState = -1;

    public NfaDumper(final Nfa nfa2dfa) {
        this.nfa = nfa2dfa;
        this.queue.add(nfa2dfa.start);
    }

    public String dump() {
        final List<Optional<Character>> unicodes = IntStream.rangeClosed(0, 0xFFFF)
                .boxed()
                .map(unicodeCodePoint -> Optional.of(Character.valueOf((char) unicodeCodePoint.intValue())))
                .collect(Collectors.toList());
        // NFAで検査(総当り)する文字集合には空文字(ε)も含む。
        final List<Optional<Character>> unicodesAndEpsilon = new ArrayList<>(unicodes.size() + 1);
        unicodesAndEpsilon.add(Optional.empty());
        unicodesAndEpsilon.addAll(unicodes);

        final StringBuilder dumpTo = new StringBuilder();
        while (!queue.isEmpty()) {
            // 検査対象の遷移元状態をキューから取り出す
            this.currentState = this.queue.remove(); // あえて要素がなければ例外をthrowさせ、異常検知させる。
            this.dumpState(dumpTo, this.currentState);
            // 検査対象の遷移元状態を、完了セットに追加 (これは遷移先が空であっても、「検査自体は完了」したので追加する)
            this.done.add(this.currentState);
            for (Optional<Character> c : unicodesAndEpsilon) {
                this.transition(dumpTo, c);
            }
        }
        return dumpTo.toString();
    }

    private void dumpState(final StringBuilder dumpTo, final int state) {
        if (state == nfa.start) {
            dumpTo.append("start-");
        }
        if (nfa.accept.contains(Integer.valueOf(state))) {
            dumpTo.append("acceptable-");
        }
        dumpTo.append("state: " + state + "\n");
    }

    private void transited(final StringBuilder dumpTo, final Optional<Character> character, final int nextState) {
        dumpTo.append("    " + NfaFragment.optchar(character) + " -> " + nextState + "\n");
        if (!done.contains(nextState)) {
            this.queue.add(nextState);
        }
    }

    private void transition(final StringBuilder dumpTo, final Optional<Character> character) {
        // 状態の集合に対する総当りについて、常に同じ順序で総当りさせるため、ソートされた TreeSet に変換する。
        final Set<Integer> setOfNextState = new TreeSet<>(this.nfa.transition.apply(currentState, character));
        for (int nextState : setOfNextState) {
            this.transited(dumpTo, character, nextState);
        }
    }
}