package dk.flasser.naturalbans.utils;

import dk.flasser.naturalbans.managers.SQLManager;

import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.platform.core.annotation.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

@Component
public class AltsUtil {
    private @Inject SQLManager SQLManager;
    private @Inject UUIDtoNameUtil uuidtoNameUtil;

    public ArrayList<String> checkAlts(UUID player) {
        if (!SQLManager.isConnected()) {
            return null;
        }

        String query = "SELECT DISTINCT IP FROM Spiller_IPs WHERE PlayerUUID = ?";
        Set<String> ips = new HashSet<>();
        ArrayList<String> alts = new ArrayList<>();

        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(query)) {
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

        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(searchQuery)) {
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
                        alts.add(uuidtoNameUtil.getNameFromUUID(altUUID));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return alts;

    }
}
