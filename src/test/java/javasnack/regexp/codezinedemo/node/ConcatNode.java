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

public class ConcatNode implements INodeAssembler {
    /* 2つのnode(= NFAフラグメント)を1つのnodeに連結する。
     * 考え方:
     * NFAフラグメント1(初期状態S1a, 状態遷移マトリクスM1, 受理可能状態集合AS1)
     * NFAフラグメント2(初期状態S2a, 状態遷移マトリクスM2, 受理可能状態集合AS2)
     * -> NFAフラグメント1/2について、状態遷移マトリクスM1/M2とも、それにマッチするときはAS1/2に遷移する。
     * -> AS1の状態について空文字(ε)でS2aに接続すれば、
     * NFAフラグメント1 -> 自動的にフラグメント2に接続すると考えられる。
     * 
     * M1:
     * (S1a, Ca) -> (S1b, S1c, ...)
     * (S1b, Cb) -> (S1d, S1e, ...)
     * -> AS1: (S1c, S1e)
     * M2:
     * (S2a, Cc) -> (S2b, S2c, ...)
     * (S2b, Cd) -> (S2d, S2e, ...)
     * -> AS1: (S2c, S2e)
     * これを単に合成するだけだと以下のようになり、1系から2系への接続が無い = 2系での受理可能状態に到達しなくなる。
     * M12:
     * (S1a, Ca) -> (S1b, S1c, ...)
     * (S1b, Cb) -> (S1d, S1e, ...)
     * (S2a, Cc) -> (S2b, S2c, ...)
     * (S2b, Cd) -> (S2d, S2e, ...)
     * -> ここで、1系の受理可能状態を2系の初期状態に空文字(ε)で接続する。
     * M12':
     * (S1a, Ca) -> (S1b, S1c, ...)
     * (S1b, Cb) -> (S1d, S1e, ...)
     * (S1c, ε) -> (S2a)
     * (S1d, ε) -> (S2a)
     * (S2a, Cc) -> (S2b, S2c, ...)
     * (S2b, Cd) -> (S2d, S2e, ...)
     * これで、1系から2系への自動接続 = AB という文字連結でのマッチングが実現する。
     */

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
        // 状態遷移マトリクスを合成する。
        final NfaFragment r = f1.createAndMergeStateTransitionMatrixByOrOps(f2);
        for (int state : f1.acceptableStates) {
            // 1系で受理可能状態となれば、空文字(ε)で自動的に2系の初期状態に遷移させる。
            r.connect(state, Optional.empty(), f2.startState);
        }
        // 全体としての初期状態は1系の初期状態。
        r.startState = f1.startState;
        // 全体としての受理可能状態は2系の設定。
        r.acceptableStates.addAll(f2.acceptableStates);
        return r;
    }
}
