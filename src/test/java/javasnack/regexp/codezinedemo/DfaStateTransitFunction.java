package javasnack.regexp.codezinedemo;

@FunctionalInterface
public interface DfaStateTransitFunction {
    /**
     * DFA state transition function
     * 
     * @param currentState current state
     * @param inputCharacter character(not empty)
     * @return new state
     */
    int apply(int currentState, char inputCharacter);
}
