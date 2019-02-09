package at.michael1011.backpacks.crafting

import org.bukkit.Material

data class BackpackConfig(
    val name: String,
    val description: List<String>,
    val slots: Number,
    val material: Material
)
