package me.robifoxx.blockquest.listener;

import me.robifoxx.blockquest.api.BlockQuestAPI;
import me.robifoxx.blockquest.api.BlockQuestSeries;
import me.robifoxx.blockquest.inherits.CacheStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CacheListener implements Listener {
    private CacheStorage cacheStorage;

    public CacheListener(CacheStorage cacheStorage) {
        this.cacheStorage = cacheStorage;
    }

    @EventHandler
    public void leave(PlayerJoinEvent e) {
        String key = BlockQuestAPI.getInstance().getPlayerKey(e.getPlayer());
        for(BlockQuestSeries series : BlockQuestAPI.getInstance().getSeriesList()) {
            cacheStorage.createCacheForSeriesIfAbsent(key, series.getID());
        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        String key = BlockQuestAPI.getInstance().getPlayerKey(e.getPlayer());
        cacheStorage.save(key);
        cacheStorage.removeFromCache(key);
    }
}
