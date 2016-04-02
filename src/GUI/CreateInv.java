package GUI;

import main.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CreateInv implements Listener {

    // todo: mysql compatibility
    // todo: coal made of wood, lava bucket

    private main plugin;

    public CreateInv(main main) {
        this.plugin = main;
        this.plugin.getServer().getPluginManager().registerEvents(this, main);
    }

    private static void setItemM(ItemStack item, String name, String lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', main.GUI.getString("FurnaceBackPackGUI.Names."+name)));
        item.setItemMeta(meta);

        if(!lore.equals("null")) {
            String loreS = ChatColor.translateAlternateColorCodes('&', main.GUI.getString("FurnaceBackPackGUI.Names."+lore));
            List<String> loreL = new ArrayList<>(Arrays.asList(loreS.split("\n")));
            meta.setLore(loreL);
        }

        item.setItemMeta(meta);
    }

    public static void load(ConfigurationSection sec, Inventory inv) {
        ItemStack ores = new ItemStack(Material.IRON_ORE, 1);
        setItemM(ores, "OresN", "OresD");

        ItemStack oresT;

        ItemStack animals = new ItemStack(Material.COOKED_BEEF, 1);
        setItemM(animals, "MeatN", "MeatD");

        ItemStack animalsT;

        if(sec.getBoolean("ores")) {
            oresT = new ItemStack(Material.WOOL, 1, (byte) 5);
            setItemM(oresT, "Enabled", "null");
        } else {
            oresT = new ItemStack(Material.WOOL, 1, (byte) 14);
            setItemM(oresT, "Disabled", "null");
        }

        if(sec.getBoolean("animals")) {
            animalsT = new ItemStack(Material.WOOL, 1, (byte) 5);
            setItemM(animalsT, "Enabled", "null");
        } else {
            animalsT = new ItemStack(Material.WOOL, 1, (byte) 14);
            setItemM(animalsT, "Disabled", "null");
        }

        ItemStack greyG = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7);
        ItemMeta gMeta = greyG.getItemMeta();
        gMeta.setDisplayName(ChatColor.GRAY+"");
        greyG.setItemMeta(gMeta);

        for (int i = 0; i < 35 ; i++) {
            inv.setItem(i, greyG);
        }

        inv.setItem(11, ores);
        inv.setItem(15, animals);
        inv.setItem(20, oresT);
        inv.setItem(24, animalsT);
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        if(e.getPlayer().hasPermission("backpacks.furnaceBackPack")) {
            if(main.GUI.getBoolean("FurnaceBackPackGUI.Enable")) {
                Player p = e.getPlayer();
                UUID id = p.getUniqueId();

                if(!main.backpacks.contains("furnaceB."+id)) {
                    ConfigurationSection sec = main.backpacks.createSection("furnaceB."+id);

                    sec.set("ores", true);
                    sec.set("animals", true);

                    try {
                        main.backpacks.save(main.backpacksF);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                Inventory inv = Bukkit.getServer().createInventory(p, 36, ChatColor.translateAlternateColorCodes('&', main.names.getString("FurnaceBackPack.Name")));
                ConfigurationSection sec = main.backpacks.getConfigurationSection("furnaceB."+id);

                load(sec, inv);

                main.furnaceB.put(id, inv);
            }
        }
    }

    @EventHandler
    public void interact(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        UUID id = p.getUniqueId();

        if(p.hasPermission("backpacks.furnaceBackPack")) {
            if(e.getInventory().equals(main.furnaceB.get(p.getUniqueId()))) {
                ItemStack item = e.getCurrentItem();
                Inventory inv = e.getInventory();

                if(item != null) {
                    if(item.hasItemMeta()) {
                        if(item.getItemMeta().hasDisplayName()) {
                            e.setCancelled(true);

                            String path = "furnaceB."+id;

                            if(e.getSlot() == 20) {
                                if(main.backpacks.getBoolean(path+".ores")) {
                                    main.backpacks.set(path+".ores", false);

                                    item = new ItemStack(Material.WOOL, 1, (byte) 14);
                                    setItemM(item, "Disabled", "null");
                                    inv.setItem(20, item);

                                    try {
                                        main.backpacks.save(main.backpacksF);
                                    } catch(Exception e1) {
                                        e1.printStackTrace();
                                    }

                                } else {
                                    main.backpacks.set(path+".ores", true);

                                    item = new ItemStack(Material.WOOL, 1, (byte) 5);
                                    setItemM(item, "Enabled", "null");
                                    inv.setItem(20, item);

                                    try {
                                        main.backpacks.save(main.backpacksF);
                                    } catch(Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }

                            } else if(e.getSlot() == 24) {
                                if(main.backpacks.getBoolean(path+".animals")) {
                                    main.backpacks.set(path+".animals", false);

                                    item = new ItemStack(Material.WOOL, 1, (byte) 14);
                                    setItemM(item, "Disabled", "null");
                                    inv.setItem(24, item);

                                    try {
                                        main.backpacks.save(main.backpacksF);
                                    } catch(Exception e1) {
                                        e1.printStackTrace();
                                    }

                                } else {
                                    main.backpacks.set(path+".animals", true);

                                    item = new ItemStack(Material.WOOL, 1, (byte) 5);
                                    setItemM(item, "Enabled", "null");
                                    inv.setItem(24, item);

                                    try {
                                        main.backpacks.save(main.backpacksF);
                                    } catch(Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void openInv(InventoryOpenEvent e) {
        Player p = (Player) e.getPlayer();
        UUID id = p.getUniqueId();

        if(p.hasPermission("backpacks.furnaceBackPack")) {
            if(e.getInventory().equals(main.furnaceB.get(id))) {
                Inventory inv = e.getInventory();
                ConfigurationSection sec = main.backpacks.getConfigurationSection("furnaceB."+id);

                load(sec, inv);

                int fuel = sec.getInt("fuel")/64;

                if(fuel <= 9) {
                    for(int i = 0; i < fuel; i++) {
                        int per = i+1;

                        ItemStack g = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
                        ItemMeta gMeta = g.getItemMeta();
                        gMeta.setDisplayName(ChatColor.GREEN+""+per+per+"%");
                        g.setItemMeta(gMeta);

                        inv.setItem(26+1+i, g);
                    }
                } else {
                    fuel = 9;

                    for(int i = 0; i < fuel; i++) {
                        int per = i+1;

                        ItemStack g = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
                        ItemMeta gMeta = g.getItemMeta();
                        gMeta.setDisplayName(ChatColor.GREEN+""+per+per+"%");
                        g.setItemMeta(gMeta);

                        inv.setItem(26+1+i, g);
                    }
                }
            }
        }
    }

    @EventHandler
    public void close(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        UUID id = p.getUniqueId();

        if(p.hasPermission("backpacks.furnaceBackPack")) {
            Inventory inv = main.furnaceB.get(id);

            if(inv.getItem(35) != null) {
                ItemStack item = inv.getItem(35);

                if(item.getType().equals(Material.COAL)) {

                    int exists = main.backpacks.getInt("furnaceB."+id+".fuel");
                    main.backpacks.set("furnaceB."+id+".fuel", exists+item.getAmount()*9);

                    try {
                        main.backpacks.save(main.backpacksF);
                    } catch(Exception e1) {
                        e1.printStackTrace();
                    }

                    inv.setItem(35, new ItemStack(Material.AIR));
                }
            }
        }
    }

}
