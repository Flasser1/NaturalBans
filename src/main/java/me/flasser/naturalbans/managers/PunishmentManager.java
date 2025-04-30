package me.flasser.naturalbans.managers;

import me.flasser.naturalbans.utils.UUIDtoNameUtil;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class PunishmentManager {

    public static void addBan(UUID player, String reason, Long duration, UUID staff) {
        if (!MySQLManager.isConnected()) {
            return;
        }

        String query = "INSERT INTO Ban (PlayerUUID, StaffUUID, Reason, Date, Expires, Undone, UndoneBy, UndoneReason, ServerScope, OriginServer) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = MySQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, player.toString());
            ps.setString(2, staff != null ? staff.toString() : null);
            ps.setString(3, reason);
            ps.setLong(4, System.currentTimeMillis());
            ps.setLong(5, duration != null ? duration : 0);
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

    public static void overrideBan(UUID player, String reason, Long duration, UUID staff) {
        if (!MySQLManager.isConnected()) {
            return;
        }

        removeBan(player, staff);
        addBan(player, reason, duration, staff);
    }

    public static void removeBan(UUID player, UUID staff) {
        if (!MySQLManager.isConnected()) {
            return;
        }

        String query = "DELETE FROM Ban WHERE PlayerUUID = ? AND Undone = false ORDER BY Date DESC LIMIT 1";
        try (PreparedStatement ps = MySQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, player.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void unBan(UUID player, String reason, UUID staff) {
        if (!MySQLManager.isConnected()) {
            return;
        }

        String query = "UPDATE Ban SET Undone = true, UndoneBy = ?, UndoneReason = ? WHERE PlayerUUID = ? AND Undone = false ORDER BY Date DESC LIMIT 1";
        try (PreparedStatement ps = MySQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, staff != null ? staff.toString() : "Console");
            ps.setString(2, reason != null ? reason : "No reason provided");
            ps.setString(3, player.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static boolean isBanned(UUID player) {
        if (!MySQLManager.isConnected()) {
            return false;
        }

        String query = "SELECT * FROM Ban WHERE PlayerUUID = ? AND Undone = false AND (Expires = 0 OR Expires > ?)";

        try (PreparedStatement ps = MySQLManager.getConnection().prepareStatement(query)) {
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

    public static BanInfo getBanInfo(UUID player) {
        if (!MySQLManager.isConnected()) {
            return null;
        }

        String query = "SELECT * FROM Ban WHERE PlayerUUID = ? AND Undone = false AND (Expires = 0 OR Expires > ?)";

        try (PreparedStatement ps = MySQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, player.toString());
            ps.setLong(2, System.currentTimeMillis());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BanInfo ban = new BanInfo();
                    ban.reason = rs.getString("Reason");
                    ban.staffName = UUIDtoNameUtil.getNameFromUUID(UUID.fromString(rs.getString("StaffUUID")));
                    ban.date = new Date(rs.getLong("Date"));
                    ban.expires = new Date(rs.getLong("Expires"));
                    ban.undone = rs.getBoolean("Undone");
                    ban.undoneBy = rs.getString("UndoneBy");
                    ban.undoneReason = rs.getString("UndoneReason");
                    ban.serverScope = rs.getString("ServerScope");
                    ban.originServer = rs.getString("OriginServer");
                    return ban;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void addMute(UUID player, String reason, Long duration, UUID staff) {
        if (!MySQLManager.isConnected()) {
            return;
        }

        String query = "INSERT INTO Mute (PlayerUUID, StaffUUID, Reason, Date, Expires, Undone, UndoneBy, UndoneReason, ServerScope, OriginServer) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = MySQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, player.toString());
            ps.setString(2, staff != null ? staff.toString() : null);
            ps.setString(3, reason);
            ps.setLong(4, System.currentTimeMillis());
            ps.setLong(5, duration != null ? duration : 0);
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

    public static void overrideMute(UUID player, String reason, Long duration, UUID staff) {
        if (!MySQLManager.isConnected()) {
            return;
        }

        removeMute(player, staff);
        addMute(player, reason, duration, staff);
    }

    public static void removeMute(UUID player, UUID staff) {
        if (!MySQLManager.isConnected()) {
            return;
        }

        String query = "DELETE FROM Mute WHERE PlayerUUID = ? AND Undone = false ORDER BY Date DESC LIMIT 1";
        try (PreparedStatement ps = MySQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, player.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void unMute(UUID player, String reason, UUID staff) {
        if (!MySQLManager.isConnected()) {
            return;
        }

        String query = "UPDATE Mute SET Undone = true, UndoneBy = ?, UndoneReason = ? WHERE PlayerUUID = ? AND Undone = false ORDER BY Date DESC LIMIT 1";
        try (PreparedStatement ps = MySQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, staff != null ? staff.toString() : "Console");
            ps.setString(2, reason != null ? reason : "No reason provided");
            ps.setString(3, player.toString());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isMuted(UUID player) {
        if (!MySQLManager.isConnected()) {
            return false;
        }

        String query = "SELECT * FROM Mute WHERE PlayerUUID = ? AND Undone = false AND (Expires = 0 OR Expires > ?)";

        try (PreparedStatement ps = MySQLManager.getConnection().prepareStatement(query)) {
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

    public static MuteInfo getMuteInfo(UUID player) {
        if (!MySQLManager.isConnected()) {
            return null;
        }

        String query = "SELECT * FROM Mute WHERE PlayerUUID = ? AND Undone = false AND (Expires = 0 OR Expires > ?)";

        try (PreparedStatement ps = MySQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, player.toString());
            ps.setLong(2, System.currentTimeMillis());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MuteInfo mute = new MuteInfo();
                    mute.reason = rs.getString("Reason");
                    mute.staffName = UUIDtoNameUtil.getNameFromUUID(UUID.fromString(rs.getString("StaffUUID")));
                    mute.date = new Date(rs.getLong("Date"));
                    mute.expires = new Date(rs.getLong("Expires"));
                    mute.undone = rs.getBoolean("Undone");
                    mute.undoneBy = rs.getString("UndoneBy");
                    mute.undoneReason = rs.getString("UndoneReason");
                    mute.serverScope = rs.getString("ServerScope");
                    mute.originServer = rs.getString("OriginServer");
                    return mute;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void addKick(UUID player, String reason, UUID staff) {
        if (!MySQLManager.isConnected()) {
            return;
        }

        String query = "INSERT INTO Kick (PlayerUUID, StaffUUID, Reason, Date, ServerScope, OriginServer) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = MySQLManager.getConnection().prepareStatement(query)) {
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

    public static class BanInfo {
        public String reason;
        public String staffName;
        public Date date;
        public Date expires;
        public boolean undone;
        public String undoneBy;
        public String undoneReason;
        public String serverScope;
        public String originServer;
    }

    public static class MuteInfo {
        public String reason;
        public String staffName;
        public Date date;
        public Date expires;
        public boolean undone;
        public String undoneBy;
        public String undoneReason;
        public String serverScope;
        public String originServer;
    }
}