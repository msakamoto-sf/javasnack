package javasnack.ojcp.se8gold.chapter10;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

public class Test10ExecutorServiceTips {
    // OJCP 試験向けの練習問題などから拾ってきた、試験問題特有のサンプルコード

    static AtomicInteger val1 = new AtomicInteger();
    static int val2 = 0;

    @Test
    public void test500msConsistency() throws InterruptedException {
        /* 教科書問題から。
         * 500ms sleep している間に、100個の小規模タスクはすべて計算されるものと仮定している。
         */
        final ExecutorService service = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 100; i++) {
            service.execute(() -> {
                val1.getAndIncrement();
                val2++;
            });
        }
        Thread.sleep(500);
        assertThat(val1.get()).isEqualTo(100);
        assertThat(val2).isEqualTo(100);
    }

    @Test
    public void testOmittingExecutorServiceShutdown() {
        System.out.println("--- see console log ---");
        final ScheduledExecutorService service = Executors.newScheduledThreadPool(5);
        DoubleStream.of(10.9, 2.3).forEach(s -> service.submit(() -> {
            System.out.print(s + " ");
        }));
        service.execute(() -> System.out.print(" end "));
        /* ScheduledExecutorService.schedule() : Callable/Runnable 両方取れる。
         * ScheduledExecutorService.scheduleAtFixedRate() : Runnable のみ。
         * ScheduledExecutorService.scheduleWithFixedDelay() : Runnable のみ。
         */

        /* 教科書問題では↓が無かったので、「プログラムが終了しない」の選択肢を選ぶのが正解になってた。
         * 実際コメントアウトしてみると、IDE上でjunit動かす分にはちゃんとJVMが終了する。
         * (Java11 + Eclipse でやってるせい？)
         * もしかしたら mvn などで実行すると違うのかもしれない。
         */
        service.shutdown();
    }

    @Test
    public void testExecutorsDemo() throws InterruptedException, ExecutionException {
        final StringBuilder sb = new StringBuilder();
        final Runnable run0 = () -> {
            sb.append("xx");
        };
        final Callable<String> call0 = () -> {
            sb.append("yy");
            return "zz";
        };
        /* Executors からの主要な ExecutorService factory method
         * - newSingleThreadExecutor() : single threaded
         * - newFixedThreadPool() : fixed number multi threaded
         * - newCachedThreadPool() : cached multi threaded
         * - newScheduledThreadPool() : scheduled thread pool
         * - newSingleThreadScheduledExecutor() : scheduled single thread
         * - newWorkStealingPool()() : worker-stealing thread pool
         */
        ExecutorService es = Executors.newSingleThreadExecutor();
        Executor e = es;
        // Executor.execute() の戻り値は void
        e.execute(run0);
        // Executor.execute() は Runnable しか受け付けない。
        //e.execute(call0);

        // ExecutorService.submit() は Callable/Runnable 両方取れて、いずれも Future を返す。
        Future<?> f0 = es.submit(run0);
        Future<String> f1 = es.submit(call0);

        Object o0 = f0.get(); // wildcard を使ってるため、Future<?>.get() は Object型になる。
        assertThat(o0).isNull();
        assertThat(f1.get()).isEqualTo("zz");
        assertThat(sb.toString()).isEqualTo("xxxxyy");
        es.shutdown();
    }

    @Test
    public void testStreamAndExecutorCombinationDemo() {
        // 教科書の練習問題から、 ExecutorService と stream の組み合わせ例
        final StringBuilder sb = new StringBuilder();
        final IntFunction<Integer> f0 = x -> x * 2;
        final Consumer<Future<?>> consumer0 = f -> {
            try {
                sb.append(f.get() + " ");
            } catch (InterruptedException | ExecutionException ignore) {
            }
        };

        final ExecutorService es = Executors.newSingleThreadExecutor();
        final List<Future<?>> futures = new ArrayList<>();
        // ここがちょっと分かりづらいが...
        IntStream.range(0, 10).forEach(i -> futures.add(es.submit(() -> f0.apply(i))));
        // 落ち着いて読み解けば、以下の処理をしていることになる。
        /*
        IntStream.range(0, 10).forEach((int i) -> {
            final Future<Integer> fx = es.submit(() -> {
                return f0.apply(i);
            });
            futures.add(fx);
        });
        */
        futures.stream().forEach(consumer0);
        es.shutdown();
        assertThat(sb.toString()).isEqualTo("0 2 4 6 8 10 12 14 16 18 ");
    }
}
