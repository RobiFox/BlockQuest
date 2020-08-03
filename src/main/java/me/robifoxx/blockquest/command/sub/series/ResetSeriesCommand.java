package me.robifoxx.blockquest.command.sub.series;

import me.robifoxx.blockquest.BlockQuest;
import me.robifoxx.blockquest.api.BlockQuestAPI;
import me.robifoxx.blockquest.api.BlockQuestSeries;
import me.robifoxx.blockquest.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetSeriesCommand extends SubCommand {
    @Override
    public String getBase() {
        return "reset";
    }

    @Override
    public void onCommand(BlockQuest blockQuest, CommandSender sender, String[] args) {
        String id = args[0];
        if(BlockQuestAPI.getInstance().getSeries(id) == null) {
            sender.sendMessage("§cA series with the id " + id + " doesn't exist!");
            return;
        }
        if(args.length < 3) {
            sender.sendMessage("§cMissing arguments.");
            sender.sendMessage("§cPlease specify a player, or use * to reset everyone's.");
            return;
        }
        String player = args[2];
        if(player.equalsIgnoreCase("*")) {
            sender.sendMessage("§cClearing everyone's data for " + id);
            for(String key : BlockQuestAPI.getInstance().getDataStorage().getAllUsers(id)) {
                BlockQuestAPI.getInstance().getDataStorage().clearStats(key, id);
            }
        } else {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
            sender.sendMessage("§cClearing " + player + "'s data for " + id);
            BlockQuestAPI.getInstance().getDataStorage().clearStats(BlockQuestAPI.getInstance().getPlayerKey(offlinePlayer), id);
        }
        sender.sendMessage("§aDone.");
    }
}
