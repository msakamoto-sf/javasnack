package javasnack.functionlambdastream;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

/* see:
 * https://www.geeksforgeeks.org/currying-functions-in-java-with-examples/
 * https://www.baeldung.com/java-currying
 * https://qiita.com/yoshi389111/items/4e40c026f6af364d219a
 * https://stackoverflow.com/questions/6134278/does-java-support-currying
 */
public class TestFunctionCurryingDemo {

    static String concat(final String s1, final String s2, final String s3) {
        return s1 + ":" + s2 + ":" + s3;
    }

    static BiFunction<String, String, String> createConcat1(final String s1) {
        return (s2, s3) -> {
            return concat(s1, s2, s3);
        };
    }

    static Function<String, String> createConcat2(final String s1, final String s2) {
        return (s3) -> {
            return concat(s1, s2, s3);
        };
    }

    @Test
    public void testFunctionCurryingDemo() {
        final BiFunction<String, String, String> f1 = createConcat1("aaa");
        assertThat(f1.apply("bbb", "ccc")).isEqualTo("aaa:bbb:ccc");

        final BiFunction<String, String, String> f2 = createConcat1("AAA");
        assertThat(f2.apply("bbb", "ccc")).isEqualTo("AAA:bbb:ccc");

        final Function<String, String> f3 = createConcat2("xxx", "yyy");
        assertThat(f3.apply("zzz")).isEqualTo("xxx:yyy:zzz");

        final Function<String, String> f4 = createConcat2("XXX", "YYY");
        assertThat(f4.apply("zzz")).isEqualTo("XXX:YYY:zzz");
    }
}
