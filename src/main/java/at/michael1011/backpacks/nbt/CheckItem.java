package at.michael1011.backpacks.nbt;

import at.michael1011.backpacks.Crafting;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static at.michael1011.backpacks.Main.version;

public class CheckItem {

    public static String checkItem(ItemStack item) {
        try {
            Object nbtItemStack = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack")
                    .getMethod("asNMSCopy", ItemStack.class)
                    .invoke(null, item);

            Object nbtTag = nbtItemStack.getClass().getMethod("getTag").invoke(nbtItemStack);

            if (nbtTag != null) {
                Method getNbtBase = nbtTag.getClass().getMethod("get", String.class);

                Object nbtBase = getNbtBase.invoke(nbtTag, Crafting.nbtKey);

                if (nbtBase != null) {
                    String backPack = nbtBase.toString();

                    backPack = backPack.substring(1, backPack.length()-1);

                    return backPack;
                }
            }

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
