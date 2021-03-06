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

public class CharacterNodeTest {
    @Test
    public void testAssemble() {
        final INodeAssembler r = new CharacterNode(Optional.of('a'));
        final NfaFragment f = r.assemble(new Context());
        final Nfa nfa = f.build();
        assertThat(nfa.start).isEqualTo(1);
        assertThat(nfa.accept).isEqualTo(Set.of(2));
        assertThat(nfa.transition.apply(1, Optional.of('a'))).isEqualTo(Set.of(2));
    }
}
