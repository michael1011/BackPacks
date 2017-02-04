package at.michael1011.backpacks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static at.michael1011.backpacks.BackPack.Type.furnace;
import static at.michael1011.backpacks.Main.*;

@SuppressWarnings("unchecked")
public class Crafting {

    public static List<BackPack> backPacks = new ArrayList<>();
    public static HashMap<String, BackPack> backPacksMap = new HashMap<>();
    public static HashMap<BackPack, ItemStack> backPacksItems = new HashMap<>();

    public static List<String> backPackNames = new ArrayList<>();

    public static final String nbtKey = "BackPack";

    private static final String path = "BackPacks.";

    private static Boolean slotsDivisible = true;

    public static void initCrafting(final CommandSender sender) {
        backPacks = new ArrayList<>();
        backPacksMap = new HashMap<>();
        backPacksItems = new HashMap<>();
        backPackNames = new ArrayList<>();

        if (syncConfig) {
            SQL.getResult("SELECT * FROM bp", new SQL.Callback<ResultSet>() {

                @Override
                public void onSuccess(ResultSet rs) {
                    try {
                        rs.beforeFirst();

                        while (rs.next()) {
                            if (Boolean.valueOf(rs.getString("enabled"))) {
                                String backPack = rs.getString("name");

                                ItemStack item = getItemStack(sender, rs, backPack);

                                if (item != null) {
                                    if (slotsDivisible) {
                                        if (!rs.getString("craftingRecipe").equals("")) {
                                            Bukkit.getServer().addRecipe(createShapedRecipe(sender, item, backPacks.get(backPacks.size() - 1), rs));
                                        }

                                        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                                                messages.getString("BackPacks.enabled").replaceAll("%backpack%", backPack)));

                                    } else {
                                        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                                                messages.getString("BackPacks.slotsNotDivisibleBy9").replaceAll("%backpack%", backPack)));

                                        slotsDivisible = true;
                                    }

                                }

                            }

                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Throwable e) {}

            }, false);

        } else {
            List<String> toLoad = new ArrayList<>();

            for (Map.Entry<String, Object> entry : config.getConfigurationSection(path + "enabled")
                    .getValues(true).entrySet()) {

                toLoad.add(entry.getValue().toString());
            }

            initConfig(toLoad, sender, true);
        }

        for (BackPack backPack : backPacks) {
            backPackNames.add(backPack.getName());
        }

    }

    static void initConfig(List<String> entries, CommandSender sender, Boolean output) {
        backPacks = new ArrayList<>();
        backPacksMap = new HashMap<>();
        backPacksItems = new HashMap<>();
        backPackNames = new ArrayList<>();

        for (String backPack : entries) {
            String backPackPath = path + backPack + ".";

            if (config.contains(backPackPath)) {
                ItemStack item = getItemStack(sender, backPackPath, backPack, output);

                if (item != null) {
                    if (slotsDivisible) {
                        if (!config.getBoolean(backPackPath + "crafting.disabled")
                                && config.get(backPackPath + "crafting.1") != null) {

                            Bukkit.getServer().addRecipe(createShapedRecipe(sender, item, backPacks.get(backPacks.size() - 1), backPackPath, output));
                        }

                        if (output) {
                            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                                    messages.getString("BackPacks.enabled").replaceAll("%backpack%", backPack)));
                        }

                    } else {
                        if (output) {
                            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                                    messages.getString("BackPacks.slotsNotDivisibleBy9").replaceAll("%backpack%", backPack)));
                        }

                        slotsDivisible = true;
                    }

                }

            } else {
                if (output) {
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                            messages.getString("BackPacks.couldNotFindConfig").replaceAll("%backpack%", backPack)));
                }

            }

        }

    }

    private static ItemStack getItemStack(CommandSender sender, ResultSet rs, String backPack) throws SQLException {
        int slots = rs.getInt("slots");

        if (slots % 9 == 0) {
            String materialString = rs.getString("material").toUpperCase();

            try {
                Material material = Material.valueOf(materialString);

                ItemStack item = new ItemStack(material, 1);

                String name = ChatColor.translateAlternateColorCodes('&',
                        rs.getString("itemTitle"));

                List<String> lore = new ArrayList<>();

                for (String entry : rs.getString("lore").split("\n")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', entry));
                }

                ItemMeta meta = item.getItemMeta();

                meta.setDisplayName(name);

                if (lore.size() > 0) {
                    meta.setLore(lore);
                }

                item.setItemMeta(meta);

                Sound open = null;
                Sound close = null;

                String sound = rs.getString("openSound");

                try {
                    if (!sound.equals("")) {
                        open = Sound.valueOf(sound.toUpperCase());
                    }

                    sound = rs.getString("closeSound");

                    if (!sound.equals("")) {
                        close = Sound.valueOf(sound.toUpperCase());
                    }

                } catch (IllegalArgumentException e) {
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                            messages.getString("Help.soundNotValid")
                                    .replaceAll("%sound%", sound.toUpperCase())
                                    .replaceAll("%backpack%", backPack)));

                    return null;
                }

                item = setNbtTag(item, backPack);

                BackPack finishedBackPack = new BackPack(backPack, new BackPack.Type(rs.getString("type")), material, slots,
                        Boolean.valueOf(rs.getString("furnaceGui")), name, lore, rs.getString("inventoryTitle"), open, close);

                backPacks.add(finishedBackPack);
                backPacksMap.put(backPack, finishedBackPack);
                backPacksItems.put(finishedBackPack, item);

                return item;

            } catch (IllegalArgumentException e) {
                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                        messages.getString("Help.materialNotValid")
                                .replaceAll("%material%", materialString)
                                .replaceAll("%backpack%", backPack)));

                return null;
            }

        } else {
            slotsDivisible = false;
        }

        return null;
    }

    private static ItemStack getItemStack(CommandSender sender, String backPackPath, String backPack, Boolean output) {
        int slots = config.getInt(backPackPath + "slots");

        if (slots % 9 == 0) {
            String materialString = config.getString(backPackPath + "material").toUpperCase();

            try {
                Material material = Material.valueOf(materialString);

                ItemStack item = new ItemStack(material, 1);

                String name = ChatColor.translateAlternateColorCodes('&',
                        config.getString(backPackPath + "name"));

                List<String> lore = new ArrayList<>();

                for (Map.Entry<String, Object> entry :
                        config.getConfigurationSection(backPackPath + "description").getValues(true).entrySet()) {

                    lore.add(ChatColor.translateAlternateColorCodes('&', entry.getValue().toString()));
                }

                ItemMeta meta = item.getItemMeta();

                meta.setDisplayName(name);

                if (lore.size() > 0) {
                    meta.setLore(lore);
                }

                item.setItemMeta(meta);

                Sound open = null;
                Sound close = null;

                String sound = config.getString(backPackPath + "sounds.open");

                try {
                    if (sound != null) {
                        open = Sound.valueOf(sound.toUpperCase());
                    }

                    sound = config.getString(backPackPath + "sounds.close");

                    if (sound != null) {
                        close = Sound.valueOf(sound.toUpperCase());
                    }

                } catch (IllegalArgumentException e) {
                    assert sound != null;

                    if (output) {
                        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                                messages.getString("Help.soundNotValid")
                                        .replaceAll("%sound%", sound.toUpperCase())
                                        .replaceAll("%backpack%", backPack)));
                    }

                    return null;
                }

                BackPack.Type type = new BackPack.Type(config.getString(backPackPath + "type"));

                Boolean furnaceGui = false;

                if (type.equals(furnace)) {
                    furnaceGui = config.getBoolean(backPackPath + "gui.enabled");
                }

                String inventoryTitle = "";
                String inventoryTitleConfig = config.getString(backPackPath + "inventoryTitle");

                if (inventoryTitleConfig != null) {
                    inventoryTitle = inventoryTitleConfig;
                }

                item = setNbtTag(item, backPack);

                BackPack finishedBackPack = new BackPack(backPack, type, material, slots, furnaceGui, name, lore, inventoryTitle, open, close);

                backPacks.add(finishedBackPack);
                backPacksMap.put(backPack, finishedBackPack);
                backPacksItems.put(finishedBackPack, item);

                return item;

            } catch (IllegalArgumentException e) {
                if (output) {
                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                            messages.getString("Help.materialNotValid")
                                    .replaceAll("%material%", materialString)
                                    .replaceAll("%backpack%", backPack)));
                }

            }

        } else {
            slotsDivisible = false;
        }

        return null;
    }

    private static ItemStack setNbtTag(ItemStack item, String backPack) {
        try {
            Class craftItemStack = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");

            Object nbtCopy = craftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
            Object nbtTag = nbtCopy.getClass().getMethod("getTag").invoke(nbtCopy);

            if (nbtTag == null) {
                nbtTag = Class.forName(serverPackage + "NBTTagCompound").getConstructor().newInstance();
            }

            nbtTag.getClass().getMethod("setString", String.class, String.class)
                    .invoke(nbtTag, nbtKey, backPack);

            nbtCopy.getClass().getMethod("setTag", nbtTag.getClass()).invoke(nbtCopy, nbtTag);

            return ((ItemStack) craftItemStack.getMethod("asBukkitCopy", nbtCopy.getClass()).invoke(null, nbtCopy));

        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException | InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static ShapedRecipe createShapedRecipe(CommandSender sender, ItemStack item, BackPack backPack, ResultSet rs) throws SQLException {
        ShapedRecipe recipe = new ShapedRecipe(item);

        String[] crafting = rs.getString("craftingRecipe").split("/");

        recipe.shape(
                crafting[0].replaceAll("\\+", ""),
                crafting[1].replaceAll("\\+", ""),
                crafting[2].replaceAll("\\+", "")
        );

        backPack.setCraftingRecipe(crafting[0] + "/" + crafting[1] + "/" + crafting[2]);

        String[] materials = rs.getString("materials").split("/");

        for (String singleMaterial : materials) {
            String[] split = singleMaterial.split(":");

            char ingredient = split[0].charAt(0);
            String material = split[1];

            backPack.setMaterials(backPack.getMaterials() + ingredient + ":" + material + "/");

            try {
                recipe.setIngredient(ingredient, Material.valueOf(material));

            } catch (IllegalArgumentException e) {
                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                        messages.getString("Help.materialNotValid")
                                .replaceAll("%material%", material)
                                .replaceAll("%backpack%", backPack.getName())));

                return null;
            }

        }

        String finishedMaterials = backPack.getMaterials();

        if (!finishedMaterials.equals("")) {
            backPack.setMaterials(finishedMaterials.substring(0, finishedMaterials.length() - 1));
        }

        return recipe;
    }

    private static ShapedRecipe createShapedRecipe(CommandSender sender, ItemStack item, BackPack backPack, String backPackPath, Boolean output) {
        ShapedRecipe recipe = new ShapedRecipe(item);

        String crafting1 = config.getString(backPackPath + "crafting.1");
        String crafting2 = config.getString(backPackPath + "crafting.2");
        String crafting3 = config.getString(backPackPath + "crafting.3");

        recipe.shape(
                crafting1.replaceAll("\\+", ""),
                crafting2.replaceAll("\\+", ""),
                crafting3.replaceAll("\\+", "")
        );

        backPack.setCraftingRecipe(crafting1 + "/" + crafting2 + "/" + crafting3);

        Map<String, Object> ingredients = config.getConfigurationSection(backPackPath +
                "crafting.materials").getValues(true);

        for (Map.Entry<String, Object> entry : ingredients.entrySet()) {
            char ingredient = entry.getKey().charAt(0);
            String material = entry.getValue().toString().toUpperCase();

            backPack.setMaterials(backPack.getMaterials() + ingredient + ":" + material + "/");

            try {
                recipe.setIngredient(ingredient, Material.valueOf(material));

            } catch (IllegalArgumentException e) {
                if (output) {
                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                            messages.getString("Help.materialNotValid")
                                    .replaceAll("%material%", material)
                                    .replaceAll("%backpack%", backPack.getName())));
                }

                return null;
            }

        }

        String materials = backPack.getMaterials();

        if (!materials.equals("")) {
            backPack.setMaterials(materials.substring(0, materials.length() - 1));
        }

        return recipe;
    }

}
