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

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* see:
 * https://junit.org/junit5/docs/current/user-guide/#writing-tests-repeated-tests
 */
public class TestJUnit5RepeatedTests {
    private static final Logger LOG = LoggerFactory.getLogger(TestJUnit5RepeatedTests.class);

    private static final String MYUUID = UUID.randomUUID().toString();

    private final String instanceUUID = UUID.randomUUID().toString();

    static {
        LOG.info("class loading static block, MYUUID={}", MYUUID);
    }

    public TestJUnit5RepeatedTests() {
        LOG.info("constructor block, instanceUUID = {}", instanceUUID);
    }

    @RepeatedTest(3)
    void repeatedTest() {
        LOG.info("repeatedTest() called.");
    }

    @RepeatedTest(4)
    void repeatedTestWithRepetitionInfo(RepetitionInfo repetitionInfo) {
        assertEquals(4, repetitionInfo.getTotalRepetitions());
        LOG.info("repeatedTestWithRepetitionInfo() called, current={}", repetitionInfo.getCurrentRepetition());
    }

    @RepeatedTest(5)
    void repeatedTestWithTestInfo(TestInfo testInfo) {
        LOG.info("repeatedTestWithTestInfo() called, displayName={}", testInfo.getDisplayName());
    }

    @RepeatedTest(6)
    void repeatedTestWithInfos(TestInfo testInfo, RepetitionInfo repetitionInfo) {
        assertEquals(6, repetitionInfo.getTotalRepetitions());
        LOG.info("repeatedTestWithInfos() called, current={}", repetitionInfo.getCurrentRepetition());
        LOG.info("repeatedTestWithInfos() called, displayName={}", testInfo.getDisplayName());
    }
}
