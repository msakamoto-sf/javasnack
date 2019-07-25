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

package javasnack.junit.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PersonModel {
    private final PersonRepository repo;
    private final PersonEventNotifier notifier;

    public PersonModel(final PersonRepository repo, final PersonEventNotifier notifier) {
        this.repo = repo;
        this.notifier = notifier;
    }

    public Person register(final String firstName, final String lastName, final int age) {
        final long id = repo.create(firstName, lastName, age, Collections.emptyList());
        if (id <= 0) {
            throw new IllegalStateException("create return id is errorneous value : " + id);
        }
        final Person created = repo.getOne(id).get();
        notifier.notifyRepositoryEvent(RepositoryChangeEvent.CREATE, created);
        return created;
    }

    public void editName(final long id, final String firstName, final String lastName) {
        final Optional<Person> opt = repo.getOne(id);
        if (opt.isEmpty()) {
            return;
        }
        final Person src = opt.get();
        final int affected = repo.update(id, firstName, lastName, src.age, src.hobbies);
        if (affected != 1) {
            throw new IllegalStateException("update affected 0 records, illegal state.");
        }
        final Person updated = new Person(id, firstName, lastName, src.age, src.hobbies);
        notifier.notifyRepositoryEvent(RepositoryChangeEvent.UPDATE, updated);
    }

    public void editAge(final long id, final int age) {
        final Optional<Person> opt = repo.getOne(id);
        if (opt.isEmpty()) {
            return;
        }
        final Person src = opt.get();
        final int affected = repo.update(id, src.firstName, src.lastName, age, src.hobbies);
        if (affected != 1) {
            throw new IllegalStateException("update affected 0 records, illegal state.");
        }
        final Person updated = new Person(id, src.firstName, src.lastName, age, src.hobbies);
        notifier.notifyRepositoryEvent(RepositoryChangeEvent.UPDATE, updated);
    }

    public void addHobby(final long id, final String hobby) {
        final Optional<Person> opt = repo.getOne(id);
        if (opt.isEmpty()) {
            return;
        }
        final Person src = opt.get();
        final List<String> hobbies = new ArrayList<>(src.hobbies);
        hobbies.add(hobby);
        final int affected = repo.update(id, src.firstName, src.lastName, src.age,
                Collections.unmodifiableList(hobbies));
        if (affected != 1) {
            throw new IllegalStateException("update affected 0 records, illegal state.");
        }
        final Person updated = new Person(id, src.firstName, src.lastName, src.age,
                Collections.unmodifiableList(hobbies));
        notifier.notifyRepositoryEvent(RepositoryChangeEvent.UPDATE, updated);
    }

    public void clearHobbies(final long id) {
        final Optional<Person> opt = repo.getOne(id);
        if (opt.isEmpty()) {
            return;
        }
        final Person src = opt.get();
        final int affected = repo.update(id, src.firstName, src.lastName, src.age, Collections.emptyList());
        if (affected != 1) {
            throw new IllegalStateException("update affected 0 records, illegal state.");
        }
        final Person updated = new Person(id, src.firstName, src.lastName, src.age, Collections.emptyList());
        notifier.notifyRepositoryEvent(RepositoryChangeEvent.UPDATE, updated);
    }

    public void remove(final long id) {
        final Optional<Person> opt = repo.getOne(id);
        if (opt.isEmpty()) {
            return;
        }
        final Person deleted = opt.get();
        final int affected = repo.delete(id);
        if (affected != 1) {
            throw new IllegalStateException("delete affected 0 records, illegal state.");
        }
        notifier.notifyRepositoryEvent(RepositoryChangeEvent.DELETE, deleted);
    }

    public Optional<Person> byId(final long id) {
        return repo.getOne(id);
    }

    public List<Person> list() {
        return repo.getList();
    }
}
