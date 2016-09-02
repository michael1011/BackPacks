package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.SQL;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Random;

import static at.michael1011.backpacks.Crafting.furnaceGui;
import static at.michael1011.backpacks.Crafting.items;
import static at.michael1011.backpacks.Main.messages;
import static at.michael1011.backpacks.Main.prefix;

public class BlockBreak implements Listener {

    private Random random = new Random();

    public BlockBreak(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

     @EventHandler
    public void blockBreak(final BlockBreakEvent e) {
         final Player p = e.getPlayer();

         final Material material = e.getBlock().getType();

         for(Map.Entry<ItemStack, String> item : items.entrySet()) {
             if(p.getInventory().contains(item.getKey())) {
                 if(furnaceGui.containsKey(item.getValue())) {
                     if(furnaceGui.get(item.getValue()).equals("true")) {
                         final String trimmedID = p.getUniqueId().toString().replaceAll("-", "");

                         e.setCancelled(true);
                         e.getBlock().setType(Material.AIR);

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
                                                                 smelt(e, material, p);
                                                             }

                                                             @Override
                                                             public void onFailure(Throwable e) {}

                                                         });

                                             } else {
                                                 p.sendMessage(prefix+ ChatColor.translateAlternateColorCodes('&',
                                                         messages.getString("BackPacks.furnaceBackPack.noCoal")));

                                             }

                                         }

                                     } else {
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
                         e.setCancelled(true);
                         e.getBlock().setType(Material.AIR);

                         smelt(e, material, p);
                     }

                 }
             }

         }
    }

    private void smelt(BlockBreakEvent e, Material material, Player p) {
        Location location = e.getBlock().getLocation();

        if(material.equals(Material.IRON_ORE)) {
            drop(Material.IRON_INGOT, 1, location);

        } else if(material.equals(Material.GOLD_ORE)) {
            drop(Material.GOLD_ORE, 1, location);
        }


    }

    private void drop(Material material, int amount, Location location) {
        location.getWorld().dropItem(location, new ItemStack(material, amount));

        ExperienceOrb exp = location.getWorld().spawn(location, ExperienceOrb.class);
        exp.setExperience(1);
    }

}
