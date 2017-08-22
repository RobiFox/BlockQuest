package me.robifoxx.block;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;

/**
 * Created by RobiFoxx.
 * All rights reserved.
 */
public class Placeholders extends EZPlaceholderHook {
    private Main p;
    public Placeholders(Main p) {
        super(p, "blockquest");
        this.p = p;
    }

    @Override
    public String onPlaceholderRequest(Player player, String s) {
        if(s.startsWith("blocksfound")) {
            return String.valueOf(Main.blocksss.get(player.getName()).size());
        } else if(s.startsWith("blocksleft")) {
            return String.valueOf(Main.getProvidingPlugin(Main.class).getConfig().getStringList("blocks").size() - Main.blocksss.get(player.getName()).size());
        }
        return null;
    }
}
