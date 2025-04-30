package me.flasser.naturalbans;

import me.flasser.naturalbans.commands.punishments.*;
import me.flasser.naturalbans.commands.utils.*;
import me.flasser.naturalbans.listeners.*;
import me.flasser.naturalbans.managers.MySQLManager;
import me.flasser.naturalbans.managers.FileManager;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class NaturalBans extends JavaPlugin {

    private static NaturalBans instance;

    @Override
    public void onEnable() {

        instance = this;

        FileManager.createMessages();

        MySQLManager.connect();

        if (!MySQLManager.isSetUp()) {
            MySQLManager.setUp();
        }

        int pluginId = 25176;
        Metrics metrics = new Metrics(this, pluginId);

        getServer().getPluginManager().registerEvents(new ConnectListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);

        this.getCommand("ban").setExecutor(new BanCommand());
        this.getCommand("kick").setExecutor(new KickCommand());
        this.getCommand("mute").setExecutor(new MuteCommand());
        this.getCommand("unban").setExecutor(new UnbanCommand());
        this.getCommand("unmute").setExecutor(new UnmuteCommand());
        this.getCommand("alts").setExecutor(new AltsCommand());

    }

    @Override
    public void onDisable() {
        MySQLManager.disconnect();
    }

    public static NaturalBans getInstance() {
        return instance;
    }
}
