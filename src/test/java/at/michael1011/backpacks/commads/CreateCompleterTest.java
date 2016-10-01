package at.michael1011.backpacks.commads;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.Assert.assertTrue;

public class CreateCompleterTest {

    @Test
    public void onTabComplete() throws Exception {
        CreateCompleter completer = new CreateCompleter();

        Command command = Mockito.mock(Command.class);
        CommandSender sender = Mockito.mock(CommandSender.class);

        String[] noValues = {""};
        String[] twoValues = {"one", "two"};

        String[] display = {"display"};

        assertTrue(completer.onTabComplete(sender, command, "", noValues).equals(completer.functions));
        assertTrue(completer.onTabComplete(sender, command, "", twoValues) == null);

        assertTrue(completer.onTabComplete(sender, command, "", display).equals(Collections.singletonList("displayname")));

    }

}