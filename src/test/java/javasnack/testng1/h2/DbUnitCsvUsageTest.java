package javasnack.testng1.h2;

import static org.testng.Assert.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
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
import java.util.HashSet;
import java.util.Set;

import javasnack.tool.FileDirHelper;
import javasnack.tool.UnsignedByte;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvBase64BinarySafeDataSet;
import org.dbunit.dataset.csv.CsvBase64BinarySafeDataSetWriter;
import org.dbunit.dataset.csv.CsvDataSet;
import org.dbunit.dataset.csv.CsvDataSetWriter;
import org.dbunit.operation.DatabaseOperation;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class DbUnitCsvUsageTest {

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
                                    "varchar_c varchar" }, ", ") + ")");
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
                    .prepareStatement("select id, boolean_c, int_c, decimal_c, double_c, time_c, date_c, timestamp_c, varchar_c from t1");
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
                    .prepareStatement("create table t4(id identity primary key, varchar_c varchar, binary_c binary, blob_c blob, null_c varchar)");
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

    @BeforeTest
    public void prepareDb() throws Exception {
        tmpDir = FileDirHelper.createTmpDir();

        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection("jdbc:h2:mem:test2", "sa", "");

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
        new T4(UnsignedByte.create0x00to0xFFString(),
                UnsignedByte.create0x00to0xFF(),
                UnsignedByte.create0x00to0xFF()).insertMe(conn);
    }

    @AfterTest
    public void closeDb() throws SQLException, IOException {
        FileUtils.deleteDirectory(tmpDir);
        conn.close();
    }

    @Test
    public void safelyCsvExportAndImportStandardColumnTables()
            throws IOException, DatabaseUnitException, SQLException {
        IDatabaseConnection dbunit_conn = new DatabaseConnection(conn);

        // cache(save) expected data snap-shot.
        IDataSet expectedDataSet = new CachedDataSet(
                dbunit_conn.createDataSet());

        // export only t1, t2 table to CSV
        QueryDataSet exportDataSet = new QueryDataSet(dbunit_conn);
        exportDataSet.addTable("t1");
        exportDataSet.addTable("t2");
        CsvDataSetWriter.write(exportDataSet, tmpDir);

        // after export, insert new record to t3 (not exported)
        new T3("new label").insertMe(conn);

        // clear & insert from exported CSV
        IDataSet csvDataSet = new CsvDataSet(tmpDir);
        DatabaseOperation.CLEAN_INSERT.execute(dbunit_conn, csvDataSet);

        IDataSet actualDataSet = dbunit_conn.createDataSet();
        Assertion.assertEquals(expectedDataSet.getTable("t1"),
                actualDataSet.getTable("t1"));
        Assertion.assertEquals(expectedDataSet.getTable("t2"),
                actualDataSet.getTable("t2"));
        // this produce assertion error : table record will be 3.
        // Assertion.assertEquals(expectedDataSet.getTable("t3"),
        // actualDataSet.getTable("t3"));
        assertEquals(3, actualDataSet.getTable("t3").getRowCount());
    }

    @Test
    public void customExportAndImportForControlCodeStrings()
            throws IOException, DatabaseUnitException, SQLException {
        IDatabaseConnection dbunit_conn = new DatabaseConnection(conn);

        // cache(save) expected data snap-shot.
        IDataSet expectedDataSet = new CachedDataSet(
                dbunit_conn.createDataSet());

        // H2DB can store and load binary string to varchar column safely.
        Set<T4> a = new T4().findAll(conn);
        for (T4 a2 : a) {
            assertEquals(a2.stringField, UnsignedByte.create0x00to0xFFString());
        }

        // export t4 using base64 binary safely csv dataset writer.
        QueryDataSet exportDataSet = new QueryDataSet(dbunit_conn);
        exportDataSet.addTable("t4");
        CsvBase64BinarySafeDataSetWriter.write(exportDataSet, tmpDir);

        // clear & insert from exported CSV using base64 binary safely csv data producer.
        IDataSet csvDataSet = new CsvBase64BinarySafeDataSet(tmpDir);
        DatabaseOperation.CLEAN_INSERT.execute(dbunit_conn, csvDataSet);

        IDataSet actualDataSet = dbunit_conn.createDataSet();
        Assertion.assertEquals(expectedDataSet.getTable("t4"),
                actualDataSet.getTable("t4"));
    }
}
