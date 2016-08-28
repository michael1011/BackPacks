package at.michael1011.backpacks.tasks;

import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.SQL;
import org.bukkit.Bukkit;

import java.sql.SQLException;

import static at.michael1011.backpacks.Main.config;

public class Reconnect {

    public Reconnect(Main main) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(main, new Runnable() {
            @Override
            public void run() {
                try {
                    SQL.closeCon();

                    SQL.createCon(config.getString("MySQL.host"), config.getString("MySQL.port"),
                            config.getString("MySQL.database"), config.getString("MySQL.username"),
                            config.getString("MySQL.password"));

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

        }, 36000, 36000);

    }

}
