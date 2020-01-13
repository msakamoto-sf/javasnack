package javasnack.functionlambdastream.fpjbook;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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

    void createDemoFileTree(final Path tempDir) throws IOException {
        System.out.println("createDemoFileTree(tempDir=[" + tempDir + "])");
        final String parentDir = tempDir.toString();
        new File(parentDir + "/a.txt").createNewFile();
        new File(parentDir + "/b.txt").createNewFile();
        new File(parentDir + "/c.txt").createNewFile();
        new File(parentDir + "/dir1").mkdir();
        new File(parentDir + "/dir1/a_1.txt").createNewFile();
        new File(parentDir + "/dir1/b_1.txt").createNewFile();
        new File(parentDir + "/dir1/c_1.txt").createNewFile();
        new File(parentDir + "/dir2").mkdir();
        new File(parentDir + "/dir2/a_2.txt").createNewFile();
        new File(parentDir + "/dir2/b_2.txt").createNewFile();
        new File(parentDir + "/dir2/c_2.txt").createNewFile();
        new File(parentDir + "/dir2/dir2_1").mkdir();
        new File(parentDir + "/dir2/dir2_1/a_2_1.txt").createNewFile();
        new File(parentDir + "/dir2/dir2_1/b_2_1.txt").createNewFile();
        new File(parentDir + "/dir2/dir2_1/c_2_1.txt").createNewFile();
        new File(parentDir + "/dir2/dir2_2").mkdir();
        new File(parentDir + "/dir2/dir2_2/a_2_2.txt").createNewFile();
        new File(parentDir + "/dir2/dir2_2/b_2_2.txt").createNewFile();
        new File(parentDir + "/dir2/dir2_2/c_2_2.txt").createNewFile();
        new File(parentDir + "/dir3").mkdir();
        new File(parentDir + "/dir3/dir3_1").mkdir();
        new File(parentDir + "/dir3/dir3_1/dir3_1_1").mkdir();
        new File(parentDir + "/dir3/dir3_1/dir3_1_1/a_3_1_1.txt").createNewFile();
        new File(parentDir + "/dir3/dir3_1/dir3_1_1/b_3_1_1.txt").createNewFile();
        new File(parentDir + "/dir3/dir3_1/dir3_1_1/c_3_1_1.txt").createNewFile();
        new File(parentDir + "/dir3/dir3_1/dir3_1_2").mkdir();
        new File(parentDir + "/dir3/dir3_1/dir3_1_2/a_3_1_2.txt").createNewFile();
        new File(parentDir + "/dir3/dir3_1/dir3_1_2/b_3_1_2.txt").createNewFile();
        new File(parentDir + "/dir3/dir3_1/dir3_1_2/c_3_1_2.txt").createNewFile();
        new File(parentDir + "/dir3/dir3_2").mkdir();
        new File(parentDir + "/dir3/dir3_2/dir3_2_1").mkdir();
        new File(parentDir + "/dir3/dir3_2/dir3_2_1/a_3_2_1.txt").createNewFile();
        new File(parentDir + "/dir3/dir3_2/dir3_2_1/b_3_2_1.txt").createNewFile();
        new File(parentDir + "/dir3/dir3_2/dir3_2_1/c_3_2_1.txt").createNewFile();
        new File(parentDir + "/dir3/dir3_2/dir3_2_2").mkdir();
        new File(parentDir + "/dir4").mkdir();
    }

    @Test
    public void filesListDemo(@TempDir Path tempDir) throws IOException {
        createDemoFileTree(tempDir);
        final String parentDir = tempDir.toString();
        final String sep = File.separator;
        final List<String> path1 = Files.list(tempDir)
                .map(p -> p.toString())
                .collect(Collectors.toList());
        assertThat(path1.size()).isEqualTo(7);
        assertThat(path1.contains(parentDir + sep + "a.txt")).isTrue();
        assertThat(path1.contains(parentDir + sep + "b.txt")).isTrue();
        assertThat(path1.contains(parentDir + sep + "c.txt")).isTrue();
        assertThat(path1.contains(parentDir + sep + "dir1")).isTrue();
        assertThat(path1.contains(parentDir + sep + "dir2")).isTrue();
        assertThat(path1.contains(parentDir + sep + "dir3")).isTrue();
        assertThat(path1.contains(parentDir + sep + "dir4")).isTrue();

        final List<String> path2 = Files.list(tempDir)
                .filter(Files::isDirectory)
                .map(p -> p.toString())
                .collect(Collectors.toList());
        assertThat(path2.size()).isEqualTo(4);
        assertThat(path2.contains(parentDir + sep + "dir1")).isTrue();
        assertThat(path2.contains(parentDir + sep + "dir2")).isTrue();
        assertThat(path2.contains(parentDir + sep + "dir3")).isTrue();
        assertThat(path2.contains(parentDir + sep + "dir4")).isTrue();

        final List<String> path3 = StreamSupport
                .stream(
                        Files.newDirectoryStream(
                                tempDir,
                                path -> path.toString().endsWith(".txt"))
                                .spliterator(),
                        false)
                .map(p -> p.toString())
                .collect(Collectors.toList());
        assertThat(path3.size()).isEqualTo(3);
        assertThat(path3.contains(parentDir + sep + "a.txt")).isTrue();
        assertThat(path3.contains(parentDir + sep + "b.txt")).isTrue();
        assertThat(path3.contains(parentDir + sep + "c.txt")).isTrue();

        final List<String> path4 = Stream.of(tempDir.toFile().listFiles(f -> f.isDirectory()))
                .map(f -> f.toString())
                .collect(Collectors.toList());
        assertThat(path4.size()).isEqualTo(4);
        assertThat(path4.contains(parentDir + sep + "dir1")).isTrue();
        assertThat(path4.contains(parentDir + sep + "dir2")).isTrue();
        assertThat(path4.contains(parentDir + sep + "dir3")).isTrue();
        assertThat(path4.contains(parentDir + sep + "dir4")).isTrue();

        final List<String> path5 = Stream.of(tempDir.toFile().listFiles())
                .flatMap(file -> file.listFiles() == null ? Stream.of(file) : Stream.of(file.listFiles()))
                .map(f -> f.toString())
                .collect(Collectors.toList());
        assertThat(path5.size()).isEqualTo(13);
        assertThat(path5.contains(parentDir + sep + "a.txt")).isTrue();
        assertThat(path5.contains(parentDir + sep + "b.txt")).isTrue();
        assertThat(path5.contains(parentDir + sep + "c.txt")).isTrue();
        assertThat(path5.contains(parentDir + sep + "dir1" + sep + "a_1.txt")).isTrue();
        assertThat(path5.contains(parentDir + sep + "dir1" + sep + "b_1.txt")).isTrue();
        assertThat(path5.contains(parentDir + sep + "dir1" + sep + "c_1.txt")).isTrue();
        assertThat(path5.contains(parentDir + sep + "dir2" + sep + "a_2.txt")).isTrue();
        assertThat(path5.contains(parentDir + sep + "dir2" + sep + "b_2.txt")).isTrue();
        assertThat(path5.contains(parentDir + sep + "dir2" + sep + "c_2.txt")).isTrue();
        assertThat(path5.contains(parentDir + sep + "dir2" + sep + "dir2_1")).isTrue();
        assertThat(path5.contains(parentDir + sep + "dir2" + sep + "dir2_2")).isTrue();
        assertThat(path5.contains(parentDir + sep + "dir3" + sep + "dir3_1")).isTrue();
        assertThat(path5.contains(parentDir + sep + "dir3" + sep + "dir3_2")).isTrue();
    }

    @Test
    public void watchServiceDemo(@TempDir Path tempDir) throws IOException, InterruptedException {
        final String parentDir = tempDir.toString();
        final WatchService ws = tempDir.getFileSystem().newWatchService();
        tempDir.register(ws, StandardWatchEventKinds.ENTRY_CREATE);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    new File(parentDir + "/aaaa.txt").createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 500);
        final WatchKey wk = ws.poll(1, TimeUnit.MINUTES);
        assertThat(wk).isNotNull();
        final List<WatchEvent<?>> events = wk.pollEvents().stream().collect(Collectors.toList());
        assertThat(events.size()).isEqualTo(1);
        assertThat(events.get(0).count()).isEqualTo(1);
        assertThat(events.get(0).kind().name()).isEqualTo(StandardWatchEventKinds.ENTRY_CREATE.name());
        assertThat(events.get(0).kind().type()).isEqualTo(Path.class);
        assertThat(events.get(0).context()).isEqualTo(Paths.get("aaaa.txt"));
    }
}
