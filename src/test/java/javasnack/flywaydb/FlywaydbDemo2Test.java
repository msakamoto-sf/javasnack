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

import static org.testng.Assert.*;

import java.util.Properties;

import org.testng.annotations.Test;

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
        assertEquals(flyway.getSchemas().length, 0);

        // to avoid initial version conflict, specify 0 as initial version.
        flyway.setInitVersion(MigrationVersion.fromVersion("0"));
        flyway.setInitDescription("(init flyway)");
        flyway.init();
        assertEquals(flyway.getSchemas().length, 1);
        assertEquals(flyway.getSchemas()[0], "PUBLIC");
        assertEquals(flyway.getTable(), "schema_version");

        MigrationInfoService mis = flyway.info();
        assertEquals(mis.all().length, 4);
        for (MigrationInfo mi : mis.all()) {
            System.out.println(mi.getVersion());
            System.out.println(mi.getDescription());
            System.out.println(mi.getState());
            System.out.println(mi.getType());

        }
        assertEquals(mis.pending().length, 3);
        assertEquals(mis.applied().length, 1);
        MigrationInfo mi = mis.current();
        assertEquals(mi.getVersion().getVersion(), "0");
        assertEquals(mi.getDescription(), "(init flyway)");
        assertEquals(mi.getState(), MigrationState.SUCCESS);

        // migrate to V002
        flyway.setTarget(MigrationVersion.fromVersion("2"));
        flyway.migrate();
        mis = flyway.info();
        assertEquals(mis.all().length, 4);
        assertEquals(mis.pending().length, 0);
        assertEquals(mis.applied().length, 3);
        mi = mis.current();
        assertEquals(mi.getVersion().getVersion(), "002");
        assertEquals(mi.getDescription(), "add telephone ok");
        assertEquals(mi.getState(), MigrationState.SUCCESS);

        flyway.setTarget(MigrationVersion.LATEST);
        try {
            flyway.migrate();
            fail("V3 must be error");
        } catch (FlywaySqlScriptException expected) {
            // ignore detailed exception assertions.
        }
        mis = flyway.info();
        assertEquals(mis.all().length, 4);
        assertEquals(mis.pending().length, 0);
        assertEquals(mis.applied().length, 4);
        mi = mis.current();
        // V003 status is FAILED
        assertEquals(mi.getVersion().getVersion(), "003");
        assertEquals(mi.getDescription(), "add telephone ng");
        assertEquals(mi.getState(), MigrationState.FAILED);

        flyway.repair();
        mis = flyway.info();
        // after repair, V003 status is rollbacked, pending.
        assertEquals(mis.all().length, 4);
        assertEquals(mis.pending().length, 1);
        assertEquals(mis.applied().length, 3);
        mi = mis.current();
        // now, current version is rollbacked to V002.
        assertEquals(mi.getVersion().getVersion(), "002");
        assertEquals(mi.getDescription(), "add telephone ok");
        assertEquals(mi.getState(), MigrationState.SUCCESS);
    }

}
