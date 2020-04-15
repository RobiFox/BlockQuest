package me.robifoxx.blockquest;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.robifoxx.blockquest.api.BlockQuestAPI;
import org.bukkit.entity.Player;

public class BlockQuestPlaceholders extends PlaceholderExpansion {
    private BlockQuest blockQuest;
    public BlockQuestPlaceholders(BlockQuest blockQuest) {
        this.blockQuest = blockQuest;
    }

    @Override
    public String getIdentifier() {
        return "blockquest";
    }

    @Override
    public String getAuthor() {
        return blockQuest.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return blockQuest.getDescription().getVersion();
    }


    @Override
    public String onPlaceholderRequest(Player player, String s) {
        if(!s.contains("_")) return null;
        String series = s.split("_")[1];
        String key = BlockQuestAPI.getInstance().getPlayerKey(player);
        BlockQuestAPI instance = BlockQuestAPI.getInstance();
        if(s.startsWith("blocksfound_")) {
            return String.valueOf(instance.getDataStorage().getFoundBlockCount(key, series));
        } else if(s.startsWith("blocksleft_")) {
            return String.valueOf(instance.getBlockCount(blockQuest, series) - instance.getDataStorage().getFoundBlockCount(key, series));
        } else if(s.startsWith("allblocks_")) {
            return String.valueOf(instance.getBlockCount(blockQuest, series));
        }
        return null;
    }
}
