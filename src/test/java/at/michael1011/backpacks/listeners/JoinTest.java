package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.Updater;
import org.bukkit.entity.Player;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

public class JoinTest {

    @Test
    public void checkForUpdates() throws Exception {
        Updater.updateAvailable = true;

        Updater.newVersion = "newVersion";
        Updater.newVersionDownload = "newVersionDownload";

        Player p = PowerMockito.mock(Player.class);

        Mockito.when(p.hasPermission("backpacks.update")).thenReturn(true);

        Join.checkForUpdates(p);

        Mockito.verify(p, Mockito.times(1)).sendMessage(Updater.newVersion);
        Mockito.verify(p, Mockito.times(1)).sendMessage(Updater.newVersionDownload);
    }

}