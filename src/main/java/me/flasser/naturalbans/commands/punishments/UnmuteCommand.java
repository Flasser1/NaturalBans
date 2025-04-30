package me.flasser.naturalbans.commands.punishments;

import me.flasser.naturalbans.managers.PunishmentManager;
import me.flasser.naturalbans.managers.FileManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnmuteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player staff = (Player) sender;

        if (!staff.hasPermission("naturalbans.unmute")) {
            staff.sendMessage(FileManager.getMessage("no_permission"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(FileManager.getMessage("missing_player"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (target == null || (!target.hasPlayedBefore()) && !target.isOnline()) {
            sender.sendMessage(FileManager.getMessage("not_joined_before"));
            return true;
        }

        if (!PunishmentManager.isMuted(target.getUniqueId())) {
            sender.sendMessage(FileManager.getMessage("missing_mute"));
            return true;
        }

        String reason;
        if (args[args.length-1].equals("-s")) {
            reason = toString().substring(2, args.length-1);
            staff.sendMessage(FileManager.getMessage("unmute_success_silent")
                    .replace("{target}", target.getName())
                    .replace("{player}", staff.getName())
            );
        } else {
            reason = toString().substring(2, args.length);
            staff.sendMessage(FileManager.getMessage("unmute_success_loud")
                    .replace("{target}", target.getName())
                    .replace("{player}", staff.getName())
            );
        }

        PunishmentManager.unMute(target.getUniqueId(), reason, staff.getUniqueId());

        return true;
    }
}
