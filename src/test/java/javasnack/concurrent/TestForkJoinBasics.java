/*
 * Copyright 2018 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

import org.junit.jupiter.api.Test;

/* see:
 * https://docs.oracle.com/javase/tutorial/essential/concurrency/forkjoin.html
 * https://docs.oracle.com/javase/jp/8/docs/api/java/util/concurrent/ForkJoinPool.html
 * https://docs.oracle.com/javase/jp/8/docs/api/java/util/concurrent/ForkJoinTask.html
 * http://waman.hatenablog.com/entry/20111203/1322942842
 * http://d.hatena.ne.jp/Kazuhira/20120923/1348394888
 * http://d.hatena.ne.jp/miyakawa_taku/20110620/1308525439
 */
public class TestForkJoinBasics {

    static class RecursiveTaskSample extends RecursiveTask<Integer> {
        private static final long serialVersionUID = 1L;
        static final int THRESHOLD = 10;
        final String taskName;;
        final List<String> strings;

        RecursiveTaskSample(final List<String> strings) {
            this(strings, "1");
        }

        RecursiveTaskSample(final List<String> strings, final String taskName) {
            this.taskName = taskName;
            this.strings = strings;
        }

        @Override
        protected Integer compute() {
            final String tname = Thread.currentThread().getName();
            System.out.printf("[%s] - %s start%n", tname, this.taskName);
            if (this.strings.size() < THRESHOLD) {
                int sum = 0;
                for (String s : this.strings) {
                    sum += s.length();
                }
                System.out.printf("[%s] - %s direct-compute end : %d%n", tname, this.taskName, sum);
                return sum;
            } else {
                final int m = this.strings.size() / 2;
                RecursiveTaskSample t1 = new RecursiveTaskSample(this.strings.subList(0, m), this.taskName + "-1");
                t1.fork();
                RecursiveTaskSample t2 = new RecursiveTaskSample(this.strings.subList(m, this.strings.size()),
                        this.taskName + "-2");
                t2.fork();
                final int r = t1.join() + t2.join();
                System.out.printf("[%s] - %s fork-join end : %d%n", tname, this.taskName, r);
                return r;
            }
        }
    }

    @Test
    public void testRecursiveTaskSample() {
        List<String> strings = new ArrayList<>(200);
        for (int i = 0; i < 200; i++) {
            strings.add("abc");
        }
        ForkJoinPool forkJoin = new ForkJoinPool();
        int n = forkJoin.invoke(new RecursiveTaskSample(strings));
        assertEquals(600, n);
    }

    static class RecursiveActionSample extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        final CountDownLatch latch;
        final String taskName;
        final StringBuffer dest;
        final int repeats;

        RecursiveActionSample(final StringBuffer dest, final int repeats, final CountDownLatch latch) {
            this.latch = latch;
            this.taskName = "1";
            this.dest = dest;
            this.repeats = repeats;
        }

        RecursiveActionSample(final StringBuffer dest, final int repeats, final String taskName) {
            this.latch = null;
            this.taskName = taskName;
            this.dest = dest;
            this.repeats = repeats;
        }

        @Override
        protected void compute() {
            final String tname = Thread.currentThread().getName();
            System.out.printf("!![%s] - %s start%n", tname, this.taskName);
            if (repeats < 5) {
                for (int i = 0; i < repeats; i++) {
                    dest.append("hello");
                }
                System.out.printf("!![%s] - %s direct-compute end%n", tname, this.taskName);
            } else {
                RecursiveActionSample t1 = new RecursiveActionSample(dest, repeats - 1, taskName + "-1");
                RecursiveActionSample t2 = new RecursiveActionSample(dest, repeats - 2, taskName + "-2");
                RecursiveActionSample t3 = new RecursiveActionSample(dest, repeats - 3, taskName + "-3");
                RecursiveActionSample t4 = new RecursiveActionSample(dest, repeats - 4, taskName + "-4");
                invokeAll(t1, t2, t3, t4);
                System.out.printf("!![%s] - %s invoke-all end%n", tname, this.taskName);
            }
            if (Objects.nonNull(latch)) {
                latch.countDown();
            }
        }
    }

    @Test
    public void testRecursiveActionSample() throws InterruptedException {
        ForkJoinPool forkJoin = new ForkJoinPool();
        StringBuffer sb = new StringBuffer(1000);
        CountDownLatch latch = new CountDownLatch(1);
        forkJoin.execute(new RecursiveActionSample(sb, 10, latch));
        latch.await();
        StringBuilder expected = new StringBuilder(2000);
        for (int i = 0; i < 258; i++) {
            expected.append("hello");
        }
        assertEquals(expected.toString().length(), sb.toString().length());
        assertEquals(expected.toString(), sb.toString());
    }
}
