package me.robifoxx.blockquest.command;

import me.robifoxx.blockquest.BlockQuest;
import me.robifoxx.blockquest.command.sub.GuideCommand;
import me.robifoxx.blockquest.command.sub.ListSubCommand;
import me.robifoxx.blockquest.command.sub.series.SeriesBaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class BlockQuestBaseCommand implements CommandExecutor {
    private static final SubCommandHandler BASE_SUBS;
    static {
        BASE_SUBS = new SubCommandHandler();
        BASE_SUBS.registerSubCommand(new ListSubCommand());
        BASE_SUBS.registerSubCommand(new GuideCommand());
        BASE_SUBS.registerSubCommand(new SeriesBaseCommand());
    }

    private BlockQuest blockQuest;

    public BlockQuestBaseCommand(BlockQuest blockQuest) {
        this.blockQuest = blockQuest;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!sender.hasPermission("blockquest.command")) {
            sender.sendMessage(blockQuest.getConfig().getString("no-permission-msg").replace("&", "§"));
            return true;
        }
        if(args.length == 0) {
            sender.sendMessage("§7§m----------------------------------------");
            sender.sendMessage(" §2§lWelcome to BlockQuest!");
            sender.sendMessage("  §aIf this is your first time using BlockQuest,");
            sender.sendMessage("  §ayou might want to run §2/blockquest guide");
            sender.sendMessage("§7§m----------------------------------------");
            // TODO replace with automatic help list, instead of hard coded
            sender.sendMessage(getHelpFormat("series <id> create", "Creates a new series, which will contain hidden blocks"));
            sender.sendMessage(getHelpFormat("series <id> delete", "Deletes the series which has the specified id"));
            sender.sendMessage(getHelpFormat("series <id> edit", "Add or remove hidden blocks for the specified id"));
            sender.sendMessage(getHelpFormat("series <id> toggle", "Enables or disables the series with the specified id"));
            sender.sendMessage(getHelpFormat("series <id> stats [player]", "Displays who found all blocks in that series. If player is specified, it will display their stats."));
            sender.sendMessage(getHelpFormat("series <id> teleport <number>", "Teleports you to the specified index"));
            sender.sendMessage(getHelpFormat("series <id> reset <player>", "Resets the specified player's statistics. Use * to clear everyone's."));
            sender.sendMessage(getHelpFormat("list", "Lists all series"));
        } else if(!BASE_SUBS.handleSubCommand(blockQuest, sender, args[0], Arrays.copyOfRange(args, 1, args.length))) {
            sender.sendMessage("§cUnknown arguments.");
        }
        return true;
    }

    private String getHelpFormat(String args, String description) {
        return " §2/blockquest §a" + args + " §8§l>>§7 " + description;
    }
}
