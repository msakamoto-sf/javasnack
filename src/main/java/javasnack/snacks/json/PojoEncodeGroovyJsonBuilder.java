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
