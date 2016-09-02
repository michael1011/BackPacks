package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.SQL;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static at.michael1011.backpacks.Crafting.furnaceGui;
import static at.michael1011.backpacks.Crafting.items;
import static at.michael1011.backpacks.Main.messages;
import static at.michael1011.backpacks.Main.prefix;

public class EntityDeath implements Listener {

    public EntityDeath(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void entityDeath(final EntityDeathEvent e) {
        final Player p = e.getEntity().getKiller();

        if (p != null) {
            for(Map.Entry<ItemStack, String> item : items.entrySet()) {
                String backpack = getBackPack(item, p);

                if(furnaceGui.containsKey(backpack)) {
                    final Location loc = e.getEntity().getLocation();

                    final List<ItemStack> toDrop = new ArrayList<>();

                    for(ItemStack add : e.getDrops()) {
                        toDrop.add(add);
                    }

                    e.getDrops().clear();

                    if(furnaceGui.get(item.getValue()).equals("true")) {
                        final String trimmedID = p.getUniqueId().toString().replaceAll("-", "");

                        SQL.getResult("SELECT * FROM bp_furnaces WHERE uuid='"+trimmedID+"'", new SQL.Callback<ResultSet>() {
                            @Override
                            public void onSuccess(ResultSet rs) {
                                try {
                                    if(rs.first()) {
                                        if(Boolean.valueOf(rs.getString("food"))) {
                                            int amount = rs.getInt("coal");

                                            if(amount > 0) {
                                                SQL.query("UPDATE bp_furnaces SET coal="+String.valueOf(amount-1)+" WHERE uuid='"+trimmedID+"'",
                                                        new SQL.Callback<Boolean>() {
                                                            @Override
                                                            public void onSuccess(Boolean rs) {
                                                                smelt(toDrop, loc);
                                                            }

                                                            @Override
                                                            public void onFailure(Throwable e) {}

                                                });

                                            } else {
                                                for(ItemStack drop : toDrop) {
                                                    loc.getWorld().dropItem(loc, drop);
                                                }

                                                p.sendMessage(prefix+ ChatColor.translateAlternateColorCodes('&',
                                                        messages.getString("BackPacks.furnaceBackPack.noCoal")));
                                            }

                                        }

                                    } else {
                                        for(ItemStack drop : toDrop) {
                                            loc.getWorld().dropItem(loc, drop);
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
                        smelt(toDrop, loc);
                    }

                    break;

                }

            }

        }

    }

    private void smelt(List<ItemStack> toSmelt, Location loc) {
        World world = loc.getWorld();

        for(ItemStack smelt : toSmelt) {
            switch (smelt.getType()) {
                case RAW_CHICKEN:
                    world.dropItem(loc, new ItemStack(Material.COOKED_CHICKEN, smelt.getAmount()));

                    break;

                case RAW_BEEF:
                    world.dropItem(loc, new ItemStack(Material.COOKED_BEEF, smelt.getAmount()));

                    break;

                case PORK:
                    world.dropItem(loc, new ItemStack(Material.GRILLED_PORK, smelt.getAmount()));

                    break;

                case MUTTON:
                    world.dropItem(loc, new ItemStack(Material.COOKED_MUTTON, smelt.getAmount()));

                    break;

                case RABBIT:
                    world.dropItem(loc, new ItemStack(Material.COOKED_RABBIT, smelt.getAmount()));

                    break;

                default:
                    world.dropItem(loc, new ItemStack(smelt.getType(), smelt.getAmount()));

                    break;
            }
        }

    }

    static String getBackPack(Map.Entry<ItemStack, String> item, Player p) {
        String backPack = null;

        for(ItemStack contents : p.getInventory().getContents()) {
            if(contents.hasItemMeta()) {
                if(contents.getItemMeta().getLore() != null) {
                    if(contents.getItemMeta().getLore().equals(item.getKey().getItemMeta().getLore())) {
                        backPack = item.getValue();
                    }
                }
            }
        }

        return backPack;
    }

}
