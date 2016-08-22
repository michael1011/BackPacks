package at.michael1011.backpacks.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

public class JoinTest {

    @Test
    public void join() throws Exception {
        SecureRandom random = new SecureRandom();

        String randomName = new BigInteger(130, random).toString(20);

        UUID uuid = UUID.randomUUID();

        PlayerJoinEvent event = PowerMockito.mock(PlayerJoinEvent.class);

        Player player = PowerMockito.mock(Player.class);

        PowerMockito.when(player.getName()).thenReturn(randomName);
        PowerMockito.when(player.getDisplayName()).thenReturn(randomName);
        PowerMockito.when(player.getUniqueId()).thenReturn(uuid);

        PowerMockito.when(event.getPlayer()).thenReturn(player);

        Join listener = new Join();

        listener.join(event);

    }

}