/*
 * Copyright 2019 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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

package javasnack.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javasnack.junit.model.Person;
import javasnack.junit.model.PersonEventLoggingNotifier;
import javasnack.junit.model.PersonModel;
import javasnack.junit.model.PersonRepository;
import javasnack.testee.MockitoExercise01;

/* see-also:
 * - https://site.mockito.org/
 * - https://github.com/mockito/mockito
 * - https://static.javadoc.io/org.mockito/mockito-core/3.0.0/org/mockito/Mockito.html
 */
public class TestMockitoExercise01 {

    @Test
    public void test() {
        @SuppressWarnings("unchecked")
        List<String> mockedList = mock(List.class);
        String expectedIndex2 = "Hello";
        when(mockedList.get(2)).thenReturn(expectedIndex2);

        MockitoExercise01 t = new MockitoExercise01(mockedList);
        t.doSomething();

        verify(mockedList).add("Bonjour");
        verify(mockedList).clear();

        assertThat(t.getIndex2()).isEqualTo(expectedIndex2);
    }

    @Test
    public void testMockedMethodsDefaultReturnValues() {
        final PersonRepository repo = mock(PersonRepository.class);
        /* mockしただけで、何も when-thenXXXX を設定していない場合、
         * メソッド戻り値はその型の空値になるっぽい。
         */
        final long newId = repo.create("aaa", "bbb", 1, List.of("cooking"));
        assertThat(newId).isEqualTo(0);
        int affectedRows = repo.update(newId, "ccc", "ddd", 2, List.of("a", "b"));
        assertThat(affectedRows).isEqualTo(0);
        affectedRows = repo.delete(newId);
        assertThat(affectedRows).isEqualTo(0);
        assertThat(repo.getList()).hasSize(0);
        assertThat(repo.getOne(newId).isEmpty()).isTrue();
    }

    @Test
    public void testMockWhenThenReturn() {
        final PersonRepository repo = mock(PersonRepository.class);
        final PersonEventLoggingNotifier loggigNotifier = new PersonEventLoggingNotifier();
        final PersonModel model = new PersonModel(repo, loggigNotifier);

        final Person expected = new Person(1, "aaa", "bbb", 10, List.of("cooking"));
        when(repo.getOne(1)).thenReturn(Optional.of(expected));

        final Optional<Person> opt = model.byId(1);
        assertThat(opt.isPresent()).isTrue();
        final Person p = opt.get();
        assertThat(p).isEqualTo(expected);

        // mockしてない when() は空。
        final Optional<Person> opt2 = model.byId(2);
        assertThat(opt2.isPresent()).isFalse();

        // 途中から追加もできる。
        final Person expected2 = new Person(2, "ccc", "ddd", 20, List.of("book", "running"));
        when(repo.getOne(2)).thenReturn(Optional.of(expected2));
        final Optional<Person> opt2b = model.byId(2);
        assertThat(opt2b.isPresent()).isTrue();
        assertThat(opt2b.get()).isEqualTo(expected2);

        // 引数を具体的なのとanyを組み合わせてみる。
        final Person expected3 = new Person(3, "eee", "fff", 30, List.of("swimming"));
        when(repo.getOne(3L)).thenReturn(Optional.of(expected3));
        final Person expected4 = new Person(4, "ggg", "hhh", 40, List.of("running"));
        when(repo.getOne(anyLong())).thenReturn(Optional.of(expected4));
        final Person expected5 = new Person(5, "iii", "jjj", 50, List.of("walking"));
        when(repo.getOne(5L)).thenReturn(Optional.of(expected5));

        // -> any matcher がそれまでの when-then を上書きする。any matcher のあとに設定したのは有効。
        final Optional<Person> opt3 = model.byId(3);
        assertThat(opt3.get()).isEqualTo(expected4); // not 3
        final Optional<Person> opt4 = model.byId(4);
        assertThat(opt4.get()).isEqualTo(expected4);
        final Optional<Person> opt5 = model.byId(5);
        assertThat(opt5.get()).isEqualTo(expected5);
    }

    @Test
    public void testMockMultiThenReturnValues() {
        final PersonRepository repo = mock(PersonRepository.class);

        when(repo.create(anyString(), anyString(), anyInt(), anyList())).thenReturn(
                Long.valueOf(1), Long.valueOf(2), Long.valueOf(3));

        assertThat(repo.create("a", "b", 1, List.of("a"))).isEqualTo(1);
        assertThat(repo.create("a", "b", 2, List.of("b"))).isEqualTo(2);
        assertThat(repo.create("a", "b", 3, List.of("c"))).isEqualTo(3);
        // thenReturn の引数分呼び出し終わったあとは、最後の引数が返される。
        assertThat(repo.create("a", "b", 4, List.of("d"))).isEqualTo(3);
        assertThat(repo.create("a", "b", 5, List.of("e"))).isEqualTo(3);
    }

    @Test
    public void testMockWhenThenThrow() {
        final PersonRepository repo = mock(PersonRepository.class);

        when(repo.create(anyString(), anyString(), anyInt(), anyList()))
                .thenThrow(new IllegalArgumentException("demo"));
        final Throwable thrown0 = catchThrowable(() -> {
            repo.create("a", "b", 1, List.of("a"));
        });
        assertThat(thrown0).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("demo");

        // mock 動作を普通のreturnに切り替えようとする・・・が・・・mock済みの例外が発生する。
        final Throwable thrown1 = catchThrowable(() -> {
            when(repo.create(anyString(), anyString(), anyInt(), anyList())).thenReturn(Long.valueOf(10));
        });
        assertThat(thrown1).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("demo");
    }

    @Test
    public void testMockDoNothingAndDoThrowMultiStep() {
        final Runnable runner = mock(Runnable.class);
        doNothing().doNothing().doThrow(new IllegalArgumentException("xxx")).doNothing().when(runner).run();
        runner.run();
        runner.run();
        final Throwable thrown0 = catchThrowable(() -> {
            runner.run();
        });
        assertThat(thrown0).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("xxx");
        runner.run();
        runner.run();
    }

    @Test
    public void testMockDoReturnMultiStep() throws Exception {
        @SuppressWarnings("unchecked")
        final Callable<String> callable = mock(Callable.class);

        doReturn("aaa").doReturn("bbb").doThrow(new IllegalStateException("xxx")).doReturn("ccc")
                .when(callable).call();
        assertThat(callable.call()).isEqualTo("aaa");
        assertThat(callable.call()).isEqualTo("bbb");
        final Throwable thrown0 = catchThrowable(() -> {
            callable.call();
        });
        assertThat(thrown0).isInstanceOf(IllegalStateException.class).hasMessageContaining("xxx");
        assertThat(callable.call()).isEqualTo("ccc");
    }

    @Test
    public void testMockVerifyInvocationsAndResetMockInteractions() {
        final PersonRepository repo = mock(PersonRepository.class);

        repo.create("aaa", "bbb", 10, List.of("a", "b"));
        repo.create("aaa", "ccc", 20, List.of("a", "b"));
        repo.create("bbb", "ccc", 30, List.of("c", "d"));

        verify(repo).create("aaa", "bbb", 10, List.of("a", "b"));
        verify(repo, times(1)).create("aaa", "bbb", 10, List.of("a", "b"));
        verify(repo, times(1)).create(eq("aaa"), eq("bbb"), eq(10), eq(List.of("a", "b")));
        verify(repo, times(3)).create(anyString(), anyString(), anyInt(), anyList());
        verify(repo, times(2)).create(eq("aaa"), anyString(), anyInt(), anyList());
        verify(repo, atLeast(2)).create(eq("aaa"), anyString(), anyInt(), anyList());
        verify(repo, atMost(3)).create(anyString(), anyString(), anyInt(), anyList());
        verify(repo, atLeastOnce()).create(eq("bbb"), anyString(), anyInt(), anyList());
        verify(repo, never()).create(eq("xxx"), anyString(), anyInt(), anyList());

        reset(repo);
        verifyNoInteractions(repo);
        verifyNoMoreInteractions(repo);

        final PersonRepository repo2 = mock(PersonRepository.class);
        verifyNoInteractions(repo2);
        verifyNoMoreInteractions(repo2);
    }

    @Test
    public void testMockVerifyArgumentCapture() {
        final PersonRepository repo = mock(PersonRepository.class);
        repo.getOne(100L);
        final ArgumentCaptor<Long> longac = ArgumentCaptor.forClass(Long.class);
        verify(repo).getOne(longac.capture());
        assertThat(longac.getValue()).isEqualTo(100L);

        repo.create("aaa", "bbb", 10, List.of("a", "b"));
        repo.create("aaa", "ccc", 20, List.of("a", "b"));
        repo.create("bbb", "ccc", 30, List.of("c", "d"));
        final ArgumentCaptor<String> firstNameCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> lastNameCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Integer> ageCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(repo, times(3)).create(
                firstNameCaptor.capture(),
                lastNameCaptor.capture(),
                ageCaptor.capture(),
                anyList());
        assertThat(firstNameCaptor.getAllValues()).isEqualTo(List.of("aaa", "aaa", "bbb"));
        assertThat(lastNameCaptor.getAllValues()).isEqualTo(List.of("bbb", "ccc", "ccc"));
        assertThat(ageCaptor.getAllValues()).isEqualTo(List.of(10, 20, 30));
    }

    @Test
    public void testSpyVerifyInvocations() {
        final PersonRepository repo = new PersonRepository();
        final PersonRepository spiedRepo = spy(repo);
        final long id1 = spiedRepo.create("aaa", "bbb", 10, List.of("a", "b"));
        final long id2 = spiedRepo.create("bbb", "ccc", 20, List.of("b", "c"));
        final long id3 = spiedRepo.create("ccc", "ddd", 30, List.of("c", "d"));
        int affectedRows = spiedRepo.update(id1, "AAA", "BBB", 15, List.of("A", "B"));
        assertThat(affectedRows).isEqualTo(1);
        affectedRows = spiedRepo.delete(id2);
        assertThat(affectedRows).isEqualTo(1);
        final Optional<Person> opt = spiedRepo.getOne(id3);
        final Person expected3 = new Person(id3, "ccc", "ddd", 30, List.of("c", "d"));
        assertThat(opt.get()).isEqualTo(expected3);

        verify(spiedRepo, times(3)).create(anyString(), anyString(), anyInt(), anyList());
        verify(spiedRepo, times(1)).update(anyLong(), anyString(), anyString(), anyInt(), anyList());
        verify(spiedRepo, times(1)).delete(anyLong());
        final ArgumentCaptor<Long> longac = ArgumentCaptor.forClass(Long.class);
        verify(spiedRepo).getOne(longac.capture());
        assertThat(longac.getValue()).isEqualTo(id3);

        // real object:
        final Optional<Person> opt1 = repo.getOne(id1);
        final Person expected1 = new Person(id1, "AAA", "BBB", 15, List.of("A", "B"));
        assertThat(opt1.get()).isEqualTo(expected1);
        final Optional<Person> opt2 = repo.getOne(id2);
        assertThat(opt2.isEmpty()).isTrue();
        final Optional<Person> opt3 = repo.getOne(id3);
        assertThat(opt3.get()).isEqualTo(expected3);
    }

    @Test
    public void testSpyStubRealMethod() {
        final PersonRepository repo = new PersonRepository();
        final PersonRepository spiedRepo = spy(repo);
        final long id1 = spiedRepo.create("aaa", "bbb", 10, List.of("a", "b"));

        doReturn(99).when(spiedRepo).update(anyLong(), anyString(), anyString(), anyInt(), anyList());
        int affectedRows = spiedRepo.update(id1, "AAA", "BBB", 15, List.of("A", "B"));
        assertThat(affectedRows).isEqualTo(99);
        verify(spiedRepo, times(1)).update(id1, "AAA", "BBB", 15, List.of("A", "B"));

        // doXxxx does not affect spied object.
        final Optional<Person> opt = spiedRepo.getOne(id1);
        final Person expected1 = new Person(id1, "aaa", "bbb", 10, List.of("a", "b"));
        assertThat(opt.get()).isEqualTo(expected1);
    }
}
