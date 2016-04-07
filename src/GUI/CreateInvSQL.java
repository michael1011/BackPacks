package GUI;

import main.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CreateInvSQL implements Listener {

    // todo: fix crazy bug

    private int animals, ores, fuel;
    private ResultSet rs;
    private main plugin;

    public CreateInvSQL(main main) {
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

    public static void load(int oresB, int animalsB, Inventory inv) {
        ItemStack ores = new ItemStack(Material.IRON_ORE, 1);
        setItemM(ores, "OresN", "OresD");

        ItemStack oresT;

        ItemStack animals = new ItemStack(Material.COOKED_BEEF, 1);
        setItemM(animals, "MeatN", "MeatD");

        ItemStack animalsT;

        if(oresB == 0) {
            oresT = new ItemStack(Material.WOOL, 1, (byte) 14);
            setItemM(oresT, "Disabled", "null");
        } else {
            oresT = new ItemStack(Material.WOOL, 1, (byte) 5);
            setItemM(oresT, "Enabled", "null");
        }

        if(animalsB == 0) {
            animalsT = new ItemStack(Material.WOOL, 1, (byte) 14);
            setItemM(animalsT, "Disabled", "null");
        } else {
            animalsT = new ItemStack(Material.WOOL, 1, (byte) 5);
            setItemM(animalsT, "Enabled", "null");
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
                String trimmedID = id.toString().replaceAll("-", "");

                int animals = 1;
                int ores = 1;

                try {
                    main.update("create table if not exists furnaceBP_"+trimmedID+" (animals INT(10),ores INT(10),fuel BIGINT(255))");
                    rs = main.getResult("select * from furnaceBP_"+trimmedID);

                    Inventory inv = Bukkit.getServer().createInventory(p, 36, ChatColor.translateAlternateColorCodes('&', main.names.getString("FurnaceBackPack.Name")));

                    assert rs != null;

                    if(rs.next()) {
                        animals = rs.getInt(1);
                        ores = rs.getInt(2);

                    } else {
                        main.update("insert into furnaceBP_"+trimmedID+" (animals, ores, fuel) values ('1', '1', '0')");
                    }

                    load(ores, animals, inv);

                    main.furnaceB.put(id, inv);
                } catch(SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    @EventHandler
    public void interact(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        UUID id = p.getUniqueId();

        if(p.hasPermission("backpacks.furnaceBackPack")) {
            if(e.getInventory().equals(main.furnaceB.get(id))) {
                ItemStack item = e.getCurrentItem();
                Inventory inv = e.getInventory();

                if(item != null) {
                    if(item.hasItemMeta()) {
                        if(item.getItemMeta().hasDisplayName()) {
                            String trimmedID = id.toString().replaceAll("-", "");

                            e.setCancelled(true);

                            try {
                                rs = main.getResult("select * from furnaceBP_"+trimmedID);
                                assert rs != null;

                                if(rs.next()) {
                                    animals = rs.getInt(1);
                                    ores = rs.getInt(2);
                                    fuel = rs.getInt(3);
                                }

                            } catch(SQLException e1) {
                                e1.printStackTrace();
                            }

                            if(e.getSlot() == 20) {
                                if(ores == 1) {
                                    main.update("delete from furnaceBP_"+trimmedID);
                                    main.update("insert into furnaceBP_"+trimmedID+" (animals, ores, fuel) values ("+animals+",0,"+fuel+")");

                                    item = new ItemStack(Material.WOOL, 1, (byte) 14);
                                    setItemM(item, "Disabled", "null");
                                    inv.setItem(20, item);

                                } else {
                                    main.update("delete from furnaceBP_"+trimmedID);
                                    main.update("insert into furnaceBP_"+trimmedID+" (animals, ores, fuel) values ("+animals+",1,"+fuel+")");

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
                                if(animals == 1) {
                                    main.update("delete from furnaceBP_"+trimmedID);
                                    main.update("insert into furnaceBP_"+trimmedID+" (animals, ores, fuel) values (0,"+ores+","+fuel+")");

                                    item = new ItemStack(Material.WOOL, 1, (byte) 14);
                                    setItemM(item, "Disabled", "null");
                                    inv.setItem(24, item);

                                    try {
                                        main.backpacks.save(main.backpacksF);
                                    } catch(Exception e1) {
                                        e1.printStackTrace();
                                    }

                                } else {
                                    main.update("delete from furnaceBP_"+trimmedID);
                                    main.update("insert into furnaceBP_"+trimmedID+" (animals, ores, fuel) values (1,"+ores+","+fuel+")");

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

        if(p.hasPermission("backpacks.furnaceBackPack")) {
            UUID id = p.getUniqueId();

            if(e.getInventory().equals(main.furnaceB.get(id))) {
                String trimmedID = id.toString().replace("-", "");
                Inventory inv = e.getInventory();

                try {
                    rs = main.getResult("select * from furnaceBP_"+trimmedID);
                    assert rs != null;

                    if(rs.next()) {
                        load(rs.getInt(2), rs.getInt(1), inv);

                        int fuel = rs.getInt(3) / 64;

                        if (fuel <= 9) {
                            for (int i = 0; i < fuel; i++) {
                                int per = i + 1;

                                ItemStack g = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
                                ItemMeta gMeta = g.getItemMeta();
                                gMeta.setDisplayName(ChatColor.GREEN + "" + per + per + "%");
                                g.setItemMeta(gMeta);

                                inv.setItem(26 + 1 + i, g);
                            }
                        } else {
                            fuel = 9;

                            for (int i = 0; i < fuel; i++) {
                                int per = i + 1;

                                ItemStack g = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
                                ItemMeta gMeta = g.getItemMeta();
                                gMeta.setDisplayName(ChatColor.GREEN + "" + per + per + "%");
                                g.setItemMeta(gMeta);

                                inv.setItem(26 + 1 + i, g);
                            }
                        }
                    }

                } catch(SQLException e1) {
                    e1.printStackTrace();
                }

            }
        }
    }

    @EventHandler
    public void close(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();

        if(p.hasPermission("backpacks.furnaceBackPack")) {
            if(main.GUI.getBoolean("FurnaceBackPackGUI.Enable")) {
                UUID id = p.getUniqueId();
                String trimmedID = id.toString().replaceAll("-", "");

                Inventory inv = main.furnaceB.get(id);

                if(inv.getItem(35) != null) {
                    ItemStack item = inv.getItem(35);

                    if(item.getType().equals(Material.COAL)) {

                        try {
                            rs = main.getResult("select * from furnaceBP_"+trimmedID);
                            assert rs != null;

                            if(rs.next()) {
                                animals = rs.getInt(1);
                                ores = rs.getInt(2);
                            }

                            int exists = rs.getInt(3);
                            int fuel = exists+item.getAmount()*9;

                            main.update("delete from furnaceBP_"+trimmedID);
                            main.update("insert into furnaceBP_"+trimmedID+" (animals, ores, fuel) values ("+animals+", "+ores+", "+fuel+")");


                            inv.setItem(35, new ItemStack(Material.AIR));
                        } catch(SQLException e1) {
                            e1.printStackTrace();
                        }

                    }
                }
            }

        }
    }


}
