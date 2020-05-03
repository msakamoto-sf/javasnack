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

package javasnack.snacks.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javasnack.RunnableSnack;
import javasnack.tool.RandomString;

/* NOTE:
 * slf4j + logback-classic のデフォルトでは slf4j の BasicMarkerFactory が使用される。
 * https://github.com/qos-ch/slf4j/blob/v_1.7.30/slf4j-api/src/main/java/org/slf4j/helpers/BasicMarkerFactory.java
 * これは作成した Marker インスタンスを内部 Map で保持し、これを singleton インスタンスで管理している。
 * -> 大量の Marker を作り続けると BasicMarkerFactory 内部の Map インスタンスに Marker の参照が残り続けGCされず、
 * OutOfMemoryError が発生する。
 * これについて、slf4j / logback のドキュメントともに Marker について非常に記述が薄く、ほとんど書かれていない。
 * また slf4j 側の javadoc についても getMarker() 挙動についての注意書きが無い。
 * BasicMarkerFactory 自体も api 内部実装のhelperクラスなのでapiとしてのjavadocには現れない。
 * 
 * そもそも Marker 自体、公式含め使い方の説明があまりない。あってもslf4j系でちらっと書かれてるくらい。
 * 日本語でようやく以下の記事が詳しい。
 * http://www.nurs.or.jp/~sug/soft/log4j/log4jlb.htm
 * 
 * 注意深く読むと、Markerは子供を持ったツリー構造を構成できることから、裏側で何かしらインスタンス管理をしているのを
 * 読み取ろうと思えば読み取れる。またAPIとしても "create"Marker ではなく "get"Marker という点から、
 * singletonなニュアンスを感じ取ろうと思えば感じ取れる。
 * 
 * どちらにしても Marker はlong-livedなインスタンスで、基本的には使い回しを意図したものとなる。
 * セッションIDやユーザ情報など、ログ出力の都度変化するようなものについてはMDCを使うのが正しい使い方と思われる。
 * 
 * ということで、このデモではあえてNGな使い方 : ループ中で都度ランダム文字列で marker 作成、というのを
 * 無限ループで回すことで、 "-Xmx10m" のように小さめのヒープサイズを指定すると、数十秒後にOOMが発生するのを
 * デモするコードとなっている。
 */
public class LogbackLoopWithMarkerDemo implements RunnableSnack {
    @Override
    public void run(final String... args) {
        final Logger logger = LoggerFactory.getLogger(this.getClass());
        while (true) {
            final Marker m = MarkerFactory.getMarker(RandomString.get(20));
            logger.trace(m, "basic demo");
            logger.debug(m, "basic demo");
            logger.info(m, "basic demo");
            logger.warn(m, "basic demo");
            logger.error(m, "basic demo");
        }
    }
}
