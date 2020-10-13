package javasnack.ojcp.se8gold.chapter10;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

import org.junit.jupiter.api.Test;

/**
 * {@link javasnack.concurrent.TestForkJoinBasics} も参照。
 */
public class Test10ForkJoinPoolDemo {
    static class ExamRecursiveAction extends RecursiveAction {
        private static final long serialVersionUID = 1L;

        private Double[] nums;
        private int start;
        private int end;

        public ExamRecursiveAction(String name, Double[] nums,
                int start, int end) {
            System.out.println("name : " + name + " " +
                    start + " " + end);
            this.nums = nums;
            this.start = start;
            this.end = end;
        }

        protected void compute() {
            if (end - start <= 3) {
                for (int i = start; i < end; i++) {
                    nums[i] = Math.random() * 100;
                    System.out.println("nums[" + i + "] " + nums[i]);
                }
            } else {
                int middle = start + (end - start) / 2;
                System.out.println("start:" + start +
                        " middle:" + middle +
                        " end:" + end);
                invokeAll(new ExamRecursiveAction("f1", nums, start, middle),
                        new ExamRecursiveAction("f2", nums, middle, end));
            }
        }
    }

    @Test
    public void testRecursiveActionDemo() {
        System.out.println("--- see console log ---");
        final Double[] nums = new Double[20];
        final ForkJoinTask<?> task = new ExamRecursiveAction("main", nums, 0, 20);
        final ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(task);
    }

    static class ExamRecursiveTask extends RecursiveTask<Double> {
        private static final long serialVersionUID = 1L;

        private Double[] nums;
        private int start;
        private int end;

        public ExamRecursiveTask(String name, Double[] nums,
                int start, int end) {
            System.out.println("name : " + name + " " +
                    start + " " + end);
            this.nums = nums;
            this.start = start;
            this.end = end;
        }

        protected Double compute() {
            if (end - start <= 3) {
                double sum = 0.0;
                for (int i = start; i < end; i++) {
                    nums[i] = Math.random() * 100;
                    System.out.println("nums[" + i + "] " + nums[i]);
                    sum += nums[i];
                }
                return sum;
            } else {
                int middle = start + (end - start) / 2;
                System.out.println("start:" + start +
                        " middle:" + middle +
                        " end:" + end);
                ExamRecursiveTask task1 = new ExamRecursiveTask("f1", nums, start, middle);
                ExamRecursiveTask task2 = new ExamRecursiveTask("f2", nums, middle, end);
                task1.fork();
                Double sum1 = task2.compute();
                Double sum2 = task1.join();
                return sum1 + sum2;
            }
        }
    }

    @Test
    public void testRecursiveTaskDemo() {
        System.out.println("--- see console log ---");
        final Double[] nums = new Double[20];
        final ForkJoinTask<Double> task = new ExamRecursiveTask("main", nums, 0, 20);
        final ForkJoinPool pool = new ForkJoinPool();
        final Double sum = pool.invoke(task);
        System.out.println("sum : " + sum);
    }

    /* 教科書の練習問題より、fork-join で分割するときに不適切な終端チェックをしているため
     * どこまでも分割が終わらず、 無限ループ化して StackOverflowError などの実行時エラーとなる例。
     */
    static class InfiniteAction extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int a;
        private final int b;

        InfiniteAction(final int a, final int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        protected void compute() {
            if (a < 0) {
                return;
            } else {
                final int c = a + (b - a) / 2;
                invokeAll(new InfiniteAction(a, c), new InfiniteAction(c, b));
            }
        }
    }

    //@Test
    public void testInfiniteRecursiveActionDemo() {
        final ForkJoinTask<?> task = new InfiniteAction(0, 10);
        final ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(task);
    }
}
