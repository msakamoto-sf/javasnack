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

import java.util.HashSet;
import java.util.Set;

public class Nfa2DfaRuntime {
    private final Nfa2Dfa nfa2dfa;
    private Set<Integer> currentState;

    public Nfa2DfaRuntime(final Nfa2Dfa dfa) {
        this.nfa2dfa = dfa;
        this.currentState = dfa.start;
    }

    private void transit(final char c) {
        this.currentState = nfa2dfa.transition.apply(currentState, c);
    }

    private boolean isCurrentStatusAcceptable() {
        // 詳細な解説は同ディレクトリ中の README.md の [4] 参照
        /* (a) 現在とり得る状態の集合 と (b) 元のNFAの受理可能状態の集合 の積集合を取る。
         * javaのライブラリの都合で (a) の内容が書き換えられる点に注意。
         * -> 積集合の結果が空でなければ、(a) には (b) のうち1つ以上の要素が含まれているので、受理可能とみなせる。
         */
        final Set<Integer> copyOfAcceptableStates = new HashSet<>(this.nfa2dfa.nfaAcceptableStateSet);
        copyOfAcceptableStates.retainAll(currentState);
        return !copyOfAcceptableStates.isEmpty();
    }

    public boolean accept(final String input) {
        input.codePoints().mapToObj(c -> (char) c).forEach(c -> this.transit(c));
        return this.isCurrentStatusAcceptable();
    }
}
