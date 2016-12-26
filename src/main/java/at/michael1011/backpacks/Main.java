package at.michael1011.backpacks;

import at.michael1011.backpacks.commads.*;
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
import java.util.Map;

import static at.michael1011.backpacks.Crafting.backPacks;
import static at.michael1011.backpacks.Crafting.initConfig;
import static at.michael1011.backpacks.Updater.update;

public class Main extends JavaPlugin {

    public static String version;

    public static List<String> availablePlayers = new ArrayList<>();

    public static YamlConfiguration config, messages, furnaceGui;

    public static String prefix = "";

    static Boolean syncConfig = false;

    // todo: add anvil and enchanting backpack: http://bit.ly/2cDX46w
    // todo: verify backpacks with nbt tags

    // todo: translations

    @Override
    public void onEnable() {
        loadFiles(this);

        updateConfig(this);

        syncConfig = config.getBoolean("MySQL.syncConfig");

        try {
            new SQL(this);

            SQL.createCon(config.getString("MySQL.host"), config.getString("MySQL.port"),
                    config.getString("MySQL.database"), config.getString("MySQL.username"),
                    config.getString("MySQL.password"));

            if (SQL.checkCon()) {
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                        messages.getString("MySQL.connected")));

                SQL.query("CREATE TABLE IF NOT EXISTS bp_users(name VARCHAR(100), uuid VARCHAR(100))", new SQL.Callback<Boolean>() {

                    @Override
                    public void onSuccess(Boolean rs) {
                        SQL.getResult("SELECT * FROM bp_users", new SQL.Callback<ResultSet>() {

                            @Override
                            public void onSuccess(ResultSet rs) {
                                try {
                                    rs.beforeFirst();

                                    while (rs.next()) {
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

                                            if (syncConfig) {
                                                SQL.checkTable("bp", new SQL.Callback<Boolean>() {

                                                    @Override
                                                    public void onSuccess(Boolean rs) {
                                                        if (rs) {
                                                            init();

                                                        } else {
                                                            SQL.query("CREATE TABLE bp(enabled VARCHAR(100), name VARCHAR(100), type VARCHAR(100), material VARCHAR(100), slots INT(100), " +
                                                                            "furnaceGui VARCHAR(100), itemTitle VARCHAR(100), lore VARCHAR(100), inventoryTitle VARCHAR(100), " +
                                                                            "craftingRecipe VARCHAR(100), materials VARCHAR(100), openSound VARCHAR(100), closeSound VARCHAR(100))",
                                                                    new SQL.Callback<Boolean>() {

                                                                        @Override
                                                                        public void onSuccess(Boolean rs) {
                                                                            List<String> enabled = new ArrayList<>();

                                                                            for (Map.Entry<String, Object> entry : config.getConfigurationSection("BackPacks.enabled")
                                                                                    .getValues(true).entrySet()) {

                                                                                enabled.add(entry.getValue().toString());
                                                                            }

                                                                            List<String> toLoad = new ArrayList<>();

                                                                            for (Map.Entry<String, Object> entry : config.getConfigurationSection("BackPacks")
                                                                                    .getValues(false).entrySet()) {

                                                                                if (!entry.getKey().equals("enabled")) {
                                                                                    toLoad.add(entry.getKey());
                                                                                }

                                                                            }

                                                                            initConfig(toLoad, null, false);

                                                                            for (BackPack bp : backPacks) {
                                                                                String openSound = "";
                                                                                String closeSound = "";

                                                                                if (bp.getOpenSound() != null) {
                                                                                    openSound = bp.getOpenSound().name();
                                                                                }

                                                                                if (bp.getCloseSound() != null) {
                                                                                    closeSound = bp.getCloseSound().name();
                                                                                }

                                                                                SQL.query("INSERT INTO bp (enabled, name, type, material, slots, furnaceGui, itemTitle, lore, inventoryTitle, craftingRecipe, materials, openSound, closeSound) VALUES " +
                                                                                        "('" + String.valueOf(enabled.contains(bp.getName())) + "', '" + bp.getName() + "', '" + bp.getType().toString() + "', '" + bp.getMaterial().name() + "', '" + bp.getSlots() + "', '" + String.valueOf(bp.getFurnaceGui()) + "', " +
                                                                                        "'" + bp.getItemTitle() + "', '" + bp.getLoreString().replaceAll("§", "&") + "', '" + bp.getInventoryTitle().replaceAll("§", "&") + "', '" + bp.getCraftingRecipe() + "', '" + bp.getMaterials() + "', " +
                                                                                        "'" + openSound + "', '" + closeSound + "')", new SQL.Callback<Boolean>() {

                                                                                    @Override
                                                                                    public void onSuccess(Boolean rs) {}

                                                                                    @Override
                                                                                    public void onFailure(Throwable e) {}

                                                                                });

                                                                            }

                                                                            config.set("BackPacks", null);

                                                                            try {
                                                                                config.save(new File(getDataFolder(), "config.yml"));
                                                                            } catch (IOException e) {
                                                                                e.printStackTrace();
                                                                            }

                                                                            Bukkit.getServer().resetRecipes();

                                                                            onEnable();

                                                                        }

                                                                        @Override
                                                                        public void onFailure(Throwable e) {}

                                                            });

                                                        }

                                                    }

                                                    @Override
                                                    public void onFailure(Throwable e) {}

                                                });

                                            } else {
                                                init();
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
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                        messages.getString("MySQL.failedToConnect")));

                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                        messages.getString("MySQL.failedToConnectCheck")));
            }

        } catch (SQLException e) {
            e.printStackTrace();

            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                    messages.getString("MySQL.failedToConnect")));

            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                    messages.getString("MySQL.failedToConnectCheck")));
        }

    }

    @Override
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            InventoryClose.saveBackPack(p, p.getOpenInventory(), false, false);
        }

        Bukkit.getScheduler().cancelTasks(this);

        if (SQL.checkCon()) {
            try {
                SQL.closeCon();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                    messages.getString("MySQL.closedConnection")));
        }

    }

    private void init() {
        Crafting.initCrafting(Bukkit.getConsoleSender());

        version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        new Join(this);
        new RightClick(this);
        new InventoryClose(this);
        new BlockPlace(this);
        new PlayerDeath(this);
        new InventoryClick(this);
        new BlockBreak(this);
        new EntityDeath(this);

        new Give(this);
        new Open(this);
        new Reload(this);
        new ListBackPacks(this);

        new Reconnect(this);

        if (!syncConfig) {
            new Create(this);
        }

        if (config.getBoolean("Updater.enabled")) {
            update(this, Bukkit.getConsoleSender());

            new Updater(this);
        }
    }

    private void updateConfig(Main main) {
        try {
            int configVersion = config.getInt("configVersion");

            if (configVersion < 3) {
                File folder = main.getDataFolder();

                YamlConfiguration messagesJar = new YamlConfiguration();

                InputStreamReader reader = new InputStreamReader(getClass().getClassLoader()
                        .getResourceAsStream("messages.yml"));

                messagesJar.load(reader);

                reader.close();

                if (configVersion == 0) {
                    if (new File(folder, "messages.yml").renameTo(new File(folder, "messages.old.yml"))) {
                        main.saveResource("messages.yml", false);

                        String updater = "Updater.";

                        config.set(updater + "enabled", true);
                        config.set(updater + "interval", 24);
                        config.set(updater + "autoUpdate", false);

                        config.set("configVersion", 1);

                        Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                                "&cUpdated config files to version 1. " +
                                        "&4Your old messages.yml file was renamed to messages.old.yml"));

                    }

                }

                if (configVersion== 1) {
                    String path = "Help.bpcreate.steps.";

                    messages.set("Help.bpcreate.syntaxError.2", messagesJar.getString("Help.bpcreate.syntaxError.2"));

                    messages.set(path + "inventorytitle.1", messagesJar.getString(path + "inventorytitle.1"));
                    messages.set(path + "inventorytitle.2", messagesJar.getString(path + "inventorytitle.2"));
                    messages.set(path + "inventorytitle.3", messagesJar.getString(path + "inventorytitle.3"));

                    messages.set(path + "preview.inventoryTitle", messagesJar.getString(path + "preview.inventoryTitle"));

                    messages.set(path + "preview.sound.title", messagesJar.getString(path + "preview.sound.title"));
                    messages.set(path + "preview.sound.line", messagesJar.getString(path + "preview.sound.line"));

                    messages.set("Help.soundNotValid", messagesJar.getString("Help.soundNotValid"));

                    messages.set("Help.bpcreate.steps.sound.1", messagesJar.getString("Help.bpcreate.steps.sound.1"));
                    messages.set("Help.bpcreate.steps.sound.2", messagesJar.getString("Help.bpcreate.steps.sound.2"));
                    messages.set("Help.bpcreate.steps.sound.3", messagesJar.getString("Help.bpcreate.steps.sound.3"));
                    messages.set("Help.bpcreate.steps.sound.4", messagesJar.getString("Help.bpcreate.steps.sound.4"));

                    messages.set("Help.bpcreate.steps.soundOther.1", messagesJar.getString("Help.bpcreate.steps.soundOther.1"));
                    messages.set("Help.bpcreate.steps.soundOther.2", messagesJar.getString("Help.bpcreate.steps.soundOther.2"));

                    messages.set("Help.bpcreate.steps.soundNotValid", messagesJar.getString("Help.bpcreate.steps.soundNotValid"));

                    messages.set("Help.bplist.list", messagesJar.getString("Help.bplist.list"));

                    config.set("BackPackInBackPack", false);
                    config.set("configVersion", 2);

                    Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                            "&cUpdated config files to version 2"));
                }

                if (configVersion == 2) {
                    messages.set("Help.bpopen.error", messagesJar.getString("Help.bpopen.error"));

                    config.set("MySQL.syncConfig", false);
                    config.set("configVersion", 3);

                    Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                            "&cUpdated config files to version 3"));
                }

                config.save(new File(folder, "config.yml"));
                messages.save(new File(folder, "messages.yml"));

                loadFiles(main);
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

            if (!configF.exists()) {
                main.saveResource("config.yml", false);
            }

            if (!messagesF.exists()) {
                main.saveResource("messages.yml", false);
            }

            if (!furnacesF.exists()) {
                main.saveResource("furnaceBackPack.yml", false);
            }

            config = new YamlConfiguration();
            messages = new YamlConfiguration();
            furnaceGui = new YamlConfiguration();

            config.load(configF);
            messages.load(messagesF);
            furnaceGui.load(furnacesF);

            prefix = ChatColor.translateAlternateColorCodes('&', messages.getString("prefix"));

        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

    public static String getTrimmedId(Player player) {
        return player.getUniqueId().toString().replaceAll("-", "");
    }

}
