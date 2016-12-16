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
                    final ResultSet rs = con.prepareStatement(query).executeQuery();

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

    public static void query(String query, Callback<Boolean> callback, Boolean async) {
        if (async) {
            query(query, callback);

        } else {
            try {
                con.prepareStatement(query).execute();

                callback.onSuccess(true);

            } catch (SQLException e) {
                e.printStackTrace();

                callback.onFailure(e);
            }

        }

    }

    public static void query(final String query, final Callback<Boolean> callback) {
        scheduler.runTaskAsynchronously(main, new Runnable() {

            @Override
            public void run() {
                try {
                    con.prepareStatement(query).execute();

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

    public static void createCon(String host, String port, String database,
                                 String username, String password) throws SQLException {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        con = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database,
                username, password);
    }

    public static void closeCon() throws SQLException {
        if (con != null) {
            if (!con.isClosed()) {
                con.close();
            }
        }

    }

    public static boolean checkCon() {
        if (con != null) {
            try {
                if (!con.isClosed()) {
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
                    DatabaseMetaData dmb = con.getMetaData();

                    final ResultSet rs = dmb.getTables(null, null, table, null);

                    final Boolean call = rs.next();

                    rs.close();

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

}
