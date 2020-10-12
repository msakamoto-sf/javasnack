package javasnack.ojcp.se8gold.chapter10;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

public class Test10ConcurrentCollections {
    @Test
    public void testFailFastIteratorAndConcurrentModificationExceptionDemo() {
        final Map<Integer, String> m0 = new HashMap<>();
        m0.put(1, "aaa");
        m0.put(2, "bbb");
        m0.put(3, "ccc");
        m0.remove(1);
        /* NOTE: 拡張for文により内部で iterator が使われるが、
         * iteratorの反復処理中に元のcollectionに変更が加えられると
         * ConcurrentModificationException 実行時例外を発生させる。
         * (iterator自体のremoveを使わないと駄目な模様。"fail-fast iterator".)
         */
        assertThatThrownBy(() -> {
            for (Integer key : m0.keySet()) {
                m0.remove(key);
            }
        }).isInstanceOf(ConcurrentModificationException.class);

        final Map<Integer, String> m1 = Collections.synchronizedMap(new HashMap<>());
        m1.put(1, "aaa");
        m1.put(2, "bbb");
        m1.put(3, "ccc");
        // synchronizedXxxx() でラップしても、同様に実行時例外発生。
        assertThatThrownBy(() -> {
            for (Integer key : m1.keySet()) {
                m1.remove(key);
            }
        }).isInstanceOf(ConcurrentModificationException.class);

        final Map<Integer, String> m2 = Collections.synchronizedMap(new HashMap<>());
        m2.put(1, "aaa");
        m2.put(2, "bbb");
        m2.put(3, "ccc");
        // synchronized で囲っても、同様に実行時例外発生。
        assertThatThrownBy(() -> {
            synchronized (m2) {
                for (Integer key : m2.keySet()) {
                    m2.remove(key);
                }
            }
        }).isInstanceOf(ConcurrentModificationException.class);
    }

    @Test
    public void testConcurrentHashMapDemo() {
        final ConcurrentMap<Integer, String> m0 = new ConcurrentHashMap<>();
        m0.put(1, "aaa");
        m0.put(2, "bbb");
        m0.put(3, "ccc");
        m0.remove(1);
        /* ConcurrentHashMapを使うと、拡張for文などで間接的にiteratorを扱うときに、
         * 元の map を変更しても実行時例外は発生しない。
         */
        for (Integer key : m0.keySet()) {
            m0.remove(key);
        }
        assertThat(m0).isEmpty();

        // 以下、ConcurrentMap インターフェイス特有の、複数の操作をatomicに行えるメソッド紹介

        // 指定されたキーがまだ値と関連付けられていない場合は、指定された値に関連付けする。
        assertThat(m0.putIfAbsent(1, "abc")).isNull();
        assertThat(m0.putIfAbsent(1, "ABC")).isEqualTo("abc"); // 既に"abc"があるので、更新されない

        // 指定された値にキーが現在マッピングされている場合にのみ、そのキーのエントリを削除
        assertThat(m0.remove(1, "abc")).isTrue();
        assertThat(m0.remove(1, "abc")).isFalse();

        // 指定された値にキーが現在マッピングされている場合にのみ、そのキーのエントリを置換
        m0.put(1, "xxx");
        assertThat(m0.replace(1, "xxx", "yyy")).isTrue();
        assertThat(m0.replace(1, "xxx", "yyy")).isFalse();
        assertThat(m0.replace(2, "xxx", "yyy")).isFalse();
        assertThat(m0.size()).isEqualTo(1);
        assertThat(m0.get(1)).isEqualTo("yyy");

        // キーが値に現在マッピングされている場合にのみ、そのキーのエントリを置換
        m0.put(1, "xxx");
        assertThat(m0.replace(1, "yyy")).isEqualTo("xxx");
        assertThat(m0.replace(2, "zzz")).isNull();
        assertThat(m0.size()).isEqualTo(1);
        assertThat(m0.get(1)).isEqualTo("yyy");
    }

    @Test
    public void testFailFastIteratorAndConcurrentModificationExceptionDemo2() throws InterruptedException {
        /* 通常の ArrayList だと、iterator 操作開始後に元の ArrayList の要素を
         * 追加/削除しようとすると、ConcurrentModificationException が発生する。
         */
        final ArrayList<String> l0 = new ArrayList<String>();
        l0.add("A");
        l0.add("B");
        l0.add("C");
        l0.add("D");
        final Iterator<String> it0 = l0.iterator();
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);

        new Thread(() -> {
            try {
                latch1.await();
                while (it0.hasNext()) {
                    System.out.println("iterator.next=" + it0.next());
                }
            } catch (ConcurrentModificationException expected) {
                latch2.countDown();
            } catch (InterruptedException ignore) {
            }
        }).start();

        l0.add("E");
        l0.remove(0);
        latch1.countDown();
        assertThat(latch2.await(10, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    public void testCopyOnWriteArrayListDemo() throws InterruptedException {
        /* 通常の ArrayList だと、iterator 操作開始後に元の ArrayList の要素を
         * 追加/削除しようとすると、ConcurrentModificationException が発生する。
         */
        final CopyOnWriteArrayList<String> l0 = new CopyOnWriteArrayList<String>();
        l0.add("A");
        l0.add("B");
        l0.add("C");
        l0.add("D");
        final Iterator<String> it0 = l0.iterator();
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final StringBuilder sb = new StringBuilder("");

        new Thread(() -> {
            try {
                latch1.await();
                while (it0.hasNext()) {
                    sb.append(it0.next());
                }
                latch2.countDown();
            } catch (InterruptedException ignore) {
            }
        }).start();

        // copy-on-write なので、変更操作により内部的には新たな配列にコピーされ、そちらで操作が始まる。
        l0.add("E");
        l0.remove(0);
        latch1.countDown();
        assertThat(latch2.await(10, TimeUnit.SECONDS)).isTrue();
        // iterator側では古い配列を参照している。
        assertThat(sb.toString()).isEqualTo("ABCD");
        // 元のインスタンスの方では、新しい配列にコピーされたものが、変更されている。
        assertThat(l0).isEqualTo(List.of("B", "C", "D", "E"));
    }

    @Test
    public void testBlockingQueueDemo() throws InterruptedException {
        // 容量を5つに制限した BlockingQueue を作成
        final BlockingQueue<Integer> q1 = new ArrayBlockingQueue<>(5);
        final Thread t1 = new Thread(() -> {
            // 5つしか容量の無いqueueに6つの要素を追加しようとする。
            // -> 6つめの要素追加で、スレッドがblockされる。
            for (int i = 0; i < 6; i++) {
                try {
                    q1.put(Integer.valueOf(i));
                    System.out.println("Thread[" + Thread.currentThread().getName() + "], i=" + i + " put.");
                } catch (InterruptedException ignore) {
                }
            }
        });
        t1.start();
        t1.join(2_000);
        // 6つめの要素追加でスレッドがblockされ、終了していないことを確認。
        assertThat(t1.isAlive()).isTrue();
        assertThat(q1.size()).isEqualTo(5);

        final Thread t2 = new Thread(() -> {
            // 5つしか容量の無いqueueから7つの要素を取得しようとする。
            // -> t1 で6つ要素が追加されるが、1つ足りず、スレッドがblockされる。
            for (int i = 0; i < 7; i++) {
                try {
                    final Integer x = q1.take();
                    System.out.println("Thread[" + Thread.currentThread().getName() + "], x=" + x + " take.");
                } catch (InterruptedException ignore) {
                }
            }
        });
        t2.start();
        t2.join(2_000);
        // t1については t2 のtakeで空きができたため、6つめの要素を追加してスレッドが終了。
        assertThat(t1.isAlive()).isFalse();
        // t2については7つめのtake()でスレッドがblockされ、終了していない。
        assertThat(t2.isAlive()).isTrue();
        assertThat(q1.size()).isEqualTo(0);
        // 7つめの要素を追加
        q1.put(0);
        t2.join(2_000);
        // 7つめの要素をtake()して、t2スレッドが終了。
        assertThat(t2.isAlive()).isFalse();
        assertThat(q1.isEmpty()).isTrue();

        final Thread t3 = new Thread(() -> {
            // 5つしか容量の無いqueueに6つの要素を追加しようとする。
            // -> 6つめの要素については500ミリ秒blockingした後諦める。
            for (int i = 0; i < 6; i++) {
                try {
                    q1.offer(Integer.valueOf(i), 500, TimeUnit.MILLISECONDS);
                    System.out.println("Thread[" + Thread.currentThread().getName() + "], i=" + i + " offer.");
                } catch (InterruptedException ignore) {
                }
            }
        });
        t3.start();
        t3.join(2_000);
        // 6つめの要素について500ミリ秒後に諦めたので、t3スレッド自体は終了。
        assertThat(t3.isAlive()).isFalse();
        assertThat(q1.size()).isEqualTo(5);

        final Thread t4 = new Thread(() -> {
            // 5つしか要素が入っていないqueueから6つの要素を取得しようとする。
            // -> 6つめの要素については500ミリ秒blockingした後諦める。
            for (int i = 0; i < 6; i++) {
                try {
                    final Integer x = q1.poll(500, TimeUnit.MILLISECONDS);
                    System.out.println("Thread[" + Thread.currentThread().getName() + "], x=" + x + " poll.");
                } catch (InterruptedException ignore) {
                }
            }
        });
        t4.start();
        t4.join(2_000);
        // 6つめの要素について500ミリ秒後に諦めたので、t4スレッド自体は終了。
        assertThat(t4.isAlive()).isFalse();
        assertThat(q1.isEmpty()).isTrue();
    }

    @Test
    public void testBlockingDequeDemo() throws InterruptedException {
        // 容量を5つに制限した BlockingDeque を作成
        final BlockingDeque<Integer> q1 = new LinkedBlockingDeque<>(5);

        // NOTE: 以下、{put|take|offer|poll}First のみデモする。
        // ({put|take|offer|poll}Last についてのデモは省略)

        final Thread t1 = new Thread(() -> {
            // 5つしか容量の無いqueueに6つの要素を追加しようとする。
            // -> 6つめの要素追加で、スレッドがblockされる。
            for (int i = 0; i < 6; i++) {
                try {
                    q1.putFirst(Integer.valueOf(i));
                    System.out.println("Thread[" + Thread.currentThread().getName() + "], i=" + i + " putFirst.");
                } catch (InterruptedException ignore) {
                }
            }
        });
        t1.start();
        t1.join(2_000);
        // 6つめの要素追加でスレッドがblockされ、終了していないことを確認。
        assertThat(t1.isAlive()).isTrue();
        assertThat(q1.size()).isEqualTo(5);

        final Thread t2 = new Thread(() -> {
            // 5つしか容量の無いqueueから7つの要素を取得しようとする。
            // -> t1 で6つ要素が追加されるが、1つ足りず、スレッドがblockされる。
            for (int i = 0; i < 7; i++) {
                try {
                    final Integer x = q1.takeFirst();
                    System.out.println("Thread[" + Thread.currentThread().getName() + "], x=" + x + " takeFirst.");
                } catch (InterruptedException ignore) {
                }
            }
        });
        t2.start();
        t2.join(2_000);
        // t1については t2 のtakeで空きができたため、6つめの要素を追加してスレッドが終了。
        assertThat(t1.isAlive()).isFalse();
        // t2については7つめのtake()でスレッドがblockされ、終了していない。
        assertThat(t2.isAlive()).isTrue();
        assertThat(q1.size()).isEqualTo(0);
        // 7つめの要素を追加
        q1.putFirst(0);
        t2.join(2_000);
        // 7つめの要素をtake()して、t2スレッドが終了。
        assertThat(t2.isAlive()).isFalse();
        assertThat(q1.isEmpty()).isTrue();

        final Thread t3 = new Thread(() -> {
            // 5つしか容量の無いqueueに6つの要素を追加しようとする。
            // -> 6つめの要素については500ミリ秒blockingした後諦める。
            for (int i = 0; i < 6; i++) {
                try {
                    q1.offerFirst(Integer.valueOf(i), 500, TimeUnit.MILLISECONDS);
                    System.out.println("Thread[" + Thread.currentThread().getName() + "], i=" + i + " offerFirst.");
                } catch (InterruptedException ignore) {
                }
            }
        });
        t3.start();
        t3.join(2_000);
        // 6つめの要素について500ミリ秒後に諦めたので、t3スレッド自体は終了。
        assertThat(t3.isAlive()).isFalse();
        assertThat(q1.size()).isEqualTo(5);

        final Thread t4 = new Thread(() -> {
            // 5つしか要素が入っていないqueueから6つの要素を取得しようとする。
            // -> 6つめの要素については500ミリ秒blockingした後諦める。
            for (int i = 0; i < 6; i++) {
                try {
                    final Integer x = q1.pollFirst(500, TimeUnit.MILLISECONDS);
                    System.out.println("Thread[" + Thread.currentThread().getName() + "], x=" + x + " pollFirst.");
                } catch (InterruptedException ignore) {
                }
            }
        });
        t4.start();
        t4.join(2_000);
        // 6つめの要素について500ミリ秒後に諦めたので、t4スレッド自体は終了。
        assertThat(t4.isAlive()).isFalse();
        assertThat(q1.isEmpty()).isTrue();
    }
}
