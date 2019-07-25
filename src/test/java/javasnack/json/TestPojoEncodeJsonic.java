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

import javasnack.json.pojo.EncodePojo;
import javasnack.tool.StreamTool;
import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;
import net.arnx.jsonic.JSONWriter;

public class TestPojoEncodeJsonic {

    @Test
    public void testPojoEncode() throws JSONException, Exception {
        String jsonout = JSON.encode(new EncodePojo(), true);
        final var expected = StreamTool.res2str("json/pojo-encode/jsonic.txt");
        assertThat(jsonout).isEqualToNormalizingNewlines(expected);

        Writer out = new StringWriter();
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
        writer.flush();
        final var expected1 = StreamTool.res2str("json/pojo-encode/jsonic-streamout-1.txt");
        assertThat(out.toString()).isEqualToNormalizingNewlines(expected1);

        writer.value(new EncodePojo());
        writer.flush();
        final var expected2 = StreamTool.res2str("json/pojo-encode/jsonic-streamout-2.txt");
        assertThat(out.toString()).isEqualToNormalizingNewlines(expected2);

        writer.value(new EncodePojo());
        writer.flush();
        final var expected3 = StreamTool.res2str("json/pojo-encode/jsonic-streamout-3.txt");
        assertThat(out.toString()).isEqualToNormalizingNewlines(expected3);

        writer.endArray();
        writer.endObject();
        writer.flush();
        final var expected4 = StreamTool.res2str("json/pojo-encode/jsonic-streamout-4.txt");
        assertThat(out.toString()).isEqualToNormalizingNewlines(expected4);
    }
}
