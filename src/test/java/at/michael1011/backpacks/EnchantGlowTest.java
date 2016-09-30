package at.michael1011.backpacks;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EnchantGlowTest {

    @Test
    public void testReturns() throws Exception {
        EnchantGlow test = new EnchantGlow();

        assertTrue(test.canEnchantItem(PowerMockito.mock(ItemStack.class)));
        assertFalse(test.conflictsWith(PowerMockito.mock(Enchantment.class)));
        assertTrue(test.getItemTarget() == null);
        assertTrue(test.getMaxLevel() == 10);
        assertTrue(test.getName().equals("Glow"));
        assertTrue(test.getStartLevel() == 1);

        assertTrue(EnchantGlow.getGlow() == EnchantGlow.glow && EnchantGlow.createdNew);
        assertTrue(EnchantGlow.getGlow() == EnchantGlow.glow && !EnchantGlow.createdNew);
    }

}