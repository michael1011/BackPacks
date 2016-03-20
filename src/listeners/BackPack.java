package listeners;

import main.Pref;
import main.main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;


public class BackPack implements Listener {

    public main plugin;

    public BackPack(main main) {
        this.plugin = main;
        plugin.getServer().getPluginManager().registerEvents(this, main);
    }

    private String oldBlock;
    private int x, z;

    private void offHand(Player p, String name, World w, Location player) {
        if(p.getInventory().getItemInOffHand().hasItemMeta()) {
            if(p.getInventory().getItemInOffHand().getItemMeta().hasDisplayName()) {
                if(p.getInventory().getItemInOffHand().getItemMeta().getDisplayName().equals(name)) {
                    w.getBlockAt(x, 1, z).setType(Material.getMaterial(oldBlock));
                }
            }
        }
    }

    @EventHandler
    public void Craft(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();
        String NoPermission = ChatColor.translateAlternateColorCodes('&', main.config.getString("NoPermission"));

        if (e.getItem() != null && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if(e.getItem().getItemMeta().hasDisplayName()) {

                String var = e.getItem().getItemMeta().getDisplayName();

                if(var.equals(ChatColor.translateAlternateColorCodes('&', main.names.getString("EnderBackPack.Name")))) {
                    if(p.hasPermission("backpacks.enderBackPack")) {
                        e.setCancelled(true);
                        p.openInventory(p.getEnderChest());

                    } else {
                        p.sendMessage(Pref.p+NoPermission);
                    }

                } else if(var.equals(ChatColor.translateAlternateColorCodes('&', main.names.getString("WorkbenchBackPack.Name")))) {
                    if(p.hasPermission("backpacks.workbenchBackPack")) {
                        e.setCancelled(true);
                        p.openWorkbench(null, true);

                    } else {
                        p.sendMessage(Pref.p+NoPermission);
                    }

                } else if(var.equals(ChatColor.translateAlternateColorCodes('&', main.names.getString("LittleBackPack.Name")))) {
                    if(p.hasPermission("backpacks.littleBackPack")) {
                        e.setCancelled(true);
                        p.openInventory(main.littleB.get(id));

                    } else {
                        p.sendMessage(Pref.p+NoPermission);
                    }

                } else if(var.equals(ChatColor.translateAlternateColorCodes('&', main.names.getString("NormalBackPack.Name")))) {
                    if(p.hasPermission("backpacks.normalBackPack")) {
                        e.setCancelled(true);
                        p.openInventory(main.normalB.get(id));

                    } else {
                        p.sendMessage(Pref.p+NoPermission);
                    }

                } else if(var.equals(ChatColor.translateAlternateColorCodes('&', main.names.getString("EnchantingBackPack.Name")))) {
                    if(p.hasPermission("backpacks.enchantingBackPack")) {
                        e.setCancelled(true);

                        World w = p.getWorld();
                        Location player = p.getLocation();

                        x = player.getBlockX();
                        z = player.getBlockZ();

                        Block change = w.getBlockAt(x, 1, z);
                        oldBlock = change.getType().toString();

                        change.setType(Material.ENCHANTMENT_TABLE);

                        Location ench = new Location(w, player.getBlockX(), 1, player.getBlockZ());

                        p.openEnchanting(ench, true);

                    } else {
                        p.sendMessage(Pref.p+NoPermission);
                    }
                }

            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void close(InventoryCloseEvent e) {

        Player p = (Player) e.getPlayer();

        if(p.hasPermission("backpacks.enchantingBackPack")) {
            UUID id = p.getUniqueId();

            String FullVersion = Bukkit.getBukkitVersion();
            String version = FullVersion.substring(0, 3);

            String enchName = ChatColor.translateAlternateColorCodes('&', main.names.getString("EnchantingBackPack.Name"));

            World w = p.getWorld();
            Location player = p.getLocation();

            if(version.equals("1.9")) {
                if(p.getInventory().getItemInMainHand().hasItemMeta()) {
                    if(p.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) {
                        if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(enchName)) {
                            w.getBlockAt(x, 1, z).setType(Material.getMaterial(oldBlock));

                        } else {
                            offHand(p, enchName, w, player);
                        }

                    } else {
                        offHand(p, enchName, w, player);
                    }

                } else {
                    offHand(p, enchName, w, player);
                }

            } else {
                if(p.getItemInHand().hasItemMeta()) {
                    if(p.getItemInHand().getItemMeta().hasDisplayName()) {
                        if(p.getItemInHand().getItemMeta().getDisplayName().equals(enchName)) {
                            w.getBlockAt(x, 1, z).setType(Material.getMaterial(oldBlock));
                        }
                    }
                }
            }

        }
    }

}
