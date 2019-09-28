package me.robifoxx.blockquest.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class BlockQuestSeries {
    public abstract String getID();
    public abstract boolean isEnabled();
    public abstract int getRequiredInventorySpace();

    public abstract void getFindBlockAction(Player p, Location blockLocation);
    public abstract void getFoundAllBlockAction(Player p, Location blockLocation);
    public abstract void getAlreadyFoundBlockAction(Player p, Location blockLocation);
    public abstract void getAlreadyFoundAllBlockAction(Player p, Location blockLocation);

    public abstract void playNotFoundParticle(Player p, Location blockLocation);
    public abstract void playFoundParticle(Player p, Location blockLocation);

    // TODO find effect
}
