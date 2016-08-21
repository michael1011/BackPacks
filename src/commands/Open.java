package commands;

import main.Main;
import main.Pref;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Open implements CommandExecutor {

    public Open(Main main) {
        main.getCommand("bpopen").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        String pre = Pref.p;

        if(sender.hasPermission("backpacks.open")) {
            if(sender instanceof Player) {
                if(args.length == 2) {
                    try {
                        ResultSet rs = Main.getResult("SELECT * FROM bp_users WHERE name='"+args[1]+"'");

                        assert rs != null;

                        rs.beforeFirst();

                        if(rs.next()) {
                            switch(args[0]) {
                                case "little":
                                    ((Player) sender).openInventory(Main.littleB.get(UUID.fromString(rs.getString("uuid"))));

                                    break;

                                case "normal":
                                    ((Player) sender).openInventory(Main.normalB.get(UUID.fromString(rs.getString("uuid"))));

                                    break;

                                default:
                                    sender.sendMessage(pre+ChatColor.translateAlternateColorCodes('&',
                                            Main.config.getString("GiveNotFound").replace("%backpack%", args[0])));

                                    break;
                            }

                        } else {
                            sender.sendMessage(pre+ChatColor.translateAlternateColorCodes('&',
                                    Main.config.getString("GivePlayerNotFound").replace("%target%", args[1])));
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } else {
                    sender.sendMessage(pre+ChatColor.translateAlternateColorCodes('&',
                            Main.config.getString("OpenHelp")));
                }

            } else {
                sender.sendMessage(pre+ChatColor.translateAlternateColorCodes('&', Main.config.getString("OnlyPlayers")));
            }

            } else {
            sender.sendMessage(pre+ChatColor.translateAlternateColorCodes('&', Main.config.getString("NoPermission")));
        }

        return true;
    }

}
