package dk.flasser.naturalbans.listeners;

import dk.flasser.naturalbans.managers.FileManager;
import dk.flasser.naturalbans.managers.PunishmentManager;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.platform.core.annotation.Component;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@Component
public class ChatListener implements Listener {
    private @Inject PunishmentManager punishmentManager;
    private @Inject FileManager fileManager;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        Player player = e.getPlayer();

        if (!punishmentManager.isMuted(player.getUniqueId())) {
            return;
        }

        player.sendMessage(fileManager.getMessage("isMuted"));
        e.setCancelled(true);
    }
}
