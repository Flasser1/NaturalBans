package me.flasser.naturalbans.listeners;

import me.flasser.naturalbans.managers.PunishmentManager;
import me.flasser.naturalbans.managers.MySQLManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConnectListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerConnect(AsyncPlayerPreLoginEvent e) {

        String query = "INSERT INTO Spiller (UUID, Name) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE Name = VALUES(Name)";
        try (PreparedStatement ps = MySQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, e.getUniqueId().toString());
            ps.setString(2, e.getName());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Failed to insert/update Spiller: " + ex.getMessage());
            ex.printStackTrace();
        }

        query = "INSERT INTO Spiller_IPs (PlayerUUID, IP, LastSeen) VALUES (?, ?, NOW()) " +
                "ON DUPLICATE KEY UPDATE LastSeen = NOW();";
        try (PreparedStatement ps = MySQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, e.getUniqueId().toString());
            ps.setString(2, e.getAddress().getHostAddress());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Failed to insert/update Spiller_IPs: " + ex.getMessage());
            ex.printStackTrace();
        }

        if (!PunishmentManager.isBanned(e.getUniqueId())) {
            return;
        }

        PunishmentManager.BanInfo ban = PunishmentManager.getBanInfo(e.getUniqueId());

        StringBuilder message = new StringBuilder();
        message.append("§c§lYou are banned from this server!\n\n");
        assert ban != null;
        message.append("§7Reason: §f").append(ban.reason).append("\n");
        message.append("§7Banned by: §f").append(ban.staffName).append("\n");

        if (ban.expires == 0) {
            message.append("§7Expires: §cNever\n");
        } else {
            long remaining = ban.expires - System.currentTimeMillis();
            message.append("§7Expires: §c").append(remaining).append("\n");
        }

        message.append("\n§7Appeal at: §fNaturalBans.com");

        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, message.toString());

    }
}
