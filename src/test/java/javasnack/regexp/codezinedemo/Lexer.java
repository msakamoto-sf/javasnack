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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import javasnack.regexp.codezinedemo.Token.TokenType;

public class Lexer {
    private final Deque<Character> stack;

    public Lexer(final String input) {
        final List<Character> chars = input.codePoints().mapToObj(c -> (char) c).collect(Collectors.toList());
        this.stack = new ArrayDeque<>(chars);
    }

    public Token nextToken() {
        if (this.stack.isEmpty()) {
            return Token.of('\0', TokenType.EOF);
        }
        final Character c = this.stack.pop();
        switch (c) {
        case '\\':
            return Token.of(this.stack.pop(), TokenType.CHARACTER);
        case '|':
            return Token.of(c, TokenType.OPE_UNION);
        case '(':
            return Token.of(c, TokenType.LPAREN);
        case ')':
            return Token.of(c, TokenType.RPAREN);
        case '*':
            return Token.of(c, TokenType.OPE_STAR);
        default:
            return Token.of(c, TokenType.CHARACTER);
        }
    }
}
