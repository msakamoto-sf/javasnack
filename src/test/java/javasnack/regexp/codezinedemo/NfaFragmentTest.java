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

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class NfaFragmentTest {
    @Test
    public void testEmptyConstructionThenConnect() {
        final NfaFragment r = new NfaFragment();
        assertThat(r.startState).isEqualTo(0);
        assertThat(r.acceptableStates).isEmpty();

        final Nfa nfa = r.build();
        assertThat(nfa.start).isEqualTo(0);
        assertThat(nfa.accept).isEmpty();
        assertThat(nfa.transition.apply(0, Optional.empty())).isEmpty();

        r.startState = 1;
        r.connect(1, Optional.of('a'), 2);
        r.connect(2, Optional.of('b'), 3);
        // Integer.valueOf() では -128 - 127 までキャッシュするため、その範囲外の動作についてもチェック
        r.connect(2, Optional.of('b'), 128);
        r.connect(128, Optional.empty(), 129);
        r.connect(128, Optional.empty(), 130);
        r.acceptableStates.add(3);
        r.acceptableStates.add(129);

        final Nfa nfa2 = r.build();
        assertThat(nfa2.start).isEqualTo(1);
        assertThat(nfa2.accept).isEqualTo(Set.of(3, 129));
        assertThat(nfa2.transition.apply(1, Optional.of('a'))).isEqualTo(Set.of(2));
        assertThat(nfa2.transition.apply(2, Optional.of('b'))).isEqualTo(Set.of(3, 128));
        assertThat(nfa2.transition.apply(128, Optional.empty())).isEqualTo(Set.of(129, 130));
        assertThat(nfa2.transition.apply(999, Optional.empty())).isEmpty();
    }

    @Test
    public void testCreateAndCopyStateTransitionMatrix() {
        NfaFragment r0 = new NfaFragment();
        NfaFragment r1 = r0.createAndCopyStateTransitionMatrix();

        assertThat(r1.startState).isEqualTo(0);
        assertThat(r1.acceptableStates).isEmpty();

        Nfa nfa = r1.build();
        assertThat(nfa.start).isEqualTo(0);
        assertThat(nfa.accept).isEmpty();
        assertThat(nfa.transition.apply(0, Optional.empty())).isEmpty();

        // copyした後に、copy元のNFAフラグメントのパラメータを更新する。
        r0.startState = 1;
        r0.acceptableStates.add(100);
        r0.acceptableStates.add(200);
        r0.connect(1, Optional.of('a'), 100);
        r0.connect(1, Optional.of('a'), 200);
        r0.connect(2, Optional.empty(), 300);
        r0.connect(2, Optional.empty(), 400);

        // copy先のr1および、r1から生成済みのNFAに影響しないこと。
        assertThat(r1.startState).isEqualTo(0);
        assertThat(r1.acceptableStates).isEmpty();
        assertThat(nfa.start).isEqualTo(0);
        assertThat(nfa.accept).isEmpty();
        assertThat(nfa.transition.apply(1, Optional.of('a'))).isEmpty();
        assertThat(nfa.transition.apply(2, Optional.empty())).isEmpty();

        // copy先のr1からNFAを生成し直しても影響しないこと。
        nfa = r1.build();
        assertThat(nfa.start).isEqualTo(0);
        assertThat(nfa.accept).isEmpty();
        assertThat(nfa.transition.apply(1, Optional.of('a'))).isEmpty();
        assertThat(nfa.transition.apply(2, Optional.empty())).isEmpty();

        // copy元から改めてコピーし直す。
        r1 = r0.createAndCopyStateTransitionMatrix();
        // -> 初期状態, 受理可能状態はコピーされないこと。
        assertThat(r1.startState).isEqualTo(0);
        assertThat(r1.acceptableStates).isEmpty();
        // -> copy先からNFAを生成すると、状態遷移マトリクスに従った遷移関数となること。
        r1.startState = 10;
        r1.acceptableStates.add(20);
        nfa = r1.build();
        assertThat(nfa.start).isEqualTo(10);
        assertThat(nfa.accept).isEqualTo(Set.of(20));
        assertThat(nfa.transition.apply(1, Optional.of('a'))).isEqualTo(Set.of(100, 200));
        assertThat(nfa.transition.apply(2, Optional.empty())).isEqualTo(Set.of(300, 400));
    }

    @Test
    public void testCreateAndMergeStateTransitionMatrixByOrOps() {
        NfaFragment r0 = new NfaFragment();
        NfaFragment r1 = new NfaFragment();
        NfaFragment r2 = r0.createAndMergeStateTransitionMatrixByOrOps(r1);

        assertThat(r2.startState).isEqualTo(0);
        assertThat(r2.acceptableStates).isEmpty();

        Nfa nfa = r2.build();
        assertThat(nfa.start).isEqualTo(0);
        assertThat(nfa.accept).isEmpty();
        assertThat(nfa.transition.apply(0, Optional.empty())).isEmpty();

        // mergeした後に、merge元のNFAフラグメントのパラメータを更新する。
        r0.startState = 1;
        r0.acceptableStates.add(100);
        r0.connect(1, Optional.of('a'), 100);
        r0.connect(1, Optional.of('a'), 200);
        r1.startState = 2;
        r1.acceptableStates.add(200);
        r1.connect(2, Optional.empty(), 300);
        r1.connect(2, Optional.empty(), 400);

        // merge先のr2および、r2から生成済みのNFAに影響しないこと。
        assertThat(r2.startState).isEqualTo(0);
        assertThat(r2.acceptableStates).isEmpty();
        assertThat(nfa.start).isEqualTo(0);
        assertThat(nfa.accept).isEmpty();
        assertThat(nfa.transition.apply(1, Optional.of('a'))).isEmpty();
        assertThat(nfa.transition.apply(2, Optional.empty())).isEmpty();

        // merge先のr2からNFAを生成し直しても影響しないこと。
        nfa = r2.build();
        assertThat(nfa.start).isEqualTo(0);
        assertThat(nfa.accept).isEmpty();
        assertThat(nfa.transition.apply(1, Optional.of('a'))).isEmpty();
        assertThat(nfa.transition.apply(2, Optional.empty())).isEmpty();

        // 改めて merge し直す。
        r2 = r0.createAndMergeStateTransitionMatrixByOrOps(r1);
        // -> 初期状態, 受理可能状態はコピーされないこと。
        assertThat(r2.startState).isEqualTo(0);
        assertThat(r2.acceptableStates).isEmpty();
        // -> copy先からNFAを生成すると、状態遷移マトリクスに従った遷移関数となること。
        r2.startState = 10;
        r2.acceptableStates.add(20);
        nfa = r2.build();
        assertThat(nfa.start).isEqualTo(10);
        assertThat(nfa.accept).isEqualTo(Set.of(20));
        assertThat(nfa.transition.apply(1, Optional.of('a'))).isEqualTo(Set.of(100, 200));
        assertThat(nfa.transition.apply(2, Optional.empty())).isEqualTo(Set.of(300, 400));
    }
}
