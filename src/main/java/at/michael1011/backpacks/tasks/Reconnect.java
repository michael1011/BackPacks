package at.michael1011.backpacks.tasks;

import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.SQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;

import static at.michael1011.backpacks.Main.*;

public class Reconnect {

    public Reconnect(Main main) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(main, new Runnable() {
            @Override
            public void run() {
                reconnectTask(Bukkit.getConsoleSender(), true);
            }

        }, 36000, 36000);

    }

    static void reconnectTask(CommandSender sender, Boolean stackTrace) {
        try {
            if(SQL.checkCon()) {
                SQL.closeCon();
            }

            SQL.createCon(config.getString("MySQL.host"), config.getString("MySQL.port"),
                    config.getString("MySQL.database"), config.getString("MySQL.username"),
                    config.getString("MySQL.password"));

        } catch (SQLException e) {
            if(stackTrace) {
                e.printStackTrace();
            }

            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                    messages.getString("MySQL.failedToConnect")));

            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                    messages.getString("MySQL.failedToConnectCheck")));
        }
    }

}
