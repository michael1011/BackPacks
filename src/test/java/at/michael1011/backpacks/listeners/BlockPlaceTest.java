package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.Crafting;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlockPlaceTest {

    @Test
    public void blockPlace() throws Exception {
        Crafting.loreMap = new HashMap<>();

        List<String> contains = Arrays.asList("test", "test");
        List<String> containsNot = Arrays.asList("test2", "test2");

        Crafting.loreMap.put(contains, "test");

        assertTrue(BlockPlace.backPackExists(contains));
        assertFalse(BlockPlace.backPackExists(containsNot));
    }

}