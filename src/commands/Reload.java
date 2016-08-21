package commands;

import main.Main;
import main.Pref;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Reload implements CommandExecutor {

    public Main plugin;

    public Reload(Main Main) {
        this.plugin = Main;
        plugin.getCommand("bpreload").setExecutor(this);
    }

    public void reload(CommandSender sender) {
        try {
            Main.config.load(Main.configF);
            Main.names.load(Main.namesF);
            Main.backpacks.load(Main.backpacksF);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sender.sendMessage(pre+ChatColor.translateAlternateColorCodes('&', Main.config.getString("Reload")));
    }

    private String pre = Pref.p;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        String NoPermission = ChatColor.translateAlternateColorCodes('&', Main.config.getString("NoPermission"));

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
