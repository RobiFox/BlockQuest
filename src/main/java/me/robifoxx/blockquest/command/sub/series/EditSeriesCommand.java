package me.robifoxx.blockquest.command.sub.series;

import me.robifoxx.blockquest.BlockQuest;
import me.robifoxx.blockquest.api.BlockQuestAPI;
import me.robifoxx.blockquest.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditSeriesCommand extends SubCommand {
    @Override
    public String getBase() {
        return "edit";
    }

    @Override
    public void onCommand(BlockQuest blockQuest, CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can't be ran from console!");
            return;
        }
        String id = args[0];
        if(BlockQuestAPI.getInstance().getSeries(id) == null) {
            sender.sendMessage("§cA series with the id " + id + " doesn't exist!");
            return;
        }
        blockQuest.playersInEdit.put(sender.getName(), id);
        sender.sendMessage("§aEntered edit mode for " + id + "!");
        sender.sendMessage("§aClick on a block to add it as a Hidden Block.");
        sender.sendMessage("§aLeft click the air to exit edit mode");
    }
}
