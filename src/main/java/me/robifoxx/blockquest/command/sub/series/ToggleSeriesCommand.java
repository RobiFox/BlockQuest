package me.robifoxx.blockquest.command.sub.series;

import me.robifoxx.blockquest.BlockQuest;
import me.robifoxx.blockquest.api.BlockQuestAPI;
import me.robifoxx.blockquest.api.BlockQuestSeries;
import me.robifoxx.blockquest.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class ToggleSeriesCommand extends SubCommand {
    @Override
    public String getBase() {
        return "toggle";
    }

    @Override
    public void onCommand(BlockQuest blockQuest, CommandSender sender, String[] args) {
        String id = args[0];
        BlockQuestSeries series = BlockQuestAPI.getInstance().getSeries(id);
        if(series == null) {
            sender.sendMessage("§cA series with the id " + id + " doesn't exist!");
            return;
        }
        if(series.isEnabled()) {
            sender.sendMessage("§cDisabled series " + id + "!");
        } else {
            sender.sendMessage("§aEnabled series " + id + "!");
        }
        BlockQuestAPI.getInstance().getSeries(id).setEnabled(!series.isEnabled());
    }
}
