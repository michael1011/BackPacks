package at.michael1011.backpacks.tasks;

import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.SQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.SQLException;

import static at.michael1011.backpacks.Main.*;

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

                    if(!SQL.checkCon()) {
                        Bukkit.getConsoleSender().sendMessage(prefix+ ChatColor.translateAlternateColorCodes('&',
                                messages.getString("MySQL.failedToConnect")));

                        Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                messages.getString("MySQL.failedToConnectCheck")));
                    }

                } catch (SQLException e) {
                    e.printStackTrace();

                    Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                            messages.getString("MySQL.failedToConnect")));

                    Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                            messages.getString("MySQL.failedToConnectCheck")));
                }

            }

        }, 36000, 36000);

    }

}
