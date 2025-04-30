package me.flasser.naturalbans.commands.utils;

import me.flasser.naturalbans.utils.AltsUtil;
import me.flasser.naturalbans.managers.FileManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class AltsCommand implements CommandExecutor {

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

        ArrayList<String> alts = AltsUtil.checkAlts(target.getUniqueId());

        assert alts != null;
        staff.sendMessage(new String[]{
                FileManager.getMessage("alts_success"), alts.toString()
        });

        return true;
    }
}
