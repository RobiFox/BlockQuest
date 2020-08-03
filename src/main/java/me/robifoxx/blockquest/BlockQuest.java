package me.robifoxx.blockquest;

import me.robifoxx.blockquest.api.BlockQuestAPI;
import me.robifoxx.blockquest.api.FindEffect;
import me.robifoxx.blockquest.command.BlockQuestCommand;
import me.robifoxx.blockquest.inherits.DefaultSeries;
import me.robifoxx.blockquest.inherits.LocalFileDataStorage;
import me.robifoxx.blockquest.listener.BlockFindListener;
import me.robifoxx.blockquest.listener.SeriesModifyListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        FindEffect findEffect;
        if(getConfig().getBoolean("series." + id + ".find-effect.enabled", false)) {
            FindEffect.ParticleData particleData;
            if(getConfig().getBoolean("series." + id + ".find-effect.particle.enabled", false)) {
                particleData = new FindEffect.ParticleData(
                        Particle.valueOf(getConfig().getString("series." + id + ".find-effect.particle.particle", "FLAME")),
                        getConfig().getInt("series." + id + ".find-effect.particle.amount", 1),
                        getConfig().getDouble("series." + id + ".find-effect.particle.offset.x", 0),
                        getConfig().getDouble("series." + id + ".find-effect.particle.offset.y", 1),
                        getConfig().getDouble("series." + id + ".find-effect.particle.offset.z", 0),
                        getConfig().getDouble("series." + id + ".find-effect.particle.delta.x", 0),
                        getConfig().getDouble("series." + id + ".find-effect.particle.delta.y", 0),
                        getConfig().getDouble("series." + id + ".find-effect.particle.delta.z", 0),
                        getConfig().getDouble("series." + id + ".find-effect.particle.speed", 0),
                        getConfig().getInt("series." + id + ".find-effect.particle.delay", 1)
                );
            } else {
                particleData = null;
            }
            findEffect = new FindEffect(
                    this,
                    new ItemStack[] {
                            new ItemStack(Material.valueOf(getConfig().getString("series." + id + ".find-effect.equipment.boot", "AIR"))),
                            new ItemStack(Material.valueOf(getConfig().getString("series." + id + ".find-effect.equipment.leg", "AIR"))),
                            new ItemStack(Material.valueOf(getConfig().getString("series." + id + ".find-effect.equipment.chest", "AIR"))),
                            new ItemStack(Material.valueOf(getConfig().getString("series." + id + ".find-effect.equipment.head", "DIRT")))
                    },
                    getConfig().getBoolean("series." + id + ".find-effect.small", false),
                    particleData,
                    new FindEffect.MovementData(
                            getConfig().getInt("series." + id + ".find-effect.movement.lifetime", 40),
                            getConfig().getDouble("series." + id + ".find-effect.movement.float-per-tick", 0.075),
                            (float) getConfig().getDouble("series." + id + ".find-effect.movement.rotate-per-tick", 10),
                            getConfig().getDouble("series." + id + ".find-effect.movement.initial-offset", 0)
                    ),
                    getDefaultFindEvent(getConfig().getStringList("series." + id + ".find-effect.commands.begin")),
                    getDefaultFindEvent(getConfig().getStringList("series." + id + ".find-effect.commands.end"))
            );
        } else {
            findEffect = null;
        }
        instance.registerSeries(
                new DefaultSeries(this,
                        id,
                        getConfig().getBoolean("series." + id + ".enabled"),
                        getConfig().getStringList("series." + id + ".blocks"),
                        getConfig().getStringList("series." + id + ".find-block-commands"),
                        getConfig().getStringList("series." + id + ".all-blocks-found-commands"),
                        getConfig().getStringList("series." + id + ".already-found-commands"),
                        getConfig().getStringList("series." + id + ".already-found-all-blocks"),
                        findEffect
                ));
    }

    private FindEffect.Event getDefaultFindEvent(List<String> commands) {
        return (finder, findEffectLocation, blockLocation) -> {
            for(String s : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s
                        .replace("%x%", "" + findEffectLocation.getLocation().getX())
                        .replace("%y%", "" + findEffectLocation.getLocation().getY())
                        .replace("%z%", "" + findEffectLocation.getLocation().getZ())
                        .replace("%player%", finder.getName())
                );
            }
        };
    }
}
