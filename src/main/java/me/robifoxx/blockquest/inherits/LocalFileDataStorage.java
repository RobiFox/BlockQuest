package me.robifoxx.blockquest.inherits;

import me.robifoxx.blockquest.BlockQuest;
import me.robifoxx.blockquest.Config;
import me.robifoxx.blockquest.api.BlockQuestAPI;
import me.robifoxx.blockquest.api.BlockQuestDataStorage;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class LocalFileDataStorage extends BlockQuestDataStorage {
    private Config data;
    public LocalFileDataStorage(BlockQuest plugin) {
        Config c = new Config("plugins/" + plugin.getDescription().getName(), "data.yml", plugin);
        c.create();
        c.setDefault("data.yml");
        if(!c.exists()) {
            c.getConfig().options().copyDefaults(true);
            c.saveConfig();
        }
        data = c;
    }

    @Override
    public void storeFoundBlock(String key, String series, Location location) {
        List<String> locations = data.getConfig().getStringList("data." + key + "." + series);
        locations.add(BlockQuestAPI.getInstance().locationToString(location));
        data.getConfig().set("data." + key + "." + series, locations);
        data.saveConfig();
    }

    @Override
    public boolean hasFoundBlock(String key, String series, Location location) {
        return data.getConfig().getStringList("data." + key + "." + series).contains(BlockQuestAPI.getInstance().locationToString(location));
    }

    @Override
    public int getFoundBlockCount(String key, String series) {
        return data.getConfig().getStringList("data." + key + "." + series).size();
    }

    @Override
    public List<String> getAllUsers(String series) {
        List<String> list = new ArrayList<>();
        ConfigurationSection cs = data.getConfig().getConfigurationSection("data");
        if(cs != null)
            list.addAll(cs.getKeys(false));
        return list;
    }

    @Override
    public void clearStats(String key, String series) {
        data.getConfig().set("data." + key + "." + series, null);
        data.saveConfig();
    }
}
