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
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Properties;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.api.MigrationState;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.Test;

public class FlywaydbDemo2Test {

    @Test
    public void migrateFailThenRepairFlow() throws Exception {
        // Property based setup
        // see: https://flywaydb.org/documentation/configfiles
        Properties properties = new Properties();
        properties.setProperty("flyway.user", "sa");
        properties.setProperty("flyway.password", "");
        properties.setProperty("flyway.url",
                "jdbc:h2:mem:flywaydb_demo2;DB_CLOSE_DELAY=-1");
        properties.setProperty("flyway.driver", "org.h2.Driver");

        final Flyway flyway0 = Flyway.configure()
                .configuration(properties)
                .locations("flywaydbdemos/demo2")
                // to avoid initial version conflict, specify 0 as initial version.
                .baselineVersion(MigrationVersion.fromVersion("0"))
                .baselineDescription("(init flyway)")
                .load();
        // setup baseline structure (create schema history table if not exists)
        flyway0.baseline();

        MigrationInfoService mis = flyway0.info();
        assertEquals(4, mis.all().length);
        for (MigrationInfo mi : mis.all()) {
            System.out.println(mi.getVersion());
            System.out.println(mi.getDescription());
            System.out.println(mi.getState());
            System.out.println(mi.getType());

        }
        assertEquals(3, mis.pending().length);
        assertEquals(1, mis.applied().length);
        MigrationInfo mi = mis.current();
        assertEquals("0", mi.getVersion().getVersion());
        assertEquals("(init flyway)", mi.getDescription());
        assertEquals(MigrationState.BASELINE, mi.getState());

        // migrate to V002
        final Flyway flyway1 = Flyway.configure()
                .configuration(flyway0.getConfiguration())
                .target(MigrationVersion.fromVersion("2"))
                .load();
        flyway1.migrate();
        mis = flyway1.info();
        assertEquals(4, mis.all().length);
        assertEquals(0, mis.pending().length);
        assertEquals(3, mis.applied().length);
        mi = mis.current();
        assertEquals("002", mi.getVersion().getVersion());
        assertEquals("add telephone ok", mi.getDescription());
        assertEquals(MigrationState.SUCCESS, mi.getState());

        // migrate to V003
        final Flyway flyway2 = Flyway.configure()
                .configuration(flyway0.getConfiguration())
                .target(MigrationVersion.LATEST)
                .load();
        try {
            flyway2.migrate();
            fail("V3 must be error");
        } catch (FlywayException expected) {
            // ignore detailed exception assertions.
        }
        mis = flyway2.info();
        assertEquals(4, mis.all().length);
        assertEquals(0, mis.pending().length);
        assertEquals(4, mis.applied().length);
        mi = mis.current();
        // V003 status is FAILED
        assertEquals("003", mi.getVersion().getVersion());
        assertEquals("add telephone ng", mi.getDescription());
        assertEquals(MigrationState.FAILED, mi.getState());

        flyway2.repair();
        mis = flyway2.info();
        // after repair, V003 status is rollbacked, pending.
        assertEquals(4, mis.all().length);
        assertEquals(1, mis.pending().length);
        assertEquals(3, mis.applied().length);
        mi = mis.current();
        // now, current version is rollbacked to V002.
        assertEquals("002", mi.getVersion().getVersion());
        assertEquals("add telephone ok", mi.getDescription());
        assertEquals(MigrationState.SUCCESS, mi.getState());
    }

}
