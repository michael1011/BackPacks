package listeners;

import main.main;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SaveLoadSQL implements Listener {

    public main plugin;

    public SaveLoadSQL(main main) {
        this.plugin = main;
        this.plugin.getServer().getPluginManager().registerEvents(this, main);
    }

    public static void createTableLB(String ID) {
        main.update("create table if not exists littleBP_"+ID+" (type VARCHAR(200),amount INT(100),dur INT(200),displayname VARCHAR(2000),lore VARCHAR(2000),enchantname VARCHAR(2000))");
    }

    public static void createTableNB(String ID) {
        main.update("create table if not exists normalBP_"+ID+" (type VARCHAR(200),amount INT(100),dur INT(200),displayname VARCHAR(2000),lore VARCHAR(2000),enchantname VARCHAR(2000))");
    }

    private void save(ItemStack stack, String id, String bp) {
        String type, lore = "doesnotexist", name = "null";
        int amount, durability;

        List<String> enchantname = new ArrayList<String>();
        List<String> loreName;

        type = stack.getType().name();
        amount = stack.getAmount();
        durability = stack.getDurability();

        if(stack.hasItemMeta()) {
            if(stack.getItemMeta().hasDisplayName()) {
                name = stack.getItemMeta().getDisplayName();
            }

            if(stack.getItemMeta().hasLore()) {
                loreName = stack.getItemMeta().getLore();

                lore = StringUtils.join(loreName, '~');
            }
        }

        if(stack.getItemMeta().hasEnchants()) {
            for(Enchantment ench : stack.getEnchantments().keySet()) {
                String enchantment = ench.getName()+":"+stack.getEnchantmentLevel(ench);
                enchantname.add(enchantment);
            }
        } else {
            enchantname.add("null");
        }

        String enchantstring = StringUtils.join(enchantname, '/');

        main.update("insert into "+bp+id+" (type,amount,dur,displayname,lore,enchantname) values ('"+type+"','"+amount+"','"+durability+"','"+name+"','"+lore+"','"+enchantstring+"')");
    }

    public static ItemStack load(String type, int amount, int durability, String displayname, String lore, String enchantments) {
        Short dur = (short) durability;

        ItemStack item = new ItemStack(Material.valueOf(type));
        ItemMeta meta = item.getItemMeta();

        item.setAmount(amount);
        item.setDurability(dur);

        if(!displayname.equals("null")) {
            meta.setDisplayName(displayname);
        }

        if(!lore.equals("doesnotexist")) {
            List<String> loreList = new ArrayList<String>(Arrays.asList(lore.split("~")));
            meta.setLore(loreList);
        }

        if(!enchantments.equals("null")) {
            List<String> enchantmentsList = new ArrayList<String>(Arrays.asList(enchantments.split("/")));

            for(String enchantment : enchantmentsList) {
                String[] parts = enchantment.split(":");

                Enchantment ench = Enchantment.getByName(parts[0]);
                int enchLvl = Integer.parseInt(parts[1]);

                item.addEnchantment(ench, enchLvl);
                meta.addEnchant(ench, enchLvl, true);
            }
        }

        item.setItemMeta(meta);
        return new ItemStack(item);
    }


    @EventHandler
    public void join(PlayerJoinEvent e) {
        if(main.config.getBoolean("MySQL.enable")) {
            Player p = e.getPlayer();
            UUID id = p.getUniqueId();

            String ID = id.toString();
            String trimmedID = ID.replaceAll("-", "");


            if(p.hasPermission("backpacks.littleBackPack")) {
                createTableLB(trimmedID);

                Inventory inv = Bukkit.getServer().createInventory(p, main.names.getInt("LittleBackPack.Slots"), ChatColor.translateAlternateColorCodes('&', main.names.getString("LittleBackPack.Name")));

                try {
                    ResultSet rs = main.getResult("select * from littleBP_"+trimmedID);

                    assert rs != null;
                    while(rs.next()) {
                        inv.addItem(load(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getString(5), rs.getString(6)));
                    }

                } catch(SQLException e1) {
                    e1.printStackTrace();
                }

                main.littleB.put(id, inv);
            }

            if(p.hasPermission("backpacks.normalBackPack")) {
                createTableNB(trimmedID);

                Inventory inv = Bukkit.getServer().createInventory(p, main.names.getInt("NormalBackPack.Slots"), ChatColor.translateAlternateColorCodes('&', main.names.getString("NormalBackPack.Name")));

                try {
                    ResultSet rs = main.getResult("select * from normalBP_"+trimmedID);

                    assert rs != null;
                    while(rs.next()) {
                        inv.addItem(load(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getString(5), rs.getString(6)));
                    }

                } catch(SQLException e2) {
                    e2.printStackTrace();
                }

                main.normalB.put(id, inv);
            }

        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        if(main.config.getBoolean("MySQL.enable")) {
            Player p = e.getPlayer();
            UUID id = p.getUniqueId();

            String ID = id.toString();
            String trimmedID = ID.replaceAll("-", "");

            if(p.hasPermission("backpacks.littleBackPack")) {
                createTableLB(trimmedID);

                main.update("delete from littleBP_"+trimmedID);

                for(ItemStack stack : main.littleB.get(id)) {
                    if(stack != null) {
                        save(stack, trimmedID, "littleBP_");
                    }
                }
            }

            if(p.hasPermission("backpacks.normalBackPack")) {
                createTableNB(trimmedID);

                main.update("delete from normalBP_"+trimmedID);

                for(ItemStack stack : main.normalB.get(id)) {
                    if(stack != null) {
                        save(stack, trimmedID, "normalBP_");
                    }
                }
            }

        }

    }

}
