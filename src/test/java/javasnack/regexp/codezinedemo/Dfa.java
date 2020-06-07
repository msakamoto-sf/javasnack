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
import java.util.function.BiFunction;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Deterministic Finite Automaton : DFA representation
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor(staticName = "of")
public class Dfa {
    /** transition function : current state, character(not empty) -> new state */
    public final BiFunction<Integer, Character, Integer> transition;
    /** starting state */
    public final int start;
    /** set of acceptable states */
    public final Set<Integer> accept;
}
