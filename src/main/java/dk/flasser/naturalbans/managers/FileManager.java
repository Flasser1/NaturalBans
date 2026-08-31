package dk.flasser.naturalbans.managers;

import dk.flasser.naturalbans.NaturalBans;

import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.platform.core.annotation.Component;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
@Component
public class FileManager {
    private @Inject NaturalBans naturalBans;
    private static File messagesFile;
    private static FileConfiguration messages;

    public FileConfiguration getMessages() {
        return messages;
    }

    public String getMessage(String path) {
        String message = messages.getString(path).replace("{prefix}", messages.getString("prefix"));
        return message.replace("&", "§");
    }

    public void createMessages() {

        messagesFile = new File(naturalBans.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            naturalBans.saveResource("messages.yml", false);
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
        try {
            messages.load(messagesFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}