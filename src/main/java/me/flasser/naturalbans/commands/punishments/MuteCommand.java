package me.flasser.naturalbans.commands.punishments;

import me.flasser.naturalbans.utils.DurationUtil;
import me.flasser.naturalbans.managers.PunishmentManager;
import me.flasser.naturalbans.managers.FileManager;

import me.flasser.naturalbans.utils.getReasonUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;

public class MuteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player staff = (Player) sender;

        if (!staff.hasPermission("naturalbans.mute")) {
            staff.sendMessage(FileManager.getMessage("no_permission"));
            return true;
        }

        switch (args.length) {
            case 0:
                sender.sendMessage(FileManager.getMessage("missing_player"));
                return true;
            case 1:
                sender.sendMessage(FileManager.getMessage("missing_duration"));
                return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (target == null || (!target.hasPlayedBefore()) && !target.isOnline()) {
            sender.sendMessage(FileManager.getMessage("not_joined_before"));
            return true;
        }

        Long duration = DurationUtil.parseDuration(args[1]);

        if (duration == null) {
            sender.sendMessage(FileManager.getMessage("invalid_duration"));
            return true;
        }

        String reason;
        if (args[args.length-1].equals("-s")) {
            reason = getReasonUtil.getReason(args, 2,1);
            staff.sendMessage(FileManager.getMessage("mute_success_silent")
                    .replace("{target}", target.getName())
                    .replace("{expires}", (new Date(duration)).toString())
                    .replace("{player}", staff.getName())
            );
        } else {
            reason = getReasonUtil.getReason(args, 2,0);
            staff.sendMessage(FileManager.getMessage("mute_success_loud")
                    .replace("{target}", target.getName())
                    .replace("{expires}", (new Date(duration)).toString())
                    .replace("{player}", staff.getName())
            );
        }

        if (!PunishmentManager.isMuted(target.getUniqueId())) {
            PunishmentManager.addMute(target.getUniqueId(), reason, duration, staff.getUniqueId());
        } else {
            PunishmentManager.overrideMute(target.getUniqueId(), reason, duration, staff.getUniqueId());
        }

        return true;
    }
}
