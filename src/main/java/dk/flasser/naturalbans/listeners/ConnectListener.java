package dk.flasser.naturalbans.listeners;

import dk.flasser.naturalbans.managers.SQLManager;
import dk.flasser.naturalbans.managers.PunishmentManager;

import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.platform.core.annotation.Component;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class ConnectListener implements Listener {
    private @Inject PunishmentManager punishmentManager;
    private @Inject SQLManager SQLManager;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerConnect(AsyncPlayerPreLoginEvent e) {

        String query = "INSERT OR REPLACE INTO Spiller (UUID, Name) VALUES (?, ?)";
        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, e.getUniqueId().toString());
            ps.setString(2, e.getName());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Failed to insert/update Spiller: " + ex.getMessage());
            ex.printStackTrace();
        }

        query = "INSERT OR REPLACE INTO Spiller_IPs (PlayerUUID, IP, LastSeen) VALUES (?, ?, System.currentTimeMillis())";
        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, e.getUniqueId().toString());
            ps.setString(2, e.getAddress().getHostAddress());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Failed to insert/update Spiller_IPs: " + ex.getMessage());
            ex.printStackTrace();
        }

        if (punishmentManager.isBanned(e.getUniqueId())) {

            PunishmentManager.PunishmentInfo ban = punishmentManager.getBanInfo(e.getUniqueId());

            if (ban != null) {

                StringBuilder message = new StringBuilder();

                message.append("§c§lYou are banned from this server!\n\n");
                message.append("§7Reason: §f").append(ban.reason).append("\n");
                message.append("§7Banned by: §f").append(ban.staffName).append("\n");

                if (ban.expires == null) {
                    message.append("§7Expires: §cNever\n");
                } else {
                    message.append("§7Expires: §c").append(ban.expires).append("\n");
                }

                message.append("\n§7Appeal at: §fNaturalBans.com");

                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, message.toString());
            }
        }

        String ip = e.getAddress().getHostAddress();

        query = " SELECT PlayerUUID FROM Spiller_IPs WHERE IP = ? ";

        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(query)) {

            ps.setString(1, ip);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    UUID knownPlayerUUID = UUID.fromString(rs.getString("PlayerUUID"));

                    if (!punishmentManager.isIPBanned(knownPlayerUUID)) {
                        continue;
                    }

                    PunishmentManager.PunishmentInfo ipBan =
                            punishmentManager.getIPBanInfo(ip);

                    if (ipBan == null) {
                        continue;
                    }

                    StringBuilder message = new StringBuilder();

                    message.append("§c§lYour IP is banned from this server!\n\n");
                    message.append("§7Reason: §f").append(ipBan.reason).append("\n");
                    message.append("§7Banned by: §f").append(ipBan.staffName).append("\n");

                    if (ipBan.expires == null) {
                        message.append("§7Expires: §cNever\n");
                    } else {
                        message.append("§7Expires: §c").append(ipBan.expires).append("\n");
                    }

                    message.append("\n§7Appeal at: §fNaturalBans.com");

                    e.disallow(
                            AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                            message.toString()
                    );

                    return;
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
}
