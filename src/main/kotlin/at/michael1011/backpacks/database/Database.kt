package at.michael1011.backpacks.database

import at.michael1011.backpacks.Main
import java.sql.Connection
import java.sql.DriverManager

class Database(main: Main, type: String, mysqlCredentials: MysqlCredentials?) {
    private var connection = if (type.toLowerCase() == "mysql" && mysqlCredentials != null) {
        createMysqlDatabase(mysqlCredentials)
    } else {
        DriverManager.getConnection(
            "jdbc:sqlite:${main.dataFolder.absolutePath}/backpacks.sqlite3"
        )
    }

    init {
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS backpacks (id INTEGER)").execute()
    }

    private fun createMysqlDatabase(credentials: MysqlCredentials) : Connection {
        return DriverManager.getConnection(
            "jdbc:mysql://${credentials.host}:${credentials.port}/" +
                    "${credentials.database}?" +
                    "user=${credentials.username}&" +
                    "password=${credentials.password}"
        )
    }
}