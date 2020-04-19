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

package javasnack.langspecs;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * {@link Map} オブジェクトを簡単に初期化するためのショートカットテクニックのサンプル集。
 * 
 * Java9 以降なら {@link Map#of(Object, Object)} シリーズが使えるため、これらのテクニックは不要。
 */
public class TestMapBuilderDemo {

    /**
     * Java9 以降の {@link Map#of(Object, Object)} と同様、
     * key-valueペアの引数を一つずつ増やして地道にfactoryメソッドを準備する例。
     * 一見、すごいダサくてクールじゃないように見えてしまうが・・・
     * Java8 以前であれば、案外これが一番安定して使える技法じゃなかろうか。
     * そもそも Map の初期化で何十個もエントリーをベタ書きすることはあまり無いと思う。
     * 10個程度までのエントリーであれば、力技でメソッドを用意しておくだけで十分実用できそう。
     */
    private static class TediousMapBuilder {
        static <K, V> Map<K, V> map(K k1, V v1) {
            final Map<K, V> m = new LinkedHashMap<>();
            m.put(k1, v1);
            return m;
        }

        static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2) {
            final Map<K, V> m = new LinkedHashMap<>();
            m.put(k1, v1);
            m.put(k2, v2);
            return m;
        }

        static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2, K k3, V v3) {
            final Map<K, V> m = new LinkedHashMap<>();
            m.put(k1, v1);
            m.put(k2, v2);
            m.put(k3, v3);
            return m;
        }

        // ...
    }

    @Test
    public void testTediousMapBuilder() {
        Map<String, String> m = TediousMapBuilder.map("k1", "v1");
        assertThat(m.size()).isEqualTo(1);
        assertThat(m.get("k1")).isEqualTo("v1");
        m = TediousMapBuilder.map("k1", "v1", "k2", "v2");
        assertThat(m.size()).isEqualTo(2);
        assertThat(m.get("k1")).isEqualTo("v1");
        assertThat(m.get("k2")).isEqualTo("v2");
        m = TediousMapBuilder.map("k1", "v1", "k2", "v2", "k3", "v3");
        assertThat(m.size()).isEqualTo(3);
        assertThat(m.get("k1")).isEqualTo("v1");
        assertThat(m.get("k2")).isEqualTo("v2");
        assertThat(m.get("k3")).isEqualTo("v3");
    }

    /**
     * map(entry(k, v), entry(k, v), ...) 形式でDSLっぽい初期化を実現するための、entry一個分を表現するクラス。
     * もし初期化entryが何十個も出てくるようであれば、この形式のほうが良いかも。
     *
     * @param <K> type of key
     * @param <V> type of value
     */
    private static class RedundantMapBuilderEntry<K, V> {
        public final K key;
        public final V val;

        public RedundantMapBuilderEntry(final K k, final V v) {
            this.key = k;
            this.val = v;
        }
    }

    static <K, V> RedundantMapBuilderEntry<K, V> entry(K k, V v) {
        return new RedundantMapBuilderEntry<>(k, v);
    }

    /* @SafeVarargs が必要な理由については以下参照。
     * "Type safety: Potential heap pollution via varargs parameter entries"
     * see: https://stackoverflow.com/questions/38781226/potential-heap-pollution-via-varargs-parameter-for-enume-why
     * 
     * 実用の際は、K, V を K extends XXXX / V extends YYYY みたいにして境界型パラメータにすると
     * インターフェイスを使えて便利かもしれない。
     */
    @SafeVarargs
    public static <K, V> Map<K, V> map(RedundantMapBuilderEntry<K, V>... entries) {
        final Map<K, V> map = new LinkedHashMap<>();
        for (RedundantMapBuilderEntry<K, V> e : entries) {
            map.put(e.key, e.val);
        }
        return map;
    }

    @Test
    public void testRedundantMapBuilder() {
        Map<String, String> m = map(entry("k1", "v1"));
        assertThat(m.size()).isEqualTo(1);
        assertThat(m.get("k1")).isEqualTo("v1");
        m = map(entry("k1", "v1"), entry("k2", "v2"));
        assertThat(m.size()).isEqualTo(2);
        assertThat(m.get("k1")).isEqualTo("v1");
        assertThat(m.get("k2")).isEqualTo("v2");
        m = map(entry("k1", "v1"), entry("k2", "v2"), entry("k3", "v3"));
        assertThat(m.size()).isEqualTo(3);
        assertThat(m.get("k1")).isEqualTo("v1");
        assertThat(m.get("k2")).isEqualTo("v2");
        assertThat(m.get("k3")).isEqualTo("v3");
    }

    /* この他に
     * https://github.com/benjiman/lambda-type-references
     * より、
     * https://github.com/benjiman/lambda-type-references/blob/master/src/test/java/com/benjiweber/HashLiteralExample.java
     * にあるようなラムダ式のパラメータ名をリフレクションで取り出すことによる
     * mapビルダーの例も見つかった。
     * 解説ブログ: https://benjiweber.co.uk/blog/2015/08/04/lambda-type-references/
     * ただしこのテクニックを使うには javac で -parameters オプションを指定する必要がある。
     * また JDK も 8u60 以上が必要らしい：
     * https://mike-neck.hatenadiary.com/entry/2015/08/21/034542
     * https://twitter.com/benjiweber/status/633022519650451456
     * 
     * JDKバージョンについてはあまり心配する必要は無いと思うが、 "-parameters" オプションについては
     * メソッドのパラメータ名が公開されるということから、互換性や情報漏えいの懸念点が指摘されている：
     * https://stackoverflow.com/questions/44067477/drawbacks-of-javac-parameters-flag
     * 
     * 実験的には興味深いが(単にリフレクションを使うだけでなく、SerializedLambda を経由しているなど
     * 相当ディープな考察の上に実現されている)、実用にはちょっと向いてなさそうなので検証はスキップ。
     */
}
