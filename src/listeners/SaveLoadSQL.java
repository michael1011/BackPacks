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
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SaveLoadSQL implements Listener {

    public main plugin;

    public SaveLoadSQL(main main) {
        this.plugin = main;
        this.plugin.getServer().getPluginManager().registerEvents(this, main);
    }

    public static void createTableLB(String ID, String bp) {
        main.update("create table if not exists "+bp+"_"+ID+" (type VARCHAR(200),amount INT(100),dur INT(200),slot INT(100),displayname VARCHAR(2000),lore VARCHAR(2000),enchantname VARCHAR(2000),potion VARCHAR(5000))");
    }

    private static void noPotion(ItemStack stack, String name, List<String> loreName, String lore, List<String> enchantname ) {
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
    }

    public static void save(ItemStack stack, String id, String bp, int slot) {
        String type, lore = "doesnotexist", name = "null", potion = "null";
        int amount, durability;

        List<String> enchantname = new ArrayList<>();
        List<String> loreName = new ArrayList<>();

        Material typeM = stack.getType();

        type = typeM.name();
        amount = stack.getAmount();
        durability = stack.getDurability();

        if(typeM == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta book = (EnchantmentStorageMeta) stack.getItemMeta();
            Map<Enchantment, Integer> enchants = book.getStoredEnchants();

            for (Enchantment ench : enchants.keySet()) {
                String enchantment = ench.getName() + ":" + book.getStoredEnchantLevel(ench);
                enchantname.add(enchantment);
            }

        } else if(main.version.equals("1.9")) {
            if(typeM == Material.POTION || typeM == Material.SPLASH_POTION || typeM == Material.LINGERING_POTION || typeM == Material.TIPPED_ARROW) {
                PotionMeta potionM = (PotionMeta) stack.getItemMeta();

                if(potionM.getBasePotionData().getType().getEffectType() != null) {
                    PotionData potionD = potionM.getBasePotionData();

                    String potionType = potionD.getType().toString();
                    Boolean potionExtended = potionD.isExtended();
                    Boolean potionUpgraded = potionD.isUpgraded();

                    potion = potionType+"/"+potionExtended+"/"+potionUpgraded;
                }

            } else {
                noPotion(stack, name, loreName, lore, enchantname);
            }

        } else {
            if(typeM == Material.POTION) {
                @SuppressWarnings("deprecation")
                Potion potionD = Potion.fromItemStack(stack);

                if(potionD.getType().getEffectType() != null) {
                    Boolean pExtended = false;

                    String pType = potionD.getType().toString();
                    int pLvl = potionD.getLevel();
                    Boolean pSplash = potionD.isSplash();

                    if(!pSplash) {
                        pExtended = potionD.hasExtendedDuration();
                    }

                    potion = pType+"/"+pLvl+"/"+pSplash+"/"+pExtended;
                }

            } else {
                noPotion(stack, name, loreName, lore, enchantname);
            }
        }

        String enchantstring = StringUtils.join(enchantname, '/');

        main.update("insert into "+bp+id+" (type,amount,dur,slot,displayname,lore,enchantname,potion) values ('"+type+"','"+amount+"','"+durability+"','"+slot+"','"+name+"','"+lore+"','"+enchantstring+"','"+potion+"')");
    }

    public static ItemStack load(String type, int amount, int durability, String displayname, String lore, String enchantments, String potion) {
        Short dur = (short) durability;

        ItemStack item = new ItemStack(Material.valueOf(type));
        item.setAmount(amount);
        item.setDurability(dur);

        if(type.equals("ENCHANTED_BOOK")) {
            EnchantmentStorageMeta enchantmentsMeta = (EnchantmentStorageMeta) item.getItemMeta();
            List<String> enchantmentsList = new ArrayList<>(Arrays.asList(enchantments.split("/")));

            for (String ench : enchantmentsList) {
                String[] parts = ench.split(":");

                Enchantment enchant = Enchantment.getByName(parts[0]);
                int enchantLvl = Integer.parseInt(parts[1]);

                enchantmentsMeta.addStoredEnchant(enchant, enchantLvl, true);
            }

            item.setItemMeta(enchantmentsMeta);

        } else if(type.contains("POTION") || type.equals("TIPPED_ARROW")) {
            if(main.version.equals("1.9")) {
                String[] parts = potion.split("/");

                PotionMeta potionM = (PotionMeta) item.getItemMeta();

                PotionData potionD = new PotionData(PotionType.valueOf(parts[0]), Boolean.parseBoolean(parts[1]), Boolean.parseBoolean(parts[2]));

                potionM.setBasePotionData(potionD);
                item.setItemMeta(potionM);

            } else {
                String[] parts = potion.split("/");

                @SuppressWarnings("deprecation")
                Potion potionD = new Potion(PotionType.valueOf(parts[0]), Integer.parseInt(parts[1]), Boolean.parseBoolean(parts[2]), Boolean.parseBoolean(parts[3]));

                item = potionD.toItemStack(amount);
            }

        } else {
            ItemMeta meta = item.getItemMeta();

            if(!displayname.equals("null")) {
                meta.setDisplayName(displayname);
            }

            if(!lore.equals("doesnotexist")) {
                List<String> loreList = new ArrayList<>(Arrays.asList(lore.split("~")));
                meta.setLore(loreList);
            }

            if(!enchantments.equals("null")) {
                List<String> enchantmentsList = new ArrayList<>(Arrays.asList(enchantments.split("/")));

                for(String enchantment : enchantmentsList) {
                    String[] parts = enchantment.split(":");

                    Enchantment ench = Enchantment.getByName(parts[0]);
                    int enchLvl = Integer.parseInt(parts[1]);

                    item.addEnchantment(ench, enchLvl);
                    meta.addEnchant(ench, enchLvl, true);
                }
            }

            item.setItemMeta(meta);
        }

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
                createTableLB(trimmedID, "littleBP");

                Inventory inv = Bukkit.getServer().createInventory(p, main.names.getInt("LittleBackPack.Slots"), ChatColor.translateAlternateColorCodes('&', main.names.getString("LittleBackPack.Name")));

                try {
                    ResultSet rs = main.getResult("select * from littleBP_"+trimmedID);

                    assert rs != null;
                    while(rs.next()) {
                        inv.setItem(rs.getInt(4), load(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)));
                    }

                } catch(SQLException e1) {
                    e1.printStackTrace();
                }

                main.littleB.put(id, inv);
            }

            if(p.hasPermission("backpacks.normalBackPack")) {
                createTableLB(trimmedID, "normalBP");

                Inventory inv = Bukkit.getServer().createInventory(p, main.names.getInt("NormalBackPack.Slots"), ChatColor.translateAlternateColorCodes('&', main.names.getString("NormalBackPack.Name")));

                try {
                    ResultSet rs = main.getResult("select * from normalBP_"+trimmedID);

                    assert rs != null;
                    while(rs.next()) {
                        inv.setItem(rs.getInt(4),load(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)));
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
                createTableLB(trimmedID, "littleBP");

                main.update("delete from littleBP_"+trimmedID);

                for(int i = 0; i < main.names.getInt("LittleBackPack.Slots") ; i++) {
                    if(main.littleB.get(id).getItem(i) != null) {
                        save(main.littleB.get(id).getItem(i), trimmedID, "littleBP_", i);
                    }
                }
            }

            if(p.hasPermission("backpacks.normalBackPack")) {
                createTableLB(trimmedID, "normalBP");

                main.update("delete from normalBP_"+trimmedID);

                for(int i = 0; i < main.names.getInt("NormalBackPack.Slots") ; i++) {
                    if(main.normalB.get(id).getItem(i) != null) {
                        save(main.normalB.get(id).getItem(i), trimmedID, "normalBP_", i);
                    }
                }
            }

        }

    }

}
