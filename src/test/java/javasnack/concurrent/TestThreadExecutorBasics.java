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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.Test;

public class TestThreadExecutorBasics {

    @Test
    void testFixedThreadPoolDemo() throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(2);
        final int NUM = 5;
        CountDownLatch latch = new CountDownLatch(NUM);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignore) {
                }
                latch.countDown();
            }
        };
        for (int i = 0; i < NUM; i++) {
            es.submit(task);
        }
        assertFalse(es.isTerminated());
        assertFalse(es.isShutdown());
        latch.await();
        assertFalse(es.isTerminated());
        assertFalse(es.isShutdown());
        es.shutdown(); // REQUIRED.
        assertFalse(es.isTerminated());
        assertTrue(es.isShutdown());
    }

    @Test
    void testFixedThreadPoolWithCustomThreadFactory() throws InterruptedException, ExecutionException {
        class MyThreadFactory implements ThreadFactory {
            int count = 0;

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "MyThreadNo." + count);
                count++;
                return t;
            }
        }
        MyThreadFactory tf = new MyThreadFactory();
        ExecutorService es = Executors.newFixedThreadPool(3, tf);
        final int NUM = 7;
        CountDownLatch latch = new CountDownLatch(NUM);
        class MyTask implements Callable<String> {
            @Override
            public String call() throws Exception {
                latch.countDown();
                return Thread.currentThread().getName();
            }
        }
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < NUM; i++) {
            futures.add(es.submit(new MyTask()));
        }
        latch.await();
        es.shutdown(); // REQUIRED.
        assertEquals(tf.count, 3);
        assertEquals(futures.size(), 7);
        for (int i = 0; i < NUM; i++) {
            assertTrue(futures.get(0).get().startsWith("MyThreadNo."));
        }
    }

    @Test
    void testShudown() throws InterruptedException {
        ExecutorService es = Executors.newSingleThreadExecutor();
        final int NUM = 5;
        CountDownLatch latch = new CountDownLatch(NUM);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignore) {
                }
                latch.countDown();
            }
        };
        for (int i = 0; i < NUM; i++) {
            es.submit(task);
        }
        // shutdown() rejects new task submit(), and wait for all task gone.
        es.shutdown();
        assertTrue(es.isShutdown());
        assertFalse(es.isTerminated());
        try {
            es.submit(task);
            fail("should not reach here.");
        } catch (RejectedExecutionException expected) {
            // already called shutdown(), new submit() was rejected, expected behaviour.
        }
        // all task is done, so latch.await() returns :)
        latch.await();
        assertFalse(es.isTerminated());
    }

    class ShutdownNowDemoTask implements Runnable {
        final AtomicInteger counts;
        final CountDownLatch latch;

        public ShutdownNowDemoTask(AtomicInteger counts, CountDownLatch latch) {
            this.counts = counts;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(50);
                this.counts.addAndGet(1);
            } catch (InterruptedException expected) {
            }
            this.latch.countDown();
        }
    }

    @Test
    void testShutdownNow() throws InterruptedException {
        ExecutorService es = Executors.newSingleThreadExecutor();
        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger counts = new AtomicInteger(0);
        for (int i = 0; i < 5; i++) {
            es.submit(new ShutdownNowDemoTask(counts, latch));
        }
        latch.await(); // 2 * 50 = wait 100 msec.
        Thread.sleep(10); // no.3 task has begun.
        List<Runnable> remains = es.shutdownNow(); // no.3 task has begun...
        // shutdownNow() cancels waiting tasks AND current running task.

        // "current" count-up task would be canceled, so we'll got "2".
        assertEquals(counts.get(), 2);
        // we'll got 2 tasks canceled. (2 tasks done, 1 task has begun)
        assertEquals(remains.size(), 2);
        assertTrue(es.isShutdown());
        assertFalse(es.isTerminated());
    }

    @Test
    void testAwaitTermination() throws InterruptedException {
        ExecutorService es = Executors.newSingleThreadExecutor();
        final int NUM = 5;
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignore) {
                }
            }
        };
        for (int i = 0; i < NUM; i++) {
            es.submit(task);
        }
        es.shutdown();
        // 50 * 2 msec, all tasks not terminated yet.
        assertFalse(es.awaitTermination(105, TimeUnit.MILLISECONDS));
        // 50 * 4 msec, all tasks not terminated yet.
        assertFalse(es.awaitTermination(105, TimeUnit.MILLISECONDS));
        // 50 * 6 msec, all tasks terminated.
        assertTrue(es.awaitTermination(105, TimeUnit.MILLISECONDS));
        assertTrue(es.isShutdown());
        assertTrue(es.isTerminated());
    }
}
