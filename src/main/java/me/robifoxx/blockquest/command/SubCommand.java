package me.robifoxx.blockquest.command;

import me.robifoxx.blockquest.BlockQuest;
import org.bukkit.command.CommandSender;

public abstract class SubCommand {
    public abstract String getBase();
    public abstract void onCommand(BlockQuest plugin, CommandSender sender, String[] args);
}
