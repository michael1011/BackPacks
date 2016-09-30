package at.michael1011.backpacks.tasks;

import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.MainTest;
import at.michael1011.backpacks.SQL;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import static at.michael1011.backpacks.Main.messages;
import static at.michael1011.backpacks.Main.prefix;
import static at.michael1011.backpacks.SQL.createCon;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReconnectTest {

    @Test
    public void reconnectTask() throws Exception {
        MainTest loadFiles = new MainTest();
        loadFiles.loadFiles();

        CommandSender sender = PowerMockito.mock(CommandSender.class);

        createCon("localhost", "3306", "bpTest", "bpTest", "bpTest");

        assertTrue(SQL.checkCon());

        Reconnect.reconnectTask(sender, false);

        assertTrue(SQL.checkCon());
        Mockito.verify(sender, Mockito.never()).sendMessage(Matchers.anyString());

        SQL.closeCon();

        assertFalse(SQL.checkCon());

        Main.config.set("MySQL.password", "notWorking");

        Reconnect.reconnectTask(sender, false);

        assertFalse(SQL.checkCon());
        Mockito.verify(sender, Mockito.times(1)).sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                messages.getString("MySQL.failedToConnect")));
        Mockito.verify(sender, Mockito.times(1)).sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                messages.getString("MySQL.failedToConnectCheck")));

    }

}