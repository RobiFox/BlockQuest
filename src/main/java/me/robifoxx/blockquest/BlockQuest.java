package me.robifoxx.blockquest;

import me.robifoxx.blockquest.api.BlockQuestAPI;
import me.robifoxx.blockquest.api.BlockQuestDataStorage;
import me.robifoxx.blockquest.api.FindEffect;
import me.robifoxx.blockquest.command.BlockQuestBaseCommand;
import me.robifoxx.blockquest.inherits.CacheStorage;
import me.robifoxx.blockquest.inherits.DefaultSeries;
import me.robifoxx.blockquest.inherits.LocalFileDataStorage;
import me.robifoxx.blockquest.listener.BlockFindListener;
import me.robifoxx.blockquest.listener.CacheListener;
import me.robifoxx.blockquest.listener.SeriesModifyListener;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

public class BlockQuest extends JavaPlugin {
    public HashMap<String, String> playersInEdit;

    private Constructor<?> packetPlayOutWorldParticles;
    private Method enumParticleValueOf;
    private Method getHandle;
    private Field playerConnection;
    private Method sendPacket;

    public void onEnable() {
        try {// 1.9+
            Class.forName("org.bukkit.Particle");
        } catch (Exception e) {//1.8.8
            try {
                String nms = "net.minecraft.server.v1_8_R3.";
                Class<?> enumParticle = Class.forName(nms + "EnumParticle");
                enumParticleValueOf = enumParticle.getMethod("valueOf",String.class);
                packetPlayOutWorldParticles = Class.forName(nms+"PacketPlayOutWorldParticles")
                        .getConstructor(enumParticle, boolean.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class, int[].class);
                Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer");
                getHandle = craftPlayer.getDeclaredMethod("getHandle");
                Class<?> entityPlayer = Class.forName(nms + "EntityPlayer");
                playerConnection = entityPlayer.getDeclaredField("playerConnection");
                sendPacket = Class.forName(nms + "PlayerConnection").getDeclaredMethod("sendPacket",
                        Class.forName(nms + "Packet"));
            } catch (Exception ignored) {}
        }

        String fileName = this.getDescription().getName();
        if(!(new File("plugins/" + fileName + "/config.yml").exists())) {
            getConfig().options().copyDefaults(true);
            saveConfig();
        }

        playersInEdit = new HashMap<>();
        BlockQuestAPI.getInstance().useUuid = getConfig().getBoolean("use-uuid");
        BlockQuestAPI.getInstance().cache = getConfig().getBoolean("cache.enabled", false);

        BlockQuestAPI.getInstance().setDataStorage(new LocalFileDataStorage(this));

        if(BlockQuestAPI.getInstance().cache) {
            int interval = getConfig().getInt("cache.autosave-interval", 600) * 20;
            CacheStorage cacheStorage = (CacheStorage) BlockQuestAPI.getInstance().getDataStorage();
            Bukkit.getPluginManager().registerEvents(new CacheListener(cacheStorage), this);
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                    long begin = System.currentTimeMillis();
                    getLogger().info("Saving BlockQuest cache...");
                    cacheStorage.save();
                    getLogger().info("Saving done.");
                    getLogger().info("Took " + (System.currentTimeMillis() - begin) + "ms.");
                });
            }, interval, interval);
        }

        if(!getConfig().getBoolean("api-only", false)) {
            getCommand("blockquest").setExecutor(new BlockQuestBaseCommand(this));
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

    public void onDisable() {
        BlockQuestDataStorage dataStorage = BlockQuestAPI.getInstance().getDataStorage();
        if(dataStorage instanceof CacheStorage) {
            ((CacheStorage) dataStorage).save();
        }
    }

    public void registerDefaultSeries(String id, BlockQuestAPI instance) {
        FindEffect findEffect;
        if(getConfig().getBoolean("series." + id + ".find-effect.enabled", false)) {
            FindEffect.ParticleData particleData;
            if(getConfig().getBoolean("series." + id + ".find-effect.particle.enabled", false)) {
                particleData = new FindEffect.ParticleData(
                        getParticleEffect(getConfig().getString("series." + id + ".find-effect.particle.particle", "FLAME")),
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

    private Object getParticleEffect(String particle) {
        try {
            Class.forName("org.bukkit.Particle");
            return Particle.valueOf(particle);
        } catch (Exception e) {
            try {
                return enumParticleValueOf.invoke(null,particle);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    public void spawnParticle(Location location, FindEffect.ParticleData data) {
        World world = location.getWorld();
        if (world == null) return;
        try {//1.9+
            Class.forName("org.bukkit.Particle");
            world.spawnParticle((Particle) data.getParticle(),
                    location.add(data.getOffX(), data.getOffY(), data.getOffZ()),
                    data.getAmount(),
                    data.getDx(), data.getDy(), data.getDz(),
                    data.getSpeed());
        } catch (Exception e) {//1.8.8
            try {
                Object packet = packetPlayOutWorldParticles.newInstance(data.getParticle(),true,
                        (float) data.getOffX(), (float) data.getOffY(), (float) data.getOffZ(),
                        (float) data.getDx(), (float) data.getDy(), (float) data.getDz(),
                        (float) data.getSpeed(),
                        data.getAmount());
                for (Player p : world.getPlayers()) sendPacket.invoke(playerConnection.get(getHandle.invoke(p)),packet);
            } catch (Exception ignored) {}
        }
    }
}
