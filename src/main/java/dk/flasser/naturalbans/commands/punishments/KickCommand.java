package dk.flasser.naturalbans.commands.punishments;

import dk.flasser.naturalbans.managers.FileManager;
import dk.flasser.naturalbans.managers.PunishmentManager;

import eu.okaeri.commands.annotation.Arg;
import eu.okaeri.commands.annotation.Context;
import eu.okaeri.commands.annotation.Executor;
import eu.okaeri.commands.bukkit.annotation.Async;
import eu.okaeri.commands.service.CommandService;
import eu.okaeri.commands.service.Option;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.platform.core.annotation.Component;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@Component
@eu.okaeri.commands.annotation.Command(
        label = "kick",
        aliases = {"nkick"}
)
@Async
public class KickCommand implements CommandService {

    private @Inject PunishmentManager punishmentManager;
    private @Inject FileManager fileManager;

    @Executor
    public void _def(@Context Player player) {
        if (!player.hasPermission("naturalbans.kick")) {
            player.sendMessage(fileManager.getMessage("no_permission"));
            return;
        }

        player.sendMessage(fileManager.getMessage("missing_player"));
    }

    @Executor(pattern = "<player> ?...")
    public void kick(
            @Context Player player,
            @Arg("player") OfflinePlayer target,
            @Arg("reason") Option<String> reason
    ) {
        if (!player.hasPermission("naturalbans.kick")) {
            player.sendMessage(fileManager.getMessage("no_permission"));
            return;
        }

        if (target == null || !target.isOnline()) {
            player.sendMessage(fileManager.getMessage("not_online"));
            return;
        }

        Player playerTarget = (Player) target;

        String reasonString = reason.orElseNull();

        boolean silent = reasonString != null && reasonString.endsWith("-s");

        if (silent) {
            reasonString = reasonString.substring(0, reasonString.length() - 2).trim();
        }

        if (reasonString == null || reasonString.isEmpty()) {
            reasonString = "No reason specified";
        }

        if (silent) {
            player.sendMessage(fileManager.getMessage("kick_success_silent")
                    .replace("{target}", target.getName())
                    .replace("{player}", player.getName())
            );
        } else {
            Bukkit.getServer().broadcastMessage(fileManager.getMessage("kick_success_loud")
                    .replace("{target}", target.getName())
                    .replace("{player}", player.getName())
            );
        }

        punishmentManager.addKick(
                target.getUniqueId(),
                reasonString,
                player.getUniqueId()
        );

        playerTarget.kickPlayer(reasonString);
    }
}