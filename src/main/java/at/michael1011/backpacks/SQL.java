package at.michael1011.backpacks;

import java.sql.*;

public class SQL {

    private static Connection con;

    public static ResultSet getResult(final String query) {
        try {
            return con.prepareStatement(query).executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static void query(final String query) {
        try {
            con.prepareStatement(query).executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    static boolean checkCon() {
        return con != null;
    }

    public static boolean checkTable(final String table) {
        try {

            DatabaseMetaData dmb = con.getMetaData();

            ResultSet tables = dmb.getTables(null, null, table, null);

            return tables.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
