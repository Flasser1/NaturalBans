package me.flasser.naturalbans.listeners;

import me.flasser.naturalbans.managers.FileManager;
import me.flasser.naturalbans.managers.PunishmentManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        Player player = e.getPlayer();

        if (!PunishmentManager.isMuted(player.getUniqueId())) {
            return;
        }

        player.sendMessage(FileManager.getMessage("isMuted"));
        e.setCancelled(true);
    }
}
