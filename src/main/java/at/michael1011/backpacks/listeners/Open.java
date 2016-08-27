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

class Open {

    static HashMap<Player, String> openInvs = new HashMap<>();

    static void open(final Player opener, UUID ownerID, final String backPack, final String name) {
        final String trimmedID = ownerID.toString().replaceAll("-", "");

        SQL.checkTable("bp_" + backPack + "_" + trimmedID, new SQL.Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean rs) {
                if(rs) {
                    SQL.getResult("select * from bp_"+backPack+"_"+trimmedID, new SQL.Callback<ResultSet>() {
                        @Override
                        public void onSuccess(ResultSet rs) {
                            if (rs != null) {
                                try {
                                    rs.beforeFirst();

                                    Inventory inv = Bukkit.getServer().createInventory(opener, slots.get(backPack), name);

                                    while (rs.next()) {
                                        ItemStack item = new ItemStack(Material.valueOf(rs.getString("material")),
                                                rs.getInt("amount"));

                                        if (rs.getBoolean("hasItemMeta")) {
                                            ItemMeta meta = item.getItemMeta();

                                            meta.setDisplayName(rs.getString("name"));
                                            meta.setLore(Arrays.asList(rs.getString("lore").split("~")));

                                            item.setItemMeta(meta);
                                        }

                                        inv.setItem(rs.getInt("position"), item);
                                    }

                                    rs.close();

                                    openInvs.put(opener, backPack);

                                    opener.openInventory(inv);

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable e) {
                            e.printStackTrace();
                        }
                    });


                } else {
                    SQL.query("CREATE TABLE IF NOT EXISTS bp_"+backPack+"_"+trimmedID+"(position INT(100), material VARCHAR(100), "+
                            "amount INT(100), hasItemMeta BOOLEAN, name VARCHAR(100), lore VARCHAR(100))", new SQL.Callback<Boolean>() {

                        @Override
                        public void onSuccess(Boolean rs) {
                            openInvs.put(opener, backPack);

                            opener.openInventory(Bukkit.getServer().createInventory(opener, slots.get(backPack), name));
                        }

                        @Override
                        public void onFailure(Throwable e) {

                        }
                    });

                }

            }

        @Override
        public void onFailure(Throwable e) {}

        });




    }

}
