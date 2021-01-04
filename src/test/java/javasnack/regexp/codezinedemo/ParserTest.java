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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class ParserTest {
    /* 正規表現のパターンをテストするというよりは、parserの各ruleの詳細ロジックをテストする形にしている。
     * code coverage ツールを通したわけではないが、一応目視で各ruleの条件分岐は網羅したはずとなっている。
     * parser については元の連載記事の python コードをほぼそのまま java に変換しただけなので、
     * 元の連載記事が間違ってなければ、これで合ってるはず。
     * (心配なのは seq rule における空文字列くらい。εとして Optional.empty() にしたけど合ってるかな??)
     * 
     * 元の連載記事 : https://codezine.jp/article/detail/3158
     */

    @Test
    public void testEmptyString() {
        // subexpr -> seq(neither '(' or character)  -> CharacterNode
        final var lex0 = new Lexer("");
        final var parser0 = new Parser(lex0);
        final var debugTo = new StringBuilder();
        final var nfa0 = parser0.expression(debugTo);
        assertThat(debugTo.toString()).isEqualTo("----[NFA Fragment]----\n"
                + "initial state = 1\n"
                + "[ 1, (ε) -> [2] ]\n"
                + "set of acceptable state = [2]\n");
        assertThat(nfa0.start).isEqualTo(1);
        assertThat(nfa0.accept).isEqualTo(Set.of(2));
        assertThat(nfa0.transition.apply(1, Optional.empty())).isEqualTo(Set.of(2));
    }

    @Test
    public void testSingleCharacterString() {
        // subexpr -> seq(character) -> subseq -> star -> factor(character) -> CharacterNode
        final var lex0 = new Lexer("a");
        final var parser0 = new Parser(lex0);
        final var debugTo = new StringBuilder();
        final var nfa0 = parser0.expression(debugTo);
        assertThat(debugTo.toString()).isEqualTo("----[NFA Fragment]----\n"
                + "initial state = 1\n"
                + "[ 1, 'a' -> [2] ]\n"
                + "set of acceptable state = [2]\n");
        assertThat(nfa0.start).isEqualTo(1);
        assertThat(nfa0.accept).isEqualTo(Set.of(2));
        assertThat(nfa0.transition.apply(1, Optional.of('a'))).isEqualTo(Set.of(2));
    }

    @Test
    public void testSingleCharacterUnionString() {
        /* subexpr
         * -> seq(character) -> subseq -> star -> factor(character) -> CharacterNode[1]
         * -> '|'
         * -> subexpr -> seq(character) -> subseq -> star -> factor(character) -> CharacterNode[2]
         * -> Union([1], [2])
         */
        final var lex0 = new Lexer("a|b");
        final var parser0 = new Parser(lex0);
        final var debugTo = new StringBuilder();
        final var nfa0 = parser0.expression(debugTo);
        assertThat(debugTo.toString()).isEqualTo("----[NFA Fragment]----\n"
                + "initial state = 5\n"
                + "[ 1, 'a' -> [2] ]\n"
                + "[ 3, 'b' -> [4] ]\n"
                + "[ 5, (ε) -> [1, 3] ]\n"
                + "set of acceptable state = [2, 4]\n");
        assertThat(nfa0.start).isEqualTo(5);
        assertThat(nfa0.accept).isEqualTo(Set.of(2, 4));
        assertThat(nfa0.transition.apply(1, Optional.of('a'))).isEqualTo(Set.of(2));
        assertThat(nfa0.transition.apply(3, Optional.of('b'))).isEqualTo(Set.of(4));
        assertThat(nfa0.transition.apply(5, Optional.empty())).isEqualTo(Set.of(1, 3));
    }

    @Test
    public void testCharacterConcatString() {
        /* subexpr -> seq(character) -> subseq(character)
         *   -> star -> factor(character) -> CharacterNode[1]
         *   -> subseq -> star -> factor(character) -> CharacterNode[2]
         *   -> Concat([1], [2])
         */
        final var lex0 = new Lexer("ab");
        final var parser0 = new Parser(lex0);
        final var debugTo = new StringBuilder();
        final var nfa0 = parser0.expression(debugTo);
        assertThat(debugTo.toString()).isEqualTo("----[NFA Fragment]----\n"
                + "initial state = 1\n"
                + "[ 1, 'a' -> [2] ]\n"
                + "[ 2, (ε) -> [3] ]\n"
                + "[ 3, 'b' -> [4] ]\n"
                + "set of acceptable state = [4]\n");
        assertThat(nfa0.start).isEqualTo(1);
        assertThat(nfa0.accept).isEqualTo(Set.of(4));
        assertThat(nfa0.transition.apply(1, Optional.of('a'))).isEqualTo(Set.of(2));
        assertThat(nfa0.transition.apply(2, Optional.empty())).isEqualTo(Set.of(3));
        assertThat(nfa0.transition.apply(3, Optional.of('b'))).isEqualTo(Set.of(4));
    }

    @Test
    public void testParenedSingleCharacterString() {
        // subexpr -> seq('(') -> subseq(character) -> star -> factor -> '(' + subexpr + ')' -> CharacterNode
        final var lex0 = new Lexer("(a)");
        final var parser0 = new Parser(lex0);
        final var debugTo = new StringBuilder();
        final var nfa0 = parser0.expression(debugTo);
        assertThat(debugTo.toString()).isEqualTo("----[NFA Fragment]----\n"
                + "initial state = 1\n"
                + "[ 1, 'a' -> [2] ]\n"
                + "set of acceptable state = [2]\n");
        assertThat(nfa0.start).isEqualTo(1);
        assertThat(nfa0.accept).isEqualTo(Set.of(2));
        assertThat(nfa0.transition.apply(1, Optional.of('a'))).isEqualTo(Set.of(2));
    }

    @Test
    public void testSingleCharacterStaredString() {
        /* subexpr -> seq(character) -> subseq(character) -> star -> factor(character) -> CharacterNode[1]
         *                                                        -> '*'
         *                                                        -> StarNode([1])
         */
        final var lex0 = new Lexer("a*");
        final var parser0 = new Parser(lex0);
        final var debugTo = new StringBuilder();
        final var nfa0 = parser0.expression(debugTo);
        assertThat(debugTo.toString()).isEqualTo("----[NFA Fragment]----\n"
                + "initial state = 3\n"
                + "[ 1, 'a' -> [2] ]\n"
                + "[ 2, (ε) -> [1] ]\n"
                + "[ 3, (ε) -> [1] ]\n"
                + "set of acceptable state = [2, 3]\n");
        assertThat(nfa0.start).isEqualTo(3);
        assertThat(nfa0.accept).isEqualTo(Set.of(2, 3));
        assertThat(nfa0.transition.apply(1, Optional.of('a'))).isEqualTo(Set.of(2));
        assertThat(nfa0.transition.apply(2, Optional.empty())).isEqualTo(Set.of(1));
        assertThat(nfa0.transition.apply(3, Optional.empty())).isEqualTo(Set.of(1));
    }

    @Test
    public void testStaredAndConcatSingleCharacterString() {
        /* subexpr -> seq(character) -> subseq(character)
         * -> star -> factor(character) -> CharacterNode[1]
         *         -> '*'
         *         -> StarNode([1]) : [2]
         * -> subseq(character) -> ... [3]
         * -> ConcatNode([2], [3])
         */
        final var lex0 = new Lexer("a*b");
        final var parser0 = new Parser(lex0);
        final var debugTo = new StringBuilder();
        final var nfa0 = parser0.expression(debugTo);
        assertThat(debugTo.toString()).isEqualTo("----[NFA Fragment]----\n"
                + "initial state = 3\n"
                + "[ 1, 'a' -> [2] ]\n"
                + "[ 2, (ε) -> [1, 4] ]\n"
                + "[ 3, (ε) -> [1, 4] ]\n"
                + "[ 4, 'b' -> [5] ]\n"
                + "set of acceptable state = [5]\n");
        assertThat(nfa0.start).isEqualTo(3);
        assertThat(nfa0.accept).isEqualTo(Set.of(5));
        assertThat(nfa0.transition.apply(1, Optional.of('a'))).isEqualTo(Set.of(2));
        assertThat(nfa0.transition.apply(2, Optional.empty())).isEqualTo(Set.of(1, 4));
        assertThat(nfa0.transition.apply(3, Optional.empty())).isEqualTo(Set.of(1, 4));
        assertThat(nfa0.transition.apply(4, Optional.of('b'))).isEqualTo(Set.of(5));
    }

    @Test
    public void testStaredAndConcatParenedUnionCharacterString() {
        /* subexpr -> seq(character) -> subseq(character)
         * -> star -> factor(character) -> CharacterNode[1]
         *         -> '*'
         *         -> StarNode([1]) : [2]
         * -> subseq('(') -> ... [3]
         * -> ConcatNode([2], [3])
         */
        final var lex0 = new Lexer("a*(b|c)");
        final var parser0 = new Parser(lex0);
        final var debugTo = new StringBuilder();
        final var nfa0 = parser0.expression(debugTo);
        assertThat(debugTo.toString()).isEqualTo("----[NFA Fragment]----\n"
                + "initial state = 3\n"
                + "[ 1, 'a' -> [2] ]\n"
                + "[ 2, (ε) -> [1, 8] ]\n"
                + "[ 3, (ε) -> [1, 8] ]\n"
                + "[ 4, 'b' -> [5] ]\n"
                + "[ 6, 'c' -> [7] ]\n"
                + "[ 8, (ε) -> [4, 6] ]\n"
                + "set of acceptable state = [5, 7]\n");
        assertThat(nfa0.start).isEqualTo(3);
        assertThat(nfa0.accept).isEqualTo(Set.of(5, 7));
        assertThat(nfa0.transition.apply(1, Optional.of('a'))).isEqualTo(Set.of(2));
        assertThat(nfa0.transition.apply(2, Optional.empty())).isEqualTo(Set.of(1, 8));
        assertThat(nfa0.transition.apply(3, Optional.empty())).isEqualTo(Set.of(1, 8));
        assertThat(nfa0.transition.apply(8, Optional.empty())).isEqualTo(Set.of(4, 6));
        assertThat(nfa0.transition.apply(4, Optional.of('b'))).isEqualTo(Set.of(5));
        assertThat(nfa0.transition.apply(6, Optional.of('c'))).isEqualTo(Set.of(7));
    }
}
