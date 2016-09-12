package at.michael1011.backpacks.commads;

import at.michael1011.backpacks.Crafting;
import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.SQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static at.michael1011.backpacks.Crafting.itemsInverted;
import static at.michael1011.backpacks.Crafting.type;
import static at.michael1011.backpacks.Main.messages;
import static at.michael1011.backpacks.Main.prefix;
import static at.michael1011.backpacks.listeners.RightClick.getInv;

public class Open implements CommandExecutor {

    public Open(Main main) {
        PluginCommand command = main.getCommand("bpopen");

        command.setExecutor(this);
        command.setTabCompleter(new OpenCompleter());
    }

    // todo: allow console to see CraftingBackPack and FurnaceBackPack

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args) {
        if(sender.hasPermission("backpacks.open")) {
            if(args.length == 2) {
                final String backPack = args[0];
                final String finalType = type.get(backPack);
                final String player = args[1];

                final ItemStack item = itemsInverted.get(backPack);

                if(item != null) {
                    SQL.getResult("SELECT * FROM bp_users WHERE name='"+player+"'", new SQL.Callback<ResultSet>() {
                        @Override
                        public void onSuccess(ResultSet rs) {
                            try {
                                rs.beforeFirst();

                                if(rs.next()) {
                                    final String trimmedID = rs.getString("uuid");

                                    switch (finalType) {
                                        case "normal":
                                            if(sender instanceof Player) {
                                                final Player opener = (Player) sender;

                                                SQL.checkTable("bp_"+backPack+"_"+trimmedID, new SQL.Callback<Boolean>() {
                                                    @Override
                                                    public void onSuccess(Boolean rs) {
                                                        if(rs) {
                                                            SQL.getResult("SELECT * FROM bp_"+backPack+"_"+trimmedID, new SQL.Callback<ResultSet>() {
                                                                @Override
                                                                public void onSuccess(ResultSet rs) {
                                                                    opener.openInventory(getInv(rs, opener, backPack, item.getItemMeta().getDisplayName(), false, trimmedID));
                                                                }

                                                                @Override
                                                                public void onFailure(Throwable e) {}

                                                                });

                                                        } else {
                                                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                                                    messages.getString("Help.bpopen.hasNotUsedYet")
                                                                            .replaceAll("%player%", player).replaceAll("%backpack%", backPack)));
                                                        }

                                                    }

                                                    @Override
                                                    public void onFailure(Throwable e) {}

                                                });

                                            } else {
                                                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                                        messages.getString("Help.bpopen.onlyPlayers").replaceAll("%type%", finalType)));
                                            }

                                            break;

                                        case "ender":
                                            Player target = Bukkit.getPlayer(player);

                                            if(sender instanceof Player) {
                                                final Player opener = (Player) sender;

                                                if(target != null) {
                                                    opener.openInventory(target.getEnderChest());

                                                } else {
                                                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                                            messages.getString("Help.bpopen.hasNotUsedYet")
                                                                    .replaceAll("%player%", player).replaceAll("%backpack%", backPack)));
                                                }

                                            }  else {
                                                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                                        messages.getString("Help.bpopen.onlyPlayers").replaceAll("%type%", finalType)));
                                            }

                                            break;

                                        case "furnace":
                                            SQL.getResult("SELECT * FROM bp_furnaces WHERE uuid='"+trimmedID+"'", new SQL.Callback<ResultSet>() {
                                                @Override
                                                public void onSuccess(ResultSet rs) {
                                                    try {
                                                        rs.beforeFirst();

                                                        if(rs.next()) {
                                                            String ores = rs.getString("ores");
                                                            String food = rs.getString("food");
                                                            String autoFill = rs.getString("autoFill");

                                                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                                                    messages.getString("BackPacks.furnaceBackPack.open.title")
                                                                            .replaceAll("%target%", player).replaceAll("%backpack%", backPack)));

                                                            sender.sendMessage(prefix);

                                                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                                                    messages.getString("BackPacks.furnaceBackPack.open.ores")
                                                                            .replaceAll("%value%", getColor(Boolean.valueOf(ores))+ores)));

                                                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                                                    messages.getString("BackPacks.furnaceBackPack.open.food")
                                                                            .replaceAll("%value%", getColor(Boolean.valueOf(food))+food)));

                                                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                                                    messages.getString("BackPacks.furnaceBackPack.open.autoFill")
                                                                            .replaceAll("%value%", getColor(Boolean.valueOf(autoFill))+autoFill)));

                                                            sender.sendMessage(prefix);

                                                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                                                    messages.getString("BackPacks.furnaceBackPack.open.coal")
                                                                            .replaceAll("%value%", String.valueOf(rs.getInt("coal")))));

                                                        } else {
                                                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                                                    messages.getString("Help.bpopen.hasNotUsedYet")
                                                                            .replaceAll("%player%", player).replaceAll("%backpack%", backPack)));
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
                                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("Help.bpopen.craftingBackPack")));

                                            break;
                                    }

                                } else {
                                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                                messages.getString("Help.playerNotFound").replaceAll("%target%", player)));
                                }

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Throwable e) {}

                    });

                } else {
                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                            messages.getString("Help.backPackNotFound").replaceAll("%backpack%", backPack)));

                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                            messages.getString("Help.backPackNotFoundAvailable")
                                    .replaceAll("%backpacks%", Crafting.available.replaceAll(",", ", "))));
                }

            } else {
                Map<String, Object> syntaxError = messages.getConfigurationSection("Help.bpopen.syntaxError").getValues(true);

                for(Map.Entry<String, Object> error : syntaxError.entrySet()) {
                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', (String) error.getValue()));
                }

            }

        } else {
            sender.sendMessage(prefix+ ChatColor.translateAlternateColorCodes('&',
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
