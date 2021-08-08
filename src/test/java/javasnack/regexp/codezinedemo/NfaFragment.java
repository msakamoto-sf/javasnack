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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

public class NfaFragment {
    /* NFA全体ではなく、NFAの一部分としての
     * [初期状態, 状態S x 文字C に対する遷移先状態S'の集合, 受理可能状態の集合]
     * を表現する。
     * 入れ子になっていないプレーンな状態遷移マトリクス1つ分を表現するイメージ。
     */

    /** starting state (modifiable) */
    public int startState = 0;

    /** set of acceptable states (modifiable) */
    public final Set<Integer> acceptableStates = new HashSet<>();

    /* 状態S x 文字C の組み合わせをキーとして、遷移先状態S'の集合を取り出せるようにしたmap
     * -> NFAの遷移関数の内部マトリクスとしてそのまま利用できる。
     */
    private Map<StateAndInputCharacter, Set<Integer>> stateTransitionMatrix = new HashMap<>();

    /**
     * このNFAフラグメントの状態遷移マトリクスに、新たな遷移設定を追加する。(= 副作用あり)
     * 
     * @param stateFrom 初期状態
     * @param c 入力文字(εの場合は Optional.empty() を指定)
     * @param stateTo 遷移先状態
     */
    public void connect(final int stateFrom, final Optional<Character> c, final int stateTo) {
        final var key = StateAndInputCharacter.of(stateFrom, c);
        final Set<Integer> transitionSet = this.stateTransitionMatrix.getOrDefault(key, new HashSet<>());
        transitionSet.add(stateTo);
        this.stateTransitionMatrix.put(key, transitionSet);
    }

    /**
     * @return このNFAフラグメントの状態遷移マトリクスだけを deep-copy した、新しいNFAフラグメント(初期状態/受理可能状態の集合は未設定)
     */
    public NfaFragment createAndCopyStateTransitionMatrix() {
        final NfaFragment r = new NfaFragment();
        for (final Entry<StateAndInputCharacter, Set<Integer>> e : this.stateTransitionMatrix.entrySet()) {
            final StateAndInputCharacter originalKey = e.getKey();
            // 事故防止のため、値コピーしたkey値を使う。
            final StateAndInputCharacter deepCopiedKey = StateAndInputCharacter.of(
                    originalKey.currentState,
                    originalKey.inputCharacter);
            r.stateTransitionMatrix.put(deepCopiedKey, new HashSet<>(e.getValue()));
        }
        return r;
    }

    /**
     * このNFAフラグメントと、引数で指定されたNFAフラグメントの状態遷移マトリクスを合成(OR演算)する。
     * 状態S x 入力文字C における状態Sについては、全体として重複が無いことを前提とする。
     * 
     * @param fragment 合成対象のNFAフラグメント
     * @return 状態遷移マトリクスをdeep-copyでOR演算した、新しいNFAフラグメント(初期状態/受理可能状態の集合は未設定)
     */
    public NfaFragment createAndMergeStateTransitionMatrixByOrOps(final NfaFragment fragment) {
        final NfaFragment r = this.createAndCopyStateTransitionMatrix();
        for (final Entry<StateAndInputCharacter, Set<Integer>> e : fragment.stateTransitionMatrix.entrySet()) {
            final StateAndInputCharacter originalKey = e.getKey();
            // 事故防止のため、値コピーしたkey値を使う。
            final StateAndInputCharacter deepCopiedKey = StateAndInputCharacter.of(
                    originalKey.currentState,
                    originalKey.inputCharacter);
            r.stateTransitionMatrix.put(deepCopiedKey, new HashSet<>(e.getValue()));
        }
        return r;
    }

    /**
     * 状態遷移マトリクスを元に生成した状態遷移関数を持つNFAを作成する。
     * 
     * @param enableTraceLog トレースログ(System.out.println()) ON/OFF
     * @return 状態遷移マトリクスを元に生成した状態遷移関数を持つNFA
     */
    public Nfa build(final boolean enableTraceLog) {
        final Map<StateAndInputCharacter, Set<Integer>> mapref = this.stateTransitionMatrix;
        final NfaStateTransitFunction transition = (final int currentState, final Optional<Character> input) -> {
            final StateAndInputCharacter key = StateAndInputCharacter.of(currentState, input);
            final Set<Integer> r = mapref.getOrDefault(key, Collections.emptySet());
            if (enableTraceLog) {
                System.out.println("NFA TRANSITION: (" + currentState + ", " + optchar(input) + ") => " + r);
            }
            return Collections.unmodifiableSet(r);
        };
        return Nfa.of(transition, this.startState, this.acceptableStates, enableTraceLog);
    }

    public Nfa build() {
        return build(false);
    }

    public static String optchar(final Optional<Character> c) {
        return c.isPresent() ? "'" + c.get() + "'" : "(ε)";
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("----[NFA Fragment]----\n");
        sb.append("initial state = " + this.startState + "\n");
        final TreeMap<StateAndInputCharacter, Set<Integer>> m = new TreeMap<>(new Comparator<StateAndInputCharacter>() {
            @Override
            public int compare(StateAndInputCharacter o1, StateAndInputCharacter o2) {
                if (o1.currentState != o2.currentState) {
                    return o1.currentState - o2.currentState;
                }
                if (o1.inputCharacter.isPresent() && o2.inputCharacter.isPresent()) {
                    return o1.inputCharacter.get().charValue() - o2.inputCharacter.get().charValue();
                }
                if (o1.inputCharacter.isPresent()) {
                    return 1;
                } else {
                    if (o2.inputCharacter.isPresent()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            }
        });
        m.putAll(this.stateTransitionMatrix);
        for (Entry<StateAndInputCharacter, Set<Integer>> e : m.entrySet()) {
            final StateAndInputCharacter key = e.getKey();
            final int currentState = key.currentState;
            final Optional<Character> input = key.inputCharacter;
            final Set<Integer> r = e.getValue();
            sb.append("[ " + currentState + ", " + optchar(input) + " -> " + r + " ]\n");
        }
        sb.append("set of acceptable state = " + this.acceptableStates + "\n");
        return sb.toString();
    }
}
