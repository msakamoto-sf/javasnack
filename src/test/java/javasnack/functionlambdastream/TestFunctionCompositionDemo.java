package javasnack.functionlambdastream;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

/* see:
 * https://en.wikipedia.org/wiki/Function_composition
 * https://ja.wikipedia.org/wiki/%E5%86%99%E5%83%8F%E3%81%AE%E5%90%88%E6%88%90
 * https://stackoverflow.com/questions/19834611/how-to-do-function-composition
 */
public class TestFunctionCompositionDemo {

    @Test
    public void testFunctionComposition() {
        final Function<String, Integer> s2i = String::length;
        final Function<Integer, String> i2s = i -> i.toString();

        final Function<String, String> f1 = s2i.andThen(i2s);
        assertThat(f1.apply("hello")).isEqualTo("5");

        final Function<String, String> f2 = i2s.compose(s2i);
        assertThat(f2.apply("hello")).isEqualTo("5");

        final Function<Integer, Integer> f3 = i2s.andThen(s2i);
        assertThat(f3.apply(12345)).isEqualTo(5);

        final Function<Integer, Integer> f4 = s2i.compose(i2s);
        assertThat(f4.apply(12345)).isEqualTo(5);
    }

    @Test
    public void testConsumerComposition() {
        final StringBuilder sb = new StringBuilder();
        final Consumer<String> c1 = s -> {
            sb.append("xxx");
            sb.append(s);
        };
        final Consumer<String> c2 = s -> {
            sb.append("yyy");
            sb.append(s);
        };
        final Consumer<String> c3 = c1.andThen(c2);
        c3.accept("aaa");
        assertThat(sb.toString()).isEqualTo("xxxaaayyyaaa");
        final Consumer<String> c4 = c2.andThen(c1);
        c4.accept("bbb");
        assertThat(sb.toString()).isEqualTo("xxxaaayyyaaayyybbbxxxbbb");
    }

    @Test
    public void testPredicateComposition() {
        final List<String> log = new ArrayList<>();
        final Predicate<Integer> ilt10 = i -> {
            log.add("<10");
            return i < 10;
        };
        final Predicate<Integer> igt1 = i -> {
            log.add(">1");
            return i > 1;
        };
        final Predicate<Integer> ilt5 = i -> {
            log.add("<5");
            return i < 5;
        };
        final Predicate<Integer> igt3 = i -> {
            log.add(">3");
            return i > 3;
        };

        final Predicate<Integer> p1 = ilt10.and(igt1);
        assertThat(p1.test(1)).isFalse();
        assertThat(log).isEqualTo(List.of("<10", ">1"));
        log.clear();
        assertThat(p1.test(2)).isTrue();
        assertThat(log).isEqualTo(List.of("<10", ">1"));
        log.clear();
        assertThat(p1.test(9)).isTrue();
        assertThat(log).isEqualTo(List.of("<10", ">1"));
        log.clear();
        assertThat(p1.test(10)).isFalse();
        assertThat(log).isEqualTo(List.of("<10"));
        log.clear();

        final Predicate<Integer> p2 = igt3.or(ilt5);
        assertThat(p2.test(3)).isTrue();
        assertThat(log).isEqualTo(List.of(">3", "<5"));
        log.clear();
        assertThat(p2.test(4)).isTrue();
        assertThat(log).isEqualTo(List.of(">3"));
        log.clear();

        final Predicate<Integer> p3 = igt3.negate();
        assertThat(p3.test(1)).isTrue();
        assertThat(log).isEqualTo(List.of(">3"));
        log.clear();
    }
}
