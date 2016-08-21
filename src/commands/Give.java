package commands;

import main.Crafting;
import main.Pref;
import main.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

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

        String NoPermission = ChatColor.translateAlternateColorCodes('&', main.config.getString("NoPermission"));
        String OnlyPlayers = ChatColor.translateAlternateColorCodes('&', main.config.getString("OnlyPlayers"));

        String gaveM = main.config.getString("GaveBackPack");
        String gaveO = ChatColor.translateAlternateColorCodes('&', main.config.getString("GaveBackPackOthers"));

        String littleBN = ChatColor.translateAlternateColorCodes('&', main.names.getString("LittleBackPack.Name"));
        String normalBN = ChatColor.translateAlternateColorCodes('&', main.names.getString("NormalBackPack.Name"));
        String enderBN = ChatColor.translateAlternateColorCodes('&', main.names.getString("EnderBackPack.Name"));
        String craftingBN = ChatColor.translateAlternateColorCodes('&', main.names.getString("WorkbenchBackPack.Name"));
        String enchantBN = ChatColor.translateAlternateColorCodes('&', main.names.getString("EnchantingBackPack.Name"));
        String furnaceBN = ChatColor.translateAlternateColorCodes('&', main.names.getString("FurnaceBackPack.Name"));

        String notFound = ChatColor.translateAlternateColorCodes('&', main.config.getString("GiveNotFound"));

        if(args.length == 0) {
            helpG(sender);

        } else if(args.length == 1) {

            if (sender instanceof Player) {
                Player p = (Player) sender;

                if (p.hasPermission("backpacks.give")) {

                    String bp = args[0];
                    Inventory inv = p.getInventory();

                    if(bp.equalsIgnoreCase("littlebp")) {
                        inv.addItem(Crafting.littleB);

                        gaveM = gaveM.replace("%backpack%", littleBN);
                        p.sendMessage(pre+ChatColor.translateAlternateColorCodes('&', gaveM));

                    } else if(bp.equalsIgnoreCase("normalbp")) {
                        inv.addItem(Crafting.normalB);

                        gaveM = gaveM.replace("%backpack%", normalBN);
                        p.sendMessage(pre+ChatColor.translateAlternateColorCodes('&', gaveM));

                    } else if(bp.equalsIgnoreCase("enderbp")) {
                        inv.addItem(Crafting.enderB);

                        gaveM = gaveM.replace("%backpack%", enderBN);
                        p.sendMessage(pre+ChatColor.translateAlternateColorCodes('&', gaveM));

                    } else if(bp.equalsIgnoreCase("workbenchbp")) {
                        inv.addItem(Crafting.craftingB);

                        gaveM = gaveM.replace("%backpack%", craftingBN);
                        p.sendMessage(pre+ChatColor.translateAlternateColorCodes('&', gaveM));

                    } else if(bp.equalsIgnoreCase("enchantingbp")) {
                        inv.addItem(Crafting.enchantingB);

                        gaveM = gaveM.replace("%backpack%", enchantBN);
                        p.sendMessage(pre + ChatColor.translateAlternateColorCodes('&', gaveM));

                    } else if(bp.equalsIgnoreCase("furnacebp")) {
                        inv.addItem(Crafting.furnaceB);

                        gaveM = gaveM.replace("%backpack%", furnaceBN);
                        p.sendMessage(pre + ChatColor.translateAlternateColorCodes('&', gaveM));

                    } else {
                        notFound = notFound.replace("%backpack%", bp);
                        p.sendMessage(pre+notFound);
                    }

                } else {
                    p.sendMessage(pre+NoPermission);
                }

            } else {
                sender.sendMessage(pre+OnlyPlayers);
            }

        } else if(args.length == 2) {

            if(sender.hasPermission("backpacks.give")) {
                Player target = Bukkit.getPlayerExact(args[1]);

                if(target != null) {

                    String bp = args[0];
                    Inventory inv = target.getInventory();

                    if(bp.equalsIgnoreCase("littlebp")) {
                        inv.addItem(Crafting.littleB);

                        gaveO = gaveO.replace("%backpack%", littleBN);
                        gaveO = gaveO.replace("%target%", args[1]);

                        sender.sendMessage(pre+gaveO);

                    } else if(bp.equalsIgnoreCase("normalbp")) {
                        inv.addItem(Crafting.normalB);

                        gaveO = gaveO.replace("%backpack%", normalBN);
                        gaveO = gaveO.replace("%target%", args[1]);

                        sender.sendMessage(pre+gaveO);

                    } else if(bp.equalsIgnoreCase("enderbp")) {
                        inv.addItem(Crafting.enderB);

                        gaveO = gaveO.replace("%backpack%", enderBN);
                        gaveO = gaveO.replace("%target%", args[1]);

                        sender.sendMessage(pre+gaveO);

                    } else if(bp.equalsIgnoreCase("workbenchbp")) {
                        inv.addItem(Crafting.craftingB);

                        gaveO = gaveO.replace("%backpack%", craftingBN);
                        gaveO = gaveO.replace("%target%", args[1]);

                        sender.sendMessage(pre+gaveO);

                    } else if(bp.equalsIgnoreCase("enchantingbp")) {
                        inv.addItem(Crafting.enchantingB);

                        gaveO = gaveO.replace("%backpack%", enchantBN);
                        gaveO = gaveO.replace("%target%", args[1]);

                        sender.sendMessage(pre+gaveO);

                    } else if(bp.equals("furnacebp")) {
                        inv.addItem(Crafting.furnaceB);

                        gaveO = gaveO.replace("%backpack%", furnaceBN);
                        gaveO = gaveO.replace("%target%", args[1]);

                        sender.sendMessage(pre+gaveO);

                    } else {
                        notFound = notFound.replace("%backpack%", bp);
                        sender.sendMessage(pre+notFound);
                    }

                } else {
                    String notFoundP = ChatColor.translateAlternateColorCodes('&', main.config.getString("GivePlayerNotFound"));
                    notFoundP = notFoundP.replace("%target%", args[1]);

                    sender.sendMessage(pre+notFoundP);
                }

            } else {
                sender.sendMessage(pre+NoPermission);
            }

        } else {
            helpG(sender);
        }

        return true;
    }
}
