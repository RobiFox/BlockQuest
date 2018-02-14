package me.robifoxx.block;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BlockQuestAPI {
    private static BlockQuestAPI instance;
    private Main plugin;
    public BlockQuestAPI(Main plugin) {
        this.plugin = plugin;
    }
    public static BlockQuestAPI getInstance() {
        return instance == null ? instance = new BlockQuestAPI(Main.getPlugin()) : instance;
    }

    public int getFoundBlocks(Player p) {
        return plugin.blocksss.get(p.getName()).size();
    }

    public int getBlocksLeft(Player p) {
        return getAllBlocks().length - getFoundBlocks(p);
    }
    public Location[] getAllBlocks() {
        List<Location> locationList = new ArrayList<>();
        for(String s : plugin.getConfig().getStringList("blocks")) {
            String[] split = s.split(";");
            locationList.add(new Location(Bukkit.getWorld(split[3]), Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2])));
        }
        return locationList.toArray(new Location[locationList.size()]);
    }
}
