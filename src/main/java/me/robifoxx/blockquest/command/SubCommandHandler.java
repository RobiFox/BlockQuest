package me.robifoxx.blockquest.command;

import me.robifoxx.blockquest.BlockQuest;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class SubCommandHandler {
    private HashMap<String, SubCommand> subCommands;
    public SubCommandHandler() {
        subCommands = new HashMap<>();
    }

    public void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getBase(), subCommand);
    }

    public SubCommand findSubCommand(String base) {
        return subCommands.get(base);
    }

    public boolean handleSubCommand(BlockQuest plugin, CommandSender sender, String subCommand, String[] args) {
        SubCommand cmd = findSubCommand(subCommand);
        if(cmd == null) {
            return false;
        } else {
            cmd.onCommand(plugin, sender, args);
            return true;
        }
    }
}
