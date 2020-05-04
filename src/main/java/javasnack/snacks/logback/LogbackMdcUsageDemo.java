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

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javasnack.RunnableSnack;
import javasnack.tool.Sleep;

/* MDCの基本的なデモ + thread-pool 上で実行した場合にMDCはどうなるか？のデモ
 * 
 * reference:
 * ref[1] http://logback.qos.ch/manual/mdc.html
 * ref[2] http://logback.qos.ch/manual/mdc_ja.html
 * ref[3] http://logback.qos.ch/manual/layouts.html#ClassicPatternLayout
 * 
 * see-also:
 * - logback+MDCでWebアプリのリクエスト内容を簡単にログに出力する方法 - Qiita
 *   https://qiita.com/namutaka/items/c35c437b7246c5e4d729
 * - 5.6. ロギング — TERASOLUNA Global Framework Development Guideline 1.0.0.publicreview documentation
 *   https://terasolunaorg.github.io/guideline/public_review/ArchitectureInDetail/Logging.html
 *   (TERASOLUNAフレームワークのドキュメントだが、Spring系におけるMDCの使い方の参考になる)
 * - java - How to use MDC with thread pools? - Stack Overflow
 *   https://stackoverflow.com/questions/6073019/how-to-use-mdc-with-thread-pools
 * 
 * NOTE:
 * 
 * MDCはthreadごとに管理(ThreadLocalを使用)しているが、ちょっとややこしいことに、本家マニュアルだと
 * > Also note that a child thread does not automatically inherit a copy of the mapped diagnostic context of its parent.
 * と前半で書いているのに、後半で
 * > MDC operations such as put() and get() affect only the MDC of the current thread, 
 * > and the children of the current thread. 
 * と、まるで子クラスにも自動で反映されるような書きっぷりになっている。
 * 
 * これは前者が正で、後者はおそらくマニュアルの反映ミスだと思われる。
 * なぜかというと、子クラスへの引き継ぎが以下の対応で無効化されているため。
 * ref[4] https://jira.qos.ch/browse/LOGBACK-422
 * ref[5] https://github.com/qos-ch/logback/commit/aa7d584ecdb1638bfc4c7223f4a5ff92d5ee6273
 * -> 2020-04 時点の logback-classic:1.2.3 だと上記のcommitの通りになっていたので、
 * 実際、子クラスへの引き継ぎは「行われない」のが正と思われる。
 * 
 * ちなみに ThreadLocal には InheritableThreadLocal というサブクラスがある。
 * new Thread() はデフォルトで InheritableThreadLocal を継承する。(ただの ThreadLocal は継承しない)
 * ref[5] のcommitを追ってみると、修正前は InheritableThreadLocal だったのがただの ThreadLocal に修正されている。
 * これにより、breaking-change として子クラスへの引き継ぎが行われなくなったものと思われる。
 * 
 * さらに thread-pool を使う場合は、以下の公式マニュアルにあるとおりスレッド生成がカスタムされており、
 * そもそも InheritableThreadLocal を引き継がない設定になっている場合もある。
 * ref[6] http://logback.qos.ch/manual/mdc.html#managedThreads
 * ref[7] http://logback.qos.ch/manual/mdc_ja.html#managedThreads
 * 
 * このため ref[5] でworkarroundとして示されたラッパーコード例のように、
 * アプリケーション側でMDCを引き継ぐような設計を組み込む必要がある。
 */
public class LogbackMdcUsageDemo implements RunnableSnack {
    @Override
    public void run(final String... args) {
        final Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("MDC usage demo[1:nothing happend yet]");

        MDC.put("key1", "hello");
        logger.info("MDC usage demo[2:put key1]");

        MDC.put("key2", "world");
        logger.info("MDC usage demo[3:put key2]");

        MDC.put("key3", "foo");
        logger.info("MDC usage demo[4:put key3]");

        MDC.remove("key1");
        logger.info("MDC usage demo[5:removed key1]");

        MDC.clear();
        logger.info("MDC usage demo[6:cleared]");

        MDC.put("key1", "HELLO");
        MDC.put("key2", "WORLD");

        // 2スレッド固定のスレッドプールを作成し、2つずつ順にworkerを動かしてみる。
        final var twoThreads = Executors.newFixedThreadPool(2);

        // MDC未サポートのworkerを動かす -> MDCは空
        twoThreads.submit(new LogWorkerWithoutMdc(1));
        twoThreads.submit(new LogWorkerWithoutMdc(2));

        // MDCを内部でsetするworkerを動かす。現在スレッドとは異なる値をput -> そちらが出力される。
        twoThreads.submit(new LogWorkerWithMdc(3, "foo", "bar", "baz"));
        twoThreads.submit(new LogWorkerWithMdc(4, "FOO", "BAR", "BAZ"));

        // 上記ではclear()してないため、MDC未サポートのworkerを動かすと前のが残ってる。
        twoThreads.submit(new LogWorkerWithoutMdc(5));
        twoThreads.submit(new LogWorkerWithoutMdc(6));

        // MDCサポートのラッパーを経由した実行 -> 現在スレッドのMDCが出力される。
        // (こちらは clear までしてる)
        twoThreads.submit(MdcWrapper.wrap(new LogWorkerWithoutMdc(7)));
        twoThreads.submit(MdcWrapper.wrap(new LogWorkerWithoutMdc(8)));

        // clear までしてるので残らない。
        twoThreads.submit(new LogWorkerWithoutMdc(9));
        twoThreads.submit(new LogWorkerWithoutMdc(10));

        // Caller 版を動かしてみる -> 現在スレッドのMDCが出力される。
        twoThreads.submit(MdcWrapper.wrap(new LogWorkerWithoutMdc2(11)));
        twoThreads.submit(MdcWrapper.wrap(new LogWorkerWithoutMdc2(12)));

        // clear までしてるので残らない。
        twoThreads.submit(new LogWorkerWithoutMdc(13));
        twoThreads.submit(new LogWorkerWithoutMdc(14));
        twoThreads.shutdown();
    }

    private static class LogWorkerWithoutMdc implements Runnable {
        final int id;

        LogWorkerWithoutMdc(final int id) {
            this.id = id;
        }

        @Override
        public void run() {
            final Logger logger = LoggerFactory.getLogger(this.getClass());
            for (int i = 0; i < 5; i++) {
                logger.info("id:{}, count#{}", this.id, i);
                Sleep.seconds(1);
            }
        }
    }

    /* MDCを手動で引き継ぐ例。
     * -> worker全てに同等のコードを埋め込まないと行けないので、忘れやすい。
     * 実際、この例は MDC.clear() を呼ぶのを忘れてる例。
     */
    private static class LogWorkerWithMdc implements Runnable {
        final int id;
        final String key1;
        final String key2;
        final String key3;

        LogWorkerWithMdc(final int id, final String key1, final String key2, final String key3) {
            this.id = id;
            this.key1 = key1;
            this.key2 = key2;
            this.key3 = key3;
        }

        @Override
        public void run() {
            final Logger logger = LoggerFactory.getLogger(this.getClass());
            MDC.put("key1", key1);
            MDC.put("key2", key2);
            MDC.put("key3", key3);
            for (int i = 0; i < 5; i++) {
                logger.info("id:{}, count#{}", this.id, i);
                Sleep.seconds(1);
            }
        }
    }

    // Caller 版, Void で戻り値を使わないことを明示。
    private static class LogWorkerWithoutMdc2 implements Callable<Void> {
        final int id;

        LogWorkerWithoutMdc2(final int id) {
            this.id = id;
        }

        @Override
        public Void call() throws Exception {
            final Logger logger = LoggerFactory.getLogger(this.getClass());
            for (int i = 0; i < 5; i++) {
                logger.info("id:{}, count#{}", this.id, i);
                Sleep.seconds(1);
            }
            return null;
        }
    }

    // ref[5] の中で例示されているサンプルコードを参考にしたラッパー。
    // Runnable / Callable 2パターン用意。
    private static class MdcWrapper {
        static Runnable wrap(final Runnable runnable) {
            final Map<String, String> mdc = MDC.getCopyOfContextMap();
            return new Runnable() {
                @Override
                public void run() {
                    MDC.setContextMap(mdc);
                    runnable.run();
                    MDC.clear();
                }
            };
        }

        static <R> Callable<R> wrap(final Callable<R> callable) {
            final Map<String, String> mdc = MDC.getCopyOfContextMap();
            return new Callable<>() {
                @Override
                public R call() throws Exception {
                    MDC.setContextMap(mdc);
                    try {
                        final R r = callable.call();
                        MDC.clear();
                        return r;
                    } catch (Throwable t) {
                        throw t;
                    }
                }
            };
        }
    }
}