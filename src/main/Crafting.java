package main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Crafting {

    public Main plugin;

    public Crafting(Main Main) {
        this.plugin = Main;
    }

    public static ItemStack littleB = new ItemStack(Material.getMaterial(Main.names.getString("LittleBackPack.Material")), 1);
    public static ItemStack normalB = new ItemStack(Material.getMaterial(Main.names.getString("NormalBackPack.Material")), 1);
    public static ItemStack enderB = new ItemStack(Material.getMaterial(Main.names.getString("EnderBackPack.Material")), 1);
    public static ItemStack craftingB = new ItemStack(Material.getMaterial(Main.names.getString("WorkbenchBackPack.Material")), 1);
    public static ItemStack enchantingB = new ItemStack(Material.getMaterial(Main.names.getString("EnchantingBackPack.Material")), 1);
    public static ItemStack furnaceB = new ItemStack(Material.getMaterial(Main.names.getString("FurnaceBackPack.Material")), 1);

    static void CraftingB() {
        if(Main.names.getBoolean("LittleBackPack.Enable")) {
            String Name = ChatColor.translateAlternateColorCodes('&', Main.names.getString("LittleBackPack.Name"));
            String Lore1 = ChatColor.translateAlternateColorCodes('&', Main.names.getString("LittleBackPack.Desc1"));
            String Lore2 = ChatColor.translateAlternateColorCodes('&', Main.names.getString("LittleBackPack.Desc2"));

            ItemMeta littleBM = littleB.getItemMeta();
            littleBM.setDisplayName(Name);
            littleBM.setLore(Arrays.asList("", Lore1, Lore2));
            littleB.setItemMeta(littleBM);

            ShapedRecipe littleBR = new ShapedRecipe(littleB);
            littleBR.shape(
                    Main.names.getString("LittleBackPack.Crafting.Line1"),
                    Main.names.getString("LittleBackPack.Crafting.Line2"),
                    Main.names.getString("LittleBackPack.Crafting.Line3")
            );

            if(!Main.names.getString("LittleBackPack.Crafting.Material1").equals("null")) {
                littleBR.setIngredient('*', Material.getMaterial(Main.names.getString("LittleBackPack.Crafting.Material1")));
            }

            if(!Main.names.getString("LittleBackPack.Crafting.Material2").equals("null")) {
                littleBR.setIngredient('@', Material.getMaterial(Main.names.getString("LittleBackPack.Crafting.Material2")));
            }

            if(!Main.names.getString("LittleBackPack.Crafting.Material3").equals("null")) {
                littleBR.setIngredient('+', Material.getMaterial(Main.names.getString("LittleBackPack.Crafting.Material3")));
            }

            if(!Main.names.getString("LittleBackPack.Crafting.Material4").equals("null")) {
                littleBR.setIngredient('%', Material.getMaterial(Main.names.getString("LittleBackPack.Crafting.Material4")));
            }

            if(!Main.names.getString("LittleBackPack.Crafting.Material5").equals("null")) {
                littleBR.setIngredient('&', Material.getMaterial(Main.names.getString("LittleBackPack.Crafting.Material5")));
            }

            Bukkit.getServer().addRecipe(littleBR);
        }


        if(Main.names.getBoolean("NormalBackPack.Enable")) {
            String Name = ChatColor.translateAlternateColorCodes('&', Main.names.getString("NormalBackPack.Name"));
            String Lore1 = ChatColor.translateAlternateColorCodes('&', Main.names.getString("NormalBackPack.Desc1"));
            String Lore2 = ChatColor.translateAlternateColorCodes('&', Main.names.getString("NormalBackPack.Desc2"));

            ItemMeta normalBM = normalB.getItemMeta();
            normalBM.setDisplayName(Name);
            normalBM.setLore(Arrays.asList("", Lore1, Lore2));
            normalB.setItemMeta(normalBM);

            ShapedRecipe normalBR = new ShapedRecipe(normalB);
            normalBR.shape(
                    Main.names.getString("NormalBackPack.Crafting.Line1"),
                    Main.names.getString("NormalBackPack.Crafting.Line2"),
                    Main.names.getString("NormalBackPack.Crafting.Line3")
            );

            if(!Main.names.getString("NormalBackPack.Crafting.Material1").equals("null")) {
                normalBR.setIngredient('*', Material.getMaterial(Main.names.getString("NormalBackPack.Crafting.Material1")));
            }

            if(!Main.names.getString("NormalBackPack.Crafting.Material2").equals("null")) {
                normalBR.setIngredient('@', Material.getMaterial(Main.names.getString("NormalBackPack.Crafting.Material2")));
            }

            if(!Main.names.getString("NormalBackPack.Crafting.Material3").equals("null")) {
                normalBR.setIngredient('+', Material.getMaterial(Main.names.getString("NormalBackPack.Crafting.Material3")));
            }

            if(!Main.names.getString("NormalBackPack.Crafting.Material4").equals("null")) {
                normalBR.setIngredient('%', Material.getMaterial(Main.names.getString("NormalBackPack.Crafting.Material4")));
            }

            if(!Main.names.getString("NormalBackPack.Crafting.Material5").equals("null")) {
                normalBR.setIngredient('&', Material.getMaterial(Main.names.getString("NormalBackPack.Crafting.Material5")));
            }

            Bukkit.getServer().addRecipe(normalBR);
        }


        if(Main.names.getBoolean("EnderBackPack.Enable")) {
            String Name = ChatColor.translateAlternateColorCodes('&', Main.names.getString("EnderBackPack.Name"));
            String Lore1 = ChatColor.translateAlternateColorCodes('&', Main.names.getString("EnderBackPack.Desc1"));
            String Lore2 = ChatColor.translateAlternateColorCodes('&', Main.names.getString("EnderBackPack.Desc2"));

            ItemMeta enderBM = enderB.getItemMeta();
            enderBM.setDisplayName(Name);
            enderBM.setLore(Arrays.asList("", Lore1, Lore2));
            enderB.setItemMeta(enderBM);

            ShapedRecipe enderBR = new ShapedRecipe(enderB);
            enderBR.shape(
                    Main.names.getString("EnderBackPack.Crafting.Line1"),
                    Main.names.getString("EnderBackPack.Crafting.Line2"),
                    Main.names.getString("EnderBackPack.Crafting.Line3")
            );

            if(!Main.names.getString("EnderBackPack.Crafting.Material1").equals("null")) {
                enderBR.setIngredient('*', Material.getMaterial(Main.names.getString("EnderBackPack.Crafting.Material1")));
            }

            if(!Main.names.getString("EnderBackPack.Crafting.Material2").equals("null")) {
                enderBR.setIngredient('@', Material.getMaterial(Main.names.getString("EnderBackPack.Crafting.Material2")));
            }

            if(!Main.names.getString("EnderBackPack.Crafting.Material3").equals("null")) {
                enderBR.setIngredient('+', Material.getMaterial(Main.names.getString("EnderBackPack.Crafting.Material3")));
            }

            if(!Main.names.getString("EnderBackPack.Crafting.Material4").equals("null")) {
                enderBR.setIngredient('%', Material.getMaterial(Main.names.getString("EnderBackPack.Crafting.Material4")));
            }

            if(!Main.names.getString("EnderBackPack.Crafting.Material5").equals("null")) {
                enderBR.setIngredient('&', Material.getMaterial(Main.names.getString("EnderBackPack.Crafting.Material5")));
            }

            Bukkit.getServer().addRecipe(enderBR);
        }


        if(Main.names.getBoolean("WorkbenchBackPack.Enable")) {
            String Name = ChatColor.translateAlternateColorCodes('&', Main.names.getString("WorkbenchBackPack.Name"));
            String Lore1 = ChatColor.translateAlternateColorCodes('&', Main.names.getString("WorkbenchBackPack.Desc1"));
            String Lore2 = ChatColor.translateAlternateColorCodes('&', Main.names.getString("WorkbenchBackPack.Desc2"));

            ItemMeta craftingBM = craftingB.getItemMeta();
            craftingBM.setDisplayName(Name);
            craftingBM.setLore(Arrays.asList("", Lore1, Lore2));
            craftingB.setItemMeta(craftingBM);

            ShapedRecipe craftingBR = new ShapedRecipe(craftingB);
            craftingBR.shape(
                    Main.names.getString("WorkbenchBackPack.Crafting.Line1"),
                    Main.names.getString("WorkbenchBackPack.Crafting.Line2"),
                    Main.names.getString("WorkbenchBackPack.Crafting.Line3")
            );

            if(!Main.names.getString("WorkbenchBackPack.Crafting.Material1").equals("null")) {
                craftingBR.setIngredient('*', Material.getMaterial(Main.names.getString("WorkbenchBackPack.Crafting.Material1")));
            }

            if(!Main.names.getString("WorkbenchBackPack.Crafting.Material2").equals("null")) {
                craftingBR.setIngredient('@', Material.getMaterial(Main.names.getString("WorkbenchBackPack.Crafting.Material2")));
            }

            if(!Main.names.getString("WorkbenchBackPack.Crafting.Material3").equals("null")) {
                craftingBR.setIngredient('+', Material.getMaterial(Main.names.getString("WorkbenchBackPack.Crafting.Material3")));
            }

            if(!Main.names.getString("WorkbenchBackPack.Crafting.Material4").equals("null")) {
                craftingBR.setIngredient('%', Material.getMaterial(Main.names.getString("WorkbenchBackPack.Crafting.Material4")));
            }

            if(!Main.names.getString("WorkbenchBackPack.Crafting.Material5").equals("null")) {
                craftingBR.setIngredient('&', Material.getMaterial(Main.names.getString("WorkbenchBackPack.Crafting.Material5")));
            }

            Bukkit.getServer().addRecipe(craftingBR);
        }

        if(Main.names.getBoolean("EnchantingBackPack.Enable")) {
            String Name = ChatColor.translateAlternateColorCodes('&', Main.names.getString("EnchantingBackPack.Name"));
            String Lore1 = ChatColor.translateAlternateColorCodes('&', Main.names.getString("EnchantingBackPack.Desc1"));
            String Lore2 = ChatColor.translateAlternateColorCodes('&', Main.names.getString("EnchantingBackPack.Desc2"));

            ItemMeta enchantingBM = enchantingB.getItemMeta();
            enchantingBM.setDisplayName(Name);
            enchantingBM.setLore(Arrays.asList("", Lore1, Lore2));
            enchantingB.setItemMeta(enchantingBM);

            ShapedRecipe enchantingBR = new ShapedRecipe(enchantingB);
            enchantingBR.shape(
                    Main.names.getString("EnchantingBackPack.Crafting.Line1"),
                    Main.names.getString("EnchantingBackPack.Crafting.Line2"),
                    Main.names.getString("EnchantingBackPack.Crafting.Line3")
            );

            if(!Main.names.getString("EnchantingBackPack.Crafting.Material1").equals("null")) {
                enchantingBR.setIngredient('*', Material.getMaterial(Main.names.getString("EnchantingBackPack.Crafting.Material1")));
            }

            if(!Main.names.getString("EnchantingBackPack.Crafting.Material2").equals("null")) {
                enchantingBR.setIngredient('@', Material.getMaterial(Main.names.getString("EnchantingBackPack.Crafting.Material2")));
            }

            if(!Main.names.getString("EnchantingBackPack.Crafting.Material3").equals("null")) {
                enchantingBR.setIngredient('+', Material.getMaterial(Main.names.getString("EnchantingBackPack.Crafting.Material3")));
            }

            if(!Main.names.getString("EnchantingBackPack.Crafting.Material4").equals("null")) {
                enchantingBR.setIngredient('%', Material.getMaterial(Main.names.getString("EnchantingBackPack.Crafting.Material4")));
            }

            if(!Main.names.getString("EnchantingBackPack.Crafting.Material5").equals("null")) {
                enchantingBR.setIngredient('&', Material.getMaterial(Main.names.getString("EnchantingBackPack.Crafting.Material5")));
            }

            Bukkit.getServer().addRecipe(enchantingBR);
        }

        if(Main.names.getBoolean("FurnaceBackPack.Enable")) {
            String Name = ChatColor.translateAlternateColorCodes('&', Main.names.getString("FurnaceBackPack.Name"));
            String Lore1 = ChatColor.translateAlternateColorCodes('&', Main.names.getString("FurnaceBackPack.Desc1"));
            String Lore2 = ChatColor.translateAlternateColorCodes('&', Main.names.getString("FurnaceBackPack.Desc2"));

            ItemMeta furnaceBM = furnaceB.getItemMeta();
            furnaceBM.setDisplayName(Name);
            furnaceBM.setLore(Arrays.asList("", Lore1, Lore2));
            furnaceB.setItemMeta(furnaceBM);

            ShapedRecipe furnaceBR = new ShapedRecipe(furnaceB);
            furnaceBR.shape(
                    Main.names.getString("FurnaceBackPack.Crafting.Line1"),
                    Main.names.getString("FurnaceBackPack.Crafting.Line2"),
                    Main.names.getString("FurnaceBackPack.Crafting.Line3")
            );

            if(!Main.names.getString("FurnaceBackPack.Crafting.Material1").equals("null")) {
                furnaceBR.setIngredient('*', Material.getMaterial(Main.names.getString("FurnaceBackPack.Crafting.Material1")));
            }

            if(!Main.names.getString("FurnaceBackPack.Crafting.Material2").equals("null")) {
                furnaceBR.setIngredient('@', Material.getMaterial(Main.names.getString("FurnaceBackPack.Crafting.Material2")));
            }

            if(!Main.names.getString("FurnaceBackPack.Crafting.Material3").equals("null")) {
                furnaceBR.setIngredient('+', Material.getMaterial(Main.names.getString("FurnaceBackPack.Crafting.Material3")));
            }

            if(!Main.names.getString("FurnaceBackPack.Crafting.Material4").equals("null")) {
                furnaceBR.setIngredient('%', Material.getMaterial(Main.names.getString("FurnaceBackPack.Crafting.Material4")));
            }

            if(!Main.names.getString("FurnaceBackPack.Crafting.Material5").equals("null")) {
                furnaceBR.setIngredient('&', Material.getMaterial(Main.names.getString("FurnaceBackPack.Crafting.Material5")));
            }

            Bukkit.getServer().addRecipe(furnaceBR);
        }

    }
}