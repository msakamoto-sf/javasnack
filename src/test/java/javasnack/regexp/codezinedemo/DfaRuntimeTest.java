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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiFunction;

import org.junit.jupiter.api.Test;

public class DfaRuntimeTest {

    @Test
    public void testDfaRuntimeAcceptance() {
        final BiFunction<Integer, Character, Integer> transition0 = (status, character) -> {
            if (status == 1 && character == 'a') {
                return 2;
            }
            if (status == 2 && character == 'b') {
                return 3;
            }
            return 0;
        };
        final var dfa = Dfa.of(transition0, 1, Collections.unmodifiableSet(Set.of(3)));
        var runtime0 = new DfaRuntime(dfa);
        assertTrue(runtime0.accept("ab"));
        runtime0 = new DfaRuntime(dfa);
        assertFalse(runtime0.accept("ba"));
        runtime0 = new DfaRuntime(dfa);
        assertFalse(runtime0.accept("xxxxx"));
    }
}
