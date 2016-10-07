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

import static at.michael1011.backpacks.Updater.update;

public class Main extends JavaPlugin {

    public static String version;

    public static YamlConfiguration config, messages, furnaceGui;

    public static String prefix = "";

    public static List<String> availablePlayers = new ArrayList<>();

    private static Main main;

    // fixme: save backpacks onDisable

    // todo: add anvil and enchanting backpack: http://bit.ly/2cDX46w
    // todo: verify backpacks with nbt tags

    // todo: translations

    @Override
    public void onEnable() {
        loadFiles(this);

        prefix = ChatColor.translateAlternateColorCodes('&', messages.getString("prefix"));

        updateConfig(this);

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
                                                update(main, Bukkit.getConsoleSender());

                                                new Updater(main);
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

    private void updateConfig(Main main) {
        try {
            File folder = main.getDataFolder();

            if(config.getInt("configVersion") == 0) {
                if(new File(folder, "messages.yml").renameTo(new File(folder, "messages.old.yml"))) {
                    main.saveResource("messages.yml", false);

                    String updater = "Updater.";

                    config.set(updater+"enabled", true);
                    config.set(updater+"interval", 24);
                    config.set(updater+"autoUpdate", false);

                    config.set("configVersion", 1);

                    config.save(new File(folder, "config.yml"));

                    loadFiles(main);

                    Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                            "&cUpdated config files to version 1. "+
                                    "&4Your old messages.yml file was renamed to messages.old.yml"));

                }

            }

            if(config.getInt("configVersion") == 1) {
                // fixme: get values from messages.yml in classpath

                String path = "Help.bpcreate.steps.";

                messages.set("Help.bpcreate.syntaxError.2", "&eOther available functions are: &6displayname&e, &6description&e, &6inventorytitle, &e&6material&e, &6crafting&e, &6materials&e, &6type&e, &6slots&e, &6gui&e, &6preview&e, &6finish&e, &6item");

                messages.set(path+"inventorytitle.1", "&eSet the &6title of the inventory &ewith &6/bpc inventorytitle <title>");
                messages.set(path+"inventorytitle.2", "&eIf you skip this the name of the item will be the name of the inventory");
                messages.set(path+"inventorytitle.3", "&eYou can get information about the next step with &6/bpc description");

                messages.set(path+"preview.inventoryTitle", "&eInventorytitle: &r%title%");

                messages.save(new File(folder, "messages.yml"));

                config.set("configVersion", 2);
                config.save(new File(folder, "config.yml"));

                Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                        "&cUpdated config files to version 2"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void loadFiles(Main main) {
        try {
            File folder = main.getDataFolder();

            File configF = new File(folder, "config.yml");
            File messagesF = new File(folder, "messages.yml");
            File furnacesF = new File(folder, "furnaceBackPack.yml");

            if(!configF.exists()) {
                main.saveResource("config.yml", false);
            }

            if(!messagesF.exists()) {
                main.saveResource("messages.yml", false);
            }

            if(!furnacesF.exists()) {
                main.saveResource("furnaceBackPack.yml", false);
            }

            config = new YamlConfiguration();
            messages = new YamlConfiguration();
            furnaceGui = new YamlConfiguration();

            config.load(configF);
            messages.load(messagesF);
            furnaceGui.load(furnacesF);

        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

}
