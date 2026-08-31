package dk.flasser.naturalbans.managers;

import dk.flasser.naturalbans.utils.UUIDtoNameUtil;

import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.platform.core.annotation.Component;

import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;
@Component
public class PunishmentManager {
    private @Inject SQLManager SQLManager;
    private @Inject UUIDtoNameUtil uuidtoNameUtil;

    public void addBan(UUID player, String reason, Long duration, UUID staff) {
        if (!SQLManager.isConnected()) {
            return;
        }

        String query = "INSERT INTO Ban (PlayerUUID, StaffUUID, Reason, Date, Expires, Undone, UndoneBy, UndoneReason, ServerScope, OriginServer) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, player.toString());
            ps.setString(2, staff != null ? staff.toString() : null);
            ps.setString(3, reason);
            ps.setLong(4, System.currentTimeMillis());
            if (duration != null) {
                ps.setLong(5, duration);
            } else {
                ps.setNull(5, java.sql.Types.BIGINT);
            }
            ps.setBoolean(6, false);
            ps.setString(7, null);
            ps.setString(8, null);
            ps.setString(9, Bukkit.getServer().getName());
            ps.setString(10, Bukkit.getServer().getName());


            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void overrideBan(UUID player, String reason, Long duration, UUID staff) {
        if (!SQLManager.isConnected()) {
            return;
        }

        removeBan(player, staff);
        addBan(player, reason, duration, staff);
    }

    public void removeBan(UUID player, UUID staff) {
        if (!SQLManager.isConnected()) {
            return;
        }

        String query = "DELETE FROM Ban WHERE PlayerUUID = ? AND Undone = false ORDER BY Date DESC LIMIT 1";
        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, player.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unBan(UUID player, String reason, UUID staff) {
        if (!SQLManager.isConnected()) {
            return;
        }

        String query = "UPDATE Ban SET Undone = true, UndoneBy = ?, UndoneReason = ? WHERE PlayerUUID = ? AND Undone = false ORDER BY Date DESC LIMIT 1";
        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, staff != null ? staff.toString() : "Console");
            ps.setString(2, reason != null ? reason : "No reason provided");
            ps.setString(3, player.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean isBanned(UUID player) {
        if (!SQLManager.isConnected()) {
            return false;
        }

        String query = "SELECT * FROM Ban WHERE PlayerUUID = ? AND Undone = false AND (Expires IS NULL OR Expires > ?)";

        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, player.toString());
            ps.setLong(2, System.currentTimeMillis());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public PunishmentInfo getBanInfo(UUID player) {
        if (!SQLManager.isConnected()) {
            return null;
        }

        String query = "SELECT * FROM Ban WHERE PlayerUUID = ? AND Undone = false AND (Expires IS NULL OR Expires > ?)";

        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, player.toString());
            ps.setLong(2, System.currentTimeMillis());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PunishmentInfo ban = new PunishmentInfo();
                    ban.reason = rs.getString("Reason");

                    String staffUUID = rs.getString("StaffUUID");

                    if (staffUUID != null) {
                        ban.staffName = uuidtoNameUtil.getNameFromUUID(UUID.fromString(staffUUID));
                    } else {
                        ban.staffName = "Console";
                    }

                    ban.date = new Date(rs.getLong("Date"));

                    long expires = rs.getLong("Expires");

                    if (rs.wasNull()) {
                        ban.expires = null;
                    } else {
                        ban.expires = new Date(expires);
                    }

                    ban.undone = rs.getBoolean("Undone");
                    ban.undoneBy = rs.getString("UndoneBy");
                    ban.undoneReason = rs.getString("UndoneReason");
                    return ban;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void addIPBan(UUID player, String reason, Long duration, UUID staff) {
        if (!SQLManager.isConnected()) {
            return;
        }

        String query =
                "INSERT INTO IPBan " +
                        "(PlayerUUID, StaffUUID, Reason, Date, Expires, Undone, UndoneBy, UndoneReason) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(query)) {

            ps.setString(1, player.toString());
            ps.setString(2, staff != null ? staff.toString() : null);
            ps.setString(3, reason);
            ps.setLong(4, System.currentTimeMillis());

            if (duration != null) {
                ps.setLong(5, duration);
            } else {
                ps.setNull(5, java.sql.Types.BIGINT);
            }

            ps.setBoolean(6, false);
            ps.setString(7, null);
            ps.setString(8, null);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void overrideIPBan(UUID player, String reason, Long duration, UUID staff) {
        if (SQLManager.isConnected()) {
            removeIPBan(player, staff);
            addIPBan(player, reason, duration, staff);
        }
    }

    public void removeIPBan(UUID player, UUID staff) {
        if (!SQLManager.isConnected()) {
            return;
        }

        String query =
                "DELETE FROM IPBan " +
                        "WHERE ID = (" +
                        "    SELECT ID FROM IPBan " +
                        "    WHERE PlayerUUID = ? " +
                        "    AND Undone = 0 " +
                        "    ORDER BY Date DESC " +
                        "    LIMIT 1" +
                        ")";

        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(query)) {

            ps.setString(1, player.toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unIPBan(UUID player, String reason, UUID staff) {
        if (!SQLManager.isConnected()) {
            return;
        }

        String query =
                "UPDATE IPBan SET " +
                        "Undone = 1, " +
                        "UndoneBy = ?, " +
                        "UndoneReason = ? " +
                        "WHERE ID = (" +
                        "    SELECT ID FROM IPBan " +
                        "    WHERE PlayerUUID = ? " +
                        "    AND Undone = 0 " +
                        "    ORDER BY Date DESC " +
                        "    LIMIT 1" +
                        ")";

        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(query)) {

            ps.setString(1, staff != null ? staff.toString() : "Console");
            ps.setString(2, reason != null ? reason : "No reason provided");
            ps.setString(3, player.toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isIPBanned(UUID player) {
        if (!SQLManager.isConnected()) {
            return false;
        }

        String query =
                "SELECT 1 " +
                        "FROM IPBan " +
                        "WHERE PlayerUUID = ? " +
                        "AND Undone = 0 " +
                        "AND (Expires IS NULL OR Expires > ?) " +
                        "LIMIT 1";

        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(query)) {

            ps.setString(1, player.toString());
            ps.setLong(2, System.currentTimeMillis());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public PunishmentManager.PunishmentInfo getIPBanInfo(String ip) {
        if (!SQLManager.isConnected()) {
            return null;
        }

        String query =
                "SELECT IPBan.* " +
                        "FROM IPBan " +
                        "INNER JOIN Spiller_IPs ON IPBan.PlayerUUID = Spiller_IPs.PlayerUUID " +
                        "WHERE Spiller_IPs.IP = ? " +
                        "AND IPBan.Undone = 0 " +
                        "AND (IPBan.Expires IS NULL OR IPBan.Expires > ?) " +
                        "ORDER BY IPBan.Date DESC " +
                        "LIMIT 1";

        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(query)) {

            ps.setString(1, ip);
            ps.setLong(2, System.currentTimeMillis());

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                PunishmentManager.PunishmentInfo ipBan =
                        new PunishmentManager.PunishmentInfo();

                ipBan.reason = rs.getString("Reason");

                String staffUUID = rs.getString("StaffUUID");

                if (staffUUID != null) {
                    ipBan.staffName =
                            uuidtoNameUtil.getNameFromUUID(UUID.fromString(staffUUID));
                } else {
                    ipBan.staffName = "Console";
                }

                ipBan.date = new Date(rs.getLong("Date"));

                long expires = rs.getLong("Expires");

                if (rs.wasNull()) {
                    ipBan.expires = null;
                } else {
                    ipBan.expires = new Date(expires);
                }

                ipBan.undone = rs.getBoolean("Undone");
                ipBan.undoneBy = rs.getString("UndoneBy");
                ipBan.undoneReason = rs.getString("UndoneReason");

                return ipBan;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void addMute(UUID player, String reason, Long duration, UUID staff) {
        if (!SQLManager.isConnected()) {
            return;
        }

        String query = "INSERT INTO Mute (PlayerUUID, StaffUUID, Reason, Date, Expires, Undone, UndoneBy, UndoneReason, ServerScope, OriginServer) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, player.toString());
            ps.setString(2, staff != null ? staff.toString() : null);
            ps.setString(3, reason);
            ps.setLong(4, System.currentTimeMillis());
            if (duration != null) {
                ps.setLong(5, duration);
            } else {
                ps.setNull(5, java.sql.Types.BIGINT);
            }
            ps.setBoolean(6, false);
            ps.setString(7, null);
            ps.setString(8, null);
            ps.setString(9, Bukkit.getServer().getName());
            ps.setString(10, Bukkit.getServer().getName());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void overrideMute(UUID player, String reason, Long duration, UUID staff) {
        if (!SQLManager.isConnected()) {
            return;
        }

        removeMute(player, staff);
        addMute(player, reason, duration, staff);
    }

    public void removeMute(UUID player, UUID staff) {
        if (!SQLManager.isConnected()) {
            return;
        }

        String query = "DELETE FROM Mute WHERE PlayerUUID = ? AND Undone = false ORDER BY Date DESC LIMIT 1";
        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, player.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unMute(UUID player, String reason, UUID staff) {
        if (!SQLManager.isConnected()) {
            return;
        }

        String query = "UPDATE Mute SET Undone = true, UndoneBy = ?, UndoneReason = ? WHERE PlayerUUID = ? AND Undone = false ORDER BY Date DESC LIMIT 1";
        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, staff != null ? staff.toString() : "Console");
            ps.setString(2, reason != null ? reason : "No reason provided");
            ps.setString(3, player.toString());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isMuted(UUID player) {
        if (!SQLManager.isConnected()) {
            return false;
        }

        String query = "SELECT * FROM Mute WHERE PlayerUUID = ? AND Undone = false AND (Expires IS NULL OR Expires > ?)";

        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, player.toString());
            ps.setLong(2, System.currentTimeMillis());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public PunishmentInfo getMuteInfo(UUID player) {
        if (!SQLManager.isConnected()) {
            return null;
        }

        String query = "SELECT * FROM Mute WHERE PlayerUUID = ? AND Undone = false AND (Expires IS NULL OR Expires > ?)";

        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, player.toString());
            ps.setLong(2, System.currentTimeMillis());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PunishmentInfo mute = new PunishmentInfo();
                    mute.reason = rs.getString("Reason");
                    String staffUUID = rs.getString("StaffUUID");

                    if (staffUUID != null) {
                        mute.staffName = uuidtoNameUtil.getNameFromUUID(UUID.fromString(staffUUID));
                    } else {
                        mute.staffName = "Console";
                    }
                    mute.date = new Date(rs.getLong("Date"));
                    long expires = rs.getLong("Expires");

                    if (rs.wasNull()) {
                        mute.expires = null;
                    } else {
                        mute.expires = new Date(expires);
                    }
                    mute.undone = rs.getBoolean("Undone");
                    mute.undoneBy = rs.getString("UndoneBy");
                    mute.undoneReason = rs.getString("UndoneReason");
                    return mute;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void addKick(UUID player, String reason, UUID staff) {
        if (!SQLManager.isConnected()) {
            return;
        }

        String query = "INSERT INTO Kick (PlayerUUID, StaffUUID, Reason, Date, ServerScope, OriginServer) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, player.toString());
            ps.setString(2, staff != null ? staff.toString() : null);
            ps.setString(3, reason);
            ps.setLong(4, System.currentTimeMillis());
            ps.setString(5, Bukkit.getServer().getName());
            ps.setString(6, Bukkit.getServer().getName());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static class PunishmentInfo {
        public String reason;
        public String staffName;
        public Date date;
        public Date expires;
        public boolean undone;
        public String undoneBy;
        public String undoneReason;
    }
}