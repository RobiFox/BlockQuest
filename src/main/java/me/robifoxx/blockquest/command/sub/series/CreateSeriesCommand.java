package me.robifoxx.blockquest.command.sub.series;

import me.robifoxx.blockquest.BlockQuest;
import me.robifoxx.blockquest.api.BlockQuestAPI;
import me.robifoxx.blockquest.command.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateSeriesCommand extends SubCommand {
    private static final List<String> DEFAULT_FIND_BLOCK_COMMANDS = new ArrayList<>(Arrays.asList(
            "particle mobSpell %locX% %locY% %locZ% 0.25 0.25 0.25 1 10",
            "rawmsg %player% true &a&lBlock&2&lQUEST",
            "rawmsg %player% false &a",
            "rawmsg %player% true &fYou found a block!",
            "rawmsg %player% true &f%blocksLeft% left",
            "give %player% diamond 1"));
    private static final List<String> DEFAULT_ALL_BLOCKS_FOUND = new ArrayList<>(Arrays.asList(
            "rawmsg %player% true &a&lBlock&2&lQUEST",
            "rawmsg %player% false &a",
            "rawmsg %player% true &fYou found &lALL &fblocks!",
            "rawmsg %player% true &fNice!",
            "give %player% diamond_block 1"));
    private static final List<String> DEFAULT_ALREADY_FOUND = new ArrayList<>(Arrays.asList(
            "rawmsg %player% true &a&lBlock&2&lQUEST",
            "rawmsg %player% false &a",
            "rawmsg %player% true &fYou have already found this block!"));

    private static final List<String> DEFAULT_ALREADY_FOUND_ALL = new ArrayList<>(Arrays.asList(
            "rawmsg %player% true &a&lBlock&2&lQUEST",
            "rawmsg %player% false &a",
            "rawmsg %player% true &fYou have already found all blocks!"));

    @Override
    public String getBase() {
        return "create";
    }

    @Override
    public void onCommand(BlockQuest blockQuest, CommandSender sender, String[] args) {
        String id = args[0];
        if(BlockQuestAPI.getInstance().getSeries(id) != null) {
            sender.sendMessage("§cA series with the id " + id + " already exists!");
            return;
        }
        blockQuest.getConfig().set("series." + id + ".enabled", false);
        blockQuest.getConfig().set("series." + id + ".find-block-commands", DEFAULT_FIND_BLOCK_COMMANDS);
        blockQuest.getConfig().set("series." + id + ".all-blocks-found-commands", DEFAULT_ALL_BLOCKS_FOUND);
        blockQuest.getConfig().set("series." + id + ".already-found-commands", DEFAULT_ALREADY_FOUND);
        blockQuest.getConfig().set("series." + id + ".already-found-all-blocks", DEFAULT_ALREADY_FOUND_ALL);
        blockQuest.getConfig().set("series." + id + ".blocks", new ArrayList<String>());
        blockQuest.saveConfig();
        blockQuest.registerDefaultSeries(id, BlockQuestAPI.getInstance());
        sender.sendMessage("§aCreated BlockQuest series " + id + "!");
    }
}
