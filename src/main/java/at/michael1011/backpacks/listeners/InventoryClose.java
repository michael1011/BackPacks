package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.SQL;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

import java.util.List;
import java.util.Map;

import static at.michael1011.backpacks.Crafting.slots;
import static at.michael1011.backpacks.listeners.RightClick.*;

public class InventoryClose implements Listener {

    public InventoryClose(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void invCloseEvent(final InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();

        final String backPack = openInvs.get(p);
        final String[] backPackCommand = openInvsCommand.get(p);
        final String furnace = openFurnaces.get(p);

        final String trimmedID = p.getUniqueId().toString().replaceAll("-", "");

        if(backPack != null) {
            openInvs.remove(p);

            saveBackPack(backPack, trimmedID, e.getInventory());

        } else if(backPackCommand != null) {
            saveBackPack(backPackCommand[0], backPackCommand[1], e.getInventory());

        } else if(furnace != null) {
            openFurnaces.remove(p);
            openFurnacesInvs.remove(p);

            int amount = 0;

            ItemStack coal = e.getInventory().getItem(35);

            if(coal != null) {
                amount = coal.getAmount();
            }

            SQL.query("UPDATE bp_furnaces SET coal="+amount+" WHERE uuid='"+trimmedID+"'",
                    new SQL.Callback<Boolean>() {
                @Override
                public void onSuccess(Boolean rs) {}

                @Override
                public void onFailure(Throwable e) {}

            });
        }

    }

    private void saveBackPack(final String backPack, final String trimmedID, final Inventory inv) {
        SQL.query("DELETE FROM bp_"+backPack+"_"+trimmedID, new SQL.Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean bool) {
                for(int i = 0; i < slots.get(backPack); i++) {
                    ItemStack item = inv.getItem(i);

                    if(item != null) {
                        Boolean hasItemMeta = item.hasItemMeta();

                        String name = "";
                        String lore = "";
                        String potion = "";

                        String material = item.getType().toString();

                        if(hasItemMeta) {
                            ItemMeta itemM = item.getItemMeta();

                            if(itemM.hasDisplayName()) {
                                 name = itemM.getDisplayName();
                            }

                            List<String> rawLore = itemM.getLore();

                            StringBuilder builder = new StringBuilder();

                            if(itemM.hasLore()) {
                                for(String loreLine : rawLore) {
                                    builder.append(loreLine).append("~");
                                }

                                lore = builder.toString();
                            }

                        }

                        StringBuilder enchantments = new StringBuilder();

                        for(Map.Entry<Enchantment, Integer> enchantment : item.getEnchantments().entrySet()) {
                            enchantments.append(enchantment.getKey().getName()).append(":")
                                    .append(enchantment.getValue()).append("/");
                        }

                        if(material.toLowerCase().contains("potion")) {
                            PotionMeta potionM = (PotionMeta) item.getItemMeta();
                            PotionData potionD = potionM.getBasePotionData();

                            if(potionD != null) {
                                potion = potionD.getType().name()+"/"+potionD.isExtended()+"/"+potionD.isUpgraded();
                            }
                        }

                        SQL.query("INSERT INTO bp_"+backPack+"_"+trimmedID+" (position, material, durability, amount, "+
                                "name, lore, enchantments, potion) values ('"+i+"', '"+material+"', '"+item.getDurability()+"', "+
                                 "'"+item.getAmount()+"', '"+name+"', '"+lore+"', '"+enchantments.toString()+"', '"+potion+"')",
                                        new SQL.Callback<Boolean>() {

                                    @Override
                                    public void onSuccess(Boolean rs) {}

                                    @Override
                                    public void onFailure(Throwable e) {}

                        });

                    }

                }

            }

            @Override
            public void onFailure(Throwable e) {}

        });

    }


}
