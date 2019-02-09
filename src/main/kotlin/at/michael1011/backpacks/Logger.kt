package at.michael1011.backpacks

import org.bukkit.Bukkit
import org.bukkit.ChatColor

class Logger(private val prefix: String) {
    private val console = Bukkit.getConsoleSender()

    fun sendMessage(message: String) {
        console.sendMessage(
            ChatColor.translateAlternateColorCodes(
                '&',
                prefix + message)
        )
    }
}