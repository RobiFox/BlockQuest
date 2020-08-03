package me.robifoxx.blockquest.command.sub.series;

import me.robifoxx.blockquest.BlockQuest;
import me.robifoxx.blockquest.command.SubCommand;
import me.robifoxx.blockquest.command.SubCommandHandler;
import me.robifoxx.blockquest.command.sub.GuideCommand;
import me.robifoxx.blockquest.command.sub.ListSubCommand;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class SeriesBaseCommand extends SubCommand {
    private static final SubCommandHandler SUBS;
    static {
        SUBS = new SubCommandHandler();
        SUBS.registerSubCommand(new CreateSeriesCommand());
        SUBS.registerSubCommand(new DeleteSeriesCommand());
        SUBS.registerSubCommand(new EditSeriesCommand());
        SUBS.registerSubCommand(new ToggleSeriesCommand());
        SUBS.registerSubCommand(new StatsSeriesCommand());
        SUBS.registerSubCommand(new TeleportSeriesCommand());
        SUBS.registerSubCommand(new ResetSeriesCommand());
    }

    @Override
    public String getBase() {
        return "series";
    }

    @Override
    public void onCommand(BlockQuest plugin, CommandSender sender, String[] args) {
        if(args.length <= 1 || !SUBS.handleSubCommand(plugin, sender, args[1], Arrays.copyOfRange(args, 0, args.length))) {
            sender.sendMessage("§cUnknown arguments.");
        }
    }
}
