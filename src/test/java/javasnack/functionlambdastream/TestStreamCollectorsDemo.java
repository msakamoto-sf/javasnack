package javasnack.functionlambdastream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

    @Test
    public void testToListDemo() {
        final Collector<String, ?, List<String>> col1 = Collectors.toList();
        List<String> l1 = Stream.of("aa", "bb", "cc").collect(col1);
        assertThat(l1).isEqualTo(List.of("aa", "bb", "cc"));
        l1.add("dd");
        assertThat(l1).isEqualTo(List.of("aa", "bb", "cc", "dd"));
    }

    @Test
    public void testToSetDemo() {
        final Collector<String, ?, Set<String>> col1 = Collectors.toSet();
        final Set<String> s1 = Stream.of("aa", "bb", "cc").collect(col1);
        assertThat(s1).isEqualTo(Set.of("aa", "bb", "cc"));
        s1.add("dd");
        assertThat(s1).isEqualTo(Set.of("aa", "bb", "cc", "dd"));
    }

    @Test
    public void testToMapDemo() {
        final Collector<String, ?, Map<String, File>> col1 = Collectors.toMap(s -> s, s -> new File(s));
        final Map<String, File> m1 = Stream.of("/home/aaa", "/home/bbb", "/home/ccc").collect(col1);
        assertThat(m1).hasSize(3);
        assertThat(m1.get("/home/aaa")).isEqualTo(new File("/home/aaa"));
        assertThat(m1.get("/home/bbb")).isEqualTo(new File("/home/bbb"));
        assertThat(m1.get("/home/ccc")).isEqualTo(new File("/home/ccc"));
        m1.put("/home/ddd", new File("/home/ddd"));
        assertThat(m1).hasSize(4);
        assertThat(m1.get("/home/ddd")).isEqualTo(new File("/home/ddd"));
    }

    @Test
    public void testToUnmodifiableListDemo() {
        final Collector<String, ?, List<String>> col1 = Collectors.toUnmodifiableList();
        List<String> l1 = Stream.of("aa", "bb", "cc").collect(col1);
        assertThat(l1).isEqualTo(List.of("aa", "bb", "cc"));
        final Throwable thrown1 = catchThrowable(() -> l1.add("dd"));
        assertThat(thrown1).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testToUnmodifiableSetDemo() {
        final Collector<String, ?, Set<String>> col1 = Collectors.toUnmodifiableSet();
        final Set<String> s1 = Stream.of("aa", "bb", "cc").collect(col1);
        assertThat(s1).isEqualTo(Set.of("aa", "bb", "cc"));
        final Throwable thrown1 = catchThrowable(() -> s1.add("dd"));
        assertThat(thrown1).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testToMapUnmodifiableDemo() {
        final Collector<String, ?, Map<String, File>> col1 = Collectors.toUnmodifiableMap(s -> s, s -> new File(s));
        final Map<String, File> m1 = Stream.of("/home/aaa", "/home/bbb", "/home/ccc").collect(col1);
        assertThat(m1).hasSize(3);
        assertThat(m1.get("/home/aaa")).isEqualTo(new File("/home/aaa"));
        assertThat(m1.get("/home/bbb")).isEqualTo(new File("/home/bbb"));
        assertThat(m1.get("/home/ccc")).isEqualTo(new File("/home/ccc"));
        final Throwable thrown1 = catchThrowable(() -> m1.put("/home/ddd", new File("/home/ddd")));
        assertThat(thrown1).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testToCollectionDemo() {
        final Collector<String, ?, Collection<String>> col1 = Collectors.toCollection(() -> new ArrayList<>());
        Collection<String> l1 = Stream.of("aa", "bb", "cc").collect(col1);
        assertThat(l1).isEqualTo(List.of("aa", "bb", "cc"));
        l1.add("dd");
        assertThat(l1).isEqualTo(List.of("aa", "bb", "cc", "dd"));
    }

    @Test
    public void testToCollectingAndThenDemo() {
        final Collector<String, ?, List<String>> col1 = Collectors.toList();
        final Collector<String, ?, List<String>> col2 = Collectors.collectingAndThen(col1,
                Collections::unmodifiableList);
        List<String> l1 = Stream.of("aa", "bb", "cc").collect(col2);
        assertThat(l1).isEqualTo(List.of("aa", "bb", "cc"));
        final Throwable thrown1 = catchThrowable(() -> l1.add("dd"));
        assertThat(thrown1).isInstanceOf(UnsupportedOperationException.class);
    }

    /* TODO
     * flatMapping
     * mapping
     * partitioningBy
     * groupingBy
     * reducing
     */
}
