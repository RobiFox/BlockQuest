package me.robifoxx.blockquest.command.sub;

import me.robifoxx.blockquest.BlockQuest;
import me.robifoxx.blockquest.api.BlockQuestAPI;
import me.robifoxx.blockquest.api.BlockQuestSeries;
import me.robifoxx.blockquest.command.SubCommand;
import org.bukkit.command.CommandSender;

public class ListSubCommand extends SubCommand {
    @Override
    public String getBase() {
        return "list";
    }

    @Override
    public void onCommand(BlockQuest plugin, CommandSender sender, String[] args) {
        sender.sendMessage("§2BlockQuest series:");
        for(BlockQuestSeries series : BlockQuestAPI.getInstance().getSeriesList()) {
            sender.sendMessage(" " + (series.isEnabled() ? "§a" : "§c") + series.getID());
        }
    }
}
