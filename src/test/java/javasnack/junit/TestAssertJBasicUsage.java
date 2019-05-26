package javasnack.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import org.junit.jupiter.api.Test;

import lombok.Data;

/**
 * @see https://joel-costigliola.github.io/assertj/
 * @see https://assertj.github.io/doc/
 * @see https://qiita.com/opengl-8080/items/b07307ab0d33422be9c5
 * @see https://qiita.com/disc99/items/31fa7abb724f63602dc9
 * @see https://www.casleyconsulting.co.jp/blog/engineer/960/
 */
public class TestAssertJBasicUsage {

    @Data
    static class Foo {
        public String name;
        public int age;
    }

    @Test
    public void testSimpleAssertions() {
        assertThat("hello").isEqualTo("hello");
        assertThat(new byte[] { 1, 2, 3 }).isEqualTo(new byte[] { 1, 2, 3 });

        assertThat("hello").isNotEqualTo("world");
        assertThat(new byte[] { 1, 2, 3 }).isNotEqualTo(new byte[] { 4, 5, 6 });

        final var f1 = new Foo();
        f1.name = "abc";
        f1.age = 10;
        final var f2 = new Foo();
        f2.name = "abc";
        f2.age = 10;
        assertThat(f1).isEqualTo(f2);
        assertThat(f1).isNotSameAs(f2);
    }

    static class FooException extends Exception {
        private static final long serialVersionUID = 1L;
        int line;
        int column;

        public FooException(String msg, int line, int column) {
            super(msg);
            this.line = line;
            this.column = column;
        }
    }

    @Test
    public void testExceptionCatcher() {
        final String[] arr0 = { "foo", "bar", "baz" };

        // throw:
        final Throwable thrown0 = catchThrowable(() -> System.out.println("dummy output:" + arr0[9]));
        assertThat(thrown0).isInstanceOf(ArrayIndexOutOfBoundsException.class).hasMessageContaining("9");

        // don't throw:
        final Throwable thrown1 = catchThrowable(() -> System.out.println("dummy output:" + arr0[0] + arr0[1]));
        assertThat(thrown1).isNull();

        // catch specific exception type:
        FooException textException = catchThrowableOfType(() -> {
            throw new FooException("abc def", 1, 2);
        }, FooException.class);
        assertThat(textException).hasMessageContaining("abc");
        assertThat(textException).hasMessageContaining("def");
        assertThat(textException).hasMessage("%s %s", "abc", "def");
        assertThat(textException.line).isEqualTo(1);
        assertThat(textException.column).isEqualTo(2);

        // don't throw:
        assertThat(catchThrowableOfType(() -> {
        }, Exception.class)).isNull();

        // throw:
        assertThatThrownBy(() -> {
            throw new FooException("abc def", 10, 20);
        }).isInstanceOf(FooException.class)
                .hasMessageContaining("abc")
                .hasMessage("%s %s", "abc", "def");
    }
}
