package at.michael1011.backpacks.commands

import at.michael1011.backpacks.Main
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack

fun registerCommands(main: Main, messages: YamlConfiguration, backpacks: HashMap<String, ItemStack>) {
    val commandLogger = CommandLogger(messages)

    main.getCommand("bpgive").executor = Give(commandLogger, messages.getConfigurationSection("help.bpgive"), backpacks)
}
