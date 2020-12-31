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

package javasnack.regexp.codezinedemo.node;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import javasnack.regexp.codezinedemo.Context;
import javasnack.regexp.codezinedemo.Nfa;
import javasnack.regexp.codezinedemo.NfaFragment;

public class StarNodeTest {
    @Test
    public void testAssemble1() {
        // S[1] x 'a' => S[2] 
        final INodeAssembler r1 = new CharacterNode(Optional.of('a'));
        final INodeAssembler r2 = new StarNode(r1);

        final NfaFragment f = r2.assemble(new Context());
        final Nfa nfa = f.build();
        assertThat(nfa.start).isEqualTo(3); // 新しい初期状態
        assertThat(nfa.accept).isEqualTo(Set.of(2, 3));
        // S[1] x 'a' => S[2] 
        assertThat(nfa.transition.apply(1, Optional.of('a'))).isEqualTo(Set.of(2));
        // S[2] x 空文字(ε) => S[1] : 繰り返しを表現するための巻き戻し遷移
        assertThat(nfa.transition.apply(2, Optional.empty())).isEqualTo(Set.of(1));
        // 新しい初期状態 S[3] x 空文字(ε) => S[1]
        assertThat(nfa.transition.apply(3, Optional.empty())).isEqualTo(Set.of(1));
    }

    @Test
    public void testAssemble2() {
        // 1系 : S[1] x 'a' => S[2] 
        final INodeAssembler r1 = new CharacterNode(Optional.of('a'));
        // 2系 : S[3] x 'b' => S[4]
        final INodeAssembler r2 = new CharacterNode(Optional.of('b'));
        final INodeAssembler r3 = new UnionNode(r1, r2);
        final INodeAssembler r4 = new StarNode(r3);

        final NfaFragment f = r4.assemble(new Context());
        final Nfa nfa = f.build();
        assertThat(nfa.start).isEqualTo(6); // 新しい初期状態
        // 1系, 2系の受理可能状態集合の和集合 + 新しい初期状態でも受理可能
        assertThat(nfa.accept).isEqualTo(Set.of(2, 4, 6));
        // 1系 : S[1] x 'a' => S[2] 
        assertThat(nfa.transition.apply(1, Optional.of('a'))).isEqualTo(Set.of(2));
        // 2系 : S[3] x 'b' => S[4]
        assertThat(nfa.transition.apply(3, Optional.of('b'))).isEqualTo(Set.of(4));
        // union用初期状態 S[5] x 空文字(ε) => 1系 S[1], 2系 S[3]
        assertThat(nfa.transition.apply(5, Optional.empty())).isEqualTo(Set.of(1, 3));
        // S[2] x 空文字(ε) => union用初期状態S[5] : 繰り返しを表現するための巻き戻し遷移
        assertThat(nfa.transition.apply(2, Optional.empty())).isEqualTo(Set.of(5));
        // S[4] x 空文字(ε) => union用初期状態S[5] : 繰り返しを表現するための巻き戻し遷移
        assertThat(nfa.transition.apply(4, Optional.empty())).isEqualTo(Set.of(5));
        // 新しい初期状態 S[6] x 空文字(ε) => union用初期状態 S[5]
        assertThat(nfa.transition.apply(6, Optional.empty())).isEqualTo(Set.of(5));
    }
}
