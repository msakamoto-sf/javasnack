/*
 * Copyright 2013 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
package javasnack.h2;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javasnack.tool.UnsignedByte;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class H2UsageDemo1Test {

    Connection conn;

    @BeforeTest
    public void prepareDb() throws Exception {
        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection("jdbc:h2:mem:test1", "sa", "");
        PreparedStatement ps = conn
                .prepareStatement("create table t1(id integer auto_increment primary key, name varchar, age int)");
        ps.execute();
        ps = conn
                .prepareStatement("create table t2(id integer auto_increment primary key, name varchar, data clob)");
        ps.execute();
        ps = conn
                .prepareStatement("create table t3(id integer auto_increment primary key, name varchar, data blob)");
        ps.execute();
        ps.close();
    }

    @AfterTest
    public void closeDb() throws SQLException {
        conn.close();
    }

    @Test
    public void basicInsertAndSelect() throws Exception {
        PreparedStatement ps = conn
                .prepareStatement("insert into t1(name, age) values(?, ?)");

        // basic insertion(1)
        ps.setString(1, "tom");
        ps.setInt(2, 30);
        int r = ps.executeUpdate();
        Assert.assertEquals(r, 1);

        // basic insertion(2)
        ps.setString(1, "nancy");
        ps.setInt(2, 40);
        r = ps.executeUpdate();
        Assert.assertEquals(r, 1);

        ps = conn.prepareStatement("select name, age from t1 where age > ?");
        // basic select query
        ps.setInt(1, 35);
        ResultSet rs = ps.executeQuery();
        rs.first();
        Assert.assertEquals(rs.getString("name"), "nancy");
        Assert.assertEquals(rs.getInt("age"), 40);
        rs.close();
        ps.close();
    }

    @Test
    public void insertAndSelect0x00to0xFFString() throws Exception {
        PreparedStatement ps = conn
                .prepareStatement("insert into t1(name, age) values(?, ?)");

        String s = UnsignedByte.create0x00to0xFFString();

        // insert Latin-1 0x00 to 0xFF String
        ps.setString(1, s);
        ps.setInt(2, 10);
        int r = ps.executeUpdate();
        Assert.assertEquals(r, 1);

        // select Latin-1 String
        ps = conn.prepareStatement("select name, age from t1 where age = ?");
        ps.setInt(1, 10);
        ResultSet rs = ps.executeQuery();
        rs.first();
        Assert.assertEquals(rs.getString("name"), s);
        rs.close();
        ps.close();
    }

    @Test
    public void insertAndSelect0x00to0xFFClob() throws Exception {
        PreparedStatement ps = conn
                .prepareStatement("insert into t2(name, data) values(?, ?)");
        ps.setString(1, "clob1");

        byte[] src = UnsignedByte.create0x00to0xFF();
        // insert clob data
        ByteArrayInputStream bais = new ByteArrayInputStream(src);
        ps.setAsciiStream(2, bais, 0x100);
        int r = ps.executeUpdate();
        Assert.assertEquals(r, 1);

        // select clob data
        ps = conn.prepareStatement("select data from t2 where name = ?");
        ps.setString(1, "clob1");
        ResultSet rs = ps.executeQuery();
        rs.first();
        BufferedInputStream bis = new BufferedInputStream(
                rs.getAsciiStream("data"));
        byte[] recv = new byte[0x100];
        bis.read(recv);
        // for (byte d : recv) {
        // System.out.println(d);
        // }
        // OOPS!! : recv[128]=-17, recv[129]=-65, recv[130]=-67,... what happen???
        Assert.assertNotEquals(recv, src);
        rs.close();
        ps.close();
    }

    @Test
    public void insertAndSelect0x00to0xFFBlob() throws Exception {
        PreparedStatement ps = conn
                .prepareStatement("insert into t3(name, data) values(?, ?)");
        ps.setString(1, "blob1");

        byte[] src = UnsignedByte.create0x00to0xFF();
        // insert blob data
        ByteArrayInputStream bais = new ByteArrayInputStream(src);
        ps.setBinaryStream(2, bais, 0x100);
        int r = ps.executeUpdate();
        Assert.assertEquals(r, 1);

        // select blob data
        ps = conn.prepareStatement("select data from t3 where name = ?");
        ps.setString(1, "blob1");
        ResultSet rs = ps.executeQuery();
        rs.first();
        BufferedInputStream bis = new BufferedInputStream(
                rs.getBinaryStream("data"));
        byte[] recv = new byte[0x100];
        bis.read(recv);
        Assert.assertEquals(recv, src);
        rs.close();
        ps.close();
    }
}
