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

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import javasnack.RunnableSnack;

public class LogbackAddRemoveAppenderAtRuntimeDemo implements RunnableSnack {
    /* demonstration of detaching existing appender then creating appender and adding to root logger.
     * 
     * see-also:
     * - http://mailman.qos.ch/pipermail/logback-user/2007-May/000207.html
     * - http://mailman.qos.ch/pipermail/logback-user/2007-May/000209.html
     * - https://stackoverflow.com/questions/16910955/programmatically-configure-logback-appender
     * - https://stackoverflow.com/questions/54161783/logback-adding-appender-at-runtime
     */

    @Override
    public void run(final String... args) {
        final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        final ch.qos.logback.classic.Logger childLogger = lc.getLogger(Logger.ROOT_LOGGER_NAME);
        if (Objects.isNull(childLogger.getAppender("STDOUT"))) {
            System.err.println("child logger does not have STDOUT appender.");
        } else {
            System.err.println("child logger have STDOUT appender.");
        }
        final ch.qos.logback.classic.Logger rootLogger = lc.getLogger(Logger.ROOT_LOGGER_NAME);
        if (Objects.isNull(rootLogger.getAppender("STDOUT"))) {
            System.err.println("root logger does not have STDOUT appender.");
        } else {
            System.err.println("root logger have STDOUT appender.");
        }

        doLog("[before dettaching STDOUT from rootLogger] ");
        rootLogger.detachAppender("STDOUT");
        doLog("[after dettaching STDOUT from rootLogger] ");

        // we need to call setContext() and start() for both encoder and appender.
        final PatternLayoutEncoder ple = new PatternLayoutEncoder();
        ple.setContext(lc);
        ple.setPattern("[STDOUT#2] %d{ISO8601} [%thread] [%-5level] %logger - %msg%n");
        ple.start();
        final ConsoleAppender<ILoggingEvent> appender0 = new ConsoleAppender<>();
        appender0.setContext(lc);
        appender0.setName("STDOUT#2");
        appender0.setEncoder(ple);
        appender0.start();
        rootLogger.addAppender(appender0);
        doLog("[after attaching STDOUT#2 to rootLogger] ");
    }

    void doLog(final String prefix) {
        final Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.trace(prefix + "basic demo(日本語)");
        logger.debug(prefix + "basic demo(日本語)");
        logger.info(prefix + "basic demo(日本語)");
        logger.warn(prefix + "basic demo(日本語)");
        logger.error(prefix + "basic demo(日本語)");

        logger.trace(prefix + "basic demo(日本語) (exception)", new RuntimeException("trace-level"));
        logger.debug(prefix + "basic demo(日本語) (exception)", new RuntimeException("debug-level"));
        logger.info(prefix + "basic demo(日本語) (exception)", new RuntimeException("info-level"));
        logger.warn(prefix + "basic demo(日本語) (exception)", new RuntimeException("warn-level"));
        logger.error(prefix + "basic demo(日本語) (exception)", new RuntimeException("error-level"));
    }
}
