package javasnack.ojcp.se8gold.chapter10;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class Test10ParallelStreamDemo {
    @Test
    public void testParallelStreamAndForEachDemo() {
        /* NOTE: parallel stream の生成は、2種類ある。
         * Collection -> Collection.parallelStream()
         * Stream -> BaseStream.parallel()
         * (メソッド名の微妙な差異に注意。)
         */
        List<String> data = Arrays.asList("aaa", "bb", "c");
        Stream<String> st1 = data.parallelStream();
        Stream<String> st2 = data.stream();
        assertThat(st1.isParallel()).isTrue();
        assertThat(st2.isParallel()).isFalse();

        // 多段で切り替えても、最後に呼んだのが有効になる。
        assertThat(st1.sequential().isParallel()).isFalse();
        assertThat(st1.sequential().parallel().isParallel()).isTrue();
        assertThat(st1.sequential().parallel().sequential().isParallel()).isFalse();
        assertThat(st1.sequential().parallel().sequential().parallel().isParallel()).isTrue();

        Stream<String> st3 = st2.parallel();
        assertThat(st3.isParallel()).isTrue();

        IntStream st4 = IntStream.range(0, 10).parallel();
        assertThat(st4.isParallel()).isTrue();

        System.out.println("--- see console log ---");
        Arrays.asList("a", "b", "c", "d", "e")
                .stream()
                .forEach(s -> System.out.println(Thread.currentThread().getName() + ":" + s));
        Arrays.asList("A", "B", "C", "D", "E")
                .parallelStream()
                .forEach(s -> System.out.println(Thread.currentThread().getName() + ":" + s));
    }

    @Test
    public void testStreamOfParallelStreamDemo() {
        final Stream<Integer> s1 = Stream.of(10, 20, 30, 40).parallel();
        final Stream<Integer> s2 = Stream.of(50, 60, 70).parallel();
        final Stream<Stream<Integer>> st3 = Stream.of(s1, s2);
        final Map<Boolean, List<Integer>> m0 = st3
                .flatMap(i -> i)
                .collect(Collectors.groupingByConcurrent(i -> i < 35));
        assertThat(m0.get(true)).hasSize(3);
        assertThat(m0.get(true).contains(10)).isTrue();
        assertThat(m0.get(true).contains(20)).isTrue();
        assertThat(m0.get(true).contains(30)).isTrue();
        assertThat(m0.get(false)).hasSize(4);
        assertThat(m0.get(false).contains(40)).isTrue();
        assertThat(m0.get(false).contains(50)).isTrue();
        assertThat(m0.get(false).contains(60)).isTrue();
        assertThat(m0.get(false).contains(70)).isTrue();
    }

    @Test
    public void testMapAndForeachOrderedDemo() {
        System.out.println("--- see console log ---");
        final List<String> l0 = new CopyOnWriteArrayList<String>();
        /* NOTE: forEachOrdered() は元のstreamの順序を維持する。(performance 低下の可能性あり)
         * 途中のmap()についてはmulti threadで処理されるため順序は保証されない。
         */
        Arrays.asList("a", "b", "c", "d", "e")
                .parallelStream()
                .map(s -> {
                    l0.add(s);
                    return s.toUpperCase();
                })
                .forEachOrdered(s -> System.out.println(Thread.currentThread().getName() + ":" + s));
        for (String s : l0) {
            System.out.print(s + " ");
        }
        System.out.println();
    }

    @Test
    public void testFindAnyAndFindFirstDemo() {
        System.out.println("--- see console log ---");
        List<String> data = Arrays.asList("c", "a", "d", "b");

        /* parallel stream では最初に処理される要素が不定 -> findFirst() により、
         * 確実に最初の要素が処理されるまで時間を要する可能性がある。
         * ただし sequential stream と結果は同じであり、
         * skip() や limit() の中間処理も同様の性質を持つ。
         */
        Optional<String> s1 = data.parallelStream().findFirst();
        System.out.println("findFirst() = " + s1.get());
        assertThat(s1.get()).isEqualTo("c");

        // findAny はどれか一つの要素を返すので、parallel/sequential 同様。
        Optional<String> s2 = data.parallelStream().findAny();
        System.out.println("findAny() = " + s2.get());
    }

    @Test
    public void testParallelReduceDemo() {
        System.out.println("--- see console log ---");
        Integer total = Arrays.asList(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150)
                .parallelStream()
                .reduce(
                        // identity : 各要素毎に分割された後、まず identity と accumulate される。
                        5,
                        // accumulator : 1st arg を過去計算結果, 2nd arg が対象要素
                        (sum, a) -> {
                            System.out.println(Thread.currentThread().getName() + ", sum:" + sum + " a:" + a);
                            return sum += a;
                        },
                        // combiner : multi-threadで分割した結果を結合する。
                        (b, c) -> {
                            System.out.println(Thread.currentThread().getName() + ", b:" + b + " c:" + c);
                            return b + c;
                        });
        assertThat(total).isEqualTo(1275);
    }

    @Test
    public void testParallelCollectionDemo() {
        List<String> data = Arrays.asList("aa", "bb", "cc", "dd", "ee", "ff", "gg", "hh", "ii");
        List<String> l0 = data.parallelStream()
                .collect(
                        // supplier : 結果を反映する対象(container)
                        () -> new CopyOnWriteArrayList<>(),
                        // accumulator : 1st arg が過去計算結果が反映された container, 2nd arg が対象要素
                        (plist, s) -> plist.add(s.toUpperCase()),
                        // combiner : multi-threadで分割した結果を結合する。
                        (alist, blist) -> alist.addAll(blist));
        // collect 終端処理では、順序が復元される。
        assertThat(l0.stream().collect(Collectors.joining())).isEqualTo("AABBCCDDEEFFGGHHII");

        Set<String> s0 = data.parallelStream()
                .collect(
                        // supplier : 結果を反映する対象(container)
                        CopyOnWriteArraySet::new,
                        // accumulator : 1st arg が過去計算結果が反映された container, 2nd arg が対象要素
                        Set::add,
                        // combiner : multi-threadで分割した結果を結合する。
                        Set::addAll);
        // collect 終端処理では、順序が復元される。
        assertThat(s0.stream().collect(Collectors.joining())).isEqualTo("aabbccddeeffgghhii");
    }

    @Test
    public void testGroupingByConcurrentDemo() {
        final Map<String, List<String>> m0 = Stream.of("aa", "ab", "bb", "bc", "cc")
                .parallel()
                .collect(Collectors.groupingByConcurrent(s -> s.substring(0, 1)));

        assertThat(m0).hasSize(3);
        // Listの順序は不定
        assertThat(m0.get("a")).hasSize(2);
        assertThat(m0.get("a").contains("aa")).isTrue();
        assertThat(m0.get("a").contains("ab")).isTrue();
        assertThat(m0.get("b")).hasSize(2);
        assertThat(m0.get("b").contains("bb")).isTrue();
        assertThat(m0.get("b").contains("bc")).isTrue();
        assertThat(m0.get("c")).isEqualTo(List.of("cc"));
        assertThat(m0 instanceof ConcurrentMap).isTrue();
        assertThat(m0 instanceof ConcurrentHashMap).isTrue();
    }

    @Test
    public void testToConcurrentMapDemo() {
        final Map<String, String> m0 = Stream.of("aa", "ab", "bb", "bc", "cc")
                .parallel()
                .collect(Collectors.toConcurrentMap(
                        // key-mapper
                        s -> s.substring(0, 1),
                        // value-mapper
                        s -> s,
                        // merge-function : value が衝突したときの結合処理
                        (s1, s2) -> s1 + ":" + s2));

        assertThat(m0).hasSize(3);
        // value が衝突したときの結合順序は不定
        assertThat(m0.get("a").length()).isEqualTo(5);
        assertThat(m0.get("a").contains(":")).isTrue();
        assertThat(m0.get("a").contains("aa")).isTrue();
        assertThat(m0.get("a").contains("ab")).isTrue();
        assertThat(m0.get("b").length()).isEqualTo(5);
        assertThat(m0.get("b").contains(":")).isTrue();
        assertThat(m0.get("b").contains("bb")).isTrue();
        assertThat(m0.get("b").contains("bc")).isTrue();
        assertThat(m0.get("c")).isEqualTo("cc");
        assertThat(m0 instanceof ConcurrentMap).isTrue();
        assertThat(m0 instanceof ConcurrentHashMap).isTrue();
    }
}
