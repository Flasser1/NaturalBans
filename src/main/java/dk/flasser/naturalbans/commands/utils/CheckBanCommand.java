package dk.flasser.naturalbans.commands.utils;

import dk.flasser.naturalbans.managers.FileManager;
import dk.flasser.naturalbans.managers.PunishmentManager;

import eu.okaeri.commands.annotation.Arg;
import eu.okaeri.commands.annotation.Context;
import eu.okaeri.commands.annotation.Executor;
import eu.okaeri.commands.bukkit.annotation.Async;
import eu.okaeri.commands.service.CommandService;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.platform.core.annotation.Component;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@Component
@eu.okaeri.commands.annotation.Command(
        label = "checkban",
        aliases = {"baninfo"}
)
@Async
public class CheckBanCommand implements CommandService {

    private @Inject PunishmentManager punishmentManager;
    private @Inject FileManager fileManager;

    @Executor
    public void _def(@Context Player player) {
        if (!player.hasPermission("naturalbans.checkban")) {
            player.sendMessage(fileManager.getMessage("no_permission"));
            return;
        }

        player.sendMessage(fileManager.getMessage("missing_player"));
    }

    @Executor(pattern = "<player>")
    public void checkBan(
            @Context Player player,
            @Arg("player") OfflinePlayer target
    ) {
        if (!player.hasPermission("naturalbans.checkban")) {
            player.sendMessage(fileManager.getMessage("no_permission"));
            return;
        }

        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            player.sendMessage(fileManager.getMessage("not_joined_before"));
            return;
        }

        if (!punishmentManager.isBanned(target.getUniqueId())) {
            player.sendMessage(fileManager.getMessage("missing_ban"));
            return;
        }

        PunishmentManager.PunishmentInfo ban =
                punishmentManager.getBanInfo(target.getUniqueId());

        if (ban == null) {
            player.sendMessage(fileManager.getMessage("missing_ban"));
            return;
        }

        player.sendMessage(new String[]{
                "§c§lNatural§f§lBans",
                "§fPlayer ban information:",
                "Player: " + target.getName(),
                "Staff: " + ban.staffName,
                "",
                "Reason: " + ban.reason,
                "Date: " + ban.date,
                "Expires: " + ban.expires
        });
    }
}