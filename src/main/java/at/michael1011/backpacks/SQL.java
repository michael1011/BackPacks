package at.michael1011.backpacks;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQL {

    static Connection con;

    public static ResultSet getResult(final String query) throws SQLException {
        return con.prepareStatement(query).executeQuery();
    }


    public static Boolean query(final String query) throws SQLException {
        return con.prepareStatement(query).execute();
    }

    public static void createCon(String host, String port, String database,
                                 String username, String password) throws SQLException {

        con = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database,
                username, password);
    }

    public static void closeCon() throws SQLException {
        if (con != null) {
            con.close();
        }

    }

    public static boolean checkCon() {
        return con != null;
    }

}
