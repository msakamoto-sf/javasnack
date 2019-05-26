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
package javasnack.ser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class Bar implements Serializable {
    private static final long serialVersionUID = -1117467365797726761L;
    protected int num;
    protected String name;
    protected Date createdAt;
    transient protected String hiddenName;
    Map<String, String> options = null;

    public void setNum(int v) {
        this.num = v;
    }

    public int getNum() {
        return this.num;
    }

    public void setName(String v) {
        this.name = v;
    }

    public String getName() {
        return this.name;
    }

    public void setCreatedAt(Date v) {
        this.createdAt = v;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setHiddenName(String v) {
        this.hiddenName = v;
    }

    public String getHiddenName() {
        return this.hiddenName;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        // serialize optional map field manually.
        options = new HashMap<String, String>();
        options.put("name", name);
        options.put("num", Integer.toString(num));
        options.put("hidden", hiddenName);
        out.writeObject(options);
        // serialize transient field manually.
        out.writeObject(hiddenName + " with spice!!");
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        options = (Map<String, String>) in.readObject();
        hiddenName = (String) in.readObject();
    }
}

public class TestCustomizedReadWrite {

    @Test
    public void serializeCustomizedReadWriteObject() throws IOException,
            ClassNotFoundException {

        Bar o = new Bar();
        o.setNum(100);
        o.setName("Foooooo");
        o.setHiddenName("ABC");
        o.setCreatedAt(new Date());

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(bout);
        oout.writeObject(o);
        oout.close();
        bout.close();

        byte[] serdata = bout.toByteArray();
        System.out.println("---------- serialized Foo object : binary");
        System.out.println(Arrays.toString(serdata));
        System.out
                .println("---------- serialized Foo object : ISO-8859-1 encoded string");
        String serstr = new String(serdata, "ISO-8859-1");
        System.out.println(serstr);

        ByteArrayInputStream bin = new ByteArrayInputStream(serdata);

        ObjectInputStream oin = new ObjectInputStream(bin);

        Bar o2 = (Bar) oin.readObject();

        assertEquals(o.getNum(), o2.getNum());
        assertEquals(o.getName(), o2.getName());
        assertEquals(o.getHiddenName() + " with spice!!", o2.getHiddenName());
        assertEquals(o.getCreatedAt(), o2.getCreatedAt());
        assertEquals(o.getName(), o2.options.get("name"));
        assertEquals(Integer.toString(o.getNum()), o2.options.get("num"));
        assertEquals(o.getHiddenName(), o2.options.get("hidden"));
    }
}
