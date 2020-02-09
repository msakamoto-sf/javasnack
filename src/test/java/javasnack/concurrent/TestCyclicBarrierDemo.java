package javasnack.concurrent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

public class TestCyclicBarrierDemo {

    private static class WaitingTask implements Runnable {
        final CyclicBarrier cb;
        final AtomicInteger counter;
        final long toms;

        WaitingTask(final CyclicBarrier cb, final AtomicInteger counter, final long toms) {
            this.cb = cb;
            this.counter = counter;
            this.toms = toms;
        }

        WaitingTask(final CyclicBarrier cb, final AtomicInteger counter) {
            this(cb, counter, 0);
        }

        @Override
        public void run() {
            try {
                if (toms > 0) {
                    this.cb.await(toms, TimeUnit.MILLISECONDS);
                } else {
                    this.cb.await();
                }
                this.counter.incrementAndGet();
            } catch (InterruptedException ignore) {
                this.counter.addAndGet(10);
            } catch (BrokenBarrierException ignore) {
                this.counter.addAndGet(100);
            } catch (TimeoutException e) {
                this.counter.addAndGet(1000);
            }
        }
    }

    @Test
    public void testCyclicBarrierBasicUsageDemo() throws InterruptedException, BrokenBarrierException {
        final int NUM = 5;
        final int waitms = 50;
        final ExecutorService es = Executors.newFixedThreadPool(NUM - 1);

        final CyclicBarrier cb = new CyclicBarrier(NUM);
        assertThat(cb.getNumberWaiting()).isEqualTo(0);
        assertThat(cb.getParties()).isEqualTo(NUM);
        final AtomicInteger counter = new AtomicInteger(0);

        es.submit(new WaitingTask(cb, counter));
        Thread.sleep(waitms);
        assertThat(cb.getNumberWaiting()).isEqualTo(1);
        assertThat(cb.getParties()).isEqualTo(NUM);

        es.submit(new WaitingTask(cb, counter));
        Thread.sleep(waitms);
        assertThat(cb.getNumberWaiting()).isEqualTo(2);
        assertThat(cb.getParties()).isEqualTo(NUM);

        es.submit(new WaitingTask(cb, counter));
        Thread.sleep(waitms);
        assertThat(cb.getNumberWaiting()).isEqualTo(3);
        assertThat(cb.getParties()).isEqualTo(NUM);

        es.submit(new WaitingTask(cb, counter));
        Thread.sleep(waitms);
        assertThat(cb.getNumberWaiting()).isEqualTo(4);
        assertThat(cb.getParties()).isEqualTo(NUM);

        assertThat(cb.await()).isEqualTo(0);
        es.shutdown();
        es.awaitTermination(100, TimeUnit.MILLISECONDS);
        assertThat(counter.get()).isEqualTo(NUM - 1);

        assertThat(cb.getNumberWaiting()).isEqualTo(0);
        assertThat(cb.getParties()).isEqualTo(NUM);
        assertThat(cb.isBroken()).isFalse();

        // 解放された後も繰り返し使える(cyclic)
        counter.set(0);
        final ExecutorService es2 = Executors.newFixedThreadPool(NUM - 1);
        es2.submit(new WaitingTask(cb, counter));
        Thread.sleep(waitms);
        assertThat(cb.getNumberWaiting()).isEqualTo(1);
        assertThat(cb.getParties()).isEqualTo(NUM);

        es2.submit(new WaitingTask(cb, counter));
        Thread.sleep(waitms);
        assertThat(cb.getNumberWaiting()).isEqualTo(2);
        assertThat(cb.getParties()).isEqualTo(NUM);

        es2.submit(new WaitingTask(cb, counter));
        Thread.sleep(waitms);
        assertThat(cb.getNumberWaiting()).isEqualTo(3);
        assertThat(cb.getParties()).isEqualTo(NUM);

        es2.submit(new WaitingTask(cb, counter));
        Thread.sleep(waitms);
        assertThat(cb.getNumberWaiting()).isEqualTo(4);
        assertThat(cb.getParties()).isEqualTo(NUM);

        assertThat(cb.await()).isEqualTo(0);
        es2.shutdown();
        es2.awaitTermination(100, TimeUnit.MILLISECONDS);
        assertThat(counter.get()).isEqualTo(NUM - 1);
    }

    @Test
    public void testCyclicBarrierResetDemo() throws InterruptedException {
        final int NUM = 2;
        final int waitms = 50;
        final ExecutorService es = Executors.newFixedThreadPool(NUM);

        final CyclicBarrier cb = new CyclicBarrier(NUM + 1);
        final AtomicInteger counter = new AtomicInteger(0);

        es.submit(new WaitingTask(cb, counter));
        es.submit(new WaitingTask(cb, counter));
        Thread.sleep(waitms);
        assertThat(cb.getNumberWaiting()).isEqualTo(NUM);
        assertThat(cb.getParties()).isEqualTo(NUM + 1);
        cb.reset();
        assertThat(cb.getNumberWaiting()).isEqualTo(0);
        assertThat(cb.getParties()).isEqualTo(NUM + 1);

        cb.reset();
        es.shutdown();
        es.awaitTermination(100, TimeUnit.MILLISECONDS);
        assertThat(counter.get()).isEqualTo(200); // incremented by BrokenBarrierException
    }

    @Test
    public void testCyclicBarrierActionDemo() throws InterruptedException, BrokenBarrierException {
        final int NUM = 2;
        final int waitms = 50;
        final ExecutorService es = Executors.newFixedThreadPool(NUM);

        final AtomicInteger counter = new AtomicInteger(0);
        final CyclicBarrier cb = new CyclicBarrier(NUM + 1, () -> {
            counter.addAndGet(10_000);
        });

        es.submit(new WaitingTask(cb, counter));
        es.submit(new WaitingTask(cb, counter));
        Thread.sleep(waitms);
        assertThat(cb.await()).isEqualTo(0);

        es.shutdown();
        es.awaitTermination(100, TimeUnit.MILLISECONDS);
        assertThat(counter.get()).isEqualTo(10_002);
    }

    @Test
    public void testCyclicBarrierTimeoutDemo() throws InterruptedException, BrokenBarrierException {
        final int NUM = 2;
        final ExecutorService es = Executors.newFixedThreadPool(NUM);

        final AtomicInteger counter = new AtomicInteger(0);
        final CyclicBarrier cb = new CyclicBarrier(NUM + 1, () -> {
            counter.addAndGet(10_000);
        });

        es.submit(new WaitingTask(cb, counter, 20)); // thread1 : wait 20ms
        es.submit(new WaitingTask(cb, counter)); // thread2 : wait no timeout
        Thread.sleep(50); // wait 50ms -> thread1 timeout
        assertThatThrownBy(() -> {
            cb.await();
        }).isInstanceOf(BrokenBarrierException.class);

        es.shutdown();
        es.awaitTermination(100, TimeUnit.MILLISECONDS);
        // thread1 : +1000 (via timeout exception)
        // thread2 : +100 (via broken barrier exception)
        assertThat(counter.get()).isEqualTo(1100);
    }

}
