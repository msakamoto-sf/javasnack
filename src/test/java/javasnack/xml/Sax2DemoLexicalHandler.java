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

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/* see:
 * http://d.hatena.ne.jp/vh5150/20071114
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