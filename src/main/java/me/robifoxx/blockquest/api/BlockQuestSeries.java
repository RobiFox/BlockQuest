package me.robifoxx.blockquest.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;

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
     * Whether the Series is enabled or not
     * @return Return
     */
    public abstract boolean isEnabled();

    /**
     * Action that happens, when a player finds a block.
     * @param p The player, that clicked the block.
     * @param blockLocation The location of the block
     */
    public abstract void getFindBlockAction(Player p, Location blockLocation);

    /**
     * Action that happens, when a player finds the last block of the series.
     * @param p The player, that clicked the block.
     * @param blockLocation The location of the block
     */
    public abstract void getFoundAllBlockAction(Player p, Location blockLocation);

    /**
     * Action that happens, when a player clicks a block that the player has already found.
     * @param p The player, that clicked the block.
     * @param blockLocation The location of the block
     */
    public abstract void getAlreadyFoundBlockAction(Player p, Location blockLocation);

    /**
     * Action that happens, when a right clicks a block in a series, when he already found all blocks.
     * @param p The player, that clicked the block.
     * @param blockLocation The location of the block
     */
    public abstract void getAlreadyFoundAllBlockAction(Player p, Location blockLocation);

    /**
     * Action, that plays for the player every {@link #getRepeatTime()} ticks for blocks, that are not found in the series.
     * @param p The player, that hasn't found the block.
     * @param blockLocation The location of the block
     */
    public abstract void playNotFoundEffect(Player p, Location blockLocation);

    /**
     * Action, that plays for the player every {@link #getRepeatTime()} ticks for blocks, that are found in the series.
     * @param p The player, that has found the block.
     * @param blockLocation The location of the block
     */
    public abstract void playFoundEffect(Player p, Location blockLocation);

    /**
     * The frequency of the block indicator, that shows whether the player found the block or not.
     * @return Return
     */
    public abstract int getRepeatTime();

    // TODO find effect
}
