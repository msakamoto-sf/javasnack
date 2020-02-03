package javasnack.functionlambdastream;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;

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

    // TODO zip demo
}
