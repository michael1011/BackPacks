package at.michael1011.backpacks;

import org.bukkit.Sound;

import java.util.List;

public class BackPack {

    public static class Type {
        public static final Type normal = new Type("normal");
        public static final Type ender = new Type("ender");
        public static final Type crafting = new Type("crafting");
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

    private static String name;

    private static Type type;
    private static int slots = 0;
    private static Boolean furnaceGui = false;

    private static List<String> lore;
    private static String inventoryTitle;
    private static Sound openSound;
    private static Sound closeSound;

    public BackPack(String name, Type type, int slots, Boolean furnaceGui, List<String> lore, String inventoryTitle,
                    Sound openSound, Sound closeSound) {

        BackPack.name = name;

        BackPack.type = type;
        BackPack.slots = slots;
        BackPack.furnaceGui = furnaceGui;

        BackPack.lore = lore;
        BackPack.inventoryTitle = inventoryTitle;
        BackPack.openSound = openSound;
        BackPack.closeSound = closeSound;
    }

    public void setName(String name) {
        BackPack.name = name;
    }

    public void setType(Type type) {
        BackPack.type = type;
    }

    public void setSlots(int slots) {
        BackPack.slots = slots;
    }

    public void setFurnaceGui(Boolean furnaceGui) {
        BackPack.furnaceGui = furnaceGui;
    }

    public void setLore(List<String> lore) {
        BackPack.lore = lore;
    }

    public void setInventoryTitle(String inventoryTitle) {
        BackPack.inventoryTitle = inventoryTitle;
    }

    public void setOpenSound(Sound openSound) {
        BackPack.openSound = openSound;
    }

    public void setCloseSound(Sound closeSound) {
        BackPack.closeSound = closeSound;
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
