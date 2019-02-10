package at.michael1011.backpacks.listeners

import at.michael1011.backpacks.database.Database
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class Join(private val database: Database) : Listener {
    // In case of a reload the database also has to be updated
    init {
        Bukkit.getServer().onlinePlayers.forEach {
            updatePlayer(it)
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        updatePlayer(event.player)
    }

    private fun updatePlayer(player: Player) {
        // Insert a new player or just update the name if the UUID is already in the database
        // The exact queries vary from SQL implementation therefore we need two different ones for SQLite and MySQL
        val statement = if (database.isMysql) {
            database.connection.prepareStatement(
                "INSERT INTO players VALUES (?, ?) ON DUPLICATE KEY UPDATE name = ?"
            )
        } else {
            database.connection.prepareStatement(
                "INSERT INTO players VALUES (?, ?) ON CONFLICT(uuid) DO UPDATE SET name = ?"
            )
        }

        statement.setString(1, player.uniqueId.toString())
        statement.setString(2, player.name)
        statement.setString(3, player.name)

        database.executeStatement(statement) {}
    }
}
