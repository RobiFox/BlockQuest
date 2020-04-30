package me.robifoxx.blockquest.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * A BlockQuest Series consists of
 * hidden blocks.
 */
public abstract class BlockQuestSeries {
    /**
     * The ID of the Block Quest Series.
     * @return Return
     */
    public abstract String getID();

    /**
     * If the series is enabled
     * @return if the series is enabled
     */
    public abstract boolean isEnabled();

    /**
     * Enables or disables the series
     * @param enabled if it's enabled or not
     */
    public abstract void setEnabled(boolean enabled);

    /**
     * Action that happens, when a player finds a block.
     * @param p The player, that clicked the block.
     * @param blockLocation The location of the block
     */
    public abstract void onFindBlockAction(Player p, Location blockLocation);

    /**
     * Action that happens, when a player finds the last block of the series.
     * @param p The player, that clicked the block.
     * @param blockLocation The location of the block
     */
    public abstract void onFoundAllBlockAction(Player p, Location blockLocation);

    /**
     * Action that happens, when a player clicks a block that the player has already found.
     * @param p The player, that clicked the block.
     * @param blockLocation The location of the block
     */
    public abstract void onAlreadyFoundBlockAction(Player p, Location blockLocation);

    /**
     * Action that happens, when a right clicks a block in a series, when he already found all blocks.
     * @param p The player, that clicked the block.
     * @param blockLocation The location of the block
     */
    public abstract void onAlreadyFoundAllBlockAction(Player p, Location blockLocation);

    /**
     * This is the list of the blocks that needs to be found
     * @return List of block's locations
     */
    public abstract List<Location> getHiddenBlocks();

    /**
     * An optional method, that runs when the series is unregistered
     */
    public void onUnregister() {

    }
}
