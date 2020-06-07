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

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import javasnack.regexp.codezinedemo.Token.TokenType;

public class LexerTest {
    @Test
    public void testLexer() {
        var lex0 = new Lexer("abc");
        assertEquals(Token.of('a', TokenType.CHARACTER), lex0.nextToken());
        assertEquals(Token.of('b', TokenType.CHARACTER), lex0.nextToken());
        assertEquals(Token.of('c', TokenType.CHARACTER), lex0.nextToken());
        assertEquals(Token.of('\0', TokenType.EOF), lex0.nextToken());

        lex0 = new Lexer("x(a|b)*\\*c");
        assertEquals(Token.of('x', TokenType.CHARACTER), lex0.nextToken());
        assertEquals(Token.of('(', TokenType.LPAREN), lex0.nextToken());
        assertEquals(Token.of('a', TokenType.CHARACTER), lex0.nextToken());
        assertEquals(Token.of('|', TokenType.OPE_UNION), lex0.nextToken());
        assertEquals(Token.of('b', TokenType.CHARACTER), lex0.nextToken());
        assertEquals(Token.of(')', TokenType.RPAREN), lex0.nextToken());
        assertEquals(Token.of('*', TokenType.OPE_STAR), lex0.nextToken());
        assertEquals(Token.of('*', TokenType.CHARACTER), lex0.nextToken());
        assertEquals(Token.of('c', TokenType.CHARACTER), lex0.nextToken());
        assertEquals(Token.of('\0', TokenType.EOF), lex0.nextToken());
    }
}
