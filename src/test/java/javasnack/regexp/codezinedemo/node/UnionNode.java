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

import java.util.Optional;

import javasnack.regexp.codezinedemo.Context;
import javasnack.regexp.codezinedemo.NfaFragment;

public class UnionNode implements INodeAssembler {
    /* 2つのnode(= NFAフラグメント)について、「そのどちらかを採用」という遷移構造に合成する。
     * -> (A|B) という正規表現を実現する。
     * 
     * 考え方:
     * NFAフラグメント1(初期状態S1a, 状態遷移マトリクスM1, 受理可能状態集合AS1)
     * M1:
     * (S1a, Ca) -> (S1b, S1c, ...)
     * (S1b, Cb) -> (S1d, S1e, ...)
     * -> AS1: (S1c, S1e)
     * 
     * NFAフラグメント2(初期状態S2a, 状態遷移マトリクスM2, 受理可能状態集合AS2)
     * M2:
     * (S2a, Cc) -> (S2b, S2c, ...)
     * (S2b, Cd) -> (S2d, S2e, ...)
     * -> AS1: (S2c, S2e)
     * 
     * 「どちらか」を実現する方法として、今回は「新しい初期状態から、空文字(ε)でM1とM2に遷移」という形を採用する。
     * NFAでは遷移先を複数設定でき、総当たりで辿る前提となっている。
     * 新しい初期状態から自動でM1/M2両方に遷移する設定にすれば、
     * NFAにおいては総当りで両方辿ることとなり、それにより (A|B) のマッチングが実現する。
     * 
     * -> M3(M1 + M2):
     * (S3, ε) -> (S1a, S2a) // 遷移先の状態集合として1系, 2系の初期状態を設定 
     * (S1a, Ca) -> (S1b, S1c, ...) // 1系からマージ
     * (S1b, Cb) -> (S1d, S1e, ...) // 1系からマージ
     * (S2a, Cc) -> (S2b, S2c, ...) // 2系からマージ
     * (S2b, Cd) -> (S2d, S2e, ...) // 2系からマージ
     * 受理可能状態集合AS3: (S1c, S1e, S2c, S2e)
     * 
     * これで、1系または2系いずれかへのマッチ = (A|B) のマッチングが実現する。
     */

    private final INodeAssembler op1;
    private final INodeAssembler op2;

    public UnionNode(final INodeAssembler op1, final INodeAssembler op2) {
        this.op1 = op1;
        this.op2 = op2;
    }

    @Override
    public NfaFragment assemble(final Context context) {
        final NfaFragment f1 = this.op1.assemble(context);
        final NfaFragment f2 = this.op2.assemble(context);
        // 状態遷移マトリクスを合成する。
        final NfaFragment r = f1.createAndMergeStateTransitionMatrixByOrOps(f2);
        // 新しい初期状態を設定し、空文字(ε) の遷移先として1系, 2系の初期状態を設定する。
        final int s = context.newState();
        r.connect(s, Optional.empty(), f1.startState);
        r.connect(s, Optional.empty(), f2.startState);
        r.startState = s;
        // 受理可能状態の集合としては、1系, 2系の和集合となる。
        r.acceptableStates.addAll(f1.acceptableStates);
        r.acceptableStates.addAll(f2.acceptableStates);
        return r;
    }
}
