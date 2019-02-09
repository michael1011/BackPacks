package at.michael1011.backpacks

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class Config(private val main: Main) {
    lateinit var messages: YamlConfiguration

    fun loadFiles() {
        messages = loadFile("messages.yml")
    }

    private fun loadFile(fileName: String) : YamlConfiguration {
        val file = File(main.dataFolder, fileName)

        if (!file.exists()) {
            main.saveResource(fileName, false)
        }

        val yaml = YamlConfiguration()
        yaml.load(file)

        return yaml
    }
}