package at.michael1011.backpacks.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;
import org.mockito.Mockito;

public class PlayerDeathTest {

    @Test
    public void dropInventory() throws Exception {
        ItemStack[] toDrop = {new ItemStack(Material.STONE), new ItemStack(Material.STONE)};

        Player p = Mockito.mock(Player.class);

        Mockito.when(p.getWorld()).thenReturn(Mockito.mock(World.class));
        Mockito.when(p.getLocation()).thenReturn(new Location(Mockito.mock(World.class), 0, 0, 0));

        PlayerDeath.dropInventory(toDrop, p);
    }

}