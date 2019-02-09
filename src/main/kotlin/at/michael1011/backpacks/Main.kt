package at.michael1011.backpacks

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin;

@Suppress("unused")
class Main : JavaPlugin() {
    @Override
    override fun onEnable() {
        val console = Bukkit.getServer().consoleSender
        console.sendMessage("Enabled")
    }
}
