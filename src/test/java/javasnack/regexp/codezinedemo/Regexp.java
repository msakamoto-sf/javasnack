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

import java.util.Arrays;
import java.util.List;

public class Regexp {
    public enum RegexpType {
        NFA, NFA2DFA
    }

    public enum RegexpOption {
        DEBUG_LOG, ENABLE_NFA2DFA_TRANSITION_CACHE
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

    public static Regexp compileNfa2Dfa(final String regexp, RegexpOption... options) {
        final List<RegexpOption> optionset = Arrays.asList(options);
        final boolean enableDebugLog = optionset.contains(RegexpOption.DEBUG_LOG);
        final boolean enableCache = optionset.contains(RegexpOption.ENABLE_NFA2DFA_TRANSITION_CACHE);
        final Lexer lex0 = new Lexer(regexp);
        final Parser parser0 = new Parser(lex0);
        final StringBuilder dumpTo = new StringBuilder();
        final Nfa nfa0 = parser0.expression(dumpTo);
        final Nfa2Dfa nfa2dfa = Nfa2Dfa.from(nfa0, enableDebugLog, enableCache);
        final Nfa2Dfa nfa2dfaForDump = Nfa2Dfa.from(nfa0, false, false);
        if (enableDebugLog) {
            System.out.println(dumpTo.toString());
            System.out.println("dump NFA....");
            final NfaDumper nfaDumper = new NfaDumper(nfa0);
            System.out.println(nfaDumper.dump());
            System.out.println("dump NFA2DFA...");
            final Nfa2DfaDumper nfa2dfaDumper = new Nfa2DfaDumper(nfa2dfaForDump);
            System.out.println(nfa2dfaDumper.dump());
            System.out.println("NFA2DFA: setOfInitialState=" + nfa2dfa.start + ", setOfAcceptableState="
                    + nfa2dfa.nfaAcceptableStateSet);
        }
        return new Regexp(RegexpType.NFA2DFA, null, nfa2dfa);
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
