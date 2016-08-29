package at.michael1011.backpacks.commads;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

import static at.michael1011.backpacks.Crafting.availableList;
import static at.michael1011.backpacks.Main.availablePlayers;
import static at.michael1011.backpacks.commads.GiveCompleter.filterList;

class OpenCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length <= 1) {
            return filterList(args[0], availableList);

        } else if(args.length == 2) {
            return filterList(args[1], availablePlayers);
        }

        return null;
    }

}
