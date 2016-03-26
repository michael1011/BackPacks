package listeners;

import main.main;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import main.Crafting;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class Furnace implements Listener {

    public main plugin;

    public Furnace(main main) {
        this.plugin = main;
        plugin.getServer().getPluginManager().registerEvents(this, main);
    }

    private int random(int min, int max) {
        // todo: fix that

        Random rand = new Random();

        return rand.nextInt((max-min)+1)+min;
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        if(e.getPlayer() != null) {
            Player p = e.getPlayer();

            if(p.getGameMode() == GameMode.SURVIVAL) {
                if(p.getInventory().contains(Crafting.furnaceB)) {
                    Block broke = e.getBlock();
                    Material mat = broke.getType();

                    World w = broke.getWorld();
                    Location loc = broke.getLocation();

                    if(mat.equals(Material.IRON_ORE)) {
                        e.setCancelled(true);
                        broke.setType(Material.AIR);

                        ExperienceOrb exp = w.spawn(loc, ExperienceOrb.class);
                        exp.setExperience(1);

                        w.dropItem(loc, new ItemStack(Material.IRON_INGOT));
                    } else if(mat.equals(Material.GOLD_ORE)) {
                        e.setCancelled(true);
                        broke.setType(Material.AIR);

                        ExperienceOrb exp = w.spawn(loc, ExperienceOrb.class);
                        exp.setExperience(2);

                        w.dropItem(loc, new ItemStack(Material.GOLD_INGOT));
                    }
                }
            }
        }
    }

    @EventHandler
    public void entityKill(EntityDeathEvent e) {
        if(e.getEntity().getKiller() != null) {
            Player p = e.getEntity().getKiller();

            if(p.getInventory().contains(Crafting.furnaceB)) {
                if (e.getEntity() instanceof Ageable) {
                    Ageable killed = (Ageable) e.getEntity();
                    EntityType type = killed.getType();

                    World w = killed.getWorld();
                    Location loc = killed.getLocation();

                    int dXP = e.getDroppedExp();

                    if(type.equals(EntityType.CHICKEN)) {
                        if(killed.isAdult()) {
                            e.getDrops().clear();

                            e.setDroppedExp(dXP*2);
                            w.dropItem(loc, new ItemStack(Material.COOKED_CHICKEN, random(1, 3)));

                            int rand = random(0, 3);

                            if(rand != 0) {
                                w.dropItem(loc, new ItemStack(Material.FEATHER, rand));
                            }
                        }

                    } else if(type.equals(EntityType.COW)) {
                        if(killed.isAdult()) {
                            e.getDrops().clear();

                            e.setDroppedExp(dXP*2);
                            w.dropItem(loc, new ItemStack(Material.COOKED_BEEF, random(1, 3)));

                            int rand = random(0, 3);

                            if(rand != 0) {
                                w.dropItem(loc, new ItemStack(Material.LEATHER, rand));
                            }
                        }

                    } else if(type.equals(EntityType.MUSHROOM_COW)) {
                        if(killed.isAdult()) {
                            e.getDrops().clear();

                            e.setDroppedExp(dXP*2);
                            w.dropItem(loc, new ItemStack(Material.COOKED_BEEF, random(1, 3)));

                            int rand = random(0, 3);

                            if(rand != 0) {
                                w.dropItem(loc, new ItemStack(Material.LEATHER, rand));
                            }
                        }

                    } else if(type.equals(EntityType.PIG)) {
                        if(killed.isAdult()) {
                            e.getDrops().clear();

                            e.setDroppedExp(dXP*2);
                            w.dropItem(loc, new ItemStack(Material.GRILLED_PORK, random(1 ,3)));
                        }

                    } else if(type.equals(EntityType.SHEEP)) {
                        if(killed.isAdult()) {
                            e.getDrops().clear();

                            e.setDroppedExp(dXP*2);
                            w.dropItem(loc, new ItemStack(Material.COOKED_MUTTON, random(1, 2)));
                            w.dropItem(loc, new ItemStack(Material.WOOL, 1));
                        }
                    }

                }
            }
        }
    }


    // todo: add animals

}
