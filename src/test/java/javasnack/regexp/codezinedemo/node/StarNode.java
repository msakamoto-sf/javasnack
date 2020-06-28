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

public class StarNode implements INodeAssembler {
    private final INodeAssembler op;

    public StarNode(final INodeAssembler op) {
        this.op = op;
    }

    @Override
    public NfaFragment assemble(final Context context) {
        final NfaFragment original = this.op.assemble(context);
        final NfaFragment r = original.createSkelton();
        for (int state : original.accept) {
            r.connect(state, Optional.empty(), original.start);
        }
        final int newState = context.newState();
        r.connect(newState, Optional.empty(), original.start);
        r.start = newState;
        r.accept = new HashSet<>(original.accept);
        r.accept.add(newState);
        return r;
    }
}
