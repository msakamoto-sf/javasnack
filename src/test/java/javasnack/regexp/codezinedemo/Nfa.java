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
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Nondeterministic Finite Automaton : NFA representation.
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor(staticName = "of")
public class Nfa {
    /** transition function */
    public final NfaStateTransitFunction transition;
    /** starting state */
    public final int start;
    /** set of acceptable states */
    public final Set<Integer> accept;

    /**
     * NFAにおける状態の集合について、各状態から空文字(ε)で遷移可能な状態を集約し、元の集合にマージする。
     * 
     * NFAをDFAに変換するには、NFAにおける「状態の集合」それ自体をDFAの「状態」として扱う。
     * NFAの「状態の集合」に対してそこから空文字で遷移可能な状態も洗い出してマージし、
     * それを以てDFAの「状態」にするためのメソッドとなる。
     * 
     * 詳細な解説は同ディレクトリ中の README.md の [4] 参照
     * 
     * @param states 元のNFAの状態集合
     * @return statesおよび、statesから空文字(ε)で遷移可能な状態の集合 (= statesも含まれる)
     */
    public Set<Integer> expandEpsilon(Set<Integer> states) {
        /* 遷移関数で取り出した状態集合について、そこからさらに空文字で遷移可能な場合がある。
         * そのため、取り出した状態集合に対して1つずつ「これは空文字で遷移可能として検査済みの状態か」判定する必要がある。
         * もし未検査の状態があれば、それを検査対象の状態集合に追加する。
         * これを実現するため、引数で渡されたチェック対象の状態集合を queue 化し、
         * 検査済みの状態集合でマークするようにする。
         */
        // 空文字で遷移可能か検査対象の状態集合
        Queue<Integer> queue = new ArrayDeque<>(states);
        // 完了セット : 遷移元の状態の集合 + そこから空文字で遷移可能な状態の集合
        Set<Integer> done = new HashSet<>();
        while (!queue.isEmpty()) {
            // 検査対象の遷移元状態をキューから取り出す
            final int state = queue.remove(); // あえて要素がなければ例外をthrowさせ、異常検知させる。
            // 空文字を入力したときの遷移先状態の集合を取得
            final Set<Integer> nextStates = this.transition.apply(state, Optional.empty());
            // 検査対象の遷移元状態を、完了セットに追加 (これは遷移先が空であっても、「検査自体は完了」したので追加する)
            done.add(state);
            for (int nextState : nextStates) {
                if (!done.contains(nextState)) {
                    /* もし遷移先状態について、完了セットに入っていないものがあれば、
                     * それは「そこから更に空文字で遷移できる状態が無いか検査すべき状態」となる。
                     * よって検査対象のキューに追加する。
                     */
                    queue.add(nextState);
                }
            }
        }
        return Collections.unmodifiableSet(done);
    }

}
