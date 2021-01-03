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
}
