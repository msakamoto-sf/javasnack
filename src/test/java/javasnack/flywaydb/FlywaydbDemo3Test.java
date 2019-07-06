/*
 * Copyright 2014 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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

package javasnack.flywaydb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.AbstractKeyedHandler;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.api.MigrationState;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FlywaydbDemo3Test {

    Connection conn;

    class T1 {
        public long id;
        public String name;
        public int age;
        public String hobby;
    }

    @BeforeEach
    public void prepareDb() throws Exception {
        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection("jdbc:h2:mem:flywaydb_demo3", "sa",
                "");
        // prepare empty database.
    }

    @AfterEach
    public void closeDb() throws SQLException {
        conn.close();
    }

    @Test
    public void usingPlaceholderAndEncoding() throws Exception {
        final Map<String, String> placeholders = new HashMap<>();
        placeholders.put("L1", "abc");
        placeholders.put("L2", "def");
        placeholders.put("L3", "abc");
        placeholders.put("L4", "def");
        final Flyway flyway0 = Flyway.configure()
                .dataSource("jdbc:h2:mem:flywaydb_demo3;DB_CLOSE_DELAY=-1", "sa", "")
                .locations("flywaydbdemos/demo3")
                .encoding(StandardCharsets.UTF_8)
                .placeholders(placeholders)
                .load();

        // before calling baseline() method
        MigrationInfoService mis = flyway0.info();
        assertEquals(3, mis.all().length);
        for (MigrationInfo mi : mis.all()) {
            System.out.println(mi.getVersion());
            System.out.println(mi.getDescription());
            System.out.println(mi.getState());
            System.out.println(mi.getType());
        }
        // 3 migrations (V1, V1.1, V1.2) must be pending status.
        assertEquals(3, mis.pending().length);
        // no version applied.
        assertEquals(0, mis.applied().length);
        // no current version.
        MigrationInfo mi = mis.current();
        assertNull(mi);

        // migrate to V1.1
        final Flyway flyway1 = Flyway.configure()
                .configuration(flyway0.getConfiguration())
                .target(MigrationVersion.fromVersion("1.1"))
                .load();
        flyway1.migrate();
        mis = flyway1.info();
        assertEquals(3, mis.all().length);
        // no pending, V1.2 -> "ABOVE_TARGET".
        assertEquals(0, mis.pending().length);
        // V1, V1.1, V1.2 were applied.
        assertEquals(2, mis.applied().length);
        for (MigrationInfo _mi : mis.all()) {
            System.out.println(_mi.getVersion());
            System.out.println(_mi.getDescription());
            System.out.println(_mi.getState());
            System.out.println(_mi.getType());

        }
        mi = mis.current();
        assertEquals("1.1", mi.getVersion().getVersion());
        assertEquals("add t1 hobby column", mi.getDescription());
        assertEquals(MigrationState.SUCCESS, mi.getState());

        final Flyway flyway2 = Flyway.configure()
                .configuration(flyway0.getConfiguration())
                // change placeholder prefix and suffix.
                .placeholderPrefix("%{%")
                .placeholderSuffix("%}%")
                // migrate to latest version
                .target(MigrationVersion.LATEST)
                .load();
        flyway2.migrate();
        mis = flyway2.info();
        assertEquals(3, mis.all().length);
        assertEquals(0, mis.pending().length);
        assertEquals(3, mis.applied().length);
        mi = mis.current();
        assertEquals("1.2", mi.getVersion().getVersion());
        assertEquals("insert t1 data2", mi.getDescription());
        assertEquals(MigrationState.SUCCESS, mi.getState());

        // select and validate records.
        QueryRunner run = new QueryRunner();
        ResultSetHandler<Map<Long, T1>> h = new AbstractKeyedHandler<Long, T1>() {
            @Override
            protected T1 createRow(ResultSet rs) throws SQLException {
                T1 row = new T1();
                row.id = rs.getLong("id");
                row.name = rs.getString("name");
                row.age = rs.getInt("age");
                row.hobby = rs.getString("hobby");
                return row;
            }

            @Override
            protected Long createKey(ResultSet rs) throws SQLException {
                return rs.getLong("id");
            }
        };
        Map<Long, T1> found = run.query(conn,
                "select id, name, age, hobby from abc_def_t1", h);
        assertEquals(4, found.size());
        T1 jon = found.get(1L);
        assertEquals("日本語", jon.name);
        assertEquals("", jon.hobby);
        T1 alice = found.get(3L);
        assertEquals("alice", alice.name);
        assertEquals("swimming", alice.hobby);
    }
}
