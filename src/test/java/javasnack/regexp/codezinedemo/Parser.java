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

import java.util.Optional;

import javasnack.regexp.codezinedemo.Token.TokenType;
import javasnack.regexp.codezinedemo.node.CharacterNode;
import javasnack.regexp.codezinedemo.node.ConcatNode;
import javasnack.regexp.codezinedemo.node.INodeAssembler;
import javasnack.regexp.codezinedemo.node.StarNode;
import javasnack.regexp.codezinedemo.node.UnionNode;

public class Parser {

    private final Lexer lexer;
    private Token look = null;

    public Parser(final Lexer lexer) {
        this.lexer = lexer;
        this.move();
    }

    public void match(final TokenType tag) {
        if (this.look.type != tag) {
            throw new RuntimeException("syntax error");
        }
        this.move();
    }

    private void move() {
        this.look = this.lexer.nextToken();
    }

    /* rules:
     * (A) expression -> subexpr EOF
     * (B) subexpr -> seq '|' subexpr | seq
     * (C) subseq -> star subseq | star
     * (D) seq -> subseq | ''
     * (E) star -> factor '*' | factor
     * (F) factor -> '(' subexpr ')' | CHARACTER
     */

    public INodeAssembler factor() {
        if (this.look.type == TokenType.LPAREN) {
            this.match(TokenType.LPAREN);
            INodeAssembler node = this.subexpr();
            this.match(TokenType.RPAREN);
            return node;
        } else {
            INodeAssembler node = new CharacterNode(Optional.of(this.look.value));
            this.match(TokenType.CHARACTER);
            return node;
        }
    }

    public INodeAssembler star() {
        INodeAssembler node = this.factor();
        if (this.look.type == TokenType.OPE_STAR) {
            this.match(TokenType.OPE_STAR);
            node = new StarNode(node);
        }
        return node;
    }

    public INodeAssembler seq() {
        if (this.look.type == TokenType.LPAREN || this.look.type == TokenType.CHARACTER) {
            return this.subseq();
        } else {
            return new CharacterNode(Optional.empty());
        }
    }

    public INodeAssembler subseq() {
        final INodeAssembler node1 = this.star();
        if (this.look.type == TokenType.LPAREN || this.look.type == TokenType.CHARACTER) {
            final INodeAssembler node2 = this.subseq();
            return new ConcatNode(node1, node2);
        } else {
            return node1;
        }
    }

    public INodeAssembler subexpr() {
        INodeAssembler node = this.factor();
        if (this.look.type == TokenType.OPE_UNION) {
            this.match(TokenType.OPE_UNION);
            INodeAssembler node2 = this.subexpr();
            node = new UnionNode(node, node2);
        }
        return node;
    }

    public Nfa expression() {
        final INodeAssembler node = this.subexpr();
        this.match(TokenType.EOF);
        final Context context = new Context();
        final NfaFragment fragment = node.assemble(context);
        return fragment.build();
    }
}
