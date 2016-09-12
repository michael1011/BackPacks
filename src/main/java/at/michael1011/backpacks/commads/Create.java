package at.michael1011.backpacks.commads;

import at.michael1011.backpacks.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.*;

import static at.michael1011.backpacks.Main.messages;
import static at.michael1011.backpacks.Main.prefix;

public class Create implements CommandExecutor {

    private static final String path = "Help.bpcreate.";

    private static HashMap<CommandSender, String> steps = new HashMap<>();
    private static HashMap<CommandSender, HashMap<String, String>> data = new HashMap<>();

    public Create(Main main) {
        PluginCommand command = main.getCommand("bpcreate");

        command.setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender.hasPermission("backpacks.create")) {
            if(args.length > 0) {
                if(steps.containsKey(sender)) {
                    if(args[0].equalsIgnoreCase("reset")) {
                        if(args.length == 2) {
                            if(args[1].equalsIgnoreCase("confirm")) {
                                steps.remove(sender);
                                data.remove(sender);

                                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString(path+"reset")));

                                Map<String, Object> syntaxError = messages.getConfigurationSection(path+"syntaxError").getValues(true);

                                for(Map.Entry<String, Object> error : syntaxError.entrySet()) {
                                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', (String) error.getValue()));
                                }

                                return true;
                            }
                        }
                    }

                    switch (steps.get(sender)) {
                        default:
                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString(path+"unknownError")));

                            break;
                    }

                } else {
                    steps.put(sender, "desc");

                    HashMap<String, String> initData = new HashMap<>();

                    initData.put("name", Arrays.toString(args).replace("[", "").replace("]", "").replaceAll(",", ""));

                    data.put(sender, initData);
                }

            } else {
                Map<String, Object> syntaxError = messages.getConfigurationSection(path+"syntaxError").getValues(true);

                for(Map.Entry<String, Object> error : syntaxError.entrySet()) {
                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', (String) error.getValue()));
                }
            }

        } else {
            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                    messages.getString("Help.noPermission")));
        }

        return true;
    }

}
