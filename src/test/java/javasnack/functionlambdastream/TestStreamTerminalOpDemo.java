package javasnack.functionlambdastream;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class TestStreamTerminalOpDemo {

    @Test
    public void testToArray() {
        assertThat(Stream.of(1, 2, 3, 4, 5)
                .toArray()).isEqualTo(new int[] { 1, 2, 3, 4, 5 });
        assertThat(Stream.of("aaa", "bbb", "ccc", "ddd")
                .toArray()).isEqualTo(new String[] { "aaa", "bbb", "ccc", "ddd" });
    }

    @Test
    public void testForEach() {
        final ArrayList<Integer> ints = new ArrayList<>();
        Stream.of(1, 2, 3, 4, 5).forEach(i -> ints.add(i));
        assertThat(ints).isEqualTo(List.of(1, 2, 3, 4, 5));

        final ArrayList<String> strs = new ArrayList<>();
        Stream.of("aaa", "bbb", "ccc", "ddd").forEach(s -> strs.add(s));
        assertThat(strs).isEqualTo(List.of("aaa", "bbb", "ccc", "ddd"));
    }

    @Test
    public void testCount() {
        assertThat(Stream.of(1, 2, 3, 4, 5).count()).isEqualTo(5);
        assertThat(Stream.of("aaa", "bbb", "ccc", "ddd").count()).isEqualTo(4);
    }

    @Test
    public void testMaxMin() {
        assertThat(Stream.of(1, 2, 3, 4, 5).max(Comparator.naturalOrder()).get()).isEqualTo(5);
        assertThat(Stream.of(1, 2, 3, 4, 5).min(Comparator.naturalOrder()).get()).isEqualTo(1);
        assertThat(Stream.of("a", "bb", "ccc", "dddd", "eeeee")
                .max(Comparator.comparing(s -> s.length())).get()).isEqualTo("eeeee");
        assertThat(Stream.of("a", "bb", "ccc", "dddd", "eeeee")
                .min(Comparator.comparing(s -> s.length())).get()).isEqualTo("a");
    }

    @Test
    public void testFindFirst() {
        final AtomicInteger counter = new AtomicInteger(0);
        final Supplier<Integer> sup0 = new Supplier<>() {
            @Override
            public Integer get() {
                return counter.incrementAndGet();
            }
        };
        assertThat(Stream.generate(sup0)
                .filter(i -> i > 3)
                .findFirst()
                .get()).isEqualTo(4);
        assertThat(counter.get()).isEqualTo(4);

        assertThat(Stream.of(1, 2, 3, 4, 5)
                .filter(i -> i > 10)
                .findFirst()
                .isPresent()).isFalse();
    }

    @Test
    public void testFindAny() {
        final String f = Stream.of("aaa", "bbbb", "ccc")
                .filter(s -> s.length() == 3)
                .findAny()
                .get();
        assertThat(List.of("aaa", "ccc").contains(f)).isTrue();
    }

    @Test
    public void testMatches() {
        assertThat(Stream.of("aaa", "bbb", "ccc").allMatch(s -> s.length() == 3)).isTrue();
        assertThat(Stream.of("aaa", "bbb", "ccc").anyMatch(s -> s.length() == 3)).isTrue();
        assertThat(Stream.of("aaa", "bbb", "ccc").noneMatch(s -> s.length() == 3)).isFalse();
        assertThat(Stream.of("aaa", "bbb", "cccx").allMatch(s -> s.length() == 3)).isFalse();
        assertThat(Stream.of("aaa", "bbb", "cccx").anyMatch(s -> s.length() == 3)).isTrue();
        assertThat(Stream.of("aaa", "bbb", "cccx").noneMatch(s -> s.length() == 3)).isFalse();
        assertThat(Stream.of("aaax", "bbbx", "cccx").noneMatch(s -> s.length() == 3)).isTrue();
    }

    @Test
    public void testReduce() {
        assertThat(Stream.of("aa", "bb", "cc", "dd")
                .reduce((s1, s2) -> {
                    return s1 + ":" + s2;
                }).get()).isEqualTo("aa:bb:cc:dd");
        assertThat(Stream.empty()
                .reduce((s1, s2) -> {
                    return s1 + ":" + s2;
                }).isPresent()).isFalse();

        assertThat(Stream.of("aa", "bb", "cc", "dd")
                .reduce("xxx", (s1, s2) -> {
                    return s1 + ":" + s2;
                })).isEqualTo("xxx:aa:bb:cc:dd");
        assertThat(Stream.empty()
                .reduce("xxx", (s1, s2) -> {
                    return s1 + ":" + s2;
                })).isEqualTo("xxx");

        assertThat(Stream.of("aa", "bb", "cc", "dd")
                .reduce("xxx", (s1, s2) -> {
                    return s1 + ":" + s2;
                }, (s1, s2) -> {
                    return s1 + ":" + s2;
                })).isEqualTo("xxx:aa:bb:cc:dd");
        assertThat(Stream.empty()
                .reduce("xxx", (s1, s2) -> {
                    return s1 + ":" + s2;
                }, (s1, s2) -> {
                    return s1 + ":" + s2;
                })).isEqualTo("xxx");

        /* from: https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html#reduce-U-java.util.function.BiFunction-java.util.function.BinaryOperator-
         * The identity value must be an identity for the combiner function.
         * This means that for all u, combiner(identity, u) is equal to u.
         * 
         * (japanese)
         * from: https://docs.oracle.com/javase/jp/8/docs/api/java/util/stream/Stream.html#reduce-U-java.util.function.BiFunction-java.util.function.BinaryOperator-
         * identity値はコンバイナ関数の単位元でなければいけません。
         * つまり、すべてのuについて、combiner(identity, u)がuに等しくなります。
         */
        final String identity1 = "";
        final BinaryOperator<String> combiner1 = (s1, s2) -> {
            return s1 + s2;
        };
        assertThat(combiner1.apply(identity1, "xxx")).isEqualTo("xxx");

        /* Additionally, the combiner function must be compatible with the accumulator function; 
         * for all u and t, the following must hold:
         * 
         * (japanese)
         * さらに、combiner関数はaccumulator関数と互換性がある必要があります。
         * すべてのuとtについて、次が成り立つ必要があります。
         * combiner.apply(u, accumulator.apply(identity, t)) == accumulator.apply(u, t)
         */
        final BiFunction<String, Integer, String> accumulator1 = (s, i) -> {
            return s + Integer.valueOf(i).toString();
        };
        assertThat(combiner1.apply("xxx", accumulator1.apply(identity1, 10))).isEqualTo(accumulator1.apply("xxx", 10));

        assertThat(Stream.of(10, 20, 30, 40, 50)
                .reduce(identity1, accumulator1, combiner1)).isEqualTo("1020304050");
    }

    @Test
    public void testCollect() {
        final List<String> l0 = Stream.of("aa", "bb", "cc", "dd")
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        assertThat(l0).isEqualTo(List.of("aa", "bb", "cc", "dd"));

        final String s0 = Stream.of("aa", "bb", "cc", "dd")
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        assertThat(s0).isEqualTo("aabbccdd");

        /* from: https://docs.oracle.com/javase/8/docs/api/java/util/stream/Collector.html
         * To ensure that sequential and parallel executions produce equivalent results, 
         * the collector functions must satisfy an identity and an associativity constraints.
         * 
         * The identity constraint says that for any partially accumulated result, 
         * combining it with an empty result container must produce an equivalent result. 
         * That is, for a partially accumulated result a that is the result of any series of 
         * accumulator and combiner invocations, a must be equivalent to combiner.apply(a, supplier.get()).
         * 
         * (japanese)
         * from: https://docs.oracle.com/javase/jp/8/docs/api/java/util/stream/Collector.html
         * 順次実行と並列実行で同一の結果が得られるようにするには、
         * コレクタの関数が同一性制約と結合性制約を満たす必要があります。
         * 
         * 同一性制約とは、部分的に蓄積された任意の結果について、
         * その結果を空の結果コンテナと結合したときに元と同じ結果が生成される必要がある、
         * というものです。
         * つまり、一連のアキュムレータ呼出しやコンバイナ呼出しの結果として得られた、
         * 部分的に蓄積された結果aについて、aはcombiner.apply(a, supplier.get())と等しくなる必要があります。
         */
        Supplier<TreeSet<String>> supplier1 = TreeSet::new;
        BinaryOperator<TreeSet<String>> combiner1 = (left, right) -> {
            left.addAll(right);
            return left;
        };
        final TreeSet<String> a1 = new TreeSet<>(List.of("aaa", "bbb", "ccc"));
        assertThat(combiner1.apply(a1, supplier1.get())).isEqualTo(a1);

        /* The associativity constraint says that splitting the computation must produce an equivalent result.
         * That is, for any input elements t1 and t2, the results r1 and r2 in the computation below must be equivalent:
         * 
         * 結合性制約とは、計算を分割しても同一の結果が得られなければいけない、というものです。
         * つまり、任意の入力要素t1とt2について、以下の計算の結果r1とr2が等しくなる必要があります。
         */
        final BiConsumer<TreeSet<String>, Integer> accumulator1 = (a0, i0) -> {
            a0.add("item" + i0);
        };
        final Function<TreeSet<String>, List<String>> finisher1 = set -> {
            return new ArrayList<>(set);
        };
        final TreeSet<String> a1demo = supplier1.get();
        accumulator1.accept(a1demo, 10);
        accumulator1.accept(a1demo, 20);
        final List<String> r1 = finisher1.apply(a1demo);

        final TreeSet<String> a2demo = supplier1.get();
        accumulator1.accept(a2demo, 10);
        final TreeSet<String> a3demo = supplier1.get();
        accumulator1.accept(a3demo, 20);
        final List<String> r2 = finisher1.apply(combiner1.apply(a2demo, a3demo));

        assertThat(r1).isEqualTo(r2);

        assertThat(Stream.of(10, 20, 30, 40, 50)
                .collect(Collector.of(supplier1, accumulator1, combiner1, finisher1)))
                        .isEqualTo(List.of("item10", "item20", "item30", "item40", "item50"));
    }
}
