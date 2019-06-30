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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javasnack.tool.FileDirHelper;
import javasnack.tool.UnsignedByte;

public class DbUnitBasicUsageTest {

    /**
     * simple T1 table object
     */
    class T1 {
        @Override
        public String toString() {
            return "T1 [id=" + id + ", booleanField=" + booleanField
                    + ", intField=" + intField + ", decimalField="
                    + decimalField + ", doubleField=" + doubleField
                    + ", timeField=" + timeField + ", dateField=" + dateField
                    + ", timestampField=" + timestampField + ", stringField="
                    + stringField + "]";
        }

        public T1() {
            super();
        }

        public T1(boolean booleanField, int intField, BigDecimal decimalField,
                double doubleField, Time timeField, Date dateField,
                Timestamp timestampField, String stringField) {
            super();
            this.booleanField = booleanField;
            this.intField = intField;
            this.decimalField = decimalField;
            this.doubleField = doubleField;
            this.timeField = timeField;
            this.dateField = dateField;
            this.timestampField = timestampField;
            this.stringField = stringField;
        }

        long id;
        boolean booleanField;
        int intField;
        BigDecimal decimalField;
        double doubleField;
        java.sql.Time timeField;
        java.sql.Date dateField;
        java.sql.Timestamp timestampField;
        String stringField;

        void setup(Connection targetDbConn) throws SQLException {
            PreparedStatement ps = targetDbConn
                    .prepareStatement("create table t1("
                            + StringUtils.join(new String[] {
                                    "id identity primary key ",
                                    "boolean_c boolean", "int_c int",
                                    "decimal_c decimal", "double_c double",
                                    "time_c time", "date_c date",
                                    "timestamp_c timestamp",
                                    "varchar_c varchar" }, ", ")
                            + ")");
            ps.execute();
            ps.close();
        }

        int insertMe(Connection targetDbConn) throws SQLException {
            PreparedStatement ps = targetDbConn
                    .prepareStatement("insert into t1("
                            + "boolean_c, int_c, decimal_c, double_c, time_c, date_c, timestamp_c, varchar_c"
                            + ") values(?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setBoolean(1, this.booleanField);
            ps.setInt(2, this.intField);
            ps.setBigDecimal(3, this.decimalField);
            ps.setDouble(4, this.doubleField);
            ps.setTime(5, this.timeField);
            ps.setDate(6, this.dateField);
            ps.setTimestamp(7, this.timestampField);
            ps.setString(8, this.stringField);
            int r = ps.executeUpdate();
            ps.close();
            return r;
        }

        public Set<T1> findAll(Connection targetDbConn) throws SQLException {
            Set<T1> r = new HashSet<T1>();
            PreparedStatement ps = targetDbConn
                    .prepareStatement(
                            "select id, boolean_c, int_c, decimal_c, double_c, time_c, date_c, timestamp_c, varchar_c from t1");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                T1 c = new T1();
                c.id = rs.getLong(1);
                c.booleanField = rs.getBoolean(2);
                c.intField = rs.getInt(3);
                c.decimalField = rs.getBigDecimal(4);
                c.doubleField = rs.getDouble(5);
                c.timeField = rs.getTime(6);
                c.dateField = rs.getDate(7);
                c.timestampField = rs.getTimestamp(8);
                c.stringField = rs.getString(9);
                r.add(c);
            }
            rs.close();
            ps.close();
            return r;
        }
    }

    /**
     * simple T2 table object
     */
    class T2 {
        public T2() {
            super();
        }

        public T2(int age, String name) {
            super();
            this.age = age;
            this.name = name;
        }

        @Override
        public String toString() {
            return "T2 [id=" + id + ", age=" + age + ", name=" + name + "]";
        }

        long id;
        int age;
        String name;

        void setup(Connection targetDbConn) throws SQLException {
            PreparedStatement ps = targetDbConn
                    .prepareStatement("create table t2(id identity primary key, age int, name varchar)");
            ps.execute();
            ps.close();
        }

        int insertMe(Connection targetDbConn) throws SQLException {
            PreparedStatement ps = targetDbConn
                    .prepareStatement("insert into t2(age, name) values (?, ?)");
            ps.setInt(1, this.age);
            ps.setString(2, this.name);
            int r = ps.executeUpdate();
            ps.close();
            return r;
        }

        public Set<T2> findAll(Connection targetDbConn) throws SQLException {
            Set<T2> r = new HashSet<T2>();
            PreparedStatement ps = targetDbConn
                    .prepareStatement("select id, age, name from t2");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                T2 c = new T2();
                c.id = rs.getLong(1);
                c.age = rs.getInt(2);
                c.name = rs.getString(3);
                r.add(c);
            }
            rs.close();
            ps.close();
            return r;
        }
    }

    /**
     * simple T3 table object
     */
    class T3 {
        public T3() {
            super();
        }

        public T3(String label) {
            super();
            this.label = label;
        }

        @Override
        public String toString() {
            return "T3 [id=" + id + ", label=" + label + "]";
        }

        long id;
        String label;

        void setup(Connection targetDbConn) throws SQLException {
            PreparedStatement ps = targetDbConn
                    .prepareStatement("create table t3(id identity primary key, label varchar)");
            ps.execute();
            ps.close();
        }

        int insertMe(Connection targetDbConn) throws SQLException {
            PreparedStatement ps = targetDbConn
                    .prepareStatement("insert into t3(label) values (?)");
            ps.setString(1, this.label);
            int r = ps.executeUpdate();
            ps.close();
            return r;
        }
    }

    /**
     * simple T4 table object
     */
    class T4 {
        @Override
        public String toString() {
            return "T4 [id=" + id + ", stringField=" + stringField
                    + ", byteArrayField=" + Arrays.toString(byteArrayField)
                    + ", blobField=" + Arrays.toString(blobField) + "]";
        }

        public T4(String stringField, byte[] byteArrayField, byte[] blobField) {
            super();
            this.stringField = stringField;
            this.byteArrayField = byteArrayField;
            this.blobField = blobField;
        }

        public T4() {
            super();
        }

        long id;
        String stringField;
        byte[] byteArrayField;
        byte[] blobField;

        void setup(Connection targetDbConn) throws SQLException {
            PreparedStatement ps = targetDbConn
                    .prepareStatement(
                            "create table t4(id identity primary key, varchar_c varchar, binary_c binary, blob_c blob, null_c varchar)");
            ps.execute();
            ps.close();
        }

        int insertMe(Connection targetDbConn) throws SQLException {
            PreparedStatement ps = targetDbConn
                    .prepareStatement("insert into t4(varchar_c, binary_c, blob_c) values (?, ?, ?)");
            ps.setString(1, this.stringField);
            ps.setBytes(2, this.byteArrayField);
            ps.setBinaryStream(3, new ByteArrayInputStream(this.blobField),
                    this.blobField.length);
            int r = ps.executeUpdate();
            ps.close();
            return r;
        }

        public Set<T4> findAll(Connection targetDbConn) throws SQLException,
                IOException {
            Set<T4> r = new HashSet<T4>();
            PreparedStatement ps = targetDbConn
                    .prepareStatement("select id, varchar_c, binary_c, blob_c from t4");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                T4 c = new T4();
                c.id = rs.getLong(1);
                c.stringField = rs.getString(2);
                c.byteArrayField = rs.getBytes(3);
                BufferedInputStream bis = new BufferedInputStream(
                        rs.getBinaryStream(4));
                byte[] recv = new byte[0x100];
                bis.read(recv);
                c.blobField = recv;
                r.add(c);
            }
            rs.close();
            ps.close();
            return r;
        }

    }

    Connection conn;
    File tmpDir;

    @BeforeEach
    public void prepareDb() throws Exception {
        tmpDir = FileDirHelper.createTmpDir();

        /* テストごとに異なる in-memory db を使うよう、nano秒 suffix でDBを分離
         * + dbunit内部の close で in-memory db がロストしないよう、DB_CLOSE_DELAYを調整
         * see: https://www.h2database.com/html/features.html#in_memory_databases
         */
        final String randomDbName = "testdb" + System.nanoTime();
        conn = DriverManager.getConnection("jdbc:h2:mem:" + randomDbName + ";DB_CLOSE_DELAY=-1", "sa", "");

        new T1().setup(conn);
        Calendar c = Calendar.getInstance();

        c.clear();
        c.set(2000, 11, 31, 23, 59, 59);
        long insertTIM = c.getTimeInMillis();
        new T1(true, 10, new BigDecimal(20), 30.0, new Time(insertTIM),
                new Date(insertTIM), new Timestamp(insertTIM),
                "'Hello', \"World\"!").insertMe(conn);

        c.clear();
        c.set(2001, 0, 1, 12, 30, 15);
        insertTIM = c.getTimeInMillis();
        new T1(false, 20, new BigDecimal(30), 40.0, new Time(insertTIM),
                new Date(insertTIM), new Timestamp(insertTIM),
                "'Hello', \n\"World\"!").insertMe(conn);

        new T2().setup(conn);
        new T2(10, "abc").insertMe(conn);
        new T2(20, "def").insertMe(conn);

        new T3().setup(conn);
        new T3("label100").insertMe(conn);
        new T3("label200").insertMe(conn);

        new T4().setup(conn);
        // 第一引数にはUnsignedByte.create0x00to0xFFString() を入れたいところだが、
        // varcharのXML export/importでそれをやるとXML処理のエラーとなるため断念。
        new T4("abc",
                UnsignedByte.create0x00to0xFF(),
                UnsignedByte.create0x00to0xFF()).insertMe(conn);
    }

    @AfterEach
    public void closeDb() throws SQLException, IOException {
        FileUtils.deleteDirectory(tmpDir);
        conn.close();
    }

    // see: https://qiita.com/sh-ogawa/items/6a96a9bc3195d7ed50f7
    public void setupBinarySafeColumnConfig(final IDataSet dataSet, final String tableName, final String columnName)
            throws DataSetException {
        final ITableMetaData meta = dataSet.getTableMetaData(tableName);
        final int colIndex = meta.getColumnIndex(columnName);
        final Column c = meta.getColumns()[colIndex];
        final Column newColumn = new Column(
                c.getColumnName(),
                DataType.BINARY,
                c.getSqlTypeName(),
                c.getNullable(),
                c.getDefaultValue(),
                c.getRemarks(),
                c.getAutoIncrement());
        meta.getColumns()[colIndex] = newColumn;
    }

    @Disabled("FlatXmlDataSetBuilderがファイルを閉じないため一時ディレクトリも削除できない問題が解決できないため、無効化中")
    @Test
    public void xmlExportImportCanHandleBinaryColumnsByBase64()
            throws IOException, DatabaseUnitException, SQLException {
        IDatabaseConnection dbunitConn = new DatabaseConnection(conn);

        // setup BINARY (base64 enc/dec) column adjustment
        final IDataSet entireDataSetAtT0 = dbunitConn.createDataSet();
        setupBinarySafeColumnConfig(entireDataSetAtT0, "t4", "binary_c");
        setupBinarySafeColumnConfig(entireDataSetAtT0, "t4", "blob_c");

        /* create in-memory snapshot for entire table contents (ITable) at just after db initialized.
         * NOTE: dbunit 2.6.0, CachedDataSet(IDataSet dataSet) does NOT copy ITables from dataSet (BUG???)
         * so we need to create ITable copy manually. 
         */
        final ITableIterator it = entireDataSetAtT0.iterator();
        final Map<String, ITable> tablesAtT0 = new HashMap<>();
        while (it.next()) {
            final ITable tbl = it.getTable();
            final String tableName = tbl.getTableMetaData().getTableName().toLowerCase();
            tablesAtT0.put(tableName, tbl);
        }

        // export entire tables into XML (binary columns should be encoded to base64)
        final String xmlFilename = this.tmpDir.getAbsolutePath() + "/full.xml";
        FlatXmlDataSet.write(entireDataSetAtT0, new FileOutputStream(xmlFilename));

        // clear & insert from exported XML
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        IDataSet flatXmlDataSet = builder.build(new File(xmlFilename));
        DatabaseOperation.CLEAN_INSERT.execute(dbunitConn, flatXmlDataSet);

        IDataSet actualDataSet = dbunitConn.createDataSet();
        Assertion.assertEquals(tablesAtT0.get("t1"), actualDataSet.getTable("t1"));
        Assertion.assertEquals(tablesAtT0.get("t2"), actualDataSet.getTable("t2"));
        Assertion.assertEquals(tablesAtT0.get("t3"), actualDataSet.getTable("t3"));

        // binary column data compare should be success.
        Assertion.assertEquals(tablesAtT0.get("t4"), actualDataSet.getTable("t4"));
    }

}
