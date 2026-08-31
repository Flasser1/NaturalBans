package dk.flasser.naturalbans.commands.punishments;

import dk.flasser.naturalbans.utils.DurationUtil;
import dk.flasser.naturalbans.managers.PunishmentManager;
import dk.flasser.naturalbans.managers.FileManager;

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
        label = "ipban",
        aliases = {"nipban"}
)
@Async
public class IPBanCommand implements CommandService {

    private @Inject PunishmentManager punishmentManager;
    private @Inject FileManager fileManager;
    private @Inject DurationUtil durationUtil;

    @Executor
    public void _def(@Context Player player) {
        if (!player.hasPermission("naturalbans.ipban")) {
            player.sendMessage(fileManager.getMessage("no_permission"));
            return;
        }

        player.sendMessage(fileManager.getMessage("missing_player"));
    }

    @Executor(pattern = "<player> ?...")
    public void ipban(
            @Context Player player,
            @Arg("player") OfflinePlayer target,
            @Arg("reason") Option<String> args
    ) {
        if (!player.hasPermission("naturalbans.ipban")) {
            player.sendMessage(fileManager.getMessage("no_permission"));
            return;
        }

        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            player.sendMessage(fileManager.getMessage("not_joined_before"));
            return;
        }

        String banArgs = args.orElseNull();

        Long duration = null;

        if (banArgs != null && !banArgs.trim().isEmpty()) {
            duration = durationUtil.parseDuration(
                    banArgs.trim().split("\\s+", 2)[0]
            );
        }

        boolean silent = banArgs != null && banArgs.endsWith("-s");

        String reasonString = null;

        if (silent) {
            reasonString = banArgs.substring(
                    banArgs.trim().split("\\s+", 2)[0].length(),
                    banArgs.length() - 2
            ).trim();
        }

        if (reasonString == null || reasonString.isEmpty()) {
            reasonString = "No reason specified";
        }

        String expires = duration == null
                ? "Never"
                : durationUtil.getRemaining(duration);

        if (silent) {
            player.sendMessage(
                    fileManager.getMessage("ipban_success_silent")
                            .replace("{target}", target.getName())
                            .replace("{expires}", expires)
                            .replace("{player}", player.getName())
                            .replace("{reason}", reasonString)
            );
        } else {
            Bukkit.getServer().broadcastMessage(
                    fileManager.getMessage("ipban_success_loud")
                            .replace("{target}", target.getName())
                            .replace("{expires}", expires)
                            .replace("{player}", player.getName())
                            .replace("{reason}", reasonString)
            );
        }

        if (!punishmentManager.isIPBanned(target.getUniqueId())) {

            punishmentManager.addIPBan(
                    target.getUniqueId(),
                    reasonString,
                    duration,
                    player.getUniqueId()
            );

            if (target.isOnline()) {
                target.getPlayer().kickPlayer(reasonString);
            }

        } else {

            punishmentManager.overrideIPBan(
                    target.getUniqueId(),
                    reasonString,
                    duration,
                    player.getUniqueId()
            );
        }
    }
}