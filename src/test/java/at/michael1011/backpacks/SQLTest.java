package at.michael1011.backpacks;

import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class SQLTest {

    @Test
    public void testSQL() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            SQL.createCon("sql7.freemysqlhosting.net", "3306", "sql7132524", "sql7132524", "Ekll7fuBGU");

            assertTrue(SQL.checkCon());

            SQL.query("CREATE TABLE IF NOT EXISTS bp_users(name VARCHAR(100), "+
                    "displayName VARCHAR(100), uuid VARCHAR(100))");

            SQL.closeCon();

            assertTrue(SQL.con.isClosed());

        } catch (ClassNotFoundException | SQLException e) {
            Assert.fail(e.getCause().toString());
        }

    }

}