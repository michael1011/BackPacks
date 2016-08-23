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

    static void initCrafting() {
        String path = "BackPacks.";

        Map<String, Object> enabled = config.getConfigurationSection(path +"enabled").getValues(true);

        for(Map.Entry<String, Object> entry : enabled.entrySet()) {
            String backPack = entry.getValue().toString();
            String backPackPath = path+backPack+".";

            if(config.contains(backPackPath)) {
                Bukkit.getServer().addRecipe(createBackPack(config, backPackPath));

            } else {
                Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                        messages.getString("BackPacks.couldFindConfig").replaceAll("%backpack%", backPack)));
            }

        }

    }

    static ShapedRecipe createBackPack(YamlConfiguration config, String backPackPath) {
        ItemStack craft = new ItemStack(Material.getMaterial(
                config.getString(backPackPath+"material")));

        String name = config.getString(backPackPath+"name");

        String lore = null;

        Map<String, Object> loreSec = config.getConfigurationSection(backPackPath+"description").getValues(true);

        for(Map.Entry<String, Object> ent : loreSec.entrySet()) {
            lore = lore+","+ent.getValue();
        }

        ItemMeta craftM = craft.getItemMeta();

        craftM.setDisplayName(name);

        if (lore != null) {
            craftM.setLore(Arrays.asList(lore.split("\\s*,\\s*")));
        }

        craft.setItemMeta(craftM);

        ShapedRecipe recipe = new ShapedRecipe(craft);

        recipe.shape(
                config.getString(backPackPath+"crafting.1"),
                config.getString(backPackPath+"crafting.2"),
                config.getString(backPackPath+"crafting.3"));

        Map<String, Object> ingredients = config.getConfigurationSection(backPackPath+
                "crafting.materials").getValues(true);

        for(Map.Entry<String, Object> ing : ingredients.entrySet()) {
            recipe.setIngredient(ing.getKey().charAt(0), Material.valueOf(ing.getValue().toString()));
        }

        items.put(name, craft);

        return recipe;
    }

}
