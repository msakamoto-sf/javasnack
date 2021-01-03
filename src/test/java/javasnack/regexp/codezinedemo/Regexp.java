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

public class Regexp {
    public enum RegexpType {
        NFA, NFA2DFA
    }

    private final RegexpType type;
    private final Nfa nfa;
    private final Nfa2Dfa nfa2dfa;

    private Regexp(final RegexpType type, final Nfa nfa, final Nfa2Dfa nfa2dfa) {
        this.type = type;
        this.nfa = nfa;
        this.nfa2dfa = nfa2dfa;
    }

    public static Regexp compileNfa(final String regexp) {
        final Lexer lex0 = new Lexer(regexp);
        final Parser parser0 = new Parser(lex0);
        final Nfa nfa0 = parser0.expression();
        return new Regexp(RegexpType.NFA, nfa0, null);
    }

    public static Regexp compileNfa2Dfa(final String regexp) {
        final Lexer lex0 = new Lexer(regexp);
        final Parser parser0 = new Parser(lex0);
        final Nfa nfa0 = parser0.expression();
        return new Regexp(RegexpType.NFA2DFA, null, Nfa2Dfa.from(nfa0));
    }

    public boolean match(final String str) {
        switch (this.type) {
        case NFA2DFA:
            final Nfa2DfaRuntime runtime0 = new Nfa2DfaRuntime(nfa2dfa);
            return runtime0.accept(str);
        case NFA:
        default:
            throw new UnsupportedOperationException();
        }
    }
}
