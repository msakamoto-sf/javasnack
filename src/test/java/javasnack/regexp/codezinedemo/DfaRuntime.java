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

public class DfaRuntime {
    private final Dfa dfa;
    private int currentState;

    public DfaRuntime(final Dfa dfa) {
        this.dfa = dfa;
        this.currentState = dfa.start;
    }

    private void transit(final char c) {
        this.currentState = dfa.transition.apply(Integer.valueOf(currentState), Character.valueOf(c));
    }

    private boolean isCurrentStatusAcceptable() {
        return this.dfa.accept.contains(Integer.valueOf(currentState));
    }

    public boolean accept(final String input) {
        input.codePoints().mapToObj(c -> (char) c).forEach(c -> this.transit(c));
        return this.isCurrentStatusAcceptable();
    }
}
