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

package javasnack.json;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;
import java.io.Writer;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import javasnack.json.pojo.EncodePojo;
import javasnack.tool.StreamTool;

public class TestPojoEncodeGson {

    @Test
    public void testPojoEncode() throws Exception {
        Gson gson = new Gson();
        assertThat(gson.toJson(new EncodePojo()))
                .isEqualToNormalizingNewlines(StreamTool.res2str("json/pojo-encode/gson.txt"));

        assertThat(new GsonBuilder().setPrettyPrinting().create().toJson(new EncodePojo()))
                .isEqualToNormalizingNewlines(StreamTool.res2str("json/pojo-encode/gson-pp.txt"));

        // see https://sites.google.com/site/gson/streaming
        Writer out = new StringWriter();
        JsonWriter writer = new JsonWriter(out);
        writer.setIndent("  "); // enable pretty print
        writer.beginObject();
        writer.name("message").value("success");
        writer.name("count").value(10);
        writer.name("records");
        writer.beginArray();

        writer.flush();
        assertThat(out.toString()).isEqualToNormalizingNewlines(
                StreamTool.res2str("json/pojo-encode/gson-streamout-1.txt"));

        gson.toJson(new EncodePojo(), EncodePojo.class, writer);
        writer.flush();
        assertThat(out.toString()).isEqualToNormalizingNewlines(
                StreamTool.res2str("json/pojo-encode/gson-streamout-2.txt"));

        gson.toJson(new EncodePojo(), EncodePojo.class, writer);
        writer.flush();
        assertThat(out.toString()).isEqualToNormalizingNewlines(
                StreamTool.res2str("json/pojo-encode/gson-streamout-3.txt"));

        writer.endArray();
        writer.endObject();
        writer.close();
        assertThat(out.toString()).isEqualToNormalizingNewlines(
                StreamTool.res2str("json/pojo-encode/gson-streamout-4.txt"));
    }

}
