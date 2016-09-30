package at.michael1011.backpacks;

import at.michael1011.backpacks.commads.Create;
import at.michael1011.backpacks.commads.Give;
import at.michael1011.backpacks.commads.Open;
import at.michael1011.backpacks.commads.Reload;
import at.michael1011.backpacks.listeners.*;
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

import static at.michael1011.backpacks.Updater.checkUpdates;

public class Main extends JavaPlugin {

    public static String version;

    public static YamlConfiguration config, messages, furnaceGui;

    public static String prefix = "";

    public static List<String> availablePlayers = new ArrayList<>();

    private static Main main;

    // todo: add anvil and enchanting backpack: http://bit.ly/2cDX46w
    // todo: verify backpack with nbt tags

    // fixme: translations
    // fixme: add aliases here: https://github.com/michael1011/BackPacks/wiki/Permissions-and-commands

    @Override
    public void onEnable() {
        loadFiles(this);

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

                                    SQL.query("CREATE TABLE IF NOT EXISTS bp_furnaces(uuid VARCHAR(100), ores VARCHAR(100), food VARCHAR(100), " +
                                            "autoFill VARCHAR(100), coal VARCHAR(100))", new SQL.Callback<Boolean>() {
                                        @Override
                                        public void onSuccess(Boolean rs) {
                                            try {
                                                EnchantGlow.getGlow();
                                            } catch (IllegalArgumentException | IllegalStateException ignored) {}

                                            Crafting.initCrafting(Bukkit.getConsoleSender());

                                            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

                                            new Join(main);
                                            new RightClick(main);
                                            new InventoryClose(main);
                                            new BlockPlace(main);
                                            new PlayerDeath(main);
                                            new FurnaceGui(main);
                                            new BlockBreak(main);
                                            new EntityDeath(main);

                                            new Give(main);
                                            new Open(main);
                                            new Create(main);
                                            new Reload(main);

                                            new Reconnect(main);

                                            if(config.getBoolean("Updater.enabled")) {
                                                new Updater(main);

                                                checkUpdates(main, Bukkit.getConsoleSender());
                                            }

                                        }

                                        @Override
                                        public void onFailure(Throwable e) {}

                                    });

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Throwable e) {}

                        });

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
        Bukkit.getScheduler().cancelTasks(this);

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

    public static void loadFiles(Main main) {
        try {
            loadFiles(main, main.getDataFolder(), true);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    static void loadFiles(Main main, File folder, Boolean createNew) throws IOException, InvalidConfigurationException {
        File configF = new File(folder, "config.yml");
        File messagesF = new File(folder, "messages.yml");
        File furnacesF = new File(folder, "furnaceBackPack.yml");

        if(createNew) {
            if(!configF.exists()) {
                configF.getParentFile().mkdirs();
                main.saveResource("config.yml", false);
            }

            if(!messagesF.exists()) {
                messagesF.getParentFile().mkdirs();
                main.saveResource("messages.yml", false);
            }

            if(!furnacesF.exists()) {
                furnacesF.getParentFile().mkdirs();
                main.saveResource("furnaceBackPack.yml", false);
            }

        }

        config = new YamlConfiguration();
        messages = new YamlConfiguration();
        furnaceGui = new YamlConfiguration();

        config.load(configF);
        messages.load(messagesF);
        furnaceGui.load(furnacesF);

        // fixme: config updater

    }

}
