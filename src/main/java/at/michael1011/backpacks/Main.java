package at.michael1011.backpacks;

import at.michael1011.backpacks.commads.Give;
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
import java.sql.SQLException;

public class Main extends JavaPlugin {

    public static YamlConfiguration config, messages;

    public static String prefix;

    // todo: add cache option (load backpacks of player on join)
    // todo: optimize database table types

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

                SQL.query("CREATE TABLE IF NOT EXISTS bp_users(name VARCHAR(100), uuid VARCHAR(100))");

                Crafting.initCrafting();

                new Reconnect(this);

                new Join(this);
                new RightClick(this);
                new InventoryClose(this);

                new Give(this);

            } else {
                Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                        messages.getString("MySQL.failedToConnect")));

            }

        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                    messages.getString("MySQL.failedToConnect")));

            e.printStackTrace();
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
