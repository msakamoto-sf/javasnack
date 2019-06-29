/*
 * Copyright 2013 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
package javasnack.xml;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Formatter;

import org.xml.sax.SAXParseException;

/**
 * Tiny "printf" shortcuts, variable arguments demo :)
 * 
 * @see http://www.ne.jp/asahi/hishidama/home/tech/java/varargs.html
 * @see http://www.javainthebox.net/laboratory/J2SE1.5/LangSpec/Varargs/Varargs.html
 * @see http://news.mynavi.jp/column/java/008/index.html
 */
class DebugPrinter {
    int indent = 0;

    final StringWriter swout = new StringWriter();
    final StringWriter swerr = new StringWriter();
    final PrintWriter pwout = new PrintWriter(swout, true); // auto flush = true
    final PrintWriter pwerr = new PrintWriter(swerr, true); // auto flush = true

    String getOut() {
        return swout.toString();
    }

    String getErr() {
        return swerr.toString();
    }

    @SuppressWarnings("resource")
    void start(String f, Object... args) {
        indent++;
        String s = new Formatter().format(f, args).toString();
        pwout.println(">".repeat(indent) + " " + s);
    }

    @SuppressWarnings("resource")
    void print(String f, Object... args) {
        String s = new Formatter().format(f, args).toString();
        pwout.println(">".repeat(indent) + " " + s);
    }

    @SuppressWarnings("resource")
    void end(String f, Object... args) {
        String s = new Formatter().format(f, args).toString();
        pwout.println("<".repeat(indent) + " " + s);
        indent--;
    }

    @SuppressWarnings("resource")
    void oops(String f, Object... args) {
        String s = new Formatter().format(f, args).toString();
        pwerr.println(s);
    }

    void saxerr(String level, SAXParseException e) {
        oops("%s : SAXParserException(line:%d, column:%d, publicId:%s, systemId:%s)",
                level,
                e.getLineNumber(),
                e.getColumnNumber(),
                e.getPublicId(),
                e.getSystemId());
        System.err.println(e);
    }
}