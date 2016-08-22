package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.SQL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Join implements Listener {

    public void register(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void join(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        String uuid = p.getUniqueId().toString().replaceAll("-", "");

        try {
            ResultSet rs = SQL.getResult("SELECT * FROM bp_users WHERE uuid='"+uuid+"'");

            assert rs != null;

            String name = p.getName();
            String displayName = p.getDisplayName();

            if(rs.first()) {
                if(!rs.getString("name").equals(name)) {
                    SQL.query("UPDATE bp_users SET name='"+name+"' WHERE uuid='"+uuid+"'");

                } else if(!rs.getString("displayName").equals(displayName)){
                    SQL.query("UPDATE bp_users SET displayName='"+displayName+"' WHERE uuid='"+uuid+"'");
                }

            } else {
                SQL.query("INSERT INTO bp_users (name, displayName, uuid) values ('"+name+"', " +
                        "'"+displayName+"', '"+uuid+"')");
            }

        } catch (SQLException e1) {
            e1.printStackTrace();
        }

    }

}
