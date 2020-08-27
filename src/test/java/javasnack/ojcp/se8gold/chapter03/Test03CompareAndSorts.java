package javasnack.ojcp.se8gold.chapter03;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

public class Test03CompareAndSorts {
    @Test
    public void testCollectionsSortDemo() {
        List<Person> l1 = new ArrayList<>();
        l1.add(Person.of("bbb", "ccc", 20));
        l1.add(Person.of("bbb", "ccc", 10));
        l1.add(Person.of("aaa", "bbb", 20));
        l1.add(Person.of("aaa", "bbb", 10));
        l1.add(Person.of("aaa", "aaa", 20));
        l1.add(Person.of("aaa", "aaa", 10));
        //Collections.sort(l1); // Person は Comparable を実装していないため compile error
        // firstName -> lastName -> age の昇順にソートする。
        Collections.sort(l1, Person.comparator());
        assertThat(l1.get(0)).isEqualTo(Person.of("aaa", "aaa", 10));
        assertThat(l1.get(1)).isEqualTo(Person.of("aaa", "aaa", 20));
        assertThat(l1.get(2)).isEqualTo(Person.of("aaa", "bbb", 10));
        assertThat(l1.get(3)).isEqualTo(Person.of("aaa", "bbb", 20));
        assertThat(l1.get(4)).isEqualTo(Person.of("bbb", "ccc", 10));
        assertThat(l1.get(5)).isEqualTo(Person.of("bbb", "ccc", 20));

        // リストを逆順にする。(リスト中の要素を入れ替えるだけなので、compareは行わない)
        Collections.reverse(l1);
        assertThat(l1.get(0)).isEqualTo(Person.of("bbb", "ccc", 20));
        assertThat(l1.get(1)).isEqualTo(Person.of("bbb", "ccc", 10));
        assertThat(l1.get(2)).isEqualTo(Person.of("aaa", "bbb", 20));
        assertThat(l1.get(3)).isEqualTo(Person.of("aaa", "bbb", 10));
        assertThat(l1.get(4)).isEqualTo(Person.of("aaa", "aaa", 20));
        assertThat(l1.get(5)).isEqualTo(Person.of("aaa", "aaa", 10));

        List<PersonComparable> l2 = new ArrayList<>();
        l2.add(PersonComparable.of("bbb", "ccc", 20));
        l2.add(PersonComparable.of("bbb", "ccc", 10));
        l2.add(PersonComparable.of("aaa", "bbb", 20));
        l2.add(PersonComparable.of("aaa", "bbb", 10));
        l2.add(PersonComparable.of("aaa", "aaa", 20));
        l2.add(PersonComparable.of("aaa", "aaa", 10));
        // firstName -> lastName -> age の昇順にソートする。
        Collections.sort(l2); // リスト中の要素がComparable なので、Comparator は不要。
        assertThat(l2.get(0)).isEqualTo(PersonComparable.of("aaa", "aaa", 10));
        assertThat(l2.get(1)).isEqualTo(PersonComparable.of("aaa", "aaa", 20));
        assertThat(l2.get(2)).isEqualTo(PersonComparable.of("aaa", "bbb", 10));
        assertThat(l2.get(3)).isEqualTo(PersonComparable.of("aaa", "bbb", 20));
        assertThat(l2.get(4)).isEqualTo(PersonComparable.of("bbb", "ccc", 10));
        assertThat(l2.get(5)).isEqualTo(PersonComparable.of("bbb", "ccc", 20));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testCollectionsSortClassCastExceptionDemo() {
        // cast して相互比較できないオブジェクトが混在している場合は、ClassCastException 発生
        List l3 = new ArrayList();
        l3.add("hello");
        l3.add(Integer.valueOf(100));
        l3.add(Double.valueOf(1.01));
        assertThatThrownBy(() -> {
            Collections.sort(l3);
        }).isInstanceOf(ClassCastException.class);
    }

    @Test
    public void testArraysSortDemo() {
        Person[] a1 = {
                Person.of("bbb", "ccc", 20),
                Person.of("bbb", "ccc", 10),
                Person.of("aaa", "bbb", 20),
                Person.of("aaa", "bbb", 10),
                Person.of("aaa", "aaa", 20),
                Person.of("aaa", "aaa", 10),
        };
        // firstName -> lastName -> age の昇順にソートする。
        Arrays.sort(a1, Person.comparator());
        assertThat(a1[0]).isEqualTo(Person.of("aaa", "aaa", 10));
        assertThat(a1[1]).isEqualTo(Person.of("aaa", "aaa", 20));
        assertThat(a1[2]).isEqualTo(Person.of("aaa", "bbb", 10));
        assertThat(a1[3]).isEqualTo(Person.of("aaa", "bbb", 20));
        assertThat(a1[4]).isEqualTo(Person.of("bbb", "ccc", 10));
        assertThat(a1[5]).isEqualTo(Person.of("bbb", "ccc", 20));

        PersonComparable[] a2 = {
                PersonComparable.of("bbb", "ccc", 20),
                PersonComparable.of("bbb", "ccc", 10),
                PersonComparable.of("aaa", "bbb", 20),
                PersonComparable.of("aaa", "bbb", 10),
                PersonComparable.of("aaa", "aaa", 20),
                PersonComparable.of("aaa", "aaa", 10),
        };
        // firstName -> lastName -> age の昇順にソートする。
        Arrays.sort(a2);
        assertThat(a2[0]).isEqualTo(PersonComparable.of("aaa", "aaa", 10));
        assertThat(a2[1]).isEqualTo(PersonComparable.of("aaa", "aaa", 20));
        assertThat(a2[2]).isEqualTo(PersonComparable.of("aaa", "bbb", 10));
        assertThat(a2[3]).isEqualTo(PersonComparable.of("aaa", "bbb", 20));
        assertThat(a2[4]).isEqualTo(PersonComparable.of("bbb", "ccc", 10));
        assertThat(a2[5]).isEqualTo(PersonComparable.of("bbb", "ccc", 20));

        // cast して相互比較できないオブジェクトが混在している場合は、ClassCastException 発生
        Object[] a3 = {
                "hello",
                Integer.valueOf(100),
                Double.valueOf(1.01),
        };
        assertThatThrownBy(() -> {
            Arrays.sort(a3);
        }).isInstanceOf(ClassCastException.class);
    }
}
