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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javasnack.json.pojo.EncodePojo;
import javasnack.json.pojo.EncodePojoEnum;
import javasnack.json.pojo.EncodePojoEnum2;
import javasnack.json.pojo.EncodePojoEnum2JacksonSerializer;
import javasnack.json.pojo.EncodePojoEnumJacksonSerializer;
import javasnack.tool.StreamTool;

/**
 * @author "Masahiko Sakamoto"(msakamoto-sf, sakamoto.gsyc.3s@gmail.com)
 */
/* see:
 * https://github.com/FasterXML/jackson
 * https://github.com/FasterXML/jackson-docs
 */
public class TestPojoEncodeJackson {

    @Test
    public void testPojoEncode() throws Exception {
        final ObjectMapper om = new ObjectMapper();

        /* see: https://stackoverflow.com/questions/27577701/jackson-objectmapper-specify-serialization-order-of-object-properties
         * see: https://www.stubbornjava.com/posts/creating-a-somewhat-deterministic-jackson-objectmapper
         */
        om.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        om.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

        assertThat(om.writeValueAsString(new EncodePojo()))
                .isEqualToNormalizingNewlines(StreamTool.res2str("json/pojo-encode/jackson.txt"));

        assertThat(om.writerWithDefaultPrettyPrinter().writeValueAsString(new EncodePojo()))
                .isEqualToNormalizingNewlines(StreamTool.res2str("json/pojo-encode/jackson-pp.txt"));

        JsonFactory jsonFactory = om.getFactory();
        Writer out = new StringWriter();
        JsonGenerator jg = jsonFactory.createGenerator(out);
        jg.setPrettyPrinter(new DefaultPrettyPrinter());
        jg.writeStartObject();
        jg.writeStringField("message", "success");
        jg.writeNumberField("count", 10);
        jg.writeArrayFieldStart("records");

        jg.flush();
        assertThat(out.toString())
                .isEqualToNormalizingNewlines(StreamTool.res2str("json/pojo-encode/jackson-streamout-1.txt"));

        jg.writeObject(new EncodePojo());
        jg.flush();
        assertThat(out.toString())
                .isEqualToNormalizingNewlines(StreamTool.res2str("json/pojo-encode/jackson-streamout-2.txt"));

        jg.writeObject(new EncodePojo());
        jg.flush();
        assertThat(out.toString())
                .isEqualToNormalizingNewlines(StreamTool.res2str("json/pojo-encode/jackson-streamout-3.txt"));

        jg.writeEndArray();
        jg.writeEndObject();
        jg.close();
        assertThat(out.toString())
                .isEqualToNormalizingNewlines(StreamTool.res2str("json/pojo-encode/jackson-streamout-4.txt"));
    }

    @Test
    public void testPojoEncodeWithCustomSerializer() throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        om.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        om.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

        Object pojo = new Object() {
            @SuppressWarnings("unused")
            public int intv = 10;
            @SuppressWarnings("unused")
            public EncodePojoEnum enum1 = EncodePojoEnum.ONE;
            @SuppressWarnings("unused")
            public EncodePojoEnum2 enum2 = EncodePojoEnum2.DEF;
        };
        assertThat(om.writeValueAsString(pojo)).isEqualTo("{\"enum1\":\"ONE\",\"enum2\":\"DEF\",\"intv\":10}");

        Object pojo2 = new Object() {
            @SuppressWarnings("unused")
            public int intv = 10;

            @JsonSerialize(using = EncodePojoEnumJacksonSerializer.class)
            public EncodePojoEnum enum1 = EncodePojoEnum.ONE;

            @JsonSerialize(using = EncodePojoEnum2JacksonSerializer.class)
            public EncodePojoEnum2 enum2 = EncodePojoEnum2.DEF;
        };
        assertThat(om.writeValueAsString(pojo2))
                .isEqualTo("{\"enum1\":0,\"enum2\":\"num=[20], name=[def]\",\"intv\":10}");
    }
}
