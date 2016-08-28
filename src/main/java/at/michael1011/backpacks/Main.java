package at.michael1011.backpacks;

import at.michael1011.backpacks.commads.Give;
import at.michael1011.backpacks.commads.Open;
import at.michael1011.backpacks.listeners.InventoryClose;
import at.michael1011.backpacks.listeners.Join;
import at.michael1011.backpacks.listeners.RightClick;
import at.michael1011.backpacks.tasks.Reconnect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {

    public static YamlConfiguration config, messages;

    public static String prefix;

    public static List<String> availablePlayers = new ArrayList<>();

    private static Main main;

    // todo: add cache option (load backpacks of player on join)
    // todo: updater
    // todo: drop backpacks in inventory on death
    // todo: command to create a new backpack in config

    @Override
    public void onEnable() {
        createFiles();

        prefix = ChatColor.translateAlternateColorCodes('&', messages.getString("prefix"));

        try {
            new SQL(this);

            SQL.createCon(config.getString("MySQL.host"), config.getString("MySQL.port"),
                    config.getString("MySQL.database"), config.getString("MySQL.username"),
                    config.getString("MySQL.password"));

            if(SQL.checkCon()) {
                Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                        messages.getString("MySQL.connected")));

                main = this;

                SQL.query("CREATE TABLE IF NOT EXISTS bp_users(name VARCHAR(100), uuid VARCHAR(100))", new SQL.Callback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean rs) {
                        SQL.getResult("SELECT * FROM bp_users", new SQL.Callback<ResultSet>() {
                            @Override
                            public void onSuccess(ResultSet rs) {
                                try {
                                    rs.beforeFirst();

                                    while(rs.next()) {
                                        availablePlayers.add(rs.getString("name"));
                                    }

                                    rs.close();

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Throwable e) {

                            }
                        });

                        Crafting.initCrafting();

                        new Reconnect(main);

                        new Join(main);
                        new RightClick(main);
                        new InventoryClose(main);

                        new Give(main);
                        new Open(main);
                    }

                    @Override
                    public void onFailure(Throwable e) {}

                });

            } else {
                Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
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

    @Override
    public void onDisable() {
        if(SQL.checkCon()) {
            try {
                SQL.closeCon();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                    messages.getString("MySQL.closedConnection")));
        }

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createFiles() {
        File configF = new File(getDataFolder(), "config.yml");
        File messagesF = new File(getDataFolder(), "messages.yml");

        if(!configF.exists()) {
            configF.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }

        if(!messagesF.exists()) {
            messagesF.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }

        config = new YamlConfiguration();
        messages = new YamlConfiguration();

        try {
            config.load(configF);
            messages.load(messagesF);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

}
