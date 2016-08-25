package at.michael1011.backpacks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static at.michael1011.backpacks.Main.*;

public class Crafting {

    public static HashMap<String, ItemStack> items = new HashMap<>();
    public static String available;

    private static Boolean works = true;

    // todo: write test for this

    static void initCrafting() {
        String path = "BackPacks.";

        Map<String, Object> enabled = config.getConfigurationSection(path+"enabled").getValues(true);

        for(Map.Entry<String, Object> entry : enabled.entrySet()) {
            String backPack = entry.getValue().toString();
            String backPackPath = path+backPack+".";

            if(config.contains(backPackPath)) {
                ItemStack item = getItemStack(config, backPackPath);

                if(works) {
                    items.put(backPack, item);

                    Bukkit.getServer().addRecipe(createShapedRecipe(item, backPackPath));

                    Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                            messages.getString("BackPacks.enabled").replaceAll("%backpack%", backPack)));

                } else {
                    Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                            messages.getString("BackPacks.slotsNotDivisibleBy9").replaceAll("%backpack%", backPack)));

                    works = true;
                }

            } else {
                Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                        messages.getString("BackPacks.couldNotFindConfig").replaceAll("%backpack%", backPack)));
            }

        }

        for(Map.Entry<String, ItemStack> backpack : items.entrySet()) {
            available = available+", "+backpack.getKey();
        }

    }

    private static ItemStack getItemStack(YamlConfiguration config, String backPackPath) {
        if(config.getInt(backPackPath+"slots") % 9 == 0) {
            ItemStack craft = new ItemStack(Material.getMaterial(
                    config.getString(backPackPath+"material")), 1);

            String name = ChatColor.translateAlternateColorCodes('&', config.getString(backPackPath+"name"));

            String lore = "";

            Map<String, Object> loreSec = config.getConfigurationSection(backPackPath+"description").getValues(true);

            for(Map.Entry<String, Object> ent : loreSec.entrySet()) {
                lore = lore+","+ChatColor.translateAlternateColorCodes('&', ent.getValue().toString());
            }

            ItemMeta craftM = craft.getItemMeta();

            craftM.setDisplayName(name);

            if(!lore.equals("")) {
                craftM.setLore(Arrays.asList(lore.split("\\s*,\\s*")));
            }

            craft.setItemMeta(craftM);

            return craft;

        } else {
            works = false;

            return null;
        }

    }

    private static ShapedRecipe createShapedRecipe(ItemStack item, String backPackPath) {
        ShapedRecipe recipe = new ShapedRecipe(item);

        recipe.shape(
                config.getString(backPackPath+"crafting.1").replaceAll("\\+", ""),
                config.getString(backPackPath+"crafting.2").replaceAll("\\+", ""),
                config.getString(backPackPath+"crafting.3").replaceAll("\\+", ""));

        Bukkit.getConsoleSender().sendMessage(config.getString(backPackPath+"crafting.3").replaceAll("\\+", ""));

        Map<String, Object> ingredients = config.getConfigurationSection(backPackPath+
                "crafting.materials").getValues(true);

        for(Map.Entry<String, Object> ing : ingredients.entrySet()) {
            Bukkit.getConsoleSender().sendMessage(ing.getKey().charAt(0)+"+"+ing.getValue().toString());

            recipe.setIngredient(ing.getKey().charAt(0), Material.valueOf(ing.getValue().toString()));
        }

        return recipe;
    }

}
