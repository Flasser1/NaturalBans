package dk.flasser.naturalbans.commands.punishments;

import dk.flasser.naturalbans.managers.FileManager;
import dk.flasser.naturalbans.managers.PunishmentManager;
import dk.flasser.naturalbans.utils.DurationUtil;

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
        label = "mute",
        aliases = {"nmute"}
)
@Async
public class MuteCommand implements CommandService {

    private @Inject PunishmentManager punishmentManager;
    private @Inject FileManager fileManager;
    private @Inject DurationUtil durationUtil;

    @Executor
    public void _def(@Context Player player) {
        if (!player.hasPermission("naturalbans.mute")) {
            player.sendMessage(fileManager.getMessage("no_permission"));
            return;
        }

        player.sendMessage(fileManager.getMessage("missing_player"));
    }

    @Executor(pattern = "<player> ?...")
    public void mute(
            @Context Player player,
            @Arg("player") OfflinePlayer target,
            @Arg("reason") Option<String> args
    ) {
        if (!player.hasPermission("naturalbans.mute")) {
            player.sendMessage(fileManager.getMessage("no_permission"));
            return;
        }

        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            player.sendMessage(fileManager.getMessage("not_joined_before"));
            return;
        }

        String muteArgs = args.orElseNull();

        Long duration = null;

        if (muteArgs != null && !muteArgs.trim().isEmpty()) {
            duration = durationUtil.parseDuration(
                    muteArgs.trim().split("\\s+", 2)[0]
            );
        }

        boolean silent = muteArgs != null && muteArgs.endsWith("-s");

        String reasonString = null;

        if (silent) {
            reasonString = muteArgs.substring(
                    muteArgs.trim().split("\\s+", 2)[0].length(),
                    muteArgs.length() - 2
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
                    fileManager.getMessage("mute_success_silent")
                            .replace("{target}", target.getName())
                            .replace("{expires}", expires)
                            .replace("{player}", player.getName())
                            .replace("{reason}", reasonString)
            );
        } else {
            Bukkit.getServer().broadcastMessage(
                    fileManager.getMessage("mute_success_loud")
                            .replace("{target}", target.getName())
                            .replace("{expires}", expires)
                            .replace("{player}", player.getName())
                            .replace("{reason}", reasonString)
            );
        }

        if (!punishmentManager.isMuted(target.getUniqueId())) {

            punishmentManager.addMute(
                    target.getUniqueId(),
                    reasonString,
                    duration,
                    player.getUniqueId()
            );

        } else {

            punishmentManager.overrideMute(
                    target.getUniqueId(),
                    reasonString,
                    duration,
                    player.getUniqueId()
            );
        }
    }
}