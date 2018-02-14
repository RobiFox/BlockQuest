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
            return String.valueOf(BlockQuestAPI.getInstance().getFoundBlocks(player));
        } else if(s.startsWith("blocksleft")) {
            return String.valueOf(BlockQuestAPI.getInstance().getBlocksLeft(player));
        } else if(s.startsWith("allblocks")) {
            return String.valueOf(BlockQuestAPI.getInstance().getAllBlocks().length);
        }
        return null;
    }
}
