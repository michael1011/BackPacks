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

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args) {
        if(sender.hasPermission("backpacks.getInv")) {
            if(sender instanceof Player) {
                if(args.length == 2) {
                    final String backPack = args[0];
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

                                        final Player opener = (Player) sender;

                                        switch (type.get(backPack)) {
                                            case "normal":
                                                SQL.checkTable("bp_"+backPack+"_"+trimmedID, new SQL.Callback<Boolean>() {
                                                    @Override
                                                    public void onSuccess(Boolean rs) {
                                                        if(rs) {
                                                            SQL.getResult("select * from bp_"+backPack+"_"+trimmedID, new SQL.Callback<ResultSet>() {
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

                                                break;

                                            case "ender":
                                                Player target = Bukkit.getPlayer(player);

                                                opener.openInventory(target.getEnderChest());

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
                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                        messages.getString("Help.onlyPlayers")));
            }

        } else {
            sender.sendMessage(prefix+ ChatColor.translateAlternateColorCodes('&',
                    messages.getString("Help.noPermission")));
        }

        return true;
    }

}
