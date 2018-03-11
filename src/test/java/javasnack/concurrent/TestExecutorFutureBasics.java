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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.testng.annotations.Test;

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
        assertEquals(rl, 100L);
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
        assertEquals(rl, 100L);
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

    @Test(expectedExceptions = ExecutionException.class)
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
        try {
            f.get();
        } catch (ExecutionException expected) {
            Throwable cause = expected.getCause();
            assertEquals(cause.getClass(), NullPointerException.class);
            throw expected;
        }
        es.shutdown();
    }

    @Test(expectedExceptions = ExecutionException.class)
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
        try {
            f.get();
        } catch (ExecutionException expected) {
            Throwable cause = expected.getCause();
            assertEquals(cause.getClass(), AssertionError.class);
            throw expected;
        }
        es.shutdown();
    }
}
