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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class TestSerializeBasics {
    static class Foo implements Serializable {
        private static final long serialVersionUID = 7388974747804746922L;
        protected int num;
        protected String name;
        protected Date createdAt;

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
    }

    @Test
    public void simpleClassSerialize() throws IOException,
            ClassNotFoundException {

        Foo o = new Foo();
        o.setNum(100);
        o.setName("Foooooo");
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

        Foo o2 = (Foo) oin.readObject();

        assertEquals(o.getNum(), o2.getNum());
        assertEquals(o.getName(), o2.getName());
        assertEquals(o.getCreatedAt(), o2.getCreatedAt());
    }

    @Test
    public void serializeArrayList() throws IOException, ClassNotFoundException {

        final List<Foo> l = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        Foo o = new Foo();
        o.setNum(100);
        o.setName("o1");
        cal.set(2010, 1, 2, 3, 4, 5);
        o.setCreatedAt(new Date(cal.getTimeInMillis()));
        l.add(o);
        cal.clear();

        o = new Foo();
        o.setNum(200);
        o.setName("o2");
        cal.set(2011, 2, 3, 4, 5, 6);
        o.setCreatedAt(new Date(cal.getTimeInMillis()));
        l.add(o);
        cal.clear();

        o = new Foo();
        o.setNum(300);
        o.setName("o3");
        cal.set(2012, 3, 4, 5, 6, 7);
        o.setCreatedAt(new Date(cal.getTimeInMillis()));
        l.add(o);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(bout);
        oout.writeObject(l);
        oout.close();
        bout.close();

        byte[] serdata = bout.toByteArray();
        System.out.println("---------- serialized List<Foo> object : binary");
        System.out.println(Arrays.toString(serdata));
        System.out
                .println("---------- serialized List<Foo> object : ISO-8859-1 encoded string");
        String serstr = new String(serdata, "ISO-8859-1");
        System.out.println(serstr);

        ByteArrayInputStream bin = new ByteArrayInputStream(serdata);

        ObjectInputStream oin = new ObjectInputStream(bin);

        @SuppressWarnings("unchecked")
        List<Foo> l2 = (List<Foo>) oin.readObject();
        assertEquals("java.util.ArrayList", l2.getClass().getName());
        assertEquals(3, l2.size());
        for (int i = 0; i < l2.size(); i++) {
            o = l.get(i);
            Foo o2 = l2.get(i);
            assertEquals(o.getNum(), o2.getNum());
            assertEquals(o.getName(), o2.getName());
            assertEquals(o.getCreatedAt().getTime(), o2.getCreatedAt().getTime());
        }
    }

    @Test
    public void serializeHashMap() throws IOException, ClassNotFoundException {

        final Map<String, Foo> m = new HashMap<>();

        Calendar cal = Calendar.getInstance();
        Foo o = new Foo();
        o.setNum(100);
        o.setName("o1");
        cal.set(2010, 1, 2, 3, 4, 5);
        o.setCreatedAt(new Date(cal.getTimeInMillis()));
        m.put("k1", o);
        cal.clear();

        o = new Foo();
        o.setNum(200);
        o.setName("o2");
        cal.set(2011, 2, 3, 4, 5, 6);
        o.setCreatedAt(new Date(cal.getTimeInMillis()));
        m.put("k2", o);
        cal.clear();

        o = new Foo();
        o.setNum(300);
        o.setName("o3");
        cal.set(2012, 3, 4, 5, 6, 7);
        o.setCreatedAt(new Date(cal.getTimeInMillis()));
        m.put("k3", o);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(bout);
        oout.writeObject(m);
        oout.close();
        bout.close();

        byte[] serdata = bout.toByteArray();
        System.out.println("---------- serialized List<Foo> object : binary");
        System.out.println(Arrays.toString(serdata));
        System.out
                .println("---------- serialized List<Foo> object : ISO-8859-1 encoded string");
        String serstr = new String(serdata, "ISO-8859-1");
        System.out.println(serstr);

        ByteArrayInputStream bin = new ByteArrayInputStream(serdata);

        ObjectInputStream oin = new ObjectInputStream(bin);

        @SuppressWarnings("unchecked")
        Map<String, Foo> m2 = (Map<String, Foo>) oin.readObject();
        assertEquals("java.util.HashMap", m2.getClass().getName());
        assertEquals(3, m2.size());
        for (Map.Entry<String, Foo> e : m2.entrySet()) {
            o = m.get(e.getKey());
            Foo o2 = e.getValue();
            assertEquals(o.getNum(), o2.getNum());
            assertEquals(o.getName(), o2.getName());
            assertEquals(o.getCreatedAt().getTime(), o2.getCreatedAt().getTime());
        }
    }
}
