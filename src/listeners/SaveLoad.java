package listeners;

import main.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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

import java.util.Map;
import java.util.UUID;

public class SaveLoad implements Listener {

    public main plugin;

    public SaveLoad(main main) {
        this.plugin = main;
        this.plugin.getServer().getPluginManager().registerEvents(this, main);
    }

    private static void noPotion(ItemStack stack, ConfigurationSection sec) {
        if(stack.getItemMeta().hasEnchants()) {
            for (Enchantment enchantment : stack.getEnchantments().keySet()) {
                sec.set("enchantments."+enchantment.getName()+".lvl", stack.getEnchantmentLevel(enchantment));
            }
        }

        if(stack.hasItemMeta()) {
            if(stack.getItemMeta().hasDisplayName()) {
                sec.set("name", stack.getItemMeta().getDisplayName());
            }
            if(stack.getItemMeta().hasLore()) {
                sec.set("lore", stack.getItemMeta().getLore());
            }
        }
    }

    @SuppressWarnings("deprecation")
    private static void saveP_1_8(ItemStack stack, ConfigurationSection sec) {
        Potion potion = Potion.fromItemStack(stack);

        if(potion.getType().getEffectType() != null) {
            sec.set("potion.type", potion.getType().toString());
            sec.set("potion.level", potion.getLevel());
            sec.set("potion.splash", potion.isSplash());

            if(!potion.isSplash()) {
                sec.set("potion.dur", potion.hasExtendedDuration());

            } else {
                sec.set("potion.dur", false);
            }
        }
    }

    public static void save(ConfigurationSection sec, ItemStack stack, int slot) {
        Material type = stack.getType();

        sec.set("type", type.name());
        sec.set("amount", stack.getAmount());
        sec.set("dur", stack.getDurability());
        sec.set("slot", slot);

        if(type == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta book = (EnchantmentStorageMeta) stack.getItemMeta();
            Map<Enchantment, Integer> enchants = book.getStoredEnchants();

            for (Enchantment enchantment : enchants.keySet()) {
                sec.set("enchantments." + enchantment.getName() + ".lvl", book.getStoredEnchantLevel(enchantment));
            }

        } else if(main.version.equals("1.9")) {
            if(type == Material.POTION || type == Material.SPLASH_POTION || type == Material.LINGERING_POTION || type == Material.TIPPED_ARROW) {
                PotionMeta potion = (PotionMeta) stack.getItemMeta();

                if(potion.getBasePotionData().getType().getEffectType() != null) {
                    PotionData pData = potion.getBasePotionData();

                    sec.set("potion.type", pData.getType().toString());
                    sec.set("potion.extended", pData.isExtended());
                    sec.set("potion.upgraded", pData.isUpgraded());
                }

                // todo: add custom potions

            } else {
                noPotion(stack, sec);
            }

        } else {
            if(type == Material.POTION) {
                saveP_1_8(stack, sec);
            } else {
                noPotion(stack, sec);
            }
        }
    }

    public static ItemStack load(ConfigurationSection sec) {
        Short dur = (short) sec.getLong("dur");
        String type = sec.getString("type");

        ItemStack item = new ItemStack(Material.valueOf(sec.getString("type")));
        item.setAmount(sec.getInt("amount"));
        item.setDurability(dur);

        if(type.equals("ENCHANTED_BOOK")) {
            EnchantmentStorageMeta enchantments = (EnchantmentStorageMeta) item.getItemMeta();

            for (String enchStr : sec.getConfigurationSection("enchantments").getKeys(false)) {
                Enchantment ench = Enchantment.getByName(enchStr);
                int lvl = sec.getInt("enchantments." + enchStr + ".lvl");

                enchantments.addStoredEnchant(ench, lvl, true);
            }

            item.setItemMeta(enchantments);

        } else if(type.equals("POTION") || type.equals("SPLASH_POTION") || type.equals("LINGERING_POTION") || type.equals("TIPPED_ARROW")) {
            if(main.version.equals("1.9")) {
                if(sec.getString("potion.type") != null) {
                    PotionMeta potionM = (PotionMeta) item.getItemMeta();

                    PotionData potionD = new PotionData(PotionType.valueOf(sec.getString("potion.type")), sec.getBoolean("potion.extended"), sec.getBoolean("potion.upgraded"));

                    potionM.setBasePotionData(potionD);
                    item.setItemMeta(potionM);
                }

            } else {
                @SuppressWarnings("deprecation")
                Potion potion = new Potion(PotionType.valueOf(sec.getString("potion.type")), sec.getInt("potion.level"), sec.getBoolean("potion.splash"), sec.getBoolean("potion.dur"));

                item = potion.toItemStack(sec.getInt("amount"));
            }

        } else {
            ItemMeta itemM = item.getItemMeta();

            if (sec.get("enchantments") != null) {
                for (String enchantmentStr : sec.getConfigurationSection("enchantments").getKeys(false)) {
                    Enchantment ench = Enchantment.getByName(enchantmentStr);
                    int level = sec.getInt("enchantments." + enchantmentStr + ".lvl");

                    item.addEnchantment(ench, level);
                    itemM.addEnchant(ench, level, true);
                }
            }

            if (sec.getString("name") != null) {
                itemM.setDisplayName(sec.getString("name"));
            }

            if (sec.getString("lore") != null) {
                itemM.setLore(sec.getStringList("lore"));
            }

            item.setItemMeta(itemM);
        }

        return new ItemStack(item);
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        if(!main.config.getBoolean("MySQL.enable")) {
            Player p = e.getPlayer();
            UUID id = p.getUniqueId();

            if(p.hasPermission("backpacks.littleBackPack")) {
                Inventory inv = Bukkit.getServer().createInventory(p, main.names.getInt("LittleBackPack.Slots"), ChatColor.translateAlternateColorCodes('&', main.names.getString("LittleBackPack.Name")));

                if(main.backpacks.contains("littleB."+id)) {
                    for(String item : main.backpacks.getConfigurationSection("littleB."+id).getKeys(false)) {
                        ConfigurationSection sec = main.backpacks.getConfigurationSection("littleB."+id+"."+item);
                        inv.setItem(sec.getInt("slot") ,load(sec));
                    }
                }

                main.littleB.put(id, inv);
            }

            if(p.hasPermission("backpacks.normalBackPack")) {
                Inventory inv = Bukkit.getServer().createInventory(p, main.names.getInt("NormalBackPack.Slots"), ChatColor.translateAlternateColorCodes('&', main.names.getString("NormalBackPack.Name")));

                if(main.backpacks.contains("normalB."+id)) {
                    for(String item : main.backpacks.getConfigurationSection("normalB."+id).getKeys(false)) {
                        ConfigurationSection sec = main.backpacks.getConfigurationSection("normalB."+id+"."+item);
                        inv.setItem(sec.getInt("slot"),load(sec));
                    }
                }

                main.normalB.put(id, inv);
            }
        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        if(!main.config.getBoolean("MySQL.enable")) {
            Player p = e.getPlayer();
            UUID id = p.getUniqueId();

            if(p.hasPermission("backpacks.littleBackPack")) {
                for(int in = 0; in < main.names.getInt("LittleBackPack.Slots"); in++) {
                    if(main.backpacks.get("littleB."+id+"."+in) != null) {
                        main.backpacks.set("littleB."+id+"."+in, null);
                    }
                }

                if(!main.backpacks.contains("littleB."+id)) {
                    main.backpacks.createSection("littleB."+id);
                }

                for(int i = 0; i < main.names.getInt("LittleBackPack.Slots") ; i++) {
                    if(main.littleB.get(id).getItem(i) != null) {
                        save(main.backpacks.createSection("littleB."+id+"."+i), main.littleB.get(id).getItem(i), i);
                    }
                }
            }

            if(p.hasPermission("backpacks.normalBackPack")) {
                for(int in2 = 0; in2 < main.names.getInt("NormalBackPack.Slots"); in2++) {
                    if(main.backpacks.get("normalB."+id+"."+in2) != null) {
                        main.backpacks.set("normalB."+id+"."+in2, null);
                    }
                }

                if(!main.backpacks.contains("normalB."+id)) {
                    main.backpacks.createSection("normalB."+id);
                }

                for(int i = 0; i < main.names.getInt("NormalBackPack.Slots") ; i++) {
                    if(main.normalB.get(id).getItem(i) != null) {
                        save(main.backpacks.createSection("normalB."+id+"."+i), main.normalB.get(id).getItem(i), i);
                    }
                }
            }

            try {
                main.backpacks.save(main.backpacksF);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

}