/*
 * Copyright 2020 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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

package javasnack.snacks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javasnack.RunnableSnack;

public class LogbackBasicUsageDemo implements RunnableSnack {
    @Override
    public void run(final String... args) {
        final Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.trace("basic demo");
        logger.debug("basic demo");
        logger.info("basic demo");
        logger.warn("basic demo");
        logger.error("basic demo");

        logger.trace("basic demo (exception)", new RuntimeException("trace-level"));
        logger.debug("basic demo (exception)", new RuntimeException("debug-level"));
        logger.info("basic demo (exception)", new RuntimeException("info-level"));
        logger.warn("basic demo (exception)", new RuntimeException("warn-level"));
        logger.error("basic demo (exception)", new RuntimeException("error-level"));
    }
}
