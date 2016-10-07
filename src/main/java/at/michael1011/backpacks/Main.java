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
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static at.michael1011.backpacks.Updater.update;

public class Main extends JavaPlugin {

    public static String version;

    public static YamlConfiguration config, messages, furnaceGui;

    public static String prefix = "";

    public static Boolean backPackInBackPack;

    public static List<String> availablePlayers = new ArrayList<>();

    private static Main main;

    // todo: add anvil and enchanting backpack: http://bit.ly/2cDX46w
    // todo: verify backpacks with nbt tags

    // todo: translations

    @Override
    public void onEnable() {
        loadFiles(this);

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
                                            new InventoryClick(main);
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
        for(Player p : Bukkit.getOnlinePlayers()) {
            InventoryClose.saveBackPack(p, p.getOpenInventory(), false, false);
        }

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
                YamlConfiguration messagesJar = new YamlConfiguration();

                InputStreamReader reader = new InputStreamReader(getClass().getClassLoader()
                        .getResourceAsStream("messages.yml"));

                messagesJar.load(reader);

                reader.close();

                String path = "Help.bpcreate.steps.";

                messages.set("Help.bpcreate.syntaxError.2", messagesJar.getString("Help.bpcreate.syntaxError.2"));

                messages.set(path+"inventorytitle.1", messagesJar.getString(path+"inventorytitle.1"));
                messages.set(path+"inventorytitle.2", messagesJar.getString(path+"inventorytitle.2"));
                messages.set(path+"inventorytitle.3", messagesJar.getString(path+"inventorytitle.3"));

                messages.set(path+"preview.inventoryTitle", messagesJar.getString(path+"preview.inventoryTitle"));

                messages.set("Help.soundNotValid", messagesJar.getString("Help.soundNotValid"));

                messages.save(new File(folder, "messages.yml"));

                config.set("BackPackInBackPack", false);
                config.set("configVersion", 2);

                config.save(new File(folder, "config.yml"));

                Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                        "&cUpdated config files to version 2"));
            }

        } catch (IOException | InvalidConfigurationException e) {
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


            backPackInBackPack = config.getBoolean("BackPackInBackPack");

            prefix = ChatColor.translateAlternateColorCodes('&', messages.getString("prefix"));

        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

}
