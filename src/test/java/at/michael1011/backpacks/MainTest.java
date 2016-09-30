package at.michael1011.backpacks;

import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.io.File;
import java.io.IOException;

public class MainTest {

    @Test
    public void testLoadFiles() throws IOException, InvalidConfigurationException {
        Main.loadFiles(PowerMockito.mock(Main.class), new File("src/test/resources"), false);
    }

}