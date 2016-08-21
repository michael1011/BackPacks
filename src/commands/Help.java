package commands;

import main.Main;
import main.Pref;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Help implements CommandExecutor {

    public Main plugin;

    public Help(Main Main) {
        this.plugin = Main;
        plugin.getCommand("bp").setExecutor(this);
    }

    private void help(CommandSender sender) {
        sender.sendMessage(Pref.p+ ChatColor.translateAlternateColorCodes('&', Main.config.getString("Usage")));
        sender.sendMessage(Pref.p+ ChatColor.translateAlternateColorCodes('&', Main.config.getString("BPHelp.1")));
        sender.sendMessage(Pref.p+ ChatColor.translateAlternateColorCodes('&', Main.config.getString("BPHelp.2")));
        sender.sendMessage(Pref.p+ ChatColor.translateAlternateColorCodes('&', Main.config.getString("BPHelp.3")));
        sender.sendMessage(Pref.p+ ChatColor.translateAlternateColorCodes('&', Main.config.getString("BPHelp.4")));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(args.length == 0) {
            help(sender);

        } else {
            help(sender);
        }

        return true;
    }
}
