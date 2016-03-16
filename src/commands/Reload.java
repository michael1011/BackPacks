package commands;

import main.main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import main.Pref;

public class Reload implements CommandExecutor {

    public main plugin;

    public Reload(main main) {
        this.plugin = main;
        plugin.getCommand("bpreload").setExecutor(this);
    }

    public void reload(CommandSender sender) {
        try {
            main.config.load(main.configF);
            main.names.load(main.namesF);
            main.backpacks.load(main.backpacksF);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sender.sendMessage(pre+ChatColor.translateAlternateColorCodes('&', main.config.getString("Reload")));
    }

    String pre = Pref.p;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        String NoPermission = ChatColor.translateAlternateColorCodes('&', main.config.getString("NoPermission"));

        if(args.length == 0) {
            if(sender.hasPermission("backpacks.reload")) {
                reload(sender);
            } else {
                sender.sendMessage(pre+NoPermission);
            }

        } else {
            if(sender.hasPermission("backpacks.reload")) {
                reload(sender);
            } else {
                sender.sendMessage(pre+NoPermission);
            }
        }

        return true;
    }
}
