package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.Crafting;
import at.michael1011.backpacks.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static at.michael1011.backpacks.Main.messages;
import static at.michael1011.backpacks.Main.prefix;

public class RightClick implements Listener {

    public RightClick(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void rightClickEvent(PlayerInteractEvent e) {
        Action action = e.getAction();

        if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = e.getItem();

            if(item != null) {
                String backPack = Crafting.items.get(item);

                if(backPack != null) {
                    Player p = e.getPlayer();

                    if(p.hasPermission("backpacks.use."+backPack)) {
                        Open.openOwner(p, p.getUniqueId(), backPack, item.getItemMeta().getDisplayName());

                    } else {
                        p.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                messages.getString("Help.noPermission")));
                    }

                }

            }

        }

    }

}
