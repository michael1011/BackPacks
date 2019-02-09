package at.michael1011.backpacks

import at.michael1011.backpacks.database.Database
import at.michael1011.backpacks.database.MysqlCredentials
import org.bukkit.plugin.java.JavaPlugin;
import java.lang.Exception

@Suppress("unused")
class Main : JavaPlugin() {
    @Override
    override fun onEnable() {
        val config = Config(this)
        val logger = Logger(config.messages.getString("prefix"))

        try {
            val database = Database(
                this,
                config.config.getString("database"),
                MysqlCredentials(config.config))

            logger.log("Connected to database")

            logger.log("Plugin enabled")
        } catch (error: Exception) {
            logger.error("Could not connect to database: ${error.message}")
        }
    }
}
