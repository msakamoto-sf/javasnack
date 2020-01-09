package javasnack.stream.fpjbook;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

public class TestChapter6SamplesDemo {

    /* JUnit5の デフォルトライフサイクルでは
     * テストメソッドごとにインスタンスを作るため、
     * @BeforeEach/@AfterEachで0クリアは不要。
     */
    static class StepLogger {
        private final List<String> logs = new ArrayList<>();

        public synchronized void log(final String msg) {
            logs.add(msg);
        }

        public List<String> getLogs() {
            return Collections.unmodifiableList(this.logs);
        }
    }

    final StepLogger stepLogger = new StepLogger();

    // chapter 6.1 : initialize delay

    static class Heavy {
        final StepLogger slog;

        public Heavy(final StepLogger slog) {
            this.slog = slog;
            this.slog.log("Heavy created");
        }

        @Override
        public String toString() {
            this.slog.log("quite heavy");
            return "heavy";
        }
    }

    static class HolderNaive {
        private Heavy heavy;
        private final StepLogger slog;

        public HolderNaive(final StepLogger slog) {
            this.slog = slog;
        }

        public synchronized Heavy getHeavy() {
            if (heavy == null) {
                heavy = new Heavy(this.slog);
            }
            return heavy;
        }
    }

    @Test
    public void testHolderNaive() {
        final HolderNaive holder = new HolderNaive(stepLogger);
        assertThat(stepLogger.getLogs()).isEmpty();
        System.out.println(holder.getHeavy());
        assertThat(stepLogger.getLogs()).hasSize(2).isEqualTo(List.of("Heavy created", "quite heavy"));
        System.out.println(holder.getHeavy());
        assertThat(stepLogger.getLogs()).hasSize(3).isEqualTo(List.of("Heavy created", "quite heavy", "quite heavy"));
    }

    static class Holder {
        private Supplier<Heavy> heavy;

        public Holder(final StepLogger slog) {
            this.heavy = () -> createAndCacheHeavy(slog);
        }

        public Heavy getHeavy() {
            return heavy.get();
        }

        public synchronized Heavy createAndCacheHeavy(final StepLogger slog) {
            class HeavyFactory implements Supplier<Heavy> {
                private final Heavy heavyInstance = new Heavy(slog);

                @Override
                public Heavy get() {
                    return this.heavyInstance;
                }
            }
            if (!HeavyFactory.class.isInstance(this.heavy)) {
                slog.log("Heavy factory created");
                /* ここ、すごい分かりづらいけど Holder クラスの
                 * Supplier<Heavy> heavy フィールドに、生成済みの = 排他制御不要な
                 * singletonを返すだけの Supplier<Heavy> を直接設定している。
                 */
                this.heavy = new HeavyFactory();
            }
            slog.log("return from createAndCacheHeavy()");
            return heavy.get();
        }
    }

    @Test
    public void testHolderSmartDelayed() {
        final Holder holder = new Holder(stepLogger);
        assertThat(stepLogger.getLogs()).isEmpty();
        /* 以下、書籍の説明だけだとかなり分かりづらいので補足説明。
         * 
         * (1) まず Holder のコンストラクタ時点では
         * this.heavy = () -> createAndCacheHeavy(screamer);
         * となるので、Supplier<Heavy> heavy の get() が呼ばれたら
         * createAndCacheHeavy(screamer) の戻り値が返される。
         * (createAndCacheHeavy自体はコンストラクタ時点では呼ばれてない)
         * 
         * (2) 最初に Holder.getHeavy() が呼ばれると、続いて
         * this.heavy.get() 経由で createAndCacheHeavy(screamer) が呼ばれる。
         * 重要 : この時点での this.heavy は HeavyFactory クラスではなく、
         * コンストラクタ中でラムダ式で生成されたクラスなので、
         * if文により HeavyFactory クラスのインスタンスに上書きされる。
         * createAndCacheHeavy(screamer) 自体は this.heavy.get() を返すが、
         * これは上記if文処理により HeavyFactory クラスのインスタンスになっているため、
         * Heavy のインスタンスが返される。
         * 
         * (3) 2回目に Holder.getHeavy() が呼ばれると、(2) で this.heavy が
         * 排他制御しない HeavyFactory クラスに入れ替わっている。
         * そのため、排他制御の無いgetterにより Heavy インスタンスを返すだけとなる。
         * 
         * ポイント:
         * 「排他制御が必要なインスタンス生成を行う Supplier (A)」と
         * 「排他制御が不要な Supplier (B)」を用意しておき、
         * (B)は(A)で生成されたインスタンスを返すようにしておく。
         * そして、(A)の中で、Supplier を (B) に入れ替える。
         * これにより初回呼び出しだけ排他制御が発生するが、2回目以降はキャッシュされた (B) 
         * が呼び出される仕組みとなる。
         * 
         * (A) の中で Holder.heavy フィールドを書き換えているのが、
         * 「自分で自分を書き換えている」ように見えてややこしいが、
         * 仕組みとポイントとしては上記のような動きになっている。
         */
        System.out.println(holder.getHeavy());
        assertThat(stepLogger.getLogs()).hasSize(4).isEqualTo(List.of(
                "Heavy factory created",
                "Heavy created",
                "return from createAndCacheHeavy()",
                "quite heavy"));
        System.out.println(holder.getHeavy());
        assertThat(stepLogger.getLogs()).hasSize(5).isEqualTo(List.of(
                "Heavy factory created",
                "Heavy created",
                "return from createAndCacheHeavy()",
                "quite heavy",
                "quite heavy"));
    }

    // chapter 6.2 : eager / lazy evaluate

    static boolean evaluate(final int v, final StepLogger slog) {
        slog.log("eval: " + v);
        return v > 100;
    }

    static boolean eagerEval(final boolean e1, final boolean e2) {
        return e1 && e2;
    }

    static boolean lazyEval(final Supplier<Boolean> e1, final Supplier<Boolean> e2) {
        // e1.get() が false を返せば、e2.get() は呼ばれない。
        return e1.get() && e2.get();
    }

    @Test
    public void testEagerLazyEvaluate() {
        assertThat(eagerEval(evaluate(1, stepLogger), evaluate(2, stepLogger))).isFalse();
        assertThat(stepLogger.getLogs()).hasSize(2).isEqualTo(List.of("eval: 1", "eval: 2"));

        // e1がfalseを返すので、e2 の Supplier.get() は呼ばれず、"eval: 4" は現れない。
        assertThat(lazyEval(() -> evaluate(3, stepLogger), () -> evaluate(4, stepLogger))).isFalse();
        assertThat(stepLogger.getLogs()).hasSize(3).isEqualTo(List.of("eval: 1", "eval: 2", "eval: 3"));
    }
}
