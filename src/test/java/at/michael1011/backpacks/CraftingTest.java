package at.michael1011.backpacks;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import static at.michael1011.backpacks.Main.messages;
import static at.michael1011.backpacks.Main.prefix;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CraftingTest {

    @Test
    public void testGetItemStack() throws Exception {
        MainTest loadFiles = new MainTest();
        loadFiles.testLoadFiles();

        CommandSender sender = PowerMockito.mock(CommandSender.class);

        assertTrue(Crafting.getItemStack(sender, "BackPacks.notDivisibleByNine.", "notDivisibleByNine", false) == null);
        assertFalse(Crafting.slotsDivisible);

        Mockito.verify(sender, Mockito.never()).sendMessage(Matchers.anyString());

        Crafting.slotsDivisible = true;

        assertTrue(Crafting.getItemStack(sender, "BackPacks.materialNotValid.", "materialNotValid", false) == null);
        assertTrue(Crafting.slotsDivisible);

        Mockito.verify(sender, Mockito.times(1)).sendMessage(prefix+ ChatColor.translateAlternateColorCodes('&',
                messages.getString("Help.materialNotValid").replaceAll("%material%", "WATC")
                        .replaceAll("%backpack%", "materialNotValid")));

        testWorkingBackPack("works", sender, false);

        Mockito.verify(sender, Mockito.times(1)).sendMessage(Matchers.anyString());

        testWorkingBackPack("worksFurnace", sender, true);

        Mockito.verify(sender, Mockito.times(1)).sendMessage(Matchers.anyString());
    }

    private void testWorkingBackPack(String backPack, CommandSender sender, Boolean furnace) {
        assertTrue(Crafting.getItemStack(sender, "BackPacks."+backPack+".", backPack, false) != null);
        assertTrue(Crafting.slots.get(backPack) == Main.config.getInt("BackPacks."+backPack+".slots"));
        assertTrue(Crafting.type.get(backPack).equals(Main.config.getString("BackPacks."+backPack+".type")));
        assertTrue(Crafting.slotsDivisible);

        if(furnace) {
            assertTrue(Crafting.furnaceGui.get(backPack).equals(Main.config.getString("BackPacks."+backPack+".gui.enabled")));

        } else {
            assertFalse(Crafting.furnaceGui.containsKey(backPack));
        }

    }

}