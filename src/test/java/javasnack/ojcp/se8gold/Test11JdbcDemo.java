package javasnack.ojcp.se8gold;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Test11JdbcDemo {
    /* オラクル認定資格教科書 Javaプログラマ Gold SE 8
     * 11章, JDBC の内容とサンプルコード/SQL を写経
     */

    static final String CONN_URL = "jdbc:h2:mem:javase8golddemo";
    static final String CONN_USER = "sa";
    static final String CONN_PASS = "";

    /* h2db の in-memory db "jdbc:h2:mem:(dbname)" では、
     * 最後の Connection が close されるとデータが消える。
     * -> テストメソッドの実行毎に新規にDBをセットアップするとともに、
     * テストメソッド実行中も最低1つはConnectionを維持するようにしておく。
     * 
     * see: https://www.h2database.com/html/features.html#in_memory_databases
     * 
     * -> 本テストクラスについては、test method 単位の並行実行すると
     * 複数のテストメソッドが同時にアクセスすることにより結果が変わる可能性がある。
     */
    Connection retainingConnection;

    @BeforeEach
    public void prepareDb() throws SQLException {
        final String createTblDep = "CREATE TABLE department ("
                + "  dept_code INT NOT NULL,"
                + "  dept_name VARCHAR(20) NOT NULL,"
                + "  dept_address VARCHAR(40) NOT NULL,"
                + "  pilot_number VARCHAR(20) DEFAULT NULL,"
                + "  PRIMARY KEY  (dept_code)"
                + ")";
        final String createTblA = "CREATE TABLE mytableA ("
                + "  field1 DATE NOT NULL,"
                + "  field2 TIME NOT NULL,"
                + "  field3 TIMESTAMP NOT NULL"
                + ")";
        final String createTblB = "CREATE TABLE mytableB ("
                + "  field1 INT(10) NOT NULL,"
                + "  field2 INT(20) NOT NULL,"
                + "  field3 VARCHAR(20) NOT NULL"
                + ")";
        retainingConnection = DriverManager.getConnection(CONN_URL, CONN_USER, CONN_PASS);
        final Statement stmt = retainingConnection.createStatement();
        stmt.execute(createTblDep);
        stmt.executeUpdate("INSERT INTO department VALUES(1,'Sales','Tokyo', '03-3333-xxxx')");
        stmt.executeUpdate("INSERT INTO department VALUES(2,'Engineering','Yokohama', '045-444-xxxx')");
        stmt.executeUpdate("INSERT INTO department VALUES(3,'Development','Osaka', null)");
        stmt.executeUpdate("INSERT INTO department VALUES(4,'Marketing','Fukuoka', '092-222-xxxx')");
        stmt.executeUpdate("INSERT INTO department VALUES(5,'Education','Tokyo', null)");
        stmt.execute(createTblA);
        stmt.executeUpdate("INSERT INTO mytableA VALUES('2016-03-30', '12:40', '2016-03-30T12:40')");
        stmt.execute(createTblB);
    }

    @AfterEach
    public void closeDb() throws SQLException {
        retainingConnection.close();
    }

    @Test
    public void testBasicJdbcDemo() throws SQLException {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection(CONN_URL, CONN_USER, CONN_PASS);
            stmt = con.createStatement();
            String sql = "SELECT dept_code, dept_name FROM department ORDER BY dept_code ASC";
            rs = stmt.executeQuery(sql);
            assertThat(rs.next()).isTrue();
            // カラム番号は1始まり
            assertThat(rs.getInt(1)).isEqualTo(1);
            assertThat(rs.getString(2)).isEqualTo("Sales");

            assertThat(rs.next()).isTrue();
            // カラム名で指定することも可能
            assertThat(rs.getInt("dept_code")).isEqualTo(2);
            assertThat(rs.getString("dept_name")).isEqualTo("Engineering");

            // int型のカラムに対しては getString() も可能 
            assertThat(rs.getString("dept_code")).isEqualTo("2");

            // varchar型のカラムに対しての getInt() は SQLException
            final ResultSet rsx = rs;
            assertThatThrownBy(() -> {
                rsx.getInt("dept_name");
            }).isInstanceOf(SQLException.class);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("dept_code")).isEqualTo(3);
            assertThat(rs.getString("dept_name")).isEqualTo("Development");
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(4);
            assertThat(rs.getString(2)).isEqualTo("Marketing");
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(5);
            assertThat(rs.getString(2)).isEqualTo("Education");
            assertThat(rs.next()).isFalse();
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                throw e;
            }
        }
    }

    @FunctionalInterface
    interface ResultSetConsumer {
        void accept(final ResultSet rs) throws SQLException;
    }

    void query(final String query, final ResultSetConsumer consumer) throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONN_URL, CONN_USER, CONN_PASS);
                final Statement stmt = conn.createStatement();
                final ResultSet rs = stmt.executeQuery(query)) {
            consumer.accept(rs);
        }
    }

    @Test
    public void testJdbcDateTimeTypeDemo() throws SQLException {
        query("SELECT field1, field2, field3 FROM mytableA", (rs) -> {
            assertThat(rs.next()).isTrue();
            final java.sql.Date sqlDate = rs.getDate(1);
            assertThat(sqlDate.toLocalDate().toString()).isEqualTo("2016-03-30");

            final java.sql.Time sqlTime = rs.getTime(2);
            assertThat(sqlTime.toLocalTime().toString()).isEqualTo("12:40");

            final java.sql.Timestamp timestamp = rs.getTimestamp(3);
            assertThat(timestamp.toLocalDateTime().toString()).isEqualTo("2016-03-30T12:40");
        });
    }

    @Test
    public void testEmptyResultSetDemo() throws SQLException {
        query("SELECT dept_name FROM department WHERE dept_code = 99", (rs) -> {
            // executeQuery() はクエリ結果のレコードが0行のとき、nullは返さず空のResultSetを返す。
            assertThat(rs).isNotNull();
            assertThat(rs.next()).isFalse();

            // 空のResultSetに対して getXxx() を呼ぶと SQLException
            assertThatThrownBy(() -> {
                rs.getInt(1);
            }).isInstanceOf(SQLException.class);
            assertThatThrownBy(() -> {
                rs.getString(1);
            }).isInstanceOf(SQLException.class);
        });
    }

    @Test
    public void testErrorneousResultSetOperationDemo() throws SQLException {
        final String query = "SELECT dept_code, dept_name FROM department ORDER BY dept_code ASC";
        try (final Connection conn = DriverManager.getConnection(CONN_URL, CONN_USER, CONN_PASS);
                final Statement stmt = conn.createStatement();
                final ResultSet rs = stmt.executeQuery(query)) {

            assertThatThrownBy(() -> {
                rs.getInt(1);
            }).isInstanceOf(SQLException.class);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("dept_code")).isEqualTo(1);
            assertThat(rs.getString("dept_name")).isEqualTo("Sales");

            // int型のカラムに対しては getString() も可能 
            assertThat(rs.getString("dept_code")).isEqualTo("1");

            // varchar型のカラムに対しての getInt() は SQLException
            assertThatThrownBy(() -> {
                rs.getInt("dept_name");
            }).isInstanceOf(SQLException.class);

            final int r = stmt.executeUpdate("DELETE FROM department WHERE dept_code = 2");
            assertThat(r).isEqualTo(1);

            /* 同じ Statement 上で、query -> update の順で行うと、
             * 最初の query による ResultSet が自動的に close される。
             * そのため、update のあとで前の ResultSet を操作しようとすると SQLException
             * (もし同様のことをしたければ ResultSet.CONCUR_UPDATABLE を組み合わせる)
             */
            assertThatThrownBy(() -> {
                rs.next();
            }).isInstanceOf(SQLException.class);
        }
    }

    @FunctionalInterface
    interface StatementConsumer {
        void accept(final Statement stmt) throws SQLException;
    }

    void statement(final StatementConsumer consumer) throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONN_URL, CONN_USER, CONN_PASS);
                final Statement stmt = conn.createStatement()) {
            consumer.accept(stmt);
        }
    }

    @Test
    public void testInsertUpdateDeleteDemo() throws SQLException {
        // insert
        statement(stmt -> {
            int r = stmt.executeUpdate("INSERT INTO department VALUES (6,'Planning','Yokohama', '045-333-xxxx')");
            assertThat(r).isEqualTo(1);
        });

        // update : executeUpdate() returns updated row size
        statement(stmt -> {
            int r = stmt.executeUpdate(
                    "UPDATE department set dept_address='xxx', pilot_number='yyy' where dept_code = 6");
            assertThat(r).isEqualTo(1);
        });
        query("select * from department where dept_code = 6", rs -> {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("dept_address")).isEqualTo("xxx");
            assertThat(rs.getString("pilot_number")).isEqualTo("yyy");
        });
        statement(stmt -> {
            int r = stmt.executeUpdate(
                    "UPDATE department set dept_address='AAA', pilot_number='BBB' where dept_code = 99");
            assertThat(r).isEqualTo(0);
        });

        // delete : executeUpdate() returns deleted row size
        statement(stmt -> {
            int r = stmt.executeUpdate("DELETE FROM department where dept_code = 6");
            assertThat(r).isEqualTo(1);
        });
        query("select * from department where dept_code = 6", rs -> {
            assertThat(rs.next()).isFalse();
        });
        statement(stmt -> {
            int r = stmt.executeUpdate("DELETE FROM department where dept_code = 99");
            assertThat(r).isEqualTo(0);
        });
    }

    @Test
    public void testStatementExecuteDemo() throws SQLException {
        /* Statement.execute(sql) のデモ。
         * 戻り値がbooleanで、trueなら Statement.getResultSet() で ResultSet 取得。
         * (= executeQuery())
         * false なら Statement.getUpdateCount() で影響を受けた行数を取得。
         * (= executeUpdate())
         */
        statement(stmt -> {
            final boolean isResultSet = stmt
                    .execute("INSERT INTO department VALUES (6,'Planning','Yokohama', '045-333-xxxx')");
            assertThat(isResultSet).isFalse();
            assertThat(stmt.getUpdateCount()).isEqualTo(1);
            assertThat(stmt.getResultSet()).isNull(); // query でないときはnull
        });

        statement(stmt -> {
            final boolean isResultSet = stmt.execute("SELECT * FROM department WHERE dept_code = 6");
            assertThat(isResultSet).isTrue();
            assertThat(stmt.getUpdateCount()).isEqualTo(-1); // 更新処理でないときは -1
            final ResultSet rs = stmt.getResultSet();
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("dept_code")).isEqualTo(6);
            assertThat(rs.getString("dept_name")).isEqualTo("Planning");
        });
    }

    @Test
    public void testResultSetTypeConcurrencySupportDemo() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONN_URL, CONN_USER, CONN_PASS)) {
            DatabaseMetaData m = conn.getMetaData();

            // h2database : TYPE_SCROLL_SENSITIVE が未サポート

            assertThat(m.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY)).isTrue();
            assertThat(m.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE)).isTrue();
            assertThat(m.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE)).isFalse();

            assertThat(m.supportsResultSetConcurrency(
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)).isTrue();
            assertThat(m.supportsResultSetConcurrency(
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)).isTrue();

            assertThat(m.supportsResultSetConcurrency(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)).isTrue();
            assertThat(m.supportsResultSetConcurrency(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)).isTrue();

            assertThat(m.supportsResultSetConcurrency(
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)).isFalse();
            assertThat(m.supportsResultSetConcurrency(
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)).isFalse();
        }
    }

    @Test
    public void testResultSetScrollDemo() throws SQLException {
        final String sql = "SELECT dept_name FROM department ORDER BY dept_code DESC";
        try (final Connection conn = DriverManager.getConnection(CONN_URL, CONN_USER, CONN_PASS);
                final Statement stmt = conn.createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                final ResultSet rs = stmt.executeQuery(sql)) {

            assertThat(rs.last()).isTrue();
            assertThat(rs.isFirst()).isFalse();
            assertThat(rs.isLast()).isTrue();
            assertThat(rs.getRow()).isEqualTo(5);
            // 最終行の次の行に移動 : 戻り値はvoid
            rs.afterLast();
            assertThat(rs.getRow()).isEqualTo(0);
            assertThat(rs.isBeforeFirst()).isFalse();
            assertThat(rs.isAfterLast()).isTrue();
            // 逆方向にスクロール
            assertThat(rs.previous()).isTrue();
            // 行番号は dept_code とは関連せず、あくまでも result set のレコード位置の値となる。
            assertThat(rs.getRow()).isEqualTo(5);
            assertThat(rs.getString(1)).isEqualTo("Sales");
            assertThat(rs.previous()).isTrue();
            assertThat(rs.getRow()).isEqualTo(4);
            assertThat(rs.getString(1)).isEqualTo("Engineering");
            assertThat(rs.previous()).isTrue();
            assertThat(rs.getRow()).isEqualTo(3);
            assertThat(rs.getString(1)).isEqualTo("Development");
            assertThat(rs.previous()).isTrue();
            assertThat(rs.getRow()).isEqualTo(2);
            assertThat(rs.getString(1)).isEqualTo("Marketing");
            assertThat(rs.previous()).isTrue();
            assertThat(rs.getRow()).isEqualTo(1);
            assertThat(rs.getString(1)).isEqualTo("Education");
            assertThat(rs.previous()).isFalse();

            assertThat(rs.first()).isTrue();
            assertThat(rs.isFirst()).isTrue();
            assertThat(rs.isLast()).isFalse();
            assertThat(rs.getRow()).isEqualTo(1);

            assertThat(rs.absolute(1)).isTrue(); // = rs.first()
            assertThat(rs.getRow()).isEqualTo(1);
            assertThat(rs.getString(1)).isEqualTo("Education");
            assertThat(rs.absolute(-1)).isTrue(); // = rs.last()
            assertThat(rs.getString(1)).isEqualTo("Sales");
            assertThat(rs.getRow()).isEqualTo(5);
            assertThat(rs.absolute(-2)).isTrue(); // = rs.last() && rs.previous()
            assertThat(rs.getRow()).isEqualTo(4);
            assertThat(rs.getString(1)).isEqualTo("Engineering");

            assertThat(rs.absolute(3)).isTrue();
            assertThat(rs.relative(2)).isTrue();
            assertThat(rs.getRow()).isEqualTo(5);
            assertThat(rs.getString(1)).isEqualTo("Sales");
            assertThat(rs.relative(-2)).isTrue();
            assertThat(rs.getRow()).isEqualTo(3);
            assertThat(rs.getString(1)).isEqualTo("Development");

            // 先頭行の前の行に移動 : 戻り値はvoid
            rs.beforeFirst();
            assertThat(rs.getRow()).isEqualTo(0);
            assertThat(rs.isBeforeFirst()).isTrue();
            assertThat(rs.isAfterLast()).isFalse();
        }
    }

    @Test
    public void testResultSetScrollErrorneousDemo() throws SQLException {
        final String sql = "SELECT dept_name FROM department ORDER BY dept_code DESC";
        try (final Connection conn = DriverManager.getConnection(CONN_URL, CONN_USER, CONN_PASS);
                // わざとスクロールオプション未指定でstatement作成
                final Statement stmt = conn.createStatement();
                final ResultSet rs = stmt.executeQuery(sql)) {

            // 順方向の移動, プラス相対位置指定の移動や位置判定メソッドは動く。
            assertThat(rs.next()).isTrue();
            assertThat(rs.relative(1)).isTrue();
            assertThat(rs.last()).isTrue();
            assertThat(rs.isFirst()).isFalse();
            assertThat(rs.isLast()).isTrue();
            assertThat(rs.getRow()).isEqualTo(5);
            // 最終行の次の行に移動 : 戻り値はvoid
            rs.afterLast();
            assertThat(rs.getRow()).isEqualTo(0);
            assertThat(rs.isBeforeFirst()).isFalse();
            assertThat(rs.isAfterLast()).isTrue();

            // 逆方向の移動と絶対位置指定/マイナス相対位置指定の移動は例外発生
            assertThatThrownBy(() -> {
                rs.previous();
            }).isInstanceOf(SQLException.class);
            assertThatThrownBy(() -> {
                rs.first();
            }).isInstanceOf(SQLException.class);
            assertThatThrownBy(() -> {
                rs.absolute(1);
            }).isInstanceOf(SQLException.class);
            assertThatThrownBy(() -> {
                rs.relative(-1);
            }).isInstanceOf(SQLException.class);
            assertThatThrownBy(() -> {
                rs.beforeFirst();
            }).isInstanceOf(SQLException.class);
        }
    }

    @Test
    public void testResultSetScrollWithUpdateDemo() throws SQLException {
        final String sql = "SELECT dept_code, dept_name, dept_address FROM department ORDER BY dept_code DESC";
        try (final Connection conn = DriverManager.getConnection(CONN_URL, CONN_USER, CONN_PASS);
                final Statement stmt = conn.createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                final ResultSet rs = stmt.executeQuery(sql)) {

            rs.absolute(4);
            assertThat(rs.getInt(1)).isEqualTo(2);
            assertThat(rs.getString(2)).isEqualTo("Engineering");
            assertThat(rs.getString("dept_address")).isEqualTo("Yokohama");

            rs.updateString(2, "xxx");
            rs.updateString("dept_address", "yyy");
            rs.updateRow();

            // TYPE_SCROLL_INSENSITIVE なのに、更新後の値が ResultSet で見えている。
            // h2db だとそうなるのだろうか？？
            assertThat(rs.getString(2)).isEqualTo("xxx");
            assertThat(rs.getString("dept_address")).isEqualTo("yyy");

            // 念の為一旦別の行に移動してから、戻ってきて、取り直す
            rs.first();
            rs.absolute(4);
            assertThat(rs.getInt(1)).isEqualTo(2);
            assertThat(rs.getString(2)).isEqualTo("xxx");
            assertThat(rs.getString("dept_address")).isEqualTo("yyy");
        }
    }
}
