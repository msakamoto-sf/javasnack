/*
 * Copyright 2019 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
package javasnack.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.Assertions.tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import lombok.Data;

/* see:
 * https://joel-costigliola.github.io/assertj/
 * https://assertj.github.io/doc/
 * https://qiita.com/opengl-8080/items/b07307ab0d33422be9c5
 * https://qiita.com/disc99/items/31fa7abb724f63602dc9
 * https://www.casleyconsulting.co.jp/blog/engineer/960/
 */
public class TestAssertJBasicUsage {

    @Data
    public static class Foo {
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

    @Test
    public void testStringSpecificAssertions() {
        assertThat("foo bar baz abc def ghi")
                .contains("abc")
                .contains("bar", "ghi")
                .containsIgnoringCase("ABC")
                .containsOnlyOnce("baz")
                .containsPattern(Pattern.compile("abc"))
                .containsSequence("abc", " def ", "ghi")
                .doesNotContain("ABC")
                .doesNotContainOnlyWhitespaces()
                .doesNotContainPattern(Pattern.compile("xyz"))
                .startsWith("foo")
                .endsWith("ghi")
                .doesNotStartWith("ghi")
                .doesNotEndWith("foo");

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat("").hasLineCount(0);
        softly.assertThat("\r").hasLineCount(1);
        softly.assertThat("\n").hasLineCount(1);
        softly.assertThat("\r\n").hasLineCount(1);
        softly.assertThat("abc").hasLineCount(1);
        softly.assertThat("abc\r").hasLineCount(1);
        softly.assertThat("abc\n").hasLineCount(1);
        softly.assertThat("abc\r\n").hasLineCount(1);
        softly.assertThat("abc\ndef").hasLineCount(2);
        softly.assertThat("abc\rdef").hasLineCount(2);
        softly.assertThat("abc\r\ndef").hasLineCount(2);
        softly.assertAll();

        assertThat("aaa").isEqualToIgnoringCase("Aaa");
        assertThat("Aaa").isEqualToIgnoringCase("aaa");
        //assertThat("\ra\nb\r\nc").isEqualToIgnoringNewLines("abc"); // -> fail
        assertThat("abc").isEqualToIgnoringNewLines("a\nbc");
        //assertThat("a\rbc").isEqualToIgnoringNewLines("a\nbc"); // -> fail
        assertThat("a\nbc").isEqualToIgnoringNewLines("abc");
        assertThat("abc").isEqualToIgnoringNewLines("a\r\nbc");
        assertThat("ab\r\nc").isEqualToIgnoringNewLines("abc");
        assertThat("\r\na\nbc").isEqualToIgnoringNewLines("abc");
        assertThat("abc").isEqualToIgnoringNewLines("a\r\nbc\n");
        assertThat("\na\nb\nc\n").isEqualToIgnoringNewLines("\r\na\r\nb\r\nc\r\n");
        assertThat("\r\na\r\nb\r\nc\r\n").isEqualToIgnoringNewLines("\na\nb\nc\n");
        assertThat("abc").isEqualToIgnoringWhitespace(" a\tb c");
        assertThat("abc").isEqualToIgnoringWhitespace(" a\nb c");
        assertThat("abc").isEqualToIgnoringWhitespace(" a\rb c");
        assertThat("abc").isEqualToIgnoringWhitespace(" a\r\nb c");
        assertThat(" a\tb c").isEqualToIgnoringWhitespace("abc");
        assertThat(" a\nb c").isEqualToIgnoringWhitespace("abc");
        assertThat(" a\rb c").isEqualToIgnoringWhitespace("abc");
        assertThat(" a\r\nb c").isEqualToIgnoringWhitespace("abc");
        assertThat("a\rb\nc\t").isEqualToIgnoringWhitespace(" a\tb c");
        assertThat("a\rb\nc\t").isEqualToIgnoringWhitespace(" a\nb c");
        assertThat("a\rb\nc\t").isEqualToIgnoringWhitespace(" a\rb c");
        assertThat("a\rb\nc\t").isEqualToIgnoringWhitespace(" a\r\nb c");
        assertThat("a\nb\nc").isEqualToNormalizingNewlines("a\r\nb\r\nc");
        assertThat("a\r\nb\r\nc").isEqualToNormalizingNewlines("a\r\nb\r\nc");
        assertThat("a\r\nb\r\nc").isEqualToNormalizingNewlines("a\nb\nc");
        //assertThat("a\rb\r\nc\n").isEqualToNormalizingNewlines("abc"); // -> fail
        //assertThat("abc").isEqualToNormalizingNewlines("a\rb\r\nc\n"); // -> fail
        assertThat("\ra\nb\r\nc").isEqualToNormalizingNewlines("\ra\nb\r\nc");
        //assertThat("\ra\nb\r\nc").isEqualToNormalizingNewlines("\r\na\r\nb\nc"); // -> fail
        //assertThat("\ra\nb\r\nc").isEqualToNormalizingNewlines("\na\nb\nc"); // -> fail
        assertThat("a b c").isEqualToNormalizingWhitespace(" a b c\t");
        assertThat("a b c").isEqualToNormalizingWhitespace("a  b\t\tc");
        assertThat("a  b\t\tc").isEqualToNormalizingWhitespace("a\t\tb  c");
        assertThat("a \t b\t \tc").isEqualToNormalizingWhitespace("a\tb c");
        assertThat("a b\tc").isEqualToNormalizingWhitespace("a\t \tb \t c");
        assertThat(" a b c ").isEqualToNormalizingWhitespace("a b c");
        //assertThat(" a b c ").isEqualToNormalizingWhitespace("abc"); // -> fail
        //assertThat(" abc ").isEqualToNormalizingWhitespace("a b c"); // -> fail
        assertThat("a\r\nb\t  c").isEqualToNormalizingWhitespace(" a\r\nb c");
    }

    @Test
    public void testCollectionSpecificAssertions() {
        assertThat(new int[0]).hasSize(0).isEmpty();
        assertThat(new int[] { 1, 2, 3 })
                .hasSize(3)
                .hasSizeBetween(2, 4)
                .hasSizeGreaterThan(2)
                .hasSizeLessThan(4);
        assertThat(new ArrayList<String>()).hasSize(0).isEmpty();
        assertThat(List.of("abc", "def", "ghi"))
                .hasSize(3)
                .hasSizeBetween(2, 4)
                .hasSizeGreaterThan(2)
                .hasSizeLessThan(4)
                .isEqualTo(List.of("abc", "def", "ghi"));
        assertThat(new HashMap<String, String>()).hasSize(0).isEmpty();
        assertThat(Map.of("k0", "v0", "k1", "v1", "k2", "v2"))
                .hasSize(3)
                .hasSizeBetween(2, 4)
                .hasSizeGreaterThan(2)
                .hasSizeLessThan(4)
                .isEqualTo(Map.of("k0", "v0", "k1", "v1", "k2", "v2"));
        assertThat(new int[] { 1, 2, 3, 3 })
                .isEqualTo(new int[] { 1, 2, 3, 3 })
                .isNotEmpty()
                .isNotNull()
                .isNotSameAs(new int[] { 1, 2, 3, 3 })
                .contains(1, 3)
                .contains(3, 1)
                .containsAnyOf(1, 3, 5, 7)
                .containsSequence(1, 2)
                .containsSequence(2, 3)
                .containsExactly(1, 2, 3, 3)
                .containsOnly(1, 3, 2)
                .containsOnlyOnce(1)
                .containsSequence(1, 2)
                .containsSequence(3, 3)
                .doesNotContain(4, 5, 6);
    }

    @Data
    public static class Bar {
        private final String name;
        private final int age;
    }

    @Test
    public void testStreamingFeatures() {
        final List<Bar> l0 = List.of(
                new Bar("abc", 10),
                new Bar("def", 15),
                new Bar("ghi", 20),
                new Bar("jkl", 25),
                new Bar("mno", 30));
        assertThat(l0).contains(new Bar("abc", 10));
        assertThat(l0).extracting("name").containsExactly("abc", "def", "ghi", "jkl", "mno");
        assertThat(l0).extracting("name", "age").contains(tuple("abc", 10), tuple("jkl", 25));
        assertThat(l0).extracting(b -> b.getName()).contains("abc", "ghi", "mno");
        assertThat(l0)
                .filteredOn(b -> {
                    return b.getAge() > 15;
                })
                .extracting(b -> b.getName())
                .containsExactly("ghi", "jkl", "mno");

        final Map<String, Bar> m0 = Map.of(
                "k-abc", new Bar("abc", 10),
                "k-def", new Bar("def", 15),
                "k-ghi", new Bar("ghi", 20),
                "k-jkl", new Bar("jkl", 25),
                "k-mno", new Bar("mno", 30));
        assertThat(m0)
                .contains(Map.entry("k-abc", new Bar("abc", 10)))
                .containsAnyOf(
                        Map.entry("k-def", new Bar("def", 15)),
                        Map.entry("k-xyz", new Bar("xyz", 100)))
                .containsEntry("k-mno", new Bar("mno", 30))
                .containsKey("k-abc")
                .containsKeys("k-abc", "k-jkl", "k-def")
                .containsValue(new Bar("def", 15))
                .containsValues(new Bar("def", 15), new Bar("jkl", 25))
                .extracting("k-abc", "k-ghi")
                .containsExactly(
                        new Bar("abc", 10), new Bar("ghi", 20));
    }
}
