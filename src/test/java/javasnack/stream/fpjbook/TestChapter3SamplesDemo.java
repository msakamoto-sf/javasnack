package javasnack.stream.fpjbook;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import lombok.Value;

public class TestChapter3SamplesDemo {

    @Test
    public void testCharacterStreamDemo() {
        final String s = "w00t";
        final List<Integer> dst1 = new ArrayList<>();
        s.chars().forEach(dst1::add);
        assertThat(dst1).isEqualTo(List.of(119, 48, 48, 116));

        final List<Character> dst2 = new ArrayList<>();
        s.chars().mapToObj(c -> Character.valueOf((char) c)).forEach(dst2::add);
        assertThat(dst2).isEqualTo(List.of('w', '0', '0', 't'));

        dst2.clear();
        s.chars().filter(Character::isDigit).mapToObj(c -> Character.valueOf((char) c)).forEach(dst2::add);
        assertThat(dst2).isEqualTo(List.of('0', '0'));
    }

    @Value(staticConstructor = "of")
    private static class Person {
        private final String name;
        private final int age;

        public int ageDifference(final Person other) {
            return age - other.age;
        }
    }

    @Test
    public void testComparatorDemo() {
        final List<Person> people = List.of(
                Person.of("Sara", 21),
                Person.of("Greg", 35),
                Person.of("John", 20),
                Person.of("Jane", 21));
        final List<String> sortedNamesByAgeAscending = people.stream()
                .sorted((person1, person2) -> person1.ageDifference(person2))
                .map(p -> p.getName())
                .collect(Collectors.toList());
        assertThat(sortedNamesByAgeAscending).isEqualTo(List.of("John", "Sara", "Jane", "Greg"));

        final List<String> sortedNamesByAgeAscending2 = people.stream()
                .sorted(Person::ageDifference)
                .map(p -> p.getName())
                .collect(Collectors.toList());
        assertThat(sortedNamesByAgeAscending2).isEqualTo(List.of("John", "Sara", "Jane", "Greg"));

        final List<String> sortedNamesByAgeDescending = people.stream()
                .sorted((person1, person2) -> person2.ageDifference(person1))
                .map(p -> p.getName())
                .collect(Collectors.toList());
        assertThat(sortedNamesByAgeDescending).isEqualTo(List.of("Greg", "Sara", "Jane", "John"));

        final Comparator<Person> compareByAgeAscending = (person1, person2) -> person1.ageDifference(person2);
        assertThat(people.stream().sorted(compareByAgeAscending).map(Person::getName).collect(Collectors.toList()))
                .isEqualTo(List.of("John", "Sara", "Jane", "Greg"));
        assertThat(people.stream().min(compareByAgeAscending).orElse(Person.of("", -1)).getName()).isEqualTo("John");
        assertThat(people.stream().max(compareByAgeAscending).orElse(Person.of("", -1)).getName()).isEqualTo("Greg");

        final Comparator<Person> compareByAgeDescending = compareByAgeAscending.reversed();
        assertThat(people.stream().sorted(compareByAgeDescending).map(Person::getName).collect(Collectors.toList()))
                .isEqualTo(List.of("Greg", "Sara", "Jane", "John"));
        assertThat(people.stream().min(compareByAgeDescending).orElse(Person.of("", -1)).getName()).isEqualTo("Greg");
        assertThat(people.stream().max(compareByAgeDescending).orElse(Person.of("", -1)).getName()).isEqualTo("John");

        final Function<Person, Integer> byAge = person -> person.getAge();
        final Function<Person, String> byName = person -> person.getName();
        final List<String> sortedNamesByAgeThenName = people.stream()
                .sorted(Comparator.comparing(byAge).thenComparing(byName)).map(Person::getName)
                .collect(Collectors.toList());
        assertThat(sortedNamesByAgeThenName).isEqualTo(List.of("John", "Jane", "Sara", "Greg"));
    }

    @Test
    public void testCollectorsDemo() {
        final List<Person> people = List.of(
                Person.of("Sara", 21),
                Person.of("Greg", 35),
                Person.of("John", 20),
                Person.of("Jane", 21));

        final List<Person> olderThan20 = people.stream()
                .filter(p -> p.getAge() > 20)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        assertThat(olderThan20.size()).isEqualTo(3);
        assertThat(olderThan20.contains(Person.of("Sara", 21))).isTrue();
        assertThat(olderThan20.contains(Person.of("Greg", 35))).isTrue();
        assertThat(olderThan20.contains(Person.of("Jane", 21))).isTrue();

        final List<Person> olderThan20b = people.stream()
                .filter(p -> p.getAge() > 20)
                .collect(Collectors.toList());
        assertThat(olderThan20b.size()).isEqualTo(3);
        assertThat(olderThan20b.contains(Person.of("Sara", 21))).isTrue();
        assertThat(olderThan20b.contains(Person.of("Greg", 35))).isTrue();
        assertThat(olderThan20b.contains(Person.of("Jane", 21))).isTrue();

        final Map<Integer, List<Person>> peopleByAge = people.stream()
                .collect(Collectors.groupingBy(Person::getAge));
        assertThat(peopleByAge.size()).isEqualTo(3);
        assertThat(peopleByAge.get(35).size()).isEqualTo(1);
        assertThat(peopleByAge.get(35).contains(Person.of("Greg", 35))).isTrue();
        assertThat(peopleByAge.get(21).size()).isEqualTo(2);
        assertThat(peopleByAge.get(21).contains(Person.of("Sara", 21))).isTrue();
        assertThat(peopleByAge.get(21).contains(Person.of("Jane", 21))).isTrue();
        assertThat(peopleByAge.get(20).size()).isEqualTo(1);
        assertThat(peopleByAge.get(20).contains(Person.of("John", 20))).isTrue();

        final Map<Integer, List<String>> nameOfPeopleByAge = people.stream()
                .collect(
                        Collectors.groupingBy(Person::getAge,
                                Collectors.mapping(Person::getName, Collectors.toList())));
        assertThat(nameOfPeopleByAge.size()).isEqualTo(3);
        assertThat(nameOfPeopleByAge.get(35).size()).isEqualTo(1);
        assertThat(nameOfPeopleByAge.get(35).contains("Greg")).isTrue();
        assertThat(nameOfPeopleByAge.get(21).size()).isEqualTo(2);
        assertThat(nameOfPeopleByAge.get(21).contains("Sara")).isTrue();
        assertThat(nameOfPeopleByAge.get(21).contains("Jane")).isTrue();
        assertThat(nameOfPeopleByAge.get(20).size()).isEqualTo(1);
        assertThat(nameOfPeopleByAge.get(20).contains("John")).isTrue();

        final Comparator<Person> byAge = Comparator.comparing(Person::getAge);
        final Map<Character, Optional<Person>> oldestPersonOfEachLetter = people.stream()
                .collect(
                        Collectors.groupingBy(p -> p.getName().charAt(0),
                                Collectors.reducing(BinaryOperator.maxBy(byAge))));

        assertThat(oldestPersonOfEachLetter.size()).isEqualTo(3);
        assertThat(oldestPersonOfEachLetter.get('S').get()).isEqualTo(Person.of("Sara", 21));
        assertThat(oldestPersonOfEachLetter.get('G').get()).isEqualTo(Person.of("Greg", 35));
        assertThat(oldestPersonOfEachLetter.get('J').get()).isEqualTo(Person.of("Jane", 21));
    }
}
