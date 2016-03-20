package commands;

import main.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import main.Pref;
import main.Crafting;

public class Give implements CommandExecutor {

    public main plugin;

    public Give(main main) {
        this.plugin = main;
        plugin.getCommand("bpgive").setExecutor(this);
    }

    private String pre = Pref.p;

    private void helpG(CommandSender sender) {
        sender.sendMessage(pre+ ChatColor.translateAlternateColorCodes('&', main.config.getString("Usage")));
        sender.sendMessage(pre+ChatColor.translateAlternateColorCodes('&', main.config.getString("BPGiveHelp.1")));
        sender.sendMessage(pre+ChatColor.translateAlternateColorCodes('&', main.config.getString("BPGiveHelp.2")));
        sender.sendMessage(pre);
        sender.sendMessage(pre+ChatColor.translateAlternateColorCodes('&', main.config.getString("BPGiveHelp.3")));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // todo: add enchanting backpack

        String NoPermission = ChatColor.translateAlternateColorCodes('&', main.config.getString("NoPermission"));
        String OnlyPlayers = ChatColor.translateAlternateColorCodes('&', main.config.getString("OnlyPlayers"));

        String gaveM = main.config.getString("GaveBackPack");
        String gaveO = ChatColor.translateAlternateColorCodes('&', main.config.getString("GaveBackPackOthers"));

        String littleBN = ChatColor.translateAlternateColorCodes('&', main.names.getString("LittleBackPack.Name"));
        String normalBN = ChatColor.translateAlternateColorCodes('&', main.names.getString("NormalBackPack.Name"));
        String enderBN = ChatColor.translateAlternateColorCodes('&', main.names.getString("EnderBackPack.Name"));
        String craftingBN = ChatColor.translateAlternateColorCodes('&', main.names.getString("WorkbenchBackPack.Name"));

        String notFound = ChatColor.translateAlternateColorCodes('&', main.config.getString("GiveNotFound"));

        if(args.length == 0) {
            helpG(sender);

        } else if(args.length == 1) {

            if (sender instanceof Player) {
                Player p = (Player) sender;

                if (p.hasPermission("backpacks.give")) {

                    if (args[0].equalsIgnoreCase("littlebp")) {
                        p.getInventory().addItem(Crafting.littleB);

                        gaveM = gaveM.replace("%backpack%", littleBN);
                        p.sendMessage(pre + ChatColor.translateAlternateColorCodes('&', gaveM));

                    } else if (args[0].equalsIgnoreCase("normalbp")) {
                        p.getInventory().addItem(Crafting.normalB);

                        gaveM = gaveM.replace("%backpack%", normalBN);
                        p.sendMessage(pre + ChatColor.translateAlternateColorCodes('&', gaveM));

                    } else if (args[0].equalsIgnoreCase("enderbp")) {
                        p.getInventory().addItem(Crafting.enderB);

                        gaveM = gaveM.replace("%backpack%", enderBN);
                        p.sendMessage(pre + ChatColor.translateAlternateColorCodes('&', gaveM));

                    } else if (args[0].equalsIgnoreCase("workbenchbp")) {
                        p.getInventory().addItem(Crafting.craftingB);

                        gaveM = gaveM.replace("%backpack%", craftingBN);
                        p.sendMessage(pre + ChatColor.translateAlternateColorCodes('&', gaveM));

                    } else {
                        notFound = notFound.replace("%backpack%", args[0]);
                        p.sendMessage(pre+notFound);
                    }

                } else {
                    p.sendMessage(pre + NoPermission);
                }

            } else {
                sender.sendMessage(pre+OnlyPlayers);
            }

        } else if(args.length == 2) {

            if(sender.hasPermission("backpacks.give")) {
                Player target = Bukkit.getPlayerExact(args[1]);

                if(target != null) {

                    if (args[0].equalsIgnoreCase("littlebp")) {
                        target.getInventory().addItem(Crafting.littleB);

                        gaveO = gaveO.replace("%backpack%", littleBN);
                        gaveO = gaveO.replace("%target%", args[1]);

                        sender.sendMessage(pre+gaveO);

                    } else if (args[0].equalsIgnoreCase("normalbp")) {
                        target.getInventory().addItem(Crafting.normalB);

                        gaveO = gaveO.replace("%backpack%", normalBN);
                        gaveO = gaveO.replace("%target%", args[1]);

                        sender.sendMessage(pre+gaveO);

                    } else if (args[0].equalsIgnoreCase("enderbp")) {
                        target.getInventory().addItem(Crafting.enderB);

                        gaveO = gaveO.replace("%backpack%", enderBN);
                        gaveO = gaveO.replace("%target%", args[1]);

                        sender.sendMessage(pre+gaveO);

                    } else if (args[0].equalsIgnoreCase("workbenchbp")) {
                        target.getInventory().addItem(Crafting.craftingB);

                        gaveO = gaveO.replace("%backpack%", craftingBN);
                        gaveO = gaveO.replace("%target%", args[1]);

                        sender.sendMessage(pre+gaveO);

                    } else {
                        notFound = notFound.replace("%backpack%", args[0]);
                        sender.sendMessage(pre+notFound);
                    }

                } else {
                    String notFoundP = ChatColor.translateAlternateColorCodes('&', main.config.getString("GivePlayerNotFound"));
                    notFoundP = notFoundP.replace("%target%", args[1]);

                    sender.sendMessage(pre+notFoundP);
                }

            }

        } else {
            helpG(sender);
        }

        return true;
    }
}
