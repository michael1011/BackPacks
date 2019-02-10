package at.michael1011.backpacks.listeners

import at.michael1011.backpacks.Main
import at.michael1011.backpacks.database.Database

fun registerListeners(main: Main, database: Database) {
    val pluginManager = main.server.pluginManager

    pluginManager.registerEvents(Join(database), main)
}
