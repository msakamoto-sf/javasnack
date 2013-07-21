/*
 * Copyright 2013 the original author or authors.
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
package javasnack.snacks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import javasnack.tool.RandomString;

public class CollectionTypes1 implements Runnable {

    static final int NUM = 10;
    static final String DUMMY_STR = "";

    void HashMapDemos() {
        System.out.println("----------------------------------------------");
        System.out.println("HashMapDemos start.");
        HashMap<String, String> m = new HashMap<String, String>();
        String[] keys = new String[NUM];
        for (int i = 0; i < NUM; i++) {
            keys[i] = RandomString.get(10, 20);
        }
        for (String key : keys) {
            m.put(key, DUMMY_STR);
        }
        System.out.println("prepared key order : ");
        for (int i = 0; i < keys.length; i++) {
            System.out.println(String.format("key[%d] = %s", i, keys[i]));
        }
        System.out.println("actual HashMap keySet() order : ");
        Iterator<String> it = m.keySet().iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
        System.out.println("actual HashMap entrySet() order : ");
        Iterator<Entry<String, String>> it2 = m.entrySet().iterator();
        while (it2.hasNext()) {
            Entry<String, String> e = it2.next();
            System.out.println(String.format("HashMap[%s] = [%s]", e.getKey(),
                    e.getValue()));
        }
        System.out.println("HashMapDemos end.");
    }

    void HashMapDemos2() {
        System.out.println("----------------------------------------------");
        System.out.println("HashMapDemos2 start.");
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("abc", "ABC");
        m.put("def", "DEF100");
        m.put("def", "DEF200");
        m.put("ghi", "GHI");
        m.put("jkl", "JKL100");
        m.put("jkl", "JKL300");
        m.put("jkl", "JKL200");
        m.put("mno", "MNO");
        System.out.println("actual HashMap entrySet() order : ");
        Iterator<Entry<String, String>> it2 = m.entrySet().iterator();
        while (it2.hasNext()) {
            Entry<String, String> e = it2.next();
            System.out.println(String.format("HashMap[%s] = [%s]", e.getKey(),
                    e.getValue()));
        }
        System.out.println("HashMapDemos2 end.");
    }

    void LinkedHashMapDemos() {
        System.out.println("----------------------------------------------");
        System.out.println("LinkedHashMapDemos start.");
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        String[] keys = new String[NUM];
        for (int i = 0; i < NUM; i++) {
            keys[i] = RandomString.get(10, 20);
        }
        for (String key : keys) {
            m.put(key, DUMMY_STR);
        }
        System.out.println("prepared key order : ");
        for (int i = 0; i < keys.length; i++) {
            System.out.println(String.format("key[%d] = %s", i, keys[i]));
        }
        System.out.println("actual LinkedHashMap keySet() order : ");
        Iterator<String> it = m.keySet().iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
        System.out.println("actual LinkedHashMap entrySet() order : ");
        Iterator<Entry<String, String>> it2 = m.entrySet().iterator();
        while (it2.hasNext()) {
            Entry<String, String> e = it2.next();
            System.out.println(String.format("LinkedHashMap[%s] = [%s]",
                    e.getKey(), e.getValue()));
        }
        System.out.println("LinkedHashMapDemos end.");
    }

    void LinkedHashMapDemos2() {
        System.out.println("----------------------------------------------");
        System.out.println("LinkedHashMapDemos2 start.");
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put("abc", "ABC");
        m.put("def", "DEF100");
        m.put("def", "DEF200");
        m.put("ghi", "GHI");
        m.put("jkl", "JKL100");
        m.put("jkl", "JKL300");
        m.put("jkl", "JKL200");
        m.put("mno", "MNO");
        System.out.println("actual LinkedHashMap entrySet() order : ");
        Iterator<Entry<String, String>> it2 = m.entrySet().iterator();
        while (it2.hasNext()) {
            Entry<String, String> e = it2.next();
            System.out.println(String.format("LinkedHashMap[%s] = [%s]",
                    e.getKey(), e.getValue()));
        }
        System.out.println("LinkedHashMapDemos2 end.");
    }

    void LinkedHashMapDemos3() {
        System.out.println("----------------------------------------------");
        System.out.println("LinkedHashMapDemos3 start.");
        // create access-order linked hash map.
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>(16,
                0.75f, true);
        m.put("abc", "ABC");
        m.put("def", "DEF100");
        m.put("def", "DEF200");
        m.put("ghi", "GHI");
        m.put("jkl", "JKL100");
        m.put("jkl", "JKL300");
        m.put("jkl", "JKL200");
        m.put("mno", "MNO");
        m.get("def");
        m.get("jkl");
        System.out.println("actual LinkedHashMap entrySet() order : ");
        Iterator<Entry<String, String>> it2 = m.entrySet().iterator();
        while (it2.hasNext()) {
            Entry<String, String> e = it2.next();
            System.out.println(String.format("LinkedHashMap[%s] = [%s]",
                    e.getKey(), e.getValue()));
        }
        System.out.println("LinkedHashMapDemos3 end.");
    }

    void TreeMapDemos() {
        System.out.println("----------------------------------------------");
        System.out.println("TreeMapDemos start.");
        TreeMap<String, String> m = new TreeMap<String, String>();
        String[] keys = new String[NUM];
        for (int i = 0; i < NUM; i++) {
            keys[i] = RandomString.get(10, 20);
        }
        for (String key : keys) {
            m.put(key, DUMMY_STR);
        }
        System.out.println("prepared key order : ");
        for (int i = 0; i < keys.length; i++) {
            System.out.println(String.format("key[%d] = %s", i, keys[i]));
        }
        System.out.println("actual TreeMap keySet() order : ");
        Iterator<String> it = m.keySet().iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
        System.out.println("actual TreeHashMap entrySet() order : ");
        Iterator<Entry<String, String>> it2 = m.entrySet().iterator();
        while (it2.hasNext()) {
            Entry<String, String> e = it2.next();
            System.out.println(String.format("TreeMap[%s] = [%s]", e.getKey(),
                    e.getValue()));
        }
        System.out.println("TreeMapDemos end.");
    }

    void TreeMapDemos2() {
        System.out.println("----------------------------------------------");
        System.out.println("TreeMapDemos2 start.");
        TreeMap<String, String> m = new TreeMap<String, String>();
        m.put("def", "DEF100");
        m.put("def", "DEF200");
        m.put("abc", "ABC");
        m.put("mno", "MNO");
        m.put("jkl", "JKL100");
        m.put("jkl", "JKL300");
        m.put("jkl", "JKL200");
        m.put("ghi", "GHI");
        System.out.println("actual TreeMap entrySet() order : ");
        Iterator<Entry<String, String>> it2 = m.entrySet().iterator();
        while (it2.hasNext()) {
            Entry<String, String> e = it2.next();
            System.out.println(String.format("TreeMap[%s] = [%s]", e.getKey(),
                    e.getValue()));
        }
        System.out.println("TreeMapDemos2 end.");
    }

    @Override
    public void run() {
        HashMapDemos();
        HashMapDemos2();
        LinkedHashMapDemos();
        LinkedHashMapDemos2();
        LinkedHashMapDemos3();
        TreeMapDemos();
        TreeMapDemos2();
    }

}
