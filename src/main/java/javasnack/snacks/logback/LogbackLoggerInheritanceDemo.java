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

package javasnack.snacks.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javasnack.RunnableSnack;

public class LogbackLoggerInheritanceDemo implements RunnableSnack {
    @Override
    public void run(final String... args) {
        final Logger logger0 = LoggerFactory.getLogger(this.getClass());
        logger0.trace("logger0 demo");
        logger0.debug("logger0 demo");
        logger0.info("logger0 demo");
        logger0.warn("logger0 demo");
        logger0.error("logger0 demo");

        final Logger logger = LoggerFactory.getLogger("logback.inheritance.demo");
        logger.trace("logger demo");
        logger.debug("logger demo");
        logger.info("logger demo");
        logger.warn("logger demo");
        logger.error("logger demo");

        final Logger logger1 = LoggerFactory.getLogger("logback.inheritance.demo.child1");
        logger1.trace("logger1 demo");
        logger1.debug("logger1 demo");
        logger1.info("logger1 demo");
        logger1.warn("logger1 demo");
        logger1.error("logger1 demo");

        final Logger logger2 = LoggerFactory.getLogger("logback.inheritance.demo.child2");
        logger2.trace("logger2 demo");
        logger2.debug("logger2 demo");
        logger2.info("logger2 demo");
        logger2.warn("logger2 demo");
        logger2.error("logger2 demo");
    }
}
