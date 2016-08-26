package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.SQL;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import static at.michael1011.backpacks.Crafting.slots;

public class Open {

    public static HashMap<Player, String> openInvs = new HashMap<>();

    public static void open(Player opener, UUID ownerID, String backPack, String name) {
        String trimmedID = ownerID.toString().replaceAll("-", "");

        if(SQL.checkTable("bp_"+backPack+"_"+trimmedID)) {
            ResultSet rs = SQL.getResult("select * from bp_"+backPack+"_"+trimmedID);

            if (rs != null) {
                try {
                    rs.beforeFirst();

                    Inventory inv = Bukkit.getServer().createInventory(opener, slots.get(backPack), name);

                    while(rs.next()) {
                        ItemStack item = new ItemStack(Material.valueOf(rs.getString("material")),
                                rs.getInt("amount"));

                        if(rs.getBoolean("hasItemMeta")) {
                            ItemMeta meta = item.getItemMeta();

                            meta.setDisplayName(rs.getString("name"));
                            meta.setLore(Arrays.asList(rs.getString("lore").split("~")));

                            item.setItemMeta(meta);
                        }

                        inv.setItem(rs.getInt("position"), item);
                    }

                    openInvs.put(opener, backPack);

                    opener.openInventory(inv);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        } else {
            SQL.query("CREATE TABLE IF NOT EXISTS bp_"+backPack+"_"+trimmedID+"(position INT(100), material VARCHAR(100), "+
                    "amount INT(100), hasItemMeta BOOLEAN, name VARCHAR(100), lore VARCHAR(100))");

            openInvs.put(opener, backPack);

            opener.openInventory(Bukkit.getServer().createInventory(opener, slots.get(backPack), name));
        }

    }

}
