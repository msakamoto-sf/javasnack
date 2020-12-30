package javasnack.regexp.codezinedemo;

import java.util.Optional;
import java.util.Set;

@FunctionalInterface
public interface NfaStateTransitFunction {
    /**
     * NFA state transition function
     * 
     * @param currentState current state
     * @param inputCharacter single character or empty (Optional.empty())
     * @return set of new states
     */
    Set<Integer> apply(int currentState, Optional<Character> inputCharacter);
}
