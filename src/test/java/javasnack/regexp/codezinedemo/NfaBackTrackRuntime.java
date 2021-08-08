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
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

public class NfaBackTrackRuntime {
    /* 連載記事中には解説が無いが、(6) からDLできるサンプルコードには dfareg/nfa.py として収録されている。
     * 2種類含まれており、こちらは深さ優先で探索するタイプ。
     * 深さ優先 : 選択肢を適当に選んで失敗するまで潜り、失敗したら戻って別の選択肢をやり直す (バックトラック)
     */

    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor(staticName = "of")
    private static class TrackPoint implements Comparable<TrackPoint> {
        // バックトラック用に、分岐位置の状態とそこからの残りの文字列を保存するコンテナ。
        final int state;
        final String left;

        @Override
        public int compareTo(TrackPoint o) {
            if (this.state != o.state) {
                return this.state - o.state;
            }
            if (Objects.nonNull(this.left)) {
                if (Objects.nonNull(o.left)) {
                    return this.left.compareTo(o.left);
                } else {
                    return 1;
                }
            } else {
                // this.left == null
                return Objects.nonNull(o.left) ? -1 : 0;
            }
        }
    }

    private final Nfa nfa;
    private int currentState; // エンドマーカ: -1
    private String left = null; // エンドマーカ: null
    private final Queue<TrackPoint> branches = new ArrayDeque<>();
    private Set<TrackPoint> alreadyTracedBranches = new HashSet<>();
    private final boolean enableTraceLog;
    private final boolean enableTracedBackTrackSkipping;
    private int countOfBackTracked = 0;

    public int getCountOfBackTracked() {
        return this.countOfBackTracked;
    }

    public NfaBackTrackRuntime(
            final Nfa nfa,
            final boolean enableTraceLog,
            final boolean enableTracedBackTrackSkipping) {
        this.nfa = nfa;
        this.currentState = nfa.start;
        this.enableTraceLog = enableTraceLog;
        this.enableTracedBackTrackSkipping = enableTracedBackTrackSkipping;
    }

    public NfaBackTrackRuntime(final Nfa nfa) {
        this(nfa, false, true);
    }

    private boolean transitSingleChar() {
        if (Objects.isNull(this.left)) {
            // この時点で残り文字列がnull(エンドマーカ)であれば、これ以上遷移する必要が無いので false を返す。
            return false;
        }

        // 次の遷移可能な状態とその時の残り文字列を、分岐ポイントとして保存する。
        final Set<TrackPoint> setOfNextCandidateBranch = new TreeSet<>();

        // 空文字(ε)で遷移しうる状態を、分岐ポイントに保存する。
        final Set<Integer> setOfNextStateByEpsilon = this.nfa.transition.apply(currentState, Optional.empty());
        for (int state : setOfNextStateByEpsilon) {
            setOfNextCandidateBranch.add(TrackPoint.of(state, left));
        }

        if (this.left.length() > 0) {
            // 次の1文字で遷移可能な状態を取り出し、分岐ポイントに保存する。
            final char nextChar = this.left.charAt(0);
            if (this.left.length() == 1) {
                this.left = "";
            } else {
                this.left = this.left.substring(1);
            }
            final Set<Integer> setOfNextState = this.nfa.transition.apply(currentState, Optional.of(nextChar));
            for (int state : setOfNextState) {
                setOfNextCandidateBranch.add(TrackPoint.of(state, left));
            }
        } else {
            // これ以上読む文字が無いので、エンドマーカを分岐ポイントに含める。
            setOfNextCandidateBranch.add(TrackPoint.of(currentState, null));
        }

        if (enableTracedBackTrackSkipping) {
            // 今回の遷移で取り出した分岐ポイントの集合から、既にトレースした分岐ポイントを除去する。
            setOfNextCandidateBranch.removeAll(alreadyTracedBranches);
            /* これを行わない場合、全ての分岐を試行することになり
             * 正規表現によっては深刻な性能劣化が発生する。
             * 
             * パターン1, EDA : Exponential Degree of Ambiguity
             * O(2^N) などの指数計算時間がかかるパターン。
             * 例: (a|a)*, (a*)* など。
             * 問題となる文字列例: "aaa...ab"
             * 
             * パターン2, IDA : Infinite Degree of Amgibuity
             * "infinite degree polynomial" という表現もある。
             * O(N^2) など多項式計算時間がかかるパターン。
             * 例: a*a*a*a*
             * ( "a*a*" だけなら O(N) だが、それがもう1ペア連結することで O(N^2) となる )
             * 問題となる文字列例: "aaa...ab"
             */
        }

        if (setOfNextCandidateBranch.isEmpty()) {
            // これ以上辿るべき分岐ポイントが見つからなかったため、状態や残り文字列にエンドマーカを設定する。
            this.currentState = -1;
            this.left = null;
            if (this.enableTraceLog) {
                System.out.println("END-MARKED");
            }
        } else {
            // 辿るべき次の分岐ポイントを取り出し、状態と残り文字列を復元する。
            final Queue<TrackPoint> queueOfNextCandidateBranch = new ArrayDeque<>(setOfNextCandidateBranch);
            final TrackPoint next = nextTrackPoint(queueOfNextCandidateBranch);
            if (this.enableTraceLog) {
                System.out.println("NEXT-TRACKPOINT: " + next);
            }
            this.currentState = next.state;
            this.left = next.left;
            // 残りの分岐ポイントを、次回以降の分岐ポイント一覧に保存する。
            this.branches.addAll(queueOfNextCandidateBranch);
            if (this.enableTraceLog) {
                for (TrackPoint dump : this.branches) {
                    System.out.println("SAVED-TRACKPOINTS: " + dump);
                }
            }
        }
        return true;
    }

    private TrackPoint nextTrackPoint(final Queue<TrackPoint> queueOfTrackPoint) {
        final TrackPoint next = queueOfTrackPoint.remove(); // あえて要素がなければ例外をthrowさせ、異常検知させる。
        // 取り出した分岐ポイントを、トレース済みに移す。
        this.alreadyTracedBranches.add(next);
        return next;
    }

    private boolean backtrack() {
        if (this.branches.isEmpty()) {
            // セーブされた分岐ポイントが空っぽであれば、backtrack発生せず。
            return false;
        }
        // セーブされた分岐ポイントから1つ取り出し、現在状態と残り文字列を復元する。
        final TrackPoint nextBranch = nextTrackPoint(this.branches);
        this.currentState = nextBranch.state;
        this.left = nextBranch.left;
        if (this.enableTraceLog) {
            System.out.println("##>>BACKTRACK<<##: " + nextBranch);
        }
        // backtrack発生
        countOfBackTracked++;
        return true;
    }

    private boolean isCurrentStatusAcceptable() {
        // 現在状態がNFAの受理可能状態の集合に含まれていれば、受理可能と判定。
        return this.nfa.accept.contains(this.currentState);
    }

    /** バックトラック方式ではプログラムバグで無限ループが発生する危険があるため、安全リミットを設定する。*/
    private static final int LOOP_LIMITTER = 0xFFFFF;

    private void checkLimit(final int limitter) {
        if (limitter > LOOP_LIMITTER) {
            throw new IllegalStateException("loop limit over");
        }
    }

    public boolean accept(final String input) {
        this.left = input;
        int limitter = 0;
        while (true) {
            limitter++;
            checkLimit(limitter);
            while (this.transitSingleChar()) {
                // 適当な経路でNFAを辿れるところまで辿り、分岐ポイントもセーブしておく。
                limitter++;
                checkLimit(limitter);
            }
            if (this.isCurrentStatusAcceptable()) {
                // 辿れる範囲で辿り終わった時点で受理可能状態であれば、受理。
                return true;
            }
            if (!this.backtrack()) {
                // 受理可能でなく、バックトラックする分岐ポイントも尽きたため、受理できないと判定。
                return false;
            }
            /* まだ受理可能状態にはなっていないため、バックトラックする
             * = 保存された分岐ポイントの1つに立ち戻り、そこから辿り直す。
             */
        }
    }
}
