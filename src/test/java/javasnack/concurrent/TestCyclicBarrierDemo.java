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

package javasnack.concurrent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

public class TestCyclicBarrierDemo {
    /* CountDownLatch と異なり繰り返し使える・リセットできるのがポイント。
     * スレッドの待ち合わせを複数回、場合によってはループ中で繰り返したい場合に有用。
     * 
     * 以下のURLが、より分かりやすいイメージを提供してくれている。
     * - JavaのCyclicBarrierを使って平行処理を行う - Qiita
     *   https://qiita.com/nogitsune413/items/ec0132c306e1f15c6f87
     * - CyclicBarrierのサンプル - きしだのHatena
     *   https://nowokay.hatenablog.com/entry/20081128/1227840634
     */

    private static class WaitingTask implements Runnable {
        final CyclicBarrier cb;
        final AtomicInteger counter;
        final long toms;

        WaitingTask(final CyclicBarrier cb, final AtomicInteger counter, final long toms) {
            this.cb = cb;
            this.counter = counter;
            this.toms = toms;
        }

        WaitingTask(final CyclicBarrier cb, final AtomicInteger counter) {
            this(cb, counter, 0);
        }

        @Override
        public void run() {
            try {
                if (toms > 0) {
                    this.cb.await(toms, TimeUnit.MILLISECONDS);
                } else {
                    this.cb.await();
                }
                this.counter.incrementAndGet();
            } catch (InterruptedException ignore) {
                this.counter.addAndGet(10);
            } catch (BrokenBarrierException ignore) {
                this.counter.addAndGet(100);
            } catch (TimeoutException e) {
                this.counter.addAndGet(1000);
            }
        }
    }

    @Test
    public void testCyclicBarrierBasicUsageDemo() throws InterruptedException, BrokenBarrierException {
        final int NUM = 5;
        final int waitms = 50;
        final ExecutorService es = Executors.newFixedThreadPool(NUM - 1);

        final CyclicBarrier cb = new CyclicBarrier(NUM);
        assertThat(cb.getNumberWaiting()).isEqualTo(0);
        assertThat(cb.getParties()).isEqualTo(NUM);
        final AtomicInteger counter = new AtomicInteger(0);

        es.submit(new WaitingTask(cb, counter));
        Thread.sleep(waitms);
        assertThat(cb.getNumberWaiting()).isEqualTo(1);
        assertThat(cb.getParties()).isEqualTo(NUM);

        es.submit(new WaitingTask(cb, counter));
        Thread.sleep(waitms);
        assertThat(cb.getNumberWaiting()).isEqualTo(2);
        assertThat(cb.getParties()).isEqualTo(NUM);

        es.submit(new WaitingTask(cb, counter));
        Thread.sleep(waitms);
        assertThat(cb.getNumberWaiting()).isEqualTo(3);
        assertThat(cb.getParties()).isEqualTo(NUM);

        es.submit(new WaitingTask(cb, counter));
        Thread.sleep(waitms);
        assertThat(cb.getNumberWaiting()).isEqualTo(4);
        assertThat(cb.getParties()).isEqualTo(NUM);

        assertThat(cb.await()).isEqualTo(0);
        es.shutdown();
        es.awaitTermination(100, TimeUnit.MILLISECONDS);
        assertThat(counter.get()).isEqualTo(NUM - 1);

        assertThat(cb.getNumberWaiting()).isEqualTo(0);
        assertThat(cb.getParties()).isEqualTo(NUM);
        assertThat(cb.isBroken()).isFalse();

        // 解放された後も繰り返し使える(cyclic)
        counter.set(0);
        final ExecutorService es2 = Executors.newFixedThreadPool(NUM - 1);
        es2.submit(new WaitingTask(cb, counter));
        Thread.sleep(waitms);
        assertThat(cb.getNumberWaiting()).isEqualTo(1);
        assertThat(cb.getParties()).isEqualTo(NUM);

        es2.submit(new WaitingTask(cb, counter));
        Thread.sleep(waitms);
        assertThat(cb.getNumberWaiting()).isEqualTo(2);
        assertThat(cb.getParties()).isEqualTo(NUM);

        es2.submit(new WaitingTask(cb, counter));
        Thread.sleep(waitms);
        assertThat(cb.getNumberWaiting()).isEqualTo(3);
        assertThat(cb.getParties()).isEqualTo(NUM);

        es2.submit(new WaitingTask(cb, counter));
        Thread.sleep(waitms);
        assertThat(cb.getNumberWaiting()).isEqualTo(4);
        assertThat(cb.getParties()).isEqualTo(NUM);

        assertThat(cb.await()).isEqualTo(0);
        es2.shutdown();
        es2.awaitTermination(100, TimeUnit.MILLISECONDS);
        assertThat(counter.get()).isEqualTo(NUM - 1);
    }

    // 使い回せるという特徴をメインにした、無限ループで CyclicBarrier#await() を繰り返すデモ。
    private static class CyclicWaitingTask implements Runnable {
        final CyclicBarrier cb;
        final AtomicInteger counter;
        // 外部からの停止シグナルを volatile で簡易実装
        volatile boolean shutdown = false;

        CyclicWaitingTask(final CyclicBarrier cb, final AtomicInteger counter) {
            this.cb = cb;
            this.counter = counter;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    this.cb.await();
                    this.counter.incrementAndGet();
                }
            } catch (InterruptedException ignore) {
                this.counter.addAndGet(10);
            } catch (BrokenBarrierException ignore) {
                this.counter.addAndGet(100);
            }
        }
    }

    @Test
    public void testCyclicWaitingTaskDemo() throws InterruptedException, BrokenBarrierException {
        final int numOfTasks = 3;
        final ExecutorService es = Executors.newFixedThreadPool(numOfTasks);

        final CyclicBarrier cb = new CyclicBarrier(numOfTasks + 1);
        final AtomicInteger counter = new AtomicInteger(0);
        final CyclicWaitingTask[] tasks = new CyclicWaitingTask[numOfTasks];
        for (int i = 0; i < numOfTasks; i++) {
            tasks[i] = new CyclicWaitingTask(cb, counter);
            es.submit(tasks[i]);
        }
        // 生成後すぐに getNumberWaiting() しても、生成したスレッドすべてが await() まで到達してない。
        // そのため wait を入れる。
        Thread.sleep(200);
        // 生成したworkerの数だけ wait 中
        assertThat(cb.getNumberWaiting()).isEqualTo(numOfTasks);
        // テスト実行スレッドからも await() -> これでロックが解除される。
        cb.await();
        Thread.sleep(200);
        // worker 数だけ increment されているはず。
        assertThat(counter.get()).isEqualTo(numOfTasks);
        // await() 後の wait 経過 -> 再度、すべてのworkerがawait()中になるはず。
        assertThat(cb.getNumberWaiting()).isEqualTo(numOfTasks);
        // 再度ロック解除
        cb.await();
        Thread.sleep(200);
        // -> worker 数分 increment されてるはず。
        assertThat(counter.get()).isEqualTo(numOfTasks * 2);
        // ダメ押しでもう一回
        cb.await();
        Thread.sleep(200);
        assertThat(counter.get()).isEqualTo(numOfTasks * 3);
        assertThat(cb.getNumberWaiting()).isEqualTo(numOfTasks);

        // workerのshutdownフラグをセット
        for (int i = 0; i < numOfTasks; i++) {
            tasks[i].shutdown = true;
        }
        cb.await();
        // worker のロックが外れ increment された後に、worker終了。
        es.shutdown();
        es.awaitTermination(100, TimeUnit.MILLISECONDS);
        // 最終状態
        assertThat(counter.get()).isEqualTo(numOfTasks * 4);
    }

    @Test
    public void testCyclicBarrierResetDemo() throws InterruptedException {
        final int NUM = 2;
        final int waitms = 50;
        final ExecutorService es = Executors.newFixedThreadPool(NUM);

        final CyclicBarrier cb = new CyclicBarrier(NUM + 1);
        final AtomicInteger counter = new AtomicInteger(0);

        es.submit(new WaitingTask(cb, counter));
        es.submit(new WaitingTask(cb, counter));
        Thread.sleep(waitms);
        assertThat(cb.getNumberWaiting()).isEqualTo(NUM);
        assertThat(cb.getParties()).isEqualTo(NUM + 1);
        cb.reset();
        assertThat(cb.getNumberWaiting()).isEqualTo(0);
        assertThat(cb.getParties()).isEqualTo(NUM + 1);

        cb.reset();
        es.shutdown();
        es.awaitTermination(100, TimeUnit.MILLISECONDS);
        assertThat(counter.get()).isEqualTo(200); // incremented by BrokenBarrierException
    }

    @Test
    public void testCyclicBarrierActionDemo() throws InterruptedException, BrokenBarrierException {
        final int NUM = 2;
        final int waitms = 50;
        final ExecutorService es = Executors.newFixedThreadPool(NUM);

        final AtomicInteger counter = new AtomicInteger(0);
        final CyclicBarrier cb = new CyclicBarrier(NUM + 1, () -> {
            counter.addAndGet(10_000);
        });

        es.submit(new WaitingTask(cb, counter));
        es.submit(new WaitingTask(cb, counter));
        Thread.sleep(waitms);
        assertThat(cb.await()).isEqualTo(0);

        es.shutdown();
        es.awaitTermination(100, TimeUnit.MILLISECONDS);
        assertThat(counter.get()).isEqualTo(10_002);
    }

    @Test
    public void testCyclicBarrierTimeoutDemo() throws InterruptedException, BrokenBarrierException {
        final int NUM = 2;
        final ExecutorService es = Executors.newFixedThreadPool(NUM);

        final AtomicInteger counter = new AtomicInteger(0);
        final CyclicBarrier cb = new CyclicBarrier(NUM + 1, () -> {
            counter.addAndGet(10_000);
        });

        es.submit(new WaitingTask(cb, counter, 20)); // thread1 : wait 20ms
        es.submit(new WaitingTask(cb, counter)); // thread2 : wait no timeout
        Thread.sleep(50); // wait 50ms -> thread1 timeout
        assertThatThrownBy(() -> {
            cb.await();
        }).isInstanceOf(BrokenBarrierException.class);

        es.shutdown();
        es.awaitTermination(100, TimeUnit.MILLISECONDS);
        // thread1 : +1000 (via timeout exception)
        // thread2 : +100 (via broken barrier exception)
        assertThat(counter.get()).isEqualTo(1100);
    }

}
