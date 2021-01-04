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

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public class NfaRuntime {
    /* 連載記事中には解説が無いが、(6) からDLできるサンプルコードには dfareg/nfa.py として収録されている。
     * 2種類含まれており、こちらは幅優先で探索するタイプ。
     * 幅優先 : 選択しうる状態を集合とし、全ての可能性を保持しつつマッチするまで広げて行く
     * -> 「選択可能な状態の集合」を扱うため、DFAの処理に近い実装コードとなっている。
     * 
     * 状態を集合として扱う際には、なるべくデバッグやトレースがしやすいよう、HashSet ではなく TreeSet で扱う。
     * NFAの実装では状態を整数値として扱うため、TreeSetでラップすることにより、
     * 集合に対するiterate処理が常に昇順でどうさすることとなり、デバッグやトレース時に理解しやすくなる。
     */
    private final Nfa nfa;
    /** 「選択可能な状態の集合」 = 現在遷移可能な全ての未来の選択肢 */
    private Set<Integer> currentState;

    public NfaRuntime(final Nfa nfa) {
        this.nfa = nfa;
        // 初期状態について、空文字(ε)で遷移可能な選択肢をマージしておく。
        this.currentState = new TreeSet<>(nfa.expandEpsilon(Set.of(nfa.start)));
    }

    private void transit(final char c) {
        final Set<Integer> setOfNextState = new TreeSet<>();
        for (int state : currentState) {
            /* 現在の状態が「現在から選択可能な状態の集合」なので、それぞれについて遷移関数を実行。
             * -> 結果となる「遷移可能な状態の集合」の和集合が、新たな「現在の状態」となる。
             * こうした扱い方はDFAに近いイメージ。
             */
            setOfNextState.addAll(nfa.transition.apply(state, Optional.of(c)));
        }
        /* 和集合から空文字(ε)で遷移可能な選択肢をマージする。
         * -> 初期状態でもマージしているので、全体として空文字(ε)の遷移を除去した形になっている。
         */
        this.currentState = new TreeSet<>(nfa.expandEpsilon(setOfNextState));
    }

    private boolean isCurrentStatusAcceptable() {
        for (int state : currentState) {
            /* 現在の状態が「現在から選択可能な状態の集合」なので、それぞれについて受理可能か判定。
             * -> 1つでも受理可能な状態が見つかれば、その時点で受理可能と判定する。
             */
            if (this.nfa.accept.contains(state)) {
                return true;
            }
        }
        return false;
    }

    public boolean accept(final String input) {
        input.codePoints().mapToObj(c -> (char) c).forEach(c -> this.transit(c));
        return this.isCurrentStatusAcceptable();
    }
}
