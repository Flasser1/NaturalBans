package me.flasser.naturalbans.utils;

import me.flasser.naturalbans.managers.MySQLManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class AltsUtil {

    public static ArrayList<String> checkAlts(UUID player) {
        if (!MySQLManager.isConnected()) {
            return null;
        }

        String query = "SELECT DISTINCT IP FROM Spiller_IPs WHERE PlayerUUID = ?";
        Set<String> ips = new HashSet<>();
        ArrayList<String> alts = new ArrayList<>();

        try (PreparedStatement ps = MySQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, player.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ips.add(rs.getString("IP"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String searchQuery = "SELECT DISTINCT PlayerUUID FROM Spiller_IPs WHERE IP IN (" +
                String.join(",", Collections.nCopies(ips.size(), "?")) + ")";

        try (PreparedStatement ps = MySQLManager.getConnection().prepareStatement(searchQuery)) {
            int i = 1;
            for (String ip : ips) {
                ps.setString(i++, ip);
            }

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("Alts for player " + player + ":");
                while (rs.next()) {
                    UUID altUUID = UUID.fromString(rs.getString("PlayerUUID"));
                    if (!altUUID.equals(player)) {
                        System.out.println("- " + altUUID);
                        alts.add(UUIDtoNameUtil.getNameFromUUID(altUUID));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return alts;

    }
}
