package me.robifoxx.blockquest.inherits;

import me.robifoxx.blockquest.BlockQuest;
import me.robifoxx.blockquest.api.BlockQuestAPI;
import me.robifoxx.blockquest.api.BlockQuestDataStorage;
import me.robifoxx.blockquest.api.BlockQuestSeries;
import me.robifoxx.blockquest.api.FindEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the class that's used when the plugin
 * itself registers the series. You can use BlockQuestSeries
 * if you want a custom series.
 */
public class DefaultSeries extends BlockQuestSeries {
    private final BlockQuest blockQuest;

    private final String id;
    private boolean enabled;
    private final List<String> findBlockCommands;
    private final List<String> foundAllBlockCommands;
    private final List<String> alreadyFoundBlockCommands;
    private final List<String> alreadyFoundAllBlockCommands;
    private final List<Location> blocks;

    private final FindEffect findEffect;

    private int particleFoundTaskId;
    private int particleNotFoundTaskId;
    public DefaultSeries(BlockQuest blockQuest, String id, boolean enabled, List<String> blocks, List<String> findBlockCommands, List<String> foundAllBlockCommands, List<String> alreadyFoundBlockCommands, List<String> alreadyFoundAllBlockCommands, FindEffect findEffect) {
        this.blockQuest = blockQuest;

        this.id = id;
        this.findBlockCommands = findBlockCommands;
        this.foundAllBlockCommands = foundAllBlockCommands;
        this.alreadyFoundBlockCommands = alreadyFoundBlockCommands;
        this.alreadyFoundAllBlockCommands = alreadyFoundAllBlockCommands;

        List<Location> blockList = new ArrayList<>();
        for(String s : blocks) {
            String[] split = s.split(";");
            int x = Integer.parseInt(split[0]);
            int y = Integer.parseInt(split[1]);
            int z = Integer.parseInt(split[2]);
            if (split.length > 3) {
                blockList.add(new Location(Bukkit.getWorld(split[3]), x, y, z));
            } else {
                blockList.add(new Location(null, x, y, z));
            }
        }
        this.blocks = blockList;
        this.findEffect = findEffect;

        if(enabled) setEnabled(true);
    }

    @Override
    public void onUnregister() {
        setEnabled(false);
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if(enabled) {
            BlockQuestAPI instance = BlockQuestAPI.getInstance();
            BlockQuestDataStorage dataStorage = BlockQuestAPI.getInstance().getDataStorage();

            if(blockQuest.getConfig().getBoolean("series." + id + ".particles.found.enabled", false)) {
                String type = blockQuest.getConfig().getString("series." + id + ".particles.found.type");
                int count = blockQuest.getConfig().getInt("series." + id + ".particles.found.count");
                double xd = blockQuest.getConfig().getDouble("series." + id + ".particles.found.xd");
                double yd = blockQuest.getConfig().getDouble("series." + id + ".particles.found.yd");
                double zd = blockQuest.getConfig().getDouble("series." + id + ".particles.found.zd");
                double speed = blockQuest.getConfig().getDouble("series." + id + ".particles.found.speed");
                int repeat = blockQuest.getConfig().getInt("series." + id + ".particles.found.repeat");
                particleFoundTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(blockQuest, () -> {
                    for(Player pl : Bukkit.getOnlinePlayers()) {
                        for(Location loc : getHiddenBlocks()) {
                            if(loc.getWorld().getName().equalsIgnoreCase(pl.getWorld().getName()))
                                if (dataStorage.hasFoundBlock(instance.getPlayerKey(pl), getID(), loc))
                                    blockQuest.spawnParticle(pl,blockQuest.getParticleEffect(type),loc.getX() + 0.5d, loc.getY() + 0.5d, loc.getZ() + 0.5d, count, xd, yd, zd, speed);
                        }
                    }
                }, repeat, repeat);
            }
            if(blockQuest.getConfig().getBoolean("series." + id + ".particles.notfound.enabled", false)) {
                String type = blockQuest.getConfig().getString("series." + id + ".particles.notfound.type");
                int count = blockQuest.getConfig().getInt("series." + id + ".particles.notfound.count");
                double xd = blockQuest.getConfig().getDouble("series." + id + ".particles.notfound.xd");
                double yd = blockQuest.getConfig().getDouble("series." + id + ".particles.notfound.yd");
                double zd = blockQuest.getConfig().getDouble("series." + id + ".particles.notfound.zd");
                double speed = blockQuest.getConfig().getDouble("series." + id + ".particles.notfound.speed");
                int repeat = blockQuest.getConfig().getInt("series." + id + ".particles.notfound.repeat");
                particleNotFoundTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(blockQuest, () -> {
                    for(Player pl : Bukkit.getOnlinePlayers()) {
                        for(Location loc : getHiddenBlocks()) {
                            if(loc.getWorld().getName().equalsIgnoreCase(pl.getWorld().getName()))
                                if (!dataStorage.hasFoundBlock(instance.getPlayerKey(pl), getID(), loc))
                                    blockQuest.spawnParticle(pl,blockQuest.getParticleEffect(type),loc.getX() + 0.5d, loc.getY() + 0.5d, loc.getZ() + 0.5d, count, xd, yd, zd, speed);
                        }
                    }
                }, repeat, repeat);
            }
        } else {
            Bukkit.getScheduler().cancelTask(particleFoundTaskId);
            Bukkit.getScheduler().cancelTask(particleNotFoundTaskId);
        }
        blockQuest.getConfig().set("series." + id + ".enabled", enabled);
        blockQuest.saveConfig();
    }

    @Override
    public void onFindBlockAction(Player p, Location blockLocation) {
        for(String s : findBlockCommands) {
            runCommand(s, p, blockLocation);
        }
        playFindEffect(p, blockLocation);
    }

    @Override
    public void onFoundAllBlockAction(Player p, Location blockLocation) {
        for(String s : foundAllBlockCommands) {
            runCommand(s, p, blockLocation);
        }
        playFindEffect(p, blockLocation);
    }

    @Override
    public void onAlreadyFoundBlockAction(Player p, Location blockLocation) {
        for(String s : alreadyFoundBlockCommands) {
            runCommand(s, p, blockLocation);
        }
    }

    @Override
    public void onAlreadyFoundAllBlockAction(Player p, Location blockLocation) {
        for(String s : alreadyFoundAllBlockCommands) {
            runCommand(s, p, blockLocation);
        }
    }

    @Override
    public List<Location> getHiddenBlocks() {
        return blocks;
    }

    @Override
    public void addHiddenBlock(Location location) {
        List<String> locations = new ArrayList<>(blockQuest.getConfig().getStringList("series." + id + ".blocks"));
        String convertedLocation = BlockQuestAPI.getInstance().locationToString(location);
        if(locations.contains(convertedLocation)) return;
        locations.add(convertedLocation);
        blockQuest.getConfig().set("series." + id + ".blocks", locations);
        blockQuest.saveConfig();

        blocks.add(location);
    }

    @Override
    public void removeHiddenBlock(Location location) {
        List<String> locations = new ArrayList<>(blockQuest.getConfig().getStringList("series." + id + ".blocks"));
        String convertedLocation = BlockQuestAPI.getInstance().locationToString(location);
        if(!locations.contains(convertedLocation)) return;
        locations.remove(convertedLocation);
        blockQuest.getConfig().set("series." + id + ".blocks", locations);
        blockQuest.saveConfig();

        blocks.remove(location);
    }

    private void runCommand(String s, Player player, Location location) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", player.getName())
                .replace("%pLocX%", "" + player.getLocation().getX())
                .replace("%pLocY%", "" + player.getLocation().getY())
                .replace("%pLocZ%", "" + player.getLocation().getZ())
                .replace("%locX5%", "" + (location.getX() + 0.5))
                .replace("%locY5%", "" + (location.getY() + 0.5))
                .replace("%locZ5%", "" + (location.getZ() + 0.5))
                .replace("%locX%", "" + location.getX())
                .replace("%locY%", "" + location.getY())
                .replace("%locZ%", "" + location.getZ())
                .replace("%blocksLeft%", "" + (getHiddenBlocks().size() - BlockQuestAPI.getInstance().getDataStorage().getFoundBlockCount(BlockQuestAPI.getInstance().getPlayerKey(player), getID())))
        );
    }

    private void playFindEffect(Player finder, Location blockLocation) {
        // TODO REMOVE
        /*new FindEffect(blockQuest,
                new ItemStack[] {new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.SLIME_BLOCK)},
                false,
                new FindEffect.ParticleData(Particle.FLAME, 2, 0, 1, 0, 0.1, 0.1, 0.1, 0, 0),
                new FindEffect.MovementData(60, 0.075, 10, 0),
                null, null).create(finder, blockLocation);*/
        if(findEffect != null) findEffect.create(finder, blockLocation);
    }
}
