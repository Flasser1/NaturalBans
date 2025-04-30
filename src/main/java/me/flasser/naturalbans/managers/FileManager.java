package me.flasser.naturalbans.managers;

import me.flasser.naturalbans.NaturalBans;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager {
    private static File messagesFile;
    private static FileConfiguration messages;

    public static FileConfiguration getMessages() {
        return messages;
    }

    public static String getMessage(String path) {
        String message = messages.getString(path).replace("{prefix}", messages.getString("prefix"));
        return message.replace("&", "§");
    }

    public static void createMessages() {

        messagesFile = new File(NaturalBans.getInstance().getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            NaturalBans.getInstance().saveResource("messages.yml", false);
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
        try {
            messages.load(messagesFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}