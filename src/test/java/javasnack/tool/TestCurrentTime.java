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
package javasnack.tool;

import static org.testng.Assert.*;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.testng.annotations.Test;

public class TestCurrentTime {
    @Test
    public void defaultCurrentTimeProviderIsSameToSystem() {
        long systemNow1 = System.currentTimeMillis();
        long systemNow2 = CurrentTime.millis();
        long diff = systemNow2 - systemNow1;
        // differential must be lesser than 10ms.
        assertTrue(diff < 10);
    }

    @Test
    public void mockCurrentTime() {
        ICurrentTimeProvider mock = new ICurrentTimeProvider() {
            @Override
            public long millis() {
                return 1L;
            }
        };
        CurrentTime.mock(mock);
        assertEquals(CurrentTime.millis(), mock.millis());
        CurrentTime.unmock();
    }

    ICurrentTimeProvider createMock(final long r) {
        return new ICurrentTimeProvider() {
            @Override
            public long millis() {
                return r;
            }
        };
    }

    static final int NUM_OF_THREAD = 20;

    class MockCaller implements Callable<Long> {
        final ICurrentTimeProvider provider;
        final CountDownLatch gate;

        public MockCaller(ICurrentTimeProvider provider, CountDownLatch gate) {
            this.provider = provider;
            this.gate = gate;
        }

        @Override
        public Long call() throws Exception {
            gate.await();
            return this.provider.millis();
        }
    }

    @Test
    public void mockConcurrent() throws InterruptedException,
            ExecutionException {
        ICurrentTimeProvider[] providers = new ICurrentTimeProvider[NUM_OF_THREAD];
        Future<Long>[] actual = new Future[NUM_OF_THREAD];
        CountDownLatch startGate = new CountDownLatch(1);
        ExecutorService es = Executors.newFixedThreadPool(NUM_OF_THREAD);
        for (int i = 0; i < NUM_OF_THREAD; i++) {
            providers[i] = createMock(i);
            actual[i] = es.submit(new MockCaller(providers[i], startGate));
        }
        startGate.countDown();
        es.awaitTermination(100, TimeUnit.MILLISECONDS);
        es.shutdown();
        for (int i = 0; i < NUM_OF_THREAD; i++) {
            assertEquals(actual[i].get().longValue(), providers[i].millis());
        }
    }
}
