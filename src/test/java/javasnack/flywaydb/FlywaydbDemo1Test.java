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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.AbstractKeyedHandler;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.api.MigrationState;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.configuration.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FlywaydbDemo1Test {

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
        conn = DriverManager.getConnection("jdbc:h2:mem:flywaydb_demo1", "sa",
                "");
        // create test table.
        PreparedStatement ps = conn
                .prepareStatement("create table t1(id integer auto_increment primary key, name varchar, age int)");
        ps.execute();
        ps.close();
    }

    @AfterEach
    public void closeDb() throws SQLException {
        conn.close();
    }

    @Test
    public void typicalMigrationFlow() throws Exception {
        // standard fluent api based configuration
        final Flyway flyway0 = Flyway.configure()
                .dataSource("jdbc:h2:mem:flywaydb_demo1;DB_CLOSE_DELAY=-1", "sa", "")
                .locations("flywaydbdemos/demo1")
                .load();
        // setup baseline structure (create schema history table if not exists)
        flyway0.baseline();

        final Configuration config0 = flyway0.getConfiguration();
        // schema is empty unless set explicitly.
        assertEquals(0, config0.getSchemas().length);

        MigrationInfoService mis = flyway0.info();
        assertEquals(4, mis.all().length);
        for (MigrationInfo mi : mis.all()) {
            System.out.println(mi.getVersion());
            System.out.println(mi.getDescription());
            System.out.println(mi.getState());
            System.out.println(mi.getType());

        }
        // 3 migrations (V1.1, V1.2, V1.3) must be pending status.
        assertEquals(3, mis.pending().length);
        // initialized version (V1) applied.
        assertEquals(1, mis.applied().length);
        // current version is initialized version (V1).
        MigrationInfo mi = mis.current();
        assertEquals("1", mi.getVersion().getVersion());
        assertEquals(config0.getBaselineDescription(), mi.getDescription());
        assertEquals(MigrationState.BASELINE, mi.getState());

        // migrate to V1.2
        final Flyway flyway1 = Flyway.configure()
                .configuration(flyway0.getConfiguration())
                .target(MigrationVersion.fromVersion("1.2"))
                .load();
        flyway1.migrate();
        mis = flyway1.info();
        assertEquals(4, mis.all().length);
        // no pending, V1.3 -> "ABOVE_TARGET".
        assertEquals(0, mis.pending().length);
        // V1, V1.1, V1.2 were applied.
        assertEquals(3, mis.applied().length);
        for (MigrationInfo _mi : mis.all()) {
            System.out.println(_mi.getVersion());
            System.out.println(_mi.getDescription());
            System.out.println(_mi.getState());
            System.out.println(_mi.getType());

        }
        mi = mis.current();
        assertEquals("1.2", mi.getVersion().getVersion());
        assertEquals("add t1 hobby column", mi.getDescription());
        assertEquals(MigrationState.SUCCESS, mi.getState());

        // migrate dual (same configuration)
        flyway1.migrate();
        mis = flyway1.info();
        assertEquals(4, mis.all().length);
        // no pending, V1.3 -> "ABOVE_TARGET".
        assertEquals(0, mis.pending().length);
        // V1, V1.1, V1.2 were applied.
        assertEquals(3, mis.applied().length);
        for (MigrationInfo _mi : mis.all()) {
            System.out.println(_mi.getVersion());
            System.out.println(_mi.getDescription());
            System.out.println(_mi.getState());
            System.out.println(_mi.getType());

        }
        // => no side effect (nothing changed)
        mi = mis.current();
        assertEquals("1.2", mi.getVersion().getVersion());
        assertEquals("add t1 hobby column", mi.getDescription());
        assertEquals(MigrationState.SUCCESS, mi.getState());

        // migrate to latest version
        final Flyway flyway2 = Flyway.configure()
                .configuration(flyway0.getConfiguration())
                .target(MigrationVersion.LATEST)
                .load();
        flyway2.migrate();
        mis = flyway2.info();
        assertEquals(4, mis.all().length);
        assertEquals(0, mis.pending().length);
        assertEquals(4, mis.applied().length);
        mi = mis.current();
        assertEquals("1.3", mi.getVersion().getVersion());
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
                "select id, name, age, hobby from t1", h);
        assertEquals(4, found.size());
        T1 jon = found.get(1L);
        assertEquals("jon", jon.name);
        assertEquals("", jon.hobby);
        T1 alice = found.get(3L);
        assertEquals("alice", alice.name);
        assertEquals("swimming", alice.hobby);
    }
}
