package me.robifoxx.blockquest.listener;

import me.robifoxx.blockquest.BlockQuest;
import me.robifoxx.blockquest.api.BlockQuestAPI;
import me.robifoxx.blockquest.api.BlockQuestDataStorage;
import me.robifoxx.blockquest.api.BlockQuestSeries;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class BlockFindListener implements Listener {
    private BlockQuest blockQuest;
    public BlockFindListener(BlockQuest blockQuest) {
        this.blockQuest = blockQuest;
    }

    @EventHandler
    public void interact(PlayerInteractEvent e) {
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            try {
                if(e.getHand() != EquipmentSlot.HAND) return;
            } catch(Exception ignored) { } // apparently this fixes compatibility issues for 1.8 or lower, not sure which exception to use, NoSuchMethodFound isn't allowed for some reason
            for(BlockQuestSeries bqs : BlockQuestAPI.getInstance().getSeriesList()) {
                if(bqs.getHiddenBlocks().contains(e.getClickedBlock().getLocation())) {
                    Player p = e.getPlayer();
                    Location location = e.getClickedBlock().getLocation();
                    String key = BlockQuestAPI.getInstance().getPlayerKey(p);
                    BlockQuestDataStorage bqds = BlockQuestAPI.getInstance().getDataStorage();

                    int foundBlocks = bqds.getFoundBlockCount(key, bqs.getID());
                    int allBlocks = bqs.getHiddenBlocks().size();

                    if(foundBlocks >= allBlocks) {
                        bqs.onAlreadyFoundAllBlockAction(e.getPlayer(), location);
                    } else if(bqds.hasFoundBlock(key, bqs.getID(), location)) {
                        bqs.onAlreadyFoundBlockAction(e.getPlayer(), location);
                    } else {
                        bqds.storeFoundBlock(key, bqs.getID(), location);
                        if(foundBlocks + 1 >= allBlocks) {
                            bqs.onFoundAllBlockAction(p, location);
                        } else {
                            bqs.onFindBlockAction(p, location);
                        }
                    }
                    break;
                }
            }
        }
    }
}
