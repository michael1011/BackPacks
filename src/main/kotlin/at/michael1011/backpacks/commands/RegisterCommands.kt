package at.michael1011.backpacks.commands

import at.michael1011.backpacks.Main
import org.bukkit.inventory.ItemStack

fun registerCommands(main: Main, backpacks: HashMap<String, ItemStack>) {
    main.getCommand("bpgive").executor = Give(backpacks)
}