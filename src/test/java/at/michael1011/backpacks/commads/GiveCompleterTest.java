package at.michael1011.backpacks.commads;

import at.michael1011.backpacks.Crafting;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class GiveCompleterTest {

    static List<String> rawData = Arrays.asList("test1", "asdf", "3test");
    static List<String> rawDataTest = Arrays.asList("test1", "3test");

    @Test
    public void onTabComplete() throws Exception {
        Crafting.availableList = rawData;

        GiveCompleter completer = new GiveCompleter();

        Command command = Mockito.mock(Command.class);
        CommandSender sender = Mockito.mock(CommandSender.class);

        String[] one = {"test"};
        String[] three = {"1", "2", "3"};

        assertTrue(completer.onTabComplete(sender, command, "", one).equals(rawDataTest));
        assertTrue(completer.onTabComplete(sender, command, "", three) == null);
    }

    @Test
    public void filterList() throws Exception {
        assertTrue(GiveCompleter.filterList("", rawData).equals(rawData));
        assertTrue(GiveCompleter.filterList("test", rawData).equals(rawDataTest));
        assertTrue(GiveCompleter.filterList("asdf", rawData).equals(Collections.singletonList("asdf")));
        assertTrue(GiveCompleter.filterList("nothing", rawData).equals(new ArrayList<String>()));
    }

}