package javasnack.ojcp.se8gold;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class Test05StreamDemo1 {

    static class StringsSupplier implements Supplier<String> {
        private final String[] strings;
        private int idx = 0;

        StringsSupplier(String[] strings) {
            this.strings = strings;
        }

        @Override
        public String get() {
            if (idx >= strings.length) {
                throw new NoSuchElementException();
                // return null; // String型のnull参照として扱われるためループは終わらない。
            }
            return strings[idx++];
        }
    }

    @Test
    public void testGenerateStreamAndToArray() {
        List<String> strings = Arrays.asList("aa", "bbb", "cccc");
        Stream<String> s1 = strings.stream();
        String[] stringarr1 = s1.toArray(String[]::new);
        assertThat(stringarr1).isEqualTo(new String[] { "aa", "bbb", "cccc" });

        // toArray() -> Object[] demo
        s1 = Arrays.stream(stringarr1);
        Object[] objects = s1.toArray();
        assertThat(objects.length).isEqualTo(3);
        assertThat(objects[0]).isEqualTo("aa");
        assertThat(objects[1]).isEqualTo("bbb");
        assertThat(objects[2]).isEqualTo("cccc");

        // toArray(IntFunction) : 配列の要素数を受け取り、T[] を生成する。
        s1 = Arrays.stream(stringarr1);
        assertThat(s1.toArray(String[]::new)).isEqualTo(stringarr1);
        // -> T[]::new という表記も可能。
        s1 = Arrays.stream(stringarr1);
        assertThat(s1.toArray(String[]::new)).isEqualTo(stringarr1);

        s1 = Stream.of("abc");
        assertThat(s1.toArray(String[]::new)).isEqualTo(new String[] { "abc" });

        s1 = Stream.of("aa", "bbb", "cccc");
        assertThat(s1.toArray(String[]::new)).isEqualTo(stringarr1);

        s1 = Stream.empty();
        assertThat(s1.toArray(String[]::new)).isEqualTo(new String[0]);

        s1 = Stream.generate(new StringsSupplier(stringarr1));
        // そのままtoArray() をしてしまうと Supplier#get() が例外をthrowするまで終わらなくなる。
        // -> Stream#limit() で要素数を制限する。
        assertThat(s1.limit(3).toArray(String[]::new)).isEqualTo(stringarr1);

        s1 = Stream.iterate("a", (String s) -> s + "a");
        assertThat(s1.limit(3).toArray(String[]::new)).isEqualTo(new String[] { "a", "aa", "aaa" });

        int[] ints = { 1, 2, 3 };
        IntStream s2 = Arrays.stream(ints);
        assertThat(s2.toArray()).isEqualTo(ints); // IntStream.toArray() は int[] を返す。

        s2 = IntStream.of(1);
        assertThat(s2.toArray()).isEqualTo(new int[] { 1 });
        s2 = IntStream.of(1, 2, 3);
        assertThat(s2.toArray()).isEqualTo(new int[] { 1, 2, 3 });
        s2 = IntStream.concat(Arrays.stream(ints), Arrays.stream(new int[] { 4, 5, 6 }));
        assertThat(s2.toArray()).isEqualTo(new int[] { 1, 2, 3, 4, 5, 6 });
        s2 = IntStream.iterate(1, i -> i + 2);
        assertThat(s2.limit(3).toArray()).isEqualTo(new int[] { 1, 3, 5 });
        s2 = IntStream.range(3, 7);
        assertThat(s2.toArray()).isEqualTo(new int[] { 3, 4, 5, 6 });
        s2 = IntStream.rangeClosed(3, 7);
        assertThat(s2.toArray()).isEqualTo(new int[] { 3, 4, 5, 6, 7 });

        long[] longs = { 1L, 2L, 3L };
        LongStream s3 = Arrays.stream(longs);
        assertThat(s3.toArray()).isEqualTo(longs); // LongStream.toArray() は long[] を返す。

        s3 = LongStream.of(1L);
        assertThat(s3.toArray()).isEqualTo(new long[] { 1L });
        s3 = LongStream.of(1L, 2L, 3L);
        assertThat(s3.toArray()).isEqualTo(new long[] { 1L, 2L, 3L });
        s3 = LongStream.concat(Arrays.stream(longs), Arrays.stream(new long[] { 4L, 5L, 6L }));
        assertThat(s3.toArray()).isEqualTo(new long[] { 1L, 2L, 3L, 4L, 5L, 6L });
        s3 = LongStream.iterate(1L, i -> i + 2L);
        assertThat(s3.limit(3).toArray()).isEqualTo(new long[] { 1L, 3L, 5L });
        s3 = LongStream.range(3L, 7L);
        assertThat(s3.toArray()).isEqualTo(new long[] { 3L, 4L, 5L, 6L });
        s3 = LongStream.rangeClosed(3L, 7L);
        assertThat(s3.toArray()).isEqualTo(new long[] { 3L, 4L, 5L, 6L, 7L });

        double[] doubles = { 1.0, 2.0, 3.0 };
        DoubleStream s4 = Arrays.stream(doubles);
        assertThat(s4.toArray()).isEqualTo(doubles); // DoubleStream.toArray() は double[] を返す。

        s4 = DoubleStream.of(1.0);
        assertThat(s4.toArray()).isEqualTo(new double[] { 1.0 });
        s4 = DoubleStream.of(1.0, 2.0, 3.0);
        assertThat(s4.toArray()).isEqualTo(new double[] { 1.0, 2.0, 3.0 });
        s4 = DoubleStream.concat(Arrays.stream(doubles), Arrays.stream(new double[] { 4.0, 5.0, 6.0 }));
        assertThat(s4.toArray()).isEqualTo(new double[] { 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 });
        s4 = DoubleStream.iterate(1.0, i -> i + 2.0);
        assertThat(s4.limit(3).toArray()).isEqualTo(new double[] { 1.0, 3.0, 5.0 });
        // DoubleStream には range 系は無い。
        //s4 = DoubleStream.range(3.0, 7.0);
        //s4 = DoubleStream.rangeClosed(3.0, 7.0);
    }

    @Test
    public void testTerminalOperationDemo1() {
        // {all|any|none}Match() demo
        List<String> strings = Arrays.asList("aa", "bbb", "cccc", "ddddd", "eeeeee");
        assertThat(strings.stream().allMatch(s -> s.length() > 1)).isTrue();
        assertThat(strings.stream().allMatch(s -> s.length() > 2)).isFalse();
        assertThat(strings.stream().allMatch(s -> s.length() < 6)).isFalse();
        assertThat(strings.stream().allMatch(s -> s.length() < 7)).isTrue();
        assertThat(strings.stream().anyMatch(s -> s.length() == 4)).isTrue();
        assertThat(strings.stream().anyMatch(s -> s.length() == 7)).isFalse();
        assertThat(strings.stream().noneMatch(s -> s.length() == 4)).isFalse();
        assertThat(strings.stream().noneMatch(s -> s.length() == 7)).isTrue();

        // NOTE: 以下のパターンでは、streamが「全て」2文字であるかチェックするため無限ループに突入。
        //assertThat(Stream.generate(() -> "aa").allMatch(s -> s.length() == 2)).isTrue();
        // NOTE: 以下のパターンであれば、すぐに否定となるため無限ループに突入することはない。
        assertThat(Stream.generate(() -> "aa").allMatch(s -> s.length() == 3)).isFalse();
        assertThat(Stream.generate(() -> "aa").noneMatch(s -> s.length() == 2)).isFalse();
        // NOTE: 以下のパターンも、すぐに2文字である要素が1つ見つかりそこで終わるため無限ループに突入することはない。
        assertThat(Stream.generate(() -> "aa").anyMatch(s -> s.length() == 2)).isTrue();

        Stream<String> s1 = strings.stream();
        assertThat(s1.anyMatch(s -> s.length() == 4)).isTrue();
        /* NOTE: 一度消費した stream は、もう一度使おうとすると IllegalStateException.
         * この例だと、「anyMatch() で == 4 のところで止まったから、==5 は動くかな？」
         * と試しているが、やはり IllegalStateException 発生。
         */
        assertThatThrownBy(() -> {
            s1.anyMatch(s -> s.length() == 5);
        }).isInstanceOf(IllegalStateException.class)
                .hasMessage("stream has already been operated upon or closed");

        // count(), forEach() demo
        assertThat(Stream.of("a", "b", "c").count()).isEqualTo(3);
        StringBuilder sb = new StringBuilder();
        Stream.of("a", "b", "c").forEach(sb::append);
        assertThat(sb.toString()).isEqualTo("abc");

        // reduce() demo
        // T identity 引数あり
        assertThat(IntStream.empty().reduce(0, (a, b) -> a + b)).isEqualTo(0);
        assertThat(IntStream.empty().reduce(1, (a, b) -> a + b)).isEqualTo(1);
        assertThat(IntStream.of(10, 20, 30).reduce(0, (a, b) -> a + b)).isEqualTo(60);
        assertThat(IntStream.of(10, 20, 30).reduce(1, (a, b) -> a + b)).isEqualTo(61);
        // T identity 引数なし
        assertThat(IntStream.empty().reduce((a, b) -> a + b)).isEqualTo(OptionalInt.empty());
        // IntStream だと OptionalInt になる。
        assertThat(IntStream.of(10, 20, 30).reduce((a, b) -> a + b).getAsInt()).isEqualTo(60);
        // Stream<Integer> だと Optional<Integer> になる。
        assertThat(Stream.of(10, 20, 30).reduce((a, b) -> a + b).get()).isEqualTo(Integer.valueOf(60));
    }

    @Test
    public void testOptionalDemos() throws Exception {
        // empty(), of(), isPresent() demo
        assertThat(Optional.empty().isPresent()).isFalse();
        assertThat(Optional.of("hello").isPresent()).isTrue();
        assertThatThrownBy(() -> {
            Optional.of(null);
        }).isInstanceOf(NullPointerException.class);

        // get() demo
        assertThat(Optional.of("hello").get()).isEqualTo("hello");
        assertThatThrownBy(() -> {
            Optional.empty().get();
        }).isInstanceOf(NoSuchElementException.class).hasMessage("No value present");

        // Optional<T> 固有, ofNullable() demo
        assertThat(Optional.ofNullable("hello").isPresent()).isTrue();
        assertThat(Optional.ofNullable("hello").get()).isEqualTo("hello");
        assertThat(Optional.ofNullable(null).isPresent()).isFalse(); // NPE は発生しない。
        assertThatThrownBy(() -> {
            Optional.ofNullable(null).get();
        }).isInstanceOf(NoSuchElementException.class).hasMessage("No value present");

        // ifPresent(Consumer) demo
        StringBuilder sb = new StringBuilder();
        Optional.empty().ifPresent(sb::append);
        Optional.of("hello").ifPresent(sb::append);
        Optional.ofNullable("hello").ifPresent(sb::append);
        Optional.ofNullable(null).ifPresent(sb::append);
        assertThat(sb.toString()).isEqualTo("hellohello");

        // orElse() demo
        assertThat(Optional.empty().orElse("aaa")).isEqualTo("aaa");
        assertThat(Optional.of("hello").orElse("aaa")).isEqualTo("hello");
        assertThat(Optional.ofNullable("hello").orElse("aaa")).isEqualTo("hello");
        assertThat(Optional.ofNullable(null).orElse("aaa")).isEqualTo("aaa");

        // orElseGet(Supplier<T>) demo
        Supplier<String> sup1 = () -> "aaa";
        assertThat(Optional.empty().orElseGet(sup1)).isEqualTo("aaa");
        assertThat(Optional.of("hello").orElseGet(sup1)).isEqualTo("hello");
        assertThat(Optional.ofNullable("hello").orElseGet(sup1)).isEqualTo("hello");
        assertThat(Optional.ofNullable(null).orElseGet(sup1)).isEqualTo("aaa");

        // orElseThrow(Supplier<? extends X(X extends Throwable)>) demo
        Supplier<Exception> sup2 = () -> new Exception("foo");
        assertThatThrownBy(() -> {
            Optional.empty().orElseThrow(sup2);
        }).isInstanceOf(Exception.class).hasMessage("foo");
        assertThat(Optional.of("hello").orElseThrow(sup2)).isEqualTo("hello");
        assertThat(Optional.ofNullable("hello").orElseThrow(sup2)).isEqualTo("hello");
        assertThatThrownBy(() -> {
            Optional.ofNullable(null).orElseThrow(sup2);
        }).isInstanceOf(Exception.class).hasMessage("foo");

        // Optional<T> 固有 : filter() demo
        // -> ちょっと意地悪 : 空に対して Objects#isNull() でfilter -> ちゃんと isPresent = false になる。
        assertThat(Optional.empty().filter(Objects::isNull).isPresent()).isFalse();
        assertThat(Optional.of("hello").filter(Objects::isNull).isPresent()).isFalse();
        assertThat(Optional.ofNullable("hello").filter(Objects::isNull).isPresent()).isFalse();
        assertThat(Optional.ofNullable(null).filter(Objects::isNull).isPresent()).isFalse();
        // 普通の使い方
        assertThat(Optional.of("hello").filter(s -> "hello".equals(s)).isPresent()).isTrue();
        assertThat(Optional.of("hello").filter(s -> "hello".equals(s)).get()).isEqualTo("hello");

        // Optional<T> 固有 : map() demo
        Optional<String> o1 = Optional.empty();
        assertThat(o1.map((String s) -> s.toUpperCase()).isPresent()).isFalse();
        assertThat(Optional.of("hello").map(s -> s.toUpperCase()).isPresent()).isTrue();
        assertThat(Optional.of("hello").map(s -> s.toUpperCase()).get()).isEqualTo("HELLO");
        assertThat(Optional.ofNullable("hello").map(s -> s.toUpperCase()).isPresent()).isTrue();
        assertThat(Optional.ofNullable("hello").map(s -> s.toUpperCase()).get()).isEqualTo("HELLO");
        o1 = Optional.ofNullable(null);
        assertThat(o1.map(s -> s.toUpperCase()).isPresent()).isFalse();

        // Optional<T> 固有 : flatMap() demo
        Function<String, Optional<String>> map1 = s -> Optional.of(s.toUpperCase());
        o1 = Optional.empty();
        assertThat(o1.flatMap(map1).isPresent()).isFalse();
        assertThat(Optional.of("hello").flatMap(map1).isPresent()).isTrue();
        assertThat(Optional.of("hello").flatMap(map1).get()).isEqualTo("HELLO");
        assertThat(Optional.ofNullable("hello").flatMap(map1).isPresent()).isTrue();
        assertThat(Optional.ofNullable("hello").flatMap(map1).get()).isEqualTo("HELLO");
        o1 = Optional.ofNullable(null);
        assertThat(o1.flatMap(map1).isPresent()).isFalse();

        /* Optional{Int|Long|Double} demo
         * get() -> getAs{Int|Long|Double}()
         * ofNullable()/filter()/map()/flatMap()は未定義
         */
        assertThat(OptionalInt.empty().isPresent()).isFalse();
        assertThat(OptionalInt.of(10).isPresent()).isTrue();
        assertThat(OptionalInt.of(10).getAsInt()).isEqualTo(10);
        assertThat(OptionalLong.empty().isPresent()).isFalse();
        assertThat(OptionalLong.of(10L).isPresent()).isTrue();
        assertThat(OptionalLong.of(10L).getAsLong()).isEqualTo(10L);
        assertThat(OptionalDouble.empty().isPresent()).isFalse();
        assertThat(OptionalDouble.of(10.0).isPresent()).isTrue();
        assertThat(OptionalDouble.of(10.0).getAsDouble()).isEqualTo(10.0);
    }

    @Test
    public void testTerminalOperationDemo2() {
        // max() / min() demo
        Optional<String> o1 = Arrays.stream(new String[0]).max(Comparator.naturalOrder());
        assertThat(o1.isPresent()).isFalse();
        o1 = Arrays.stream(new String[0]).min(Comparator.naturalOrder());
        assertThat(o1.isPresent()).isFalse();

        List<String> strings = Arrays.asList("aaa", "bb", "c", "aaa", "bb", "c");
        o1 = strings.stream().max(Comparator.naturalOrder());
        assertThat(o1.isPresent()).isTrue();
        assertThat(o1.get()).isEqualTo("c");
        o1 = strings.stream().min(Comparator.naturalOrder());
        assertThat(o1.isPresent()).isTrue();
        assertThat(o1.get()).isEqualTo("aaa");

        o1 = strings.stream().max((d1, d2) -> d1.length() - d2.length());
        assertThat(o1.isPresent()).isTrue();
        assertThat(o1.get()).isEqualTo("aaa");
        o1 = strings.stream().min((d1, d2) -> d1.length() - d2.length());
        assertThat(o1.isPresent()).isTrue();
        assertThat(o1.get()).isEqualTo("c");

        // {Int|Long|Double}Stream#average(), sum() demo
        // average() の戻り値はそれぞれ Optional{Int|Long|Double}
        OptionalDouble od1 = IntStream.of(1, 2, 3).average();
        assertThat(od1.isPresent()).isTrue();
        assertThat(od1.getAsDouble()).isEqualTo(2.0);
        od1 = LongStream.of(1L, 2L, 3L).average();
        assertThat(od1.isPresent()).isTrue();
        assertThat(od1.getAsDouble()).isEqualTo(2.0);
        od1 = DoubleStream.of(1.0, 2.0, 3.0).average();
        assertThat(od1.isPresent()).isTrue();
        assertThat(od1.getAsDouble()).isEqualTo(2.0);
        od1 = IntStream.empty().average();
        assertThat(od1.isPresent()).isFalse();
        od1 = LongStream.empty().average();
        assertThat(od1.isPresent()).isFalse();
        od1 = DoubleStream.empty().average();
        assertThat(od1.isPresent()).isFalse();
        // sum() の戻り値はそれぞれ {int|long|double}
        assertThat(IntStream.empty().sum()).isEqualTo(0);
        assertThat(LongStream.empty().sum()).isEqualTo(0L);
        assertThat(DoubleStream.empty().sum()).isEqualTo(0.0);

        // findFirst() demo
        o1 = Stream.<String>empty().findFirst();
        assertThat(o1.isPresent()).isFalse();
        o1 = strings.stream().findFirst();
        assertThat(o1.isPresent()).isTrue();
        assertThat(o1.get()).isEqualTo("aaa");

        OptionalInt oi1 = IntStream.empty().findFirst();
        assertThat(oi1.isPresent()).isFalse();
        oi1 = IntStream.of(1, 2, 3).findFirst();
        assertThat(oi1.isPresent()).isTrue();
        assertThat(oi1.getAsInt()).isEqualTo(1);

        OptionalLong ol1 = LongStream.empty().findFirst();
        assertThat(ol1.isPresent()).isFalse();
        ol1 = LongStream.of(3L, 2L, 1L).findFirst();
        assertThat(ol1.isPresent()).isTrue();
        assertThat(ol1.getAsLong()).isEqualTo(3L);

        od1 = DoubleStream.empty().findFirst();
        assertThat(od1.isPresent()).isFalse();
        od1 = DoubleStream.of(2.0, 3.0, 1.0).findFirst();
        assertThat(od1.isPresent()).isTrue();
        assertThat(od1.getAsDouble()).isEqualTo(2.0);

        // findAny() demo
        o1 = Stream.<String>empty().findAny();
        assertThat(o1.isPresent()).isFalse();
        o1 = strings.stream().findAny();
        assertThat(o1.isPresent()).isTrue();
        assertThat(strings.contains(o1.get())).isTrue();

        oi1 = IntStream.empty().findAny();
        assertThat(oi1.isPresent()).isFalse();
        oi1 = IntStream.of(1, 2, 3).findAny();
        assertThat(oi1.isPresent()).isTrue();
        assertThat(oi1.getAsInt()).isIn(1, 2, 3);

        ol1 = LongStream.empty().findAny();
        assertThat(ol1.isPresent()).isFalse();
        ol1 = LongStream.of(3L, 2L, 1L).findAny();
        assertThat(ol1.isPresent()).isTrue();
        assertThat(ol1.getAsLong()).isIn(1L, 2L, 3L);

        od1 = DoubleStream.empty().findAny();
        assertThat(od1.isPresent()).isFalse();
        od1 = DoubleStream.of(2.0, 3.0, 1.0).findAny();
        assertThat(od1.isPresent()).isTrue();
        assertThat(od1.getAsDouble()).isIn(1.0, 2.0, 3.0);
    }

    @Test
    public void testIntermediateOperationDemo() {
        // filter()
        assertThat(Stream.of("aaa", "bbb", "ccc", "abb")
                .filter(s -> s.startsWith("a"))
                .toArray(String[]::new)).isEqualTo(new String[] { "aaa", "abb" });

        // distinct()
        assertThat(Stream.of("aa", "bb", "cc", "aa", "bb")
                .distinct()
                .toArray(String[]::new)).isEqualTo(new String[] { "aa", "bb", "cc" });

        // skip(), limit()
        assertThat(IntStream.iterate(1, x -> x + 1)
                .limit(3)
                .toArray()).isEqualTo(new int[] { 1, 2, 3 });
        assertThat(IntStream.iterate(1, x -> x + 1)
                .skip(3)
                .limit(3)
                .toArray()).isEqualTo(new int[] { 4, 5, 6 });
        assertThat(IntStream.iterate(1, x -> x + 1)
                .skip(2)
                .limit(10)
                .skip(3)
                .limit(4)
                .toArray()).isEqualTo(new int[] { 6, 7, 8, 9 });
        assertThat(Stream.generate(() -> "hello")
                .limit(3)
                .toArray(String[]::new)).isEqualTo(new String[] { "hello", "hello", "hello" });

        // map()
        assertThat(Stream.of("aa", "bb", "cc")
                .map(s -> s.toUpperCase())
                .toArray(String[]::new)).isEqualTo(new String[] { "AA", "BB", "CC" });
        assertThat(Stream.of("aaa", "bb", "c")
                .map(s -> s.length())
                // jdk8-11
                //.toArray(Integer[]::new)).isEqualTo(new int[] { 3, 2, 1 });
                // jdk21
                .toArray(Integer[]::new)).isEqualTo(new Integer[] { 3, 2, 1 });
        assertThat(IntStream.of(1, 2, 3)
                .map(n -> n * 10)
                .toArray()).isEqualTo(new int[] { 10, 20, 30 });

        // flatMap() : 単一要素(何かしらのcollection)をstreamに変換し、それを結合した stream を生成する。
        List<String> strings1 = Arrays.asList("aa");
        List<String> strings2 = Arrays.asList("bb", "cc");
        List<String> strings3 = Arrays.asList("dd", "ee", "ff");
        assertThat(Stream.of(strings1, strings2, strings3)
                .flatMap(strings -> strings.stream())
                .map(s -> s.toUpperCase())
                .toArray(String[]::new)).isEqualTo(new String[] { "AA", "BB", "CC", "DD", "EE", "FF" });

        // sorted()
        assertThat(Stream.of("bb", "aa", "cc")
                .sorted()
                .toArray(String[]::new)).isEqualTo(new String[] { "aa", "bb", "cc" });
        assertThat(Stream.of("bb", "aa", "cc")
                .sorted(Comparator.reverseOrder())
                .toArray(String[]::new)).isEqualTo(new String[] { "cc", "bb", "aa" });

        // peek()
        StringBuilder sb = new StringBuilder();
        assertThat(Stream.of("one", "three", "two", "three", "four")
                .filter(s -> s.length() > 3)
                .peek(e -> sb.append("p1[" + e + "]"))
                .distinct()
                .map(String::toUpperCase)
                .peek(e -> sb.append("p2[" + e + "]"))
                .toArray(String[]::new)).isEqualTo(new String[] { "THREE", "FOUR" });
        assertThat(sb.toString()).isEqualTo("p1[three]p2[THREE]p1[three]p1[four]p2[FOUR]");
    }

    @Test
    public void testStreamTypeConversion() {
        /* typical triangle conversion
         * Stream<String> <---> Stream<Integer>
         * Stream<Integer> <---> IntStream
         * IntStream <--> Stream<String>
         */

        Stream<String> ss1 = Stream.of("a", "bb", "ccc");
        // Stream<String> -> Stream<Integer> 
        Stream<Integer> si1 = ss1.map(s -> s.length());
        // Stream<Integer> -> IntStream
        IntStream is1 = si1.mapToInt(n -> n);
        // IntStream -> Stream<String>
        Stream<String> ss2 = is1.mapToObj(n -> n + "a");
        assertThat(ss2.toArray(String[]::new)).isEqualTo(new String[] { "1a", "2a", "3a" });

        Stream<String> ss3 = Stream.of("a", "bb", "ccc");
        // Stream<String> -> IntStream
        IntStream is2 = ss3.mapToInt(s -> s.length());
        // IntStream -> Stream<Integer> by boxed()
        Stream<Integer> si2 = is2.boxed();
        // jdk8-11
        // assertThat(si2.toArray(Integer[]::new)).isEqualTo(new int[] { 1, 2, 3 });
        // jdk21
        assertThat(si2.toArray(Integer[]::new)).isEqualTo(new Integer[] { 1, 2, 3 });

        // Stream<Integer> -> Stream<String>
        Stream<Integer> si3 = Stream.of(1, 2, 3);
        Stream<String> ss4 = si3.map(n -> n + "a");
        assertThat(ss4.toArray(String[]::new)).isEqualTo(new String[] { "1a", "2a", "3a" });

        // Stream<Object>#mapTo{Int|Long|Double}() --> {Int|Long|Double}Stream
        assertThat(Stream.of("a", "bb", "ccc")
                .mapToInt(s -> s.length())
                .toArray()).isEqualTo(new int[] { 1, 2, 3 });
        assertThat(Stream.of("a", "bb", "ccc")
                .mapToLong(s -> (long) s.length())
                .toArray()).isEqualTo(new long[] { 1L, 2L, 3L });
        assertThat(Stream.of("a", "bb", "ccc")
                .mapToDouble(s -> (double) s.length())
                .toArray()).isEqualTo(new double[] { 1.0, 2.0, 3.0 });

        // {Int|Long|Double}Stream#mapToObj() -> Stream<Object>
        assertThat(IntStream.of(1, 2, 3)
                .mapToObj(n -> n + "a")
                .toArray(String[]::new)).isEqualTo(new String[] { "1a", "2a", "3a" });
        assertThat(LongStream.of(1L, 2L, 3L)
                .mapToObj(n -> n + "L")
                .toArray(String[]::new)).isEqualTo(new String[] { "1L", "2L", "3L" });
        assertThat(DoubleStream.of(1.1, 2.2, 3.3)
                .mapToObj(n -> n + "d")
                .toArray(String[]::new)).isEqualTo(new String[] { "1.1d", "2.2d", "3.3d" });

        // {Int|Long|Double}Stream#mapTo{Int|Long|Double} -> {Int|Long|Double}Stream
        // IntStream#mapToInt() は未定義
        // LongStream#mapToLong() は未定義
        // DoubleStream#mapToDouble() は未定義
        assertThat(IntStream.of(1, 2, 3)
                .mapToLong(x -> x + 1)
                .toArray()).isEqualTo(new long[] { 2L, 3L, 4L });
        assertThat(IntStream.of(1, 2, 3)
                .mapToDouble(x -> x + 0.1)
                .toArray()).isEqualTo(new double[] { 1.1, 2.1, 3.1 });
        assertThat(LongStream.of(1L, 2L, 3L)
                .mapToInt(x -> (int) x + 1)
                .toArray()).isEqualTo(new int[] { 2, 3, 4 });
        assertThat(LongStream.of(1, 2, 3)
                .mapToDouble(x -> x + 0.1)
                .toArray()).isEqualTo(new double[] { 1.1, 2.1, 3.1 });
        assertThat(DoubleStream.of(1.1, 2.2, 3.3)
                .mapToInt(x -> (int) x + 1)
                .toArray()).isEqualTo(new int[] { 2, 3, 4 });
        assertThat(DoubleStream.of(1.1, 2.2, 3.3)
                .mapToLong(x -> (long) x + 1)
                .toArray()).isEqualTo(new long[] { 2L, 3L, 4L });

        // {Int|Long|Double}Stream#boxed() -> Stream<{Integer|Long|Double}>
        assertThat(IntStream.of(1, 2, 3)
                .boxed()
                // jdk8-11
                //.toArray(Integer[]::new)).isEqualTo(new int[] { 1, 2, 3 });
                // jdk21
                .toArray(Integer[]::new)).isEqualTo(new Integer[] { 1, 2, 3 });
        assertThat(LongStream.of(1L, 2L, 3L)
                .boxed()
                // jdk8-11
                //.toArray(Long[]::new)).isEqualTo(new long[] { 1L, 2L, 3L });
                // jdk21
                .toArray(Long[]::new)).isEqualTo(new Long[] { 1L, 2L, 3L });
        assertThat(DoubleStream.of(1.0, 2.0, 3.0)
                .boxed()
                // jdk8-11
                //.toArray(Double[]::new)).isEqualTo(new double[] { 1.0, 2.0, 3.0 });
                // jdk21
                .toArray(Double[]::new)).isEqualTo(new Double[] { 1.0, 2.0, 3.0 });

        // IntStream#as{Long|Double}Stream() -> {Long|Double}Stream
        assertThat(IntStream.of(1, 2, 3)
                .asLongStream()
                .toArray()).isEqualTo(new long[] { 1L, 2L, 3L });
        assertThat(IntStream.of(1, 2, 3)
                .asDoubleStream()
                .toArray()).isEqualTo(new double[] { 1.0, 2.0, 3.0 });
        // LongStream#asDoubleStream() -> DoubleStream
        assertThat(LongStream.of(1L, 2L, 3L)
                .asDoubleStream()
                .toArray()).isEqualTo(new double[] { 1.0, 2.0, 3.0 });
    }
}
