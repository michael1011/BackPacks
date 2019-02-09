package at.michael1011.backpacks

import org.bukkit.plugin.java.JavaPlugin;

@Suppress("unused")
class Main : JavaPlugin() {
    @Override
    override fun onEnable() {
        val config = Config(this)
        config.loadFiles()

        val logger = Logger(config.messages.getString("prefix"))
        logger.sendMessage("Plugin enabled")
    }
}
