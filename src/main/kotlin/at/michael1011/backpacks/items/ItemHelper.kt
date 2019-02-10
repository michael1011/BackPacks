package at.michael1011.backpacks.items

import at.michael1011.backpacks.database.Database
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import java.sql.ResultSet
import java.sql.Types

fun serializeItem(database: Database, backpackId: Int, position: Int, item: ItemStack): Boolean {
    val insert = database.connection.prepareStatement("INSERT INTO items VALUES (?, ?, ?, ?, ?, ?, ?)")

    insert.setInt(1, backpackId)
    insert.setInt(2, position)

    insert.setString(3, item.data.itemType.name)
    insert.setInt(4, item.amount)

    if (item.hasItemMeta()) {
        val damageMeta = item.itemMeta as Damageable
        insert.setInt(5, damageMeta.damage)

        val meta = item.itemMeta
        insert.setString(6, meta.displayName)

        if (meta.hasLore()) {
            insert.setString(7, meta.lore.joinToString("\n"))
        } else {
            insert.setNull(7, Types.VARCHAR)
        }
    } else {
        insert.setNull(5, Types.INTEGER)
        insert.setNull(6, Types.VARCHAR)
        insert.setNull(7, Types.VARCHAR)
    }

    return insert.execute()
}

fun parseItem(result: ResultSet): ItemStack {
    val material = Material.valueOf(result.getString(3))
    val item = ItemStack(material, result.getInt(4))

    return item
}