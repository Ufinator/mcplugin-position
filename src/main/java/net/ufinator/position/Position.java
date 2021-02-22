package net.ufinator.position;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ufinator.position.commands.PositionCMD;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public final class Position extends JavaPlugin {

    public static String PREFIX = "§6§lPosition §8§l>> §r";
    public static Position INSTANCE;
    private final File positionFile = new File(getDataFolder(), "position.yml");
    private final File configFile = new File(getDataFolder(), "config.yml");

    @Override
    public void onEnable() {
        // Plugin startup logic
        log("§aPlugin enabled!");
        if (INSTANCE == null) {
            INSTANCE = this;
        }
        if (!positionFile.exists()) {
            log("§cposition.yml doesn't exist! It will create one, if the first position will be set!");
        }
        if (!configFile.exists()) {
            saveResource("config.yml", true);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        register();
        try {
            URL url = new URL("https://api.spiget.org/v2/resources/89357/versions/latest");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            connection.disconnect();
            JsonObject json = new JsonParser().parse(response.toString()).getAsJsonObject();
            String version = config.getString("version");
            String newVersion = json.get("name").getAsString();
            if (!newVersion.equalsIgnoreCase(version)) {
                log("§8++++++++++++++++++++");
                log("§cYou are not using the latest Plugin! §a§lDownload the latest Plugin here: §6§lhttps://www.spigotmc.org/resources/position.89357/");
                log("§8++++++++++++++++++++");
            } else {
                log("§aPerfect! You are using the latest version!");
            }
        } catch (IOException e) {
            log("§cCan't get Update Informations...");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        log("§cPlugin disabled!");
    }

    public void register() {
        Bukkit.getPluginCommand("position").setExecutor(new PositionCMD());
    }

    public void log(String txt) {
        Bukkit.getConsoleSender().sendMessage(PREFIX + txt);
    }

}
