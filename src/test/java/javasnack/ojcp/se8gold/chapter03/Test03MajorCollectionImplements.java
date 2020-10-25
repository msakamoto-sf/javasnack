package javasnack.ojcp.se8gold.chapter03;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.junit.jupiter.api.Test;

import javasnack.snacks.perfs.list.PerfArrayListFinePutGet;
import javasnack.snacks.perfs.list.PerfLinkedListFinePutGet;
import javasnack.snacks.perfs.map.PerfHashMapFinePutGet;
import javasnack.snacks.perfs.map.PerfLinkedHashMapFinePutGet;
import javasnack.snacks.perfs.map.PerfTreeMapFinePutGet;

public class Test03MajorCollectionImplements {
    // Map, List, Set の主な実装のデモ

    /**
     * {@link ArrayList}, {@link LinkedList} の特性については
     * {@link PerfArrayListFinePutGet} と {@link PerfLinkedListFinePutGet}
     * を参照。
     */
    @Test
    public void testListImplementClasses() {
        List<String> l1 = new ArrayList<>(2);
        l1.add("aa");
        l1.add("bb");
        l1.add("cc"); // capacitiy 以上を追加すると、内部で自動的に配列を拡張してくれる。
        List<String> l2 = new LinkedList<>(l1);
        l2.add("dd");
        l2.add("ee");
        /* Vector はメソッドが同期化される。
         * そのため multi thread でのアクセスが頻発するのなら便利だが、
         * そうでないユースケースでは同期化コストが高くなってしまう。
         * multi thread についても Collections.synchronizedList() を経由したほうが良さそう。
         */
        List<String> l3 = new Vector<>(l2);
        l3.add("ff");
        l3.add("gg");
        assertThat(l3).isEqualTo(List.of("aa", "bb", "cc", "dd", "ee", "ff", "gg"));

        // Collection.remove(Object o) と List.remove(int index) 
        // のどちらが呼ばれるか分かりづらいケース
        List<Integer> l4 = new ArrayList<>();
        l4.add(1);
        l4.add(2);
        l4.add(null); // null OK.
        l4.add(0);
        assertThat(l4.get(0)).isEqualTo(1);
        assertThat(l4.get(1)).isEqualTo(2);
        assertThat(l4.get(2)).isEqualTo(null);
        assertThat(l4.get(3)).isEqualTo(0);

        l4.remove(0);
        assertThat(l4.size()).isEqualTo(3);
        assertThat(l4.get(0)).isEqualTo(2);
        assertThat(l4.get(1)).isEqualTo(null);
        assertThat(l4.get(2)).isEqualTo(0);
        // -> remove(Object o) ではなく remove(int index) が呼ばれている。
        // via: List.remove(int index) で引数の型が完全一致するメソッドがある以上、
        // そちらが最優先で呼ばれ、 auto boxing が必要となる Collection.remove(Object) は呼ばれない。

        l4.remove(Integer.valueOf(0));
        assertThat(l4.size()).isEqualTo(2);
        assertThat(l4.get(0)).isEqualTo(2);
        assertThat(l4.get(1)).isEqualTo(null);
        // -> remove(Object o) が呼ばれている。
        // via: Collection.remove(Object) の方がクラス継承上で完全一致するためそちらが呼ばれる。
    }

    /**
     * キー順序の特性だけに着目すると、
     * {@link HashMap} はキー順序の保証無し、
     * {@link TreeMap} は自然順でソートされる、
     * {@link LinkedHashMap} は追加した順になる。
     * 
     * もう少し詳しい特性については
     * {@link PerfHashMapFinePutGet} ,
     * {@link PerfTreeMapFinePutGet} ,
     * {@link PerfLinkedHashMapFinePutGet} 参照。
     */
    @Test
    public void testMapImplementClasses() {
        Map<String, Integer> m1 = new HashMap<>();
        m1.put("aa", 100);
        m1.put("bb", 200);
        m1.put("cc", 300);
        // キー挿入順序は内部で保存されない。以下は偶然、put()した順で取り出せている。
        Iterator<Entry<String, Integer>> it1 = m1.entrySet().iterator();
        Entry<String, Integer> e = it1.next();
        assertThat(e.getKey()).isEqualTo("aa");
        e = it1.next();
        assertThat(e.getKey()).isEqualTo("bb");
        e = it1.next();
        assertThat(e.getKey()).isEqualTo("cc");

        m1 = new LinkedHashMap<>();
        m1.put("aa", 100);
        m1.put("cc", 300);
        m1.put("bb", 200);
        // LinkedHashMap の場合は、キー挿入順序を内部で保存している。
        // -> put()した順序を期待できる。
        it1 = m1.entrySet().iterator();
        e = it1.next();
        assertThat(e.getKey()).isEqualTo("aa");
        e = it1.next();
        assertThat(e.getKey()).isEqualTo("cc");
        e = it1.next();
        assertThat(e.getKey()).isEqualTo("bb");

        m1 = new TreeMap<>();
        m1.put("cc", 300);
        m1.put("bb", 200);
        m1.put("aa", 100);
        // TreeMap の場合は、キーを自然順にソートして保存している。
        it1 = m1.entrySet().iterator();
        e = it1.next();
        assertThat(e.getKey()).isEqualTo("aa");
        e = it1.next();
        assertThat(e.getKey()).isEqualTo("bb");
        e = it1.next();
        assertThat(e.getKey()).isEqualTo("cc");

        m1 = new Hashtable<>();
        /* Hashtable はメソッドが同期化される。
         * そのため multi thread でのアクセスが頻発するのなら便利だが、
         * そうでないユースケースでは同期化コストが高くなってしまう。
         * multi thread についても Collections.synchronizedMap() を経由したほうが良さそう。
         */
        m1.put("aa", 100);
        m1.put("bb", 200);
        m1.put("cc", 300);
        // キー挿入順序は内部で保存されない。以下は偶然、put()した順で取り出せている。
        it1 = m1.entrySet().iterator();
        e = it1.next();
        assertThat(e.getKey()).isEqualTo("aa");
        e = it1.next();
        assertThat(e.getKey()).isEqualTo("bb");
        e = it1.next();
        assertThat(e.getKey()).isEqualTo("cc");

        // key の型の区別が分かりづらい/nullのケース
        Map<Number, String> m2 = new HashMap<>();
        m2.put(100, "aa");
        m2.put(Integer.valueOf(100), "bb");
        m2.put(null, "cc"); // null OK
        assertThat(m2.size()).isEqualTo(2);
        assertThat(m2.get(100)).isEqualTo("bb"); // 後からの Integer.valueOf(100) で上書きされた
        assertThat(m2.get(Integer.valueOf(100))).isEqualTo("bb");
        assertThat(m2.get(null)).isEqualTo("cc"); // null key OK
    }

    @Test
    public void testTreeMapAndSetRejectNonComparableKeyDemo() {
        // Comparable でない class を key にして、Comparator も指定していないと、put() で ClassCastException
        Map<Person, Integer> m1 = new TreeMap<>();
        assertThatThrownBy(() -> {
            m1.put(Person.of("aa", "aa", 10), 100);
        }).isInstanceOf(ClassCastException.class);

        // Comparable でない class を key にしてても、Comparator を指定すればOK.
        Map<Person, Integer> m2 = new TreeMap<>(Person.comparator());
        m2.put(Person.of("aa", "bb", 20), 400);
        m2.put(Person.of("aa", "bb", 10), 300);
        m2.put(Person.of("aa", "aa", 20), 200);
        m2.put(Person.of("aa", "aa", 10), 100);

        // ソートされた順序で取り出せる(内部的にもソートされて保存されてる)
        Iterator<Entry<Person, Integer>> it = m2.entrySet().iterator();
        Entry<Person, Integer> e = it.next();
        assertThat(e.getKey()).isEqualTo(Person.of("aa", "aa", 10));
        assertThat(e.getValue()).isEqualTo(100);
        e = it.next();
        assertThat(e.getKey()).isEqualTo(Person.of("aa", "aa", 20));
        assertThat(e.getValue()).isEqualTo(200);
        e = it.next();
        assertThat(e.getKey()).isEqualTo(Person.of("aa", "bb", 10));
        assertThat(e.getValue()).isEqualTo(300);
        e = it.next();
        assertThat(e.getKey()).isEqualTo(Person.of("aa", "bb", 20));
        assertThat(e.getValue()).isEqualTo(400);

        // Comparable な class を key にすれば、Comparatorの指定は不要。
        Map<PersonComparable, Integer> m3 = new TreeMap<>();
        m3.put(PersonComparable.of("aa", "bb", 20), 401);
        m3.put(PersonComparable.of("aa", "bb", 10), 301);
        m3.put(PersonComparable.of("aa", "aa", 20), 201);
        m3.put(PersonComparable.of("aa", "aa", 10), 101);

        // ソートされた順序で取り出せる(内部的にもソートされて保存されてる)
        Iterator<Entry<PersonComparable, Integer>> it2 = m3.entrySet().iterator();
        Entry<PersonComparable, Integer> e2 = it2.next();
        assertThat(e2.getKey()).isEqualTo(PersonComparable.of("aa", "aa", 10));
        assertThat(e2.getValue()).isEqualTo(101);
        e2 = it2.next();
        assertThat(e2.getKey()).isEqualTo(PersonComparable.of("aa", "aa", 20));
        assertThat(e2.getValue()).isEqualTo(201);
        e2 = it2.next();
        assertThat(e2.getKey()).isEqualTo(PersonComparable.of("aa", "bb", 10));
        assertThat(e2.getValue()).isEqualTo(301);
        e2 = it2.next();
        assertThat(e2.getKey()).isEqualTo(PersonComparable.of("aa", "bb", 20));
        assertThat(e2.getValue()).isEqualTo(401);

        // Comparable でない class で、Comparator も指定していないと、add() で ClassCastException
        Set<Person> s1 = new TreeSet<>();
        assertThatThrownBy(() -> {
            s1.add(Person.of("aa", "aa", 10));
        }).isInstanceOf(ClassCastException.class);

        // Comparable でない class でも、Comparator を指定すればOK.
        Set<Person> s2 = new TreeSet<>(Person.comparator());
        s2.add(Person.of("aa", "bb", 20));
        s2.add(Person.of("aa", "bb", 10));
        s2.add(Person.of("aa", "aa", 20));
        s2.add(Person.of("aa", "aa", 10));

        // ソートされた順序で取り出せる(内部的にもソートされて保存されてる)
        Iterator<Person> it3 = s2.iterator();
        assertThat(it3.next()).isEqualTo(Person.of("aa", "aa", 10));
        assertThat(it3.next()).isEqualTo(Person.of("aa", "aa", 20));
        assertThat(it3.next()).isEqualTo(Person.of("aa", "bb", 10));
        assertThat(it3.next()).isEqualTo(Person.of("aa", "bb", 20));

        // Comparable な class なら、Comparatorの指定は不要。
        Set<PersonComparable> s3 = new TreeSet<>();
        s3.add(PersonComparable.of("aa", "bb", 20));
        s3.add(PersonComparable.of("aa", "bb", 10));
        s3.add(PersonComparable.of("aa", "aa", 20));
        s3.add(PersonComparable.of("aa", "aa", 10));

        // ソートされた順序で取り出せる(内部的にもソートされて保存されてる)
        Iterator<PersonComparable> it4 = s3.iterator();
        assertThat(it4.next()).isEqualTo(PersonComparable.of("aa", "aa", 10));
        assertThat(it4.next()).isEqualTo(PersonComparable.of("aa", "aa", 20));
        assertThat(it4.next()).isEqualTo(PersonComparable.of("aa", "bb", 10));
        assertThat(it4.next()).isEqualTo(PersonComparable.of("aa", "bb", 20));
    }

    @Test
    public void testSetImplementClasses() {
        Set<String> s1 = new HashSet<>();
        s1.add("aa");
        s1.add("bb");
        s1.add("cc");
        // 要素追加順序は内部で保存されない。以下は偶然、add()した順で取り出せている。
        Iterator<String> it1 = s1.iterator();
        assertThat(it1.next()).isEqualTo("aa");
        assertThat(it1.next()).isEqualTo("bb");
        assertThat(it1.next()).isEqualTo("cc");

        s1 = new LinkedHashSet<>();
        s1.add("aa");
        s1.add("cc");
        s1.add("bb");
        // LinkedHashSet の場合は、要素追加順序を内部で保存している。
        // -> add()した順序を期待できる。
        it1 = s1.iterator();
        assertThat(it1.next()).isEqualTo("aa");
        assertThat(it1.next()).isEqualTo("cc");
        assertThat(it1.next()).isEqualTo("bb");

        s1 = new TreeSet<>();
        s1.add("cc");
        s1.add("bb");
        s1.add("aa");
        // TreeSet の場合は、要素を自然順にソートして保存している。
        it1 = s1.iterator();
        assertThat(it1.next()).isEqualTo("aa");
        assertThat(it1.next()).isEqualTo("bb");
        assertThat(it1.next()).isEqualTo("cc");

        // Set の場合は Collections.synchronizedSet() を経由して同期化されたインスタンスを使う。

        // 型の区別が分かりづらい/nullのケース
        Set<Number> s2 = new HashSet<>();
        s2.add(100);
        s2.add(Integer.valueOf(100));
        s2.add(null); // null OK
        assertThat(s2.size()).isEqualTo(2); // 100 も Integer.valueOf(100) も同一とみなされている。
        assertThat(s2.contains(100)).isTrue();
        assertThat(s2.contains(Integer.valueOf(100))).isTrue();
        assertThat(s2.contains(null)).isTrue(); // null OK
    }

    static class Person1 {
        final String name;
        final int age;

        Person1(final String name, final int age) {
            this.name = name;
            this.age = age;
        }
    }

    static class Person2 {
        final String name;
        final int age;

        Person2(final String name, final int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + age;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Person2 other = (Person2) obj;
            if (age != other.age) {
                return false;
            }
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
            return true;
        }
    }

    @Test
    public void testSetElementDuplication() {
        Person1 p11 = new Person1("aaa", 10);
        Person1 p12 = new Person1("bbb", 20);
        Person1 p13 = new Person1("ccc", 30);
        Person1 p14 = new Person1("aaa", 10); // 中身は同じだが equals()/hashCode() override 無し
        Person2 p21 = new Person2("aaa", 10);
        Person2 p22 = new Person2("bbb", 20);
        Person2 p23 = new Person2("ccc", 30);
        Person2 p24 = new Person2("aaa", 10); // 中身は同じで equals()/hashCode() override 有り

        Set<Person1> s1 = new HashSet<>();
        s1.add(p11);
        s1.add(p11); // 同じ参照を追加しても重複扱いになる
        s1.add(p12);
        s1.add(p13);
        s1.add(p14); // equals()/hashCode() override されていないので別の値として扱われる。
        assertThat(s1.size()).isEqualTo(4);
        // -> p11 と p14 が別要素となり、要素数は4個になる。

        assertThat(s1.contains(p11)).isTrue();
        assertThat(s1.contains(p12)).isTrue();
        assertThat(s1.contains(p13)).isTrue();
        assertThat(s1.contains(p14)).isTrue();

        Set<Person2> s2 = new HashSet<>();
        s2.add(p21);
        s2.add(p21); // 同じ参照を追加しても重複扱いになる
        s2.add(p22);
        s2.add(p23);
        s2.add(p24); // equals()/hashCode() override により p21 と同じ値(重複)として扱われる。
        assertThat(s2.size()).isEqualTo(3);
        // -> p24 = p21 となり、要素数は3個になる。

        assertThat(s2.contains(p21)).isTrue();
        assertThat(s2.contains(p22)).isTrue();
        assertThat(s2.contains(p23)).isTrue();
        assertThat(s2.contains(p24)).isTrue();
    }
}
