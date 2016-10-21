package at.michael1011.backpacks.commads;

import at.michael1011.backpacks.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import static at.michael1011.backpacks.Crafting.availableList;
import static at.michael1011.backpacks.Main.messages;
import static at.michael1011.backpacks.Main.prefix;

public class ListBackPacks implements CommandExecutor {

    public ListBackPacks(Main main) {
        PluginCommand command = main.getCommand("bplist");

        command.setExecutor(this);
        command.setTabCompleter(new GiveCompleter());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission("backpacks.list") || sender.hasPermission("backpacks.*")) {
            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("Help.bplist.list").replaceAll("%backpacks%", getBackPacks())));

        } else {
            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                    messages.getString("Help.noPermission")));
        }

        return true;
    }

    private String getBackPacks() {
        StringBuilder backpacks = new StringBuilder("");

        for (String backpack : availableList) {
            if(backpacks.length() == 0) {
                backpacks.append(backpack);

            } else {
                backpacks.append(", ").append(backpack);
            }
        }

        return backpacks.toString();
    }

}
