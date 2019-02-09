package at.michael1011.backpacks.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Give(private val backpacks: HashMap<String, ItemStack>) : CommandExecutor {

    @Override
    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        if (args!!.size == 1 && sender is Player) {
            sender.inventory.addItem(backpacks[args[0]])
        }

        return true
    }
}