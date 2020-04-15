package me.robifoxx.blockquest.listener;

import me.robifoxx.blockquest.BlockQuest;
import me.robifoxx.blockquest.api.BlockQuestAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class SeriesModifyListener implements Listener {
    private BlockQuest blockQuest;
    public SeriesModifyListener(BlockQuest blockQuest) {
        this.blockQuest = blockQuest;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        String id = blockQuest.playersInEdit.get(p.getName());
        if(id == null) return;
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Location loc = e.getClickedBlock().getLocation();
            List<String> locations = new ArrayList<>(blockQuest.getConfig().getStringList("series." + id + ".blocks"));
            String convertedLocation = BlockQuestAPI.getInstance().locationToString(loc);
            if(locations.contains(convertedLocation)) {
                locations.remove(convertedLocation);
                p.sendMessage("§cDeleted Hidden Block for series " + id + " at " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ", " + loc.getWorld().getName());
            } else {
                locations.add(convertedLocation);
                p.sendMessage("§aAdded Hidden Block for series " + id + " at " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ", " + loc.getWorld().getName());
            }
            blockQuest.getConfig().set("series." + id + ".blocks", locations);
            blockQuest.saveConfig();
            p.sendMessage("§aExited edit mode.");
            blockQuest.playersInEdit.remove(p.getName());
        } else if(e.getAction() == Action.LEFT_CLICK_AIR) {
            p.sendMessage("§aExited edit mode.");
            blockQuest.playersInEdit.remove(p.getName());
        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        blockQuest.playersInEdit.remove(e.getPlayer().getName());
    }
}
