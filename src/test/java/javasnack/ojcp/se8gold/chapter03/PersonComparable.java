package javasnack.ojcp.se8gold.chapter03;

//トリビア : Comparator は java.util, Comparable は java.lang
public class PersonComparable implements Comparable<PersonComparable> {
    final String firstName;
    final String lastName;
    final int age;

    private PersonComparable(final String firstName, final String lastName, final int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    static PersonComparable of(final String firstName, final String lastName, final int age) {
        return new PersonComparable(firstName, lastName, age);
    }

    // firstName -> lastName -> age の昇順にソートする。
    @Override
    public int compareTo(PersonComparable o) {
        int r = this.firstName.compareTo(o.firstName);
        if (r != 0) {
            return r;
        }
        r = this.lastName.compareTo(o.lastName);
        if (r != 0) {
            return r;
        }
        return Integer.compare(this.age, o.age);
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
        PersonComparable other = (PersonComparable) obj;
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
        return "PersonComparable [firstName=" + firstName + ", lastName=" + lastName + ", age=" + age + "]";
    }
}
