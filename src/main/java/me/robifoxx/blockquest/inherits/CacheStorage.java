package me.robifoxx.blockquest.inherits;

import me.robifoxx.blockquest.api.BlockQuestAPI;
import me.robifoxx.blockquest.api.BlockQuestDataStorage;
import me.robifoxx.blockquest.api.BlockQuestSeries;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This is the Cache Storage.
 * If cache is enabled, this storage will be forced,
 * however, it is still possible to use your own
 * data storage, as this class keeps track of the registered
 * storage.
 */
public class CacheStorage extends BlockQuestDataStorage {
    private HashMap<String, CachedPlayer> cache;
    private BlockQuestDataStorage original;

    public CacheStorage(BlockQuestDataStorage original) {
        cache = new HashMap<>();
        this.original = original;
    }

    @Override
    public void storeFoundBlock(String key, String series, Location location) {
        createCacheForSeriesIfAbsent(key, series);
        cache.get(key).foundBlocks.get(series).add(location);
    }

    @Override
    public void setFoundBlocks(String key, String series, List<Location> locations) {
        createCacheForSeriesIfAbsent(key, series);
        cache.get(key).foundBlocks.get(series).clear();
        cache.get(key).foundBlocks.get(series).addAll(locations);
    }

    @Override
    public boolean hasFoundBlock(String key, String series, Location location) {
        createCacheForSeriesIfAbsent(key, series);
        return cache.get(key).foundBlocks.get(series).contains(location);
    }

    @Override
    public int getFoundBlockCount(String key, String series) {
        createCacheForSeriesIfAbsent(key, series);
        return cache.get(key).foundBlocks.get(series).size();
    }

    @Override
    public void clearStats(String key, String series) {
        createCacheForSeriesIfAbsent(key, series);
        cache.get(key).foundBlocks.get(series).clear();
    }

    @Override
    public List<String> getAllUsers(String series) {
        return original.getAllUsers(series);
    }

    public void save() {
        for(String key : cache.keySet()) {
            save(key);
        }
    }

    public void save(String key) {
        for(String series: cache.get(key).foundBlocks.keySet()) {
            original.setFoundBlocks(key, series, cache.get(key).foundBlocks.get(series));
        }
    }

    public void removeFromCache(String key) {
        cache.remove(key);
    }

    public void createCacheForSeriesIfAbsent(String key, String seriesName) {
        cache.putIfAbsent(key, new CachedPlayer());
        CachedPlayer cached = cache.get(key);
        if(cached.foundBlocks.get(seriesName) == null) {
            cached.foundBlocks.put(seriesName, new ArrayList<>());
            BlockQuestSeries series = BlockQuestAPI.getInstance().getSeries(seriesName);
            for(Location loc : series.getHiddenBlocks()) {
                if(original.hasFoundBlock(key, seriesName, loc)) {
                    cached.foundBlocks.get(seriesName).add(loc);
                }
            }
        }
    }

    private static class CachedPlayer {
        public HashMap<String, List<Location>> foundBlocks; // series - location pair
        public CachedPlayer() {
            foundBlocks = new HashMap<>();
        }
    }
}
