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
package javasnack.mustache;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import javasnack.json.pojo.EncodePojo;
import javasnack.tool.StreamTool;

public class TestMustacheDemo {

    static class SubScope {
        public String original;
        public String uppercase;

        SubScope(String content) {
            this.original = content;
            this.uppercase = content.toUpperCase();
        }

        public String getOriginal() {
            return original;
        }
    }

    @Test
    public void pojoscope() throws Exception {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("mustache-java-tmpl/pojo.mustache");
        EncodePojo boundScope = new EncodePojo() {
            @SuppressWarnings("unused")
            SubScope subScope = new SubScope("HelloMustache");
            @SuppressWarnings("unused")
            List<SubScope> subScopes = Arrays.asList(new SubScope("subscope1"), new SubScope("subscope2"));
        };
        StringWriter out = new StringWriter();
        mustache.execute(out, boundScope).flush();
        assertThat(out.toString().trim())
                .isEqualToNormalizingNewlines(StreamTool.res2str("mustache-java-tmpl/pojo.expected.txt").trim());
    }

    @Test
    public void mapmiss() throws Exception {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("mustache-java-tmpl/mapmiss.mustache");
        Map<String, Object> m1 = new HashMap<>();
        m1.put("k1", Integer.valueOf(100));
        m1.put("k2", "abc");
        Map<String, Object> boundScope = new HashMap<>();
        boundScope.put("m1", m1);
        StringWriter out = new StringWriter();
        mustache.execute(out, boundScope).flush();
        assertThat(out.toString().trim())
                .isEqualToNormalizingNewlines(StreamTool.res2str("mustache-java-tmpl/mapmiss.expected.txt").trim());
    }

    @Test
    public void conditions() throws Exception {
        Map<String, Object> boundScope = new HashMap<>();
        boundScope.put("cond1", true);
        boundScope.put("item1", "hello");
        boundScope.put("cond2", false);
        boundScope.put("item2", "bonjour");
        boundScope.put("cond3", "");
        boundScope.put("item3", "morning");
        boundScope.put("cond4", Collections.EMPTY_LIST);
        boundScope.put("item4", "afternoon");
        boundScope.put("cond5", null);
        boundScope.put("item5", "evening");

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("mustache-java-tmpl/conditions.mustache");
        StringWriter out = new StringWriter();
        mustache.execute(out, boundScope).flush();
        assertThat(out.toString().trim())
                .isEqualToNormalizingNewlines(StreamTool.res2str("mustache-java-tmpl/conditions.expected.txt").trim());
    }

    @Test
    public void htmlescape() throws Exception {
        Object boundScope = new Object() {
            @SuppressWarnings("unused")
            public String val = "<\"\\'&>";
        };
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("mustache-java-tmpl/htmlescape.mustache");
        StringWriter out = new StringWriter();
        mustache.execute(out, boundScope).flush();
        assertThat(out.toString().trim())
                .isEqualToNormalizingNewlines(StreamTool.res2str("mustache-java-tmpl/htmlescape.expected.txt").trim());
    }

    class Bean4 {
        public String attr4 = "bean4:attr4";
    }

    class Bean3 {
        public String attr3 = "bean3:attr3";
    }

    class Bean2 {
        public String attr2 = "bean2:attr2";
        public Bean3 bean3 = new Bean3();
    }

    class Bean1 {
        public String attr1 = "bean1:attr1";
        public Bean2 bean2 = new Bean2();
        public Map<String, Bean4> map;

        public Bean1() {
            map = new HashMap<>();
            map.put("key1", new Bean4());
        }
    }

    @Test
    public void nestedbeans() throws Exception {
        Object boundScope = new Object() {
            @SuppressWarnings("unused")
            public Bean1 bean1 = new Bean1();
        };
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("mustache-java-tmpl/nestedbeans.mustache");
        StringWriter out = new StringWriter();
        mustache.execute(out, boundScope).flush();
        assertThat(out.toString().trim())
                .isEqualToNormalizingNewlines(StreamTool.res2str("mustache-java-tmpl/nestedbeans.expected.txt").trim());
    }

}
