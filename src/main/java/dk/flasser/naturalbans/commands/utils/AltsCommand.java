package dk.flasser.naturalbans.commands.utils;

import dk.flasser.naturalbans.managers.FileManager;
import dk.flasser.naturalbans.utils.AltsUtil;

import eu.okaeri.commands.annotation.Arg;
import eu.okaeri.commands.annotation.Context;
import eu.okaeri.commands.annotation.Executor;
import eu.okaeri.commands.bukkit.annotation.Async;
import eu.okaeri.commands.service.CommandService;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.platform.core.annotation.Component;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@Component
@eu.okaeri.commands.annotation.Command(
        label = "alts",
        aliases = {"alt"}
)
@Async
public class AltsCommand implements CommandService {

    private @Inject AltsUtil altsUtil;
    private @Inject FileManager fileManager;

    @Executor
    public void _def(@Context Player player) {
        if (!player.hasPermission("naturalbans.alts")) {
            player.sendMessage(fileManager.getMessage("no_permission"));
            return;
        }

        player.sendMessage(fileManager.getMessage("missing_player"));
    }

    @Executor(pattern = "<player>")
    public void alts(
            @Context Player player,
            @Arg("player") OfflinePlayer target
    ) {
        if (!player.hasPermission("naturalbans.alts")) {
            player.sendMessage(fileManager.getMessage("no_permission"));
            return;
        }

        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            player.sendMessage(fileManager.getMessage("not_joined_before"));
            return;
        }

        ArrayList<String> alts = altsUtil.checkAlts(target.getUniqueId());

        if (alts == null) {
            player.sendMessage(fileManager.getMessage("alts_success"));
            return;
        }

        player.sendMessage(new String[]{
                fileManager.getMessage("alts_success"),
                alts.toString()
        });
    }
}