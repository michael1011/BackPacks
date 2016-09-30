package at.michael1011.backpacks;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static at.michael1011.backpacks.Main.messages;
import static at.michael1011.backpacks.Main.prefix;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UpdaterTest {

    @Test
    public void testGetMD5() throws Exception {
        String fileOne = "config.yml";
        String fileTwo = "messages.yml";

        String folder = "src/test/resources/";

        assertTrue(Updater.getMD5(folder+fileOne).equals(MD5Test(folder+fileOne)));
        assertTrue(Updater.getMD5(folder+fileTwo).equals(MD5Test(folder+fileTwo)));
    }

    private static String MD5Test(String fileName) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");

        md.update(Files.readAllBytes(Paths.get(fileName)));
        byte[] digest = md.digest();

        return DatatypeConverter.printHexBinary(digest).toLowerCase();
    }

    @Test
    public void testCheckForNewVersion() throws Exception {
        assertTrue(Updater.checkForNewVersion("2.0.0", "2.0.1"));

        assertFalse(Updater.checkForNewVersion("2.0.0", "2.0.0"));
        assertFalse(Updater.checkForNewVersion("2.0.0", "1.9.9"));
    }

    @Test
    public void testGetJsonArray() throws Exception {
        HttpsURLConnection con = (HttpsURLConnection)
                new URL("https://api.curseforge.com/servermods/files?projectIds=98508").openConnection();

        assertTrue(con.getResponseCode() == 200);

        assertFalse(Updater.getJsonArray(con) == null);
    }

    @Test
    public void testPrepareMessages() throws Exception {
        MainTest loadFiles = new MainTest();
        loadFiles.testLoadFiles();

        String newVersion = "new";
        String oldVersion = "old";
        String link = "link";

        Updater.prepareMessages(newVersion, oldVersion, link);

        assertTrue(Updater.newVersion.equals(prefix+ChatColor.translateAlternateColorCodes('&',
                messages.getString("Updater.newVersionAvailable")
                        .replaceAll("%newVersion%", newVersion)
                        .replaceAll("%oldVersion%", oldVersion))));

        assertTrue(Updater.newVersionDownload.equals(prefix+ChatColor.translateAlternateColorCodes('&',
                messages.getString("Updater.newVersionAvailableDownload")
                        .replaceAll("%newVersion%", newVersion)
                        .replaceAll("%downloadLink%", link))));
    }

    @Test
    public void testSendConnectionError() throws Exception {
        MainTest loadFiles = new MainTest();
        loadFiles.testLoadFiles();

        CommandSender sender = PowerMockito.mock(CommandSender.class);

        Updater.sendConnectionError(sender);

        Mockito.verify(sender, Mockito.times(1)).sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                messages.getString("Updater.failedToReachServer")));
    }

    @Test
    public void testReadableByteCount() throws Exception {
        assertTrue(Updater.readableByteCount(10000).equals("9,8 KiB"));
    }

    @Test
    public void testFollowRedirects() throws Exception {
        String link = "https://api.curseforge.com/servermods/files?projectIds=98508";

        assertTrue(Updater.followRedirects("https://api.curseforge.com/servermods/files?projectIds=98508")
                .equals(new URL(link)));
    }

}