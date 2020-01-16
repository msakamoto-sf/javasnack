package javasnack.functionlambdastream;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import org.junit.jupiter.api.Test;

/* reference:
 * https://docs.oracle.com/javase/jp/11/docs/api/java.base/java/util/stream/package-summary.html
 * https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/package-summary.html
 * https://docs.oracle.com/javase/jp/8/docs/api/java/util/stream/package-summary.html
 * https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html
 * 
 * see:
 * http://enterprisegeeks.hatenablog.com/entry/2014/04/30/150535
 */
public class TestStreamCreateExamples {

    @Test
    public void testCollectionToStream() {
        final List<String> l1 = Arrays.asList("bbb", "aaa", "ddd", "ccc");
        assertThat(l1.stream().sorted().collect(Collectors.toList())).isEqualTo(List.of("aaa", "bbb", "ccc", "ddd"));

        final Set<String> set1 = new HashSet<>(Arrays.asList("BBB", "AAA", "CCC", "AAA"));
        assertThat(set1.stream().collect(Collectors.toSet())).isEqualTo(Set.of("AAA", "BBB", "CCC"));

        final Map<String, Integer> map1 = Map.of("aaa", 100, "BBB", 200, "ccc", 300, "DDD", 400);
        assertThat(map1.entrySet().stream()
                .sorted(Comparator.comparing(Entry::getKey, String.CASE_INSENSITIVE_ORDER))
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.toList())).isEqualTo(List.of("aaa:100", "BBB:200", "ccc:300", "DDD:400"));

        // iterator to stream : see TestSpliteratorDemo
    }

    @Test
    public void testArrayToStream() {
        final String[] strings = new String[] { "aaa", "bbb", "ddd", "ccc" };
        assertThat(Arrays.stream(strings)
                .sorted()
                .collect(Collectors.toList())).isEqualTo(List.of("aaa", "bbb", "ccc", "ddd"));
    }

    public void testBufferedReaderToStream() {
        // TODO
    }

    public void testFileToStreamOfLines() {
        // TODO
    }

    @Test
    public void testBitSetToStream() {
        // 0xCA : 1100 1010
        // 0xFE : 1111 1110
        // -> ([15]) 1111 1110 1100 1010 ([0])
        final byte[] src = new byte[] { (byte) 0xCA, (byte) 0xFE };
        assertThat(BitSet.valueOf(src).stream().toArray())
                .isEqualTo(new int[] { 1, 3, 6, 7, 9, 10, 11, 12, 13, 14, 15 });
    }

    public void testStreamStaticMethods() {
        // TODO
        // Stream.of()
        // Stream.empty()
        // Stream.concat()
        // Stream.generate()
        // Stream.iterate()
        // IntStream.range()
        // LongStream.range()
    }

    @Test
    public void testStreamBuilder() {
        final Builder<String> b = Stream.builder();
        final List<String> r = b.add("aaa").add("bbb").add("ccc").add("ddd")
                .build()
                .collect(Collectors.toList());
        assertThat(r).isEqualTo(List.of("aaa", "bbb", "ccc", "ddd"));
    }
}
