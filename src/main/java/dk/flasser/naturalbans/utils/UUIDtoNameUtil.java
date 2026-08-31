package dk.flasser.naturalbans.utils;

import dk.flasser.naturalbans.managers.SQLManager;

import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.platform.core.annotation.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class UUIDtoNameUtil {
    private @Inject SQLManager SQLManager;

    public String getNameFromUUID(UUID player) {

        String name = "ERROR! Player not found.";
        String query = "SELECT * FROM Spiller WHERE UUID = ?";

        try (PreparedStatement ps = SQLManager.getConnection().prepareStatement(query)) {
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
