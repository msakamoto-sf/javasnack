package javasnack.snacks.json;

import java.io.OutputStreamWriter;

import javasnack.snacks.json.pojo.EncodePojo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

/**
 * google-gson JSON Encoder Demo : POJO to JSON
 * 
 * @see https://code.google.com/p/google-gson/
 * @author "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 */
public class PojoEncodeGson implements Runnable {
    @Override
    public void run() {
        Gson gson = new Gson();
        try {
            System.out.println("--- simple google-gson encode ---");
            System.out.println(gson.toJson(new EncodePojo()));
            System.out
                    .println("--- default pretty-print google-gson encode ---");
            System.out.println(new GsonBuilder().setPrettyPrinting().create()
                    .toJson(new EncodePojo()));

            // see https://sites.google.com/site/gson/streaming
            System.out.println("--- streaming google-gson encode ---");
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(
                    System.out));
            writer.setIndent(">>"); // enable pretty print
            writer.beginObject();
            writer.name("message").value("success");
            writer.name("count").value(10);
            writer.name("records");
            writer.beginArray();
            for (int i = 0; i < 10; i++) {
                gson.toJson(new EncodePojo(), EncodePojo.class, writer);
                writer.flush();
                Thread.sleep(100);
            }
            writer.endArray();
            writer.endObject();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
