package at.michael1011.backpacks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin {

    public static YamlConfiguration config, messages;

    public static String prefix;

    @Override
    public void onEnable() {
        createFiles();

        prefix = ChatColor.translateAlternateColorCodes('&', messages.getString("prefix"));

        SQL.createCon(config.getString("MySQL.host"), config.getString("MySQL.port"),
                config.getString("MySQL.database"), config.getString("MySQL.username"),
                config.getString("MySQL.password"));

        if(SQL.con != null) {
            Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                    messages.getString("MySQL.connected")));

        } else {
            Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                    messages.getString("MySQL.failedToConnect")));

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