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

public class StarNode implements INodeAssembler {
    /* NFAフラグメントの0回以上の繰り返し(A*)を表現する遷移構造を合成する。
     * 
     * 考え方:
     * NFAフラグメント1(初期状態S1a, 状態遷移マトリクスM1, 受理可能状態集合AS1)
     * M1:
     * (S1a, Ca) -> (S1b, S1c, ...)
     * (S1b, Cb) -> (S1d, S1e, ...)
     * -> AS1: (S1c, S1e)
     * 
     * 「繰り返し」を表現する方法として、受理可能状態からそのまま初期状態に巻き戻す遷移を追加する。
     * -> ソースとなるNFAフラグメントの受理可能状態集合のそれぞれについて、
     * 空文字(ε)でその初期状態に遷移する設定を追加する。
     * また0回以上を表現するために、初期状態を受理可能状態に追加する。
     * 
     * M2:
     * (S1a, Ca) -> (S1b, S1c, ...)
     * (S1b, Cb) -> (S1d, S1e, ...)
     * (S1c, ε) -> (S1a) // 受理可能状態から初期状態に巻き戻す
     * (S1e, ε) -> (S1a) // 受理可能状態から初期状態に巻き戻す
     * -> AS1: (S1a, S1c, S1e) // 初期状態のままでも受理可能
     * 
     * ・・・が、これは何かおかしい。S1a -> S1a の遷移設定が無い。
     * 空文字(ε)で追加すれば良さそうだが、ここで実現したいのは
     * 「NFAフラグメントの繰り返しを表現するNFAフラグメント」である。
     * -> ラッパーとなるNFAフラグメント専用に初期状態S2を追加し、それを受理可能状態に追加、
     * さらに空文字(ε)で元のS1aに接続する形を取る。
     * 
     * M2':
     * (S2, ε) -> (S1a) // ラッパー側の初期状態S2からそのままS1aに遷移
     * (S1a, Ca) -> (S1b, S1c, ...)
     * (S1b, Cb) -> (S1d, S1e, ...)
     * (S1c, ε) -> (S1a) // 受理可能状態から初期状態に巻き戻す
     * (S1e, ε) -> (S1a) // 受理可能状態から初期状態に巻き戻す
     * -> AS1: (S2, S1c, S1e) // ラッパー側の初期状態S2のままでも受理可能
     * 
     * ラッパー側でわざわざ初期状態を生成している点について、
     * 元となる連載記事では「そうしなかった場合に何が不都合となるか」までは解説していない。
     * そのため、こちらでもそこまでは踏み込まず、元の連載記事でのPythonコードの趣旨を忠実にJavaに変換するに留める。
     * 
     * これで、NFAフラグメントの繰り返し(A*)のマッチングが実現する。
     */

    private final INodeAssembler op;

    public StarNode(final INodeAssembler op) {
        this.op = op;
    }

    @Override
    public NfaFragment assemble(final Context context) {
        final NfaFragment original = this.op.assemble(context);
        final NfaFragment r = original.createAndCopyStateTransitionMatrix();
        for (int state : original.acceptableStates) {
            // 元のNFAフラグメントの受理可能状態を、そのまま初期状態に巻き戻す。
            r.connect(state, Optional.empty(), original.startState);
        }
        // ラッパー用に初期状態を新たに生成し、それを元のNFAフラグメントの初期状態に接続する。
        final int newState = context.newState();
        r.connect(newState, Optional.empty(), original.startState);
        r.startState = newState;
        r.acceptableStates.addAll(original.acceptableStates);
        // ラッパー用の初期状態のままでも受理可能とする。
        r.acceptableStates.add(newState);
        return r;
    }
}
