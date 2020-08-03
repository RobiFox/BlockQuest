package me.robifoxx.blockquest.inherits;

import me.robifoxx.blockquest.api.BlockQuestAPI;
import me.robifoxx.blockquest.api.BlockQuestDataStorage;
import me.robifoxx.blockquest.api.BlockQuestSeries;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        cache.get(key).foundBlocks.remove(series);
    }

    @Override
    public List<String> getAllUsers(String series) {
        return original.getAllUsers(series);
    }

    public void save(String key, String series) {
        for(Location loc : cache.get(key).foundBlocks.get(series)) {
            if(!original.hasFoundBlock(key, series, loc)) {
                original.storeFoundBlock(key, series, loc);
            }
        }
    }

    private void createCacheForSeriesIfAbsent(String key, String seriesName) {
        cache.putIfAbsent(key, new CachedPlayer());
        CachedPlayer cached = cache.get(key);
        if(cached.foundBlocks.get(seriesName) == null) {
            cached.foundBlocks.put(seriesName, new ArrayList<>());
            BlockQuestSeries series = BlockQuestAPI.getInstance().getSeries(seriesName);
            BlockQuestDataStorage dataStorage = BlockQuestAPI.getInstance().getDataStorage();
            for(Location loc : series.getHiddenBlocks()) {
                if(dataStorage.hasFoundBlock(key, seriesName, loc)) {
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
