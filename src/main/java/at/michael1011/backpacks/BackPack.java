package at.michael1011.backpacks;

import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.List;

public class BackPack {

    public static class Type {
        public static final Type normal = new Type("normal");
        public static final Type ender = new Type("ender");
        public static final Type crafting = new Type("crafting");
        public static final Type trash = new Type("trash");
        public static final Type furnace = new Type("furnace");

        private String type;

        public Type(String type) {
            this.type = type;
        }

        public Boolean equals(Type compare) {
            return this.toString().equals(compare.toString());
        }

        public String toString() {
            return type;
        }

    }

    private String name;

    private Type type = Type.normal;
    private Material material = null;
    private int slots = 0;
    private Boolean furnaceGui = false;

    private String itemTitle = "";
    private List<String> lore = new ArrayList<>();
    private String inventoryTitle = "";

    private String craftingRecipe = "";
    private String materials = "";

    private Sound openSound = null;
    private Sound closeSound = null;

    BackPack(String name, Type type, Material material, int slots, Boolean furnaceGui, String itemTitle, List<String> lore, String inventoryTitle,
             Sound openSound, Sound closeSound) {

        this.name = name;

        this.material = material;
        this.type = type;
        this.slots = slots;
        this.furnaceGui = furnaceGui;

        this.itemTitle = itemTitle;
        this.lore = lore;
        this.inventoryTitle = inventoryTitle;

        this.openSound = openSound;
        this.closeSound = closeSound;
    }

    BackPack(String name, Type type, Material material, int slots, Boolean furnaceGui, String itemTitle, List<String> lore, String inventoryTitle,
             String craftingRecipe, String materials, Sound openSound, Sound closeSound) {

        this.name = name;

        this.material = material;
        this.type = type;
        this.slots = slots;
        this.furnaceGui = furnaceGui;

        this.itemTitle = itemTitle;
        this.lore = lore;
        this.inventoryTitle = inventoryTitle;

        this.craftingRecipe = craftingRecipe;
        this.materials = materials;

        this.openSound = openSound;
        this.closeSound = closeSound;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public void setFurnaceGui(Boolean furnaceGui) {
        this.furnaceGui = furnaceGui;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public void setInventoryTitle(String inventoryTitle) {
        this.inventoryTitle = inventoryTitle;
    }

    public void setCraftingRecipe(String craftingRecipe) {
        this.craftingRecipe = craftingRecipe;
    }

    public void setMaterials(String materials) {
        this.materials = materials;
    }

    public void setOpenSound(Sound openSound) {
        this.openSound = openSound;
    }

    public void setCloseSound(Sound closeSound) {
        this.closeSound = closeSound;
    }


    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Material getMaterial() {
        return material;
    }

    public int getSlots() {
        return slots;
    }

    public Boolean getFurnaceGui() {
        return furnaceGui;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public List<String> getLore() {
        return lore;
    }

    public String getLoreString() {
        StringBuilder builder = new StringBuilder();

        for (String entry : lore) {
            if (builder.length() > 0) {
                builder.append("\n");
            }

            builder.append(entry);

        }

        return builder.toString();
    }

    public String getInventoryTitle() {
        return inventoryTitle;
    }

    public String getCraftingRecipe() {
        return craftingRecipe;
    }

    public String getMaterials() {
        return materials;
    }

    public Sound getOpenSound() {
        return openSound;
    }

    public Sound getCloseSound() {
        return closeSound;
    }

}
