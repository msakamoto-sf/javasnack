package javasnack.ojcp.se8gold.chapter03;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

public class Test03NavigableSetAndMaps {
    @Test
    public void testNavigableSetDemo() {
        /* NavigableSet interface の主なメソッドのデモ
         * 
         * E higher​(E e)
         * E lower​(E e)
         * E ceiling​(E e)
         * E floor​(E e)
         * NavigableSet<E> headSet​(E toElement, boolean inclusive)
         * NavigableSet<E> tailSet​(E fromElement, boolean inclusive)
         * NavigableSet<E> subSet​(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive)
         * Iterator<E> descendingIterator()
         * NavigableSet<E> descendingSet()
         * E pollFirst()
         * E pollLast()
         */
        NavigableSet<Person> s1 = new TreeSet<>(Person.comparator());
        s1.add(Person.of("aa", "aa", 10));
        s1.add(Person.of("aa", "aa", 20));
        s1.add(Person.of("aa", "bb", 10));
        s1.add(Person.of("aa", "bb", 20));
        s1.add(Person.of("bb", "cc", 10));
        s1.add(Person.of("bb", "cc", 20));
        // higher(3) -> 1 2 3 [4] 5 : 引数自体は含まない
        assertThat(s1.higher(Person.of("aa", "bb", 10))).isEqualTo(Person.of("aa", "bb", 20));
        assertThat(s1.higher(Person.of("aa", "bb", 19))).isEqualTo(Person.of("aa", "bb", 20));
        // lower(3) -> 1 [2] 3 4 5 : 引数自体は含まない
        assertThat(s1.lower(Person.of("aa", "bb", 10))).isEqualTo(Person.of("aa", "aa", 20));
        assertThat(s1.lower(Person.of("aa", "aa", 21))).isEqualTo(Person.of("aa", "aa", 20));
        // ceiling(4) -> 1 2 3 [4] 5  : 引数自体を含む
        // ceiling(3.5) -> 1 2 3 [4] 5 
        assertThat(s1.ceiling(Person.of("aa", "bb", 10))).isEqualTo(Person.of("aa", "bb", 10));
        assertThat(s1.ceiling(Person.of("aa", "bb", 19))).isEqualTo(Person.of("aa", "bb", 20));
        // floor(3) -> 1 2 [3] 4 5 : 引数自体を含む
        // floor(3.5) -> 1 2 [3] 4 5
        assertThat(s1.floor(Person.of("aa", "bb", 10))).isEqualTo(Person.of("aa", "bb", 10));
        assertThat(s1.floor(Person.of("aa", "aa", 21))).isEqualTo(Person.of("aa", "aa", 20));

        // SortedSet#headSet()と異なり、引数自身を含むか選べる。
        NavigableSet<Person> s1h = s1.headSet(Person.of("aa", "bb", 10), true);
        assertThat(s1h.size()).isEqualTo(3);
        assertThat(s1h.contains(Person.of("aa", "aa", 10))).isTrue();
        assertThat(s1h.contains(Person.of("aa", "aa", 20))).isTrue();
        assertThat(s1h.contains(Person.of("aa", "bb", 10))).isTrue();
        s1h = s1.headSet(Person.of("aa", "bb", 10), false); // 含まないバージョン = SortedSet#headSet()
        assertThat(s1h.size()).isEqualTo(2);
        assertThat(s1h.contains(Person.of("aa", "aa", 10))).isTrue();
        assertThat(s1h.contains(Person.of("aa", "aa", 20))).isTrue();
        // もとのSetにも反映されるのは SortedSet#headSet() と同じ。
        s1h.add(Person.of("aa", "aa", 30));
        assertThat(s1.contains(Person.of("aa", "aa", 30))).isTrue();
        // 範囲外追加で実行時例外も SortedSet#headSet() と同じ
        assertThatThrownBy(() -> {
            s1.headSet(Person.of("aa", "bb", 10), false).add(Person.of("aa", "bb", 11));
        }).isInstanceOf(IllegalArgumentException.class);

        // SortedSet#tailSet()と異なり、引数自身を含むか選べる。
        NavigableSet<Person> s1t = s1.tailSet(Person.of("aa", "bb", 20), false);
        assertThat(s1t.size()).isEqualTo(2);
        assertThat(s1t.contains(Person.of("bb", "cc", 10))).isTrue();
        assertThat(s1t.contains(Person.of("bb", "cc", 20))).isTrue();
        s1t = s1.tailSet(Person.of("aa", "bb", 20), true); // 含むバージョン = SortedSet#tailSet()
        assertThat(s1t.size()).isEqualTo(3);
        assertThat(s1t.contains(Person.of("aa", "bb", 20))).isTrue();
        assertThat(s1t.contains(Person.of("bb", "cc", 10))).isTrue();
        assertThat(s1t.contains(Person.of("bb", "cc", 20))).isTrue();
        // もとのSetにも反映されるのは SortedSet#tailSet() と同じ。
        s1t.add(Person.of("bb", "cc", 30));
        assertThat(s1.contains(Person.of("bb", "cc", 30))).isTrue();
        // 範囲外追加で実行時例外も SortedSet#tailSet() と同じ
        assertThatThrownBy(() -> {
            s1.tailSet(Person.of("aa", "bb", 20), true).add(Person.of("aa", "bb", 19));
        }).isInstanceOf(IllegalArgumentException.class);

        // SortedSet#subSet() と異なり、from/to それぞれの引数自身を含むか選べる。
        // -> SortedSet#subSet() の反対に、fromは含めず to を含めてみる。
        NavigableSet<Person> s1sub = s1.subSet(
                Person.of("aa", "aa", 20), false,
                Person.of("aa", "bb", 20), true);
        assertThat(s1sub.size()).isEqualTo(3);
        assertThat(s1sub.contains(Person.of("aa", "aa", 30))).isTrue();
        assertThat(s1sub.contains(Person.of("aa", "bb", 10))).isTrue();
        assertThat(s1sub.contains(Person.of("aa", "bb", 20))).isTrue();
        // もとのSetにも反映されるのは SortedSet#subSet() と同じ。
        s1sub.add(Person.of("aa", "bb", 5));
        assertThat(s1.contains(Person.of("aa", "bb", 5))).isTrue();
        // 範囲外追加で実行時例外も SortedSet#subSet() と同じ
        assertThatThrownBy(() -> {
            s1sub.add(Person.of("aa", "aa", 20));
        }).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> {
            s1sub.add(Person.of("aa", "bb", 21));
        }).isInstanceOf(IllegalArgumentException.class);

        // 逆順にソートしたiteratorを返すことができる。
        Iterator<Person> it = s1.descendingIterator();
        assertThat(it.next()).isEqualTo(Person.of("bb", "cc", 30));
        assertThat(it.next()).isEqualTo(Person.of("bb", "cc", 20));
        assertThat(it.next()).isEqualTo(Person.of("bb", "cc", 10));
        // ...(snip)...
        // 逆順にソートしたSetを返すことができる。
        NavigableSet<Person> s2 = s1.descendingSet();
        it = s2.iterator();
        assertThat(it.next()).isEqualTo(Person.of("bb", "cc", 30));
        assertThat(it.next()).isEqualTo(Person.of("bb", "cc", 20));
        assertThat(it.next()).isEqualTo(Person.of("bb", "cc", 10));
        // ...(snip)...

        // s1sub : [aa, aa, 30], [aa, bb, 5], [aa, bb, 10], [aa, bb, 20]
        assertThat(s1sub.pollFirst()).isEqualTo(Person.of("aa", "aa", 30));
        assertThat(s1sub.pollLast()).isEqualTo(Person.of("aa", "bb", 20));
        assertThat(s1sub.pollFirst()).isEqualTo(Person.of("aa", "bb", 5));
        assertThat(s1sub.pollLast()).isEqualTo(Person.of("aa", "bb", 10));
        assertThat(s1sub.pollFirst()).isNull();
        assertThat(s1sub.pollLast()).isNull();
        // subSet() からpoll{First|Last}()したものは、もとのSetからも削除されている。
        assertThat(s1.contains(Person.of("aa", "aa", 30))).isFalse();
        assertThat(s1.contains(Person.of("aa", "bb", 5))).isFalse();
        assertThat(s1.contains(Person.of("aa", "bb", 10))).isFalse();
        assertThat(s1.contains(Person.of("aa", "bb", 20))).isFalse();
    }

    static void assertEntry(final Entry<Person, Integer> e, final Person expectedPerson, final int expectedInt) {
        assertThat(e.getKey()).isEqualTo(expectedPerson);
        assertThat(e.getValue()).isEqualTo(expectedInt);
    }

    @Test
    public void testNavigableMapDemo() {
        /* NavigableMap interface の主なメソッドのデモ
         * 
         * Map.Entry<K,​V> firstEntry()
         * Map.Entry<K,​V> lastEntry()
         * Map.Entry<K,​V> higherEntry​(K key)
         * K higherKey​(K key)
         * Map.Entry<K,​V> lowerEntry​(K key)
         * K lowerKey​(K key)
         * Map.Entry<K,​V> ceilingEntry​(K key)
         * K ceilingKey​(K key)
         * Map.Entry<K,​V> floorEntry​(K key)
         * K floorKey​(K key)
         * NavigableMap<K,​V> headMap​(K toKey, boolean inclusive)
         * NavigableMap<K,​V> tailMap​(K fromKey, boolean inclusive)
         * NavigableMap<K,​V> subMap​(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive)
         * NavigableSet<K> navigableKeySet()
         * NavigableSet<K> descendingKeySet()
         * NavigableMap<K,​V> descendingMap()
         * Map.Entry<K,​V> pollFirstEntry()
         * Map.Entry<K,​V> pollLastEntry()
         */

        NavigableMap<Person, Integer> m1 = new TreeMap<>(Person.comparator());
        m1.put(Person.of("aa", "aa", 10), 100);
        m1.put(Person.of("aa", "aa", 20), 200);
        m1.put(Person.of("aa", "bb", 10), 300);
        m1.put(Person.of("aa", "bb", 20), 400);
        m1.put(Person.of("bb", "cc", 10), 500);
        m1.put(Person.of("bb", "cc", 20), 600);
        // head じゃなくて first
        assertEntry(m1.firstEntry(), Person.of("aa", "aa", 10), 100);
        // tail じゃなくて last
        assertEntry(m1.lastEntry(), Person.of("bb", "cc", 20), 600);

        // higher(3) -> 1 2 3 [4] 5 : 引数自体は含まない
        assertEntry(m1.higherEntry(Person.of("aa", "bb", 10)), Person.of("aa", "bb", 20), 400);
        assertEntry(m1.higherEntry(Person.of("aa", "bb", 19)), Person.of("aa", "bb", 20), 400);
        assertThat(m1.higherKey(Person.of("aa", "bb", 10))).isEqualTo(Person.of("aa", "bb", 20));
        assertThat(m1.higherKey(Person.of("aa", "bb", 19))).isEqualTo(Person.of("aa", "bb", 20));
        // lower(3) -> 1 [2] 3 4 5 : 引数自体は含まない
        assertEntry(m1.lowerEntry(Person.of("aa", "bb", 10)), Person.of("aa", "aa", 20), 200);
        assertEntry(m1.lowerEntry(Person.of("aa", "aa", 21)), Person.of("aa", "aa", 20), 200);
        assertThat(m1.lowerKey(Person.of("aa", "bb", 10))).isEqualTo(Person.of("aa", "aa", 20));
        assertThat(m1.lowerKey(Person.of("aa", "aa", 21))).isEqualTo(Person.of("aa", "aa", 20));
        // ceiling(4) -> 1 2 3 [4] 5  : 引数自体を含む
        // ceiling(3.5) -> 1 2 3 [4] 5 
        assertEntry(m1.ceilingEntry(Person.of("aa", "bb", 10)), Person.of("aa", "bb", 10), 300);
        assertEntry(m1.ceilingEntry(Person.of("aa", "bb", 19)), Person.of("aa", "bb", 20), 400);
        assertThat(m1.ceilingKey(Person.of("aa", "bb", 10))).isEqualTo(Person.of("aa", "bb", 10));
        assertThat(m1.ceilingKey(Person.of("aa", "bb", 19))).isEqualTo(Person.of("aa", "bb", 20));
        // floor(3) -> 1 2 [3] 4 5 : 引数自体を含む
        // floor(3.5) -> 1 2 [3] 4 5
        assertEntry(m1.floorEntry(Person.of("aa", "bb", 10)), Person.of("aa", "bb", 10), 300);
        assertEntry(m1.floorEntry(Person.of("aa", "aa", 21)), Person.of("aa", "aa", 20), 200);
        assertThat(m1.floorKey(Person.of("aa", "bb", 10))).isEqualTo(Person.of("aa", "bb", 10));
        assertThat(m1.floorKey(Person.of("aa", "aa", 21))).isEqualTo(Person.of("aa", "aa", 20));

        // SortedMap#headMap()と異なり、引数自身を含むか選べる。
        NavigableMap<Person, Integer> m1h = m1.headMap(Person.of("aa", "bb", 10), true);
        assertThat(m1h.size()).isEqualTo(3);
        assertThat(m1h.get(Person.of("aa", "aa", 10))).isEqualTo(100);
        assertThat(m1h.get(Person.of("aa", "aa", 20))).isEqualTo(200);
        assertThat(m1h.get(Person.of("aa", "bb", 10))).isEqualTo(300);
        m1h = m1.headMap(Person.of("aa", "bb", 10), false); // 含まないバージョン = SortedMap#headMap()
        assertThat(m1h.size()).isEqualTo(2);
        assertThat(m1h.get(Person.of("aa", "aa", 10))).isEqualTo(100);
        assertThat(m1h.get(Person.of("aa", "aa", 20))).isEqualTo(200);
        // もとのMapにも反映されるのは SortedMap#headMap() と同じ。
        m1h.put(Person.of("aa", "aa", 30), 250);
        assertThat(m1.get(Person.of("aa", "aa", 30))).isEqualTo(250);
        // 範囲外追加で実行時例外も SortedMap#headMap() と同じ
        assertThatThrownBy(() -> {
            m1.headMap(Person.of("aa", "bb", 10), false).put(Person.of("aa", "bb", 11), 301);
        }).isInstanceOf(IllegalArgumentException.class);

        // SortedMap#tailMap()と異なり、引数自身を含むか選べる。
        NavigableMap<Person, Integer> m1t = m1.tailMap(Person.of("aa", "bb", 20), false);
        assertThat(m1t.size()).isEqualTo(2);
        assertThat(m1t.get(Person.of("bb", "cc", 10))).isEqualTo(500);
        assertThat(m1t.get(Person.of("bb", "cc", 20))).isEqualTo(600);
        m1t = m1.tailMap(Person.of("aa", "bb", 20), true); // 含むバージョン = SortedMap#tailMap()
        assertThat(m1t.size()).isEqualTo(3);
        assertThat(m1t.get(Person.of("aa", "bb", 20))).isEqualTo(400);
        assertThat(m1t.get(Person.of("bb", "cc", 10))).isEqualTo(500);
        assertThat(m1t.get(Person.of("bb", "cc", 20))).isEqualTo(600);
        // もとのMapにも反映されるのは SortedMap#tailMap() と同じ。
        m1t.put(Person.of("bb", "cc", 30), 650);
        assertThat(m1.get(Person.of("bb", "cc", 30))).isEqualTo(650);
        // 範囲外追加で実行時例外も SortedMap#tailMap() と同じ
        assertThatThrownBy(() -> {
            m1.tailMap(Person.of("aa", "bb", 20), true).put(Person.of("aa", "bb", 19), 350);
        }).isInstanceOf(IllegalArgumentException.class);

        // SortedMap#subMap() と異なり、from/to それぞれの引数自身を含むか選べる。
        // -> SortedMap#subMap() の反対に、fromは含めず to を含めてみる。
        NavigableMap<Person, Integer> m1sub = m1.subMap(
                Person.of("aa", "aa", 20), false,
                Person.of("aa", "bb", 20), true);
        assertThat(m1sub.size()).isEqualTo(3);
        assertThat(m1sub.get(Person.of("aa", "aa", 30))).isEqualTo(250);
        assertThat(m1sub.get(Person.of("aa", "bb", 10))).isEqualTo(300);
        assertThat(m1sub.get(Person.of("aa", "bb", 20))).isEqualTo(400);
        // もとのMapにも反映されるのは SortedMap#subMap() と同じ。
        m1sub.put(Person.of("aa", "bb", 5), 290);
        assertThat(m1.get(Person.of("aa", "bb", 5))).isEqualTo(290);
        // 範囲外追加で実行時例外も SortedMap#subMap() と同じ
        assertThatThrownBy(() -> {
            m1sub.put(Person.of("aa", "aa", 20), 199);
        }).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> {
            m1sub.put(Person.of("aa", "bb", 21), 201);
        }).isInstanceOf(IllegalArgumentException.class);

        // ソートされた key set を返すことができる。
        NavigableSet<Person> keySet = m1.navigableKeySet();
        Iterator<Person> it = keySet.iterator();
        assertThat(it.next()).isEqualTo(Person.of("aa", "aa", 10));
        assertThat(it.next()).isEqualTo(Person.of("aa", "aa", 20));
        assertThat(it.next()).isEqualTo(Person.of("aa", "aa", 30));
        // ...(snip)...
        // 逆順にソートした key set を返すことができる。
        keySet = m1.descendingKeySet();
        it = keySet.iterator();
        assertThat(it.next()).isEqualTo(Person.of("bb", "cc", 30));
        assertThat(it.next()).isEqualTo(Person.of("bb", "cc", 20));
        assertThat(it.next()).isEqualTo(Person.of("bb", "cc", 10));
        // ...(snip)...
        // 逆順にソートしたMapを返すことができる。
        NavigableMap<Person, Integer> m2 = m1.descendingMap();
        Iterator<Entry<Person, Integer>> it2 = m2.entrySet().iterator();
        assertEntry(it2.next(), Person.of("bb", "cc", 30), 650);
        assertEntry(it2.next(), Person.of("bb", "cc", 20), 600);
        assertEntry(it2.next(), Person.of("bb", "cc", 10), 500);
        // ...(snip)...

        // m1sub : [aa,aa,30]=>250, [aa,bb,5]=>290, [aa,bb,10]=>300, [aa,bb,20]=>400
        assertEntry(m1sub.pollFirstEntry(), Person.of("aa", "aa", 30), 250);
        assertEntry(m1sub.pollLastEntry(), Person.of("aa", "bb", 20), 400);
        assertEntry(m1sub.pollFirstEntry(), Person.of("aa", "bb", 5), 290);
        assertEntry(m1sub.pollLastEntry(), Person.of("aa", "bb", 10), 300);
        assertThat(m1sub.pollFirstEntry()).isNull();
        assertThat(m1sub.pollLastEntry()).isNull();
        // subMap() からpoll{First|Last}Entry()したものは、もとのMapからも削除されている。
        assertThat(m1.containsKey(Person.of("aa", "aa", 30))).isFalse();
        assertThat(m1.containsKey(Person.of("aa", "bb", 5))).isFalse();
        assertThat(m1.containsKey(Person.of("aa", "bb", 10))).isFalse();
        assertThat(m1.containsKey(Person.of("aa", "bb", 20))).isFalse();
    }
}
