package me.robifoxx.blockquest.api;

import me.robifoxx.blockquest.BlockQuest;
import me.robifoxx.blockquest.inherits.DefaultSeries;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockQuestAPI {
    private static BlockQuestAPI instance;
    
    public static BlockQuestAPI getInstance() {
        return instance == null ? (instance = new BlockQuestAPI()) : instance;
    }

    private BlockQuestAPI() {
        series = new HashMap<>();
    }

    // i might need to seperate an inactive and active series hashmap
    private HashMap<String, BlockQuestSeries> series;
    private BlockQuestDataStorage dataStorage;

    public boolean useUuid;

    /**
     * Converts Location to String
     * @param loc The Location that needs converting
     * @return Location converted to String
     */
    public String locationToString(Location loc) {
        return loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ() + ";" + loc.getWorld().getName();
    }

    /**
     * Registers a series
     * If a series is inactive, it isn't registered
     * You can register a series any time you want to enable them
     * @param blockQuestSeries The series that you want to register
     */
    public void registerSeries(BlockQuestSeries blockQuestSeries) {
        if(isRegistered(blockQuestSeries.getID())) {
            throw new RuntimeException("BlockQuest Series " + blockQuestSeries.getID() + " is already registered!");
        }
        series.put(blockQuestSeries.getID(), blockQuestSeries);
        Bukkit.getLogger().info("Registered series " + blockQuestSeries.getID());
    }

    /**
     * Unregisters series. Use this if you want
     * to disable a series
     * @param id The id of the series that needs to be unregistered
     */
    public void unregisterSeries(String id) {
        getSeries(id).onUnregister();
        series.remove(id);
        Bukkit.getLogger().info("Unregistered series " + id);
    }

    /**
     * Checks if a series is registered
     * @param id This is the id of the series
     * @return Returns whether the series is registered or not
     */
    public boolean isRegistered(String id) {
        return series.containsKey(id);
    }

    /**
     * Returns a BlockQuestSeries with the specified id
     * @param id The id of the series
     * @return The BlockQuestSeries object
     */
    public BlockQuestSeries getSeries(String id) {
        return series.get(id);
    }

    /**
     * Get a list of registered series
     * @return List of registered series
     */
    public List<BlockQuestSeries> getSeriesList() {
        return new ArrayList<>(series.values());
    }

    /**
     * Set the data storage.
     * By default it is config, but you can
     * create your own
     * @param dataStorage The data storage type
     */
    public void setDataStorage(BlockQuestDataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Gets the current data storage.
     * @return Current data storage
     */
    public BlockQuestDataStorage getDataStorage() {
        return dataStorage;
    }

    /**
     * Returns the key, that is used in data storage.
     * @param p The target player
     * @return Either the Player's UUID, or their name, depending on the use-uuid value in config.yml
     */
    public String getPlayerKey(OfflinePlayer p) {
        return useUuid ? p.getUniqueId().toString() : p.getName();
    }

    /**
     * Gets the amount of blocks, with only an id reference
     * @param blockQuest The instance of BlockQuest's main class
     * @param series The id of the series
     * @return The amount of hidden blocks
     */
    public int getBlockCount(BlockQuest blockQuest, String series) {
        return blockQuest.getConfig().getStringList("series." + series + ".blocks").size();
    }
}
