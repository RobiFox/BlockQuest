package me.robifoxx.block.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

public class BlockQuestTab implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> tabList = null;
        if(!sender.hasPermission("blockquest.command")) {
            return null;
        }
        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("stats")) {
                List<String> list = new ArrayList<>();
                Bukkit.getOnlinePlayers().forEach(ply -> list.add(ply.getName()));
                tabList = list;
            }
        } else if(args.length == 1) {
            tabList = new ArrayList<>(Arrays.asList("reload", "stats", "save"));
        }
        List<String> newTabList = null;
        if(tabList != null) {
            newTabList = new ArrayList<>(tabList);
            if(args[args.length - 1] != null) {
                tabList.stream().filter(en -> !en.startsWith(args[args.length - 1])).forEach(newTabList::remove);
            }
        }
        return newTabList;
    }
}
