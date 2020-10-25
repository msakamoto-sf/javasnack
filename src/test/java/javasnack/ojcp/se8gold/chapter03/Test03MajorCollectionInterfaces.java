package javasnack.ojcp.se8gold.chapter03;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class Test03MajorCollectionInterfaces {
    // Collection, Map, List, Set, Iterator の各interface(API)の主な使い方のデモ

    @Test
    public void testCollectionInterfaceUsageDemo() {
        /* シンプルなCollection interface の使い方を改めて確認:
         * boolean add​(E e)
         * boolean addAll​(Collection<? extends E> c)
         * void clear()
         * boolean contains​(Object o)
         * boolean containsAll​(Collection<?> c)
         * boolean isEmpty()
         * Iterator<E> iterator()
         * boolean remove​(Object o)
         * boolean removeAll​(Collection<?> c)
         * boolean retainAll​(Collection<?> c)
         * int size()
         * Object[] toArray()
         * <T> T[] toArray​(T[] a)
         */
        Collection<String> c1 = new ArrayList<>();
        assertThat(c1.size()).isEqualTo(0);
        assertThat(c1.isEmpty()).isTrue();

        assertThat(c1.add("aa")).isTrue(); // 変更されれば true を返す
        assertThat(c1.size()).isEqualTo(1);
        assertThat(c1.isEmpty()).isFalse();
        c1.addAll(List.of("bb", "cc", "dd"));

        // contains() はあるが、取得用のメソッドは未定義
        assertThat(c1.contains("aa")).isTrue();
        assertThat(c1.containsAll(List.of("aa", "cc"))).isTrue();
        assertThat(c1.containsAll(List.of("aa", "zz"))).isFalse();
        // Collection.remove(Object) は Set などでも使える汎用性がある。
        assertThat(c1.remove("dd")).isTrue();
        assertThat(c1.remove("zz")).isFalse();
        assertThat(c1.removeAll(List.of("aa", "zz"))).isTrue(); // 1つでも削除できればtrue
        assertThat(c1.contains("aa")).isFalse(); // 1つでも削除したのが反映されてる
        assertThat(c1.removeAll(List.of("bb", "cc"))).isTrue();
        assertThat(c1.contains("aa")).isFalse();
        assertThat(c1.contains("bb")).isFalse();
        assertThat(c1.contains("cc")).isFalse();
        assertThat(c1.contains("dd")).isFalse();
        assertThat(c1.size()).isEqualTo(0);
        assertThat(c1.isEmpty()).isTrue();

        c1.addAll(List.of("aa", "bb", "cc", "dd"));
        Object[] arr1 = c1.toArray();
        assertThat(arr1.length).isEqualTo(4);
        assertThat(arr1[0]).isEqualTo("aa");
        assertThat(arr1[1]).isEqualTo("bb");
        assertThat(arr1[2]).isEqualTo("cc");
        assertThat(arr1[3]).isEqualTo("dd");
        String[] arr2 = new String[c1.size()];
        String[] arr3 = c1.toArray(arr2); // 引数の配列にも格納される。
        assertThat(arr2.length).isEqualTo(4);
        assertThat(arr2[0]).isEqualTo("aa");
        assertThat(arr2[1]).isEqualTo("bb");
        assertThat(arr2[2]).isEqualTo("cc");
        assertThat(arr2[3]).isEqualTo("dd");
        assertThat(arr3.length).isEqualTo(4);
        assertThat(arr3[0]).isEqualTo("aa");
        assertThat(arr3[1]).isEqualTo("bb");
        assertThat(arr3[2]).isEqualTo("cc");
        assertThat(arr3[3]).isEqualTo("dd");
        // もし引数を使わないのであれば、サイズ0の配列を渡してもOK.
        arr3 = c1.toArray(new String[0]);
        assertThat(arr3.length).isEqualTo(4);
        assertThat(arr3[0]).isEqualTo("aa");
        assertThat(arr3[1]).isEqualTo("bb");
        assertThat(arr3[2]).isEqualTo("cc");
        assertThat(arr3[3]).isEqualTo("dd");

        // 同一 collection に対して複数の iterator を独立して回すことができる。
        // TODO: iterator 操作中の collection 要素の変更については別途検証
        Iterator<String> i1 = c1.iterator();
        Iterator<String> i2 = c1.iterator();
        assertThat(i1.hasNext()).isTrue();
        assertThat(i1.next()).isEqualTo("aa");
        assertThat(i1.next()).isEqualTo("bb");
        assertThat(i2.hasNext()).isTrue();
        assertThat(i2.next()).isEqualTo("aa");
        assertThat(i1.next()).isEqualTo("cc");
        assertThat(i1.next()).isEqualTo("dd");
        assertThatThrownBy(() -> {
            i1.next();
        }).isInstanceOf(NoSuchElementException.class);
        assertThat(i1.hasNext()).isFalse();
        assertThat(i2.next()).isEqualTo("bb");
        assertThat(i2.next()).isEqualTo("cc");
        assertThat(i2.next()).isEqualTo("dd");
        assertThatThrownBy(() -> {
            i2.next();
        }).isInstanceOf(NoSuchElementException.class);
        assertThat(i2.hasNext()).isFalse();

        // 特定の要素だけを残す -> collection の更新があればtrueを返す。
        assertThat(c1.retainAll(List.of("aa", "bb"))).isTrue();
        assertThat(c1.size()).isEqualTo(2);
        assertThat(c1.contains("aa")).isTrue();
        assertThat(c1.contains("bb")).isTrue();
        // 同じ要素だけ残す -> 更新が無いのでfalse
        assertThat(c1.retainAll(List.of("aa", "bb"))).isFalse();

        c1.clear();
        assertThat(c1.size()).isEqualTo(0);
        assertThat(c1.isEmpty()).isTrue();
    }

    @Test
    public void testMapInterfaceUsageDemo() {
        /* Map interface の主なメソッドのデモ:
         * void clear()
         * boolean containsKey​(Object key)
         * boolean containsValue​(Object value)
         * Set<Map.Entry<K,​V>> entrySet()
         * V get​(Object key)
         * boolean isEmpty()
         * Set<K>  keySet()
         * V merge(K key, V value, BiFunction<V, V, V> remappingFunction)
         * V put​(K key, V value)
         * void putAll​(Map<? extends K,​? extends V> m)
         * V remove​(Object key)
         * int size()
         * Collection<V> values()
         */
        Map<String, Integer> m1 = new HashMap<>();
        assertThat(m1.isEmpty()).isTrue();
        assertThat(m1.size()).isEqualTo(0);
        m1.put("k1", 100);
        m1.putAll(Map.of("k2", 200, "k3", 300));
        assertThat(m1.isEmpty()).isFalse();
        assertThat(m1.size()).isEqualTo(3);

        assertThat(m1.get("k1")).isEqualTo(100);
        assertThat(m1.get("k2")).isEqualTo(200);
        assertThat(m1.get("k3")).isEqualTo(300);

        assertThat(m1.containsKey("k1")).isTrue();
        assertThat(m1.containsKey("kx")).isFalse();

        assertThat(m1.containsValue(100)).isTrue();
        assertThat(m1.containsValue(99)).isFalse();

        Set<String> keys = m1.keySet();
        assertThat(keys.size()).isEqualTo(3);
        assertThat(keys.containsAll(List.of("k1", "k2", "k3"))).isTrue();

        Collection<Integer> values = m1.values();
        assertThat(values.size()).isEqualTo(3);
        assertThat(values.containsAll(List.of(100, 200, 300))).isTrue();

        int cnt = 0;
        for (Entry<String, Integer> e : m1.entrySet()) {
            if ("k1".equals(e.getKey())) {
                assertThat(e.getValue()).isEqualTo(100);
            } else if ("k2".equals(e.getKey())) {
                assertThat(e.getValue()).isEqualTo(200);
            } else if ("k3".equals(e.getKey())) {
                assertThat(e.getValue()).isEqualTo(300);
            }
            cnt++;
        }
        assertThat(cnt).isEqualTo(3);

        assertThat(m1.remove("k1")).isEqualTo(100);
        assertThat(m1.remove("kx")).isNull();
        assertThat(m1.size()).isEqualTo(2);

        m1.clear();
        assertThat(m1.isEmpty()).isTrue();
        assertThat(m1.size()).isEqualTo(0);

        m1.put("k1", 100);
        m1.put("k2", null);
        m1.put("k3", 300);
        // merge to not null(old) -> return not null value
        m1.merge("k1", 10, (oldV, newV) -> oldV + newV);
        assertThat(m1.get("k1")).isEqualTo(110);
        // merge to null(old) -> return not null value
        m1.merge("k2", 200, (oldV, newV) -> oldV + newV);
        assertThat(m1.get("k2")).isEqualTo(200);
        // merge null value -> NPE
        assertThatThrownBy(() -> {
            m1.merge("k3", null, (oldV, newV) -> newV);
        }).isInstanceOf(NullPointerException.class);
        assertThat(m1.get("k3")).isEqualTo(300);
        // merge -> remapping results to null
        m1.merge("k3", 330, (oldV, newV) -> null);
        assertThat(m1.containsKey("k3")).isFalse(); // key was removed.
        assertThat(m1.get("k3")).isNull();
    }

    @Test
    public void testListInterfaceUsageDemo() {
        /* List interface の主要メソッドのデモ
         * 
         * void add​(int index, E element)
         * boolean addAll​(int index, Collection<? extends E> c)
         * E get​(int index)
         * int indexOf​(Object o)
         * int lastIndexOf​(Object o)
         * E remove​(int index)
         * E set​(int index, E element)
         * List<E> subList​(int fromIndex, int toIndex)
         */
        List<String> l1 = new ArrayList<>();
        l1.addAll(List.of("aa", "bb", "cc"));
        l1.add(0, "aa"); // list は同じ要素を重複して追加可能。
        // この時点で[0]=aa,[1]=aa,[2]=bb,[3]=cc
        l1.add(3, "dd");
        // -> [0]=aa,[1]=aa,[2]=bb,[3]=dd,[4]=cc
        assertThat(l1.get(0)).isEqualTo("aa");
        assertThat(l1.get(1)).isEqualTo("aa");
        assertThat(l1.get(2)).isEqualTo("bb");
        assertThat(l1.get(3)).isEqualTo("dd");
        assertThat(l1.get(4)).isEqualTo("cc");
        assertThatThrownBy(() -> {
            l1.get(99);
        }).isInstanceOf(IndexOutOfBoundsException.class);

        l1.add(5, "ee"); // l1.add(l1.size(), "ee") と同じ
        // -> [0]=aa,[1]=aa,[2]=bb,[3]=dd,[4]=cc,[5]=ee
        assertThat(l1.get(5)).isEqualTo("ee");
        assertThatThrownBy(() -> {
            l1.add(l1.size() + 1, "xx");
        }).isInstanceOf(IndexOutOfBoundsException.class);

        assertThat(l1.indexOf("aa")).isEqualTo(0);
        assertThat(l1.indexOf("xx")).isEqualTo(-1);
        assertThat(l1.lastIndexOf("aa")).isEqualTo(1);
        assertThat(l1.lastIndexOf("xx")).isEqualTo(-1);

        assertThat(l1.set(2, "BB")).isEqualTo("bb"); // 元の値を返す
        assertThat(l1.get(2)).isEqualTo("BB");
        assertThatThrownBy(() -> {
            l1.set(l1.size() + 1, "xx");
        }).isInstanceOf(IndexOutOfBoundsException.class);

        // l1: [0]=aa,[1]=aa,[2]=BB,[3]=dd,[4]=cc,[5]=ee
        List<String> l2 = l1.subList(2, l1.size()); // from を含み、to は含まない
        assertThat(l2).isEqualTo(List.of("BB", "dd", "cc", "ee"));

        assertThatThrownBy(() -> {
            l1.subList(-1, l1.size());
        }).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> {
            l1.subList(0, l1.size() + 1);
        }).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> {
            l1.subList(2, 1);
        }).isInstanceOf(IllegalArgumentException.class);

        assertThat(l1.remove(0)).isEqualTo("aa");
        assertThat(l1.remove(l1.size() - 1)).isEqualTo("ee");
        assertThatThrownBy(() -> {
            l1.remove(-1);
        }).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> {
            l1.remove(l1.size());
        }).isInstanceOf(IndexOutOfBoundsException.class);

        // l1: [0]=aa,[1]=BB,[2]=dd,[3]=cc
        assertThat(l1.addAll(1, List.of("FF", "GG"))).isTrue();
        assertThat(l1.addAll(1, Collections.emptyList())).isFalse(); // 変更無し
        assertThat(l1.get(0)).isEqualTo("aa");
        assertThat(l1.get(1)).isEqualTo("FF");
        assertThat(l1.get(2)).isEqualTo("GG");
        assertThat(l1.get(3)).isEqualTo("BB");
        assertThat(l1.get(4)).isEqualTo("dd");
        assertThat(l1.get(5)).isEqualTo("cc");
        assertThatThrownBy(() -> {
            l1.addAll(-1, List.of("xx"));
        }).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> {
            l1.addAll(l1.size() + 1, List.of("xx"));
        }).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void testSetInterfaceUsageDemo() {
        /* Set interface の主要メソッドのデモ
         * 
         * => Set 独自のメソッドというものは無く、Collection のメソッドそのまま。
         * 動作が一部 Set の特性に従うものがあるので、そこだけデモ。
         */
        Set<String> s1 = new HashSet<>();
        assertThat(s1.add("aa")).isTrue();
        assertThat(s1.addAll(List.of("bb", "cc"))).isTrue();
        // 同じ値を追加しても無視され、Setの中身に変更が無いためfalse
        assertThat(s1.add("aa")).isFalse();
        assertThat(s1.addAll(List.of("bb", "cc"))).isFalse();
    }

    @Test
    public void testIteratorInterfaceUsageDemo() {
        /* Iterator interface の主要メソッドのデモ
         * 
         * boolean  hasNext()
         * E next()
         * default void remove()
         */
        List<String> l1 = new ArrayList<>();
        l1.addAll(List.of("aa", "bb", "cc", "dd"));
        Iterator<String> it1 = l1.iterator();
        assertThat(it1.hasNext()).isTrue();
        assertThat(it1.next()).isEqualTo("aa");
        assertThat(it1.hasNext()).isTrue();
        assertThat(it1.next()).isEqualTo("bb");
        assertThat(it1.hasNext()).isTrue();
        assertThat(it1.next()).isEqualTo("cc");
        assertThat(it1.hasNext()).isTrue();
        assertThat(it1.next()).isEqualTo("dd");
        assertThat(it1.hasNext()).isFalse();
        assertThatThrownBy(() -> {
            it1.next();
        }).isInstanceOf(NoSuchElementException.class);

        Iterator<String> it2 = l1.iterator();
        assertThatThrownBy(() -> {
            it2.remove();
        }).isInstanceOf(IllegalStateException.class);
        assertThat(it2.next()).isEqualTo("aa");
        assertThat(it2.next()).isEqualTo("bb");
        assertThat(it2.next()).isEqualTo("cc");
        it2.remove();
        assertThatThrownBy(() -> {
            it2.remove();
        }).isInstanceOf(IllegalStateException.class);
        assertThat(l1).isEqualTo(List.of("aa", "bb", "dd"));
    }

}
