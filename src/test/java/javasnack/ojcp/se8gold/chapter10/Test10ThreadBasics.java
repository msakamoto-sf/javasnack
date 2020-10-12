package javasnack.ojcp.se8gold.chapter10;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

public class Test10ThreadBasics {

    static class ThreadA extends Thread {
        final CountDownLatch done;
        final int count;

        ThreadA(CountDownLatch done, final int count) {
            this.done = done;
            this.count = count;
        }

        @Override
        public void run() {
            for (int i = 0; i < count; i++) {
                System.out.println("thread[" + Thread.currentThread().getName() + "], i=" + i);
                done.countDown();
            }
        }
    }

    @Test
    public void testThreadByExtendsThread() throws InterruptedException {
        final CountDownLatch l0 = new CountDownLatch(10);
        final Thread t1 = new ThreadA(l0, 5);
        final Thread t2 = new ThreadA(l0, 5);
        t1.start();
        t2.start();
        assertThat(l0.await(10, TimeUnit.SECONDS)).isTrue();

        // 一度 start() した Thread をもう一度 start() しようとすると IllegalThreadStateException
        assertThatThrownBy(() -> {
            t1.start();
        }).isInstanceOf(IllegalThreadStateException.class);
        assertThatThrownBy(() -> {
            t2.start();
        }).isInstanceOf(IllegalThreadStateException.class);
    }

    static class ThreadB implements Runnable {
        final CountDownLatch done;
        final int count;

        ThreadB(CountDownLatch done, final int count) {
            this.done = done;
            this.count = count;
        }

        @Override
        public void run() {
            for (int i = 0; i < count; i++) {
                System.out.println("thread[" + Thread.currentThread().getName() + "], i=" + i);
                done.countDown();
            }
        }
    }

    @Test
    public void testThreadByImplementsRunnable() throws InterruptedException {
        final CountDownLatch l0 = new CountDownLatch(10);
        final Thread t1 = new Thread(new ThreadB(l0, 5));
        final Thread t2 = new Thread(new ThreadB(l0, 5));
        t1.start();
        t2.start();
        assertThat(l0.await(10, TimeUnit.SECONDS)).isTrue();

        // 一度 start() した Thread をもう一度 start() しようとすると IllegalThreadStateException
        assertThatThrownBy(() -> {
            t1.start();
        }).isInstanceOf(IllegalThreadStateException.class);
        assertThatThrownBy(() -> {
            t2.start();
        }).isInstanceOf(IllegalThreadStateException.class);
    }

    @Test
    public void testThreadByLambdaExpression() throws InterruptedException {
        final CountDownLatch l0 = new CountDownLatch(10);
        final Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    System.out.println("thread[" + Thread.currentThread().getName() + "], i=" + i);
                    l0.countDown();
                }
            }
        });
        final Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("thread[" + Thread.currentThread().getName() + "], i=" + i);
                l0.countDown();
            }
        });
        t1.start();
        t2.start();
        assertThat(l0.await(10, TimeUnit.SECONDS)).isTrue();

        // 一度 start() した Thread をもう一度 start() しようとすると IllegalThreadStateException
        assertThatThrownBy(() -> {
            t1.start();
        }).isInstanceOf(IllegalThreadStateException.class);
        assertThatThrownBy(() -> {
            t2.start();
        }).isInstanceOf(IllegalThreadStateException.class);
    }

    @Test
    public void testThreadSleepThenJoin() throws InterruptedException {
        final CountDownLatch l0 = new CountDownLatch(1);
        final Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3_000);
                    l0.countDown();
                } catch (InterruptedException ignore) {
                }
            }
        });
        final long started = System.currentTimeMillis();
        t1.start();
        t1.join(10_000);
        final long ended = System.currentTimeMillis();
        assertThat((ended - started) >= 3_000).isTrue();
        assertThat(l0.await(10, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    public void testThreadSleepThenInterrupt() throws InterruptedException {
        final CountDownLatch l0 = new CountDownLatch(1);
        final Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3_000);
                } catch (InterruptedException ignore) {
                    l0.countDown();
                }
            }
        });
        final long started = System.currentTimeMillis();
        t1.start();
        Thread.sleep(1_500);
        t1.interrupt();
        t1.join(10_000);
        final long ended = System.currentTimeMillis();
        assertThat((ended - started) >= 1_500).isTrue();
        assertThat((ended - started) < 3_000).isTrue();
        assertThat(l0.await(10, TimeUnit.SECONDS)).isTrue();
    }

    static class BrokenCounter {
        int c = 0;

        int getCount() {
            return c;
        }

        void setCount(final int v) {
            c = v;
        }
    }

    @Test
    public void testThreadUnsafeCounterDemo1() throws InterruptedException {
        final BrokenCounter c0 = new BrokenCounter();
        final int loops = 10_000;
        final Thread incrementThread = new Thread(() -> {
            for (int i = 0; i < loops; i++) {
                final int x = c0.getCount();
                c0.setCount(x + 1);
            }
        });
        final Thread decrementThread = new Thread(() -> {
            for (int i = 0; i < loops; i++) {
                final int x = c0.getCount();
                c0.setCount(x - 1);
            }
        });
        incrementThread.start();
        decrementThread.start();
        incrementThread.join(10_000);
        decrementThread.join(10_000);
        assertThat(c0.getCount()).isNotEqualTo(0);
    }

    static class SynchronizedButNotAtomicCounter {
        int c = 0;

        synchronized int getCount() {
            return c;
        }

        synchronized void setCount(final int v) {
            c = v;
        }
    }

    @Test
    public void testThreadUnsafeCounterDemo2() throws InterruptedException {
        final SynchronizedButNotAtomicCounter c0 = new SynchronizedButNotAtomicCounter();
        final int loops = 10_000;
        final Thread incrementThread = new Thread(() -> {
            for (int i = 0; i < loops; i++) {
                final int x = c0.getCount();
                c0.setCount(x + 1);
            }
        });
        final Thread decrementThread = new Thread(() -> {
            for (int i = 0; i < loops; i++) {
                final int x = c0.getCount();
                c0.setCount(x - 1);
            }
        });
        incrementThread.start();
        decrementThread.start();
        incrementThread.join(10_000);
        decrementThread.join(10_000);
        assertThat(c0.getCount()).isNotEqualTo(0);
    }

    static class SynchronizedAndAtomicCounter {
        int c = 0;

        synchronized int getCount() {
            return c;
        }

        synchronized void increment() {
            c++;
        }

        synchronized void decrement() {
            c--;
        }
    }

    @Test
    public void testThreadSafeCounterDemo() throws InterruptedException {
        final SynchronizedAndAtomicCounter c0 = new SynchronizedAndAtomicCounter();
        final int loops = 10_000;
        final Thread incrementThread = new Thread(() -> {
            for (int i = 0; i < loops; i++) {
                c0.increment();
            }
        });
        final Thread decrementThread = new Thread(() -> {
            for (int i = 0; i < loops; i++) {
                c0.decrement();
            }
        });
        incrementThread.start();
        decrementThread.start();
        incrementThread.join(10_000);
        decrementThread.join(10_000);
        assertThat(c0.getCount()).isEqualTo(0);
    }

    static class WaitAndNotifyDemo {
        private int a = 0;
        private String b;

        synchronized void set() {
            while (a != 0) {
                try {
                    wait();
                } catch (InterruptedException ignore) {
                }
            }
            notify();
            a++;
            b = "data";
            System.out.println("set() a: " + a + " b: " + b);
        }

        synchronized void print() {
            while (b == null) {
                try {
                    wait();
                } catch (InterruptedException ignore) {
                }
            }
            notify();
            a--;
            b = null;
            System.out.println("print() a: " + a + " b: " + b);
        }
    }

    @Test
    public void testWaitAndNotifyDemo() throws InterruptedException {
        final WaitAndNotifyDemo o1 = new WaitAndNotifyDemo();
        final int loops = 10;
        final Thread t1 = new Thread(() -> {
            for (int i = 0; i < loops; i++) {
                o1.set();
            }
        });
        final Thread t2 = new Thread(() -> {
            for (int i = 0; i < loops; i++) {
                o1.print();
            }
        });
        t1.start();
        t2.start();
        t1.join(10_000);
        t2.join(10_000);
        // check console output.
    }

}
