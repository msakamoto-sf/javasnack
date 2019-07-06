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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

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