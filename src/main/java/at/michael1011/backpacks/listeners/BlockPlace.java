package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import static at.michael1011.backpacks.Crafting.backPacksMap;
import static at.michael1011.backpacks.nbt.CheckItem.checkItem;

public class BlockPlace implements Listener {

    public BlockPlace(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockPlace(BlockPlaceEvent e) {
        String backPack = checkItem(e.getItemInHand());

        if (backPack != null) {
            if (backPacksMap.containsKey(backPack)) {
                e.setCancelled(true);
            }
        }

    }

}
