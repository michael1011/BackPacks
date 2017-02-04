package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.SQL;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static at.michael1011.backpacks.Crafting.backPacksMap;
import static at.michael1011.backpacks.Main.*;
import static at.michael1011.backpacks.listeners.RightClick.*;
import static at.michael1011.backpacks.nbt.CheckItem.checkItem;

public class InventoryClick implements Listener {

    public InventoryClick(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void inventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (openFurnacesInvs.containsKey(p)) {
            Inventory inv = openFurnacesInvs.get(p);

            if (e.getInventory().equals(inv)) {
                ItemStack item = e.getCurrentItem();

                if (item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();

                    if (meta.hasDisplayName()) {
                        String name = meta.getDisplayName();
                        String trimmedId = getTrimmedId(p);

                        if (name.equals(ChatColor.translateAlternateColorCodes('&', furnaceGui.getString("enabled")))) {
                            ItemStack disabled = new ItemStack(Material.WOOL, 1, (byte) getColor(false));

                            setToggleMeta(disabled, false);

                            inv.setItem(e.getSlot(), disabled);

                            setBoolean(false, e.getSlot(), trimmedId);

                        } else if (name.equals(ChatColor.translateAlternateColorCodes('&', furnaceGui.getString("disabled")))) {
                            ItemStack enabled = new ItemStack(Material.WOOL, 1, (byte) getColor(true));

                            setToggleMeta(enabled, true);

                            inv.setItem(e.getSlot(), enabled);

                            setBoolean(true, e.getSlot(), trimmedId);
                        }

                        e.setCancelled(true);

                    } else {
                        checkCoal(item, e);
                    }

                } else {
                    checkCoal(item, e);
                }

            }

        } else if (!config.getBoolean("BackPackInBackPack")) {
            if (openInvs.containsKey(p)) {
                if (e.getClick().isKeyboardClick()) {
                    Inventory inv = p.getInventory();
                    int hotBarSlot = e.getHotbarButton();

                    try {
                        ItemStack item = inv.getItem(hotBarSlot);

                        if (itemIsBackPack(item)) {
                            e.setCancelled(true);
                        }

                    } catch (ArrayIndexOutOfBoundsException ignored) {}

                } else {
                    ItemStack[] items = {e.getCurrentItem(), e.getCursor()};

                    for (ItemStack item : items) {
                        if (itemIsBackPack(item)) {
                            e.setCancelled(true);

                            break;
                        }

                    }

                }

            }

        }

    }

    private boolean itemIsBackPack(ItemStack item) {
        if (item != null) {
            String backPack = checkItem(item);

            return backPacksMap.containsKey(backPack);
        }

        return false;
    }

    private void checkCoal(ItemStack item, InventoryClickEvent e) {
        if (item.getType().equals(Material.COAL)) {
            e.setCancelled(false);

            return;
        }

        e.setCancelled(true);
    }

    private void setBoolean(Boolean value, int slot, String uuid) {
        String name = null;

        if (slot == 12) {
            name = "ores";

        } else if (slot == 13) {
            name = "food";

        } else if (slot == 14) {
            name = "autoFill";
        }

        SQL.query("UPDATE bp_furnaces SET "+name+"='"+String.valueOf(value)+"' WHERE uuid='"+uuid+"'",
                new SQL.Callback<Boolean>() {

            @Override
            public void onSuccess(Boolean rs) {}

            @Override
            public void onFailure(Throwable e) {}

        });

    }

}
