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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit5 basic annotation and assertion samples.
 * 
 * @see https://junit.org/junit5/docs/current/user-guide/#writing-tests-annotations
 * @see https://junit.org/junit5/docs/current/user-guide/#writing-tests-assertions
 * @see http://www.ne.jp/asahi/hishidama/home/tech/java/junit/5/assertion.html
 */
@DisplayName("TestExercise01, ლ(´ڡ`ლ)")
public class TestJUnit5BasicAnnotationAssertion {
    private static final Logger LOG = LoggerFactory.getLogger(TestJUnit5BasicAnnotationAssertion.class);

    private static final String MYUUID = UUID.randomUUID().toString();

    private final String instanceUUID = UUID.randomUUID().toString();

    static {
        LOG.info("class loading static block, MYUUID={}", MYUUID);
    }

    public TestJUnit5BasicAnnotationAssertion() {
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
    @DisplayName("assertStringValue(), ヽ(=´▽`=)ﾉ")
    public void assertStringValue() {
        final String expected = "hello";
        final String actual = "hello";
        assertEquals(expected, actual);
    }

    @Test
    public void assertFloadOrDoubleValue() {
        assertEquals(0.333f, 1 / 3f, 0.001f);
        assertEquals(0.333d, 1 / 3d, 0.001d);
    }

    @Test
    public void assertTrueOrFalseOrNull() {
        final boolean isTrue = true;
        final boolean isFalse = false;
        final Object isNull = null;
        assertTrue(isTrue);
        assertFalse(isFalse);
        assertNull(isNull);
    }

    @Test
    public void assertArrayOfBytes() {
        final byte[] expected = new byte[] { 1, 2, 3 };
        final byte[] actual = new byte[] { 1, 2, 3 };
        assertArrayEquals(expected, actual);
    }

    @Test
    public void assertArrayOfString() {
        final String[] expected = new String[] { "abc", "def", "ghi" };
        final String[] actual = new String[] { "abc", "def", "ghi" };
        assertArrayEquals(expected, actual);
    }

    @Test
    public void assertListValue() {
        final List<String> expected = List.of("aaa", "bbb", "ccc");
        final List<String> actual = List.of("aaa", "bbb", "ccc");
        //assertEquals(expected, actual);
        // more readable:
        assertIterableEquals(expected, actual);
    }

    int div(final int a, final int b) {
        return a / b;
    }

    @Test
    public void assertExceptionThrows() {
        final Exception exception = assertThrows(ArithmeticException.class, () -> this.div(1, 0));
        assertEquals("/ by zero", exception.getMessage());
    }

    static class Foo {
        public int intval;
        public long longval;
        public String strval;
        public byte[] bytes;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(bytes);
            result = prime * result + intval;
            result = prime * result + (int) (longval ^ (longval >>> 32));
            result = prime * result + ((strval == null) ? 0 : strval.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Foo other = (Foo) obj;
            if (!Arrays.equals(bytes, other.bytes)) {
                return false;
            }
            if (intval != other.intval) {
                return false;
            }
            if (longval != other.longval) {
                return false;
            }
            if (strval == null) {
                if (other.strval != null) {
                    return false;
                }
            } else if (!strval.equals(other.strval)) {
                return false;
            }
            return true;
        }
    }

    @Test
    public void assertFooObject() {
        final Foo expected = new Foo();
        expected.intval = 10;
        expected.longval = 20L;
        expected.strval = "hello";
        expected.bytes = new byte[] { 1, 2, 3 };
        final Foo actual = new Foo();
        actual.intval = 10;
        actual.longval = 20L;
        actual.strval = "hello";
        actual.bytes = new byte[] { 1, 2, 3 };
        assertEquals(expected, actual);
    }

    @Test
    public void assertTimeoutDemo() {
        LOG.info("assertTimeoutDemo, assertion start");
        assertTimeout(Duration.ofSeconds(2), () -> {
            LOG.info("assertTimeoutDemo, sleep start");
            Thread.sleep(1000);
            LOG.info("assertTimeoutDemo, sleep end");
        });
        LOG.info("assertTimeoutDemo, assertion end");
    }

    @Test
    public void assertTimeoutDemoWithReturnValue() {
        LOG.info("assertTimeoutDemoWithReturnValue, assertion start");
        final String actual = assertTimeout(Duration.ofSeconds(2), () -> {
            LOG.info("assertTimeoutDemoWithReturnValue, sleep start");
            Thread.sleep(1000);
            LOG.info("assertTimeoutDemoWithReturnValue, sleep end");
            return "hello";
        });
        LOG.info("assertTimeoutDemoWithReturnValue, assertion end");
        assertEquals("hello", actual);
    }

    @Test
    public void assertTimeoutPreemptivelyDemo() {
        LOG.info("assertTimeoutPreemptivelyDemo, assertion start");
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
            LOG.info("assertTimeoutPreemptivelyDemo, sleep start");
            Thread.sleep(1000);
            LOG.info("assertTimeoutPreemptivelyDemo, sleep end");
        });
        LOG.info("assertTimeoutPreemptivelyDemo, assertion end");
    }

    @Test
    @Disabled
    public void failMessage() {
        fail("fail message, disabled");
    }
}
