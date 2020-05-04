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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class TestStreamIntermediateOpDemo {

    @Test
    public void testDistinct() {
        assertThat(Stream.of(3, 2, 1, 5, 1, 2, 3, 4)
                .distinct()
                .sorted()
                .toArray()).isEqualTo(new int[] { 1, 2, 3, 4, 5 });
        assertThat(Stream.of("bbb", "ccc", "aaa", "ddd", "aaa", "bbb", "aaa")
                .distinct()
                .sorted()
                .toArray()).isEqualTo(new String[] { "aaa", "bbb", "ccc", "ddd" });
    }

    @Test
    public void testFilter() {
        assertThat(Stream.of(3, 2, 1, 5, 1, 2, 3, 4)
                .filter(i -> i > 3)
                .sorted()
                .toArray()).isEqualTo(new int[] { 4, 5 });
        assertThat(Stream.of("a", "bb", "ccc", "dddd", "eeeee")
                .filter(s -> s.length() < 4)
                .sorted()
                .toArray()).isEqualTo(new String[] { "a", "bb", "ccc" });
    }

    @Test
    public void testMap() {
        assertThat(Stream.of(1, 2, 3, 4, 5)
                .map(i -> i * 2)
                .sorted()
                .toArray()).isEqualTo(new int[] { 2, 4, 6, 8, 10 });
        assertThat(Stream.of("a", "bb", "ccc", "dddd", "eeeee")
                .map(s -> s.length())
                .sorted()
                .toArray()).isEqualTo(new int[] { 1, 2, 3, 4, 5 });
    }

    @Test
    public void testFlatMap() {
        assertThat(Stream.of(1, 2, 3)
                .flatMap(i -> IntStream.range(i * 10, i * 10 + 3).boxed())
                .sorted()
                .toArray()).isEqualTo(new int[] { 10, 11, 12, 20, 21, 22, 30, 31, 32 });
        assertThat(Stream.of("a", "bb", "ccc")
                // https://stackoverflow.com/questions/31554025/want-to-create-a-stream-of-characters-from-char-array-in-java
                .flatMap(s -> s.chars().mapToObj(i -> (char) i))
                .sorted()
                .toArray()).isEqualTo(new char[] { 'a', 'b', 'b', 'c', 'c', 'c' });
    }

    @Test
    public void testLimit() {
        final AtomicInteger counter = new AtomicInteger(0);
        final Supplier<Integer> sup0 = new Supplier<>() {
            @Override
            public Integer get() {
                return counter.incrementAndGet();
            }
        };
        assertThat(Stream.generate(sup0).limit(5).toArray()).isEqualTo(new int[] { 1, 2, 3, 4, 5 });
        assertThat(counter.get()).isEqualTo(5);
    }

    @Test
    public void testPeek() {
        final AtomicInteger counter = new AtomicInteger(0);
        final Supplier<Integer> sup0 = new Supplier<>() {
            @Override
            public Integer get() {
                return counter.incrementAndGet();
            }
        };
        final ArrayList<String> log = new ArrayList<>();
        assertThat(Stream.generate(sup0)
                .limit(5)
                .peek(i -> {
                    log.add("peek" + i);
                })
                .toArray()).isEqualTo(new int[] { 1, 2, 3, 4, 5 });
        assertThat(log).isEqualTo(List.of("peek1", "peek2", "peek3", "peek4", "peek5"));
    }

    @Test
    public void testSkip() {
        final AtomicInteger counter = new AtomicInteger(0);
        final Supplier<Integer> sup0 = new Supplier<>() {
            @Override
            public Integer get() {
                return counter.incrementAndGet();
            }
        };
        assertThat(Stream.generate(sup0)
                .limit(20)
                .skip(5)
                // 6, 7, 8, 9, 10, 11, ... 20
                .limit(10)
                // 6, 7, 8, 9, 10, 11, 12, 13, 14, 15
                .skip(5)
                .toArray()).isEqualTo(new int[] { 11, 12, 13, 14, 15 });
        assertThat(counter.get()).isEqualTo(15);
    }

    @Test
    public void testSorted() {
        assertThat(Stream.of(5, 4, 3, 2, 1)
                .sorted()
                .toArray()).isEqualTo(new int[] { 1, 2, 3, 4, 5 });
        assertThat(Stream.of(1, 2, 3, 4, 5)
                .sorted(Comparator.reverseOrder())
                .toArray()).isEqualTo(new int[] { 5, 4, 3, 2, 1 });

        final Comparator<String> cmp = Comparator.comparing(s -> s.length());
        assertThat(Stream.of("a", "bb", "ccc", "dddd", "eeeee")
                .sorted(cmp.reversed())
                .toArray()).isEqualTo(new String[] { "eeeee", "dddd", "ccc", "bb", "a" });
    }
}
