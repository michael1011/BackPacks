package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.Crafting;
import at.michael1011.backpacks.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BlockPlace implements Listener {

    public BlockPlace(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockPlace(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();

        if(backPackExists(item.getItemMeta().getLore())) {
            e.setCancelled(true);
        }

    }

    static Boolean backPackExists(List<String> lore) {
        return Crafting.loreMap.containsKey(lore);
    }

}
