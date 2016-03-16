package main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Crafting {

    public main plugin;

    public Crafting(main main) {
        this.plugin = main;
    }

    public static ItemStack littleB = new ItemStack(Material.getMaterial(main.names.getString("LittleBackPack.Material")), 1);
    public static ItemStack normalB = new ItemStack(Material.getMaterial(main.names.getString("NormalBackPack.Material")), 1);
    public static ItemStack enderB = new ItemStack(Material.getMaterial(main.names.getString("EnderBackPack.Material")), 1);
    public static ItemStack craftingB = new ItemStack(Material.getMaterial(main.names.getString("WorkbenchBackPack.Material")), 1);

    static void CraftingB() {
        if(main.names.getBoolean("LittleBackPack.Enable")) {
            String Name = ChatColor.translateAlternateColorCodes('&', main.names.getString("LittleBackPack.Name"));
            String Lore1 = ChatColor.translateAlternateColorCodes('&', main.names.getString("LittleBackPack.Desc1"));
            String Lore2 = ChatColor.translateAlternateColorCodes('&', main.names.getString("LittleBackPack.Desc2"));

            ItemMeta littleBM = littleB.getItemMeta();
            littleBM.setDisplayName(Name);
            littleBM.setLore(Arrays.asList("", Lore1, Lore2));
            littleB.setItemMeta(littleBM);

            ShapedRecipe littleBR = new ShapedRecipe(littleB);
            littleBR.shape(
                    main.names.getString("LittleBackPack.Crafting.Line1"),
                    main.names.getString("LittleBackPack.Crafting.Line2"),
                    main.names.getString("LittleBackPack.Crafting.Line3")
            );

            if(!main.names.getString("LittleBackPack.Crafting.Material1").equals("null")) {
                littleBR.setIngredient('*', Material.getMaterial(main.names.getString("LittleBackPack.Crafting.Material1")));
            }

            if(!main.names.getString("LittleBackPack.Crafting.Material2").equals("null")) {
                littleBR.setIngredient('@', Material.getMaterial(main.names.getString("LittleBackPack.Crafting.Material2")));
            }

            if(!main.names.getString("LittleBackPack.Crafting.Material3").equals("null")) {
                littleBR.setIngredient('+', Material.getMaterial(main.names.getString("LittleBackPack.Crafting.Material3")));
            }

            if(!main.names.getString("LittleBackPack.Crafting.Material4").equals("null")) {
                littleBR.setIngredient('%', Material.getMaterial(main.names.getString("LittleBackPack.Crafting.Material4")));
            }

            if(!main.names.getString("LittleBackPack.Crafting.Material5").equals("null")) {
                littleBR.setIngredient('&', Material.getMaterial(main.names.getString("LittleBackPack.Crafting.Material5")));
            }

            Bukkit.getServer().addRecipe(littleBR);
        }


        if(main.names.getBoolean("NormalBackPack.Enable")) {
            String NameN = ChatColor.translateAlternateColorCodes('&', main.names.getString("NormalBackPack.Name"));
            String Lore1N = ChatColor.translateAlternateColorCodes('&', main.names.getString("NormalBackPack.Desc1"));
            String Lore2N = ChatColor.translateAlternateColorCodes('&', main.names.getString("NormalBackPack.Desc2"));

            ItemMeta normalBM = normalB.getItemMeta();
            normalBM.setDisplayName(NameN);
            normalBM.setLore(Arrays.asList("", Lore1N, Lore2N));
            normalB.setItemMeta(normalBM);

            ShapedRecipe normalBR = new ShapedRecipe(normalB);
            normalBR.shape(
                    main.names.getString("NormalBackPack.Crafting.Line1"),
                    main.names.getString("NormalBackPack.Crafting.Line2"),
                    main.names.getString("NormalBackPack.Crafting.Line3")
            );

            if(!main.names.getString("NormalBackPack.Crafting.Material1").equals("null")) {
                normalBR.setIngredient('*', Material.getMaterial(main.names.getString("NormalBackPack.Crafting.Material1")));
            }

            if(!main.names.getString("NormalBackPack.Crafting.Material2").equals("null")) {
                normalBR.setIngredient('@', Material.getMaterial(main.names.getString("NormalBackPack.Crafting.Material2")));
            }

            if(!main.names.getString("NormalBackPack.Crafting.Material3").equals("null")) {
                normalBR.setIngredient('+', Material.getMaterial(main.names.getString("NormalBackPack.Crafting.Material3")));
            }

            if(!main.names.getString("NormalBackPack.Crafting.Material4").equals("null")) {
                normalBR.setIngredient('%', Material.getMaterial(main.names.getString("NormalBackPack.Crafting.Material4")));
            }

            if(!main.names.getString("NormalBackPack.Crafting.Material5").equals("null")) {
                normalBR.setIngredient('&', Material.getMaterial(main.names.getString("NormalBackPack.Crafting.Material5")));
            }

            Bukkit.getServer().addRecipe(normalBR);
        }


        if(main.names.getBoolean("EnderBackPack.Enable")) {
            String NameE = ChatColor.translateAlternateColorCodes('&', main.names.getString("EnderBackPack.Name"));
            String Lore1E = ChatColor.translateAlternateColorCodes('&', main.names.getString("EnderBackPack.Desc1"));
            String Lore2E = ChatColor.translateAlternateColorCodes('&', main.names.getString("EnderBackPack.Desc2"));

            ItemMeta enderBM = enderB.getItemMeta();
            enderBM.setDisplayName(NameE);
            enderBM.setLore(Arrays.asList("", Lore1E, Lore2E));
            enderB.setItemMeta(enderBM);

            ShapedRecipe enderBR = new ShapedRecipe(enderB);
            enderBR.shape(
                    main.names.getString("EnderBackPack.Crafting.Line1"),
                    main.names.getString("EnderBackPack.Crafting.Line2"),
                    main.names.getString("EnderBackPack.Crafting.Line3")
            );

            if(!main.names.getString("EnderBackPack.Crafting.Material1").equals("null")) {
                enderBR.setIngredient('*', Material.getMaterial(main.names.getString("EnderBackPack.Crafting.Material1")));
            }

            if(!main.names.getString("EnderBackPack.Crafting.Material2").equals("null")) {
                enderBR.setIngredient('@', Material.getMaterial(main.names.getString("EnderBackPack.Crafting.Material2")));
            }

            if(!main.names.getString("EnderBackPack.Crafting.Material3").equals("null")) {
                enderBR.setIngredient('+', Material.getMaterial(main.names.getString("EnderBackPack.Crafting.Material3")));
            }

            if(!main.names.getString("EnderBackPack.Crafting.Material4").equals("null")) {
                enderBR.setIngredient('%', Material.getMaterial(main.names.getString("EnderBackPack.Crafting.Material4")));
            }

            if(!main.names.getString("EnderBackPack.Crafting.Material5").equals("null")) {
                enderBR.setIngredient('&', Material.getMaterial(main.names.getString("EnderBackPack.Crafting.Material5")));
            }

            Bukkit.getServer().addRecipe(enderBR);
        }


        if(main.names.getBoolean("WorkbenchBackPack.Enable")) {
            String NameE = ChatColor.translateAlternateColorCodes('&', main.names.getString("WorkbenchBackPack.Name"));
            String Lore1E = ChatColor.translateAlternateColorCodes('&', main.names.getString("WorkbenchBackPack.Desc1"));
            String Lore2E = ChatColor.translateAlternateColorCodes('&', main.names.getString("WorkbenchBackPack.Desc2"));

            ItemMeta craftingBM = craftingB.getItemMeta();
            craftingBM.setDisplayName(NameE);
            craftingBM.setLore(Arrays.asList("", Lore1E, Lore2E));
            craftingB.setItemMeta(craftingBM);

            ShapedRecipe craftingBR = new ShapedRecipe(craftingB);
            craftingBR.shape(
                    main.names.getString("WorkbenchBackPack.Crafting.Line1"),
                    main.names.getString("WorkbenchBackPack.Crafting.Line2"),
                    main.names.getString("WorkbenchBackPack.Crafting.Line3")
            );

            if(!main.names.getString("WorkbenchBackPack.Crafting.Material1").equals("null")) {
                craftingBR.setIngredient('*', Material.getMaterial(main.names.getString("WorkbenchBackPack.Crafting.Material1")));
            }

            if(!main.names.getString("WorkbenchBackPack.Crafting.Material2").equals("null")) {
                craftingBR.setIngredient('@', Material.getMaterial(main.names.getString("WorkbenchBackPack.Crafting.Material2")));
            }

            if(!main.names.getString("WorkbenchBackPack.Crafting.Material3").equals("null")) {
                craftingBR.setIngredient('+', Material.getMaterial(main.names.getString("WorkbenchBackPack.Crafting.Material3")));
            }

            if(!main.names.getString("WorkbenchBackPack.Crafting.Material4").equals("null")) {
                craftingBR.setIngredient('%', Material.getMaterial(main.names.getString("WorkbenchBackPack.Crafting.Material4")));
            }

            if(!main.names.getString("WorkbenchBackPack.Crafting.Material5").equals("null")) {
                craftingBR.setIngredient('&', Material.getMaterial(main.names.getString("WorkbenchBackPack.Crafting.Material5")));
            }

            Bukkit.getServer().addRecipe(craftingBR);
        }
    }
}