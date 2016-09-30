package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.MainTest;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.material.Crops;
import org.junit.Test;
import org.mockito.Mockito;

import static at.michael1011.backpacks.Main.messages;
import static at.michael1011.backpacks.Main.prefix;
import static org.junit.Assert.assertTrue;

public class BlockBreakTest {

    @Test
    public void checkCoal() throws Exception {
        MainTest loadFiles = new MainTest();
        loadFiles.loadFiles();

        Material[] materials = {Material.STONE, Material.POTATO};

        World world = Mockito.mock(World.class);
        Location loc = new Location(world, 0, 0, 0);

        Player p = Mockito.mock(Player.class);

        for(int i = 0; i < materials.length; i++) {
            BlockBreak.checkCoal(0, null, null, null, materials[i], loc, p);

            Mockito.verify(p, Mockito.times(i+1)).sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                    messages.getString("BackPacks.furnaceBackPack.noCoal")));
        }
    }

    @Test
    public void smelt() throws Exception {
        Block block = Mockito.mock(Block.class);

        World world = Mockito.mock(World.class);
        Location loc = new Location(world, 0, 0, 0);

        Mockito.when(loc.getWorld().spawn(loc, ExperienceOrb.class)).thenReturn(Mockito.mock(ExperienceOrb.class));
        Mockito.when(block.getLocation()).thenReturn(loc);

        Crops seeded = Mockito.mock(Crops.class);
        Mockito.when(seeded.getState()).thenReturn(CropState.SEEDED);

        Crops ripe = Mockito.mock(Crops.class);
        Mockito.when(ripe.getState()).thenReturn(CropState.RIPE);

        Material[] materials = {Material.IRON_ORE, Material.GOLD_ORE, Material.POTATO, Material.POTATO};

        //noinspection ForLoopReplaceableByForEach
        for(int i = 0; i < materials.length; i++) {
            Crops crops = seeded;

            if(i == 3) {
                crops = ripe;
            }

            BlockBreak.smelt(block, crops, materials[i]);
        }

    }

    @Test
    public void random() throws Exception {
        for(int i = 0; i < 4; i++) {
            int random = BlockBreak.random();

            assertTrue(random > 0);
            assertTrue(random < 4);
        }
    }

}