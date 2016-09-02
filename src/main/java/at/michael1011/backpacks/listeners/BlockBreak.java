package at.michael1011.backpacks.listeners;

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
import org.bukkit.material.Crops;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Random;

import static at.michael1011.backpacks.Crafting.furnaceGui;
import static at.michael1011.backpacks.Crafting.items;
import static at.michael1011.backpacks.Main.messages;
import static at.michael1011.backpacks.Main.prefix;
import static at.michael1011.backpacks.listeners.EntityDeath.getBackPack;

public class BlockBreak implements Listener {

    public BlockBreak(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

     @EventHandler
    public void blockBreak(final BlockBreakEvent e) {
         final Player p = e.getPlayer();

         if(!p.getGameMode().equals(GameMode.CREATIVE)) {
             final Block block = e.getBlock();
             final Material material = block.getType();
             final Location location = block.getLocation();
             final World world = location.getWorld();

             if(material.equals(Material.IRON_ORE) || material.equals(Material.GOLD_ORE) ||
                     material.equals(Material.POTATO) || material.equals(Material.COAL_ORE)) {
                 for(Map.Entry<ItemStack, String> item : items.entrySet()) {
                     String backpack = getBackPack(item, p);

                     if(furnaceGui.containsKey(backpack)) {
                         e.setCancelled(true);
                         block.setType(Material.AIR);

                         if(furnaceGui.get(item.getValue()).equals("true")) {
                             final String trimmedID = p.getUniqueId().toString().replaceAll("-", "");

                             SQL.getResult("SELECT * FROM bp_furnaces WHERE uuid='"+trimmedID+"'", new SQL.Callback<ResultSet>() {
                                 @Override
                                 public void onSuccess(ResultSet rs) {
                                     try {
                                         if(rs.first()) {
                                             int amount = rs.getInt("coal");

                                             if(Boolean.valueOf(rs.getString("ores"))) {
                                                 if(!material.equals(Material.COAL_ORE)) {
                                                     if(amount > 0) {
                                                         SQL.query("UPDATE bp_furnaces SET coal="+String.valueOf(amount-1)+" WHERE uuid='"+trimmedID+"'",
                                                                 new SQL.Callback<Boolean>() {
                                                                     @Override
                                                                     public void onSuccess(Boolean rs) {
                                                                         smelt(e, material);
                                                                     }

                                                                     @Override
                                                                     public void onFailure(Throwable e) {}

                                                                 });

                                                     } else {
                                                         if(material.equals(Material.POTATO)) {
                                                             world.dropItem(location, new ItemStack(Material.POTATO_ITEM));

                                                         } else {
                                                             world.dropItem(location, new ItemStack(material));
                                                         }

                                                         p.sendMessage(prefix+ ChatColor.translateAlternateColorCodes('&',
                                                                 messages.getString("BackPacks.furnaceBackPack.noCoal")));

                                                     }

                                                 }

                                             } else {
                                                 world.dropItem(location, new ItemStack(material));
                                             }

                                             if(material.equals(Material.COAL_ORE)) {
                                                 if(Boolean.valueOf(rs.getString("autoFill"))) {
                                                     if(amount < 64) {
                                                         ExperienceOrb exp = location.getWorld().spawn(location, ExperienceOrb.class);
                                                         exp.setExperience(1);

                                                         SQL.query("UPDATE bp_furnaces SET coal="+String.valueOf(amount+random(1, 3))+" WHERE uuid='"+trimmedID+"'",
                                                                 new SQL.Callback<Boolean>() {
                                                                     @Override
                                                                     public void onSuccess(Boolean rs) {}

                                                                     @Override
                                                                     public void onFailure(Throwable e) {}

                                                                 });

                                                     } else {
                                                         ExperienceOrb exp = location.getWorld().spawn(location, ExperienceOrb.class);
                                                         exp.setExperience(1);

                                                         world.dropItem(location, new ItemStack(Material.COAL, random(1, 3)));
                                                     }

                                                 } else {
                                                     ExperienceOrb exp = location.getWorld().spawn(location, ExperienceOrb.class);
                                                     exp.setExperience(1);

                                                     world.dropItem(location, new ItemStack(Material.COAL, random(1, 3)));
                                                 }

                                             }

                                         } else {
                                             if(!material.equals(Material.COAL_ORE)) {
                                                 if(material.equals(Material.POTATO)) {
                                                     world.dropItem(location, new ItemStack(Material.POTATO_ITEM));

                                                 } else {
                                                     world.dropItem(location, new ItemStack(material));
                                                 }

                                                 p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                                                         messages.getString("BackPacks.furnaceBackPack.noCoal")));
                                             }

                                         }

                                     } catch (SQLException e1) {
                                         e1.printStackTrace();
                                     }

                                 }

                                 @Override
                                 public void onFailure(Throwable e) {}

                             });

                         } else {
                             smelt(e, material);
                         }

                         break;

                     }

                 }

             }

         }

    }


    private void smelt(BlockBreakEvent e, Material material) {
        Block block = e.getBlock();
        Location location = block.getLocation();

        switch (material) {
            case IRON_ORE:
                drop(Material.IRON_INGOT, 1, location);

                break;

            case GOLD_ORE:
                drop(Material.GOLD_INGOT, 1, location);

                break;

            case POTATO:
                Crops crops = (Crops) block.getState().getData();

                if(crops.getState().equals(CropState.RIPE)) {
                    drop(Material.BAKED_POTATO, random(1, 3), location);

                } else {
                    drop(Material.POTATO_ITEM, 1, location);
                }

                break;

        }

    }

    private void drop(Material material, int amount, Location location) {
        location.getWorld().dropItem(location, new ItemStack(material, amount));

        ExperienceOrb exp = location.getWorld().spawn(location, ExperienceOrb.class);
        exp.setExperience(1);
    }

    private int random(int min, int max) {
        Random rand = new Random();

        return rand.nextInt((max-min)+1)+min;
    }

}
