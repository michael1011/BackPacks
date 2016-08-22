package at.michael1011.backpacks;

import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class SQLTest {

    @Test
    public void testSQL() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            SQL.createCon("sql7.freemysqlhosting.net", "3306", "sql7132378", "sql7132378", "Ag8WeWFa1h");

            assertTrue(SQL.con != null);

            SQL.closeCon();

            assertTrue(SQL.con.isClosed());

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}