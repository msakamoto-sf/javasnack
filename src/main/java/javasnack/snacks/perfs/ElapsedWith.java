/*
 * Copyright 2013 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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

package javasnack.snacks.perfs;

/**
 * 簡単なパフォーマンス計測で経過時間を返す際に、計測対象のメソッド戻り値も一緒に返すことで
 * 使用されていないロジックが削除される最適化を回避するための補助クラス。
 * (JMH における Blackhole の代用)
 * 
 * @author msakamoto
 * @param <T> 計測対象のメソッド戻り値型
 */
public class ElapsedWith<T> {
    public final T data;
    public final long elapsed;

    public ElapsedWith(final T data, long elapsed) {
        this.data = data;
        this.elapsed = elapsed;
    }

    public static <T> ElapsedWith<T> of(final T data, long elapsed) {
        return new ElapsedWith<>(data, elapsed);
    }
}
