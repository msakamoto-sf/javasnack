package javasnack.ojcp.se8gold;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

public class Test02FunctionalInterfaceDemo {

    static class SomeFunction1 implements Function<String, String> {
        private String name1;

        SomeFunction1(String name1) {
            this.name1 = name1;
        }

        @Override
        public String apply(String name2) {
            return "hello, " + name1 + " " + name2;
        }
    }

    @Test
    public void testFunctionalInterfaceUsageDemo() {
        assertThat(new SomeFunction1("big").apply("world")).isEqualTo("hello, big world");

        Function<String, String> f1 = new Function<>() {
            @Override
            public String apply(String name) {
                return "hello, " + name;
            }
        };
        assertThat(f1.apply("world")).isEqualTo("hello, world");
    }

    /* 自分で作る関数型インターフェイスのデモ
     * 
     * 題材: 組み込み関数型インターフェイスの、例外をthrow可能なバージョン
     * ref1: https://qiita.com/q-ikawa/items/3f55089e9081e1a854bc
     * ref2: https://qiita.com/daylife/items/b977f4f29b1f8ced3a02
     * ref3: https://qiita.com/yoshi389111/items/c6b7d373a00f8fd3d5f3
     * ref4: https://qiita.com/KIchiro/items/4fafd74c46d08275eb56
     * ref5: https://qiita.com/mt-village/items/b88aa98f2c7064d6eee5
     */
    @FunctionalInterface
    static interface ThrowableFunction<T, R> {
        // abstract method は1つだけにする。
        R apply(T t) throws Exception;

        // java.lang.Objcet の public method をabstract methodとして宣言可能(無くても良い)
        String toString();

        boolean equals(Object o);

        int hashCode();

        // static method, default method を定義可能
        static void m1() {
        }

        default void m2() {
        }
    }

    @Test
    public void testFunctionalInterfaceCustomDemo() throws Exception {
        ThrowableFunction<String, String> f1 = (String s) -> "hello, " + s;
        assertThat(f1.apply("world")).isEqualTo("hello, world");
    }
}
