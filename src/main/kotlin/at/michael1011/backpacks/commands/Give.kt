package at.michael1011.backpacks.commands

import at.michael1011.backpacks.Logger
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Give(
    private val logger: CommandLogger,
    private val giveMessages: ConfigurationSection,
    private val backpacks: HashMap<String, ItemStack>) : CommandExecutor {

    @Override
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (!sender.hasPermission("backpacks.give")) {
            logger.printNoPermission(sender)
            return true
        }

        if (args.size == 1 && sender is Player) {
            giveBackPack(sender, sender, args[0])
        } else if (args.size == 2) {
            val player = Bukkit.getPlayer(args[1])

            if (player != null) {
                giveBackPack(sender, player, args[0])
            } else {
                logger.printPlayerNotFound(sender, args[1])
            }
        } else {
            logger.printHelp(sender, giveMessages)
        }

        return true
    }

    private fun giveBackPack(sender: CommandSender, player: Player, backpackName: String) {
        val backpack = backpacks[backpackName]

        if (backpack != null) {
            player.inventory.addItem(backpack)
            val message = Logger.replaceVariable(
                Logger.replaceVariable(
                    giveMessages.getString("success"),
                    "%backpack%",
                    backpackName),
                "%player%",
                player.displayName
            )

            logger.printMessage(sender, message)
        } else {
            val message = Logger.replaceVariable(giveMessages.getString("backpackNotFound"), "%backpack%", backpackName)
            logger.printMessage(sender, message)
        }
    }
}