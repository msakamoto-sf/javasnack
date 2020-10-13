package javasnack.ojcp.se8gold.chapter10;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

public class Test10AtomicPrimitivesDemo {
    @Test
    public void testAtomicBooleanDemo() {
        AtomicBoolean av0 = new AtomicBoolean();
        assertThat(av0.get()).isFalse();
        av0 = new AtomicBoolean(true);
        assertThat(av0.get()).isTrue();

        av0.set(false);
        assertThat(av0.get()).isFalse();
        av0.set(true);
        assertThat(av0.get()).isTrue();

        assertThat(av0.getAndSet(false)).isTrue();
        assertThat(av0.get()).isFalse();
        assertThat(av0.getAndSet(false)).isFalse();
        assertThat(av0.get()).isFalse();
        assertThat(av0.getAndSet(true)).isFalse();
        assertThat(av0.get()).isTrue();

        // compareAndSet(expected, newvalue) は 現在の値 == expected ならtrue を返して set する。
        // 現在値 != expected なら何もせず false を返す。
        assertThat(av0.compareAndSet(true, false)).isTrue();
        assertThat(av0.get()).isFalse();
        assertThat(av0.compareAndSet(true, false)).isFalse();
        assertThat(av0.get()).isFalse();

        assertThat(av0.compareAndSet(false, true)).isTrue();
        assertThat(av0.get()).isTrue();
        assertThat(av0.compareAndSet(false, true)).isFalse();
        assertThat(av0.get()).isTrue();
    }

    @Test
    public void testAtomicIntegerDemo() {
        AtomicInteger av0 = new AtomicInteger();
        assertThat(av0.get()).isEqualTo(0);
        av0 = new AtomicInteger(2);
        assertThat(av0.get()).isEqualTo(2);
        assertThat(av0.intValue()).isEqualTo(2);
        assertThat(av0.longValue()).isEqualTo(2L);
        assertThat(av0.floatValue()).isEqualTo(2.0f);
        assertThat(av0.doubleValue()).isEqualTo(2.0);

        av0.set(3);
        assertThat(av0.get()).isEqualTo(3);
        assertThat(av0.getAndSet(4)).isEqualTo(3);
        assertThat(av0.get()).isEqualTo(4);

        assertThat(av0.compareAndSet(4, 5)).isTrue();
        assertThat(av0.get()).isEqualTo(5);
        assertThat(av0.compareAndSet(4, 5)).isFalse();
        assertThat(av0.get()).isEqualTo(5);

        assertThat(av0.compareAndSet(5, 6)).isTrue();
        assertThat(av0.get()).isEqualTo(6);
        assertThat(av0.compareAndSet(5, 6)).isFalse();
        assertThat(av0.get()).isEqualTo(6);

        // return previous value
        assertThat(av0.getAndAdd(2)).isEqualTo(6);
        assertThat(av0.get()).isEqualTo(8);
        assertThat(av0.getAndIncrement()).isEqualTo(8);
        assertThat(av0.get()).isEqualTo(9);
        assertThat(av0.getAndDecrement()).isEqualTo(9);
        assertThat(av0.get()).isEqualTo(8);
        assertThat(av0.getAndAccumulate(2, (current, update) -> current / update)).isEqualTo(8);
        assertThat(av0.get()).isEqualTo(4);
        assertThat(av0.getAndUpdate(current -> current * 2)).isEqualTo(4);
        assertThat(av0.get()).isEqualTo(8);

        // return calculated value
        assertThat(av0.addAndGet(2)).isEqualTo(10);
        assertThat(av0.get()).isEqualTo(10);
        assertThat(av0.incrementAndGet()).isEqualTo(11);
        assertThat(av0.get()).isEqualTo(11);
        assertThat(av0.decrementAndGet()).isEqualTo(10);
        assertThat(av0.get()).isEqualTo(10);
        assertThat(av0.accumulateAndGet(2, (current, update) -> current / update)).isEqualTo(5);
        assertThat(av0.get()).isEqualTo(5);
        assertThat(av0.updateAndGet(current -> current * 2)).isEqualTo(10);
        assertThat(av0.get()).isEqualTo(10);
    }

    @Test
    public void testAtomicLongDemo() {
        AtomicLong av0 = new AtomicLong();
        assertThat(av0.get()).isEqualTo(0);
        av0 = new AtomicLong(2L);
        assertThat(av0.get()).isEqualTo(2L);
        assertThat(av0.intValue()).isEqualTo(2);
        assertThat(av0.longValue()).isEqualTo(2L);
        assertThat(av0.floatValue()).isEqualTo(2.0f);
        assertThat(av0.doubleValue()).isEqualTo(2.0);

        // 他はAtomicIntegerと同様なので省略
    }

    @Test
    public void testAtomicReferenceDemo() {
        AtomicReference<String> av0 = new AtomicReference<>();
        assertThat(av0.get()).isNull();
        av0 = new AtomicReference<>("aaa");
        assertThat(av0.get()).isEqualTo("aaa");

        av0.set("bbb");
        assertThat(av0.get()).isEqualTo("bbb");
        av0.set("ccc");
        assertThat(av0.get()).isEqualTo("ccc");

        assertThat(av0.getAndSet("ddd")).isEqualTo("ccc");
        assertThat(av0.get()).isEqualTo("ddd");
        assertThat(av0.getAndSet("eee")).isEqualTo("ddd");
        assertThat(av0.get()).isEqualTo("eee");

        // compareAndSet(expected, newvalue) は 現在の値 == expected ならtrue を返して set する。
        // 現在値 != expected なら何もせず false を返す。
        assertThat(av0.compareAndSet("eee", "fff")).isTrue();
        assertThat(av0.get()).isEqualTo("fff");
        assertThat(av0.compareAndSet("eee", "fff")).isFalse();
        assertThat(av0.get()).isEqualTo("fff");

        assertThat(av0.compareAndSet("fff", "ggg")).isTrue();
        assertThat(av0.get()).isEqualTo("ggg");
        assertThat(av0.compareAndSet("fff", "ggg")).isFalse();
        assertThat(av0.get()).isEqualTo("ggg");

        // return previous value
        assertThat(av0.getAndAccumulate("hhh", (current, newvalue) -> current + newvalue)).isEqualTo("ggg");
        assertThat(av0.get()).isEqualTo("ggghhh");
        assertThat(av0.getAndUpdate(current -> current.replace("hhh", ""))).isEqualTo("ggghhh");
        assertThat(av0.get()).isEqualTo("ggg");

        // return calculated value
        assertThat(av0.accumulateAndGet("hhh", (current, newvalue) -> current + newvalue)).isEqualTo("ggghhh");
        assertThat(av0.get()).isEqualTo("ggghhh");
        assertThat(av0.updateAndGet(current -> current.replace("hhh", ""))).isEqualTo("ggg");
        assertThat(av0.get()).isEqualTo("ggg");
    }
}
