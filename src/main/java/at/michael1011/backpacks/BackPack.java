package at.michael1011.backpacks;

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
    private int slots = 0;
    private Boolean furnaceGui = false;

    private List<String> lore = new ArrayList<>();
    private String inventoryTitle = "";

    private String craftingRecipe = "";
    private String materials = "";

    private Sound openSound = null;
    private Sound closeSound = null;

    BackPack(String name, Type type, int slots, Boolean furnaceGui, List<String> lore, String inventoryTitle,
             Sound openSound, Sound closeSound) {

        this.name = name;

        this.type = type;
        this.slots = slots;
        this.furnaceGui = furnaceGui;

        this.lore = lore;
        this.inventoryTitle = inventoryTitle;
        this.openSound = openSound;
        this.closeSound = closeSound;
    }

    BackPack(String name, Type type, int slots, Boolean furnaceGui, List<String> lore, String inventoryTitle,
             String craftingRecipe, String materials, Sound openSound, Sound closeSound) {

        this.name = name;

        this.type = type;
        this.slots = slots;
        this.furnaceGui = furnaceGui;

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

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public void setFurnaceGui(Boolean furnaceGui) {
        this.furnaceGui = furnaceGui;
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

    public int getSlots() {
        return slots;
    }

    public Boolean getFurnaceGui() {
        return furnaceGui;
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
