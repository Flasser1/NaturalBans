package me.flasser.naturalbans.listeners;

import me.flasser.naturalbans.managers.MySQLManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerConnect(PlayerJoinEvent e) {

        Player player = e.getPlayer();

        if (player.hasPermission("naturalbans.staff")) {
            String query = "INSERT INTO Staff (UUID, Name) VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE Name = VALUES(Name)";
            try (PreparedStatement ps = MySQLManager.getConnection().prepareStatement(query)) {
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, player.getName());
                ps.executeUpdate();
            } catch (
                    SQLException ex) {
                System.err.println("Failed to insert/update Spiller: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
