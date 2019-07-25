/*
 * Copyright 2016 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/* Examples for CERT Oracle Coding Standard for Java, 
 * "TPS03-J Ensure that tasks executing in a thread pool do not fail silently"
 * 
 * see:
 * https://www.securecoding.cert.org/confluence/display/java/TPS03-J.+Ensure+that+tasks+executing+in+a+thread+pool+do+not+fail+silently
 * https://www.jpcert.or.jp/java-rules/tps03-j.html
 */
public class TestTaskAbortion {

    @Test
    public void testThreadPoolExecutorHook() throws InterruptedException {
        class AbortTask implements Runnable {
            public String label = "hello";

            @Override
            public void run() {
                throw new NullPointerException("test");
            }
        }

        class CustomThreadPoolExecutor extends ThreadPoolExecutor {
            public Throwable myT = null;
            public AbortTask caughtT = null;

            public CustomThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                    BlockingQueue<Runnable> workQueue) {
                super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
            }

            @Override
            public void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                caughtT = (AbortTask) r;
                myT = t;
            }
        }

        CustomThreadPoolExecutor pool = new CustomThreadPoolExecutor(10, 10, 10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(10));
        pool.execute(new AbortTask());
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignore) {
        }
        List<Runnable> remains = pool.shutdownNow();
        assertEquals(remains.size(), 0);

        if (null == pool.myT) {
            fail();
        } else {
            assertEquals(NullPointerException.class, pool.myT.getClass());
            assertEquals("test", pool.myT.getMessage());
            assertEquals(0, pool.myT.getSuppressed().length);
            assertEquals(null, pool.myT.getCause());
        }
        assertEquals("hello", pool.caughtT.label);
    }

    @Test
    public void testUncaughtExceptionHandler() throws InterruptedException {

        class MyExceptionHandler implements UncaughtExceptionHandler {
            public Set<String> caughtThrowables = new HashSet<>();

            @Override
            public void uncaughtException(Thread thread, Throwable t) {
                assert t.getClass() == NullPointerException.class;
                assert t.getSuppressed().length == 0;
                assert t.getCause() == null;
                synchronized (caughtThrowables) {
                    caughtThrowables.add(t.getMessage());
                }
            }
        }

        class MyThreadFactory implements ThreadFactory {
            final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
            final UncaughtExceptionHandler handler;

            public MyThreadFactory(UncaughtExceptionHandler handler) {
                this.handler = handler;
            }

            @Override
            public Thread newThread(Runnable run) {
                Thread thread = defaultFactory.newThread(run);
                thread.setUncaughtExceptionHandler(handler);
                return thread;
            }
        }

        class AbortTask implements Runnable {
            String name;

            public AbortTask(String name) {
                this.name = name;
            }

            @Override
            public void run() {
                throw new NullPointerException("test:" + this.name);
            }
        }

        MyExceptionHandler handler = new MyExceptionHandler();
        ExecutorService pool = Executors.newFixedThreadPool(10, new MyThreadFactory(handler));

        pool.execute(new AbortTask("task1"));
        pool.execute(new AbortTask("task2"));
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignore) {
        }
        List<Runnable> remains = pool.shutdownNow();
        assertEquals(0, remains.size());

        assertEquals(2, handler.caughtThrowables.size());
        assertTrue(handler.caughtThrowables.contains("test:task1"));
        assertTrue(handler.caughtThrowables.contains("test:task2"));
    }

    @Test
    public void testFutureExecutionException() {
        class AbortTask implements Runnable {
            String name;

            public AbortTask(String name) {
                this.name = name;
            }

            @Override
            public void run() {
                throw new NullPointerException("test:" + this.name);
            }
        }

        ExecutorService pool = Executors.newFixedThreadPool(10);
        Future<?> f1 = pool.submit(new AbortTask("task1"));
        Future<?> f2 = pool.submit(new AbortTask("task2"));
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignore) {
        }
        List<Runnable> remains = pool.shutdownNow();
        assertEquals(0, remains.size());

        try {
            f1.get();
        } catch (InterruptedException e) {
            fail("AbortTask does not reach here.");
        } catch (ExecutionException e) {
            e.printStackTrace();
            Throwable t = e.getCause();
            assertEquals(NullPointerException.class, t.getClass());
            assertEquals("test:task1", t.getMessage());
            assertEquals(0, t.getSuppressed().length);
            assertEquals(null, t.getCause());
        }

        try {
            f2.get();
        } catch (InterruptedException e) {
            fail("AbortTask does not reach here.");
        } catch (ExecutionException e) {
            e.printStackTrace();
            Throwable t = e.getCause();
            assertEquals(NullPointerException.class, t.getClass());
            assertEquals("test:task2", t.getMessage());
            assertEquals(0, t.getSuppressed().length);
            assertEquals(null, t.getCause());
        }
    }
}
