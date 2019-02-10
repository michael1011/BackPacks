package at.michael1011.backpacks.listeners

import at.michael1011.backpacks.crafting.BackpackConfig
import at.michael1011.backpacks.database.Database
import at.michael1011.backpacks.items.parseItem
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.sql.ResultSet
import java.sql.Statement

// TODO: identify backpacks with NBT tags
class RightClick(
    private val database: Database,
    backpacks: HashMap<String, ItemStack>,
    private val backpackConfigs: HashMap<String, BackpackConfig>
) : Listener {
    private val server = Bukkit.getServer()

    // A map between the display names and configurations of backpacks
    private val backpackNames = HashMap<String, String>()

    init {
        backpacks.forEach {
            val displayName = it.value.itemMeta.displayName
            val config = backpackConfigs[it.key]

            if (config != null) {
                backpackNames[displayName] = it.key
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        // Return if there is no item or the item has no ItemMeta
        val item = event.item ?: return

        if (!item.hasItemMeta()) {
            return
        }

        // Return if the player has a backpack open or one of its backpacks is still saving
        val player = event.player
        val uuid = player.uniqueId.toString()

        if (openBackpacks.containsKey(uuid)) {
            return
        }

        val action = event.action

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            val itemMeta = event.item.itemMeta
            val name = backpackNames[itemMeta.displayName]

            if (name != null) {
                val config = backpackConfigs[name]!!


                database.runAsync(Runnable {
                    val query = database.connection.prepareStatement("SELECT * FROM backpacks WHERE uuid = ? AND backpack = ?")
                    query.setString(1, uuid)
                    query.setString(2, name)

                    val result = query.executeQuery()
                    val backpackId: Int

                    val inv = server.createInventory(player, config.slots, itemMeta.displayName)

                    if (result.next()) {
                        backpackId = result.getInt(1)

                        loadItems(inv, uuid, backpackId)
                    } else {
                        backpackId = createBackpack(uuid, name)
                    }

                    result.close()

                    val delete = database.connection.prepareStatement("DELETE FROM items WHERE backpackId = ?")
                    delete.setInt(1, backpackId)

                    delete.execute()

                    openBackpacks[uuid] = backpackId
                    player.openInventory(inv)
                })
            }
        }
    }

    private fun loadItems(inventory: Inventory, uuid: String, backpackId: Int) {
        openBackpacks[uuid] = backpackId

        val itemsQuery = database.connection.prepareStatement("SELECT * FROM items WHERE backpackId = ?")
        itemsQuery.setInt(1, backpackId)

        val items = itemsQuery.executeQuery()

        while (items.next()) {
            inventory.setItem(items.getInt(2), parseItem(items))
        }

        itemsQuery.close()
    }

    private fun createBackpack(uuid: String, name: String): Int {
        val insert = database.connection.prepareStatement(
            "INSERT INTO backpacks (uuid, backpack) VALUES (?, ?)",
            Statement.RETURN_GENERATED_KEYS
        )
        insert.setString(1, uuid)
        insert.setString(2, name)

        insert.executeUpdate()

        val keyResult = insert.generatedKeys
        keyResult.next()

        val backpackId = keyResult.getInt(1)

        keyResult.close()

        return backpackId
    }

    companion object {
        // TODO: don't open backpack before saving is done
        // A map between the UUIDs of the user and the id of the backpack they have open
        val openBackpacks = HashMap<String, Int>()
    }
}