package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.SQL;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static at.michael1011.backpacks.Main.furnaceGui;
import static at.michael1011.backpacks.listeners.RightClick.*;

public class FurnaceGui implements Listener {

    public FurnaceGui(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void inventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        Inventory inv = openFurnacesInvs.get(p);

        if(inv != null) {
            if(e.getInventory().equals(inv)) {
                ItemStack currentItem = e.getCurrentItem();

                if(currentItem.hasItemMeta()) {
                    String name = currentItem.getItemMeta().getDisplayName();

                    if(name.equals(ChatColor.translateAlternateColorCodes('&', furnaceGui.getString("enabled")))) {
                        ItemStack disabled = new ItemStack(Material.WOOL, 1, (byte) getColor(false));

                        setToggleMeta(disabled, false);

                        inv.setItem(e.getSlot(), disabled);

                        setBoolean(false, e.getSlot(), p.getUniqueId().toString().replaceAll("-", ""));

                    } else if(name.equals(ChatColor.translateAlternateColorCodes('&', furnaceGui.getString("disabled")))) {
                        ItemStack enabled = new ItemStack(Material.WOOL, 1, (byte) getColor(true));

                        setToggleMeta(enabled, true);

                        inv.setItem(e.getSlot(), enabled);

                        setBoolean(true, e.getSlot(), p.getUniqueId().toString().replaceAll("-", ""));
                    }

                    e.setCancelled(true);

                } else {
                    if(currentItem.getType().equals(Material.COAL) ||
                            e.getCursor().getType().equals(Material.COAL)) {
                        e.setCancelled(false);

                        return;
                    }

                    e.setCancelled(true);
                }

            }
        }

    }

    private void setBoolean(Boolean toggle, int slot, String uuid) {
        String name = null;

        if(slot == 12) {
            name = "ores";

        } else if(slot == 13) {
            name = "food";

        } else if(slot == 14) {
            name = "autoFill";
        }

        SQL.query("UPDATE bp_furnaces SET "+name+"='"+String.valueOf(toggle)+"' WHERE uuid='"+uuid+"'", new SQL.Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean rs) {}

            @Override
            public void onFailure(Throwable e) {}

        });

    }

}
