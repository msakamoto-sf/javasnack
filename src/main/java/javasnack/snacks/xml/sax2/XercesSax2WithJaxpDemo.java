/*
 * Copyright 2013 the original author or authors.
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
package javasnack.snacks.xml.sax2;

import java.io.IOException;
import java.util.Formatter;

import javasnack.tool.StreamTool;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Apache Xerces SAX2 Parser (through JAXP) Demos.
 * 
 * @see http://docs.oracle.com/javase/tutorial/jaxp/sax/parsing.html
 * @see http://msugai.fc2web.com/java/XML/xerces.html
 * @see http://msugai.fc2web.com/java/XML/SAX.html
 * @see http://www.saxproject.org/quickstart.html
 * @see http://docstore.mik.ua/orelly/xml/sax2/ch01_04.htm
 * 
 * @author "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 */
public class XercesSax2WithJaxpDemo implements Runnable {

    void printSaxParserSwitches(SAXParser p) {
        System.out.println("isNamespaceAware() : " + p.isNamespaceAware());
        System.out.println("isValidating()     : " + p.isValidating());
        System.out.println("isXIncludeAware()  : " + p.isXIncludeAware());
    }

    void loadXml(String res) {
        System.out.println("==================");
        System.out.println("================== load:" + res);
        System.out.println("==================");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        DebugPrinter p = new DebugPrinter();
        SAXParser parser;
        try {
            parser = factory.newSAXParser();
            printSaxParserSwitches(parser);
            parser.setProperty("http://xml.org/sax/properties/lexical-handler",
                    new Sax2DemoLexicalHandler(p));
            parser.parse(StreamTool.loadResource(res), new Sax2DemoHandler(p));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.setProperty("javax.xml.parsers.SAXParserFactory",
                "org.apache.xerces.jaxp.SAXParserFactoryImpl");

        loadXml("xmldemo/simple1.xml");
        loadXml("xmldemo/error1.xml");
        loadXml("xmldemo/html5.html");
        loadXml("xmldemo/ns_and_prefix1.xml");

        System.out.println("(END)");
    }

    /**
     * Tiny "printf" shortcuts, variable arguments demo :)
     * 
     * @see http://www.ne.jp/asahi/hishidama/home/tech/java/varargs.html
     * @see http://www.javainthebox.net/laboratory/J2SE1.5/LangSpec/Varargs/Varargs.html
     * @see http://news.mynavi.jp/column/java/008/index.html
     */
    class DebugPrinter {
        int indent = 0;

        @SuppressWarnings("resource")
        void start(String f, Object... args) {
            indent++;
            String s = new Formatter().format(f, args).toString();
            System.out.println(StringUtils.repeat(">", indent) + " " + s);
        }

        @SuppressWarnings("resource")
        void print(String f, Object... args) {
            String s = new Formatter().format(f, args).toString();
            System.out.println(StringUtils.repeat(">", indent) + " " + s);
        }

        @SuppressWarnings("resource")
        void end(String f, Object... args) {
            String s = new Formatter().format(f, args).toString();
            System.out.println(StringUtils.repeat("<", indent) + " " + s);
            indent--;
        }

        @SuppressWarnings("resource")
        void oops(String f, Object... args) {
            String s = new Formatter().format(f, args).toString();
            System.err.println(s);
        }

        void saxerr(String level, SAXParseException e) {
            oops("%s : SAXParserException(line:%d, column:%d, publicId:%s, systemId:%s)",
                    level, e.getLineNumber(), e.getColumnNumber(),
                    e.getPublicId(), e.getSystemId());
            oops(e.toString());
            oops("...continue");
        }
    }

    /**
     * Sample Handler (DefaultHandler = ContentHandler, DTDHandler, EntityResolver, ErrorHandler)
     */
    class Sax2DemoHandler extends DefaultHandler {
        DebugPrinter p;

        Sax2DemoHandler(DebugPrinter _p) {
            this.p = _p;
        }

        /*
         * org.xml.sax.ContentHandler implementation:
         */

        @Override
        public void startDocument() {
            p.start("Start document");
        }

        @Override
        public void endDocument() {
            p.end("End document");
        }

        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes attrs) {
            p.start("start[local:%s / qname:%s](uri=[%s])", localName, qName,
                    uri);
            for (int i = 0; i < attrs.getLength(); i++) {
                p.print("   attrs[%d].getLocalName() =%s", i,
                        attrs.getLocalName(i));
                p.print("   attrs[%d].getQName()     =%s", i, attrs.getQName(i));
                p.print("   attrs[%d].getValue()     =%s", i, attrs.getValue(i));
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            p.end("end[local:%s / qname:%s](uri=[%s])", localName, qName, uri);
        }

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            p.print("characters([%s])", new String(ch, start, length));
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length)
                throws SAXException {
            p.print("ignorableWhitespace([%s])", new String(ch, start, length));
        }

        /*
         * org.xml.sax.ErrorHandler implementation:
         */

        @Override
        public void error(SAXParseException e) {
            p.saxerr("error", e);
        }

        @Override
        public void fatalError(SAXParseException e) {
            p.saxerr("fatal", e);
        }

        @Override
        public void warning(SAXParseException e) {
            p.saxerr("warning", e);
        }
    }

    /**
     * @see http://d.hatena.ne.jp/vh5150/20071114
     */
    class Sax2DemoLexicalHandler implements LexicalHandler {
        DebugPrinter p;

        Sax2DemoLexicalHandler(DebugPrinter _p) {
            this.p = _p;
        }

        @Override
        public void startDTD(String name, String publicId, String systemId)
                throws SAXException {
            p.start("startDTD(name:%s, publicId:%s, systemId:%s)", name,
                    publicId, systemId);
        }

        @Override
        public void endDTD() throws SAXException {
            p.end("endDTD");
        }

        @Override
        public void startEntity(String name) throws SAXException {
            p.start("startEntity(%s)", name);
        }

        @Override
        public void endEntity(String name) throws SAXException {
            p.end("endEntity(%s)", name);
        }

        @Override
        public void startCDATA() throws SAXException {
            p.start("startCDATA");
        }

        @Override
        public void endCDATA() throws SAXException {
            p.end("endCDATA");
        }

        @Override
        public void comment(char[] ch, int start, int length)
                throws SAXException {
            p.print("comment([%s])", new String(ch, start, length));
        }
    }
}
