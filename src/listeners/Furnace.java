package listeners;

import main.Crafting;
import main.Main;
import main.Pref;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

public class Furnace implements Listener {

    private Boolean MySQL = Main.config.getBoolean("MySQL.enable");

    private ResultSet rs;

    public Furnace(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    private int random(int min, int max) {
        Random rand = new Random();

        return rand.nextInt((max-min)+1)+min;
    }

    private void save(UUID id) {

        if(!MySQL) {
            String path = "furnaceB."+id+".";

            int exists = Main.backpacks.getInt(path+"fuel");
            Main.backpacks.set(path+"fuel", exists-1);

            try {
                Main.backpacks.save(Main.backpacksF);
            } catch(Exception e) {
                e.printStackTrace();
            }

        } else {
            String trimmedID = id.toString().replaceAll("-", "");

            try {
                ResultSet rs = Main.getResult("select * from furnaceBP_"+trimmedID);
                assert rs != null;

                if(rs.next()) {
                    int animals = rs.getInt(1);
                    int ores = rs.getInt(2);
                    int fuel = rs.getInt(3)-1;

                    Main.update("delete from furnaceBP_"+trimmedID);
                    Main.update("insert into furnaceBP_"+trimmedID+" (animals, ores, fuel) values ("+animals+", "+ores+", "+fuel+")");
                }

            } catch(SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean checkCoal(Player p, int i) {
        if(i > 0 || !Main.GUI.getBoolean("FurnaceBackPackGUI.Enable")) {
            return true;
        } else {
            // p.sendMessage(Pref.p+ChatColor.translateAlternateColorCodes('&', Main.GUI.getString("FurnaceBackPackGUI.IsEmpty")));

            TextComponent tc = new TextComponent();
            tc.setText(Pref.p+ChatColor.translateAlternateColorCodes('&', Main.GUI.getString("FurnaceBackPackGUI.IsEmpty")));
            tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ""));
            tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Help").create()));
            p.spigot().sendMessage(tc);

            return false;
        }
    }

    private void blocks(Player p, BlockBreakEvent e, UUID id, int fuel) {
        if(p.getGameMode() == GameMode.SURVIVAL) {
            if(p.getInventory().contains(Crafting.furnaceB)) {
                Block broke = e.getBlock();
                Material mat = broke.getType();

                World w = broke.getWorld();
                Location loc = broke.getLocation();

                if(mat.equals(Material.IRON_ORE)) {
                    if(checkCoal(p, fuel)) {
                        e.setCancelled(true);
                        broke.setType(Material.AIR);

                        ExperienceOrb exp = w.spawn(loc, ExperienceOrb.class);
                        exp.setExperience(1);

                        w.dropItem(loc, new ItemStack(Material.IRON_INGOT));

                        save(id);
                    }

                } else if(mat.equals(Material.GOLD_ORE)) {
                    if(checkCoal(p, fuel)) {
                        e.setCancelled(true);
                        broke.setType(Material.AIR);

                        ExperienceOrb exp = w.spawn(loc, ExperienceOrb.class);
                        exp.setExperience(2);

                        w.dropItem(loc, new ItemStack(Material.GOLD_INGOT));

                        save(id);
                    }

                } else if(mat.equals(Material.POTATO)) {
                    Crops crops = (Crops) broke.getState().getData();
                    if(crops.getState().equals(CropState.RIPE)) {
                        if(checkCoal(p, fuel)) {
                            e.setCancelled(true);
                            broke.setType(Material.AIR);

                            ExperienceOrb exp = w.spawn(loc, ExperienceOrb.class);
                            exp.setExperience(2);

                            w.dropItem(loc, new ItemStack(Material.BAKED_POTATO, random(1, 3)));

                            save(id);
                        }

                    }
                }
            }
        }
    }

    private void kill(Player p, EntityDeathEvent e, UUID id, int fuel) {
        String version = Main.version;

        if(p.getInventory().contains(Crafting.furnaceB)) {
            if (e.getEntity() instanceof Ageable) {
                Ageable killed = (Ageable) e.getEntity();
                EntityType type = killed.getType();

                World w = killed.getWorld();
                Location loc = killed.getLocation();

                int dXP = e.getDroppedExp();

                if(type.equals(EntityType.CHICKEN)) {
                    if(killed.isAdult()) {
                        if(checkCoal(p, fuel)) {
                            e.getDrops().clear();

                            e.setDroppedExp(dXP*2);
                            w.dropItem(loc, new ItemStack(Material.COOKED_CHICKEN, random(1, 3)));

                            int rand = random(0, 3);

                            if(rand != 0) {
                                w.dropItem(loc, new ItemStack(Material.FEATHER, rand));
                            }

                            save(id);
                        }
                    }

                } else if(type.equals(EntityType.COW)) {
                    if(killed.isAdult()) {
                        if(checkCoal(p, fuel)) {
                            e.getDrops().clear();

                            e.setDroppedExp(dXP*2);
                            w.dropItem(loc, new ItemStack(Material.COOKED_BEEF, random(1, 3)));

                            int rand = random(0, 1);

                            if(rand != 0) {
                                w.dropItem(loc, new ItemStack(Material.LEATHER, rand));
                            }

                            save(id);
                        }
                    }

                } else if(type.equals(EntityType.MUSHROOM_COW)) {
                    if(killed.isAdult()) {
                        if(checkCoal(p, fuel)) {
                            e.getDrops().clear();

                            e.setDroppedExp(dXP*2);
                            w.dropItem(loc, new ItemStack(Material.COOKED_BEEF, random(1, 3)));

                            int rand = random(0, 1);

                            if(rand != 0) {
                                w.dropItem(loc, new ItemStack(Material.LEATHER, rand));
                            }

                            save(id);
                        }
                    }

                } else if(type.equals(EntityType.PIG)) {
                    if(killed.isAdult()) {
                        if(checkCoal(p, fuel)) {
                            e.getDrops().clear();

                            e.setDroppedExp(dXP*2);
                            w.dropItem(loc, new ItemStack(Material.GRILLED_PORK, random(1 ,3)));

                            save(id);
                        }
                    }

                } else if(type.equals(EntityType.SHEEP)) {
                    if(killed.isAdult()) {
                        if(checkCoal(p, fuel)) {
                            e.getDrops().clear();

                            e.setDroppedExp(dXP*2);
                            w.dropItem(loc, new ItemStack(Material.COOKED_MUTTON, random(1, 2)));
                            w.dropItem(loc, new ItemStack(Material.WOOL, 1));

                            save(id);
                        }
                    }

                } else if(version.equals("1.9") ||version.equals("1.8")) {
                    if(type.equals(EntityType.RABBIT)) {
                        if (killed.isAdult()) {
                            if(checkCoal(p, fuel)) {
                                e.getDrops().clear();

                                e.setDroppedExp(dXP*2);

                                int rand = random(0, 10);

                                w.dropItem(loc, new ItemStack(Material.RABBIT_HIDE));

                                if(rand != 0) {
                                    w.dropItem(loc, new ItemStack(Material.COOKED_RABBIT, rand));
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        if(e.getPlayer() != null) {
            Player p = e.getPlayer();
            UUID id = p.getUniqueId();

            if(Main.names.getBoolean("FurnaceBackPack.Enable")) {
                if(!Main.config.getBoolean("MySQL.enable")) {
                    if(Main.backpacks.getBoolean("furnaceB."+id+".ores")) {
                        blocks(p, e, id, Main.backpacks.getInt("furnaceB."+id+".fuel"));
                    }

                } else {
                    String trimmedID = id.toString().replaceAll("-", "");

                    try {
                        rs = Main.getResult("select * from furnaceBP_"+trimmedID);
                        assert rs != null;

                        if(rs.next()) {
                            if(rs.getInt(2) == 1) {
                                blocks(p, e, id, rs.getInt(3));
                            }
                        }

                    } catch(SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }

        }
    }

    @EventHandler
    public void entityKill(EntityDeathEvent e) {

        if(e.getEntity().getKiller() != null) {
            Player p = e.getEntity().getKiller();
            UUID id = p.getUniqueId();

            if(Main.names.getBoolean("FurnaceBackPack.Enable")) {
                if(!Main.config.getBoolean("MySQL.enable")) {
                    if(Main.backpacks.getBoolean("furnaceB."+id+".animals")) {
                        kill(p, e, id, Main.backpacks.getInt("furnaceB."+id+".fuel"));
                    }

                } else {
                    String trimmedID = id.toString().replaceAll("-", "");

                    try {
                        rs = Main.getResult("select * from furnaceBP_"+trimmedID);
                        assert rs != null;

                        if(rs.next()) {
                            if(rs.getInt(1) == 1) {
                                kill(p, e, id, rs.getInt(3));
                            }
                        }

                    } catch(SQLException e1) {
                        e1.printStackTrace();
                    }

                }
            }

        }
    }

}
