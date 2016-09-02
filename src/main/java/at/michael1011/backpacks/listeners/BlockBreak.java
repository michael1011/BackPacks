package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.SQL;
import org.bukkit.ChatColor;
import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.Material;
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

public class BlockBreak implements Listener {

    public BlockBreak(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

     @EventHandler
    public void blockBreak(final BlockBreakEvent e) {
         final Player p = e.getPlayer();

         final Block block = e.getBlock();
         final Material material = block.getType();

         if(material.equals(Material.IRON_ORE) || material.equals(Material.GOLD_ORE) ||
                 material.equals(Material.POTATO)) {
             for(Map.Entry<ItemStack, String> item : items.entrySet()) {
                 if(p.getInventory().contains(item.getKey())) {
                     if(furnaceGui.containsKey(item.getValue())) {
                         if(furnaceGui.get(item.getValue()).equals("true")) {
                             final String trimmedID = p.getUniqueId().toString().replaceAll("-", "");

                             SQL.getResult("SELECT * FROM bp_furnaces WHERE uuid='"+trimmedID+"'", new SQL.Callback<ResultSet>() {
                                 @Override
                                 public void onSuccess(ResultSet rs) {
                                     try {
                                         if(rs.first()) {
                                             if(Boolean.valueOf(rs.getString("ores"))) {
                                                 int amount = rs.getInt("coal");

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
                                                     e.setCancelled(true);
                                                     block.setType(Material.AIR);

                                                     if(!material.equals(Material.POTATO)) {
                                                         block.getLocation().getWorld().dropItem(block.getLocation(),
                                                                 new ItemStack(material));

                                                     } else {
                                                         block.getLocation().getWorld().dropItem(block.getLocation(),
                                                                 new ItemStack(Material.POTATO_ITEM));
                                                     }

                                                     p.sendMessage(prefix+ ChatColor.translateAlternateColorCodes('&',
                                                             messages.getString("BackPacks.furnaceBackPack.noCoal")));

                                                 }

                                             }

                                         } else {
                                             e.setCancelled(true);
                                             block.setType(Material.AIR);

                                             if(!material.equals(Material.POTATO)) {
                                                 block.getLocation().getWorld().dropItem(block.getLocation(),
                                                         new ItemStack(material));

                                             } else {
                                                 block.getLocation().getWorld().dropItem(block.getLocation(),
                                                         new ItemStack(Material.POTATO_ITEM));
                                             }

                                             p.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                                     messages.getString("BackPacks.furnaceBackPack.noCoal")));
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
