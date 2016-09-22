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

import static at.michael1011.backpacks.Main.*;

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
                String arg = args[0].toLowerCase();

                if(data.get(sender) == null) {
                    data.put(sender, new HashMap<String, String>());
                }

                switch (arg) {
                    case "name":
                        String name = argsToString(args);

                        if(!name.contains(" ")) {
                            data.get(sender).put("name", args[1]);

                            sendMap(sender, "steps.displayName");

                        } else {
                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.name.noSpaces")));
                        }

                        break;

                    case "display":
                    case "displayname":
                        data.get(sender).put("displayname", argsToString(args));

                        sendMap(sender, "steps.description");

                        break;

                    case "desc":
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
                                    messages.getString(path+"steps.materialNotValid")
                                            .replaceAll("%material%", material)));
                        }

                        break;

                    case "crafting":
                        String recipe = argsToString(args);
                        String[] recipeSplit = recipe.split(";");

                        if(recipeSplit.length == 3) {
                            Boolean validCrafting = true;

                            for(String recipeLine : recipeSplit) {
                                if(recipeLine.length() != 5) {
                                    validCrafting = false;
                                }
                            }

                            if(validCrafting) {
                                data.get(sender).put("crafting", recipe);

                                sendMap(sender, "steps.materials");

                            } else {
                                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                        messages.getString(path+"steps.craftingNotValid")));

                                sender.sendMessage(prefix+"");

                                sendMap(sender, "steps.crafting");
                            }

                        } else {
                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.craftingNotValid")));

                            sender.sendMessage(prefix+"");

                            sendMap(sender, "steps.crafting");
                        }

                        break;

                    case "materials":
                        String materials = argsToString(args);
                        String[] materialsSplit = materials.split(";");

                        Boolean validMaterial = true;
                        Boolean validMaterials = true;

                        for(String materialPart : materialsSplit) {
                            if(materialPart.contains(":")) {
                                String[] parts = materialPart.split(":");

                                String part = parts[1].toUpperCase();

                                try {
                                    Material.valueOf(part);

                                } catch (IllegalArgumentException e) {
                                    validMaterial = false;

                                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                            messages.getString(path+"steps.materialNotValid")
                                                    .replaceAll("%material%", part)));
                                }

                            } else {
                                validMaterials = false;
                            }
                        }

                        if(validMaterials && validMaterial) {
                            data.get(sender).put("materials", materials);

                            sendMap(sender, "steps.type");

                        } else {
                            if(validMaterial) {
                                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                        messages.getString(path+"steps.materialsNotValid")));

                                sender.sendMessage(prefix+"");

                                sendMap(sender, "steps.materials");
                            }
                        }

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

                                sender.sendMessage(prefix+"");

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
                        try {
                            switch (args[1]) {
                                case "true":
                                    HashMap<String, String> finishedData = data.get(sender);

                                    String finishedName = finishedData.get("name");
                                    String finishedPath = "BackPacks."+finishedName+".";

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
                                        config.set(finishedPath+"crafting."+line, craftingLine.toUpperCase());

                                        line++;
                                    }

                                    String[] finishedMaterials = finishedData.get("materials").split(";");

                                    for(String materialLine : finishedMaterials) {
                                        String[] parts = materialLine.split(":");

                                        config.set(finishedPath+"crafting.materials."+(parts[0].toUpperCase()),
                                                parts[1].toUpperCase());
                                    }

                                    int number = 1;

                                    try {
                                        while(!config.getString("BackPacks.enabled."+number).equals("")) {
                                            number++;
                                        }

                                    } catch (NullPointerException e) {
                                        config.set("BackPacks.enabled."+number, finishedName);
                                    }

                                    try {
                                        config.save(new File(main.getDataFolder(), "config.yml"));

                                        Map<String, Object> syntaxError = messages.getConfigurationSection(path+"steps.finishTrue")
                                                .getValues(true);

                                        for(Map.Entry<String, Object> error : syntaxError.entrySet()) {
                                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                                    String.valueOf(error.getValue()).replaceAll("%backpack%", finishedName)));
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

                        } catch (NullPointerException e) {
                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.finishNotSet")));
                        }

                        break;

                    default:
                        sendMap(sender, "syntaxError");

                        break;
                }

            }  else if(args.length == 1) {
                // todo: if args.length == 1 send the message for arg[0]

                // todo: give preview item

                String arg = args[0].toLowerCase();

                switch (arg) {
                    case "preview":
                        try {
                            HashMap<String, String> finishedData = data.get(sender);

                            String  name = finishedData.get("name");

                            String type = finishedData.get("type");

                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.preview.name").replaceAll("%name%", name)));

                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.preview.displayname").replaceAll("%name%",
                                            finishedData.get("displayname"))));

                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.preview.type").replaceAll("%type%",
                                            getBackPackColor(type)+type)));

                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.preview.description.title")));

                            String[] description = finishedData.get("description").split(";");

                            int descLine = 1;

                            for(String descriptionLine : description) {
                                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                        messages.getString(path+"steps.preview.description.line")
                                        .replaceAll("%lineNumber%", String.valueOf(descLine))
                                        .replaceAll("%content%",descriptionLine)));

                                descLine++;
                            }

                            switch (type) {
                                case "normal":
                                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                            messages.getString(path+"steps.preview.slots")
                                                    .replaceAll("%slots%", finishedData.get("slots"))));

                                    break;

                                case "furnace":
                                    Boolean gui = Boolean.valueOf(finishedData.get("gui"));

                                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                            messages.getString(path+"steps.preview.gui")
                                                    .replaceAll("%gui%", getColor(gui)+String.valueOf(gui))));

                                    break;
                            }

                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.preview.material")
                                            .replaceAll("%material%", finishedData.get("material"))));

                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.preview.crafting.title")));

                            String[] crafting = finishedData.get("crafting").split(";");

                            int line = 1;

                            for(String craftingLine : crafting) {
                                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                        messages.getString(path+"steps.preview.line")
                                                .replaceAll("%lineNumber%", String.valueOf(line))
                                                .replaceAll("%content%", craftingLine.toUpperCase())));

                                line++;
                            }

                            sender.sendMessage(prefix);

                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.preview.crafting.materials.title")));

                            String[] finishedMaterials = finishedData.get("materials").split(";");

                            for(String materialLine : finishedMaterials) {
                                String[] parts = materialLine.split(":");

                                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                        messages.getString(path+"steps.preview.crafting.materials.line")
                                                .replaceAll("%line%", parts[0].toUpperCase())
                                                .replaceAll("%material%", parts[1].toUpperCase())));

                            }

                        } catch (NullPointerException e) {
                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.previewNotSet")));
                        }

                        break;

                    default:
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

    private String getBackPackColor(String type) {
        switch (type) {
            case "normal":
                return "&a";

            case "ender":
                return "&d";

            case "crafting":
                return "&c";

            case "furnace":
                return "&8";
        }

        return "";
    }

    private String getColor(Boolean bool) {
        if(bool) {
            return "&a";
        }

        return "&c";
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
