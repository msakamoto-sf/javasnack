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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.api.MigrationInfo;
import com.googlecode.flyway.core.api.MigrationInfoService;
import com.googlecode.flyway.core.api.MigrationState;
import com.googlecode.flyway.core.api.MigrationVersion;
import com.googlecode.flyway.core.command.FlywaySqlScriptException;

public class FlywaydbDemo2Test {

    @Test
    public void migrateFailThenRepairFlow() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("flyway.user", "sa");
        properties.setProperty("flyway.password", "");
        properties.setProperty("flyway.url",
                "jdbc:h2:mem:flywaydb_demo2;DB_CLOSE_DELAY=-1");
        properties.setProperty("flyway.driver", "org.h2.Driver");

        final Flyway flyway = new Flyway();
        flyway.configure(properties);
        assertNotNull(flyway.getDataSource());
        flyway.setLocations("flywaydbdemos/demo2");
        assertEquals(0, flyway.getSchemas().length);

        // to avoid initial version conflict, specify 0 as initial version.
        flyway.setInitVersion(MigrationVersion.fromVersion("0"));
        flyway.setInitDescription("(init flyway)");
        flyway.init();
        assertEquals(1, flyway.getSchemas().length);
        assertEquals("PUBLIC", flyway.getSchemas()[0]);
        assertEquals("schema_version", flyway.getTable());

        MigrationInfoService mis = flyway.info();
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
        assertEquals(MigrationState.SUCCESS, mi.getState());

        // migrate to V002
        flyway.setTarget(MigrationVersion.fromVersion("2"));
        flyway.migrate();
        mis = flyway.info();
        assertEquals(4, mis.all().length);
        assertEquals(0, mis.pending().length);
        assertEquals(3, mis.applied().length);
        mi = mis.current();
        assertEquals("002", mi.getVersion().getVersion());
        assertEquals("add telephone ok", mi.getDescription());
        assertEquals(MigrationState.SUCCESS, mi.getState());

        flyway.setTarget(MigrationVersion.LATEST);
        try {
            flyway.migrate();
            fail("V3 must be error");
        } catch (FlywaySqlScriptException expected) {
            // ignore detailed exception assertions.
        }
        mis = flyway.info();
        assertEquals(4, mis.all().length);
        assertEquals(0, mis.pending().length);
        assertEquals(4, mis.applied().length);
        mi = mis.current();
        // V003 status is FAILED
        assertEquals("003", mi.getVersion().getVersion());
        assertEquals("add telephone ng", mi.getDescription());
        assertEquals(MigrationState.FAILED, mi.getState());

        flyway.repair();
        mis = flyway.info();
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
