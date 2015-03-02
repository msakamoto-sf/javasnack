/*
 * Copyright 2015 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
package javasnack.snacks.concurrent;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadExecutorDemos implements Runnable {

    @Override
    public void run() {
        try {
            fixedThreadPoolDemo1();
            fixedThreadPoolDemoWithCustomThreadFactory();
            shutdownDemo();
            shutdownNowDemo();
            awaitTerminationDemo();
        } catch (InterruptedException ignored) {
        }
    }

    void fixedThreadPoolDemo1() throws InterruptedException {
        System.out.println("-->fixedThreadPoolDemo1");
        ExecutorService es = Executors.newFixedThreadPool(2);
        final int NUM = 5;
        CountDownLatch latch = new CountDownLatch(NUM);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                String tn = Thread.currentThread().getName();
                System.out.println("thread name=[" + tn + "]");
                try {
                    Thread.sleep(1000);
                    System.out.println(tn + " awaken, task end.");
                } catch (InterruptedException ignore) {
                }
                latch.countDown();
            }
        };
        for (int i = 0; i < NUM; i++) {
            es.submit(task);
        }
        System.out.println("ExecutorService.isTerminated=[" + es.isTerminated() + "]");
        System.out.println("ExecutorService.isShutdown=[" + es.isShutdown() + "]");
        latch.await();
        System.out.println("all task end.");
        System.out.println("ExecutorService.isTerminated=[" + es.isTerminated() + "]");
        System.out.println("ExecutorService.isShutdown=[" + es.isShutdown() + "]");
        es.shutdown(); // REQUIRED.
        System.out.println("shutdown().");
        System.out.println("ExecutorService.isTerminated=[" + es.isTerminated() + "]");
        System.out.println("ExecutorService.isShutdown=[" + es.isShutdown() + "]");
        System.out.println("<--fixedThreadPoolDemo1");
    }

    void fixedThreadPoolDemoWithCustomThreadFactory() throws InterruptedException {
        System.out.println("-->fixedThreadPoolDemoWithCustomThreadFactory");
        ThreadFactory tf = new ThreadFactory() {
            int count = 1;

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "MyThreadNo." + count);
                count++;
                return t;
            }
        };
        ExecutorService es = Executors.newFixedThreadPool(3, tf);
        final int NUM = 7;
        CountDownLatch latch = new CountDownLatch(NUM);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                String tn = Thread.currentThread().getName();
                System.out.println("thread name=[" + tn + "]");
                try {
                    Thread.sleep(1000);
                    System.out.println(tn + " awaken, task end.");
                } catch (InterruptedException ignore) {
                }
                latch.countDown();
            }
        };
        for (int i = 0; i < NUM; i++) {
            es.submit(task);
        }
        System.out.println("ExecutorService.isTerminated=[" + es.isTerminated() + "]");
        System.out.println("ExecutorService.isShutdown=[" + es.isShutdown() + "]");
        latch.await();
        System.out.println("all task end.");
        System.out.println("ExecutorService.isTerminated=[" + es.isTerminated() + "]");
        System.out.println("ExecutorService.isShutdown=[" + es.isShutdown() + "]");
        es.shutdown(); // REQUIRED.
        System.out.println("shutdown().");
        System.out.println("ExecutorService.isTerminated=[" + es.isTerminated() + "]");
        System.out.println("ExecutorService.isShutdown=[" + es.isShutdown() + "]");
        System.out.println("<--fixedThreadPoolDemoWithCustomThreadFactory");
    }

    /**
     * shutdown() rejects new task submit(), and wait for all task gone.
     * 
     * @throws InterruptedException
     */
    void shutdownDemo() throws InterruptedException {
        System.out.println("-->shutdownDemo");
        ExecutorService es = Executors.newSingleThreadExecutor();
        final int NUM = 5;
        CountDownLatch latch = new CountDownLatch(NUM);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                String tn = Thread.currentThread().getName();
                System.out.println("thread name=[" + tn + "]");
                try {
                    Thread.sleep(1000);
                    System.out.println(tn + " awaken, task end.");
                } catch (InterruptedException ignore) {
                }
                latch.countDown();
            }
        };
        for (int i = 0; i < NUM; i++) {
            es.submit(task);
        }
        es.shutdown();
        try {
            es.submit(task);
        } catch (RejectedExecutionException expected) {
            System.out.println("already called shutdown(), new submit() was rejected, expected behaviour.");
        }
        // all task is done, so latch.await() returns :)
        latch.await();
        System.out.println("<--shutdownDemo");
    }

    class shutdownNowDemoTask implements Runnable {
        final AtomicInteger counts;
        final CountDownLatch latch;

        public shutdownNowDemoTask(AtomicInteger counts, CountDownLatch latch) {
            this.counts = counts;
            this.latch = latch;
        }

        @Override
        public void run() {
            String tn = Thread.currentThread().getName();
            System.out.println("thread name=[" + tn + "]");
            try {
                Thread.sleep(2000);
                System.out.println(tn + " awaken, task end.");
                this.counts.addAndGet(1);
            } catch (InterruptedException expected) {
                System.out.println("expected InterruptedException");
            }
            this.latch.countDown();
        }
    }

    /**
     * shutdownNow() cancels waiting tasks AND current running task.
     * 
     * @throws InterruptedException
     */
    void shutdownNowDemo() throws InterruptedException {
        System.out.println("-->shutdownNowDemo");
        ExecutorService es = Executors.newSingleThreadExecutor();
        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger counts = new AtomicInteger(0);
        for (int i = 0; i < 5; i++) {
            es.submit(new shutdownNowDemoTask(counts, latch));
        }
        latch.await(); // 2 * 2000 = wait 4 seconds.
        Thread.sleep(500); // no.3 task has begun.
        List<Runnable> remains = es.shutdownNow(); // no.3 task has begun...
        // CANCELED waiting tasks AND current running task.
        // see counts.get() and remains.size() value carefully. :P

        // "current" count-up task would be canceled, so we'll got "2".
        System.out.println("current counts = [" + counts.get() + "]");
        // we'll got 2 tasks canceled.
        System.out.println("canceled [" + remains.size() + "] tasks.");

        System.out.println("<--shutdownNowDemo");
    }

    void awaitTerminationDemo() throws InterruptedException {
        System.out.println("-->awaitTerminationDemo");
        ExecutorService es = Executors.newSingleThreadExecutor();
        final int NUM = 5;
        Runnable task = new Runnable() {
            @Override
            public void run() {
                String tn = Thread.currentThread().getName();
                System.out.println("thread name=[" + tn + "]");
                try {
                    Thread.sleep(1000);
                    System.out.println(tn + " awaken, task end.");
                } catch (InterruptedException ignore) {
                }
            }
        };
        for (int i = 0; i < NUM; i++) {
            es.submit(task);
        }
        es.shutdown();
        boolean terminated = es.awaitTermination(2, TimeUnit.SECONDS);
        System.out.println("2seconds, not terminated all tasks yet, terminated = " + terminated);
        terminated = es.awaitTermination(2, TimeUnit.SECONDS);
        System.out.println("4seconds, not terminated all tasks yet, terminated = " + terminated);
        terminated = es.awaitTermination(2, TimeUnit.SECONDS);
        System.out.println("6seconds, terminated all tasks, terminated = " + terminated);
        System.out.println("<--awaitTerminationDemo");
    }

}
