package javasnack.ojcp.se8silver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.Test;

public class Test12ApiDemoCollectionLambda {
    @Test
    public void testFunctionalInterfaceBasics() {
        String str = new Function<String, String>() {
            @Override
            public String apply(String t) {
                return t.toUpperCase();
            }
        }.apply("hello");
        assertThat(str).isEqualTo("HELLO");

        // ラムダ式の省略のしかた色々
        Function<String, String> fss = (String s) -> {
            return s.toUpperCase();
        };
        assertThat(fss.apply("hello")).isEqualTo("HELLO");

        // 引数の型を省略
        fss = (s) -> {
            return s.toUpperCase();
        };
        assertThat(fss.apply("hello")).isEqualTo("HELLO");

        // 引数の括弧と処理ブロクの brace {} も省略
        fss = s -> s.toUpperCase();
        assertThat(fss.apply("hello")).isEqualTo("HELLO");

        // 引数無し
        Supplier<String> sup1 = () -> {
            return "hello";
        };
        assertThat(sup1.get()).isEqualTo("hello");
    }

    @Test
    public void testCollectionApiDemo() {
        List<String> strings = new ArrayList<>(List.of("aa", "bb"));
        strings.replaceAll(new UnaryOperator<String>() {
            @Override
            public String apply(String t) {
                return t.toUpperCase();
            }
        });
        assertThat(strings.get(0)).isEqualTo("AA");
        assertThat(strings.get(1)).isEqualTo("BB");

        // 省略記法
        strings.replaceAll(s -> s.toLowerCase());
        assertThat(strings.get(0)).isEqualTo("aa");
        assertThat(strings.get(1)).isEqualTo("bb");

        List<Integer> ints = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        boolean r = ints.removeIf(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t) {
                return t % 2 != 0;
            }
        });
        assertThat(r).isTrue();
        assertThat(ints.size()).isEqualTo(5);
        assertThat(ints.get(0)).isEqualTo(2);
        assertThat(ints.get(1)).isEqualTo(4);
        assertThat(ints.get(2)).isEqualTo(6);
        assertThat(ints.get(3)).isEqualTo(8);
        assertThat(ints.get(4)).isEqualTo(10);

        // 省略記法
        ints.removeIf(i -> i > 5);
        assertThat(ints.size()).isEqualTo(2);
        assertThat(ints.get(0)).isEqualTo(2);
        assertThat(ints.get(1)).isEqualTo(4);

        r = ints.removeIf(i -> i > 5);
        assertThat(r).isFalse(); // 何も変更無し
        assertThat(ints.size()).isEqualTo(2);
        assertThat(ints.get(0)).isEqualTo(2);
        assertThat(ints.get(1)).isEqualTo(4);

        final List<Integer> ints2 = List.of(1, 2, 3);
        assertThatThrownBy(() -> {
            ints2.removeIf(i -> i < 5);
        }).isInstanceOf(UnsupportedOperationException.class);
    }
}
