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

package javasnack.regexp.codezinedemo.node;

import java.util.HashSet;
import java.util.Optional;

import javasnack.regexp.codezinedemo.Context;
import javasnack.regexp.codezinedemo.NfaFragment;

public class ConcatNode implements INodeAssembler {
    private final INodeAssembler op1;
    private final INodeAssembler op2;

    public ConcatNode(final INodeAssembler op1, final INodeAssembler op2) {
        this.op1 = op1;
        this.op2 = op2;
    }

    @Override
    public NfaFragment assemble(final Context context) {
        final NfaFragment f1 = this.op1.assemble(context);
        final NfaFragment f2 = this.op2.assemble(context);
        final NfaFragment r = f1.createOr(f2);
        for (int state : f1.accept) {
            r.connect(state, Optional.empty(), f2.start);
        }
        r.start = f1.start;
        r.accept = new HashSet<>(f2.accept);
        return r;
    }
}
