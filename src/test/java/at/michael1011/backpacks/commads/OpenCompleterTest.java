package at.michael1011.backpacks.commads;

import at.michael1011.backpacks.Crafting;
import at.michael1011.backpacks.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class OpenCompleterTest {

    @Test
    public void onTabComplete() throws Exception {
        Crafting.availableList = GiveCompleterTest.rawData;
        Main.availablePlayers = Arrays.asList("player1", "michael1011", "notch", "4player");

        OpenCompleter completer = new OpenCompleter();

        Command command = Mockito.mock(Command.class);
        CommandSender sender = Mockito.mock(CommandSender.class);

        String[] one = {"test"};
        String[] two = {"1", "player"};
        String[] three = {"1", "2", "3"};

        assertTrue(completer.onTabComplete(sender, command, "", one).equals(GiveCompleterTest.rawDataTest));
        assertTrue(completer.onTabComplete(sender, command, "", two).equals(Arrays.asList("player1", "4player")));
        assertTrue(completer.onTabComplete(sender, command, "", three) == null);
    }

}