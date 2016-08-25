package at.michael1011.backpacks.commads;

import at.michael1011.backpacks.Crafting;
import at.michael1011.backpacks.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

import static at.michael1011.backpacks.Main.messages;
import static at.michael1011.backpacks.Main.prefix;

public class Give implements CommandExecutor {

    public void register(Main main) {
        main.getCommand("bpgive").setExecutor(this);
    }

    // todo: add autocompletion for players and backpacks

    // todo: write test

    private static final String path = "Help.bpgive.";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1) {
            if(sender instanceof Player) {
                giveBackPack(sender, (Player) sender, args[0]);

            } else {
                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                        messages.getString("Help.onlyPlayers")));

                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                        messages.getString(path+"onlyPlayersAlternative")));
            }

        } else if(args.length == 2) {
            Player target = Bukkit.getServer().getPlayer(args[1]);

            if(target != null) {
                giveBackPack(sender, target, args[0]);

            } else {
                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                        messages.getString("Help.playerNotFound").replaceAll("%target%", args[1])));
            }

        } else {
            Map<String, Object> syntaxError = messages.getConfigurationSection(path+"syntaxError").getValues(true);

            for(Map.Entry<String, Object> error : syntaxError.entrySet()) {
                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', (String) error.getValue()));
            }

        }

        return true;
    }

    private static void giveBackPack(CommandSender sender, Player target, String backpack) {
        ItemStack item = Crafting.items.get(backpack);

        if(item != null) {
            target.getInventory().addItem(item);

            String targetName = target.getName();

            if(!sender.getName().equals(targetName)) {
                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                        messages.getString(path+"gaveBackPack").replaceAll("%backpack%", backpack)
                                .replaceAll("%target%", targetName)));
            }

        } else {
            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                    messages.getString(path+"backPackNotFound").replaceAll("%backpack%", backpack)));
        }

    }

}
