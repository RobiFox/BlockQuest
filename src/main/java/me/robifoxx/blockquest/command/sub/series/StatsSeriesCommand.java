package me.robifoxx.blockquest.command.sub.series;

import me.robifoxx.blockquest.BlockQuest;
import me.robifoxx.blockquest.api.BlockQuestAPI;
import me.robifoxx.blockquest.api.BlockQuestDataStorage;
import me.robifoxx.blockquest.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StatsSeriesCommand extends SubCommand {
    @Override
    public String getBase() {
        return "stats";
    }

    @Override
    public void onCommand(BlockQuest blockQuest, CommandSender sender, String[] args) {
        String id = args[0];
        if(BlockQuestAPI.getInstance().getSeries(id) == null) {
            sender.sendMessage("§cA series with the id " + id + " doesn't exist!");
            return;
        }
        int totalBlocks = BlockQuestAPI.getInstance().getBlockCount(blockQuest, id);
        BlockQuestDataStorage bqds = BlockQuestAPI.getInstance().getDataStorage();
        if(args.length == 3) {
            sender.sendMessage("§a§lStats for series " + id + " for " + args[2] + ":");
            sender.sendMessage("§aThis player found §e" + bqds.getFoundBlockCount(BlockQuestAPI.getInstance().getPlayerKey(Bukkit.getOfflinePlayer(args[2])), id) + " §ablocks out of §e" + totalBlocks);
        } else {
            List<String> users = BlockQuestAPI.getInstance().getDataStorage().getAllUsers(id);
            int foundAll = 0;
            for(String user : users) {
                if(bqds.getFoundBlockCount(user, id) >= totalBlocks) {
                    foundAll++;
                }
            }
            sender.sendMessage("§a§lStats for series " + id + ":");
            sender.sendMessage("§aOut of §e" + users.size() + "§a players, §e" + foundAll + " §afound all blocks");
        }
    }
}
