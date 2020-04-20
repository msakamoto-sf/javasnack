/*
 * Copyright 2020 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javasnack.functionlambdastream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import lombok.Value;

public class TestStreamCollectorsDemo {

    @Test
    public void testCountingDemo() {
        final Collector<Integer, ?, Long> col1 = Collectors.counting();
        assertThat(col1.characteristics()).isEqualTo(Collections.emptySet());
        assertThat(Stream.of(1, 2, 3, 4, 5).collect(col1)).isEqualTo(5L);
    }

    @Test
    public void testAveragingDemo() {
        final Collector<Integer, ?, Double> col1 = Collectors.averagingInt(i -> i * 2);
        assertThat(col1.characteristics()).isEqualTo(Collections.emptySet());
        assertThat(Stream.of(1, 2, 3, 4, 5).collect(col1)).isCloseTo(6.0, Offset.offset(0.001));
    }

    @Test
    public void testMaxByMinByDemo() {
        final Collector<Integer, ?, Optional<Integer>> col1 = Collectors.maxBy(Comparator.naturalOrder());
        assertThat(col1.characteristics()).isEqualTo(Collections.emptySet());
        assertThat(Stream.of(1, 2, 3, 4, 5).collect(col1)).get().isEqualTo(5);

        final Collector<Integer, ?, Optional<Integer>> col2 = Collectors.minBy(Comparator.naturalOrder());
        assertThat(col2.characteristics()).isEqualTo(Collections.emptySet());
        assertThat(Stream.of(1, 2, 3, 4, 5).collect(col2)).get().isEqualTo(1);
    }

    @Test
    public void testSummingDemo() {
        final Collector<Integer, ?, Integer> col1 = Collectors.summingInt(i -> i * 2);
        assertThat(col1.characteristics()).isEqualTo(Collections.emptySet());
        assertThat(Stream.of(1, 2, 3, 4, 5).collect(col1)).isEqualTo(30);
    }

    @Test
    public void testSummarizingDemo() {
        final Collector<Integer, ?, IntSummaryStatistics> col1 = Collectors.summarizingInt(i -> i * 2);
        assertThat(col1.characteristics()).isEqualTo(new HashSet<>(List.of(Collector.Characteristics.IDENTITY_FINISH)));
        final IntSummaryStatistics r = Stream.of(1, 2, 3, 4, 5).collect(col1);
        assertThat(r.getCount()).isEqualTo(5);
        assertThat(r.getMax()).isEqualTo(10);
        assertThat(r.getMin()).isEqualTo(2);
        assertThat(r.getSum()).isEqualTo(30L);
        assertThat(r.getAverage()).isCloseTo(6.0, Offset.offset(0.001));
    }

    @Test
    public void testJoiningDemo() {
        final Collector<CharSequence, ?, String> col1 = Collectors.joining(",", "<", ">");
        assertThat(col1.characteristics()).isEqualTo(Collections.emptySet());
        assertThat(Stream.of("aa", "bb", "cc").collect(col1)).isEqualTo("<aa,bb,cc>");
        assertThat(Stream.of("aa", "bb", "cc").collect(Collectors.joining(","))).isEqualTo("aa,bb,cc");
        assertThat(Stream.of("aa", "bb", "cc").collect(Collectors.joining())).isEqualTo("aabbcc");
    }

    @Test
    public void testFilteringDemo() {
        final Collector<Integer, ?, Long> col1 = Collectors.filtering(
                i -> i > 3,
                Collectors.counting());
        assertThat(col1.characteristics()).isEqualTo(Collections.emptySet());
        assertThat(Stream.of(1, 2, 3, 4, 5).collect(col1)).isEqualTo(2L);
    }

    @Test
    public void testToListDemo() {
        final Collector<String, ?, List<String>> col1 = Collectors.toList();
        List<String> l1 = Stream.of("aa", "bb", "cc").collect(col1);
        assertThat(l1).isEqualTo(List.of("aa", "bb", "cc"));
        l1.add("dd");
        assertThat(l1).isEqualTo(List.of("aa", "bb", "cc", "dd"));
    }

    @Test
    public void testToSetDemo() {
        final Collector<String, ?, Set<String>> col1 = Collectors.toSet();
        final Set<String> s1 = Stream.of("aa", "bb", "cc").collect(col1);
        assertThat(s1).isEqualTo(Set.of("aa", "bb", "cc"));
        s1.add("dd");
        assertThat(s1).isEqualTo(Set.of("aa", "bb", "cc", "dd"));
    }

    @Test
    public void testToMapDemo() {
        final Collector<String, ?, Map<String, File>> col1 = Collectors.toMap(s -> s, s -> new File(s));
        final Map<String, File> m1 = Stream.of("./aaa", "./bbb", "./ccc").collect(col1);
        assertThat(m1).hasSize(3);
        assertThat(m1.get("./aaa")).isEqualTo(new File("./aaa"));
        assertThat(m1.get("./bbb")).isEqualTo(new File("./bbb"));
        assertThat(m1.get("./ccc")).isEqualTo(new File("./ccc"));
        m1.put("./ddd", new File("./ddd"));
        assertThat(m1).hasSize(4);
        assertThat(m1.get("./ddd")).isEqualTo(new File("./ddd"));
    }

    @Test
    public void testToUnmodifiableListDemo() {
        final Collector<String, ?, List<String>> col1 = Collectors.toUnmodifiableList();
        List<String> l1 = Stream.of("aa", "bb", "cc").collect(col1);
        assertThat(l1).isEqualTo(List.of("aa", "bb", "cc"));
        final Throwable thrown1 = catchThrowable(() -> l1.add("dd"));
        assertThat(thrown1).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testToUnmodifiableSetDemo() {
        final Collector<String, ?, Set<String>> col1 = Collectors.toUnmodifiableSet();
        final Set<String> s1 = Stream.of("aa", "bb", "cc").collect(col1);
        assertThat(s1).isEqualTo(Set.of("aa", "bb", "cc"));
        final Throwable thrown1 = catchThrowable(() -> s1.add("dd"));
        assertThat(thrown1).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testToMapUnmodifiableDemo() {
        final Collector<String, ?, Map<String, File>> col1 = Collectors.toUnmodifiableMap(s -> s, s -> new File(s));
        final Map<String, File> m1 = Stream.of("./aaa", "./bbb", "./ccc").collect(col1);
        assertThat(m1).hasSize(3);
        assertThat(m1.get("./aaa")).isEqualTo(new File("./aaa"));
        assertThat(m1.get("./bbb")).isEqualTo(new File("./bbb"));
        assertThat(m1.get("./ccc")).isEqualTo(new File("./ccc"));
        final Throwable thrown1 = catchThrowable(() -> m1.put("./ddd", new File("./ddd")));
        assertThat(thrown1).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testToCollectionDemo() {
        final Collector<String, ?, Collection<String>> col1 = Collectors.toCollection(() -> new ArrayList<>());
        Collection<String> l1 = Stream.of("aa", "bb", "cc").collect(col1);
        assertThat(l1).isEqualTo(List.of("aa", "bb", "cc"));
        l1.add("dd");
        assertThat(l1).isEqualTo(List.of("aa", "bb", "cc", "dd"));
    }

    @Test
    public void testToCollectingAndThenDemo() {
        final Collector<String, ?, List<String>> col1 = Collectors.toList();
        final Collector<String, ?, List<String>> col2 = Collectors.collectingAndThen(col1,
                Collections::unmodifiableList);
        List<String> l1 = Stream.of("aa", "bb", "cc").collect(col2);
        assertThat(l1).isEqualTo(List.of("aa", "bb", "cc"));
        final Throwable thrown1 = catchThrowable(() -> l1.add("dd"));
        assertThat(thrown1).isInstanceOf(UnsupportedOperationException.class);
    }

    enum RoleOfEmployee {
        JUNIOR, SENIOR, MANAGER, CHIEF;
    }

    @Value(staticConstructor = "of")
    static class Employee {
        private String name;
        private int age;
        private RoleOfEmployee role;
        private Set<String> hobbies;

        static Employee of(final String name, final int age, final RoleOfEmployee role) {
            return of(name, age, role, Collections.emptySet());
        }

        static Employee of(final String name, final int age, final RoleOfEmployee role, final String... hobbies) {
            return of(name, age, role, Set.of(hobbies));
        }
    }

    @Test
    public void testMappingDemo() {
        final Collector<Employee, ?, Set<RoleOfEmployee>> col1 = Collectors.mapping(
                Employee::getRole,
                Collectors.toSet());
        final Set<RoleOfEmployee> s1 = Stream.of(
                Employee.of("alice", 20, RoleOfEmployee.JUNIOR),
                Employee.of("bob", 20, RoleOfEmployee.SENIOR),
                Employee.of("clice", 20, RoleOfEmployee.MANAGER),
                Employee.of("daniel", 20, RoleOfEmployee.JUNIOR),
                Employee.of("evan", 20, RoleOfEmployee.SENIOR),
                Employee.of("freddy", 20, RoleOfEmployee.MANAGER)).collect(col1);
        assertThat(s1).hasSize(3);
        assertThat(s1.contains(RoleOfEmployee.JUNIOR)).isTrue();
        assertThat(s1.contains(RoleOfEmployee.SENIOR)).isTrue();
        assertThat(s1.contains(RoleOfEmployee.MANAGER)).isTrue();
    }

    @Test
    public void testReducingDemo() {
        final Collector<Employee, ?, Optional<Employee>> col1 = Collectors
                .reducing(BinaryOperator.maxBy(Comparator.comparing(Employee::getAge)));
        final Employee e1 = Stream.of(
                Employee.of("alice", 20, RoleOfEmployee.JUNIOR),
                Employee.of("bob", 30, RoleOfEmployee.SENIOR),
                Employee.of("clice", 40, RoleOfEmployee.MANAGER),
                Employee.of("daniel", 30, RoleOfEmployee.JUNIOR),
                Employee.of("evan", 50, RoleOfEmployee.SENIOR),
                Employee.of("freddy", 10, RoleOfEmployee.MANAGER)).collect(col1).get();
        assertThat(e1.getName()).isEqualTo("evan");
        assertThat(e1.getAge()).isEqualTo(50);

        final Collector<Employee, ?, Employee> col2 = Collectors
                .reducing(
                        Employee.of("george", 90, RoleOfEmployee.CHIEF),
                        BinaryOperator.maxBy(Comparator.comparing(Employee::getAge)));
        final Employee e2 = Stream.of(
                Employee.of("alice", 20, RoleOfEmployee.JUNIOR),
                Employee.of("bob", 30, RoleOfEmployee.SENIOR),
                Employee.of("clice", 40, RoleOfEmployee.MANAGER),
                Employee.of("daniel", 30, RoleOfEmployee.JUNIOR),
                Employee.of("evan", 50, RoleOfEmployee.SENIOR),
                Employee.of("freddy", 10, RoleOfEmployee.MANAGER)).collect(col2);
        assertThat(e2.getName()).isEqualTo("george");
        assertThat(e2.getAge()).isEqualTo(90);

        final Collector<Employee, ?, String> col3 = Collectors.reducing("", Employee::getName,
                BinaryOperator.maxBy(Comparator.comparing(String::length)));
        final String s3 = Stream.of(
                Employee.of("alice", 20, RoleOfEmployee.JUNIOR),
                Employee.of("bob", 20, RoleOfEmployee.SENIOR),
                Employee.of("clice", 20, RoleOfEmployee.MANAGER),
                Employee.of("daniel", 20, RoleOfEmployee.JUNIOR),
                Employee.of("evan", 20, RoleOfEmployee.SENIOR),
                Employee.of("freddy", 20, RoleOfEmployee.MANAGER)).collect(col3);
        assertThat(s3).isEqualTo("daniel");
    }

    @Test
    public void testFlatMappingDemo() {
        final Collector<Employee, ?, Set<String>> col1 = Collectors
                .flatMapping((Employee employee) -> employee.getHobbies().stream(), Collectors.toSet());
        final Set<String> s1 = Stream.of(
                Employee.of("alice", 20, RoleOfEmployee.JUNIOR, "cooking"),
                Employee.of("bob", 20, RoleOfEmployee.JUNIOR, "walking", "reading"),
                Employee.of("clice", 20, RoleOfEmployee.JUNIOR, "travel"),
                Employee.of("daniel", 20, RoleOfEmployee.JUNIOR, "cooking", "movie"),
                Employee.of("evan", 20, RoleOfEmployee.JUNIOR, "reading", "movie"),
                Employee.of("freddy", 20, RoleOfEmployee.JUNIOR)).collect(col1);
        assertThat(s1).hasSize(5);
        assertThat(s1.contains("cooking")).isTrue();
        assertThat(s1.contains("walking")).isTrue();
        assertThat(s1.contains("reading")).isTrue();
        assertThat(s1.contains("travel")).isTrue();
        assertThat(s1.contains("movie")).isTrue();
    }

    @Test
    public void testGroupingByDemo() {
        final Collector<Employee, ?, Map<RoleOfEmployee, List<Employee>>> col1 = Collectors
                .groupingBy((Employee employee) -> employee.getRole());
        final Map<RoleOfEmployee, List<Employee>> m1 = Stream.of(
                Employee.of("alice", 20, RoleOfEmployee.JUNIOR),
                Employee.of("bob", 20, RoleOfEmployee.SENIOR),
                Employee.of("clice", 20, RoleOfEmployee.MANAGER),
                Employee.of("daniel", 20, RoleOfEmployee.JUNIOR),
                Employee.of("evan", 20, RoleOfEmployee.SENIOR),
                Employee.of("freddy", 20, RoleOfEmployee.MANAGER)).collect(col1);
        assertThat(m1).hasSize(3);
        List<Employee> l0 = m1.get(RoleOfEmployee.JUNIOR);
        assertThat(l0).hasSize(2);
        assertThat(l0.contains(Employee.of("alice", 20, RoleOfEmployee.JUNIOR)));
        assertThat(l0.contains(Employee.of("daniel", 20, RoleOfEmployee.JUNIOR)));
        l0 = m1.get(RoleOfEmployee.SENIOR);
        assertThat(l0).hasSize(2);
        assertThat(l0.contains(Employee.of("bob", 20, RoleOfEmployee.JUNIOR)));
        assertThat(l0.contains(Employee.of("evan", 20, RoleOfEmployee.JUNIOR)));
        l0 = m1.get(RoleOfEmployee.MANAGER);
        assertThat(l0).hasSize(2);
        assertThat(l0.contains(Employee.of("clice", 20, RoleOfEmployee.JUNIOR)));
        assertThat(l0.contains(Employee.of("freddy", 20, RoleOfEmployee.JUNIOR)));

        final Collector<Employee, ?, Map<RoleOfEmployee, Set<String>>> col2 = Collectors
                .groupingBy((Employee employee) -> employee.getRole(),
                        Collectors.mapping(Employee::getName, Collectors.toSet()));
        final Map<RoleOfEmployee, Set<String>> m2 = Stream.of(
                Employee.of("alice", 20, RoleOfEmployee.JUNIOR),
                Employee.of("bob", 20, RoleOfEmployee.SENIOR),
                Employee.of("clice", 20, RoleOfEmployee.MANAGER),
                Employee.of("daniel", 20, RoleOfEmployee.JUNIOR),
                Employee.of("evan", 20, RoleOfEmployee.SENIOR),
                Employee.of("freddy", 20, RoleOfEmployee.MANAGER)).collect(col2);
        assertThat(m2).hasSize(3);
        assertThat(m2.get(RoleOfEmployee.JUNIOR).containsAll(Set.of("alice", "daniel"))).isTrue();
        assertThat(m2.get(RoleOfEmployee.SENIOR).containsAll(Set.of("bob", "evan"))).isTrue();
        assertThat(m2.get(RoleOfEmployee.MANAGER).containsAll(Set.of("clice", "freddy"))).isTrue();
    }

    @Test
    public void testPartitionByDemo() {
        final Collector<Employee, ?, Map<Boolean, List<Employee>>> col1 = Collectors
                .partitioningBy((Employee employee) -> employee.getName().length() == 5);
        final Map<Boolean, List<Employee>> m1 = Stream.of(
                Employee.of("alice", 20, RoleOfEmployee.JUNIOR),
                Employee.of("bob", 20, RoleOfEmployee.SENIOR),
                Employee.of("clice", 20, RoleOfEmployee.MANAGER),
                Employee.of("daniel", 20, RoleOfEmployee.JUNIOR),
                Employee.of("evan", 20, RoleOfEmployee.SENIOR),
                Employee.of("freddy", 20, RoleOfEmployee.MANAGER)).collect(col1);
        assertThat(m1).hasSize(2);
        List<Employee> l0 = m1.get(Boolean.TRUE);
        assertThat(l0.stream().map(e -> e.getName()).collect(Collectors.toList())).isEqualTo(List.of("alice", "clice"));
        l0 = m1.get(Boolean.FALSE);
        assertThat(l0.stream().map(e -> e.getName()).collect(Collectors.toList()))
                .isEqualTo(List.of("bob", "daniel", "evan", "freddy"));

        final Collector<Employee, ?, Map<Boolean, Set<String>>> col2 = Collectors
                .partitioningBy((Employee employee) -> employee.getName().length() == 5,
                        Collectors.mapping((Employee employee) -> employee.getName(), Collectors.toSet()));
        final Map<Boolean, Set<String>> m2 = Stream.of(
                Employee.of("alice", 20, RoleOfEmployee.JUNIOR),
                Employee.of("bob", 20, RoleOfEmployee.SENIOR),
                Employee.of("clice", 20, RoleOfEmployee.MANAGER),
                Employee.of("daniel", 20, RoleOfEmployee.JUNIOR),
                Employee.of("evan", 20, RoleOfEmployee.SENIOR),
                Employee.of("freddy", 20, RoleOfEmployee.MANAGER)).collect(col2);
        assertThat(m2).hasSize(2);
        assertThat(m2.get(Boolean.TRUE)).isEqualTo(Set.of("alice", "clice"));
        assertThat(m2.get(Boolean.FALSE)).isEqualTo(Set.of("bob", "daniel", "evan", "freddy"));
    }
}
