package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.BackPack;
import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.SQL;
import org.bukkit.Bukkit;
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
import java.sql.SQLException;

import static at.michael1011.backpacks.Crafting.backPacksMap;
import static at.michael1011.backpacks.Main.config;
import static at.michael1011.backpacks.Main.getTrimmedId;
import static at.michael1011.backpacks.listeners.RightClick.getInv;
import static at.michael1011.backpacks.listeners.RightClick.openInvs;
import static at.michael1011.backpacks.nbt.CheckItem.checkItem;

public class PlayerDeath implements Listener {

    private boolean dropOnDeath, dropOnDeathAll;

    public PlayerDeath(Main main) {
        dropOnDeath = config.getBoolean("dropOnDeath");
        dropOnDeathAll = config.getBoolean("dropOnDeathAll");

        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerDeath(final PlayerDeathEvent e) {
        if (dropOnDeath) {
            final Player p = e.getEntity();

            if (!dropOnDeathAll) {
                if (!e.getKeepInventory()) {
                    for (final ItemStack invItem : p.getInventory().getContents()) {
                        if (invItem != null) {
                            String backPackString = checkItem(invItem);

                            if (backPackString != null) {
                                dropBackPack(backPacksMap.get(backPackString), p);
                            }

                        }

                    }

                }

            } else {
                SQL.getResult("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES", new SQL.Callback<ResultSet>() {

                    @Override
                    public void onSuccess(ResultSet rs) {
                        String trimmedId = getTrimmedId(p);

                        try {
                            while (rs.next()) {
                                String tableName = rs.getString("TABLE_NAME");

                                if (tableName.startsWith("bp_") && tableName.endsWith("_" + trimmedId)) {
                                    dropBackPack(backPacksMap.get(tableName.split("_")[1]), p);
                                }

                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Throwable e) {}

                });

            }

        }

    }

    private void dropBackPack(final BackPack backPack, final Player p) {
        if (backPack != null) {
            switch (backPack.getType().toString()) {
                case "normal":
                    final String trimmedID = getTrimmedId(p);

                    SQL.getResult("SELECT * FROM bp_" + backPack.getName() + "_" + trimmedID,
                            new SQL.Callback<ResultSet>() {

                                @Override
                                public void onSuccess(ResultSet rs) {
                                    final Inventory inv = getInv(rs, p, backPack, "", true, null);

                                    openInvs.remove(p);

                                    SQL.query("DELETE FROM bp_" + backPack.getName() + "_" + trimmedID, new SQL.Callback<Boolean>() {

                                        @Override
                                        public void onSuccess(Boolean rs) {
                                            if (inv != null) {
                                                dropInventory(inv.getContents(), p);
                                            }
                                        }

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

    private void dropInventory(ItemStack[] content, Player p) {
        Location location = p.getLocation();
        World world = location.getWorld();

        for (ItemStack inBackPack : content) {
            if (inBackPack != null) {
                world.dropItemNaturally(location, inBackPack);
            }
        }

    }

}
