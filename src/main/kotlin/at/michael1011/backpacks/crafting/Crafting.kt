package at.michael1011.backpacks.crafting

import at.michael1011.backpacks.Logger
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack

class Crafting(private val logger: Logger, config: YamlConfiguration) {
    val backpacks = HashMap<String, ItemStack>()

    private var section: ConfigurationSection = config.getConfigurationSection("backpacks")

    init {
        val backpackKeys = section.getKeys(false)

        backpackKeys.forEach {
            try {
                val backpack = parseBackpack(it)
                val item = createItemStack(backpack)

                backpacks[it] = item
            } catch (exception: IllegalArgumentException) {
                logger.warn("Could not enable backpack $it: ${exception.message}")
            }
        }
    }

    private fun createItemStack(config: BackpackConfig): ItemStack {
        val item = ItemStack(config.material, 1)
        val meta = item.itemMeta

        meta.displayName = config.name

        if (config.description.isNotEmpty()) {
            meta.lore = config.description
        }

        item.itemMeta = meta

        return item
    }

    private fun parseBackpack(key: String): BackpackConfig {
        val backpackSection = section.getConfigurationSection(key)

        val description = ArrayList<String>()

        backpackSection.getStringList("description").forEach {
            description.add(Logger.formatColorCodes(it))
        }

        val material = Material.valueOf(
            backpackSection.getString("material").toUpperCase()
        )

        return BackpackConfig(
            Logger.formatColorCodes(backpackSection.getString("name")),
            description,
            backpackSection.getInt("slots"),
            material
        )
    }
}