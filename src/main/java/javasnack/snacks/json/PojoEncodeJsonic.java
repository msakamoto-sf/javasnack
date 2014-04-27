package javasnack.snacks.json;

import java.io.OutputStreamWriter;
import java.io.Writer;

import javasnack.snacks.json.pojo.EncodePojo;
import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;
import net.arnx.jsonic.JSONWriter;

/**
 * JSONIC JSON Encoder Demo : POJO to JSON
 * 
 * @see http://jsonic.sourceforge.jp/index.html
 * @author "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 */
public class PojoEncodeJsonic implements Runnable {
    @Override
    public void run() {
        try {
            String jsonout = JSON.encode(new EncodePojo(), true);
            System.out.println("----- simple encode demo -----");
            System.out.println(jsonout);
            System.out.println("----- streaming output -----");
            Writer out = new OutputStreamWriter(System.out);
            JSON json = new JSON();
            json.setPrettyPrint(true);
            json.setInitialIndent(0);
            json.setIndentText(">>");
            JSONWriter writer = json.getWriter(out);
            writer.beginObject();
            writer.name("message").value("success");
            writer.name("count").value(10);
            writer.name("records");
            writer.beginArray();
            for (int i = 0; i < 10; i++) {
                writer.value(new EncodePojo());
                writer.flush();
                Thread.sleep(100);
            }
            writer.endArray();
            writer.endObject();
            writer.flush();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
