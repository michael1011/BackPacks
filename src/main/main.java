package main;

import commands.Give;
import commands.Help;
import commands.Reload;
import listeners.BackPack;
import listeners.SaveLoadSQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import listeners.SaveLoad;

public class main extends JavaPlugin {

    public static main instance;

    public static File configF, namesF, backpacksF;
    public static FileConfiguration config, names, backpacks;

    static Connection connection;
    private String host, port, database, username, password;

    public static HashMap<UUID, Inventory> littleB = new HashMap<UUID, Inventory>();
    public static HashMap<UUID, Inventory> normalB = new HashMap<UUID, Inventory>();

    @Override
    public void onEnable() {
        instance = this;

        createFiles();

        Boolean SQLenable = config.getBoolean("MySQL.enable");

        if(SQLenable) {
            establishMySQL();
            new SaveLoadSQL(this);
        } else {
            new SaveLoad(this);
        }

        new Reload(this);
        new Give(this);
        new Help(this);
        new Crafting(this);
        new BackPack(this);

        Crafting.CraftingB();

        for(Player p : Bukkit.getOnlinePlayers()) {
            createInv(p);
        }

        Bukkit.getConsoleSender().sendMessage(Pref.p+ChatColor.RED+"Plugin enabled!");

        if(connection != null) {
            Bukkit.getConsoleSender().sendMessage(Pref.p+ChatColor.YELLOW+"MySQL connection enabled!");
        }

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(Pref.p+ChatColor.RED+"Failed to connect to Metrics!");
        }

    }

    public static void update(String s) {
        try {
            PreparedStatement ps = connection.prepareStatement(s);
            ps.execute();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static ResultSet getResult(String s) throws SQLException{
        try {
            PreparedStatement ps = connection.prepareStatement(s);
            return ps.executeQuery();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onDisable() {

        for(Map.Entry<UUID, Inventory> entry : littleB.entrySet()) {
            if(!backpacks.contains("littleB."+entry.getKey())) {
                backpacks.createSection("littleB."+entry.getKey());
            }

            int i = 1;
            for (ItemStack stack : entry.getValue()) {
                if(stack != null) {
                    listeners.SaveLoad.save(main.backpacks.createSection("littleB."+entry.getKey()+"."+i++), stack);
                }
            }
        }

        for(Map.Entry<UUID, Inventory> entry : normalB.entrySet()) {
            if(!backpacks.contains("normalB."+entry.getKey())) {
                backpacks.createSection("normalB."+entry.getKey());
            }

            int i = 1;
            for (ItemStack stack : entry.getValue()) {
                if(stack != null) {
                    listeners.SaveLoad.save(main.backpacks.createSection("normalB."+entry.getKey()+"."+i++), stack);
                }
            }
        }

        try {
            backpacks.save(backpacksF);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if(connection != null) {
            try {
                connection.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }

            Bukkit.getConsoleSender().sendMessage(Pref.p+ChatColor.YELLOW+"MySQL connection disabled!");
        }

        instance = null;

        Bukkit.getConsoleSender().sendMessage(Pref.p + ChatColor.RED +"Plugin disabled!");
    }

    private void establishMySQL() {
        host = config.getString("MySQL.host");
        port = config.getString("MySQL.port");
        database = config.getString("MySQL.database");
        username = config.getString("MySQL.username");
        password = config.getString("MySQL.password");

        try {
            openConnection();
            Statement statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection openConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://"
                        + this.host+ ":" + this.port + "/" + this.database, this.username, this.password);

        return connection;
    }

    private ItemStack load(ConfigurationSection sec) {
        Short dur = (short) sec.getLong("dur");

        ItemStack item = new ItemStack(Material.valueOf(sec.getString("type")));
        ItemMeta itemM = item.getItemMeta();
        item.setAmount(sec.getInt("amount"));
        item.setDurability(dur);

        if(sec.get("enchantments") != null) {
            for (String enchantmentStr : sec.getConfigurationSection("enchantments").getKeys(false)) {

                Enchantment ench = Enchantment.getByName(enchantmentStr);
                int level = sec.getInt("enchantments." + enchantmentStr + ".lvl");

                item.addEnchantment(ench, level);
                itemM.addEnchant(ench, level, true);
            }
        }

        if(sec.getString("name") != null) {
            itemM.setDisplayName(sec.getString("name"));
        }

        if(sec.getString("lore") != null) {
            itemM.setLore(sec.getStringList("lore"));
        }

        item.setItemMeta(itemM);
        return new ItemStack(item);
    }

    private void createInv(Player p) {

        UUID id = p.getUniqueId();

        if(!config.getBoolean("MySQL.enable")) {
            Inventory inv = Bukkit.getServer().createInventory(p, main.names.getInt("LittleBackPack.Slots"), ChatColor.translateAlternateColorCodes('&', main.names.getString("LittleBackPack.Name")));
            Inventory invN = Bukkit.getServer().createInventory(p, main.names.getInt("NormalBackPack.Slots"), ChatColor.translateAlternateColorCodes('&', main.names.getString("NormalBackPack.Name")));

            if(main.backpacks.contains("littleB."+id)) {
                for(String item : main.backpacks.getConfigurationSection("littleB."+id).getKeys(false)) {
                    inv.addItem(load(main.backpacks.getConfigurationSection("littleB."+id+"."+item)));
                }
            }

            if(main.backpacks.contains("normalB."+id)) {
                for(String item : main.backpacks.getConfigurationSection("normalB."+id).getKeys(false)) {
                    invN.addItem(load(main.backpacks.getConfigurationSection("normalB."+id+"."+item)));
                }
            }

            main.littleB.put(id, inv);
            main.normalB.put(id, invN);

        } else {
            String ID = id.toString();
            String trimmedID = ID.replaceAll("-", "");

            if(p.hasPermission("backpacks.littleBackPack")) {
                SaveLoadSQL.createTableLB(trimmedID);

                Inventory inv = Bukkit.getServer().createInventory(p, names.getInt("LittleBackPack.Slots"), ChatColor.translateAlternateColorCodes('&', names.getString("LittleBackPack.Name")));

                try {
                    ResultSet rs = getResult("select * from littleBP_"+trimmedID);

                    assert rs != null;
                    while(rs.next()) {
                        inv.addItem(SaveLoadSQL.load(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getString(5), rs.getString(6)));
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                main.littleB.put(id, inv);
            }

            if(p.hasPermission("backpacks.normalBackPack")) {
                SaveLoadSQL.createTableNB(trimmedID);

                Inventory inv = Bukkit.getServer().createInventory(p, names.getInt("NormalBackPack.Slots"), ChatColor.translateAlternateColorCodes('&', names.getString("NormalBackPack.Name")));

                try {
                    ResultSet rs = getResult("select * from normalBP_"+trimmedID);

                    assert rs != null;
                    while(rs.next()) {
                        inv.addItem(SaveLoadSQL.load(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getString(5), rs.getString(6)));
                    }

                } catch(SQLException e) {
                    e.printStackTrace();
                }

                main.normalB.put(id, inv);
            }

        }
    }

    private void createFiles() {

        configF = new File(getDataFolder(), "config.yml");
        namesF = new File(getDataFolder(), "names.yml");
        backpacksF = new File(getDataFolder(), "backpacks.yml");

        if(!configF.exists()) {
            configF.getParentFile().mkdirs();
            copy(getResource("config.yml"), configF);
        }
        if(!namesF.exists()) {
            namesF.getParentFile().mkdirs();
            copy(getResource("names.yml"), namesF);
        }

        if(!backpacksF.exists()) {
            backpacksF.getParentFile().mkdirs();
            copy(getResource("backpacks.yml"), backpacksF);
        }

        config = new YamlConfiguration();
        names = new YamlConfiguration();
        backpacks = new YamlConfiguration();

        try {
            config.load(configF);
            names.load(namesF);
            backpacks.load(backpacksF);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void copy(InputStream in, File file) {

        try {

            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {

                out.write(buf, 0, len);

            }
            out.close();
            in.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }
}