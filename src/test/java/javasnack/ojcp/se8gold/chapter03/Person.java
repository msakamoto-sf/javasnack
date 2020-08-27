package javasnack.ojcp.se8gold.chapter03;

import java.util.Comparator;

public class Person {
    final String firstName;
    final String lastName;
    final int age;

    private Person(final String firstName, final String lastName, final int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    static Person of(final String firstName, final String lastName, final int age) {
        return new Person(firstName, lastName, age);
    }

    // トリビア : Comparator は java.util, Comparable は java.lang
    static Comparator<Person> comparator() {
        return new Comparator<>() {
            @Override
            public int compare(Person o1, Person o2) {
                // firstName -> lastName -> age の昇順にソートする。
                int r = o1.firstName.compareTo(o2.firstName);
                if (r != 0) {
                    return r;
                }
                r = o1.lastName.compareTo(o2.lastName);
                if (r != 0) {
                    return r;
                }
                return Integer.compare(o1.age, o2.age);
            }

            /* NOTE: Comparator interface では Object.equals() も override されている。
             * これについては Comparator 自体が同じソートを行うものかを確認する用途であり、
             * ソート処理の実装によってはそれによりパフォーマンスが向上するケースがあるとのこと。
             * ただ、一般的にはわざわざ Comparator.equals() を override(implement) するのは非推奨。
             * ref: https://docs.oracle.com/javase/jp/11/docs/api/java.base/java/util/Comparator.html#equals(java.lang.Object)
             */
        };
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + age;
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
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
        if (firstName == null) {
            if (other.firstName != null) {
                return false;
            }
        } else if (!firstName.equals(other.firstName)) {
            return false;
        }
        if (lastName == null) {
            if (other.lastName != null) {
                return false;
            }
        } else if (!lastName.equals(other.lastName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Person [firstName=" + firstName + ", lastName=" + lastName + ", age=" + age + "]";
    }
}
