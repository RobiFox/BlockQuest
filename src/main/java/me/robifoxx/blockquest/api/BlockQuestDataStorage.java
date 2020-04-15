package me.robifoxx.blockquest.api;

import org.bukkit.Location;

import java.util.List;

/**
 * This is what manages the data about
 * what block has a player found
 */
public abstract class BlockQuestDataStorage {
    /**
     * Whenever a player finds a hidden block, this method will run,
     * saving the player's stat
     * @param key The user, who found the block. Can be an UUID or a Player Name, depending on the settings.
     * @param series The series' id where the block belongs to
     * @param location The location of the found block
     */
    public abstract void storeFoundBlock(String key, String series, Location location);

    /**
     * Whenever a player finds a hidden block, this will run to check
     * if the player has already found the block or not
     * @param key The user, who found the block. Can be an UUID or a Player Name, depending on the settings.
     * @param series The series' id where the block belongs to
     * @param location The location of the found block
     * @return True, if player has found the block at that location already
     */
    public abstract boolean hasFoundBlock(String key, String series, Location location);

    /**
     * Get the amount of blocks found by this player in the specified series
     * @param key The user, who found the block. Can be an UUID or a Player Name, depending on the settings.
     * @param series The series' id where the block belongs to
     * @return The amount of blocks found in that series
     */
    public abstract int getFoundBlockCount(String key, String series);

    /**
     * Clears the data for this player for the series.
     * Clear usually happens, when the owner wants to
     * reintroduce the same series some time later.
     * @param key The user, who found the block. Can be an UUID or a Player Name, depending on the settings.
     * @param series The series' id where the block belongs to
     */
    public abstract void clearStats(String key, String series);

    /**
     * Gets all users that has found anything in that series.
     * @param series The specified series' id
     * @return The user's key, who has found one, or more blocks from the series
     */
    public abstract List<String> getAllUsers(String series);
}
