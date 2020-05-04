package me.robifoxx.blockquest;

import me.robifoxx.blockquest.api.BlockQuestAPI;
import me.robifoxx.blockquest.command.BlockQuestCommand;
import me.robifoxx.blockquest.inherits.DefaultSeries;
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

        if(!getConfig().getBoolean("api-only", false)) {
            getCommand("blockquest").setExecutor(new BlockQuestCommand(this));
            Bukkit.getPluginManager().registerEvents(new SeriesModifyListener(this), this);
        }

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

        BlockQuestAPI instance = BlockQuestAPI.getInstance();

        ConfigurationSection cs = getConfig().getConfigurationSection("series");
        if(cs != null)
            for(String id : cs.getKeys(false)) {
                //BlockQuestAPI.getInstance().registerDefaultSeries(id, this);
                registerDefaultSeries(id, instance);
            }

        new Metrics(this,1695);
    }

    public void registerDefaultSeries(String id, BlockQuestAPI instance) {
        instance.registerSeries(
                new DefaultSeries(this,
                        id,
                        getConfig().getBoolean("series." + id + ".enabled"),
                        getConfig().getStringList("series." + id + ".blocks"),
                        getConfig().getStringList("series." + id + ".find-block-commands"),
                        getConfig().getStringList("series." + id + ".all-blocks-found-commands"),
                        getConfig().getStringList("series." + id + ".already-found-commands"),
                        getConfig().getStringList("series." + id + ".already-found-all-blocks")
                ));
    }
}
