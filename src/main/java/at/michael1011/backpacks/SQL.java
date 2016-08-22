package at.michael1011.backpacks;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQL {

    static Connection con;

    public static ResultSet getResult(final String query) {
        try {
            return con.prepareStatement(query).executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Boolean query(final String query) {
        try {
            return con.prepareStatement(query).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void createCon(String host, String port, String database, String username, String password) {
        try {
            con = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database,
                    username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeCon() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

}
