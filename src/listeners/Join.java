package listeners;

import main.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Join implements Listener {

    public Join(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        addToTable(e.getPlayer());
    }

    public static void addToTable(Player p) {
        String uuid = p.getUniqueId().toString();
        String name = p.getName();

        try {
            ResultSet rs = Main.getResult("SELECT * FROM bp_users WHERE uuid='"+uuid+"'");

            assert rs != null;

            rs.beforeFirst();

            if(rs.next()) {
                if(!rs.getString("name").equals(name)) {
                    Main.update("UPDATE bp_users SET name='"+name+"' WHERE uuid='"+uuid+"'");
                }

            } else {
                Main.update("INSERT INTO bp_users (name, uuid) values ('"+name+"', '"+uuid+"')");
            }

        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

}
