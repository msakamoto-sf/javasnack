package javasnack.functionlambdastream;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
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
}
