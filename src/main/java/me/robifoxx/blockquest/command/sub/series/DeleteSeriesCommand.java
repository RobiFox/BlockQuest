package me.robifoxx.blockquest.command.sub.series;

import me.robifoxx.blockquest.BlockQuest;
import me.robifoxx.blockquest.api.BlockQuestAPI;
import me.robifoxx.blockquest.command.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeleteSeriesCommand extends SubCommand {
    @Override
    public String getBase() {
        return "delete";
    }

    @Override
    public void onCommand(BlockQuest blockQuest, CommandSender sender, String[] args) {
        String id = args[0];
        if(blockQuest.getConfig().get("series." + id) == null) {
            sender.sendMessage("§cA §ldefault §cseries with the id " + id + " doesn't exist!");
            return;
        }
        blockQuest.getConfig().set("series." + id, null);
        blockQuest.saveConfig();
        sender.sendMessage("§aDeleted BlockQuest series " + id + "!");
    }
}
