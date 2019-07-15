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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PersonRepository {
    private long nextId = 1;
    private Map<Long, Person> map = new LinkedHashMap<>();

    public long create(
            final String firstName,
            final String lastName,
            final int age,
            final List<String> hobbies) {
        final Person p = new Person(nextId++, firstName, lastName, age, hobbies);
        map.put(p.id, p);
        return p.id;
    }

    public int update(
            final long id,
            final String firstName,
            final String lastName,
            final int age,
            final List<String> hobbies) {
        if (!map.containsKey(id)) {
            return 0;
        }
        final Person p = new Person(id, firstName, lastName, age, hobbies);
        map.put(p.id, p);
        return 1;
    }

    public int delete(final long id) {
        map.remove(id);
        return 1;
    }

    public List<Person> getList() {
        return new ArrayList<>(map.values());
    }

    public Optional<Person> getOne(final long id) {
        return Optional.ofNullable(map.get(id));
    }
}
