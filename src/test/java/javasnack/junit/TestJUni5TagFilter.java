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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see https://junit.org/junit5/docs/current/user-guide/#writing-tests-tagging-and-filtering
 * @see http://nowokay.hatenablog.com/entry/20171012/1507769090
 */
public class TestJUni5TagFilter {

    private static final Logger LOG = LoggerFactory.getLogger(TestJUni5TagFilter.class);

    private static final String MYUUID = UUID.randomUUID().toString();

    private final String instanceUUID = UUID.randomUUID().toString();

    static {
        LOG.info("class loading static block, MYUUID={}", MYUUID);
    }

    public TestJUni5TagFilter() {
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

    @Test
    public void assertStringValue() {
        final String expected = "hello";
        final String actual = "hello";
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = { "hello abc", "hello def", "hello ghi" })
    void assertStartWithHello(String src) {
        assertTrue(src.startsWith("hello"));
    }

    @RepeatedTest(3)
    void repeatedTest() {
        LOG.info("repeatedTest() called.");
    }

    /*
     * junit5-tag-filter-1 : included in maven-surefire-plugin execution
     */

    @Test
    @Tag("junit5-tag-filter-1")
    public void assertStringValueWithTag1() {
        final String expected = "hello";
        final String actual = "hello";
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = { "hello abc", "hello def", "hello ghi" })
    @Tag("junit5-tag-filter-1")
    void assertStartWithHelloWithTag1(String src) {
        assertTrue(src.startsWith("hello"));
    }

    @RepeatedTest(3)
    @Tag("junit5-tag-filter-1")
    void repeatedTestWithTag1() {
        LOG.info("repeatedTest() called.");
    }

    /*
     * junit5-tag-filter-2 : excluded from maven-surefire-plugin execution
     */

    @Test
    @Tag("junit5-tag-filter-2")
    public void assertStringValueWithTag2() {
        final String expected = "hello";
        final String actual = "hello";
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = { "hello abc", "hello def", "hello ghi" })
    @Tag("junit5-tag-filter-2")
    void assertStartWithHelloWithTag2(String src) {
        assertTrue(src.startsWith("hello"));
    }

    @RepeatedTest(3)
    @Tag("junit5-tag-filter-2")
    void repeatedTestWithTag2() {
        LOG.info("repeatedTest() called.");
    }

    /*
     * (my-junit5-meta-annotation-1 tag) : included in maven-surefire-plugin execution
     */

    @Test
    @MyJUnit5MetaAnnotation1
    public void assertStringValueWithCustomMetaAnnnotation1() {
        final String expected = "hello";
        final String actual = "hello";
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = { "hello abc", "hello def", "hello ghi" })
    @MyJUnit5MetaAnnotation1
    void assertStartWithHelloWithCustomMetaAnnnotation1(String src) {
        assertTrue(src.startsWith("hello"));
    }

    @RepeatedTest(3)
    @MyJUnit5MetaAnnotation1
    void repeatedTestWithCustomMetaAnnnotation1() {
        LOG.info("repeatedTest() called.");
    }

    /*
     * (my-junit5-meta-annotation-2 tag) : excluded from maven-surefire-plugin execution
     */

    @Test
    @MyJUnit5MetaAnnotation2
    public void assertStringValueWithCustomMetaAnnnotation2() {
        final String expected = "hello";
        final String actual = "hello";
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = { "hello abc", "hello def", "hello ghi" })
    @MyJUnit5MetaAnnotation2
    void assertStartWithHelloWithCustomMetaAnnnotation2(String src) {
        assertTrue(src.startsWith("hello"));
    }

    @RepeatedTest(3)
    @MyJUnit5MetaAnnotation2
    void repeatedTestWithCustomMetaAnnnotation2() {
        LOG.info("repeatedTest() called.");
    }
}
