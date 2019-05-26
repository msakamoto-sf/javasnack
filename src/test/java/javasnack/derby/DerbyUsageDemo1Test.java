/*
 * Copyright 2015 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
package javasnack.derby;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import javasnack.tool.UnsignedByte;

@TestInstance(Lifecycle.PER_CLASS)
public class DerbyUsageDemo1Test {

    Connection conn;

    @BeforeAll
    public void prepareDb() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        conn = DriverManager.getConnection("jdbc:derby:memory:db1;create=true");
        PreparedStatement ps = conn
                .prepareStatement(
                        "create table t1(id integer primary key generated always as identity (start with 1, increment by 1), name varchar(1024), age int)");
        ps.execute();
        ps = conn
                .prepareStatement(
                        "create table t2(id integer primary key generated always as identity (start with 1, increment by 1), name varchar(1024), data clob)");
        ps.execute();
        ps = conn
                .prepareStatement(
                        "create table t3(id integer primary key generated always as identity (start with 1, increment by 1), name varchar(1024), data blob)");
        ps.execute();
        ps.close();
    }

    @AfterAll
    public void closeAndShutdownDb() throws SQLException {
        conn.close();
        try {
            // Derby needs special shutdown call.
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException se) {
            if (((se.getErrorCode() == 50000) && ("XJ015".equals(se.getSQLState())))) {
                System.out.println("Derby shut down normally");
            } else {
                System.err.println("Derby did not shut down normally");
                se.printStackTrace();
            }
        }
    }

    @Test
    public void basicInsertAndSelect() throws Exception {
        PreparedStatement ps = conn.prepareStatement("insert into t1(name, age) values(?, ?)");

        // basic insertion(1)
        ps.setString(1, "tom");
        ps.setInt(2, 30);
        int r = ps.executeUpdate();
        assertEquals(1, r);

        // basic insertion(2)
        ps.setString(1, "nancy");
        ps.setInt(2, 40);
        r = ps.executeUpdate();
        assertEquals(1, r);

        ps = conn.prepareStatement("select name, age from t1 where age > ?");
        // basic select query
        ps.setInt(1, 35);
        ResultSet rs = ps.executeQuery();
        rs.next();
        assertEquals("nancy", rs.getString("name"));
        assertEquals(40, rs.getInt("age"));
        rs.close();
        ps.close();
    }

    @Test
    public void insertAndSelect0x00to0xFFString() throws Exception {
        PreparedStatement ps = conn.prepareStatement("insert into t1(name, age) values(?, ?)");

        String s = UnsignedByte.create0x00to0xFFString();

        // insert Latin-1 0x00 to 0xFF String
        ps.setString(1, s);
        ps.setInt(2, 10);
        int r = ps.executeUpdate();
        assertEquals(1, r);

        // select Latin-1 String
        ps = conn.prepareStatement("select name, age from t1 where age = ?");
        ps.setInt(1, 10);
        ResultSet rs = ps.executeQuery();
        rs.next();
        assertEquals(s, rs.getString("name"));
        rs.close();
        ps.close();
    }

    @Test
    public void insertAndSelect0x00to0xFFClob() throws Exception {
        PreparedStatement ps = conn.prepareStatement("insert into t2(name, data) values(?, ?)");
        ps.setString(1, "clob1");

        byte[] src = UnsignedByte.create0x00to0xFF();
        // insert clob data
        ByteArrayInputStream bais = new ByteArrayInputStream(src);
        ps.setAsciiStream(2, bais, 0x100);
        int r = ps.executeUpdate();
        assertEquals(1, r);

        // select clob data
        ps = conn.prepareStatement("select data from t2 where name = ?");
        ps.setString(1, "clob1");
        ResultSet rs = ps.executeQuery();
        rs.next();
        BufferedInputStream bis = new BufferedInputStream(rs.getAsciiStream("data"));
        byte[] recv = new byte[0x100];
        bis.read(recv);
        assertArrayEquals(src, recv);
        rs.close();
        ps.close();
    }

    @Test
    public void insertAndSelect0x00to0xFFBlob() throws Exception {
        PreparedStatement ps = conn.prepareStatement("insert into t3(name, data) values(?, ?)");
        ps.setString(1, "blob1");

        byte[] src = UnsignedByte.create0x00to0xFF();
        // insert blob data
        ByteArrayInputStream bais = new ByteArrayInputStream(src);
        ps.setBinaryStream(2, bais, 0x100);
        int r = ps.executeUpdate();
        assertEquals(1, r);

        // select blob data
        ps = conn.prepareStatement("select data from t3 where name = ?");
        ps.setString(1, "blob1");
        ResultSet rs = ps.executeQuery();
        rs.next();
        BufferedInputStream bis = new BufferedInputStream(rs.getBinaryStream("data"));
        byte[] recv = new byte[0x100];
        bis.read(recv);
        assertArrayEquals(src, recv);
        rs.close();
        ps.close();
    }
}
