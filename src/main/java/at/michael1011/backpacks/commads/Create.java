package at.michael1011.backpacks.commads;

import at.michael1011.backpacks.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static at.michael1011.backpacks.Main.*;

public class Create implements CommandExecutor {

    private static final String path = "Help.bpcreate.";

    private Main main;

    private static HashMap<CommandSender, HashMap<String, String>> data = new HashMap<>();

    public Create(Main main) {
        this.main = main;

        PluginCommand command = main.getCommand("bpcreate");

        command.setExecutor(this);
        command.setTabCompleter(new CreateCompleter());
    }

    // todo: add option to disable crafting recipe

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
                                    messages.getString(path+"steps.materialNotValid")
                                            .replaceAll("%material%", material)));
                        }

                        break;

                    case "crafting":
                        String rawRecipe = argsToString(args);

                        if(!rawRecipe.equals("disabled")) {
                            String recipe = rawRecipe.toUpperCase();
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

                        } else {
                            data.get(sender).put("crafting", rawRecipe);

                            sendMap(sender, "steps.type");
                        }

                        break;

                    case "materials":
                        String materials = argsToString(args).toUpperCase();
                        String[] materialsSplit = materials.split(";");

                        ArrayList<String> missingMaterials = new ArrayList<>();

                        Boolean validMaterial = true;
                        Boolean validMaterials = true;

                        for(String materialPart : materialsSplit) {
                            if(materialPart.contains(":")) {
                                String[] parts = materialPart.split(":");

                                String part = parts[1];

                                try {
                                    Material.valueOf(part);

                                } catch (IllegalArgumentException e) {
                                    validMaterial = false;

                                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                            messages.getString(path+"steps.materialNotValid")
                                                    .replaceAll("%material%", part)));

                                    break;
                                }

                            } else {
                                validMaterials = false;
                            }
                        }

                        if(data.get(sender).get("crafting") != null && validMaterial && validMaterials) {
                            Boolean contains = false;

                            String crafting = data.get(sender).get("crafting")
                                    .replaceAll("\\+", "").replaceAll(";", "");

                            for(char c : crafting.toCharArray()) {
                                for(String materialPart : materialsSplit) {
                                    String[] parts = materialPart.split(":");

                                    if(parts[0].charAt(0) == c) {
                                        contains = true;

                                        break;
                                    }
                                }

                                if(!contains) {
                                    String add = String.valueOf(c).toUpperCase();

                                    if(!missingMaterials.contains(add)) {
                                        missingMaterials.add(add);
                                    }

                                } else {
                                    contains = false;
                                }

                            }

                        }

                        if(missingMaterials.size() > 0) {
                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.materialsMissing")
                                            .replaceAll("%missing%", arrayListToString(missingMaterials))));

                            sender.sendMessage(prefix);

                            sendMap(sender, "steps.materials");

                            break;
                        }

                        if(validMaterials && validMaterial) {
                            data.get(sender).put("materials", materials);

                            sendMap(sender, "steps.type");

                        } else {
                            if(validMaterial) {
                                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                        messages.getString(path+"steps.materialsNotValid")));

                                sender.sendMessage(prefix);

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

                                    String rawCrafting = finishedData.get("crafting");

                                    if(!rawCrafting.equals("disabled")) {
                                        String[] crafting = rawCrafting.split(";");

                                        int line = 1;

                                        for(String craftingLine : crafting) {
                                            config.set(finishedPath+"crafting."+line, craftingLine);

                                            line++;
                                        }

                                        String[] finishedMaterials = finishedData.get("materials").split(";");

                                        for(String materialLine : finishedMaterials) {
                                            String[] parts = materialLine.split(":");

                                            config.set(finishedPath+"crafting.materials."+(parts[0]),
                                                    parts[1]);
                                        }

                                    } else {
                                        config.set(finishedPath+"crafting.enabled", false);
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
                String arg = args[0].toLowerCase();

                switch (arg) {
                    case "displayname":
                        sendMap(sender, "steps.displayName");

                        break;

                    case "description":
                    case "material":
                    case "crafting":
                    case "materials":
                    case "type":
                    case "slots":
                    case "gui":
                    case "finish":
                        sendMap(sender, "steps."+arg);

                        break;

                    case "item":
                        if(sender instanceof Player) {
                            Player p = (Player) sender;

                            ArrayList<String> missingHere = new ArrayList<>(Arrays.asList("displayname",
                                    "description", "material"));

                            try {
                                HashMap<String, String> finishedData = data.get(sender);

                                String displayname = ChatColor.translateAlternateColorCodes('&',
                                        finishedData.get("displayname"));

                                missingHere.remove("displayname");

                                String description = ChatColor.translateAlternateColorCodes('&',
                                        finishedData.get("description")+";;"+messages.getString(path+"steps.item.extraDescription"));

                                missingHere.remove("description");

                                ItemStack item = new ItemStack(Material.valueOf(finishedData.get("material")), 1);

                                missingHere.remove("material");

                                ItemMeta meta = item.getItemMeta();

                                meta.setDisplayName(displayname);

                                meta.setLore(Arrays.asList(description.split(";")));

                                item.setItemMeta(meta);

                                p.getInventory().addItem(item);

                            } catch (NullPointerException e) {
                                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                        messages.getString(path+"steps.item.notSet")));

                                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                        messages.getString(path+"steps.item.missing")
                                                .replaceAll("%values%", arrayListToString(missingHere))));
                            }

                        } else {
                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString("Help.onlyPlayers")));
                        }
                        break;

                    case "preview":
                        ArrayList<String> missingHere = new ArrayList<>(Arrays.asList("name", "displayname",
                                "description", "material", "crafting", "materials", "type"));

                        try {
                            HashMap<String, String> finishedData = data.get(sender);

                            String name = finishedData.get("name");

                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.preview.name").replaceAll("%name%", name)));

                            missingHere.remove("name");

                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.preview.displayname").replaceAll("%name%",
                                            finishedData.get("displayname"))));

                            missingHere.remove("displayname");

                            String[] description = finishedData.get("description").split(";");

                            missingHere.remove("description");

                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.preview.description.title")));

                            int descLine = 1;

                            for(String descriptionLine : description) {
                                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                        messages.getString(path+"steps.preview.description.line")
                                                .replaceAll("%lineNumber%", String.valueOf(descLine))
                                                .replaceAll("%content%",descriptionLine)));

                                descLine++;
                            }

                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.preview.material")
                                            .replaceAll("%material%", finishedData.get("material"))));

                            missingHere.remove("material");

                            String rawCrafting = finishedData.get("crafting");

                            if(!rawCrafting.equals("disabled")) {
                                String[] crafting = rawCrafting.split(";");

                                missingHere.remove("crafting");

                                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                        messages.getString(path+"steps.preview.crafting.title")));

                                int line = 1;

                                for(String craftingLine : crafting) {
                                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                            messages.getString(path+"steps.preview.crafting.line")
                                                    .replaceAll("%lineNumber%", String.valueOf(line))
                                                    .replaceAll("%content%", craftingLine)));

                                    line++;
                                }

                                String[] finishedMaterials = finishedData.get("materials").split(";");

                                missingHere.remove("materials");

                                sender.sendMessage(prefix);

                                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                        messages.getString(path+"steps.preview.crafting.materials.title")));

                                for(String materialLine : finishedMaterials) {
                                    String[] parts = materialLine.split(":");

                                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                            messages.getString(path+"steps.preview.crafting.materials.line")
                                                    .replaceAll("%line%", parts[0])
                                                    .replaceAll("%material%", parts[1])));

                                }

                            } else {
                                missingHere.remove("crafting");
                                missingHere.remove("materials");

                                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                        messages.getString(path+"steps.preview.crafting.title")+
                                                " &c"+rawCrafting));
                            }

                            String type = finishedData.get("type");

                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.preview.type").replaceAll("%type%",
                                            getBackPackColor(type)+type)));

                            missingHere.remove("type");

                            switch (type) {
                                case "normal":
                                    missingHere.add("slots");

                                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                            messages.getString(path+"steps.preview.slots")
                                                    .replaceAll("%slots%", finishedData.get("slots"))));

                                    break;

                                case "furnace":
                                    missingHere.add("slots");

                                    Boolean gui = Boolean.valueOf(finishedData.get("gui"));

                                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                            messages.getString(path+"steps.preview.gui")
                                                    .replaceAll("%gui%", getColor(gui)+String.valueOf(gui))));

                                    break;
                            }

                        } catch (NullPointerException e) {
                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.preview.notSet")));

                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(path+"steps.preview.missing")
                                            .replaceAll("%values%", arrayListToString(missingHere))));
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

    private String arrayListToString(ArrayList<String> list) {
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < list.size(); i++) {
            if(i == 0) {
                builder.append(list.get(i));

            } else {
                builder.append(", ").append(list.get(i));
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
