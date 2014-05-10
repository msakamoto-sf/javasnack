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

import javasnack.snacks.json.pojo.EncodePojoEnum;
import javasnack.snacks.json.pojo.EncodePojoEnum2;
import javasnack.snacks.json.pojo.EncodePojoEnum2JacksonSerializer;
import javasnack.snacks.json.pojo.EncodePojoEnumJacksonSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Jackson JSON Encoder Demo : Custom JSON Serializer
 * 
 * @see http://fasterxml.github.io/jackson-databind/javadoc/2.3.0/com/fasterxml/jackson/databind/JsonSerializer.html
 * @see https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations
 * @see https://github.com/FasterXML/jackson-databind/wiki/Databind-annotations
 * @see http://java.dzone.com/articles/how-serialize-javautildate
 * @see http://stackoverflow.com/questions/12046786/jackson-json-custom-serialization-for-certain-fields
 * @see http://www.baeldung.com/jackson-custom-serialization
 * @author "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 */
public class PojoEncodeJackson2 implements Runnable {
    @Override
    public void run() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            System.out.println("------- without custom serializer -------");
            Object pojo = new Object() {
                public int intv = 10;
                public EncodePojoEnum enum1 = EncodePojoEnum.ONE;
                public EncodePojoEnum2 enum2 = EncodePojoEnum2.DEF;
            };
            System.out.println(objectMapper.writeValueAsString(pojo));
            System.out.println("------- with custom serializer -------");
            Object pojo2 = new Object() {
                public int intv = 10;

                @JsonSerialize(using = EncodePojoEnumJacksonSerializer.class)
                public EncodePojoEnum enum1 = EncodePojoEnum.ONE;

                @JsonSerialize(using = EncodePojoEnum2JacksonSerializer.class)
                public EncodePojoEnum2 enum2 = EncodePojoEnum2.DEF;
            };
            System.out.println(objectMapper.writeValueAsString(pojo2));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
