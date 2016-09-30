package at.michael1011.backpacks;

import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static at.michael1011.backpacks.SQL.createCon;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SQLTest {

    @Test
    public void testSQL() throws SQLException {
        createCon("localhost", "3306", "bpTest", "bpTest", "bpTest", false);

        assertTrue(SQL.checkCon());

        assertFalse(SQL.checkTable("doesNotExists"));

        SQL.query("DROP TABLE IF EXISTS doesExist");

        SQL.query("CREATE TABLE IF NOT EXISTS doesExist(test VARCHAR(100))");
        SQL.query("INSERT INTO doesExist(test) VALUES ('true')");

        assertTrue(SQL.checkTable("doesExist"));

        ResultSet rs = SQL.getResult("SELECT * FROM doesExist");

        assertTrue(rs.last());

        assertTrue(rs.getRow() == 1);
        assertTrue(rs.first());
        assertTrue(Boolean.valueOf(rs.getString("test")));

        SQL.query("DROP TABLE doesExist");

        SQL.closeCon();

        assertFalse(SQL.checkCon());
    }

}
