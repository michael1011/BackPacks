package at.michael1011.backpacks.database

import at.michael1011.backpacks.Main
import org.bukkit.Bukkit
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

class Database(private val main: Main, type: String, mysqlCredentials: MysqlCredentials) {
    val isMysql = type.toLowerCase() == "mysql"

    val connection = (if (isMysql) {
        createMysqlDatabase(mysqlCredentials)
    } else {
        DriverManager.getConnection(
            "jdbc:sqlite:${main.dataFolder.absolutePath}/backpacks.sqlite3"
        )
    })!!

    private val scheduler = Bukkit.getScheduler()

    // MySQL and SQLite have different key words for autoincrement
    private val autoIncrement = if (isMysql) {
        "AUTO_INCREMENT"
    } else {
        "AUTOINCREMENT"
    }

    init {
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS players (uuid VARCHAR(36), name VARCHAR(255) NOT NULL, PRIMARY KEY (`uuid`))").execute()
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS backpacks (id INTEGER PRIMARY KEY $autoIncrement, uuid VARCHAR(36) NOT NULL, backpack VARCHAR(255), FOREIGN KEY (uuid) REFERENCES players(uuid))").execute()
        connection.prepareStatement(
            "CREATE TABLE IF NOT EXISTS items (backpackId INTEGER NOT NULL, position INTEGER NOT NULL, material VARCHAR(255) NOT NULL, amount INTEGER, damage INTEGER, name VARCHAR(100), lore VARCHAR(1000), FOREIGN KEY (backpackId) REFERENCES backpacks(id))"
        ).execute()
    }

    fun executeStatement(statement: PreparedStatement, callback: (Boolean) -> Unit) {
        runAsync(Runnable {
            callback(statement.execute())
        })
    }

    fun queryStatement(statement: PreparedStatement, callback: (ResultSet) -> Unit) {
        runAsync(Runnable {
            callback(statement.executeQuery())
        })
    }

    fun runAsync(task: Runnable) {
        scheduler.runTaskAsynchronously(main, task)
    }

    private fun createMysqlDatabase(credentials: MysqlCredentials): Connection {
        return DriverManager.getConnection(
            "jdbc:mysql://${credentials.host}:${credentials.port}/" +
                    "${credentials.database}?" +
                    "user=${credentials.username}&" +
                    "password=${credentials.password}"
        )
    }
}