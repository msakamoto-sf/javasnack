package javasnack.functionlambdastream;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

public class TestStreamCollectorsDemo {

    @Test
    public void testCountingDemo() {
        final Collector<Integer, ?, Long> col1 = Collectors.counting();
        assertThat(col1.characteristics()).isEqualTo(Collections.emptySet());
        assertThat(Stream.of(1, 2, 3, 4, 5).collect(col1)).isEqualTo(5L);
    }

    @Test
    public void testAveragingDemo() {
        final Collector<Integer, ?, Double> col1 = Collectors.averagingInt(i -> i * 2);
        assertThat(col1.characteristics()).isEqualTo(Collections.emptySet());
        assertThat(Stream.of(1, 2, 3, 4, 5).collect(col1)).isCloseTo(6.0, Offset.offset(0.001));
    }

    @Test
    public void testMaxByMinByDemo() {
        final Collector<Integer, ?, Optional<Integer>> col1 = Collectors.maxBy(Comparator.naturalOrder());
        assertThat(col1.characteristics()).isEqualTo(Collections.emptySet());
        assertThat(Stream.of(1, 2, 3, 4, 5).collect(col1)).get().isEqualTo(5);

        final Collector<Integer, ?, Optional<Integer>> col2 = Collectors.minBy(Comparator.naturalOrder());
        assertThat(col2.characteristics()).isEqualTo(Collections.emptySet());
        assertThat(Stream.of(1, 2, 3, 4, 5).collect(col2)).get().isEqualTo(1);
    }

    @Test
    public void testSummingDemo() {
        final Collector<Integer, ?, Integer> col1 = Collectors.summingInt(i -> i * 2);
        assertThat(col1.characteristics()).isEqualTo(Collections.emptySet());
        assertThat(Stream.of(1, 2, 3, 4, 5).collect(col1)).isEqualTo(30);
    }

    @Test
    public void testSummarizingDemo() {
        final Collector<Integer, ?, IntSummaryStatistics> col1 = Collectors.summarizingInt(i -> i * 2);
        assertThat(col1.characteristics()).isEqualTo(new HashSet<>(List.of(Collector.Characteristics.IDENTITY_FINISH)));
        final IntSummaryStatistics r = Stream.of(1, 2, 3, 4, 5).collect(col1);
        assertThat(r.getCount()).isEqualTo(5);
        assertThat(r.getMax()).isEqualTo(10);
        assertThat(r.getMin()).isEqualTo(2);
        assertThat(r.getSum()).isEqualTo(30L);
        assertThat(r.getAverage()).isCloseTo(6.0, Offset.offset(0.001));
    }

    @Test
    public void testJoiningDemo() {
        final Collector<CharSequence, ?, String> col1 = Collectors.joining(",", "<", ">");
        assertThat(col1.characteristics()).isEqualTo(Collections.emptySet());
        assertThat(Stream.of("aa", "bb", "cc").collect(col1)).isEqualTo("<aa,bb,cc>");
        assertThat(Stream.of("aa", "bb", "cc").collect(Collectors.joining(","))).isEqualTo("aa,bb,cc");
        assertThat(Stream.of("aa", "bb", "cc").collect(Collectors.joining())).isEqualTo("aabbcc");
    }

    @Test
    public void testFilteringDemo() {
        final Collector<Integer, ?, Long> col1 = Collectors.filtering(
                i -> i > 3,
                Collectors.counting());
        assertThat(col1.characteristics()).isEqualTo(Collections.emptySet());
        assertThat(Stream.of(1, 2, 3, 4, 5).collect(col1)).isEqualTo(2L);
    }

    /* TODO
     * collectingAndThen​
     * flatMapping
     * mapping
     * partitioningBy
     * groupingBy
     * reducing
     * toCollection
     * toConcurrentMap
     * toList
     * toMap
     * toSet
     * toUnmodifiableList
     * toUnmodifiableMap
     * toUnmodifiableSet
     */
}
