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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* see:
 * https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests
 * http://www.ne.jp/asahi/hishidama/home/tech/java/junit/5/ParameterizedTest.html
 */
public class TestJUnit5ParameterizedTests {

    private static final Logger LOG = LoggerFactory.getLogger(TestJUnit5ParameterizedTests.class);

    private static final String MYUUID = UUID.randomUUID().toString();

    private final String instanceUUID = UUID.randomUUID().toString();

    static {
        LOG.info("class loading static block, MYUUID={}", MYUUID);
    }

    public TestJUnit5ParameterizedTests() {
        LOG.info("constructor block, instanceUUID = {}", instanceUUID);
    }

    @BeforeAll
    public static void beforeAll() throws Exception {
        LOG.info("beforeAll() called at {}", Thread.currentThread().getName());
    }

    @AfterAll
    public static void afterAll() throws Exception {
        LOG.info("afterAll() called at {}", Thread.currentThread().getName());
    }

    @BeforeEach
    public void beforeEach() throws Exception {
        LOG.info("beforeEach() called at {}", Thread.currentThread().getName());
    }

    @AfterEach
    public void afterEach() throws Exception {
        LOG.info("afterEach() called at {}", Thread.currentThread().getName());
    }

    @ParameterizedTest
    @ValueSource(strings = { "hello abc", "hello def", "hello ghi" })
    void assertStartWithHello(String src) {
        assertTrue(src.startsWith("hello"));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = { " ", "   ", "\t", "\n" })
    void nullEmptyOrBlankStrings(String text) {
        assertTrue(text == null || text.trim().isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "   ", "\t", "\n" })
    void nullEmptyAndBlankStrings(String text) {
        assertTrue(text == null || text.trim().isEmpty());
    }

    // must be static unless the test class is annotated with @TestInstance(Lifecycle.PER_CLASS)
    static Stream<String> provideHelloSource() {
        return Stream.of("hello abc", "hello def", "hello ghi");
    }

    @ParameterizedTest
    @MethodSource("provideHelloSource")
    void explicitMethodSourceResolutionDemo(String src) {
        assertTrue(src.startsWith("hello"));
    }

    /*
    Stream<String> provideHelloSource2() {
        return Stream.of("hello abc", "hello def", "hello ghi");
    }
    
    @ParameterizedTest
    @MethodSource("provideHelloSource2")
    void explicitMethodSourceResolutionDemo2(String src) {
        assertTrue(src.startsWith("hello"));
    }
    */

    static Stream<String> implicitMethodSourceResolutionDemo() {
        return Stream.of("hello abc", "hello def", "hello ghi");
    }

    @ParameterizedTest
    @MethodSource
    void implicitMethodSourceResolutionDemo(String src) {
        assertTrue(src.startsWith("hello"));
    }

    static Stream<Arguments> provideArgumentsSourceDemo() {
        final SomeObject so1 = new SomeObject();
        so1.intval = 10;
        so1.longval = 20L;
        so1.strval = "hello";
        so1.bytes = new byte[] { 1, 2, 3 };
        final SomeObject so2 = new SomeObject();
        so2.intval = 10;
        so2.longval = 20L;
        so2.strval = "hello";
        so2.bytes = new byte[] { 1, 2, 3 };
        final SomeObject so3 = new SomeObject();
        so3.intval = 10;
        so3.longval = 20L;
        so3.strval = "hello";
        so3.bytes = new byte[] { 1, 2, 3 };

        return Stream.of(
                arguments("hello abc", 1 / 3f, Arrays.asList("aaa", "bbb", "ccc"), so1),
                arguments("hello def", 1 / 3f, Arrays.asList("aaa", "bbb", "ccc"), so2),
                arguments("hello ghi", 1 / 3f, Arrays.asList("aaa", "bbb", "ccc"), so3));
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsSourceDemo")
    void argumentedMethodSourceDemo(final String str, final float oneByThree, final List<String> list,
            final SomeObject so) {
        assertTrue(str.startsWith("hello"));
        assertEquals(0.333f, oneByThree, 0.001f);
        final List<String> expectedList = List.of("aaa", "bbb", "ccc");
        assertIterableEquals(expectedList, list);
        final SomeObject expectedSo = new SomeObject();
        expectedSo.intval = 10;
        expectedSo.longval = 20L;
        expectedSo.strval = "hello";
        expectedSo.bytes = new byte[] { 1, 2, 3 };
        assertEquals(expectedSo, so);
    }
}
