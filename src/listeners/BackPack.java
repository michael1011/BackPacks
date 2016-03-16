package listeners;

import main.Pref;
import main.main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public class BackPack implements Listener {

    public main plugin;

    public BackPack(main main) {
        this.plugin = main;
        plugin.getServer().getPluginManager().registerEvents(this, main);
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

                }

            }
        }
    }
}
