package me.flasser.naturalbans.managers;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.sql.*;

public class MySQLManager {

    public static String host = "mysql96.unoeuro.com";
    public static String port = "3306";
    public static String database = "naturalbans_com_db";
    public static String user = "naturalbans_com";
    public static String pass = "heEzRGftcw9Bgxnm3A2k";
    public static Connection con;

    static ConsoleCommandSender console = Bukkit.getConsoleSender();

    public static void connect() {
        if (!isConnected()) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database,user,pass);
                console.sendMessage("NATURALBANS: DATABASE CONNECTED");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void disconnect() {
        if (isConnected()) {
            try {
                con.close();
                console.sendMessage("NATURALBANS: DATABASE DISCONNECTED");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isConnected() {
        return (con != null);
    }

    public static Connection getConnection() {
        return con;
    }

    public static boolean isSetUp() {
        if (!isConnected()) {
            return false;
        }

        try {
            Statement stmt = con.createStatement();

            String[] tables = {"Spiller", "Staff", "Ban", "Kick", "Mute"};
            for (String table : tables) {
                ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE '" + table + "'");
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

    public static void setUp() {
        if (!isConnected()) {
            return;
        }

        try {
            con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS Spiller (" +
                    "UUID VARCHAR(36) PRIMARY KEY, " +
                    "Name VARCHAR(50) NOT NULL" +
                    ");");

            con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS Spiller_IPs (" +
                    "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                    "PlayerUUID VARCHAR(36) NOT NULL, " +
                    "IP VARCHAR(45) NOT NULL, " +
                    "LastSeen BIGINT NOT NULL, " +
                    "FOREIGN KEY (PlayerUUID) REFERENCES Spiller(UUID) ON DELETE CASCADE" +
                    ");");

            con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS Staff (" +
                    "UUID VARCHAR(36) PRIMARY KEY, " +
                    "Name VARCHAR(50) NOT NULL" +
                    ");");

            con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS Ban (" +
                    "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                    "PlayerUUID VARCHAR(36) NOT NULL, " +
                    "StaffUUID VARCHAR(36), " +
                    "Reason VARCHAR(255) NOT NULL, " +
                    "Date BIGINT NOT NULL, " +
                    "Expires BIGINT, " +
                    "Undone BOOLEAN DEFAULT FALSE, " +
                    "UndoneBy VARCHAR(36), " +
                    "UndoneReason VARCHAR(255), " +
                    "ServerScope VARCHAR(100), " +
                    "OriginServer VARCHAR(100), " +
                    "FOREIGN KEY (PlayerUUID) REFERENCES Spiller(UUID), " +
                    "FOREIGN KEY (StaffUUID) REFERENCES Staff(UUID)" +
                    ");");

            con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS Kick (" +
                    "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                    "PlayerUUID VARCHAR(36) NOT NULL, " +
                    "StaffUUID VARCHAR(36), " +
                    "Reason VARCHAR(255) NOT NULL, " +
                    "Date BIGINT NOT NULL, " +
                    "ServerScope VARCHAR(100), " +
                    "OriginServer VARCHAR(100), " +
                    "FOREIGN KEY (PlayerUUID) REFERENCES Spiller(UUID), " +
                    "FOREIGN KEY (StaffUUID) REFERENCES Staff(UUID)" +
                    ");");

            con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS Mute (" +
                    "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                    "PlayerUUID VARCHAR(36) NOT NULL, " +
                    "StaffUUID VARCHAR(36), " +
                    "Reason VARCHAR(255) NOT NULL, " +
                    "Date BIGINT NOT NULL, " +
                    "Expires BIGINT, " +
                    "Undone BOOLEAN DEFAULT FALSE, " +
                    "UndoneBy VARCHAR(36), " +
                    "UndoneReason VARCHAR(255), " +
                    "ServerScope VARCHAR(100), " +
                    "OriginServer VARCHAR(100), " +
                    "FOREIGN KEY (PlayerUUID) REFERENCES Spiller(UUID), " +
                    "FOREIGN KEY (StaffUUID) REFERENCES Staff(UUID)" +
                    ");");

            console.sendMessage("NATURALBANS: DATABASE TABLES SETUP COMPLETE");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}