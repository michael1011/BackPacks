package at.michael1011.backpacks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static at.michael1011.backpacks.Main.*;

public class Updater {

    static String url = "https://api.curseforge.com/servermods/files?projectIds=98508";

    Updater(final Main main) {
        int interval = hoursToTicks(config.getInt("Updater.interval"));

        Bukkit.getScheduler().runTaskTimerAsynchronously(main, new Runnable() {
            @Override
            public void run() {
                checkUpdates(main, Bukkit.getConsoleSender());
            }

        }, interval, interval);
    }

    public static Boolean updateAvailable = false;

    public static String newVersion;
    public static String newVersionDownload;

    static void checkUpdates(Main main, CommandSender sender) {
        try {
            checkUpdates(main, main.getDescription().getVersion(), url, sender, false);
        } catch (IOException | NoSuchAlgorithmException | ParseException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    static void checkUpdates(Main main, String installedVersionNumber, String url,
                             CommandSender sender, Boolean equalVersionNumber)
            throws IOException, NoSuchAlgorithmException, URISyntaxException, ParseException {

        HttpsURLConnection con = (HttpsURLConnection)
                new URL(url).openConnection();

        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();

        if(responseCode == 200) {
            JSONArray array = getJsonArray(con);

            int size = array.size();

            if(size> 0) {
                JSONObject latestVersion = (JSONObject) array.get(size-1);

                String downloadUrl = String.valueOf(latestVersion.get("downloadUrl"));
                String latestVersionNumber = String.valueOf(latestVersion.get("name"));

                if(equalVersionNumber) {
                    latestVersionNumber = installedVersionNumber;
                }

                if(checkForNewVersion(installedVersionNumber, latestVersionNumber)) {
                    // fixme: check if releaseType is 'release' else take version which is newer and has type release

                    prepareMessages(latestVersionNumber, installedVersionNumber, downloadUrl);

                    sender.sendMessage(newVersion);

                    if(config.getBoolean("Updater.autoUpdate")) {
                        String messagesPath = "Updater.autoUpdate.";

                        URL download = followRedirects(downloadUrl);

                        int fileSize = download.openConnection().getContentLength();
                        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf('/')+1,
                                downloadUrl.length());

                        File downloadedFile = downloadFile(fileName, "plugins", fileSize, download, sender);

                        if(getMD5("plugins/"+fileName).equalsIgnoreCase(String.valueOf(latestVersion.get("md5")))) {
                            File old = new File(Main.class.getProtectionDomain().getCodeSource()
                                    .getLocation().toURI().getPath());

                            if(old.delete()) {
                                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                        messages.getString(messagesPath+"deletedOldVersion")
                                                .replaceAll("%oldVersion%", old.getName())));
                            }

                            sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                    messages.getString(messagesPath+"successful")));

                            Bukkit.getScheduler().cancelTasks(main);
                            Bukkit.dispatchCommand(sender, "reload");

                        } else {
                            if(downloadedFile.delete()) {
                                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                        messages.getString(messagesPath+"downloadFailed")
                                                .replaceAll("%downloadLink%", downloadUrl)));

                                updateAvailable = true;

                                newVersionDownload = prefix+ChatColor.translateAlternateColorCodes('&',
                                        messages.getString("Updater.newVersionAvailableDownload")
                                                .replaceAll("%newVersion%", latestVersionNumber)
                                                .replaceAll("%downloadLink%", downloadUrl));
                            }
                        }

                    } else {
                        updateAvailable = true;

                        sender.sendMessage(newVersionDownload);
                    }

                }

            } else {
                sendConnectionError(sender);
            }

        } else {
            sendConnectionError(sender);
        }

    }

    static File downloadFile(String fileName, String folder, int fileSize, URL download, CommandSender sender)
            throws IOException {

        File downloadedFile = new File(folder, fileName);

        BufferedInputStream downloadInput = new BufferedInputStream(download.openStream());

        FileOutputStream fileOutput = new FileOutputStream(downloadedFile);

        int byteSize = 1024;

        final byte[] data = new byte[byteSize];
        long downloaded = 0;
        int count;

        while((count = downloadInput.read(data, 0, byteSize)) != -1) {
            downloaded += count;

            fileOutput.write(data, 0, count);

            int percent = (int) ((downloaded * 100) / fileSize);

            if((percent % 10) == 0) {
                sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                        messages.getString("Updater.autoUpdate.downloading")
                                .replaceAll("%percent%", String.valueOf(percent))
                                .replaceAll("%fileSize%", readableByteCount(fileSize))));
            }
        }

        downloadInput.close();
        fileOutput.close();

        return downloadedFile;
    }

    static String getMD5(String fileName) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");

        md.update(Files.readAllBytes(Paths.get(fileName)));
        byte[] digest = md.digest();

        return DatatypeConverter.printHexBinary(digest).toLowerCase();
    }

    static Boolean checkForNewVersion(String thisVersion, String newVersion) {
        return Integer.valueOf(thisVersion.replaceAll("\\.", "")) < Integer.valueOf(newVersion.replaceAll("\\.", ""));
    }

    static JSONArray getJsonArray(HttpsURLConnection con) throws IOException, ParseException {
        BufferedReader input = new BufferedReader(new InputStreamReader(con.getInputStream()));

        JSONArray array = (JSONArray) new JSONParser().parse(input);

        input.close();

        return array;
    }

    static void prepareMessages(String latestVersionNumber, String installedVersionNumber, String downloadUrl) {
        newVersion = prefix+ChatColor.translateAlternateColorCodes('&',
                messages.getString("Updater.newVersionAvailable")
                        .replaceAll("%newVersion%", latestVersionNumber)
                        .replaceAll("%oldVersion%", installedVersionNumber));

        newVersionDownload = prefix+ChatColor.translateAlternateColorCodes('&',
                messages.getString("Updater.newVersionAvailableDownload")
                        .replaceAll("%newVersion%", latestVersionNumber)
                        .replaceAll("%downloadLink%", downloadUrl));
    }

    static void sendConnectionError(CommandSender sender) {
        sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                messages.getString("Updater.failedToReachServer")));
    }

    static String readableByteCount(long bytes) {
        int unit = 1024;

        if (bytes < unit) {
            return bytes + " B";
        }

        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp-1) + ("i");

        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    static int hoursToTicks(int hours) {
        return hours*72000;
    }

    static URL followRedirects(String location) throws IOException {
        URL resourceUrl, base, next;
        HttpURLConnection conn;
        String redLoc;

        while (true) {
            resourceUrl = new URL(location);
            conn = (HttpURLConnection) resourceUrl.openConnection();

            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0...");

            switch (conn.getResponseCode()) {
                case HttpURLConnection.HTTP_MOVED_PERM:
                case HttpURLConnection.HTTP_MOVED_TEMP:
                    redLoc = conn.getHeaderField("Location");
                    base = new URL(location);
                    next = new URL(base, redLoc);
                    location = next.toExternalForm();
                    continue;
            }

            break;
        }

        return conn.getURL();
    }

}