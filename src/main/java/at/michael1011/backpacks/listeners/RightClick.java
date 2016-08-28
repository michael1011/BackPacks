package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.Crafting;
import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.SQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

import static at.michael1011.backpacks.Crafting.slots;
import static at.michael1011.backpacks.Main.messages;
import static at.michael1011.backpacks.Main.prefix;

public class RightClick implements Listener {

    static final HashMap<Player, String> openInvs = new HashMap<>();
    static final HashMap<Player, String[]> openInvsCommand = new HashMap<>();

    public RightClick(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void rightClickEvent(PlayerInteractEvent e) {
        Action action = e.getAction();

        if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            final ItemStack item = e.getItem();

            if(item != null) {
                final String backPack = Crafting.items.get(item);

                if(backPack != null) {
                    final Player p = e.getPlayer();

                    if(p.hasPermission("backpacks.use."+backPack)) {
                        final String trimmedID = p.getUniqueId().toString().replaceAll("-", "");

                        SQL.checkTable("bp_"+backPack+"_"+trimmedID, new SQL.Callback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean rs) {
                                if(rs) {
                                    SQL.getResult("select * from bp_"+backPack+"_"+trimmedID, new SQL.Callback<ResultSet>() {
                                        @Override
                                        public void onSuccess(ResultSet rs) {
                                            open(rs, p, backPack, item.getItemMeta().getDisplayName(), true, null);
                                        }

                                        @Override
                                        public void onFailure(Throwable e) {}

                                    });

                                } else {
                                    SQL.query("CREATE TABLE IF NOT EXISTS bp_"+backPack+"_"+trimmedID+"(position INT(100), material VARCHAR(100), "+
                                            "amount INT(100), hasItemMeta BOOLEAN, name VARCHAR(100), lore VARCHAR(100))", new SQL.Callback<Boolean>() {

                                        @Override
                                        public void onSuccess(Boolean rs) {
                                            openInvs.put(p, backPack);

                                            p.openInventory(Bukkit.getServer().createInventory(p, slots.get(backPack),
                                                    item.getItemMeta().getDisplayName()));
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

                    } else {
                        p.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                messages.getString("Help.noPermission")));
                    }

                }

            }

        }

    }

    public static void open(ResultSet rs, Player opener, String backPack, String name,
                            Boolean openerIsOwner, String ownerID) {
        if(rs != null) {
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

                rs.close();

                if(openerIsOwner) {
                    openInvs.put(opener, backPack);

                } else {
                    openInvsCommand.put(opener, new String[]{backPack, ownerID});
                }

                opener.openInventory(inv);

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

    }

}
