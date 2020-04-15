package me.robifoxx.blockquest;

import me.robifoxx.blockquest.api.BlockQuestAPI;
import me.robifoxx.blockquest.command.BlockQuestCommand;
import me.robifoxx.blockquest.inherits.LocalFileDataStorage;
import me.robifoxx.blockquest.listener.BlockFindListener;
import me.robifoxx.blockquest.listener.SeriesModifyListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;

public class BlockQuest extends JavaPlugin {
    public HashMap<String, String> playersInEdit;

    public void onEnable() {
        String fileName = this.getDescription().getName();
        if(!(new File("plugins/" + fileName + "/config.yml").exists())) {
            getConfig().options().copyDefaults(true);
            saveConfig();
        }

        playersInEdit = new HashMap<>();
        BlockQuestAPI.getInstance().useUuid = getConfig().getBoolean("use-uuid");

        BlockQuestAPI.getInstance().setDataStorage(new LocalFileDataStorage(this));

        getCommand("blockquest").setExecutor(new BlockQuestCommand(this));

        Bukkit.getPluginManager().registerEvents(new SeriesModifyListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockFindListener(this), this);

        if(getConfig().getBoolean("placeholderapi")) {
            if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                new BlockQuestPlaceholders(this).register();
            } else {
                getLogger().warning("PlaceholderAPI not found, placeholders will not work.");
                getLogger().warning("Please install the following plugin:");
                getLogger().warning("https://www.spigotmc.org/resources/p.6245/");
            }
        }

        ConfigurationSection cs = getConfig().getConfigurationSection("series");
        if(cs != null)
            for(String id : cs.getKeys(false)) {
                if(getConfig().getBoolean("series." + id + ".enabled")) {
                    BlockQuestAPI.getInstance().registerDefaultSeries(id, this);
                }
            }

        new Metrics(this,1695);
    }
}
