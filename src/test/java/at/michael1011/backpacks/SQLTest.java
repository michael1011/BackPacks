package at.michael1011.backpacks;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SQLTest {

    @Test
    public void testSQL() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        SQL.createCon("sql7.freemysqlhosting.net", "3306", "sql7132378", "sql7132378", "Ag8WeWFa1h");

        assertTrue(SQL.checkCon());

        SQL.query("CREATE TABLE IF NOT EXISTS bp_users(name VARCHAR(100), "+
                "displayName VARCHAR(100), uuid VARCHAR(100))");

        SQL.closeCon();

        assertTrue(SQL.con.isClosed());
    }

}