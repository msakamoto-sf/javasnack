package javasnack.functionlambdastream.fpjbook;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class TestChapter2SamplesDemo {

    @Test
    public void testForEachDemo() {
        final List<String> dst = new ArrayList<>();
        Arrays.asList("aa", "bb", "cc", "dd").stream().forEach(s -> dst.add(s));
        assertThat(dst).isEqualTo(Arrays.asList("aa", "bb", "cc", "dd"));
    }

    @Test
    public void testMapConversionDemo() {
        final List<String> dst = new ArrayList<>();
        Arrays.asList("aa", "bb", "cc", "dd").stream().map(s -> s.toUpperCase()).forEach(s -> dst.add(s));
        assertThat(dst).isEqualTo(Arrays.asList("AA", "BB", "CC", "DD"));
    }

    @Test
    public void testMapConversionDemoUsingMethodReference1() {
        final List<String> dst = new ArrayList<>();
        Arrays.asList("aa", "bb", "cc", "dd").stream().map(String::toUpperCase).forEach(s -> dst.add(s));
        assertThat(dst).isEqualTo(Arrays.asList("AA", "BB", "CC", "DD"));
    }

    @Test
    public void testFilterAndCollectorsToListDemo() {
        final List<String> r = Arrays.asList("Lucky", "Null", "Monday", "Nonce", "Octet", "November", "Near", "Large")
                .stream()
                .filter(s -> s.startsWith("N")).collect(Collectors.toList());
        assertThat(r).isEqualTo(Arrays.asList("Null", "Nonce", "November", "Near"));
    }

    private static Predicate<String> isStartWith(final String prefix) {
        return s -> s.startsWith(prefix);
    }

    @Test
    public void testClosureDemo() {
        final List<String> r = Arrays.asList("Lucky", "Null", "Monday", "Nonce", "Octet", "November", "Near", "Large")
                .stream()
                .filter(isStartWith("N")).collect(Collectors.toList());
        assertThat(r).isEqualTo(Arrays.asList("Null", "Nonce", "November", "Near"));
    }

    @Test
    public void testClosureDemo2() {
        final Function<String, Predicate<String>> startWith = (final String prefix) -> {
            return s -> s.startsWith(prefix);
        };
        final List<String> src = Arrays.asList("Lucky", "Null", "Monday", "Nonce", "Octet", "November", "Near",
                "Large", "Lounge");
        final List<String> r = src
                .stream()
                .filter(startWith.apply("N")).collect(Collectors.toList());
        assertThat(r).isEqualTo(Arrays.asList("Null", "Nonce", "November", "Near"));
        final List<String> r2 = src
                .stream()
                .filter(startWith.apply("L")).collect(Collectors.toList());
        assertThat(r2).isEqualTo(Arrays.asList("Lucky", "Large", "Lounge"));
    }

    @Test
    public void testFindFirstOptionalDemo() {
        final List<String> src = Arrays.asList("Lucky", "Null", "Monday", "Nonce", "Octet", "November", "Near",
                "Large", "Lounge");
        final Optional<String> r = src.stream().filter(s -> s.startsWith("N")).findFirst();
        assertThat(r.orElse("xx")).isEqualTo("Null");

        final Optional<String> r2 = src.stream().filter(s -> s.startsWith("X")).findFirst();
        assertThat(r2.orElse("-")).isEqualTo("-");
    }

    @Test
    public void testReductionDemo() {
        final List<String> src = Arrays.asList("Lucky", "Null", "Monday", "Nonce", "Octet", "November", "Near",
                "Large", "Lounge");
        final int sum = src.stream().mapToInt(s -> s.length()).sum();
        assertThat(sum).isEqualTo(48);
        final int max = src.stream().mapToInt(s -> s.length()).max().orElse(-1);
        assertThat(max).isEqualTo(8);
        final int min = src.stream().mapToInt(s -> s.length()).min().orElse(-1);
        assertThat(min).isEqualTo(4);
        final long count = src.stream().mapToInt(s -> s.length()).count();
        assertThat(count).isEqualTo(src.size());
        final double avg = src.stream().mapToInt(s -> s.length()).average().orElse(99.9);
        assertThat(avg).isBetween(5.32, 5.34);
        final int[] limitTo3 = src.stream().mapToInt(s -> s.length()).limit(3).toArray();
        assertThat(limitTo3).isEqualTo(new int[] { 5, 4, 6 });
        final Optional<String> r = src.stream().reduce((s1, s2) -> {
            return s1.length() >= s2.length() ? s1 : s2;
        });
        assertThat(r.orElse("xx")).isEqualTo("November");
    }

    @Test
    public void testStringJoinerDemo() {
        final List<String> src = Arrays.asList("aa", "bb", "cc");
        assertThat(String.join(", ", src)).isEqualTo("aa, bb, cc");
        final String r = src.stream().map(String::toUpperCase).collect(Collectors.joining(", "));
        assertThat(r).isEqualTo("AA, BB, CC");
    }
}
