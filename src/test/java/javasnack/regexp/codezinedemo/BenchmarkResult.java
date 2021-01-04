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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@AllArgsConstructor(staticName = "of")
public class BenchmarkResult {
    final long totalNanos;
    final double avgNanos;
    final int matchedCount;

    final String fomatTotalNanos() {
        final NumberFormat fmt = NumberFormat.getNumberInstance();
        return fmt.format(totalNanos);
    }

    public static BenchmarkResult benchmark(final Supplier<Boolean> regexpTask, final int count) {
        final List<Long> elapsedNanos = new ArrayList<>(count);
        int matched = 0;
        for (int i = 0; i < count; i++) {
            final long started = System.nanoTime();
            /* JITによる未使用コードの削除を回避するため、マッチ結果の戻り値を使う処理を挿入
             * -> マッチしたらカウントアップする処理を入れている。
             */
            final boolean r = regexpTask.get();
            final long elapsed = System.nanoTime() - started;
            elapsedNanos.add(elapsed);
            if (r) {
                matched++;
            }
        }

        return BenchmarkResult.of(
                elapsedNanos.stream().mapToLong(x -> x).sum(),
                elapsedNanos.stream().mapToLong(x -> x).average().getAsDouble(),
                matched);
    }
}
