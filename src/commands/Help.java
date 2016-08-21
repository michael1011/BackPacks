package commands;

import main.Pref;
import main.main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Help implements CommandExecutor {

    public main plugin;

    public Help(main main) {
        this.plugin = main;
        plugin.getCommand("bp").setExecutor(this);
    }

    private void help(CommandSender sender) {
        sender.sendMessage(Pref.p+ ChatColor.translateAlternateColorCodes('&', main.config.getString("Usage")));
        sender.sendMessage(Pref.p+ ChatColor.translateAlternateColorCodes('&', main.config.getString("BPHelp.1")));
        sender.sendMessage(Pref.p+ ChatColor.translateAlternateColorCodes('&', main.config.getString("BPHelp.2")));
        sender.sendMessage(Pref.p+ ChatColor.translateAlternateColorCodes('&', main.config.getString("BPHelp.3")));
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
