package javasnack.cli;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javasnack.tool.UnsignedByte;

import org.apache.commons.lang3.StringUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvBase64BinarySafeDataSetWriter;

public class CliDbUnitCsvExportDemo {

    /*
     * mvn exec:java -Dexec.mainClass=javasnack.cli.CliDbUnitCsvExportDemo -Dexec.classpathScope=test
     */

    public static void main(String[] args) throws Exception {
        String driver = System.getProperty("CliDbUnitCsvExportDemo.driver",
                "org.h2.Driver");
        Class.forName(driver);
        String url = System.getProperty("CliDbUnitCsvExportDemo.url",
                "jdbc:h2:mem:CliDbUnitCsvExportDemo");
        String dbUser = System.getProperty("CliDbUnitCsvExportDemo.dbUser",
                "sa");
        String dbPassword = System.getProperty(
                "CliDbUnitCsvExportDemo.dbPassword", "");
        Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);

        CliDbUnitCsvExportDemo demo = new CliDbUnitCsvExportDemo();
        demo.setup(conn);

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Calendar c = Calendar.getInstance();
        File outDir = new File(sdf1.format(c.getTime()));
        outDir.mkdir();

        IDatabaseConnection dbunit_conn = new DatabaseConnection(conn);
        IDataSet dataSet = dbunit_conn.createDataSet();

        CsvBase64BinarySafeDataSetWriter.write(dataSet, outDir);

        conn.close();
    }

    void setup(Connection conn) throws Exception {

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
        new T2(UnsignedByte.create0x00to0xFFString(),
                UnsignedByte.create0x00to0xFF(),
                UnsignedByte.create0x00to0xFF()).insertMe(conn);
    }

    class T1 {
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

        public T1() {
            super();
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
    }

    class T2 {
        public T2(String stringField, byte[] byteArrayField, byte[] blobField) {
            super();
            this.stringField = stringField;
            this.byteArrayField = byteArrayField;
            this.blobField = blobField;
        }

        public T2() {
            super();
        }

        long id;
        String stringField;
        byte[] byteArrayField;
        byte[] blobField;

        void setup(Connection targetDbConn) throws SQLException {
            PreparedStatement ps = targetDbConn
                    .prepareStatement("create table t2(id identity primary key, varchar_c varchar, binary_c binary, blob_c blob, null_c varchar)");
            ps.execute();
            ps.close();
        }

        int insertMe(Connection targetDbConn) throws SQLException {
            PreparedStatement ps = targetDbConn
                    .prepareStatement("insert into t2(varchar_c, binary_c, blob_c) values (?, ?, ?)");
            ps.setString(1, this.stringField);
            ps.setBytes(2, this.byteArrayField);
            ps.setBinaryStream(3, new ByteArrayInputStream(this.blobField),
                    this.blobField.length);
            int r = ps.executeUpdate();
            ps.close();
            return r;
        }
    }

}
