package me.flasser.naturalbans.commands.punishments;

import me.flasser.naturalbans.utils.DurationUtil;
import me.flasser.naturalbans.managers.PunishmentManager;
import me.flasser.naturalbans.managers.FileManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;

public class BanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player staff = (Player) sender;

        if (!staff.hasPermission("naturalbans.ban")) {
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
            reason = toString().substring(2, args.length-1);
            staff.sendMessage(FileManager.getMessage("ban_success_silent")
                    .replace("{target}", target.getName())
                    .replace("{expires}", duration.toString())
                    .replace("{player}", staff.getName())
            );
        } else {
            reason = toString().substring(2, args.length);
            staff.sendMessage(FileManager.getMessage("ban_success_loud")
                    .replace("{target}", target.getName())
                    .replace("{expires}", duration.toString())
                    .replace("{player}", staff.getName())
            );
        }

        if (!PunishmentManager.isBanned(target.getUniqueId())) {
            Bukkit.getServer().broadcastMessage("111 Target: "+target+" Target name: "+target.getName()+" Reason: "+reason+" Duation: "+duration+" Staff: "+staff+" Staff name: "+staff.getName());
            PunishmentManager.addBan(target.getUniqueId(), reason, duration, staff.getUniqueId());
        } else {
            Bukkit.getServer().broadcastMessage("222 Target: "+target+" Target name: "+target.getName()+" Reason: "+reason+" Duation: "+duration+" Staff: "+staff+" Staff name: "+staff.getName());
            PunishmentManager.overrideBan(target.getUniqueId(), reason, duration, staff.getUniqueId());
        }

        return true;
    }
}
