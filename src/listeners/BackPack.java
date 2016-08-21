package listeners;

import main.Pref;
import main.main;
import net.minecraft.server.v1_9_R1.IChatBaseComponent;
import net.minecraft.server.v1_9_R1.PacketPlayOutOpenWindow;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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

    @EventHandler(priority = EventPriority.HIGH)
    public void Craft(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();
        String NoPermission = ChatColor.translateAlternateColorCodes('&', main.config.getString("NoPermission"));

        if (e.getItem() != null && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if(e.getItem().getItemMeta().hasDisplayName()) {

                String var = e.getItem().getItemMeta().getDisplayName();

                if(var.equals(ChatColor.translateAlternateColorCodes('&', main.names.getString("EnderBackPack.Name")))) {
                    e.setCancelled(true);
                    if(p.hasPermission("backpacks.enderBackPack")) {
                        p.openInventory(p.getEnderChest());

                    } else {
                        p.sendMessage(Pref.p+NoPermission);
                    }

                } else if(var.equals(ChatColor.translateAlternateColorCodes('&', main.names.getString("WorkbenchBackPack.Name")))) {
                    e.setCancelled(true);
                    if(p.hasPermission("backpacks.workbenchBackPack")) {
                        p.openWorkbench(null, true);

                    } else {
                        p.sendMessage(Pref.p+NoPermission);
                    }

                } else if(var.equals(ChatColor.translateAlternateColorCodes('&', main.names.getString("LittleBackPack.Name")))) {
                    e.setCancelled(true);
                    if(p.hasPermission("backpacks.littleBackPack")) {
                        p.openInventory(main.littleB.get(id));

                    } else {
                        p.sendMessage(Pref.p+NoPermission);
                    }

                } else if(var.equals(ChatColor.translateAlternateColorCodes('&', main.names.getString("NormalBackPack.Name")))) {
                    e.setCancelled(true);
                    if(p.hasPermission("backpacks.normalBackPack")) {
                        p.openInventory(main.normalB.get(id));

                    } else {
                        p.sendMessage(Pref.p+NoPermission);
                    }

                } else if(var.equals(ChatColor.translateAlternateColorCodes('&', main.names.getString("EnchantingBackPack.Name")))) {
                    e.setCancelled(true);
                    if(p.hasPermission("backpacks.enchantingBackPack")) {

                        EnchantingTable table = new EnchantingTable(p);

                        table.setTitle(main.names.getString("EnchantingBackPack.Name"));

                        table.open();

                    } else {
                        p.sendMessage(Pref.p+NoPermission);
                    }

                } else if(var.equals(ChatColor.translateAlternateColorCodes('&', main.names.getString("FurnaceBackPack.Name")))) {
                    e.setCancelled(true);
                    if(p.hasPermission("backpacks.furnaceBackPack")) {
                        if(main.names.getBoolean("FurnaceBackPack.Enable")) {
                            if(main.GUI.getBoolean("FurnaceBackPackGUI.Enable")) {
                                p.openInventory(main.furnaceB.get(id));
                            }
                        }
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

        /*if(p.hasPermission("backpacks.enchantingBackPack")) {
            UUID id = p.getUniqueId();

            String enchName = ChatColor.translateAlternateColorCodes('&', main.names.getString("EnchantingBackPack.Name"));

            World w = p.getWorld();
            Location player = p.getLocation();

            if(main.version.equals("1.9")) {
                if(p.getInventory().getItemInMainHand().hasItemMeta()) {
                    if(p.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) {
                        if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(enchName)) {
                            w.getBlockAt(x, -1, z).setType(Material.getMaterial(oldBlock));

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

        }*/
    }

}
