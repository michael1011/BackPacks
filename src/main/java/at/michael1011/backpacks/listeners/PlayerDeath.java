package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.SQL;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.util.Map;

import static at.michael1011.backpacks.Crafting.items;
import static at.michael1011.backpacks.Crafting.type;
import static at.michael1011.backpacks.Main.config;
import static at.michael1011.backpacks.listeners.RightClick.getInv;
import static at.michael1011.backpacks.listeners.RightClick.openInvs;

public class PlayerDeath implements Listener {

    public PlayerDeath(Main main) {
        if(config.getBoolean("dropOnDeath")) {
            main.getServer().getPluginManager().registerEvents(this, main);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerDeath(final PlayerDeathEvent e) {
        if(!e.getKeepInventory()) {
            final Player p = e.getEntity();

            for(final Map.Entry<ItemStack, String> entry : items.entrySet()) {
                final ItemStack item = entry.getKey();

                if(p.getInventory().contains(item)) {
                    final String backPack = entry.getValue();

                    switch (type.get(backPack)) {
                        case "normal":
                            final String trimmedID = p.getUniqueId().toString().replaceAll("-", "");

                            SQL.getResult("SELECT * FROM bp_"+backPack+"_"+trimmedID,
                                    new SQL.Callback<ResultSet>() {
                                @Override
                                public void onSuccess(ResultSet rs) {
                                    SQL.query("DELETE FROM bp_"+backPack+"_"+trimmedID, new SQL.Callback<Boolean>() {
                                        @Override
                                        public void onSuccess(Boolean rs) {}

                                        @Override
                                        public void onFailure(Throwable e) {}

                                    });

                                    Inventory inv = getInv(rs, p, backPack, item.getItemMeta().getDisplayName(), true, null);

                                    openInvs.remove(p);

                                    dropInventory(inv.getContents(), p);
                                }

                                @Override
                                public void onFailure(Throwable e) {}

                            });

                            break;

                        case "ender":
                            Inventory inv = p.getEnderChest();

                            dropInventory(inv.getContents(), p);

                            inv.clear();

                            break;
                    }

                }
            }

        }

    }

    static void dropInventory(ItemStack[] content, Player p) {
        Location location = p.getLocation();
        World world = location.getWorld();

        for(ItemStack inBackPack : content) {
            if(inBackPack != null) {
                world.dropItemNaturally(location, inBackPack);
            }
        }
    }

}
