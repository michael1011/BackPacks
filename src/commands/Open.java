package commands;

import listeners.SaveLoadSQL;
import main.Main;
import main.Pref;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static main.Main.*;
import static main.Pref.p;

public class Open implements CommandExecutor {

    public Open(Main main) {
        main.getCommand("bpopen").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        String pre = p;

        if(sender.hasPermission("backpacks.open")) {
            if(sender instanceof Player) {
                if(args.length == 2) {
                    try {
                        ResultSet rs = getResult("SELECT * FROM bp_users WHERE name='"+args[1]+"'");

                        assert rs != null;

                        rs.beforeFirst();

                        if(rs.next()) {
                            switch(args[0]) {
                                case "little":
                                    if(littleB.get(UUID.fromString(rs.getString("uuid"))) == null) {
                                        String trimmedID = rs.getString("uuid").replaceAll("-", "");

                                        SaveLoadSQL.createTableLB(trimmedID, "littleBP");

                                        Inventory inv = Bukkit.getServer().createInventory(null, names.getInt("LittleBackPack.Slots"), ChatColor.translateAlternateColorCodes('&', names.getString("LittleBackPack.Name")));

                                        try {
                                            ResultSet rs1 = getResult("select * from littleBP_"+trimmedID);

                                            assert rs1 != null;
                                            while(rs1.next()) {
                                                inv.setItem(rs1.getInt(4), SaveLoadSQL.load(rs1.getString(1), rs1.getInt(2), rs1.getInt(3), rs1.getString(5), rs1.getString(6), rs1.getString(7), rs1.getString(8)));
                                            }

                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }

                                        littleB.put(UUID.fromString(rs.getString("uuid")), inv);
                                    }

                                    ((Player) sender).openInventory(littleB.get(UUID.fromString(rs.getString("uuid"))));

                                    break;

                                case "normal":
                                    if(normalB.get(UUID.fromString(rs.getString("uuid"))) == null) {
                                        String trimmedID = rs.getString("uuid").replaceAll("-", "");

                                        SaveLoadSQL.createTableLB(trimmedID, "normalBP");

                                        Inventory inv = Bukkit.getServer().createInventory(null, names.getInt("NormalBackPack.Slots"), ChatColor.translateAlternateColorCodes('&', names.getString("NormalBackPack.Name")));

                                        try {
                                            ResultSet rs1 = getResult("select * from normalBP_"+trimmedID);

                                            assert rs1 != null;
                                            while(rs1.next()) {
                                                inv.setItem(rs1.getInt(4), SaveLoadSQL.load(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)));
                                            }

                                        } catch(SQLException e) {
                                            e.printStackTrace();
                                        }

                                        normalB.put(UUID.fromString(rs.getString("uuid")), inv);
                                    }

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
