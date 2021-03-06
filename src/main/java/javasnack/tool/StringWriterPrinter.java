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

package javasnack.tool;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StringWriterPrinter {
    private StringWriter sw;
    public PrintWriter out;

    public StringWriterPrinter() {
        this.sw = new StringWriter();
        this.out = new PrintWriter(this.sw, true); // enable auto flush
    }

    @Override
    public String toString() {
        this.sw.flush();
        return this.sw.toString();
    }

    public void clear() {
        this.sw = new StringWriter();
        this.out = new PrintWriter(this.sw, true); // enable auto flush
    }

    public String cat(final String... lines) {
        return String.join("\n", lines);
    }
}
