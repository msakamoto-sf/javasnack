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

package javasnack.snacks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javasnack.RunnableSnack;

public class InfiniteLoop implements RunnableSnack {

    static class Looper implements Runnable {
        @Override
        public void run() {
            int cnt = 0;
            while (true) {
                try {
                    System.out.println("InifiniteLoop-Looper count " + cnt);
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
                cnt++;
            }
        }
    }

    @Override
    public void run(final String... args) {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        Future<?> f = pool.submit(new Looper());
        try {
            f.get();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
