package at.michael1011.backpacks.commands

import at.michael1011.backpacks.Logger
import org.bukkit.command.CommandSender
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration

class CommandLogger(private val messages: YamlConfiguration) {
    private val prefix = Logger.formatColorCodes(messages.getString("prefix"))

    fun printPlayerNotFound(sender: CommandSender, player: String) {
        val message = messages.getString("help.playerNotFound")

        printFormattedMessage(sender, Logger.replaceVariable(message, "%player%", player))
    }

    fun printHelp(sender: CommandSender, messageSection: ConfigurationSection) {
        messageSection.getStringList("help").forEach {
            printFormattedMessage(sender, it)
        }
    }

    fun printNoPermission(sender: CommandSender) {
        printFormattedMessage(sender, messages.getString("help.noPermission"))
    }

    fun printFormattedMessage(sender: CommandSender, message: String) {
        sender.sendMessage(prefix + Logger.formatColorCodes(message))
    }
}