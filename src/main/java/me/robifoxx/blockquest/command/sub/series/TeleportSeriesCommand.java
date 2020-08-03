package me.robifoxx.blockquest.command.sub.series;

import me.robifoxx.blockquest.BlockQuest;
import me.robifoxx.blockquest.api.BlockQuestAPI;
import me.robifoxx.blockquest.api.BlockQuestSeries;
import me.robifoxx.blockquest.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportSeriesCommand extends SubCommand {
    @Override
    public String getBase() {
        return "teleport";
    }

    @Override
    public void onCommand(BlockQuest blockQuest, CommandSender sender, String[] args) {
        String id = args[0];
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can't be ran from console!");
            return;
        }
        BlockQuestSeries series = BlockQuestAPI.getInstance().getSeries(id);
        if(series == null) {
            sender.sendMessage("§cA series with the id " + id + " doesn't exist!");
            return;
        }
        if(args.length < 3) {
            sender.sendMessage("§cMissing arguments.");
            sender.sendMessage("§cPlease specify an index, starting from 1");
            return;
        }
        int index = Integer.parseInt(args[2]);
        int max = series.getHiddenBlocks().size();
        if(index <= 0 || index > max) {
            sender.sendMessage("§cInvalid index!");
            sender.sendMessage("§cIndex must be between 1 and " + max);
            return;
        }
        ((Player) sender).teleport(series.getHiddenBlocks().get(index - 1));
        sender.sendMessage("§aTeleported to the block with id of §e" + index + "§a, out of §e" + max);
    }
}
