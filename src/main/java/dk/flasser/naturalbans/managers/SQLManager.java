package dk.flasser.naturalbans.managers;

import dk.flasser.naturalbans.NaturalBans;

import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.platform.core.annotation.Component;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.io.File;
import java.sql.*;
@Component
public class SQLManager {
    private @Inject NaturalBans naturalBans;

    public Connection con;

    ConsoleCommandSender console = Bukkit.getConsoleSender();

    public void connect() {
        if (isConnected()) {
            return;
        }

        File dataFolder = Bukkit.getPluginManager().getPlugin("NaturalBans").getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        try {
            Class.forName("org.sqlite.JDBC");

            File dbFile = new File(Bukkit.getPluginManager().getPlugin("NaturalBans").getDataFolder(), "database.db");
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            con = DriverManager.getConnection(url);

            con.createStatement().execute("PRAGMA foreign_keys = ON;");

            naturalBans.getLogger().info("NATURALBANS: DATABASE CONNECTED");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        if (!isConnected()) {
            return;
        }

        try {
            con.close();
            naturalBans.getLogger().info("NATURALBANS: DATABASE DISCONNECTED");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return (con != null);
    }

    public Connection getConnection() {
        return con;
    }

    public boolean isSetUp() {
        if (!isConnected()) {
            return false;
        }

        try {
            Statement stmt = con.createStatement();

            String[] tables = {"Spiller", "Ban", "IPBan", "Kick", "Mute"};
            for (String table : tables) {
                ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + table + "'");
                if (!rs.next()) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setUp() {
        if (!isConnected()) {
            return;
        }

        try {
            con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS Spiller (" +
                    "UUID VARCHAR(36) PRIMARY KEY, " +
                    "Name VARCHAR(50) NOT NULL" +
                    ");");

            con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS Spiller_IPs (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "PlayerUUID VARCHAR(36) NOT NULL, " +
                    "IP VARCHAR(45) NOT NULL, " +
                    "LastSeen BIGINT NOT NULL, " +
                    "FOREIGN KEY (PlayerUUID) REFERENCES Spiller(UUID) ON DELETE CASCADE" +
                    ");");

            con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS Ban (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "PlayerUUID VARCHAR(36) NOT NULL, " +
                    "StaffUUID VARCHAR(36), " +
                    "Reason VARCHAR(255) NOT NULL, " +
                    "Date BIGINT NOT NULL, " +
                    "Expires BIGINT, " +
                    "Undone INTEGER DEFAULT 0, " +
                    "UndoneBy VARCHAR(36), " +
                    "UndoneReason VARCHAR(255), " +
                    "ServerScope VARCHAR(100), " +
                    "OriginServer VARCHAR(100), " +
                    "FOREIGN KEY (PlayerUUID) REFERENCES Spiller(UUID), " +
                    "FOREIGN KEY (StaffUUID) REFERENCES Spiller(UUID)" +
                    ");");

            con.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS IPBan (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "PlayerUUID VARCHAR(36) NOT NULL, " +
                    "StaffUUID VARCHAR(36), " +
                    "Reason VARCHAR(255) NOT NULL, " +
                    "Date BIGINT NOT NULL, " +
                    "Expires BIGINT, " +
                    "Undone BOOLEAN DEFAULT FALSE, " +
                    "UndoneBy VARCHAR(36), " +
                    "UndoneReason VARCHAR(255), " +
                    "FOREIGN KEY (PlayerUUID) REFERENCES Spiller(UUID), " +
                    "FOREIGN KEY (StaffUUID) REFERENCES Spiller(UUID)" +
                    ");");

            con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS Kick (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "PlayerUUID VARCHAR(36) NOT NULL, " +
                    "StaffUUID VARCHAR(36), " +
                    "Reason VARCHAR(255) NOT NULL, " +
                    "Date BIGINT NOT NULL, " +
                    "ServerScope VARCHAR(100), " +
                    "OriginServer VARCHAR(100), " +
                    "FOREIGN KEY (PlayerUUID) REFERENCES Spiller(UUID), " +
                    "FOREIGN KEY (StaffUUID) REFERENCES Spiller(UUID)" +
                    ");");

            con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS Mute (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "PlayerUUID VARCHAR(36) NOT NULL, " +
                    "StaffUUID VARCHAR(36), " +
                    "Reason VARCHAR(255) NOT NULL, " +
                    "Date BIGINT NOT NULL, " +
                    "Expires BIGINT, " +
                    "Undone INTEGER DEFAULT 0, " +
                    "UndoneBy VARCHAR(36), " +
                    "UndoneReason VARCHAR(255), " +
                    "ServerScope VARCHAR(100), " +
                    "OriginServer VARCHAR(100), " +
                    "FOREIGN KEY (PlayerUUID) REFERENCES Spiller(UUID), " +
                    "FOREIGN KEY (StaffUUID) REFERENCES Spiller(UUID)" +
                    ");");

            console.sendMessage("NATURALBANS: DATABASE TABLES SETUP COMPLETE");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}