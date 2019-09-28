package me.robifoxx.block;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;

/**
 * Created by RobiFoxx.
 * All rights reserved.
 */
public class Placeholders extends PlaceholderExpansion {
    private Main plugin;
    public Placeholders(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "blockquest";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String s) {
        if(s.startsWith("blocksfound")) {
            return String.valueOf(BlockQuestAPI.getInstance().getFoundBlocks(player.getName()));
        } else if(s.startsWith("blocksleft")) {
            return String.valueOf(BlockQuestAPI.getInstance().getBlocksLeft(player.getName()));
        } else if(s.startsWith("allblocks")) {
            return String.valueOf(BlockQuestAPI.getInstance().getAllBlocks().length);
        } else if(s.startsWith("foundpercent")) {
            return String.valueOf(BlockQuestAPI.getInstance().getFoundPercent(2));
        } else if(s.startsWith("foundpercent_")) {
            String p = s.replaceFirst("foundpercent_", "");
            return String.valueOf(BlockQuestAPI.getInstance().getFoundPercent(p,2));
        }
        return null;
    }
}
