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

import java.io.OutputStreamWriter;

import javasnack.snacks.json.pojo.EncodePojo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

/**
 * google-gson JSON Encoder Demo : POJO to JSON
 * 
 * TODO : how to use public getter, not only public field ?
 * @see http://stackoverflow.com/questions/6203487/why-does-gson-use-fields-and-not-getters-setters
 * @see http://www.javacreed.com/simple-gson-example/
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
