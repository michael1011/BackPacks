package at.michael1011.backpacks

import at.michael1011.backpacks.commands.registerCommands
import at.michael1011.backpacks.crafting.Crafting
import at.michael1011.backpacks.database.Database
import at.michael1011.backpacks.database.MysqlCredentials
import at.michael1011.backpacks.listeners.registerListeners
import org.bukkit.plugin.java.JavaPlugin
import java.lang.Exception

@Suppress("unused")
class Main : JavaPlugin() {
    private lateinit var database: Database

    @Override
    override fun onEnable() {
        val config = Config(this)
        val logger = Logger(config.messages.getString("prefix"))

        try {
            database = Database(
                this,
                config.config.getString("database"),
                MysqlCredentials(config.config))

            logger.log("Connected to database")
        } catch (exception: Exception) {
            logger.error("Could not connect to database: ${exception.message}")
        }

        val crafting = Crafting(logger, config.config)
        logger.log("Enabled backpacks: ${crafting.backpacks.keys}")

        registerListeners(this, database, crafting.backpacks, crafting.backpackConfigs)
        registerCommands(this, config.messages, crafting.backpacks)

        logger.log("Plugin enabled")
    }
}
