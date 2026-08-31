package dk.flasser.naturalbans;

import dk.flasser.naturalbans.managers.SQLManager;
import dk.flasser.naturalbans.managers.FileManager;

import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.platform.bukkit.OkaeriBukkitPlugin;
import eu.okaeri.platform.core.annotation.Scan;
import eu.okaeri.platform.core.plan.ExecutionPhase;
import eu.okaeri.platform.core.plan.Planned;

import org.bstats.bukkit.Metrics;

@Scan(deep = true)
public final class NaturalBans extends OkaeriBukkitPlugin {

    private @Inject FileManager fileManager;
    private @Inject SQLManager SQLManager;

    @Planned(ExecutionPhase.STARTUP)
    public void onStartup() {
        getLogger().info("NATURALBANS: STARTING UP");
    }

    @Planned(ExecutionPhase.POST_SETUP)
    public void afterSetup() {
        int pluginId = 25176;
        Metrics metrics = new Metrics(this, pluginId);

        this.getLogger().info("NATURALBANS: SETTING UP MESSAGES");
        fileManager.createMessages();

        this.getLogger().info("NATURALBANS: CONNECTING TO DATABASE");
        SQLManager.connect();

        if (!SQLManager.isSetUp()) {
            this.getLogger().info("NATURALBANS: SETTING UP DATABASE");
            SQLManager.setUp();
        }
    }

    @Planned(ExecutionPhase.SHUTDOWN)
    public void onShutdown() {
        this.getLogger().info("NATURALBANS: DISCONNECTING FROM DATABASE");
        SQLManager.disconnect();

        getLogger().info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }
}
