package at.michael1011.backpacks.database

import org.bukkit.configuration.file.YamlConfiguration

data class MysqlCredentials(
    val host: String,
    val port: Number,
    val database: String,
    val username: String,
    val password: String
) {
    constructor(config: YamlConfiguration): this(
        config.getString("mysql.host"),
        config.getInt("mysql.port"),
        config.getString("mysql.database"),
        config.getString("mysql.username"),
        config.getString("mysql.password")
    )
}