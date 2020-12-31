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

public class ConcatNodeTest {
    @Test
    public void testAssemble() {
        // 1系 : S[1] x 'a' => S[2] 
        final INodeAssembler r1 = new CharacterNode(Optional.of('a'));
        // 2系 : S[3] x 'b' => S[4]
        final INodeAssembler r2 = new CharacterNode(Optional.of('b'));
        final INodeAssembler r3 = new ConcatNode(r1, r2);

        final NfaFragment f = r3.assemble(new Context());
        final Nfa nfa = f.build();
        assertThat(nfa.start).isEqualTo(1); // 1系の初期状態
        assertThat(nfa.accept).isEqualTo(Set.of(4)); // 2系の受理可能状態集合
        // 1系 : S[1] x 'a' => S[2] 
        assertThat(nfa.transition.apply(1, Optional.of('a'))).isEqualTo(Set.of(2));
        // 1系 S[2] x 空文字(ε) => 2系 S[3]
        assertThat(nfa.transition.apply(2, Optional.empty())).isEqualTo(Set.of(3));
        // 2系 : S[3] x 'b' => S[4]
        assertThat(nfa.transition.apply(3, Optional.of('b'))).isEqualTo(Set.of(4));
    }
}
