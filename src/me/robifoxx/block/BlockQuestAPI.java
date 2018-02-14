package me.robifoxx.block;

import me.robifoxx.block.api.FindEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BlockQuestAPI {
    private static BlockQuestAPI instance;
    private Main plugin;
    private BlockQuestAPI(Main plugin) {
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

    /**
     * Adds a block to the specified location
     * @param l The location where the new block will be
     * @return If the task was successful
     */
    public boolean addLocation(Location l) {
        List<String> blocks = plugin.getConfig().getStringList("blocks");
        if(locationExists(l)) {
            return false;
        } else {
            blocks.add(convertLocToString(l));
            plugin.getConfig().set("blocks", blocks);
            plugin.saveConfig();
            return true;
        }
    }

    /**
     * Removes a block from a specified location
     * @param l The location where the block needs to be removed
     * @return If the task was successful
     */
    public boolean removeLocation(Location l) {
        List<String> blocks = plugin.getConfig().getStringList("blocks");
        if(!locationExists(l)) {
            return false;
        } else {
            blocks.remove(convertLocToString(l));
            plugin.getConfig().set("blocks", blocks);
            plugin.saveConfig();
            return true;
        }
    }

    /**
     * Checks if the specified location already exists
     * @param l The location to check
     * @return Returns a boolean if there's a BlockQuest block there
     */
    public boolean locationExists(Location l) {
        return plugin.getConfig().getStringList("blocks").contains(convertLocToString(l));
    }

    /**
     * Convert Location to a String, the same way they are stored in config.yml
     * @param l The location to be converted
     * @return The converted location
     */
    public String convertLocToString(Location l) {
        return l.getBlockX() + ";" + l.getBlockY() + ";" + l.getBlockZ() + ";" + l.getWorld().getName();
    }

    /**
     * Gets the current Find Effect
     * @return The Find Effect specified in config file
     */
    public FindEffect getFindEffect() {
        return plugin.findEffectC;
    }

    /**
     * Replaces the Find Effect
     * @param ef The new effect
     */
    public void setFindEffect(FindEffect ef) {
        plugin.findEffectC = ef;
    }
}
