package at.michael1011.backpacks.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

public class EntityDeathTest {

    @Test
    public void smelt() throws Exception {
        List<ItemStack> toDrop = Arrays.asList(new ItemStack(Material.RAW_CHICKEN), new ItemStack(Material.RAW_BEEF),
                new ItemStack(Material.PORK), new ItemStack(Material.MUTTON), new ItemStack(Material.RABBIT),
                new ItemStack(Material.STONE));

        Location loc = Mockito.mock(Location.class);

        Mockito.when(loc.getWorld()).thenReturn(Mockito.mock(World.class));

        EntityDeath.smelt(toDrop, loc);
    }

}