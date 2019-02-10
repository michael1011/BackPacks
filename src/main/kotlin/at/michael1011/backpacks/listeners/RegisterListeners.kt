package at.michael1011.backpacks.listeners

import at.michael1011.backpacks.Main
import at.michael1011.backpacks.crafting.BackpackConfig
import at.michael1011.backpacks.database.Database
import org.bukkit.inventory.ItemStack

fun registerListeners(
    main: Main,
    database: Database,
    backpacks: HashMap<String, ItemStack>,
    backpackConfigs: HashMap<String, BackpackConfig>
) {
    val pluginManager = main.server.pluginManager

    pluginManager.registerEvents(Join(database), main)
    pluginManager.registerEvents(RightClick(database, backpacks, backpackConfigs), main)
    pluginManager.registerEvents(InventoryClose(database), main)
}
