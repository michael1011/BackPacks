package at.michael1011.backpacks;

import org.bukkit.scheduler.BukkitScheduler;

import java.sql.*;

public class SQL {

    public interface Callback<par> {
        void onSuccess(par rs);
        void onFailure(Throwable e);
    }

    private static Main main;
    private static BukkitScheduler scheduler;

    private static Connection con;

    SQL(Main main) {
        SQL.main = main;
        scheduler = main.getServer().getScheduler();
    }

    public static void getResult(final String query, final Callback<ResultSet> callback) {
        scheduler.runTaskAsynchronously(main, new Runnable() {
            @Override
            public void run() {
                try {
                    final ResultSet rs = getResult(query);

                    scheduler.runTask(main, new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(rs);
                        }
                    });

                } catch (final SQLException e) {
                    e.printStackTrace();

                    scheduler.runTask(main, new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(e);
                        }
                    });

                }

            }
        });

    }
    
    static ResultSet getResult(String query) throws SQLException {
        return con.prepareStatement(query).executeQuery();
    }

    public static void query(final String query, final Callback<Boolean> callback) {
        scheduler.runTaskAsynchronously(main, new Runnable() {
            @Override
            public void run() {
                try {
                    query(query);

                    scheduler.runTask(main, new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(true);
                        }
                    });

                } catch (final SQLException e) {
                    e.printStackTrace();

                    scheduler.runTask(main, new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(e);
                        }
                    });

                }

            }
        });

    }

    static void query(String query) throws SQLException {
        con.prepareStatement(query).execute();
    }

    public static void createCon(String host, String port, String database,
                                 String username, String password) throws SQLException {
        createCon(host, port, database, username, password, true);
    }

    static void createCon(String host, String port, String database,
                          String username, String password, Boolean useSSL) throws SQLException {

        String ssl = "";

        if(!useSSL) {
            ssl = "?useSSL=false";
        }

        con = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database+ssl,
                username, password);
    }

    public static void closeCon() throws SQLException {
        if(con != null) {
            con.close();
        }

    }

    public static boolean checkCon() {
        if(con != null) {
            try {
                if(!con.isClosed()) {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static void checkTable(final String table, final Callback<Boolean> callback) {
        scheduler.runTaskAsynchronously(main, new Runnable() {
            @Override
            public void run() {
                try {
                    final Boolean call = checkTable(table);

                    scheduler.runTask(main, new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(call);
                        }
                    });

                } catch (final SQLException e) {
                    scheduler.runTask(main, new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(e);
                        }
                    });
                }

            }
        });

    }

    static Boolean checkTable(String table) throws SQLException {
        DatabaseMetaData dmb = con.getMetaData();

        final ResultSet rs = dmb.getTables(null, null, table, null);

        Boolean bool = rs.next();

        rs.close();

        return bool;
    }

}
