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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class CustomPrefixedFileAppender extends AppenderBase<ILoggingEvent> {

    private String filename = null;

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(final String s) {
        this.filename = s;
    }

    private String prefix = "";

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(final String s) {
        this.prefix = s;
    }

    private PatternLayoutEncoder encoder = null;

    public PatternLayoutEncoder getEncoder() {
        return this.encoder;
    }

    public void setEncoder(final PatternLayoutEncoder v) {
        this.encoder = v;
    }

    private OutputStream out;

    @Override
    public void start() {
        if (Objects.isNull(encoder)) {
            addError("No encoder set for the appender named [" + name + "].");
            return;
        }

        try {
            this.out = new FileOutputStream(this.filename, true);
        } catch (FileNotFoundException e) {
            this.addError("append file open error", e);
            return;
        }
        super.start();
    }

    @Override
    public void stop() {
        try {
            if (Objects.nonNull(out)) {
                out.close();
            }
        } catch (IOException e) {
            this.addWarn("append file close error", e);
        }
        super.stop();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        try {
            final byte[] prefixBytes = this.prefix.getBytes(this.encoder.getCharset());
            out.write(prefixBytes);
            final byte[] bytes = this.encoder.encode(eventObject);
            out.write(bytes);
            out.flush();
        } catch (IOException t) {
            this.addError("write file error", t);
        }
    }
}
