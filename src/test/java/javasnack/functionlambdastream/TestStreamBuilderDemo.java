package javasnack.functionlambdastream;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import org.junit.jupiter.api.Test;

public class TestStreamBuilderDemo {

    @Test
    public void testStreamBuilder() {
        final Builder<String> b = Stream.builder();
        final List<String> r = b.add("aaa").add("bbb").add("ccc").add("ddd")
                .build()
                .collect(Collectors.toList());
        assertThat(r).isEqualTo(List.of("aaa", "bbb", "ccc", "ddd"));
    }
}
