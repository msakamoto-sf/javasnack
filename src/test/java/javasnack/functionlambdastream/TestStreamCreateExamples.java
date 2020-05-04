/*
 * Copyright 2020 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javasnack.functionlambdastream;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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

    @Test
    public void testBufferedReaderToStream() {
        final StringBuilder sb = new StringBuilder();
        sb.append("aaa\n");
        sb.append("bbb\n");
        sb.append("ccc\n");
        sb.append("ddd\n");
        assertThat(
                new BufferedReader(new StringReader(sb.toString())).lines().collect(Collectors.toList()))
                        .isEqualTo(List.of("aaa", "bbb", "ccc", "ddd"));
    }

    @Test
    public void testFileToStreamOfLines(@TempDir final Path tempDir) throws IOException {
        final Path tempFile = Path.of(tempDir.toString(), "aaa.txt");
        Files.writeString(tempFile, "あいうえお\nかきくけこ\nさしすせそ\n",
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND);
        assertThat(Files.lines(tempFile, StandardCharsets.UTF_8).collect(Collectors.toList())).isEqualTo(
                List.of("あいうえお", "かきくけこ", "さしすせそ"));
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

    @Test
    public void testStreamStaticMethods() {
        assertThat(Stream.of("aaa").collect(Collectors.toList())).isEqualTo(List.of("aaa"));
        assertThat(Stream.of("aaa", "bbb", "ccc").collect(Collectors.toList())).isEqualTo(List.of("aaa", "bbb", "ccc"));

        assertThat(Stream.empty().count()).isEqualTo(0);

        final AtomicInteger counter = new AtomicInteger(0);
        final Supplier<String> sup0 = new Supplier<>() {
            @Override
            public String get() {
                return "cnt" + counter.incrementAndGet();
            }
        };
        assertThat(Stream.generate(sup0).limit(5).collect(Collectors.toList()))
                .isEqualTo(List.of("cnt1", "cnt2", "cnt3", "cnt4", "cnt5"));
        assertThat(Stream.concat(
                Stream.generate(sup0).limit(5),
                Stream.generate(sup0).limit(5))
                .limit(5)
                .collect(Collectors.toList())).isEqualTo(List.of("cnt6", "cnt7", "cnt8", "cnt9", "cnt10"));
        assertThat(counter.get()).isEqualTo(10);

        assertThat(Stream.iterate(1, i -> i * 2)
                .limit(5)
                .collect(Collectors.toList()))
                        .isEqualTo(List.of(1, 2, 4, 8, 16));

        assertThat(IntStream.range(1, 5).boxed().collect(Collectors.toList())).isEqualTo(List.of(1, 2, 3, 4));
        assertThat(LongStream.range(1, 5).boxed().collect(Collectors.toList())).isEqualTo(List.of(1L, 2L, 3L, 4L));
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
