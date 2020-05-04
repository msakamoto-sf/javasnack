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

import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* see:
 * https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-instance-lifecycle
 */
@TestInstance(Lifecycle.PER_CLASS)
public class TestJUni5PerClassLifecycle {

    private static final Logger LOG = LoggerFactory.getLogger(TestJUni5PerClassLifecycle.class);

    private static final String MYUUID = UUID.randomUUID().toString();

    private final String instanceUUID = UUID.randomUUID().toString();

    static {
        LOG.info("class loading static block, MYUUID={}", MYUUID);
    }

    public TestJUni5PerClassLifecycle() {
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

    @RepeatedTest(3)
    void repeatedTest() {
        LOG.info("repeatedTest() called.");
    }

    @Test
    void assertDemo1() {
        final String expected = "hello";
        final String actual = "hello";
        assertEquals(expected, actual);
        LOG.info("assertDemo1() called.");
    }
}
