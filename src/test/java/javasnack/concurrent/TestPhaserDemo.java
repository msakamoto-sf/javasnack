package javasnack.concurrent;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class TestPhaserDemo {

    private static class WaitingTask implements Runnable {
        final Phaser phaser;
        final AtomicInteger counter;

        WaitingTask(final Phaser phaser, final AtomicInteger counter) {
            this.phaser = phaser;
            this.counter = counter;
        }

        void sleep() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }

        void printPhase(final int phase) {
            System.out.println("[" + Thread.currentThread().getName() + "], arrived phase#" + phase);
        }

        @Override
        public void run() {
            this.phaser.register();
            final int phase1 = this.phaser.arriveAndAwaitAdvance();
            printPhase(phase1);
            this.counter.addAndGet(phase1);

            sleep();

            final int phase2 = this.phaser.arriveAndAwaitAdvance();
            printPhase(phase2);
            this.counter.addAndGet(phase2);

            sleep();

            final int phase3 = this.phaser.arriveAndDeregister();
            printPhase(phase3);
            this.counter.addAndGet(phase3);
        }
    }

    /* 2020-02: 
     * Phaserの理解が追いつかず、思ったようなシナリオで動作させることができなかった。
     * メインと子スレッドでフェーズごとに待ち合わせさせたかったのだけれど、うまく動かない。
     * 一旦このテストケースは無効化しておく。
     */
    @Test
    @Disabled
    public void testPhaserBasicUsageDemo() throws InterruptedException {
        final int NUM = 2;
        final int waitms = 50;
        final ExecutorService es = Executors.newFixedThreadPool(NUM);

        final Phaser phaser = new Phaser(1); // register myself
        assertThat(phaser.getArrivedParties()).isEqualTo(0);
        assertThat(phaser.getPhase()).isEqualTo(0);
        final AtomicInteger counter = new AtomicInteger(0);
        es.submit(new WaitingTask(phaser, counter));
        es.submit(new WaitingTask(phaser, counter));
        Thread.sleep(waitms);

        assertThat(phaser.getArrivedParties()).isEqualTo(2);
        assertThat(phaser.getPhase()).isEqualTo(0);
        assertThat(phaser.isTerminated()).isFalse();

        assertThat(phaser.arriveAndAwaitAdvance()).isEqualTo(1);
        Thread.sleep(waitms);
        assertThat(counter.get()).isEqualTo(2);
        assertThat(phaser.getArrivedParties()).isEqualTo(0);
        assertThat(phaser.getPhase()).isEqualTo(1);
        assertThat(phaser.isTerminated()).isFalse();

        assertThat(phaser.arriveAndAwaitAdvance()).isEqualTo(2);
        Thread.sleep(waitms);
        assertThat(counter.get()).isEqualTo(6); // 2 + 2 + 2
        assertThat(phaser.getArrivedParties()).isEqualTo(0);
        assertThat(phaser.getPhase()).isEqualTo(2);
        assertThat(phaser.isTerminated()).isFalse();

        assertThat(phaser.arriveAndAwaitAdvance()).isEqualTo(3);
        Thread.sleep(waitms);
        assertThat(counter.get()).isEqualTo(12); // 6 + 3 + 3
        assertThat(phaser.getArrivedParties()).isEqualTo(0);
        assertThat(phaser.getPhase()).isEqualTo(3);
        assertThat(phaser.isTerminated()).isFalse();

        assertThat(phaser.arriveAndDeregister()).isEqualTo(3);
        es.shutdown();
        es.awaitTermination(100, TimeUnit.MILLISECONDS);
        assertThat(phaser.isTerminated()).isTrue();
    }
}
