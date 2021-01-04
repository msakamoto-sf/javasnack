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
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Nfa2DfaDumper {

    private final Nfa2Dfa nfa2dfa;
    private final Queue<Set<Integer>> queue = new ArrayDeque<>();
    private final Set<Set<Integer>> done = new HashSet<>();
    private Set<Integer> currentState = null;

    public Nfa2DfaDumper(final Nfa2Dfa nfa2dfa) {
        this.nfa2dfa = nfa2dfa;
        this.queue.add(nfa2dfa.start);
    }

    public String dump() {
        final List<Character> unicodes = IntStream.rangeClosed(0, 0xFFFF)
                .boxed()
                .map(unicodeCodePoint -> Character.valueOf((char) unicodeCodePoint.intValue()))
                .collect(Collectors.toList());
        final StringBuilder dumpTo = new StringBuilder();
        while (!queue.isEmpty()) {
            // 検査対象の遷移元状態をキューから取り出す
            this.currentState = this.queue.remove(); // あえて要素がなければ例外をthrowさせ、異常検知させる。
            this.dumpState(dumpTo, this.currentState);
            // 検査対象の遷移元状態を、完了セットに追加 (これは遷移先が空であっても、「検査自体は完了」したので追加する)
            this.done.add(this.currentState);
            for (char c : unicodes) {
                this.transition(dumpTo, c);
            }
        }
        return dumpTo.toString();
    }

    private void dumpState(final StringBuilder dumpTo, final Set<Integer> state) {
        if (state.equals(nfa2dfa.start)) {
            dumpTo.append("start-");
        }
        if (!Nfa2DfaRuntime.intersectIsEmpty(nfa2dfa.nfaAcceptableStateSet, state)) {
            dumpTo.append("acceptable-");
        }
        dumpTo.append("state: " + state + "\n");
    }

    private void transited(final StringBuilder dumpTo, final char character, final Set<Integer> setOfNextState) {
        dumpTo.append("    '" + character + "' -> " + setOfNextState + "\n");
        if (!done.contains(setOfNextState)) {
            this.queue.add(setOfNextState);
        }
    }

    private void transition(final StringBuilder dumpTo, final char character) {
        final Set<Integer> setOfNextState = this.nfa2dfa.transition.apply(currentState, character);
        if (setOfNextState.isEmpty()) {
            // 空集合状態に向かう遷移は略記
            return;
        }
        this.transited(dumpTo, character, setOfNextState);
    }
}