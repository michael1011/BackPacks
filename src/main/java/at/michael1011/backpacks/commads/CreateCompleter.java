package at.michael1011.backpacks.commads;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

import static at.michael1011.backpacks.commads.GiveCompleter.filterList;

class CreateCompleter implements TabCompleter {

    private List<String> functions = Arrays.asList("name", "displayname", "description", "inventorytitle", "sound",
            "material", "crafting", "materials", "type", "slots", "gui", "preview", "finish", "item");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length <= 1) {
            return filterList(args[0], functions);

        } else if (args.length == 2) {
            switch (args[0]) {
                case "sound":
                    return filterList(args[1], Arrays.asList("open", "close"));

                case "finish":
                    return filterList(args[1], Arrays.asList("true", "false"));
            }

        }

        return null;
    }

}
