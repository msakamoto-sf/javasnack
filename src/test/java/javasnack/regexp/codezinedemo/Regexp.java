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
        NFA, NFA_BACKTRACK, NFA2DFA
    }

    public enum RegexpOption {
        DEBUG_LOG, ENABLE_NFA2DFA_TRANSITION_CACHE, NFA_BACKTRACK
    }

    private final RegexpType type;
    private final Nfa nfa;
    private final Nfa2Dfa nfa2dfa;
    private final boolean enableNfaBackTrackRuntimeTraceLog;

    private Regexp(final RegexpType type, final Nfa nfa, final Nfa2Dfa nfa2dfa,
            final boolean enableNfaBackTrackRuntimeTraceLog) {
        this.type = type;
        this.nfa = nfa;
        this.nfa2dfa = nfa2dfa;
        this.enableNfaBackTrackRuntimeTraceLog = enableNfaBackTrackRuntimeTraceLog;
    }

    public static Regexp compileNfa(final String regexp, RegexpOption... options) {
        final List<RegexpOption> optionset = Arrays.asList(options);
        final boolean enableDebugLog = optionset.contains(RegexpOption.DEBUG_LOG);
        final boolean useNfaBackTrack = optionset.contains(RegexpOption.NFA_BACKTRACK);

        final Lexer lex0 = new Lexer(regexp);
        final Parser parser0 = new Parser(lex0);
        final StringBuilder dumpTo = new StringBuilder();
        final Nfa nfa0 = parser0.expression(dumpTo, enableDebugLog);
        final Nfa nfaForDump = parser0.expression();
        if (enableDebugLog) {
            System.out.println(dumpTo.toString());
            System.out.println("dump NFA....");
            final NfaDumper nfaDumper = new NfaDumper(nfaForDump);
            System.out.println(nfaDumper.dump());
            System.out.println("NFA: initialState=" + nfa0.start + ", setOfAcceptableState=" + nfa0.accept);
        }
        return new Regexp((useNfaBackTrack ? RegexpType.NFA_BACKTRACK : RegexpType.NFA), nfa0, null, enableDebugLog);
    }

    public static Regexp compileNfa2Dfa(final String regexp, RegexpOption... options) {
        final List<RegexpOption> optionset = Arrays.asList(options);
        final boolean enableDebugLog = optionset.contains(RegexpOption.DEBUG_LOG);
        final boolean enableCache = optionset.contains(RegexpOption.ENABLE_NFA2DFA_TRANSITION_CACHE);

        final Lexer lex0 = new Lexer(regexp);
        final Parser parser0 = new Parser(lex0);
        final StringBuilder dumpTo = new StringBuilder();
        final Nfa nfa0 = parser0.expression(dumpTo, false); // NFAの遷移関数のトレースログは不要
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
        return new Regexp(RegexpType.NFA2DFA, null, nfa2dfa, false);
    }

    public boolean match(final String str) {
        switch (this.type) {
        case NFA2DFA:
            final Nfa2DfaRuntime nfa2dfaRuntime = new Nfa2DfaRuntime(nfa2dfa);
            return nfa2dfaRuntime.accept(str);
        case NFA:
            final NfaRuntime nfaRuntime = new NfaRuntime(nfa);
            return nfaRuntime.accept(str);
        case NFA_BACKTRACK:
            final NfaBackTrackRuntime nfaBackTrackRuntime = new NfaBackTrackRuntime(
                    nfa,
                    enableNfaBackTrackRuntimeTraceLog);
            return nfaBackTrackRuntime.accept(str);
        default:
            throw new UnsupportedOperationException();
        }
    }
}
