/*
 * Copyright 2013 the original author or authors.
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
package javasnack.testng1.flywaydb;

import static org.testng.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.AbstractKeyedHandler;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.api.MigrationInfo;
import com.googlecode.flyway.core.api.MigrationInfoService;
import com.googlecode.flyway.core.api.MigrationState;
import com.googlecode.flyway.core.api.MigrationVersion;

public class FlywaydbDemo3Test {

    Connection conn;

    class T1 {
        public long id;
        public String name;
        public int age;
        public String hobby;
    }

    @BeforeTest
    public void prepareDb() throws Exception {
        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection("jdbc:h2:mem:flywaydb_demo3", "sa",
                "");
        // prepare empty database.
    }

    @AfterTest
    public void closeDb() throws SQLException {
        conn.close();
    }

    @Test
    public void usingPlaceholderAndEncoding() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("flyway.user", "sa");
        properties.setProperty("flyway.password", "");
        properties.setProperty("flyway.url",
                "jdbc:h2:mem:flywaydb_demo3;DB_CLOSE_DELAY=-1");
        properties.setProperty("flyway.driver", "org.h2.Driver");

        final Flyway flyway = new Flyway();
        flyway.configure(properties);
        flyway.setLocations("flywaydbdemos/demo3");

        flyway.setEncoding("UTF-8");
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("L1", "abc");
        placeholders.put("L2", "def");
        placeholders.put("L3", "abc");
        placeholders.put("L4", "def");
        flyway.setPlaceholders(placeholders);

        MigrationInfoService mis = flyway.info();
        assertEquals(mis.all().length, 3);
        for (MigrationInfo mi : mis.all()) {
            System.out.println(mi.getVersion());
            System.out.println(mi.getDescription());
            System.out.println(mi.getState());
            System.out.println(mi.getType());

        }
        // 3 migrations (V1, V1.1, V1.2) must be pending status.
        assertEquals(mis.pending().length, 3);
        // no version applied.
        assertEquals(mis.applied().length, 0);
        // no current version.
        MigrationInfo mi = mis.current();
        assertNull(mi);

        // migrate to V1.1
        flyway.setTarget(MigrationVersion.fromVersion("1.1"));
        flyway.migrate();
        mis = flyway.info();
        assertEquals(mis.all().length, 3);
        // no pending, V1.2 -> "ABOVE_TARGET".
        assertEquals(mis.pending().length, 0);
        // V1, V1.1, V1.2 were applied.
        assertEquals(mis.applied().length, 2);
        for (MigrationInfo _mi : mis.all()) {
            System.out.println(_mi.getVersion());
            System.out.println(_mi.getDescription());
            System.out.println(_mi.getState());
            System.out.println(_mi.getType());

        }
        mi = mis.current();
        assertEquals(mi.getVersion().getVersion(), "1.1");
        assertEquals(mi.getDescription(), "add t1 hobby column");
        assertEquals(mi.getState(), MigrationState.SUCCESS);

        // change placeholder prefix and suffix.
        flyway.setPlaceholderPrefix("%{%");
        flyway.setPlaceholderSuffix("%}%");

        // migrate to latest version
        flyway.setTarget(MigrationVersion.LATEST);
        flyway.migrate();
        mis = flyway.info();
        assertEquals(mis.all().length, 3);
        assertEquals(mis.pending().length, 0);
        assertEquals(mis.applied().length, 3);
        mi = mis.current();
        assertEquals(mi.getVersion().getVersion(), "1.2");
        assertEquals(mi.getDescription(), "insert t1 data2");
        assertEquals(mi.getState(), MigrationState.SUCCESS);

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
        assertEquals(found.size(), 4);
        T1 jon = found.get(1L);
        assertEquals(jon.name, "日本語");
        assertEquals(jon.hobby, "");
        T1 alice = found.get(3L);
        assertEquals(alice.name, "alice");
        assertEquals(alice.hobby, "swimming");
    }
}
