package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.BackPack;
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

import static at.michael1011.backpacks.Crafting.backPacksItems;
import static at.michael1011.backpacks.Main.config;
import static at.michael1011.backpacks.Main.getTrimmedId;
import static at.michael1011.backpacks.listeners.RightClick.getInv;
import static at.michael1011.backpacks.listeners.RightClick.openInvs;

public class PlayerDeath implements Listener {

    public PlayerDeath(Main main) {
        if (config.getBoolean("dropOnDeath")) {
            main.getServer().getPluginManager().registerEvents(this, main);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerDeath(final PlayerDeathEvent e) {
        if (!e.getKeepInventory()) {
            final Player p = e.getEntity();

            for (Map.Entry<BackPack, ItemStack> entry : backPacksItems.entrySet()) {
                final ItemStack item = entry.getValue();

                if (p.getInventory().contains(item)) {
                    final BackPack backPack = entry.getKey();

                    switch (backPack.getType().toString()) {
                        case "normal":
                            final String trimmedID = getTrimmedId(p);

                            SQL.getResult("SELECT * FROM bp_" + backPack.getName() + "_" + trimmedID,
                                    new SQL.Callback<ResultSet>() {

                                @Override
                                public void onSuccess(ResultSet rs) {
                                    Inventory inv = getInv(rs, p, backPack, item.getItemMeta().getDisplayName(), true, null);

                                    openInvs.remove(p);

                                    if (inv != null) {
                                        dropInventory(inv.getContents(), p);
                                    }

                                    SQL.query("DELETE FROM bp_" + backPack.getName() + "_" + trimmedID, new SQL.Callback<Boolean>() {

                                        @Override
                                        public void onSuccess(Boolean rs) {}

                                        @Override
                                        public void onFailure(Throwable e) {}

                                    });

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

    private static void dropInventory(ItemStack[] content, Player p) {
        Location location = p.getLocation();
        World world = location.getWorld();

        for (ItemStack inBackPack : content) {
            if (inBackPack != null) {
                world.dropItemNaturally(location, inBackPack);
            }
        }

    }

}
