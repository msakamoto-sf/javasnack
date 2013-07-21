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

import javasnack.tool.StreamTool;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * ShaniXmlParser SAX2 Parser (through JAXP) Demos.
 * 
 * @see http://sourceforge.net/projects/shanidom/
 * @see http://docs.oracle.com/javase/tutorial/jaxp/sax/parsing.html
 * @see http://msugai.fc2web.com/java/XML/xerces.html
 * @see http://msugai.fc2web.com/java/XML/SAX.html
 * @see http://www.saxproject.org/quickstart.html
 * @see http://docstore.mik.ua/orelly/xml/sax2/ch01_04.htm
 * 
 * @author "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 */
public class ShaniSax2WithJaxpDemo implements Runnable {

    void printSaxParserSwitches(SAXParser p) {
        System.out.println("isNamespaceAware() : " + p.isNamespaceAware());
        System.out.println("isValidating()     : " + p.isValidating());
        // Shani's Parser invokes java.lang.UnsupportedOperationException: This parser does not support specification "null" version "null"
        //System.out.println("isXIncludeAware()  : " + p.isXIncludeAware());
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
                "org.allcolor.xml.parser.CSaxParserFactory");

        loadXml("xmldemo/simple1.xml");
        loadXml("xmldemo/error1.xml");
        loadXml("xmldemo/html5.html");
        loadXml("xmldemo/ns_and_prefix1.xml");

        System.out.println("(END)");
    }
}
