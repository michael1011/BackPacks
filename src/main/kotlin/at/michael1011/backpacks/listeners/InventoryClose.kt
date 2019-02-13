package at.michael1011.backpacks.listeners

import at.michael1011.backpacks.database.Database
import at.michael1011.backpacks.items.serializeItem
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

class InventoryClose(private val database: Database) : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun onInventoryClose(event: InventoryCloseEvent) {
        val uuid = event.player.uniqueId.toString()
        val backpackId = RightClick.openBackpacks[uuid]

        if (backpackId != null) {
            saveInventory(uuid, backpackId, event.inventory)
        }
    }

    private fun saveInventory(uuid: String, backpackId: Int, inventory: Inventory) {
        database.runAsync(Runnable {
            val insert = database.connection.prepareStatement("INSERT INTO items VALUES (?, ?, ?, ?, ?, ?, ?)")

            for (i in 0 until inventory.size) {
                val item = inventory.getItem(i)

                if (item != null) {
                    serializeItem(insert, backpackId, i, item)
                    insert.addBatch()
                }
            }

            insert.executeBatch()

            RightClick.openBackpacks.remove(uuid)
        })
    }
}