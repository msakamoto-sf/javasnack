/*
 * Copyright 2014 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
package javasnack.snacks.json;

import groovy.json.JsonBuilder;
import javasnack.snacks.json.pojo.EncodePojo;

/**
 * Groovy JsonBuilder JSON Encoder Demo : POJO to JSON
 * 
 * @see http://groovy.codehaus.org/gapi/groovy/json/JsonBuilder.html
 * @see http://npnl.hatenablog.jp/entry/20110226/1298729619
 * @see http://docs.codehaus.org/display/GroovyJSR/GEP+7+-+JSON+Support
 * @author "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 */
public class PojoEncodeGroovyJsonBuilder implements Runnable {
    @Override
    public void run() {
        JsonBuilder jsonBuilder = new JsonBuilder();
        try {
            jsonBuilder.call(new EncodePojo());
            System.out.println("--- simple Groovy JsonBuilder encode ---");
            System.out.println(jsonBuilder.toString());
            System.out
                    .println("--- pretty-print Groovy JsonBuilder encode ---");
            System.out.println(jsonBuilder.toPrettyString());

            /*
             * Groovy has StreamingJsonBuilder also. Java example waiting... :P
             */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
