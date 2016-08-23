package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.SQL;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class JoinTest {

    @Test
    public void join() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        SQL.createCon("sql7.freemysqlhosting.net", "3306", "sql7132524", "sql7132524", "Ekll7fuBGU");

        assertTrue(SQL.checkCon());

        SQL.query("CREATE TABLE IF NOT EXISTS bp_users(name VARCHAR(100), "+
                "displayName VARCHAR(100), uuid VARCHAR(100))");

        SecureRandom random = new SecureRandom();

        String randomName = new BigInteger(130, random).toString(20);
        String randomNewName = new BigInteger(130, random).toString(20);

        UUID uuid = UUID.randomUUID();

        String trimmedId = uuid.toString().replaceAll("-", "");

        Player player = PowerMockito.mock(Player.class);

        PowerMockito.when(player.getName()).thenReturn(randomName);
        PowerMockito.when(player.getDisplayName()).thenReturn(randomName);
        PowerMockito.when(player.getUniqueId()).thenReturn(uuid);

        PlayerJoinEvent event = new PlayerJoinEvent(player, "");

        Join listener = new Join();

        listener.join(event);

        checkRs(SQL.getResult("SELECT * FROM bp_users where uuid='"+trimmedId+"'"), randomName);

        Player newName = PowerMockito.mock(Player.class);

        PowerMockito.when(newName.getName()).thenReturn(randomNewName);
        PowerMockito.when(newName.getDisplayName()).thenReturn(randomNewName);
        PowerMockito.when(newName.getUniqueId()).thenReturn(uuid);

        PlayerJoinEvent newNameEvent = new PlayerJoinEvent(newName, "");

        listener.join(newNameEvent);

        checkRs(SQL.getResult("SELECT * FROM bp_users where uuid='"+trimmedId+"'"), randomNewName);

    }

    private void checkRs(ResultSet rs, String name) throws SQLException {
        rs.first();

        assertTrue(rs.getString("name").equals(name));
        assertTrue(rs.getString("displayName").equals(name));

    }

}