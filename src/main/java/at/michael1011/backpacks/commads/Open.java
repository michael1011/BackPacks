package at.michael1011.backpacks.commads;

import at.michael1011.backpacks.BackPack;
import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.SQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static at.michael1011.backpacks.Crafting.backPacksItems;
import static at.michael1011.backpacks.Main.messages;
import static at.michael1011.backpacks.Main.prefix;
import static at.michael1011.backpacks.commads.ListBackPacks.getBackPacks;
import static at.michael1011.backpacks.listeners.RightClick.*;

public class Open implements CommandExecutor {

    public Open(Main main) {
        PluginCommand command = main.getCommand("bpopen");

        command.setExecutor(this);
        command.setTabCompleter(new OpenCompleter());
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args) {
        if (sender.hasPermission("backpacks.open") || sender.hasPermission("backpacks.*")) {
            if (args.length == 2) {
                final String target = args[1];

                BackPack backPack = null;
                ItemStack item = null;

                for (Map.Entry<BackPack, ItemStack> entry : backPacksItems.entrySet()) {
                    if (entry.getKey().getName().equals(args[0])) {
                        backPack = entry.getKey();
                        item = entry.getValue();
                    }

                }

                if (backPack != null) {
                    final BackPack finalBackPack = backPack;
                    final ItemStack finalItem = item;

                    SQL.getResult("SELECT * FROM bp_users WHERE name='" + target + "'",
                            new SQL.Callback<ResultSet>() {

                        @Override
                        public void onSuccess(ResultSet rs) {
                            try {
                                rs.beforeFirst();

                                if (rs.next()) {
                                    final String trimmedID = rs.getString("uuid");

                                    switch (finalBackPack.getType().toString()) {
                                        case "normal":
                                            if (sender instanceof Player) {
                                                final Player opener = (Player) sender;

                                                SQL.checkTable("bp_" + finalBackPack.getName() + "_"+ trimmedID,
                                                        new SQL.Callback<Boolean>() {

                                                    @Override
                                                    public void onSuccess(Boolean rs) {
                                                        if (rs) {
                                                            SQL.getResult("SELECT * FROM bp_" + finalBackPack.getName() + "_" + trimmedID,
                                                                    new SQL.Callback<ResultSet>() {

                                                                @Override
                                                                public void onSuccess(ResultSet rs) {
                                                                    Inventory open = getInv(rs, opener, finalBackPack, getInventoryTitle(finalBackPack, finalItem.getItemMeta().getDisplayName()),
                                                                            false, trimmedID);

                                                                    if(open != null) {
                                                                        playOpenSound(opener, finalBackPack);

                                                                        opener.openInventory(open);
                                                                    }

                                                                }

                                                                @Override
                                                                public void onFailure(Throwable e) {}

                                                                });

                                                        } else {
                                                            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                                                                    messages.getString("Help.bpopen.hasNotUsedYet")
                                                                            .replaceAll("%player%", target)
                                                                            .replaceAll("%backpack%", finalBackPack.getName())));
                                                        }

                                                    }

                                                    @Override
                                                    public void onFailure(Throwable e) {}

                                                });

                                            } else {
                                                sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                                                        messages.getString("Help.bpopen.onlyPlayers")
                                                                .replaceAll("%type%", finalBackPack.getType().toString())));
                                            }

                                            break;

                                        case "ender":
                                            Player targetPlayer = Bukkit.getPlayer(target);

                                            if(sender instanceof Player) {
                                                final Player opener = (Player) sender;

                                                if(targetPlayer != null) {
                                                    opener.openInventory(targetPlayer.getEnderChest());

                                                } else {
                                                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                                                            messages.getString("Help.bpopen.hasNotUsedYet")
                                                                    .replaceAll("%player%", target).replaceAll("%backpack%", finalBackPack.getName())));
                                                }

                                            }  else {
                                                sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                                                        messages.getString("Help.bpopen.onlyPlayers").replaceAll("%type%", finalBackPack.getType().toString())));
                                            }

                                            break;

                                        case "furnace":
                                            SQL.getResult("SELECT * FROM bp_furnaces WHERE uuid='" + trimmedID + "'", new SQL.Callback<ResultSet>() {
                                                @Override
                                                public void onSuccess(ResultSet rs) {
                                                    try {
                                                        rs.beforeFirst();

                                                        if(rs.next()) {
                                                            String ores = rs.getString("ores");
                                                            String food = rs.getString("food");
                                                            String autoFill = rs.getString("autoFill");

                                                            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                                                                    messages.getString("BackPacks.furnaceBackPack.open.title")
                                                                            .replaceAll("%target%", target).replaceAll("%backpack%", finalBackPack.getName())));

                                                            sender.sendMessage(prefix);

                                                            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                                                                    messages.getString("BackPacks.furnaceBackPack.open.ores")
                                                                            .replaceAll("%value%", getColor(Boolean.valueOf(ores)) + ores)));

                                                            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                                                                    messages.getString("BackPacks.furnaceBackPack.open.food")
                                                                            .replaceAll("%value%", getColor(Boolean.valueOf(food)) + food)));

                                                            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                                                                    messages.getString("BackPacks.furnaceBackPack.open.autoFill")
                                                                            .replaceAll("%value%", getColor(Boolean.valueOf(autoFill)) + autoFill)));

                                                            sender.sendMessage(prefix);

                                                            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                                                                    messages.getString("BackPacks.furnaceBackPack.open.coal")
                                                                            .replaceAll("%value%", String.valueOf(rs.getInt("coal")))));

                                                        } else {
                                                            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                                                                    messages.getString("Help.bpopen.hasNotUsedYet")
                                                                            .replaceAll("%player%", target).replaceAll("%backpack%", finalBackPack.getName())));
                                                        }

                                                        rs.close();

                                                    } catch (SQLException e) {
                                                        e.printStackTrace();
                                                    }

                                                }

                                                @Override
                                                public void onFailure(Throwable e) {}

                                            });

                                            break;

                                        case "crafting":
                                            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                                                    messages.getString("Help.bpopen.craftingBackPack")));

                                            break;
                                    }

                                } else {
                                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                                                messages.getString("Help.playerNotFound").replaceAll("%target%", target)));
                                }

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Throwable e) {}

                    });

                } else {
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                            messages.getString("Help.backPackNotFound").replaceAll("%backpack%", args[0])));

                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                            messages.getString("Help.backPackNotFoundAvailable")
                                    .replaceAll("%backpacks%", getBackPacks())));
                }

            } else {
                Map<String, Object> syntaxError = messages.getConfigurationSection("Help.bpopen.syntaxError").getValues(true);

                for (Map.Entry<String, Object> error : syntaxError.entrySet()) {
                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', (String) error.getValue()));
                }

            }

        } else {
            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                    messages.getString("Help.noPermission")));
        }

        return true;
    }


    private String getColor(Boolean bool) {
        if(bool) {
            return "&a";
        }

        return "&c";
    }

}
