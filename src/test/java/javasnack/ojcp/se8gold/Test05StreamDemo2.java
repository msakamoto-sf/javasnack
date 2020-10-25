package javasnack.ojcp.se8gold;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class Test05StreamDemo2 {

    @Test
    public void testCollectorsDemo1() {
        // toList()
        assertThat(Stream.of("aa", "bb", "cc")
                .collect(Collectors.toList())).isEqualTo(List.of("aa", "bb", "cc"));
        // toSet()
        assertThat(Stream.of("aa", "bb", "aa")
                .collect(Collectors.toSet())).isEqualTo(Set.of("aa", "bb"));

        // joining()
        assertThat(Stream.of("aa", "bb", "cc")
                .collect(Collectors.joining())).isEqualTo("aabbcc");
        assertThat(Stream.of("aa", "bb", "cc")
                .collect(Collectors.joining(","))).isEqualTo("aa,bb,cc");

        // summing{Int|Long|Double} : 合計する値がint|long|doubleで、結果は常にdouble
        assertThat(Stream.of("a", "bb", "ccc")
                .collect(Collectors.summingInt(s -> s.length()))).isEqualTo(6);
        assertThat(Stream.of("a", "bb", "ccc")
                .collect(Collectors.summingLong(s -> (long) s.length()))).isEqualTo(6L);
        assertThat(Stream.of("a", "bb", "ccc")
                .collect(Collectors.summingDouble(s -> (double) s.length()))).isEqualTo(6.0);

        // averaging{Int|Long|Double} : 平均する値がint|long|doubleで、結果は常にdouble
        assertThat(Stream.of("a", "bb", "ccc", "dddd")
                .collect(Collectors.averagingInt(s -> s.length()))).isEqualTo(2.5);
        assertThat(Stream.of("a", "bb", "ccc", "dddd")
                .collect(Collectors.averagingLong(s -> (long) s.length()))).isEqualTo(2.5);
        assertThat(Stream.of("a", "bb", "ccc", "dddd")
                .collect(Collectors.averagingDouble(s -> (double) s.length()))).isEqualTo(2.5);
    }

    @Test
    public void testCollectorsDemo2() {
        // toMap(keyMapper, valueMapper)
        Map<String, String> m1 = Stream.of("aa", "bb", "cc")
                .collect(Collectors.toMap(s -> s, String::toUpperCase));
        assertThat(m1.size()).isEqualTo(3);
        assertThat(m1.get("aa")).isEqualTo("AA");
        assertThat(m1.get("bb")).isEqualTo("BB");
        assertThat(m1.get("cc")).isEqualTo("CC");

        // toMap(keyMapper, valueMapper) で key が重複すると IllegalStateException
        assertThatThrownBy(() -> {
            Stream.of("aa", "bb", "ccc").collect(Collectors.toMap(String::length, String::toUpperCase));
        }).isInstanceOf(IllegalStateException.class)
                .hasMessage("Duplicate key 2 (attempted merging values AA and BB)");
        // -> toMap(keyMapper, valueMapper, mergeFunction) で重複値の結合方法を指定する。
        Map<Integer, String> m2 = Stream.of("aa", "bb", "ccc")
                .collect(Collectors.toMap(
                        String::length,
                        String::toUpperCase,
                        (s1, s2) -> s1 + ":" + s2));
        assertThat(m2.size()).isEqualTo(2);
        assertThat(m2.get(2)).isEqualTo("AA:BB");
        assertThat(m2.get(3)).isEqualTo("CCC");
        assertThat(m2 instanceof TreeMap).isFalse();

        // toMap(keyMapper, valueMapper, mergeFunction, mapSupplier)
        m2 = Stream.of("aa", "bb", "ccc")
                .collect(Collectors.toMap(
                        String::length,
                        String::toUpperCase,
                        (s1, s2) -> s1 + ":" + s2,
                        // 実装クラスを supplier でカスタマイズ
                        TreeMap::new));
        assertThat(m2.size()).isEqualTo(2);
        assertThat(m2.get(2)).isEqualTo("AA:BB");
        assertThat(m2.get(3)).isEqualTo("CCC");
        assertThat(m2 instanceof TreeMap).isTrue();
    }

    @Test
    public void testCollectorsDemo3() {
        // groupingBy(classifier)
        Map<String, List<String>> m1 = Stream.of("bb", "aa", "ab", "ba", "cc")
                .collect(Collectors.groupingBy(s -> s.substring(0, 1)));
        assertThat(m1.size()).isEqualTo(3);
        assertThat(m1.get("a")).isEqualTo(List.of("aa", "ab"));
        assertThat(m1.get("b")).isEqualTo(List.of("bb", "ba"));
        assertThat(m1.get("c")).isEqualTo(List.of("cc"));

        // groupingBy(classifier, collector)
        Map<String, Set<String>> m2 = Stream.of("bb", "aa", "ab", "ba", "cc")
                .collect(Collectors.groupingBy(s -> s.substring(0, 1), Collectors.toSet()));
        assertThat(m2.size()).isEqualTo(3);
        assertThat(m2.get("a")).isEqualTo(Set.of("aa", "ab"));
        assertThat(m2.get("b")).isEqualTo(Set.of("bb", "ba"));
        assertThat(m2.get("c")).isEqualTo(Set.of("cc"));
        Map<String, String> m3 = Stream.of("bb", "aa", "ab", "ba", "cc")
                .collect(Collectors.groupingBy(s -> s.substring(0, 1), Collectors.joining(",")));
        assertThat(m3.size()).isEqualTo(3);
        assertThat(m3.get("a")).isEqualTo("aa,ab");
        assertThat(m3.get("b")).isEqualTo("bb,ba");
        assertThat(m3.get("c")).isEqualTo("cc");
        assertThat(m3 instanceof TreeMap).isFalse();

        // groupingBy(classifier, mapFactory, collector)
        Map<String, String> m4 = Stream.of("bb", "aa", "ab", "ba", "cc")
                .collect(Collectors.groupingBy(
                        s -> s.substring(0, 1),
                        // 実装クラスを supplier でカスタマイズ
                        TreeMap::new,
                        Collectors.joining(",")));
        assertThat(m4.size()).isEqualTo(3);
        assertThat(m4.get("a")).isEqualTo("aa,ab");
        assertThat(m4.get("b")).isEqualTo("bb,ba");
        assertThat(m4.get("c")).isEqualTo("cc");
        assertThat(m4 instanceof TreeMap).isTrue();
    }

    @Test
    public void testCollectorsDemo4() {
        // partitionBy(classifier)
        Map<Boolean, List<String>> m1 = Stream.of("bb", "aa", "ab", "ba", "cc")
                .collect(Collectors.partitioningBy(s -> s.startsWith("a")));
        assertThat(m1.size()).isEqualTo(2);
        assertThat(m1.get(true)).isEqualTo(List.of("aa", "ab"));
        assertThat(m1.get(false)).isEqualTo(List.of("bb", "ba", "cc"));

        // partitionBy は必ず true/false の両キーが作られる。
        // <> groupingBy はデータに応じてしか作られない。
        m1 = Stream.of("xx", "yy", "zz")
                .collect(Collectors.partitioningBy(s -> s.startsWith("a")));
        assertThat(m1.size()).isEqualTo(2);
        assertThat(m1.get(true)).hasSize(0);
        assertThat(m1.get(false)).isEqualTo(List.of("xx", "yy", "zz"));
        m1 = Stream.of("xx", "yy", "zz")
                .collect(Collectors.groupingBy(s -> s.startsWith("a")));
        assertThat(m1.size()).isEqualTo(1);
        assertThat(m1.get(false)).isEqualTo(List.of("xx", "yy", "zz"));

        // partitionBy(classifier, collector)
        Map<Boolean, Set<String>> m2 = Stream.of("bb", "aa", "ab", "ba", "cc")
                .collect(Collectors.partitioningBy(s -> s.startsWith("a"), Collectors.toSet()));
        assertThat(m2.size()).isEqualTo(2);
        assertThat(m2.get(true)).isEqualTo(Set.of("aa", "ab"));
        assertThat(m2.get(false)).isEqualTo(Set.of("bb", "ba", "cc"));

        // mapping(mapper, collector)
        assertThat(Stream.of("aa", "bb", "cc")
                .collect(Collectors.mapping(
                        s -> s.toUpperCase(),
                        Collectors.joining(",")))).isEqualTo("AA,BB,CC");

        // minBy(), maxBy()
        Optional<String> o1 = Stream.of("bb", "aa", "cc")
                .collect(Collectors.minBy(Comparator.naturalOrder()));
        assertThat(o1.isPresent());
        assertThat(o1.get()).isEqualTo("aa");
        o1 = Stream.of("bb", "aa", "cc")
                .collect(Collectors.maxBy(Comparator.naturalOrder()));
        assertThat(o1.isPresent());
        assertThat(o1.get()).isEqualTo("cc");

        // minBy(), maxBy() x groupingBy() demo
        Map<String, Optional<String>> m3 = Stream.of("aa", "ab", "ac", "ba", "bb")
                .collect(Collectors.groupingBy(
                        s -> s.substring(0, 1),
                        Collectors.minBy(Comparator.naturalOrder())));
        assertThat(m3.size()).isEqualTo(2);
        assertThat(m3.get("a").isPresent());
        assertThat(m3.get("a").get()).isEqualTo("aa");
        assertThat(m3.get("b").isPresent());
        assertThat(m3.get("b").get()).isEqualTo("ba");
        m3 = Stream.of("aa", "ab", "ac", "ba", "bb")
                .collect(Collectors.groupingBy(
                        s -> s.substring(0, 1),
                        Collectors.maxBy(Comparator.naturalOrder())));
        assertThat(m3.size()).isEqualTo(2);
        assertThat(m3.get("a").isPresent());
        assertThat(m3.get("a").get()).isEqualTo("ac");
        assertThat(m3.get("b").isPresent());
        assertThat(m3.get("b").get()).isEqualTo("bb");
    }
}
