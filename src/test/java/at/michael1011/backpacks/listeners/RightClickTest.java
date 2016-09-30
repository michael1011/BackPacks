package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.MainTest;
import org.bukkit.ChatColor;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class RightClickTest {

    @Test
    public void getLore() throws Exception {
        MainTest loadFiles = new MainTest();
        loadFiles.loadFiles();

        String path = "ores.description";

        assertTrue(RightClick.getLore(path).equals(loreTest(path)));
    }

    private static List<String> loreTest(String path) {
        String lore = "";

        Map<String, Object> loreSec = Main.furnaceGui.getConfigurationSection(path).getValues(true);

        for(Map.Entry<String, Object> ent : loreSec.entrySet()) {
            lore = lore+","+ChatColor.translateAlternateColorCodes('&', ent.getValue().toString());
        }

        return Arrays.asList(lore.split("\\s*,\\s*"));
    }

    @Test
    public void getColor() throws Exception {
        assertTrue(RightClick.getColor(true) == 5);
        assertTrue(RightClick.getColor(false) == 14);
    }

}