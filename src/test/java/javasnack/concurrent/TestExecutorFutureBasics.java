/*
 * Copyright 2014 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;

public class TestExecutorFutureBasics {

    @Test
    public void testFutureAndRunnableUsage() throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();
        Future<?> f = es.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignore) {
                }
            }
        });
        assertFalse(f.isCancelled());
        assertFalse(f.isDone());
        Object ro = f.get();
        assertTrue(Objects.isNull(ro));
        assertFalse(f.isCancelled());
        assertTrue(f.isDone());
        es.shutdown();
    }

    @Test
    public void testFutureAndCallableUsage() throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();
        Future<Long> f = es.submit(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignore) {
                }
                return 100L;
            }
        });
        assertFalse(f.isCancelled());
        assertFalse(f.isDone());
        long rl = f.get();
        assertEquals(100L, rl);
        assertFalse(f.isCancelled());
        assertTrue(f.isDone());
        es.shutdown();
    }

    @Test
    public void testFutureCancelUsage() throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newSingleThreadExecutor();
        class CallableDemo implements Callable<Long> {
            final long futureR;

            CallableDemo(final long futureR) {
                this.futureR = futureR;
            }

            @Override
            public Long call() throws Exception {
                long started = System.currentTimeMillis();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignore) {
                    long intr = System.currentTimeMillis();
                    System.out.println("CallableDemo interrupted, elappsed = " + (intr - started) + " msec.");
                }
                return this.futureR;
            }
        }

        Future<Long> f1 = es.submit(new CallableDemo(100L));
        assertFalse(f1.isCancelled());
        assertFalse(f1.isDone());
        Future<Long> f2 = es.submit(new CallableDemo(200L));
        assertFalse(f2.isCancelled());
        assertFalse(f2.isDone());
        Future<Long> f3 = es.submit(new CallableDemo(300L));
        assertFalse(f3.isCancelled());
        assertFalse(f3.isDone());

        long rl = f1.get();
        assertFalse(f1.cancel(true));
        assertEquals(100L, rl);
        assertFalse(f1.isCancelled());
        assertTrue(f1.isDone());

        Thread.sleep(30);
        assertTrue(f2.cancel(true));
        assertTrue(f2.isCancelled());
        assertTrue(f2.isDone());
        try {
            f2.get();
            fail("should not reach here");
        } catch (CancellationException expected) {
        }

        assertTrue(f3.cancel(false));
        assertTrue(f3.isCancelled());
        assertTrue(f3.isDone());
        try {
            f3.get();
            fail("should not reach here");
        } catch (CancellationException expected) {
        }

        es.shutdown();
    }

    @Test
    public void testUncheckedExceptionCaughtThroughExecutionException()
            throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();
        Future<?> f = es.submit(new Runnable() {
            @SuppressWarnings("null")
            @Override
            public void run() {
                String s = "hello";
                s = null;
                s.length();
            }
        });
        final ExecutionException expectedException = assertThrows(ExecutionException.class, () -> {
            f.get();
        });
        es.shutdown();
        assertEquals(NullPointerException.class, expectedException.getCause().getClass());
    }

    @Test
    public void testAssertionErrorCaughtThroughExecutionException() throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();
        Future<?> f = es.submit(new Runnable() {
            @Override
            public void run() {
                int a = 10;
                int b = 20;
                assert a == b;
            }
        });
        final ExecutionException expectedException = assertThrows(ExecutionException.class, () -> {
            f.get();
        });
        es.shutdown();
        assertEquals(AssertionError.class, expectedException.getCause().getClass());
    }

    @Test
    public void testSomeCombination() throws InterruptedException, ExecutionException, TimeoutException {
        class MyCallable implements Callable<String> {
            final CountDownLatch blocker;
            final CountDownLatch doneNotifier;

            MyCallable(final CountDownLatch blocker, final CountDownLatch doneNotifier) {
                this.blocker = blocker;
                this.doneNotifier = doneNotifier;
            }

            @Override
            public String call() throws Exception {
                try {
                    blocker.await(); // long task;
                } catch (InterruptedException ignore) {
                    return "interrupted";
                }
                doneNotifier.countDown();
                return "hello";
            }
        }

        final ExecutorService es = Executors.newSingleThreadExecutor();
        final int NUM = 6;
        final List<CountDownLatch> blockers = new ArrayList<>();
        final List<CountDownLatch> doneNotifiers = new ArrayList<>();
        final List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < NUM; i++) {
            final CountDownLatch blocker = new CountDownLatch(1);
            blockers.add(blocker);
            final CountDownLatch doneNotifier = new CountDownLatch(1);
            doneNotifiers.add(doneNotifier);
            futures.add(es.submit(new MyCallable(blocker, doneNotifier)));
        }

        blockers.get(0).countDown();
        doneNotifiers.get(0).await();
        // -> done future[0] task

        blockers.get(1).countDown();
        doneNotifiers.get(1).await();
        // -> done future[1] task

        futures.get(4).cancel(true);
        // -> manually cancels future[4] task.

        es.shutdownNow();

        futures.get(5).cancel(true); // manually cancels future[5] task after shutdownNow().

        assertEquals("hello", futures.get(0).get(1, TimeUnit.SECONDS));
        assertEquals("hello", futures.get(1).get(1, TimeUnit.SECONDS));
        try {
            final String f2result = futures.get(2).get(1, TimeUnit.SECONDS);
            // if no TimeoutException
            assertEquals("interrupted", f2result);
        } catch (TimeoutException maybeHappens) {
            // in some situation (timing or execution environment), timeout happens.
            // THIS IS EXPECTED BEHAVIOUR.
        }
        assertThrows(TimeoutException.class, () -> {
            futures.get(3).get(1, TimeUnit.SECONDS);
        });
        assertThrows(CancellationException.class, () -> {
            futures.get(4).get(1, TimeUnit.SECONDS);
        });
        assertThrows(CancellationException.class, () -> {
            futures.get(5).get(1, TimeUnit.SECONDS);
        });

        assertFalse(futures.get(0).isCancelled());
        assertTrue(futures.get(0).isDone());

        assertFalse(futures.get(1).isCancelled());
        assertTrue(futures.get(1).isDone());

        assertFalse(futures.get(2).isCancelled());
        assertTrue(futures.get(2).isDone());

        assertFalse(futures.get(3).isCancelled());
        assertFalse(futures.get(3).isDone());

        assertTrue(futures.get(4).isCancelled());
        assertTrue(futures.get(4).isDone());

        assertTrue(futures.get(5).isCancelled());
        assertTrue(futures.get(5).isDone());
    }
}
