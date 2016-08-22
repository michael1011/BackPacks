package at.michael1011.backpacks.tasks;

import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.SQL;
import org.bukkit.Bukkit;

import static at.michael1011.backpacks.Main.config;

public class Reconnect {

    @SuppressWarnings("deprecation")
    public Reconnect(Main main) {
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(main, new Runnable() {
            @Override
            public void run() {
                SQL.closeCon();

                SQL.createCon(config.getString("MySQL.host"), config.getString("MySQL.port"),
                        config.getString("MySQL.database"), config.getString("MySQL.username"),
                        config.getString("MySQL.password"));

            }

        }, 36000, 36000);
    }

}
