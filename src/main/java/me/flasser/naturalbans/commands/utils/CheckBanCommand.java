package me.flasser.naturalbans.commands.utils;

import me.flasser.naturalbans.managers.FileManager;
import me.flasser.naturalbans.managers.PunishmentManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckBanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player staff = (Player) sender;

        if (!staff.hasPermission("naturalbans.alts")) {
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

        if (!PunishmentManager.isBanned(target.getUniqueId())) {
            sender.sendMessage(FileManager.getMessage("missing_ban"));
            return true;
        }

        PunishmentManager.BanInfo ban = PunishmentManager.getBanInfo(target.getUniqueId());
        assert ban != null;
        sender.sendMessage(new String[]{"§c§lNatural§f§lBans",
                "§fPlayer ban information:",
                "Player: "+ target.getName(),
                "Staff: "+ ban.staffName,
                "",
                "Reason: "+ban.reason,
                "Date: "+ban.date,
                "Expires: "+ban.expires
        });

        return true;
    }
}
