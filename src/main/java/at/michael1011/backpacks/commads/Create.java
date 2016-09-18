package at.michael1011.backpacks.commads;

import at.michael1011.backpacks.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static at.michael1011.backpacks.Main.config;
import static at.michael1011.backpacks.Main.messages;
import static at.michael1011.backpacks.Main.prefix;

public class Create implements CommandExecutor {

    private static final String path = "Help.bpcreate.";

    private static Main main;

    private static HashMap<CommandSender, HashMap<String, String>> data = new HashMap<>();

    public Create(Main main) {
        Create.main = main;

        PluginCommand command = main.getCommand("bpcreate");

        command.setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission("backpacks.create")) {
            if(args.length >= 2) {
                String arg = args[0];

                if(data.get(sender) == null) {
                    data.put(sender, new HashMap<String, String>());
                }

                switch (arg) {
                    case "name":
                        if(args.length == 2) {
                            data.get(sender).put("name", args[1]);

                            sendMap(sender, "steps.displayName");

                        } else {
                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.name.noSpaces")));
                        }

                        break;

                    case "displayname":
                        data.get(sender).put("displayname", argsToString(args));

                        sendMap(sender, "steps.description");

                        break;

                    case "description":
                        data.get(sender).put("description", argsToString(args));

                        sendMap(sender, "steps.material");

                        break;

                    case "material":
                        String material = argsToString(args).toUpperCase();

                        try {
                            Material.valueOf(material);

                            data.get(sender).put("material", material);

                            sendMap(sender, "steps.crafting");

                        } catch (IllegalArgumentException e1) {
                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.materialDoesNotExists")
                                            .replaceAll("%material%", material)));
                        }

                        break;

                    // fixme: make sure that crafting recipe and ingredients are valid

                    case "crafting":
                        data.get(sender).put("crafting", args[1]);

                        sendMap(sender, "steps.ingredients");

                        break;

                    case "ingredients":
                        data.get(sender).put("ingredients", args[1]);

                        sendMap(sender, "steps.type");

                        break;

                    case "type":
                        String type = argsToString(args);

                        switch (type) {
                            case "ender":
                            case "crafting":
                                data.get(sender).put("type", type);

                                sendMap(sender, "steps.finish");

                                break;

                            case "normal":
                                data.get(sender).put("type", type);

                                sendMap(sender, "steps.slots");

                                break;

                            case "furnace":
                                data.get(sender).put("type", type);

                                sendMap(sender, "steps.gui");

                                break;

                            default:
                                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                        messages.getString(path+"steps.typeNotFound").replaceAll("%type%", type)));
                                sendMap(sender, "steps.type");

                                break;
                        }

                        break;

                    case "slots":
                        try {
                            int slots = Integer.valueOf(argsToString(args));

                            if(slots % 9 == 0) {
                                data.get(sender).put("slots", String.valueOf(slots));

                                sendMap(sender, "steps.finish");

                            } else {
                                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                        messages.getString(path+"steps.slotsError")));
                            }

                        } catch (NumberFormatException e) {
                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.slotsError")));
                        }

                        break;

                    case "gui":
                        String value = argsToString(args);

                        switch (value) {
                            case "true":
                            case "false":
                                data.get(sender).put("gui", value);

                                sendMap(sender, "steps.finish");

                                break;

                            default:
                                sendMap(sender, "steps.gui");

                                break;
                        }

                        break;

                    case "finish":
                        // todo: add option to see preview of backpack

                        switch (args[1]) {
                            case "true":
                                HashMap<String, String> finishedData = data.get(sender);

                                String name = finishedData.get("name");
                                String finishedPath = "BackPacks."+name+".";

                                String finishedType = finishedData.get("type");

                                config.set(finishedPath+"name", finishedData.get("displayname"));
                                config.set(finishedPath+"type", finishedType);

                                String[] description = finishedData.get("description").split(";");

                                int descLine = 1;

                                for(String descriptionLine : description) {
                                    config.set(finishedPath+"description."+descLine, descriptionLine);

                                    descLine++;
                                }

                                switch (finishedType) {
                                    case "normal":
                                        config.set(finishedPath+"slots", Integer.valueOf(finishedData.get("slots")));

                                        break;

                                    case "furnace":
                                        config.set(finishedPath+"gui.enabled", finishedData.get("gui"));

                                        break;
                                }

                                config.set(finishedPath+"material", finishedData.get("material"));

                                String[] crafting = finishedData.get("crafting").split(";");

                                int line = 1;

                                for(String craftingLine : crafting) {
                                    config.set(finishedPath+"crafting."+line, craftingLine);

                                    line++;
                                }

                                String[] materials = finishedData.get("ingredients").split(";");

                                for(String materialLine : materials) {
                                    String[] parts = materialLine.split(":");

                                    config.set(finishedPath+"crafting.materials."+(parts[0].toUpperCase()),
                                            parts[1].toUpperCase());
                                }

                                // fixme: use number
                                config.set("BackPacks.enabled."+name, name);

                                try {
                                    config.save(new File(main.getDataFolder(), "config.yml"));

                                    Map<String, Object> syntaxError = messages.getConfigurationSection(path+"steps.finishTrue")
                                            .getValues(true);

                                    for(Map.Entry<String, Object> error : syntaxError.entrySet()) {
                                        sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                                String.valueOf(error.getValue()).replaceAll("%backpack%", name)));
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                break;

                            case "false":
                                String backpack = data.get(sender).get("name");

                                data.remove(sender);

                                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                        messages.getString(path+"steps.finishFalse")
                                                .replaceAll("%backpack%", backpack)));

                                break;

                            default:
                                sendMap(sender, "steps.finish");

                                break;
                        }

                        break;

                    default:
                        // todo: add error message

                        sendMap(sender, "syntaxError");

                        break;
                }

            } else {
                sendMap(sender, "syntaxError");
            }

        } else {
            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                    messages.getString("Help.noPermission")));
        }

        return true;
    }

    private String argsToString(String[] args) {
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < args.length; i++) {
            if(i == 1) {
                builder.append(args[i]);

            } else if(i > 1) {
                builder.append(" ").append(args[i]);
            }
        }

        return builder.toString();
    }

    private void sendMap(CommandSender sender, String specificPath) {
        Map<String, Object> syntaxError = messages.getConfigurationSection(path+specificPath).getValues(true);

        for(Map.Entry<String, Object> error : syntaxError.entrySet()) {
            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', (String) error.getValue()));
        }
    }

}
