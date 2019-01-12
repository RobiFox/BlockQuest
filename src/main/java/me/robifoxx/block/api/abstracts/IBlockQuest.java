package me.robifoxx.block.api.abstracts;

import me.robifoxx.block.api.constructors.HiddenBlock;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public abstract class IBlockQuest {
    private HashMap<Location, HiddenBlock> blocks;
    public abstract void onBlockFindSuccess(Player p, HiddenBlock hb);
    public abstract void blockAlreadyFound(Player p, HiddenBlock hb);
    public abstract void alreadyFoundAllBlocks(Player p, HiddenBlock hb);
    public abstract void foundAllBlocks(Player p, HiddenBlock hb);
    public void registerBlock(Location location) {
        if(blocks.containsKey(location)) {
            throw new IllegalArgumentException("Location already exists you are trying to register.");
        } else {
            blocks.put(location, new HiddenBlock(location.getWorld().getBlockAt(location), location));
        }
    }
}
