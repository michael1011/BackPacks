package at.michael1011.backpacks;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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
    public void checkUpdates() throws Exception {
        Main main = PowerMockito.mock(Main.class);

        CommandSender sender = PowerMockito.mock(CommandSender.class);

        Updater.checkUpdates(main, "214.748.3647", Updater.url, sender, false);
        Mockito.verify(sender, Mockito.never()).sendMessage(Matchers.anyString());

        Updater.checkUpdates(main, "1.0.0", Updater.url, sender, true);
        Mockito.verify(sender, Mockito.never()).sendMessage(Matchers.anyString());

        Updater.checkUpdates(main, "0.0.0", Updater.url, sender, false);
        Mockito.verify(sender, Mockito.times(1)).sendMessage(Updater.newVersion);
        Mockito.verify(sender, Mockito.times(1)).sendMessage(Updater.newVersionDownload);

        Updater.checkUpdates(main, "0.0.0", Updater.url+"123", sender, false);
        Mockito.verify(sender, Mockito.times(1)).sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                messages.getString("Updater.failedToReachServer")));

        try {
            Updater.checkUpdates(main, "0.0.", "123"+Updater.url, sender, false);
            Mockito.verify(sender, Mockito.times(2)).sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                    messages.getString("Updater.failedToReachServer")));

        } catch (Exception e) {
            assertTrue(e.getClass().equals(MalformedURLException.class));
        }

    }

    @Test
    public void downloadFile() throws Exception {
        CommandSender sender = PowerMockito.mock(CommandSender.class);

        String file = "file.test";
        String folder = "build/resources/test/";

        URL url = Updater.followRedirects("http://www.speedtestx.de/testfiles/data_1mb.test");

        int fileSize = url.openConnection().getContentLength();

        File download = Updater.downloadFile(file, folder, fileSize, url, sender);

        Mockito.verify(sender, Mockito.atLeastOnce()).sendMessage(Matchers.anyString());

        assertTrue(download.length() == fileSize);
        assertTrue(Updater.getMD5(folder+file).equals("c5f4f7ffd5efd745d514f101560af508"));

        assertTrue(download.delete());
    }

    @Test
    public void followRedirects() throws Exception {
        String link = "https://api.curseforge.com/servermods/files?projectIds=98508";

        assertTrue(Updater.followRedirects("https://api.curseforge.com/servermods/files?projectIds=98508")
                .equals(new URL(link)));
    }

    @Test
    public void getJsonArray() throws Exception {
        HttpsURLConnection con = (HttpsURLConnection)
                new URL("https://api.curseforge.com/servermods/files?projectIds=98508").openConnection();

        assertTrue(con.getResponseCode() == 200);

        assertFalse(Updater.getJsonArray(con) == null);
    }

    @Test
    public void getMD5() throws Exception {
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
    public void readableByteCount() throws Exception {
        assertTrue(Updater.readableByteCount(10000).endsWith("KiB"));
        assertTrue(Updater.readableByteCount(1000).endsWith(" B"));
    }

    @Test
    public void hoursToTicks() throws Exception {
        int hours = 123;

        assertTrue(Updater.hoursToTicks(hours) == hours*72000);
    }

    @Test
    public void checkForNewVersion() throws Exception {
        assertTrue(Updater.checkForNewVersion("2.0.0", "2.0.1"));

        assertFalse(Updater.checkForNewVersion("2.0.0", "2.0.0"));
        assertFalse(Updater.checkForNewVersion("2.0.0", "1.9.9"));
    }

    @Test
    public void prepareMessages() throws Exception {
        MainTest loadFiles = new MainTest();
        loadFiles.loadFiles();

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
    public void sendConnectionError() throws Exception {
        MainTest loadFiles = new MainTest();
        loadFiles.loadFiles();

        CommandSender sender = PowerMockito.mock(CommandSender.class);

        Updater.sendConnectionError(sender);

        Mockito.verify(sender, Mockito.times(1)).sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                messages.getString("Updater.failedToReachServer")));
    }

}