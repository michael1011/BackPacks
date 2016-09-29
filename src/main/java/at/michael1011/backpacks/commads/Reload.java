package at.michael1011.backpacks.commads;

import at.michael1011.backpacks.Crafting;
import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.SQL;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.sql.SQLException;

import static at.michael1011.backpacks.Main.*;

public class Reload implements CommandExecutor {

    private Main main;

    public Reload(Main main) {
        this.main = main;

        PluginCommand command = main.getCommand("bpreload");

        command.setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission("backpacks.reload")) {
            try {
                if(SQL.checkCon()) {
                    SQL.closeCon();
                }

                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                        messages.getString("MySQL.closedConnection")));

                Main.loadFiles(main);

                SQL.createCon(config.getString("MySQL.host"), config.getString("MySQL.port"),
                        config.getString("MySQL.database"), config.getString("MySQL.username"),
                        config.getString("MySQL.password"));

                if(SQL.checkCon()) {
                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                            messages.getString("MySQL.connected")));

                    sender.sendMessage(prefix);

                    Crafting.initCrafting(sender);

                    sender.sendMessage(prefix);

                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                            messages.getString("Help.bpreload.successful")));

                } else {
                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                            messages.getString("MySQL.failedToConnect")));

                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                            messages.getString("MySQL.failedToConnectCheck")));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            sender.sendMessage(prefix+ ChatColor.translateAlternateColorCodes('&',
                    messages.getString("Help.noPermission")));
        }

        return true;
    }

}
