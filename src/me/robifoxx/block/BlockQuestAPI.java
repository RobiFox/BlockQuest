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

    /**
     * Gets the instance for API
     * @return The instance for API
     */
    public static BlockQuestAPI getInstance() {
        return instance == null ? instance = new BlockQuestAPI(Main.getPlugin()) : instance;
    }

    /**
     * Gets the amount of how many blocks the player has found
     * @param p The target player
     * @return The amount of blocks
     */
    public int getFoundBlocks(Player p) {
        return plugin.blocksss.get(p.getName()).size();
    }

    /**
     * Gets the amount of blocks left to find
     * @param p The target plyer
     * @return The amount of blocks required to find
     */
    public int getBlocksLeft(Player p) {
        return getAllBlocks().length - getFoundBlocks(p);
    }

    /**
     * Returns an array of blocks
     * @return An array of locations of blocks
     */
    public Location[] getAllBlocks() {
        List<Location> locationList = new ArrayList<>();
        for(String s : plugin.getConfig().getStringList("blocks")) {
            String[] split = s.split(";");
            locationList.add(new Location(Bukkit.getWorld(split[3]), Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2])));
        }
        return locationList.toArray(new Location[locationList.size()]);
    }
}
