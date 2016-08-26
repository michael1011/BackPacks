package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.SQL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static at.michael1011.backpacks.Crafting.slots;
import static at.michael1011.backpacks.listeners.Open.openInvs;

public class InventoryClose implements Listener {

    public InventoryClose(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    // todo: add enchantments and potions

    // FIXME: fix lore split

    @EventHandler(priority = EventPriority.HIGH)
    public void invCloseEvent(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();

        String trimmedID = p.getUniqueId().toString().replaceAll("-", "");

        String backPack = openInvs.get(p);

        if(backPack != null) {
            openInvs.remove(p);

            Inventory inv = e.getInventory();

            SQL.query("DROP TABLE IF EXISTS bp_"+backPack+"_"+trimmedID);

            SQL.query("CREATE TABLE IF NOT EXISTS bp_"+backPack+"_"+trimmedID+"(position INT(100), material VARCHAR(100), "+
                    "amount INT(100), hasItemMeta BOOLEAN, name VARCHAR(100), lore VARCHAR(100))");

            for(int i = 0; i < slots.get(backPack); i++) {
                ItemStack item = inv.getItem(i);

                if(item != null) {
                    Boolean hasItemMeta = item.hasItemMeta();
                    int itemMeta = 0;

                    String name = "";
                    String lore = "";

                    if(hasItemMeta) {
                        itemMeta = 1;

                        ItemMeta itemM = item.getItemMeta();

                        name = itemM.getDisplayName();

                        List<String> rawLore = itemM.getLore();

                        for(String aRawLore : rawLore) {
                            lore = lore+"~"+aRawLore;
                        }

                    }

                    SQL.query("INSERT INTO bp_"+backPack+"_"+trimmedID+" (position, material, amount, hasItemMeta, "+
                            "name, lore) values ('"+i+"', '"+item.getType().toString()+"', '"+item.getAmount()+"',"+
                            "'"+itemMeta+"', '"+name+"', '"+lore+"')");

                }

            }

        }

    }

}
