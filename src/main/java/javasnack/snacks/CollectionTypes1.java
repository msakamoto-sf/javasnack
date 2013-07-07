package javasnack.snacks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import javasnack.tool.RandomString;

public class CollectionTypes1 implements Runnable {

    static final int NUM = 20;
    static final String DUMMY_STR = "";

    void HashMapDemos() {
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
        System.out.println("HashMapDemos end.");
    }

    void LinkedHashMapDemos() {
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
        System.out.println("LinkedHashMapDemos end.");
    }

    void TreeMapDemos() {
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
        System.out.println("TreeMapDemos end.");
    }

    @Override
    public void run() {
        HashMapDemos();
        LinkedHashMapDemos();
        TreeMapDemos();
    }

}
