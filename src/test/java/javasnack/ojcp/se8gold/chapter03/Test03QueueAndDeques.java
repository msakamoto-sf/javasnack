package javasnack.ojcp.se8gold.chapter03;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.jupiter.api.Test;

public class Test03QueueAndDeques {
    // Queue(FIFO), Deque(double ended queue) interface のデモ

    @Test
    public void testQueueUsageDemo() {
        /* Queue interface のデモ
         * 例外throw系:
         * add(e), remove(), element()
         * 例外throw無し(特殊な値を返す)系:
         * offer(e), poll(), peek()
         */
        Queue<String> q1 = new LinkedBlockingQueue<>(3); // null挿入NGな実装クラス

        // 例外throw系のデモ
        assertThatThrownBy(() -> {
            // 典型的なqueueではnull挿入を許可しない
            q1.add(null);
        }).isInstanceOf(NullPointerException.class);
        assertThat(q1.add("aa")).isTrue();
        assertThat(q1.add("bb")).isTrue();
        assertThat(q1.add("cc")).isTrue();
        assertThatThrownBy(() -> {
            q1.add("dd");
        }).isInstanceOf(IllegalStateException.class);
        assertThat(q1.size()).isEqualTo(3);

        // First-In, First-Out
        assertThat(q1.element()).isEqualTo("aa");
        assertThat(q1.remove()).isEqualTo("aa");
        // -> element() は中身を参照できるが削除はしない。remove()は削除までする。
        assertThat(q1.element()).isEqualTo("bb");
        assertThat(q1.remove()).isEqualTo("bb");
        assertThat(q1.element()).isEqualTo("cc");
        assertThat(q1.remove()).isEqualTo("cc");
        assertThatThrownBy(() -> {
            q1.element();
        }).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> {
            q1.remove();
        }).isInstanceOf(NoSuchElementException.class);
        assertThat(q1.size()).isEqualTo(0);

        q1.clear();

        // 例外throw無し(特殊な値を返す)系のデモ
        assertThatThrownBy(() -> {
            // 典型的なqueueではnull挿入を許可しない
            q1.offer(null);
        }).isInstanceOf(NullPointerException.class);
        assertThat(q1.offer("aa")).isTrue();
        assertThat(q1.offer("bb")).isTrue();
        assertThat(q1.offer("cc")).isTrue();
        // offer() の場合は、capacity over 時は例外ではなくfalseを返す。
        assertThat(q1.offer("dd")).isFalse();
        assertThat(q1.size()).isEqualTo(3);

        // First-In, First-Out
        assertThat(q1.peek()).isEqualTo("aa");
        assertThat(q1.poll()).isEqualTo("aa");
        // -> peek() は中身を参照できるが削除はしない。poll()は削除までする。
        assertThat(q1.peek()).isEqualTo("bb");
        assertThat(q1.poll()).isEqualTo("bb");
        assertThat(q1.peek()).isEqualTo("cc");
        assertThat(q1.poll()).isEqualTo("cc");
        // 例外throw無しのmethodだと、要素がなくなるとnullを返す。
        // => なので、典型的なqueueではnull要素の追加は許可されてない。
        assertThat(q1.peek()).isNull();
        assertThat(q1.poll()).isNull();
        assertThat(q1.size()).isEqualTo(0);

        Queue<String> q2 = new LinkedList<>(); // null挿入を許可する実装クラス
        // 例外throw系のデモ
        assertThat(q2.add(null)).isTrue(); // null追加OK.
        assertThat(q2.add("aa")).isTrue();
        assertThat(q2.add("bb")).isTrue();

        assertThat(q2.element()).isNull();
        assertThat(q2.remove()).isNull();
        assertThat(q2.element()).isEqualTo("aa");
        assertThat(q2.remove()).isEqualTo("aa");
        assertThat(q2.element()).isEqualTo("bb");
        assertThat(q2.remove()).isEqualTo("bb");
        assertThatThrownBy(() -> {
            q2.element();
        }).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> {
            q2.remove();
        }).isInstanceOf(NoSuchElementException.class);

        q2.clear();

        // 例外throw無し(特殊な値を返す)系のデモ
        assertThat(q2.offer(null)).isTrue(); // null追加OK.
        assertThat(q2.offer("aa")).isTrue();
        assertThat(q2.offer("bb")).isTrue();

        assertThat(q2.peek()).isNull();
        assertThat(q2.poll()).isNull();
        assertThat(q2.size()).isEqualTo(2);
        /* null追加OKなので、peek()/poll()でnullが返ってきたときに
         * queue が空でnullが返ったのか、null要素が返ったのか判別できない。
         * single thread であれば事前に size() でキューの現在サイズを取得し、それで判別するのが良さそう。
         * (multi thread で同時にpeek()/poll()するのであれば atomic でなくなるので別の方法検討)
         */
        assertThat(q2.peek()).isEqualTo("aa");
        assertThat(q2.poll()).isEqualTo("aa");
        assertThat(q2.peek()).isEqualTo("bb");
        assertThat(q2.poll()).isEqualTo("bb");
        assertThat(q2.peek()).isNull();
        assertThat(q2.poll()).isNull();
        assertThat(q2.size()).isEqualTo(0);

        Queue<Integer> q3 = new LinkedBlockingQueue<>();
        q3.add(100);
        q3.add(0);
        q3.add(200);
        /* NOTE: Queue は Collection を継承しているため、remove(Object o) がある。
         * しかし List は継承していないので remove(int index) は無い。
         * よって以下の remove(0) は Collection の remove(Object o) を呼んでおり、
         * 内部で保持しているリスト上の 0 を削除するものとなる。
         */
        q3.remove(0);
        assertThat(q3.remove()).isEqualTo(100);
        assertThat(q3.remove()).isEqualTo(200);
        // -> queue から取り出してみると、0 が消えている。

        LinkedList<Integer> q4 = new LinkedList<>();
        q4.add(100);
        q4.add(0);
        q4.add(200);
        /* NOTE: 具象型LinkedList は List も実装している。
         * このため remove(0) は List#remove(int index) を呼ぶことになり、
         * 内部で保持しているリストの先頭を削除するものとなる。
         */
        q4.remove(0);
        assertThat(q4.remove()).isEqualTo(0);
        assertThat(q4.remove()).isEqualTo(200);
    }

    @Test
    public void testDequeUsageDemo() {
        /* Dequeue interface のデモ
         * 例外throw系:
         * 先頭(head) -> addFirst(e), removeFirst(), getFirst()
         * 末尾(tail) -> addLast(e), removeLast(), getLast()
         * 例外throw無し(特殊な値を返す)系:
         * 先頭(head) -> offerFirst(e), pollFirst(), peekFirst()
         * 末尾(tail) -> offerLast(e), pollLast(), peekLast()
         */
        Deque<String> q1 = new LinkedBlockingDeque<>(3); // null挿入NGな実装クラス

        // 例外throw系のデモ:先頭(head)
        assertThatThrownBy(() -> {
            // 典型的なqueueではnull挿入を許可しない
            q1.addFirst(null);
        }).isInstanceOf(NullPointerException.class);
        q1.addFirst("aa");
        q1.addFirst("bb");
        q1.addFirst("cc");
        assertThatThrownBy(() -> {
            q1.addFirst("dd");
        }).isInstanceOf(IllegalStateException.class);
        assertThat(q1.size()).isEqualTo(3);

        // [head] cc -> bb -> aa [tail]
        // -> 片側だけの add/remove(get) はLast-In, First-Out(LIFO a.k.a Stack) として動作する。
        assertThat(q1.getFirst()).isEqualTo("cc");
        assertThat(q1.removeFirst()).isEqualTo("cc");
        // -> getFirst() は中身を参照できるが削除はしない。removeFirst()は削除までする。
        assertThat(q1.getFirst()).isEqualTo("bb");
        assertThat(q1.removeFirst()).isEqualTo("bb");
        assertThat(q1.getFirst()).isEqualTo("aa");
        assertThat(q1.removeFirst()).isEqualTo("aa");
        assertThatThrownBy(() -> {
            q1.getFirst();
        }).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> {
            q1.removeFirst();
        }).isInstanceOf(NoSuchElementException.class);
        assertThat(q1.size()).isEqualTo(0);

        q1.clear();

        // 例外throw無し(特殊な値を返す)系のデモ:先頭(head)
        assertThatThrownBy(() -> {
            // 典型的なqueueではnull挿入を許可しない
            q1.offerFirst(null);
        }).isInstanceOf(NullPointerException.class);
        assertThat(q1.offerFirst("aa")).isTrue();
        assertThat(q1.offerFirst("bb")).isTrue();
        assertThat(q1.offerFirst("cc")).isTrue();
        // offerFirst() の場合は、capacity over 時は例外ではなくfalseを返す。
        assertThat(q1.offerFirst("dd")).isFalse();
        assertThat(q1.size()).isEqualTo(3);

        // [head] cc -> bb -> aa [tail]
        // -> 片側だけの add/remove(get) はLast-In, First-Out(LIFO a.k.a Stack) として動作する。
        assertThat(q1.peekFirst()).isEqualTo("cc");
        assertThat(q1.pollFirst()).isEqualTo("cc");
        // -> peekFirst() は中身を参照できるが削除はしない。pollFirst()は削除までする。
        assertThat(q1.peekFirst()).isEqualTo("bb");
        assertThat(q1.pollFirst()).isEqualTo("bb");
        assertThat(q1.peekFirst()).isEqualTo("aa");
        assertThat(q1.pollFirst()).isEqualTo("aa");
        // 例外throw無しのmethodだと、要素がなくなるとnullを返す。
        // => なので、典型的なqueueではnull要素の追加は許可されてない。
        assertThat(q1.peekFirst()).isNull();
        assertThat(q1.pollFirst()).isNull();
        assertThat(q1.size()).isEqualTo(0);

        q1.clear();

        // 例外throw系のデモ:末尾(tail)
        assertThatThrownBy(() -> {
            // 典型的なqueueではnull挿入を許可しない
            q1.addLast(null);
        }).isInstanceOf(NullPointerException.class);
        q1.addLast("aa");
        q1.addLast("bb");
        q1.addLast("cc");
        assertThatThrownBy(() -> {
            q1.addLast("dd");
        }).isInstanceOf(IllegalStateException.class);
        assertThat(q1.size()).isEqualTo(3);

        // [head] aa <- bb <- cc [tail]
        // -> 片側だけの add/remove(get) はLast-In, Last-Out(LIFO a.k.a Stack) として動作する。
        assertThat(q1.getLast()).isEqualTo("cc");
        assertThat(q1.removeLast()).isEqualTo("cc");
        // -> getLast() は中身を参照できるが削除はしない。removeLast()は削除までする。
        assertThat(q1.getLast()).isEqualTo("bb");
        assertThat(q1.removeLast()).isEqualTo("bb");
        assertThat(q1.getLast()).isEqualTo("aa");
        assertThat(q1.removeLast()).isEqualTo("aa");
        assertThatThrownBy(() -> {
            q1.getLast();
        }).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> {
            q1.removeLast();
        }).isInstanceOf(NoSuchElementException.class);
        assertThat(q1.size()).isEqualTo(0);

        q1.clear();

        // 例外throw無し(特殊な値を返す)系のデモ:末尾(tail)
        assertThatThrownBy(() -> {
            // 典型的なqueueではnull挿入を許可しない
            q1.offerLast(null);
        }).isInstanceOf(NullPointerException.class);
        assertThat(q1.offerLast("aa")).isTrue();
        assertThat(q1.offerLast("bb")).isTrue();
        assertThat(q1.offerLast("cc")).isTrue();
        // offerLast() の場合は、capacity over 時は例外ではなくfalseを返す。
        assertThat(q1.offerLast("dd")).isFalse();
        assertThat(q1.size()).isEqualTo(3);

        // [head] aa <- bb <- cc [tail]
        // -> 片側だけの add/remove(get) はLast-In, Last-Out(LIFO a.k.a Stack) として動作する。
        assertThat(q1.peekLast()).isEqualTo("cc");
        assertThat(q1.pollLast()).isEqualTo("cc");
        // -> peekLast() は中身を参照できるが削除はしない。pollLast()は削除までする。
        assertThat(q1.peekLast()).isEqualTo("bb");
        assertThat(q1.pollLast()).isEqualTo("bb");
        assertThat(q1.peekLast()).isEqualTo("aa");
        assertThat(q1.pollLast()).isEqualTo("aa");
        // 例外throw無しのmethodだと、要素がなくなるとnullを返す。
        // => なので、典型的なqueueではnull要素の追加は許可されてない。
        assertThat(q1.peekLast()).isNull();
        assertThat(q1.pollLast()).isNull();
        assertThat(q1.size()).isEqualTo(0);

        Deque<String> q2 = new LinkedList<>(); // null挿入を許可する実装クラス
        // 例外throw系のデモ
        q2.addFirst(null); // null追加OK.
        q2.addFirst("aa");
        q2.addLast(null); // null追加OK.
        q2.addLast("bb");

        assertThat(q2.getFirst()).isEqualTo("aa");
        assertThat(q2.removeFirst()).isEqualTo("aa");
        assertThat(q2.getFirst()).isNull();
        assertThat(q2.removeFirst()).isNull();
        assertThat(q2.getLast()).isEqualTo("bb");
        assertThat(q2.removeLast()).isEqualTo("bb");
        assertThat(q2.getLast()).isNull();
        assertThat(q2.removeLast()).isNull();
        assertThatThrownBy(() -> {
            q2.getFirst();
        }).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> {
            q2.removeFirst();
        }).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> {
            q2.getLast();
        }).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> {
            q2.removeLast();
        }).isInstanceOf(NoSuchElementException.class);

        q2.clear();

        // 例外throw無し(特殊な値を返す)系のデモ
        assertThat(q2.offerFirst(null)).isTrue(); // null追加OK.
        assertThat(q2.offerFirst("aa")).isTrue();
        assertThat(q2.offerLast(null)).isTrue(); // null追加OK.
        assertThat(q2.offerLast("bb")).isTrue();

        assertThat(q2.peekFirst()).isEqualTo("aa");
        assertThat(q2.pollFirst()).isEqualTo("aa");
        assertThat(q2.size()).isEqualTo(3);
        /* null追加OKなので、peek()/poll()でnullが返ってきたときに
         * queue が空でnullが返ったのか、null要素が返ったのか判別できない。
         * single thread であれば事前に size() でキューの現在サイズを取得し、それで判別するのが良さそう。
         * (multi thread で同時にpeek()/poll()するのであれば atomic でなくなるので別の方法検討)
         */
        assertThat(q2.peekFirst()).isNull();
        assertThat(q2.pollFirst()).isNull();

        assertThat(q2.peekLast()).isEqualTo("bb");
        assertThat(q2.pollLast()).isEqualTo("bb");
        assertThat(q2.size()).isEqualTo(1);
        assertThat(q2.peekLast()).isNull();
        assertThat(q2.pollLast()).isNull();

        assertThat(q2.size()).isEqualTo(0);
        assertThat(q2.peekFirst()).isNull();
        assertThat(q2.pollFirst()).isNull();
        assertThat(q2.peekLast()).isNull();
        assertThat(q2.pollLast()).isNull();
    }

    @Test
    public void testDequeAsQueueDemo() {
        /* Deque は head or tail 片方だけの操作ではLIFO(a.k.a Stack)動作になる。
         * tail に追加した要素を head から取り出すとすれば、FIFO(Queue)動作になる。
         * このため、Deque interface およびその実装クラスは Queue interface から拡張されてる。
         */
        Deque<String> q1 = new LinkedBlockingDeque<>(4);
        // Queue のメソッドが使える : addLast()/offerLast() と同等
        q1.add("aa");
        q1.offer("bb");
        // Deque のメソッドも使える。
        q1.addLast("cc");
        q1.offerLast("dd");

        // Queue のメソッドが使える : getFirst()/peekFirst()/removeFirst()/pollFirst() と同等
        assertThat(q1.element()).isEqualTo("aa");
        assertThat(q1.remove()).isEqualTo("aa");
        assertThat(q1.peek()).isEqualTo("bb");
        assertThat(q1.poll()).isEqualTo("bb");
        assertThat(q1.getFirst()).isEqualTo("cc");
        assertThat(q1.removeFirst()).isEqualTo("cc");
        assertThat(q1.peekFirst()).isEqualTo("dd");
        assertThat(q1.pollFirst()).isEqualTo("dd");
    }

    @Test
    public void testDequeAsStackDemo() {
        /* Deque は head or tail 片方だけの操作ではLIFO(a.k.a Stack)動作になる。
         * このため、Deque interface およびその実装クラスは Stack としての操作メソッドも提供している。
         */
        Deque<String> q1 = new LinkedBlockingDeque<>(2);
        // Stackとしての push() が使える。addFirst()と等価
        q1.push("aa");
        q1.addFirst("bb");

        // Stackとしての pop()/peek() が使える。removeFirst()/getFirst() と同等
        assertThat(q1.peek()).isEqualTo("bb");
        assertThat(q1.pop()).isEqualTo("bb");
        assertThat(q1.getFirst()).isEqualTo("aa");
        assertThat(q1.removeFirst()).isEqualTo("aa");
    }

    @Test
    public void testPriorityQueueDemo() {
        // Comparable でない class で、Comparator も指定していないと、add() で ClassCastException
        Queue<Person> q1 = new PriorityQueue<>();
        assertThatThrownBy(() -> {
            q1.add(Person.of("aa", "aa", 10));
        }).isInstanceOf(ClassCastException.class);

        // Comparable でない class でも、Comparator を指定すればOK.
        Queue<Person> q2 = new PriorityQueue<>(Person.comparator());
        q2.add(Person.of("aa", "bb", 20));
        q2.add(Person.of("aa", "aa", 10));
        // 内部でソートされた順序に従ってOutを返すのでFIFOには従わない。
        assertThat(q2.remove()).isEqualTo(Person.of("aa", "aa", 10));
        q2.add(Person.of("aa", "bb", 10));
        q2.add(Person.of("aa", "aa", 20));
        assertThat(q2.remove()).isEqualTo(Person.of("aa", "aa", 20));
        assertThat(q2.remove()).isEqualTo(Person.of("aa", "bb", 10));
        assertThat(q2.remove()).isEqualTo(Person.of("aa", "bb", 20));

        // Comparable な class なら、Comparatorの指定は不要。
        Queue<PersonComparable> q3 = new PriorityQueue<>();
        // 内部でソートされた順序に従ってOutを返すのでFIFOには従わない。
        q3.add(PersonComparable.of("aa", "bb", 20));
        q3.add(PersonComparable.of("aa", "aa", 10));
        assertThat(q3.remove()).isEqualTo(PersonComparable.of("aa", "aa", 10));
        q3.add(PersonComparable.of("aa", "bb", 10));
        q3.add(PersonComparable.of("aa", "aa", 20));
        assertThat(q3.remove()).isEqualTo(PersonComparable.of("aa", "aa", 20));
        assertThat(q3.remove()).isEqualTo(PersonComparable.of("aa", "bb", 10));
        assertThat(q3.remove()).isEqualTo(PersonComparable.of("aa", "bb", 20));
    }
}
