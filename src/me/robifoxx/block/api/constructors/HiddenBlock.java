package me.robifoxx.block.api.constructors;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class HiddenBlock {
    private Location location;
    private Block block;
    public HiddenBlock(Block block, Location location) {
        this.location = location;
        this.block = block;
    }

    public Location getLocation() {
        return location;
    }

    public Block getBlock() {
        return block;
    }
}
