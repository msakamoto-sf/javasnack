package javasnack.ojcp.se8gold;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class Test01Basics {
    /* Java の基礎。
     * 全体的には Java SE 8 Silver の内容にenumが加わってる。
     * こちらでは Gold の教科書から、Silverには無かったような内容を
     * 差分として練習用に抜き出している。
     */

    // JavaBeans 例
    static class JavaBeanDemo {
        // メンバ変数は private
        private String name = "";
        private int age = 0;
        private boolean student = false;
        private List<String> hobbies = new ArrayList<>();

        // 外部から getter/setter でアクセス。
        // getter の戻り値型はメンバ変数の型に一致, メソッド名は get 始まり。
        public String getName() {
            return this.name;
        }

        // setter の戻り値はvoid型, 引数はメンバ変数の型に一致, メソッド名は set 始まり。
        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return this.age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public boolean getStudent() {
            return this.student;
        }

        // boolean 型のメンバ変数の getter は is 始まりもOK.
        public boolean isStudent() {
            return this.student;
        }

        public void setStudent(boolean student) {
            this.student = student;
        }

        public List<String> getHobbies() {
            return this.hobbies;
        }

        public void setHobbies(List<String> hobbies) {
            this.hobbies = hobbies;
        }
    }

    // 不変オブジェクトとしての JavaBeans 例
    // final クラスにして継承によるクラス拡張を禁止
    static final class ImmutableJavaBeanDemo {
        // メンバ変数はfinal修飾をして変更不可にする。
        private final String name;
        private final int age;
        private final boolean student;
        private final List<String> hobbies;

        ImmutableJavaBeanDemo(String name, int age, boolean student, List<String> hobbies) {
            this.name = name;
            this.age = age;
            this.student = student;
            // コレクションなど容れ物となるメンバ変数については、コンストラクタ後に変更されないよう配慮。
            this.hobbies = new ArrayList<>(hobbies);
        }

        // getter も final修飾をして override を禁止
        public final String getName() {
            return this.name;
        }

        public final int getAge() {
            return this.age;
        }

        public final boolean isStudent() {
            return this.student;
        }

        public final List<String> getHobbies() {
            // コレクションなど容れ物となるメンバ変数については、元データを変更されないよう配慮。
            return new ArrayList<>(this.hobbies);
        }
    }

    // final クラスの継承と final メソッドの override のデモ

    static class SuperA {
    }

    static final class SuperB {
    }

    static class SuperC {
        void print() {
        }
    }

    static class SuperD {
        final void print() {
        }
    }

    // non final クラスは継承OK.
    static class SubA extends SuperA {
    }

    // final クラスを継承しようとすると compile error
    //static class SubB extends SuperB {
    //}

    static class SubC extends SuperC {
        // non final メソッドは override OK.
        void print() {
        }
    }

    static class SubD extends SuperD {
        // final メソッドを override しようとすると compile error
        //void print() {
        //}
    }

    static class Foo {
        final int num1 = 10;
        final int num2;

        Foo(int i) {
            this.num2 = i;
        }
    }

    @Test
    public void testFinalVars() {
        final Foo o1 = new Foo(100);
        // o1.num1 = 20; // compile error
        // o1 = new Foo(300); // compile error
        Foo o2 = new Foo(400);
        assertThat(o1.num1).isEqualTo(10);
        assertThat(o1.num2).isEqualTo(100);
        assertThat(o2.num1).isEqualTo(10);
        assertThat(o2.num2).isEqualTo(400);
    }

    static class Bar {
        int inum = 10;
        static int snum = 20;

        int im() {
            return this.inum + Bar.snum;
        }

        static int sm() {
            return Bar.snum + 1;
        }
    }

    @SuppressWarnings("static-access")
    @Test
    public void testStaticAccess() {
        assertThat(Bar.snum).isEqualTo(20);
        Bar.snum = 30;
        assertThat(Bar.sm()).isEqualTo(31);

        Bar b1 = new Bar();
        assertThat(b1.inum).isEqualTo(10);
        b1.inum = 11;
        b1.snum = 21; // static フィールドにインスタンスからアクセスできる。
        assertThat(b1.im()).isEqualTo(32);
        // static メソッドをインスタンスから呼び出せる。
        assertThat(b1.sm()).isEqualTo(22);
    }

    static class Baz {
        int i1;
        int i2;
        int i3;
        final int i4;
        final int i5;
        int i6;
        {
            i1 = 10;
            i2 = 20;
            i6 = 160;
            System.out.println("Baz initialize block:1");
        }
        static int si1;
        static int si2;
        static int si3;
        static {
            si1 = 100;
            si2 = 200;
            System.out.println("Baz static initialize block:1");
        }
        {
            i2 = 21;
            i3 = 31;
            i6 = 161;
            System.out.println("Baz initialize block:2");
        }
        static {
            si2 = 201;
            si3 = 300;
            System.out.println("Baz static initialize block:2");
        }

        Baz() {
            i4 = 40;
            i5 = 50;
            i6 = 162;
            System.out.println("Baz constructor(default)");
        }

        Baz(int x) {
            i4 = x;
            i5 = x;
            i6 = x;
            System.out.println("Baz constructor(args)");
        }
    }

    @Test
    public void testInitializerDemo() {
        assertThat(Baz.si1).isEqualTo(100);
        assertThat(Baz.si2).isEqualTo(201); // 後の static {} で上書き
        assertThat(Baz.si3).isEqualTo(300);
        Baz b1 = new Baz();
        assertThat(b1.i1).isEqualTo(10);
        assertThat(b1.i2).isEqualTo(21); // 後の {} で上書き
        assertThat(b1.i3).isEqualTo(31);
        assertThat(b1.i4).isEqualTo(40);
        assertThat(b1.i5).isEqualTo(50);
        assertThat(b1.i6).isEqualTo(162); // constructor が最後に呼ばれる。

        Baz b2 = new Baz(60);
        assertThat(b2.i1).isEqualTo(10);
        assertThat(b2.i2).isEqualTo(21); // 後の {} で上書き
        assertThat(b2.i3).isEqualTo(31);
        assertThat(b2.i4).isEqualTo(60);
        assertThat(b2.i5).isEqualTo(60);
        assertThat(b2.i6).isEqualTo(60); // constructor が最後に呼ばれる。
    }

    // デザインパターンの例 : singleton
    static class MySingleton {
        // クラスロード時に static field としてインスタンス生成し、final 修飾して変更不可にする。
        private static final MySingleton INSTANCE = new MySingleton();

        // コンストラクタを private にして、自由にインスタンス生成できないようにする。
        private MySingleton() {
        }

        // 公開 static メソッドを通じて単一インスタンスへの参照を提供する。
        public static MySingleton getInstance() {
            return INSTANCE;
        }

        // マルチスレッドで呼ばれる想定であれば、インスタンスメソッドは同期化するのを検討する。
        public synchronized void doSomething() {
        }
    }

    // IDE による hashCode(), equals() 自動生成のデモ
    static class Person {
        final String name;
        final int age;
        final Set<String> hobbies;

        Person(final String name, final int age, final Set<String> hobbies) {
            this.name = name;
            this.age = age;
            this.hobbies = Collections.unmodifiableSet(hobbies);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + age;
            result = prime * result + ((hobbies == null) ? 0 : hobbies.hashCode());
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
            Person other = (Person) obj;
            if (age != other.age) {
                return false;
            }
            if (hobbies == null) {
                if (other.hobbies != null) {
                    return false;
                }
            } else if (!hobbies.equals(other.hobbies)) {
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
    public void testObjectEquality() {
        Person p1 = new Person("aaa", 10, Set.of("cooking", "cycling"));
        Person p2 = new Person("aaa", 10, Set.of("cooking", "swimming"));
        Person p3 = new Person("aaa", 10, Set.of("cycling", "cooking"));
        assertThat(p1).isNotEqualTo(p2);
        assertThat(p1).isEqualTo(p3);
        /* hashCode(), equals() のルール ... 教科書より:
         * 
         * 1. 同一のオブジェクトに対して hashCode() が複数回呼び出されても、同一の整数値を返す。
         * 
         * 2. 2つのオブジェクトをequals()で比較してtrueが返るなら、2つのオブジェクトのhashCode()は同じ。
         * o1.equals(o2) == true -> o1.hashCode() == o2.hashCode()
         * 
         * 3. 2つのオブジェクトをequals()で比較してfalseが返るなら、2つのオブジェクトのhashCode()は
         * 同じかもしれないし、異なるかもしれない。(異なる方が HashSet/HashMap 系でのパフォーマンスが向上する)
         * o1.equals(o2) == false -> o1.hashCode() == / != o2.hashCode()
         * 
         * 4. 2つのオブジェクトのhashCode()が異なる場合は、equals()で比較してもfalseとなる。
         * o1.hashCode() != o2.hashCode -> o1.equals(o2) == false
         * 
         * 別の書き方をすれば:
         * equals()がtrueなら、hashCode()は同じ。
         * equals()がfalseだと、hashCode()の同じ/違うは未保証
         * hashCode()が異なれば、equals()も異なる。
         * hashCode()が同じでも、equals()の結果は未保証
         * 
         * hashCode()は計算の結果でしか無いので、hashCode()が同じでも equals() の結果までは保証できない。
         * 逆に、hashCode()が異なるのであれば、確実に equals() も異なることが期待される
         * （ので、そのようにoverrideする）
         * 
         * equals()が同じであれば、中身も同じであるのが期待されるので、
         * 同じ中身から同じ計算により導かれる hashCode() は同一であることが期待される
         * （ので、そのようにoverrideする）
         * equals()が異なれば、中身が異なるので、hashCode()も異なるかもしれない。
         * あるいは計算の都合によって、同じになるかもしれない。（未保証）
         */
    }
}
