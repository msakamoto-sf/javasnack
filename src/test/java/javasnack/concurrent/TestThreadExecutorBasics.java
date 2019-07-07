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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import javasnack.tool.BlackholeTcpServer;

@TestInstance(Lifecycle.PER_CLASS)
public class TestThreadExecutorBasics {

    BlackholeTcpServer blackholeTcpServer = null;
    int blackholeTcpServerPort = 0;

    @BeforeAll
    public void beforeAll() throws IOException {
        blackholeTcpServer = new BlackholeTcpServer();
        this.blackholeTcpServerPort = blackholeTcpServer.start();
    }

    @AfterAll
    public void afterAll() {
        if (Objects.nonNull(this.blackholeTcpServer)) {
            this.blackholeTcpServer.stop();
        }
    }

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
        assertEquals(3, tf.count, "number of created threads should be 3.");
        for (int i = 0; i < NUM; i++) {
            assertTrue(futures.get(i).get().startsWith("MyThreadNo."));
        }
    }

    static class WaitableRunner implements Runnable {
        final CountDownLatch wait;
        final CountDownLatch done;

        WaitableRunner(final CountDownLatch wait, final CountDownLatch done) {
            this.wait = wait;
            this.done = done;
        }

        @Override
        public void run() {
            try {
                wait.await();
            } catch (InterruptedException ignore) {
            }
            done.countDown();
        }
    }

    @Test
    void testShudown() throws InterruptedException {
        final ExecutorService es = Executors.newSingleThreadExecutor();
        final int NUM = 5;
        final CountDownLatch done = new CountDownLatch(NUM);
        final CountDownLatch wait = new CountDownLatch(1);
        final Runnable task = new WaitableRunner(wait, done);
        for (int i = 0; i < NUM; i++) {
            es.submit(task);
        }
        // shutdown() rejects new task submit(), and wait for all task gone.
        es.shutdown();
        assertTrue(es.isShutdown());
        assertFalse(es.isTerminated());

        assertThrows(RejectedExecutionException.class, () -> {
            es.submit(task);
        });
        // -> already called shutdown(), new submit() was rejected, expected behaviour.

        wait.countDown();
        // -> awake all task
        done.await();
        // -> all task is done

        assertTrue(es.awaitTermination(60, TimeUnit.MILLISECONDS));
        assertTrue(es.isTerminated());
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
        assertEquals(2, counts.get());
        // we'll got 2 tasks canceled. (2 tasks done, 1 task has begun)
        assertEquals(2, remains.size());
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

    class BreakableTask implements Runnable {
        final long sleepms;
        final int loopcnt;
        volatile boolean isInterrupted = false;
        volatile boolean done = false;
        volatile boolean brk = false;

        public BreakableTask(long sleepms, int loopcnt) {
            this.sleepms = sleepms;
            this.loopcnt = loopcnt;
        }

        @Override
        public void run() {
            for (int i = 0; i < loopcnt; i++) {
                if (brk) {
                    break;
                }
                try {
                    Thread.sleep(this.sleepms);
                } catch (InterruptedException expected) {
                    isInterrupted = true;
                }
            }
            if (!brk) {
                this.done = true;
            }
        }
    }

    @Test
    void testGracefulShutdownExample() throws InterruptedException {
        ExecutorService es = Executors.newSingleThreadExecutor();
        final int NUM = 4;
        final BreakableTask[] tasks = new BreakableTask[NUM];
        for (int i = 0; i < NUM; i++) {
            tasks[i] = new BreakableTask(100, 2);
            es.submit(tasks[i]);
        }
        es.shutdown();
        assertFalse(es.awaitTermination(250, TimeUnit.MILLISECONDS));
        // [0] done, [1] in sleeping, [2], [3] not started.
        List<Runnable> l = es.shutdownNow();
        assertEquals(2, l.size());
        // [1] ignores interruption, [2], [3] are removed from task queue.
        assertTrue(es.awaitTermination(250, TimeUnit.MILLISECONDS));
        // [1] done, completely terminated :)

        assertTrue(tasks[0].done);
        assertTrue(tasks[1].done);
        assertFalse(tasks[2].done);
        assertFalse(tasks[3].done);

        assertFalse(tasks[0].isInterrupted);
        assertTrue(tasks[1].isInterrupted); // only [1] received interruption (but ignored)
        assertFalse(tasks[2].isInterrupted);
        assertFalse(tasks[3].isInterrupted);

        es = Executors.newSingleThreadExecutor();
        for (int i = 0; i < NUM; i++) {
            tasks[i] = new BreakableTask(20, 5);
            es.submit(tasks[i]);
        }
        es.shutdown();
        assertFalse(es.awaitTermination(40, TimeUnit.MILLISECONDS));
        // [0] in sleep + looping, [1], [2], [3] not started.
        assertEquals(3, es.shutdownNow().size());

        // send break signal :P
        for (BreakableTask t : tasks) {
            t.brk = true;
        }
        assertTrue(es.awaitTermination(40, TimeUnit.MILLISECONDS));

        assertFalse(tasks[0].done); // broken, not done.
        assertFalse(tasks[1].done);
        assertFalse(tasks[2].done);
        assertFalse(tasks[3].done);

        // assertFalse(tasks[0].isInterrupted); // DO NOT TEST. timing-base, sensitive tests.
        assertFalse(tasks[1].isInterrupted);
        assertFalse(tasks[2].isInterrupted);
        assertFalse(tasks[3].isInterrupted);
    }

    class BlockingIOTask implements Runnable {
        final int no;
        final int remotePort;
        volatile Socket clientSocket = null;
        volatile Thread currentThread = null;
        volatile boolean isInterrupted = false;
        volatile boolean done = false;

        public BlockingIOTask(final int no, final int remotePort) {
            this.no = no;
            this.remotePort = remotePort;
        }

        public void interruptThread() {
            if (Objects.nonNull(currentThread)) {
                currentThread.interrupt();
            }
        }

        public void closeSocket() {
            if (Objects.nonNull(clientSocket) && clientSocket.isConnected()) {
                try {
                    clientSocket.close();
                } catch (IOException ignore) {
                }
            }
        }

        @Override
        public void run() {
            currentThread = Thread.currentThread();
            InetSocketAddress connectTo = new InetSocketAddress("127.0.0.1", this.remotePort);
            clientSocket = new Socket();
            try {
                clientSocket.connect(connectTo);
                System.out.println("connected[" + no + "]");
                OutputStream out = clientSocket.getOutputStream();
                out.write(new byte[] { 0x00, 0x01, 0x02 });
                out.write(new byte[] { 0x03, 0x04, 0x05 });
                out.flush();
                InputStream in = clientSocket.getInputStream();
                System.out.println("read start[" + no + "]");
                in.read();
                System.out.println("read end[" + no + "]");
            } catch (IOException e) {
                assertTrue(e instanceof SocketException);
                assertEquals("Socket closed", e.getMessage());
            } finally {
                if (clientSocket.isConnected()) {
                    try {
                        clientSocket.close();
                    } catch (IOException ignore) {
                    }
                }
            }
            this.isInterrupted = currentThread.isInterrupted();
            this.done = true;
        }
    }

    @Test
    void testGracefulShutdownBlockingIOTaskDemo() throws InterruptedException {
        final int NUM = 4;
        final ExecutorService es = Executors.newFixedThreadPool(NUM);
        final BlockingIOTask[] tasks = new BlockingIOTask[NUM];
        for (int i = 0; i < NUM; i++) {
            tasks[i] = new BlockingIOTask(i, this.blackholeTcpServerPort);
            es.submit(tasks[i]);
        }
        es.shutdown();
        assertFalse(es.awaitTermination(50, TimeUnit.MILLISECONDS));
        List<Runnable> l = es.shutdownNow();
        assertEquals(0, l.size());

        // oops ... blocking i/o read-wait does not interrupted by shutdownNow(), so not terminated yet :( 
        assertFalse(es.awaitTermination(50, TimeUnit.MILLISECONDS));

        // call Thread#interrupt() from outside MANUALLY
        for (int i = 0; i < NUM; i++) {
            tasks[i].interruptThread();
        }

        // oops ... not terminated yet :P 
        assertFalse(es.awaitTermination(50, TimeUnit.MILLISECONDS));

        // call Socket#close from outside
        for (int i = 0; i < NUM; i++) {
            tasks[i].closeSocket();
        }
        // termination completed :)
        assertTrue(es.awaitTermination(50, TimeUnit.MILLISECONDS));

        assertTrue(tasks[0].done);
        assertTrue(tasks[1].done);
        assertTrue(tasks[2].done);
        assertTrue(tasks[3].done);
        assertTrue(tasks[0].isInterrupted);
        assertTrue(tasks[1].isInterrupted);
        assertTrue(tasks[2].isInterrupted);
        assertTrue(tasks[3].isInterrupted);
    }

}
