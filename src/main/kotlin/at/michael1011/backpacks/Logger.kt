package at.michael1011.backpacks

import org.bukkit.Bukkit
import org.bukkit.ChatColor

class Logger(private val prefix: String) {
    private val console = Bukkit.getConsoleSender()

    fun error(message: String) {
        log("&4$message")
    }

    fun warn(message: String) {
        log("&6$message")
    }

    fun log(message: String) {
        console.sendMessage(
            formatColorCodes(prefix + message)
        )
    }

    companion object {
        fun formatColorCodes(message: String): String {
            return ChatColor.translateAlternateColorCodes('&', message)
        }
    }
}