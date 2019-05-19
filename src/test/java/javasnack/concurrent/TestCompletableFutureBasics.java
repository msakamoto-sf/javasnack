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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

/**
 * sample test codes for CompletableFuture, CompletionStage
 *
 * @see https://docs.oracle.com/javase/jp/8/docs/api/java/util/concurrent/CompletionStage.html
 * @see https://docs.oracle.com/javase/jp/8/docs/api/java/util/concurrent/CompletableFuture.html
 * @see https://qiita.com/subaru44k/items/d98ad79d21abccedb20b
 * @see https://qiita.com/subaru44k/items/c55d9b9fc419f0d09c64
 */
public class TestCompletableFutureBasics {

    @Test
    public void testSimpleStep() throws InterruptedException, ExecutionException {
        AtomicInteger len = new AtomicInteger(0);
        Supplier<String> supplier0 = () -> "ABC";
        Function<String, Integer> fun0 = s -> s.length();
        Consumer<Integer> consumer0 = i -> len.set(i);
        CountDownLatch latch0 = new CountDownLatch(1);
        Runnable start = new Runnable() {
            @Override
            public void run() {
                len.incrementAndGet();
                latch0.countDown();
            }
        };
        CompletableFuture<Void> cf0 = CompletableFuture
                .supplyAsync(supplier0)
                .thenApply(fun0)
                .thenAccept(consumer0)
                .thenRun(start);
        latch0.await();
        assertNull(cf0.get());
        assertEquals(4, len.get());
        assertFalse(cf0.isCancelled());
        assertTrue(cf0.isDone());
    }

    @Test
    public void testSimpleAsyncStep() throws InterruptedException, ExecutionException {
        AtomicInteger len = new AtomicInteger(0);
        Supplier<String> supplier0 = () -> "ABC";
        Function<String, Integer> fun0 = s -> s.length();
        Consumer<Integer> consumer0 = i -> len.set(i);
        CountDownLatch latch0 = new CountDownLatch(1);
        Runnable start = new Runnable() {
            @Override
            public void run() {
                len.incrementAndGet();
                latch0.countDown();
            }
        };
        CompletableFuture<Void> cf0 = CompletableFuture
                .supplyAsync(supplier0)
                .thenApplyAsync(fun0)
                .thenAcceptAsync(consumer0)
                .thenRunAsync(start);
        latch0.await();
        assertNull(cf0.get());
        assertEquals(4, len.get());
        assertFalse(cf0.isCancelled());
        assertTrue(cf0.isDone());
    }

    @Test
    public void testAcceptEitherAsync() throws InterruptedException, ExecutionException {
        Supplier<String> supplier0 = () -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
            return "ABC";
        };
        Supplier<String> supplier1 = () -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignore) {
            }
            return "ABCDEFG";
        };
        CompletableFuture<String> cf0 = CompletableFuture.supplyAsync(supplier0);
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(supplier1);
        AtomicInteger len = new AtomicInteger(0);
        Consumer<String> consumer0 = s -> {
            len.set(s.length());
        };
        CompletableFuture<Void> cf2 = cf1.acceptEitherAsync(cf0, consumer0);
        assertNull(cf2.get());
        assertEquals(7, len.get());
        assertFalse(cf2.isCancelled());
        assertTrue(cf2.isDone());
    }

    @Test
    public void testApplyToEitherAsync() throws InterruptedException, ExecutionException {
        Supplier<String> supplier0 = () -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
            return "ABC";
        };
        Supplier<String> supplier1 = () -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignore) {
            }
            return "ABCDEFG";
        };
        CompletableFuture<String> cf0 = CompletableFuture.supplyAsync(supplier0);
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(supplier1);
        Function<String, Integer> fun0 = s -> s.length();
        CompletableFuture<Integer> cf2 = cf1.applyToEitherAsync(cf0, fun0);
        assertEquals(7, cf2.get().intValue());
        assertFalse(cf2.isCancelled());
        assertTrue(cf2.isDone());
    }

    @Test
    public void testAllOfSample() throws InterruptedException, ExecutionException {
        StringBuffer sb = new StringBuffer("");
        Supplier<String> supplier0 = () -> {
            try {
                Thread.sleep(25);
            } catch (InterruptedException ignore) {
            }
            return "Hello, ";
        };
        Supplier<String> supplier1 = () -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignore) {
            }
            return "my name is ";
        };
        Supplier<String> supplier2 = () -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
            return "bob.";
        };
        Consumer<String> consumer0 = s -> {
            sb.append(s);
        };
        CompletableFuture<Void> cf0 = CompletableFuture.allOf(
                CompletableFuture.supplyAsync(supplier0).thenAccept(consumer0),
                CompletableFuture.supplyAsync(supplier1).thenAccept(consumer0),
                CompletableFuture.supplyAsync(supplier2).thenAccept(consumer0));
        assertNull(cf0.get());
        assertEquals("Hello, my name is bob.", sb.toString());
        assertFalse(cf0.isCancelled());
        assertTrue(cf0.isDone());
    }

    @Test
    public void testGetAndException() throws InterruptedException {
        AtomicInteger count = new AtomicInteger(0);
        Runnable countUp = new Runnable() {
            @Override
            public void run() {
                if (count.get() > 3) {
                    throw new IllegalStateException("test");
                }
                count.incrementAndGet();
            }
        };
        CompletableFuture<Void> cf0 = CompletableFuture
                .runAsync(countUp)
                .thenRunAsync(countUp)
                .thenRunAsync(countUp)
                .thenRunAsync(countUp)
                .thenRunAsync(countUp);
        try {
            cf0.get();
            fail("not reach here");
        } catch (ExecutionException expected) {
            assertEquals(4, count.get());
            assertEquals("java.lang.IllegalStateException: test", expected.getMessage());
            assertEquals(IllegalStateException.class, expected.getCause().getClass());
            assertEquals("test", expected.getCause().getMessage());
            assertFalse(cf0.isCancelled());
            assertTrue(cf0.isDone());
        }
    }

    @Test
    public void testCancel() throws InterruptedException {
        AtomicInteger count = new AtomicInteger(0);
        CountDownLatch latch0 = new CountDownLatch(3);
        Runnable countUp = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(30);
                } catch (InterruptedException ignore) {
                }
                count.incrementAndGet();
                latch0.countDown();
            }
        };
        CompletableFuture<Void> cf0 = CompletableFuture
                .runAsync(countUp)
                .thenRunAsync(countUp)
                .thenRunAsync(countUp)
                .thenRunAsync(countUp)
                .thenRunAsync(countUp);
        latch0.await();
        assertTrue(cf0.cancel(false));
        assertTrue(cf0.isCancelled());
        assertTrue(cf0.isDone());
    }
}
