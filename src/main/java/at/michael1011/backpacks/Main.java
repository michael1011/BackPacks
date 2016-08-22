package at.michael1011.backpacks;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin {

    public static YamlConfiguration config;

    @Override
    public void onEnable() {
        createFiles();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createFiles() {
        File configF = new File(getDataFolder(), "config.yml");

        if (!configF.exists()) {
            configF.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }

        config = new YamlConfiguration();

        try {
            config.load(configF);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

}
