package at.michael1011.backpacks;

import org.bukkit.Sound;

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

    private Type type;
    private int slots = 0;
    private Boolean furnaceGui = false;

    private List<String> lore;
    private String inventoryTitle;
    private Sound openSound;
    private Sound closeSound;

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

    public String getInventoryTitle() {
        return inventoryTitle;
    }

    public Sound getOpenSound() {
        return openSound;
    }

    public Sound getCloseSound() {
        return closeSound;
    }

}
