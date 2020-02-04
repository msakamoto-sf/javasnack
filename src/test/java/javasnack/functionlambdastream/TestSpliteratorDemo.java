package javasnack.functionlambdastream;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;

import lombok.Value;

/* refs:
 * - [Java] IteratorをStreamにする - Qiita
 *   - https://qiita.com/yoshi389111/items/7cd7883d1d838e95a9fa
 * - java - How to convert an iterator to a stream? - Stack Overflow
 *   - https://stackoverflow.com/questions/24511052/how-to-convert-an-iterator-to-a-stream
 * - Java8 Stream APIの基本(4) - ストリームの内部 - エンタープライズギークス (Enterprise Geeks)
 *   - http://enterprisegeeks.hatenablog.com/entry/2014/05/07/193604
 * - Java8 Stream APIの基本(5) - zip の実装 - エンタープライズギークス (Enterprise Geeks)
 *   - http://enterprisegeeks.hatenablog.com/entry/2014/05/19/100422
 * - Spliterator の characteristics メソッドが返す値まとめ #java - Qiita
 *   - https://qiita.com/okumin/items/ca2fa41dcd5d77e90bb3
 */
public class TestSpliteratorDemo {

    @Test
    public void testIntSpliteratorDemo() {
        final int[] ints = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        final int ac0 = Spliterator.CONCURRENT | Spliterator.DISTINCT | Spliterator.IMMUTABLE | Spliterator.NONNULL
                | Spliterator.ORDERED | Spliterator.SORTED;
        final List<Integer> received = new ArrayList<>();
        Spliterators.spliterator(ints, ac0).forEachRemaining((Integer i) -> {
            received.add(i);
        });
        assertThat(received).isEqualTo(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        received.clear();

        final Spliterator.OfInt spi1 = Spliterators.spliterator(ints, ac0);
        while (spi1.tryAdvance((Integer i) -> {
            received.add(i);
        })) {
            // nothing todo
        }
        assertThat(received).isEqualTo(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        received.clear();

        final Spliterator.OfInt spi2a = Spliterators.spliterator(ints, ac0);
        final Spliterator.OfInt spi2b = spi2a.trySplit();
        spi2a.forEachRemaining((Integer i) -> {
            received.add(i);
        });
        assertThat(received).isEqualTo(List.of(6, 7, 8, 9, 10));
        received.clear();
        spi2b.forEachRemaining((Integer i) -> {
            received.add(i);
        });
        assertThat(received).isEqualTo(List.of(1, 2, 3, 4, 5));
        received.clear();

        final Spliterator.OfInt spi3a = Spliterators.spliterator(ints, ac0);
        final int sum3a = StreamSupport.intStream(spi3a, false).reduce(0, (i, j) -> i + j);
        assertThat(sum3a).isEqualTo(55);

        final Spliterator.OfInt spi3b = Spliterators.spliterator(ints, ac0);
        final int sum3b = StreamSupport.intStream(spi3b, true).reduce(0, (i, j) -> i + j);
        assertThat(sum3b).isEqualTo(55);
    }

    @Test
    public void testIteratorToStreamDemo() {
        final List<String> strings = List.of("aaa", "bbb", "ccc", "ddd");
        final int ac0 = Spliterator.CONCURRENT | Spliterator.DISTINCT | Spliterator.IMMUTABLE | Spliterator.NONNULL
                | Spliterator.ORDERED | Spliterator.SORTED;
        final List<String> received = new ArrayList<>();
        final Spliterator<String> spi1 = Spliterators.spliterator(strings.iterator(), strings.size(), ac0);
        StreamSupport.stream(spi1, false).forEach(s -> {
            received.add(s);
        });
        assertThat(received).isEqualTo(List.of("aaa", "bbb", "ccc", "ddd"));
    }

    // zip demo (refs: http://enterprisegeeks.hatenablog.com/entry/2014/05/19/100422 )

    @Value(staticConstructor = "of")
    static class Pair<T, U> {
        public final T left;
        public final U right;
    }

    static class PairIterator<T, U, R> implements Iterator<R> {
        private final Iterator<T> itLeft;
        private final Iterator<U> itRight;
        private final BiFunction<T, U, R> mapper;

        public PairIterator(Iterator<T> itLeft, Iterator<U> itRight, BiFunction<T, U, R> mapper) {
            this.itLeft = itLeft;
            this.itRight = itRight;
            this.mapper = mapper;
        }

        @Override
        public boolean hasNext() {
            return itLeft.hasNext() && itRight.hasNext();
        }

        @Override
        public R next() {
            return mapper.apply(itLeft.next(), itRight.next());
        }
    }

    static <T, U> Stream<Pair<T, U>> zip(Stream<T> s1, Stream<U> s2, int size) {
        PairIterator<T, U, Pair<T, U>> itr = new PairIterator<>(s1.iterator(), s2.iterator(), Pair::new);
        int characteristics = Spliterator.IMMUTABLE | Spliterator.NONNULL;
        if (size < 0) {
            return StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(itr, characteristics), false);
        } else {
            return StreamSupport.stream(
                    Spliterators.spliterator(itr, size, characteristics), false);
        }
    }

    static <T, U> Stream<Pair<T, U>> zip(Stream<T> s1, Stream<U> s2) {
        return zip(s1, s2, -1);
    }

    @Test
    public void testZipDemo() {
        final List<String> left1 = List.of("aaa", "bbb", "ccc", "ddd");
        final List<String> right1 = List.of("AAA", "BBB", "CCC");
        final List<Pair<String, String>> pairs1 = zip(left1.stream(), right1.stream()).collect(Collectors.toList());
        assertThat(pairs1).hasSize(3);
        assertThat(pairs1.get(0)).isEqualTo(Pair.of("aaa", "AAA"));
        assertThat(pairs1.get(1)).isEqualTo(Pair.of("bbb", "BBB"));
        assertThat(pairs1.get(2)).isEqualTo(Pair.of("ccc", "CCC"));

        final List<Pair<Integer, String>> pairs2 = zip(Stream.iterate(0, i -> i + 1), right1.stream())
                .collect(Collectors.toList());
        assertThat(pairs2).hasSize(3);
        assertThat(pairs2.get(0)).isEqualTo(Pair.of(Integer.valueOf(0), "AAA"));
        assertThat(pairs2.get(1)).isEqualTo(Pair.of(Integer.valueOf(1), "BBB"));
        assertThat(pairs2.get(2)).isEqualTo(Pair.of(Integer.valueOf(2), "CCC"));
    }
}
