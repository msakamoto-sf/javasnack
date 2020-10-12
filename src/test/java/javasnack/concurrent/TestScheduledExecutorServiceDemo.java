package javasnack.concurrent;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

public class TestScheduledExecutorServiceDemo {
    @Test
    public void testDelayedTaskDemo() throws InterruptedException, ExecutionException {
        final ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
        final List<Long> millis = new ArrayList<>();

        final long startedAt = System.currentTimeMillis();

        ScheduledFuture<?> f1 = es.schedule(() -> {
            millis.add(System.currentTimeMillis());
        }, 1, TimeUnit.SECONDS);
        f1.get();
        final long elapsed1 = millis.get(0) - startedAt;

        ScheduledFuture<Long> f2 = es.schedule(() -> System.currentTimeMillis(), 2, TimeUnit.SECONDS);
        final long millis2 = f2.get();
        final long elapsed2 = millis2 - startedAt;

        es.shutdown();

        assertThat(elapsed1 >= 1_000).isTrue();
        assertThat(elapsed2 >= 3_000).isTrue();
    }

    @Test
    public void testRepeatAtFixedRateDemo() throws InterruptedException, ExecutionException {
        final ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
        final List<Long> millis = new ArrayList<>();

        millis.add(System.currentTimeMillis());

        es.scheduleAtFixedRate(() -> {
            millis.add(System.currentTimeMillis());
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
            millis.add(System.currentTimeMillis());
        }, 200, 300, TimeUnit.MILLISECONDS);

        // 最低2回 (init-delay: 200ms -> 300ms(100ms sleep + 200ms wait) * 2 ) 動くまで待つ
        Thread.sleep(1_000);

        es.shutdown();

        // init-delay
        long elapsed = millis.get(1) - millis.get(0);
        assertThat(elapsed >= 200);
        // 1st task unit: 100ms sleep
        elapsed = millis.get(2) - millis.get(1);
        assertThat(elapsed >= 100);
        // 1st task unit: 300ms(100ms sleep + 200ms wait)
        elapsed = millis.get(3) - millis.get(1);
        assertThat(elapsed >= 300);
        // 2nd task unit: 100ms sleep
        elapsed = millis.get(4) - millis.get(3);
        assertThat(elapsed >= 100);
        // 2nd task unit: 300ms(100ms sleep + 200ms wait)
        elapsed = millis.get(5) - millis.get(3);
        assertThat(elapsed >= 300);
    }

    @Test
    public void testRepeatWithFixedDelayDemo() throws InterruptedException, ExecutionException {
        final ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
        final List<Long> millis = new ArrayList<>();

        millis.add(System.currentTimeMillis());

        es.scheduleWithFixedDelay(() -> {
            millis.add(System.currentTimeMillis());
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
            millis.add(System.currentTimeMillis());
        }, 200, 300, TimeUnit.MILLISECONDS);

        // 最低2回 (init-delay: 200ms -> 400ms(100ms sleep + 300ms wait) * 2 ) 動くまで待つ
        Thread.sleep(2_000);

        es.shutdown();

        // init-delay : 200ms
        long elapsed = millis.get(1) - millis.get(0);
        assertThat(elapsed >= 200);
        // 1st task unit: 100ms sleep
        elapsed = millis.get(2) - millis.get(1);
        assertThat(elapsed >= 100);
        // 2nd task delay : 300ms
        elapsed = millis.get(3) - millis.get(2);
        assertThat(elapsed >= 300);
        // 2nd task unit: 100ms sleep
        elapsed = millis.get(4) - millis.get(3);
        assertThat(elapsed >= 100);
        // 3rd task delay : 300ms
        elapsed = millis.get(5) - millis.get(4);
        assertThat(elapsed >= 300);
    }
}
