package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.BackPack;
import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.SQL;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Crops;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import static at.michael1011.backpacks.Crafting.backPacks;
import static at.michael1011.backpacks.Main.*;

public class BlockBreak implements Listener {

    public BlockBreak(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

     @EventHandler
    public void blockBreak(final BlockBreakEvent e) {
         final Player p = e.getPlayer();

         if (!p.getGameMode().equals(GameMode.CREATIVE)) {
             final Block block = e.getBlock();
             final Material material = block.getType();
             final Location location = block.getLocation();
             final World world = location.getWorld();

             Crops crops = null;

             if (material.equals(Material.POTATO)) {
                 crops = (Crops) block.getState().getData();
             }

             switch (material) {
                 case IRON_ORE:
                 case GOLD_ORE:
                 case POTATO:
                 case COAL_ORE:
                     ItemStack[] inv = p.getInventory().getContents();

                     for (BackPack backPack : backPacks) {
                         if (backPack.getType().equals(BackPack.Type.furnace)) {

                             for (ItemStack item : inv) {
                                 ItemMeta meta = item.getItemMeta();

                                 if (meta.hasLore()) {
                                     if (backPack.getLore().equals(meta.getLore())) {
                                         e.setCancelled(true);
                                         block.setType(Material.AIR);

                                         if (backPack.getFurnaceGui()) {
                                             final String trimmedID = getTrimmedId(p);

                                             final Crops finalCrops = crops;

                                             SQL.getResult("SELECT * FROM bp_furnaces WHERE uuid='" + trimmedID + "'",
                                                     new SQL.Callback<ResultSet>() {

                                                 @Override
                                                 public void onSuccess(ResultSet rs) {
                                                     try {
                                                         if (rs.first()) {
                                                             int amount = rs.getInt("coal");

                                                             if (material.equals(Material.COAL_ORE)) {
                                                                 if (Boolean.valueOf(rs.getString("autoFill"))) {
                                                                     if(amount < 64) {
                                                                         ExperienceOrb exp = location.getWorld().spawn(location, ExperienceOrb.class);
                                                                         exp.setExperience(1);

                                                                         SQL.query("UPDATE bp_furnaces SET coal=" + String.valueOf(amount+random()) + " WHERE uuid='" + trimmedID + "'",
                                                                                 new SQL.Callback<Boolean>() {

                                                                             @Override
                                                                             public void onSuccess(Boolean rs) {}

                                                                             @Override
                                                                             public void onFailure(Throwable e) {}

                                                                         });

                                                                     } else {
                                                                         ExperienceOrb exp = location.getWorld().spawn(location, ExperienceOrb.class);
                                                                         exp.setExperience(1);

                                                                         world.dropItem(location, new ItemStack(Material.COAL, random()));
                                                                     }

                                                                 } else {
                                                                     ExperienceOrb exp = location.getWorld().spawn(location, ExperienceOrb.class);
                                                                     exp.setExperience(1);

                                                                     world.dropItem(location, new ItemStack(Material.COAL, random()));
                                                                 }

                                                             } else if (material.equals(Material.POTATO)) {
                                                                 if (Boolean.valueOf(rs.getString("food"))) {
                                                                     checkCoal(amount, trimmedID, e, finalCrops, material, location, p);

                                                                 } else {
                                                                     world.dropItem(location, new ItemStack(Material.POTATO_ITEM, random()));
                                                                 }

                                                             } else if (Boolean.valueOf(rs.getString("ores"))) {
                                                                 checkCoal(amount, trimmedID, e, finalCrops, material, location, p);

                                                             } else {
                                                                 world.dropItem(location, new ItemStack(material));
                                                             }

                                                         } else {
                                                             if (!material.equals(Material.COAL_ORE)) {
                                                                 if (material.equals(Material.POTATO)) {
                                                                     world.dropItem(location, new ItemStack(Material.POTATO_ITEM));

                                                                 } else {
                                                                     world.dropItem(location, new ItemStack(material));
                                                                 }

                                                                 p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                                                                         messages.getString("BackPacks.furnaceBackPack.noCoal")));
                                                             }

                                                         }

                                                     } catch (SQLException e) {
                                                         e.printStackTrace();
                                                     }

                                                 }

                                                 @Override
                                                 public void onFailure(Throwable e) {}

                                             });

                                         } else {
                                             smelt(e.getBlock(), crops, material);
                                         }

                                         break;

                                     }

                                 }

                             }

                         }

                     }

                     break;

             }

         }

    }

    private static void checkCoal(int amount, String trimmedID, final BlockBreakEvent e, final Crops finalCrops,
                                  final Material material, Location location, Player p) {

        if (amount > 0) {
            SQL.query("UPDATE bp_furnaces SET coal=" + String.valueOf(amount-1) + " WHERE uuid='" + trimmedID + "'",
                    new SQL.Callback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean rs) {
                            smelt(e.getBlock(), finalCrops, material);
                        }

                        @Override
                        public void onFailure(Throwable e) {}

            });

        } else {
            if (!material.equals(Material.POTATO)) {
                location.getWorld().dropItem(location, new ItemStack(material));

            } else {
                location.getWorld().dropItem(location, new ItemStack(Material.POTATO_ITEM, random()));
            }

            p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                    messages.getString("BackPacks.furnaceBackPack.noCoal")));

        }
    }

    private static void smelt(Block block, Crops crops, Material material) {
        Location location = block.getLocation();

        switch (material) {
            case IRON_ORE:
                drop(Material.IRON_INGOT, 1, location);

                break;

            case GOLD_ORE:
                drop(Material.GOLD_INGOT, 1, location);

                break;

            case POTATO:
                if (crops.getState().equals(CropState.RIPE)) {
                    drop(Material.BAKED_POTATO, random(), location);
                    drop(Material.POTATO_ITEM, 1, location);

                } else {
                    drop(Material.POTATO_ITEM, 1, location);
                }

                break;

        }

    }

    private static void drop(Material material, int amount, Location location) {
        location.getWorld().dropItem(location, new ItemStack(material, amount));

        ExperienceOrb exp = location.getWorld().spawn(location, ExperienceOrb.class);
        exp.setExperience(1);
    }

    private static int random() {
        Random rand = new Random();

        return rand.nextInt((3 - 1)+1)+ 1;
    }

}
