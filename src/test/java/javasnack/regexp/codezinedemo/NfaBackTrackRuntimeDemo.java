package javasnack.regexp.codezinedemo;

public class NfaBackTrackRuntimeDemo {
    public static void main(final String... args) {
        final String regexp = "(a|a)*";
        final int length = 3;

        final Lexer lex0 = new Lexer(regexp);
        final Parser parser0 = new Parser(lex0);
        final StringBuilder dumpTo = new StringBuilder();
        final Nfa nfa0 = parser0.expression(dumpTo, true);

        final Nfa nfaForDump = parser0.expression();
        System.out.println(dumpTo.toString());
        System.out.println("dump NFA....");
        final NfaDumper nfaDumper = new NfaDumper(nfaForDump);
        System.out.println(nfaDumper.dump());
        System.out.println("NFA: initialState=" + nfa0.start + ", setOfAcceptableState=" + nfa0.accept);

        final NfaBackTrackRuntime nfaBackTrackRuntime = new NfaBackTrackRuntime(nfa0, true, false);
        final String matchTo = "a".repeat(length) + "b";
        final long startedAt = System.nanoTime();
        final boolean matched = nfaBackTrackRuntime.accept(matchTo);
        final long elapsed = System.nanoTime() - startedAt;
        System.out.println("[match: " + matched + "]");
        System.out.println("[backtrack: " + nfaBackTrackRuntime.getCountOfBackTracked() + "]");
        System.out.println("[elapsed: " + elapsed + "]");
    }
}
