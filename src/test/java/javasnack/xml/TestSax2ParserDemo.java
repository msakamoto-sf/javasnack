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
package javasnack.xml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Stream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.SAXParseException;

import javasnack.tool.StreamTool;

/* もともと cli 起動するjavasnackとして Xerces2 の SAX2 のデモコードを置いていた。
 * Xerces2をわざわざ使っていたのは、当時の仕事関連で興味があったから。
 * 
 * -> Java11に移行する際、特にXerces2にこだわる必要も無く、また標準のJAXPの範囲内で
 * デモを作成したほうが後々メンテや使い回しが良さそうなので、Xerces2の依存を削除し、
 * 標準JAXPの範囲内で動作するようなテストコードにマイグレーションしている。
 * 
 * see:
 * 
 * JAXP : Java API for XML Processing
 * https://docs.oracle.com/javase/tutorial/jaxp/index.html
 * 
 * SAX : Simple API for XML
 * https://docs.oracle.com/javase/tutorial/jaxp/sax/index.html
 * http://www.saxproject.org/quickstart.html
 * https://docstore.mik.ua/orelly/xml/sax2/ch01_04.htm
 * http://msugai.fc2web.com/java/XML/SAX.html
 * 
 * Xerces:
 * https://xerces.apache.org/
 * http://xerces.apache.org/xerces2-j/
 * http://msugai.fc2web.com/java/XML/xerces.html
 */
public class TestSax2ParserDemo {

    static Stream<Arguments> provideSax2ParserDemoParameters() {
        return Stream.of(
                arguments("simple1", "", null, ""),
                arguments("error1",
                        "fatal : SAXParserException(line:2, column:6, publicId:null, systemId:null)",
                        SAXParseException.class,
                        "[xX][mM][lL]"),
                arguments("html5-xhtml-compat", "", null, ""),
                arguments("namespace-and-prefix1", "", null, ""));
    }

    @ParameterizedTest
    @MethodSource("provideSax2ParserDemoParameters")
    public void testSax2ParserDemo(
            final String baseFilename,
            final String expectedDumpErr,
            final Class<?> expectExceptionClass,
            final String expectExceptionMessageContained) throws Exception {

        final String res = "xmldemo/sax2-parser-demo/" + baseFilename + ".xml";
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        final DebugPrinter p = new DebugPrinter();

        final Throwable t = catchThrowable(() -> {
            final SAXParser parser = factory.newSAXParser();
            assertThat(parser.isNamespaceAware()).isFalse();
            assertThat(parser.isValidating()).isFalse();
            assertThat(parser.isXIncludeAware()).isFalse();
            parser.setProperty("http://xml.org/sax/properties/lexical-handler",
                    new Sax2DemoLexicalHandler(p));
            parser.parse(StreamTool.loadResource(res), new Sax2DemoHandler(p));
        });
        if (Objects.nonNull(expectExceptionClass)) {
            assertThat(t).isInstanceOf(expectExceptionClass).hasMessageContaining(expectExceptionMessageContained);
        } else {
            assertThat(t).isNull();
        }

        assertThat(p.getOut()).isEqualToNormalizingNewlines(
                StreamTool.res2str("xmldemo/sax2-parser-demo/" + baseFilename + "-expected.txt",
                        StandardCharsets.UTF_8));
        assertThat(p.getErr().trim()).isEqualToNormalizingNewlines(expectedDumpErr);
    }
}
