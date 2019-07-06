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

package make.findbugs.angry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DummyBuggy1 {

    private void dangerSqlCalls(String sql, String p1) throws Exception {
        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test1",
                "sa", "");

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(0, 100); // MISS!! must be from 1.

        Statement stmt = conn.createStatement();

        // DANGER!!
        ResultSet rs1 = stmt.executeQuery(sql);

        // DANGER!!
        ResultSet rs2 = stmt.executeQuery("select abc from def where id = '"
                + p1 + "'");
    }

}
