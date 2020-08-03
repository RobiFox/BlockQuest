package me.robifoxx.blockquest.command.sub;

import me.robifoxx.blockquest.BlockQuest;
import me.robifoxx.blockquest.api.BlockQuestAPI;
import me.robifoxx.blockquest.api.BlockQuestSeries;
import me.robifoxx.blockquest.command.SubCommand;
import org.bukkit.command.CommandSender;

public class GuideCommand extends SubCommand {
    @Override
    public String getBase() {
        return "guide";
    }

    @Override
    public void onCommand(BlockQuest plugin, CommandSender sender, String[] args) {
        sender.sendMessage("§7§m----------------------------------------");
        sender.sendMessage(" §2§l1) §aFirst, create a series using the following command:");
        sender.sendMessage(" §7   /blockquest series §8myseries §7create");
        sender.sendMessage(" §a   You can change §8myseries §ato anything you want, such as §8christmas§a, §8halloween§a, etc.");
        sender.sendMessage(" §2§l2) §aPlace down a block, or a player head, which you want to be a hidden block.");
        sender.sendMessage(" §2§l3) §aType the following command:");
        sender.sendMessage(" §7   /blockquest series §8myseries §7edit");
        sender.sendMessage(" §a   Don't forget to change §8myseries §ato whatever you named your series to.");
        sender.sendMessage(" §2§l4) §aRepeat step 2 and 3 until you have enough blocks");
        sender.sendMessage(" §2§l5) §aOpen BlockQuest's config.yml, find your series, and change the commands to fit your needs");
        sender.sendMessage(" §2§l6) §aAfter making the changes, save the config, and restart the server");
        sender.sendMessage(" §2§l7) §aDon't forget to enable your Series by running the following command:");
        sender.sendMessage(" §7   /blockquest series §8myseries §7toggle");
        sender.sendMessage(" §a   Don't forget to change §8myseries §ato whatever you named your series to.");
        sender.sendMessage("§7§m----------------------------------------");
    }
}
