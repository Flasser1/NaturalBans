package me.flasser.naturalbans.utils;

import me.flasser.naturalbans.managers.MySQLManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UUIDtoNameUtil {

    public static String getNameFromUUID(UUID player) {

        String name = "ERROR! Player not found.";
        String query = "SELECT * FROM Spiller WHERE UUID = ?";

        try (PreparedStatement ps = MySQLManager.getConnection().prepareStatement(query)) {
            ps.setString(1, player.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("Name");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return name;
    }
}
