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

import org.junit.jupiter.api.Test;

import groovy.json.JsonBuilder;
import javasnack.json.pojo.EncodePojo;
import javasnack.tool.StreamTool;

/**
 * Groovy JsonBuilder JSON Encoder Demo : POJO to JSON
 * 
 * @author "Masahiko Sakamoto"(msakamoto-sf, sakamoto.gsyc.3s@gmail.com)
 */
/* see:
 * https://groovy-lang.org/json.html
 * http://docs.groovy-lang.org/latest/html/documentation/core-domain-specific-languages.html#_jsonbuilder
 * http://npnl.hatenablog.jp/entry/20110226/1298729619
 */
public class TestPojoEncodeJsonBuilder {

    @Test
    public void testPojoEncode() throws Exception {
        final JsonBuilder jsonBuilder = new JsonBuilder();
        jsonBuilder.call(new EncodePojo());
        assertThat(jsonBuilder.toString())
                .isEqualToNormalizingNewlines(StreamTool.res2str("json/pojo-encode/jsb.txt"));

        assertThat(jsonBuilder.toPrettyString())
                .isEqualToNormalizingNewlines(StreamTool.res2str("json/pojo-encode/jsb-pp.txt"));

        // TODO StreamingJsonBuilder
        // http://docs.groovy-lang.org/latest/html/documentation/core-domain-specific-languages.html#_streamingjsonbuilder
    }
}
