package me.robifoxx.block.events;

import me.robifoxx.block.api.FindEffect;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class BlockFindEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

    private Player p;
    private Block b;
    private FindEffect ef;

    public BlockFindEvent(Player p, Block b, FindEffect ef) {
        this.p = p;
        this.b = b;
        this.ef = ef;
    }

    public Block getBlock() {
        return b;
    }

    public FindEffect getEffect() {
        return ef;
    }

    public Player getPlayer() {
        return p;
    }

    public void setEffect(FindEffect ef) {
        this.ef = ef;
    }

    private boolean cancel = false;

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        cancel = b;
    }
}
