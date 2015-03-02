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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureDemos implements Runnable {

    class MyCallable implements Callable<String> {
        @Override
        public String call() throws Exception {
            try {
                Thread.sleep(1000); // long task;
            } catch (InterruptedException ignore) {
                return "interrupted";
            }
            return "hello";
        }
    }

    @Override
    public void run() {
        try {
            FutureDemo1();
        } catch (InterruptedException | ExecutionException | TimeoutException ignored) {
        }
    }

    void FutureDemo1() throws InterruptedException, ExecutionException, TimeoutException {
        System.out.println("-->FutureDemo1");
        ExecutorService es = Executors.newSingleThreadExecutor();
        final int NUM = 6;
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < NUM; i++) {
            futures.add(es.submit(new MyCallable()));
        }
        Thread.sleep(2200);
        futures.get(4).cancel(true); // manually cancels future[4] task.
        es.shutdownNow();
        futures.get(5).cancel(true); // manually cancels future[5] task after shutdownNow().
        System.out.println("future[0].get()=" + futures.get(0).get(1, TimeUnit.SECONDS));
        System.out.println("future[1].get()=" + futures.get(1).get(1, TimeUnit.SECONDS));
        System.out.println("future[2].get()=" + futures.get(2).get(1, TimeUnit.SECONDS));
        try {
            System.out.println("future[3].get()=" + futures.get(3).get(1, TimeUnit.SECONDS));
        } catch (TimeoutException expected) {
            System.out.println("future[3].get() throws TimeoutException, expected");
        }
        try {
            System.out.println("future[4].get()=" + futures.get(4).get(1, TimeUnit.SECONDS));
        } catch (CancellationException expected) {
            System.out.println("future[4].get() throws CancellationException, expected");
        }
        try {
            System.out.println("future[5].get()=" + futures.get(5).get(1, TimeUnit.SECONDS));
        } catch (CancellationException expected) {
            System.out.println("future[5].get() throws CancellationException, expected");
        }
        System.out.println("futures dump:");
        for (Future<String> f : futures) {
            System.out.println("canceled=" + f.isCancelled());
            System.out.println("done=" + f.isDone());
        }
        System.out.println("<--FutureDemo1");
    }

}
