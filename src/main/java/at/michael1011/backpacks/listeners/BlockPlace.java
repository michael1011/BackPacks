package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.BackPack;
import at.michael1011.backpacks.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.ItemMeta;

import static at.michael1011.backpacks.Crafting.backPacks;

public class BlockPlace implements Listener {

    public BlockPlace(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockPlace(BlockPlaceEvent e) {
        ItemMeta meta = e.getItemInHand().getItemMeta();

        if (meta.hasLore()) {
            for (BackPack backPack : backPacks) {
                if (backPack.getLore().equals(meta.getLore())) {
                    e.setCancelled(true);

                    break;
                }
            }
        }

    }

}
