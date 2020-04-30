package me.robifoxx.blockquest.inherits;

import me.robifoxx.blockquest.BlockQuest;
import me.robifoxx.blockquest.api.BlockQuestAPI;
import me.robifoxx.blockquest.api.BlockQuestDataStorage;
import me.robifoxx.blockquest.api.BlockQuestSeries;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the class that's used when the plugin
 * itself registers the series. You can use BlockQuestSeries
 * if you want a custom series.
 */
public class DefaultSeries extends BlockQuestSeries {
    private BlockQuest blockQuest;

    private String id;
    private boolean enabled;
    private List<String> findBlockCommands;
    private List<String> foundAllBlockCommands;
    private List<String> alreadyFoundBlockCommands;
    private List<String> alreadyFoundAllBlockCommands;
    private List<Location> blocks;

    private int particleFoundTaskId;
    private int particleNotFoundTaskId;
    public DefaultSeries(BlockQuest blockQuest, String id, boolean enabled, List<String> blocks, List<String> findBlockCommands, List<String> foundAllBlockCommands, List<String> alreadyFoundBlockCommands, List<String> alreadyFoundAllBlockCommands) {
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
            String world = split[3];
            blockList.add(new Location(Bukkit.getWorld(world), x, y, z));
        }
        this.blocks = blockList;

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
                            if(dataStorage.hasFoundBlock(instance.getPlayerKey(pl), getID(), loc)) {
                                pl.spawnParticle(Particle.valueOf(type), loc.getX() + 0.5d, loc.getY() + 0.5d, loc.getZ() + 0.5d, count, xd, yd, zd, speed);
                            }
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
                            if(!dataStorage.hasFoundBlock(instance.getPlayerKey(pl), getID(), loc)) {
                                pl.spawnParticle(Particle.valueOf(type), loc.getX() + 0.5d, loc.getY() + 0.5d, loc.getZ() + 0.5d, count, xd, yd, zd, speed);
                            }
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
    }

    @Override
    public void onFoundAllBlockAction(Player p, Location blockLocation) {
        for(String s : foundAllBlockCommands) {
            runCommand(s, p, blockLocation);
        }
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
}
