package javasnack.ojcp.se8gold.chapter03;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

public class Test03SortedSetsAndMaps {
    @Test
    public void testSortedSetDemo() {
        /* SortedSet interface の主なメソッドのデモ
         * 
         * Comparator<? super E> comparator()
         * E first()
         * E last()
         * SortedSet<E> headSet​(E toElement)
         * SortedSet<E> tailSet​(E fromElement)
         * SortedSet<E> subSet​(E fromElement, E toElement)
         */

        // comparator 未指定だと、comparator() はnull(= 自然順 or Comparable によるソート)
        SortedSet<PersonComparable> s0 = new TreeSet<>();
        assertThat(s0.comparator()).isNull();

        // comparator 指定ありだと、comparator() で comparator を返す。
        Comparator<Person> cmp = Person.comparator();
        SortedSet<Person> s1 = new TreeSet<>(cmp);
        assertThat(s1.comparator()).isEqualTo(cmp);
        s1.add(Person.of("aa", "aa", 10));
        s1.add(Person.of("aa", "aa", 20));
        s1.add(Person.of("aa", "bb", 10));
        s1.add(Person.of("aa", "bb", 20));
        s1.add(Person.of("bb", "cc", 10));
        s1.add(Person.of("bb", "cc", 20));
        assertThat(s1.first()).isEqualTo(Person.of("aa", "aa", 10)); // head じゃなくて first
        assertThat(s1.last()).isEqualTo(Person.of("bb", "cc", 20)); // tail じゃなくて last

        SortedSet<Person> s1h = s1.headSet(Person.of("aa", "bb", 10));
        // 引数「よりも」小さな要素になるため、引数自体は含まれない。
        assertThat(s1h.size()).isEqualTo(2);
        assertThat(s1h.contains(Person.of("aa", "aa", 10))).isTrue();
        assertThat(s1h.contains(Person.of("aa", "aa", 20))).isTrue();
        // headSet() はもとのSetのsubsetなので、headSet()への追加はもとのSetにも反映される。
        s1h.add(Person.of("aa", "aa", 30));
        assertThat(s1.contains(Person.of("aa", "aa", 30))).isTrue();
        // ただし headSet(E) で指定した範囲外を追加しようとすると実行時例外
        assertThatThrownBy(() -> {
            s1h.add(Person.of("aa", "bb", 11));
        }).isInstanceOf(IllegalArgumentException.class);

        SortedSet<Person> s1t = s1.tailSet(Person.of("aa", "bb", 20));
        // 引数「と等しいかそれよりも」大きな要素になるため、引数自体を含む。
        assertThat(s1t.size()).isEqualTo(3);
        assertThat(s1t.contains(Person.of("aa", "bb", 20))).isTrue();
        assertThat(s1t.contains(Person.of("bb", "cc", 10))).isTrue();
        assertThat(s1t.contains(Person.of("bb", "cc", 20))).isTrue();
        // tailSet() はもとのSetのsubsetなので、tailSet()への追加はもとのSetにも反映される。
        s1t.add(Person.of("bb", "cc", 30));
        assertThat(s1.contains(Person.of("bb", "cc", 30))).isTrue();
        // ただし tailSet(E) で指定した範囲外を追加しようとすると実行時例外
        assertThatThrownBy(() -> {
            s1t.add(Person.of("aa", "bb", 19));
        }).isInstanceOf(IllegalArgumentException.class);

        // subSet(from, to) : from < to で from は含む, to は含まない。
        SortedSet<Person> s1sub = s1.subSet(Person.of("aa", "aa", 20), Person.of("aa", "bb", 20));
        assertThat(s1sub.size()).isEqualTo(3);
        assertThat(s1sub.contains(Person.of("aa", "aa", 20))).isTrue();
        assertThat(s1sub.contains(Person.of("aa", "aa", 30))).isTrue();
        assertThat(s1sub.contains(Person.of("aa", "bb", 10))).isTrue();
        // subSet() はもとのSetのsubsetなので、subSet()への追加はもとのSetにも反映される。
        s1sub.add(Person.of("aa", "bb", 5));
        assertThat(s1.contains(Person.of("aa", "bb", 5))).isTrue();
        // ただし subSet(from, to) で指定した範囲外を追加しようとすると実行時例外
        assertThatThrownBy(() -> {
            s1sub.add(Person.of("aa", "aa", 19));
        }).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> {
            s1sub.add(Person.of("aa", "bb", 20));
        }).isInstanceOf(IllegalArgumentException.class);

        // subSet() で from > to だと実行時例外
        assertThatThrownBy(() -> {
            s1.subSet(Person.of("aa", "aa", 20), Person.of("aa", "aa", 19));
        }).isInstanceOf(IllegalArgumentException.class);

        // subSet() で from = to だと、toを含まないので、空になる。
        SortedSet<Person> s1subx = s1.subSet(Person.of("aa", "aa", 20), Person.of("aa", "aa", 20));
        assertThat(s1subx.size()).isEqualTo(0);
    }

    @Test
    public void testSortedMapDemo() {
        /* SortedMap interface の主なメソッドのデモ
         * 
         * Comparator<? super K> comparator()
         * K firstKey()
         * K lastKey()
         * SortedMap<K,​V> headMap​(K toKey)
         * SortedMap<K,​V> tailMap​(K fromKey)
         * SortedMap<K,​V> subMap​(K fromKey, K toKey)
         */

        // comparator 未指定だと、comparator() はnull(= 自然順 or Comparable によるソート)
        SortedMap<PersonComparable, Integer> m0 = new TreeMap<>();
        assertThat(m0.comparator()).isNull();

        // comparator 指定ありだと、comparator() で comparator を返す。
        Comparator<Person> cmp = Person.comparator();
        SortedMap<Person, Integer> m1 = new TreeMap<>(cmp);
        assertThat(m1.comparator()).isEqualTo(cmp);
        m1.put(Person.of("aa", "aa", 10), 100);
        m1.put(Person.of("aa", "aa", 20), 200);
        m1.put(Person.of("aa", "bb", 10), 300);
        m1.put(Person.of("aa", "bb", 20), 400);
        m1.put(Person.of("bb", "cc", 10), 500);
        m1.put(Person.of("bb", "cc", 20), 600);
        assertThat(m1.firstKey()).isEqualTo(Person.of("aa", "aa", 10)); // head じゃなくて first
        assertThat(m1.lastKey()).isEqualTo(Person.of("bb", "cc", 20)); // tail じゃなくて last

        SortedMap<Person, Integer> m1h = m1.headMap(Person.of("aa", "bb", 10));
        // 引数「よりも」小さな要素になるため、引数自体は含まれない。
        assertThat(m1h.size()).isEqualTo(2);
        assertThat(m1h.get(Person.of("aa", "aa", 10))).isEqualTo(100);
        assertThat(m1h.get(Person.of("aa", "aa", 20))).isEqualTo(200);
        // headMap() はもとのMapのsubsetなので、headMap()への追加はもとのMapにも反映される。
        m1h.put(Person.of("aa", "aa", 30), 250);
        assertThat(m1.get(Person.of("aa", "aa", 30))).isEqualTo(250);
        // ただし headMap(E) で指定した範囲外を追加しようとすると実行時例外
        assertThatThrownBy(() -> {
            m1h.put(Person.of("aa", "bb", 11), 301);
        }).isInstanceOf(IllegalArgumentException.class);

        SortedMap<Person, Integer> m1t = m1.tailMap(Person.of("aa", "bb", 20));
        // 引数「と等しいかそれよりも」大きな要素になるため、引数自体を含む。
        assertThat(m1t.size()).isEqualTo(3);
        assertThat(m1t.get(Person.of("aa", "bb", 20))).isEqualTo(400);
        assertThat(m1t.get(Person.of("bb", "cc", 10))).isEqualTo(500);
        assertThat(m1t.get(Person.of("bb", "cc", 20))).isEqualTo(600);
        // tailMap() はもとのMapのsubsetなので、tailMap()への追加はもとのMapにも反映される。
        m1t.put(Person.of("bb", "cc", 30), 650);
        assertThat(m1.get(Person.of("bb", "cc", 30))).isEqualTo(650);
        // ただし tailMap(E) で指定した範囲外を追加しようとすると実行時例外
        assertThatThrownBy(() -> {
            m1t.put(Person.of("aa", "bb", 19), 350);
        }).isInstanceOf(IllegalArgumentException.class);

        // subMap(from, to) : from < to で from は含む, to は含まない。
        SortedMap<Person, Integer> m1sub = m1.subMap(Person.of("aa", "aa", 20), Person.of("aa", "bb", 20));
        assertThat(m1sub.size()).isEqualTo(3);
        assertThat(m1sub.get(Person.of("aa", "aa", 20))).isEqualTo(200);
        assertThat(m1sub.get(Person.of("aa", "aa", 30))).isEqualTo(250);
        assertThat(m1sub.get(Person.of("aa", "bb", 10))).isEqualTo(300);
        // subMap() はもとのMapのsubsetなので、subMap()への追加はもとのMapにも反映される。
        m1sub.put(Person.of("aa", "bb", 5), 290);
        assertThat(m1.get(Person.of("aa", "bb", 5))).isEqualTo(290);
        // ただし subMap(from, to) で指定した範囲外を追加しようとすると実行時例外
        assertThatThrownBy(() -> {
            m1sub.put(Person.of("aa", "aa", 19), 199);
        }).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> {
            m1sub.put(Person.of("aa", "bb", 20), 200);
        }).isInstanceOf(IllegalArgumentException.class);

        // subMap() で from > to だと実行時例外
        assertThatThrownBy(() -> {
            m1.subMap(Person.of("aa", "aa", 20), Person.of("aa", "aa", 19));
        }).isInstanceOf(IllegalArgumentException.class);

        // subMap() で from = to だと、toを含まないので、空になる。
        SortedMap<Person, Integer> m1subx = m1.subMap(Person.of("aa", "aa", 20), Person.of("aa", "aa", 20));
        assertThat(m1subx.size()).isEqualTo(0);
    }
}
