package me.robifoxx.blockquest.command;

import com.sun.istack.internal.NotNull;
import me.robifoxx.blockquest.BlockQuest;
import me.robifoxx.blockquest.api.BlockQuestAPI;
import me.robifoxx.blockquest.api.BlockQuestDataStorage;
import me.robifoxx.blockquest.api.BlockQuestSeries;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockQuestCommand implements CommandExecutor {
    private static final List<String> DEFAULT_FIND_BLOCK_COMMANDS = new ArrayList<>(Arrays.asList(
            "particle mobSpell %locX% %locY% %locZ% 0.25 0.25 0.25 1 10",
            "rawmsg %player% true &a&lBlock&2&lQUEST",
            "rawmsg %player% false &a",
            "rawmsg %player% true &fYou found a block!",
            "rawmsg %player% true &f%blocksLeft% left",
            "give %player% diamond 1"));
    private static final List<String> DEFAULT_ALL_BLOCKS_FOUND = new ArrayList<>(Arrays.asList(
            "rawmsg %player% true &a&lBlock&2&lQUEST",
            "rawmsg %player% false &a",
            "rawmsg %player% true &fYou found &lALL &fblocks!",
            "rawmsg %player% true &fNice!",
            "give %player% diamond_block 1"));
    private static final List<String> DEFAULT_ALREADY_FOUND = new ArrayList<>(Arrays.asList(
            "rawmsg %player% true &a&lBlock&2&lQUEST",
            "rawmsg %player% false &a",
            "rawmsg %player% true &fYou already found this block!"));

    private static final List<String> DEFAULT_ALREADY_FOUND_ALL = new ArrayList<>(Arrays.asList(
            "rawmsg %player% true &a&lBlock&2&lQUEST",
            "rawmsg %player% false &a",
            "rawmsg %player% true &fYou already found all blocks!"));

    private BlockQuest blockQuest;
    public BlockQuestCommand(BlockQuest blockQuest) {
        this.blockQuest = blockQuest;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if(!sender.hasPermission("blockquest.command")) {
            sender.sendMessage(blockQuest.getConfig().getString("no-permission-msg").replace("&", "§"));
            return true;
        }
        if(args.length == 0) {
            sender.sendMessage("§7§m----------------------------------------");
            sender.sendMessage(" §2§lWelcome to BlockQuest!");
            sender.sendMessage("  §aIf this is your first time using BlockQuest,");
            sender.sendMessage("  §ayou might want to run §2/blockquest guide");
            sender.sendMessage("§7§m----------------------------------------");
            sender.sendMessage(getHelpFormat("series <id> create", "Creates a new series, which will contain hidden blocks"));
            sender.sendMessage(getHelpFormat("series <id> delete", "Deletes the series which has the specified id"));
            sender.sendMessage(getHelpFormat("series <id> edit", "Add or remove hidden blocks for the specified id"));
            sender.sendMessage(getHelpFormat("series <id> toggle", "Enables or disables the series with the specified id"));
            sender.sendMessage(getHelpFormat("series <id> stats [player]", "Displays who found all blocks in that series. If player is specified, it will display their stats."));
            sender.sendMessage(getHelpFormat("series <id> teleport <number>", "Teleports you to the specified index"));
            sender.sendMessage(getHelpFormat("series <id> reset <player>", "Resets the specified player's statistics. Use * to clear everyone's."));
            sender.sendMessage(getHelpFormat("list", "Lists all series"));
            sender.sendMessage("§7§m----------------------------------------");
        } else if(args[0].equalsIgnoreCase("guide")) {
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
        } else if(args[0].equalsIgnoreCase("series")) {
            if(args.length < 3) {
                sender.sendMessage("§cMissing arguments.");
                return true;
            }
            String id = args[1];
            if(args[2].equalsIgnoreCase("create")) {
                if(blockQuest.getConfig().get("series." + id) != null) {
                    sender.sendMessage("§cA series with the id " + id + " already exists!");
                    return true;
                }
                blockQuest.getConfig().set("series." + id + ".enabled", false);
                blockQuest.getConfig().set("series." + id + ".find-block-commands", DEFAULT_FIND_BLOCK_COMMANDS);
                blockQuest.getConfig().set("series." + id + ".all-blocks-found-commands", DEFAULT_ALL_BLOCKS_FOUND);
                blockQuest.getConfig().set("series." + id + ".already-found-commands", DEFAULT_ALREADY_FOUND);
                blockQuest.getConfig().set("series." + id + ".already-found-all-blocks", DEFAULT_ALREADY_FOUND_ALL);
                blockQuest.getConfig().set("series." + id + ".blocks", new ArrayList<String>());
                blockQuest.saveConfig();
                sender.sendMessage("§aCreated BlockQuest series " + id + "!");
            } else if(args[2].equalsIgnoreCase("delete")) {
                if(blockQuest.getConfig().get("series." + id) == null) {
                    sender.sendMessage("§cA series with the id " + id + " doesn't exist!");
                    return true;
                }
                blockQuest.getConfig().set("series." + id, null);
                blockQuest.saveConfig();
                sender.sendMessage("§aDeleted BlockQuest series " + id + "!");
            } else if(args[2].equalsIgnoreCase("edit")) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage("§cThis command can't be ran from console!");
                    return true;
                }
                if(blockQuest.getConfig().get("series." + id) == null) {
                    sender.sendMessage("§cA series with the id " + id + " doesn't exist!");
                    return true;
                }
                blockQuest.playersInEdit.put(sender.getName(), id);
                sender.sendMessage("§aEntered edit mode for " + id + "!");
                sender.sendMessage("§aClick on a block to add it as a Hidden Block.");
                sender.sendMessage("§aLeft click the air to exit edit mode");
            } else if(args[2].equalsIgnoreCase("toggle")) {
                boolean exists = BlockQuestAPI.getInstance().isRegistered(id);
                if(exists) {
                    sender.sendMessage("§cDisabled series " + id + "!");
                    BlockQuestAPI.getInstance().unregisterSeries(id);
                } else {
                    sender.sendMessage("§aEnabled series " + id + "!");
                    BlockQuestAPI.getInstance().registerDefaultSeries(id, blockQuest);
                }
                blockQuest.getConfig().set("series." + id + ".enabled", !exists);
                blockQuest.saveConfig();
            } else if(args[2].equalsIgnoreCase("stats")) {
                if(blockQuest.getConfig().get("series." + id) == null) {
                    sender.sendMessage("§cA series with the id " + id + " doesn't exist!");
                    return true;
                }
                int totalBlocks = BlockQuestAPI.getInstance().getBlockCount(blockQuest, id);
                BlockQuestDataStorage bqds = BlockQuestAPI.getInstance().getDataStorage();
                if(args.length == 4) {
                    sender.sendMessage("§a§lStats for series " + id + " for " + args[3] + ":");
                    sender.sendMessage("§aThis player found §e" + bqds.getFoundBlockCount(BlockQuestAPI.getInstance().getPlayerKey(Bukkit.getOfflinePlayer(args[3])), id) + " §ablocks out of §e" + totalBlocks);
                } else {
                    List<String> users = BlockQuestAPI.getInstance().getDataStorage().getAllUsers(id);
                    int foundAll = 0;
                    for(String user : users) {
                        if(bqds.getFoundBlockCount(user, id) >= totalBlocks) {
                            foundAll++;
                        }
                    }
                    sender.sendMessage("§a§lStats for series " + id + ":");
                    sender.sendMessage("§aOut of §e" + users.size() + "§a players, §e" + foundAll + " §afound all blocks");
                }
            } else if(args[2].equalsIgnoreCase("teleport")) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage("§cThis command can't be ran from console!");
                    return true;
                }
                if(blockQuest.getConfig().get("series." + id) == null) {
                    sender.sendMessage("§cA series with the id " + id + " doesn't exist!");
                    return true;
                }
                BlockQuestSeries series = BlockQuestAPI.getInstance().getSeries(id);
                if(series == null) {
                    sender.sendMessage("§cThe series must be enabled to check its stats.");
                    sender.sendMessage("§4/blockquest series " + id + "toggle ");
                    return true;
                }
                if(args.length < 4) {
                    sender.sendMessage("§cMissing arguments.");
                    sender.sendMessage("§cPlease specify an index, starting from 1");
                    return true;
                }
                int index = Integer.parseInt(args[3]);
                int max = series.getHiddenBlocks().size();
                if(index <= 0 || index > max) {
                    sender.sendMessage("§cInvalid index!");
                    sender.sendMessage("§cIndex must be between 1 and " + max);
                    return true;
                }
                ((Player) sender).teleport(series.getHiddenBlocks().get(index - 1));
                sender.sendMessage("§aTeleported to the block with id of §e" + index + "§a, out of §e" + max);
            } else if(args[2].equalsIgnoreCase("reset")) {
                if(blockQuest.getConfig().get("series." + id) == null) {
                    sender.sendMessage("§cA series with the id " + id + " doesn't exist!");
                    return true;
                }
                if(args.length < 4) {
                    sender.sendMessage("§cMissing arguments.");
                    sender.sendMessage("§cPlease specify a player, or use * to reset everyone's.");
                    return true;
                }
                String player = args[3];
                if(player.equalsIgnoreCase("*")) {
                    sender.sendMessage("§cClearing everyone's data for " + id);
                    for(String key : BlockQuestAPI.getInstance().getDataStorage().getAllUsers(id)) {
                        BlockQuestAPI.getInstance().getDataStorage().clearStats(key, id);
                    }
                    sender.sendMessage("§aDone.");
                } else {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[3]);
                    sender.sendMessage("§cClearing " + args[3] + "'s data for " + id);
                    BlockQuestAPI.getInstance().getDataStorage().clearStats(BlockQuestAPI.getInstance().getPlayerKey(offlinePlayer), id);
                    sender.sendMessage("§aDone.");
                }
            }
        } else if(args[0].equalsIgnoreCase("list")) {
            sender.sendMessage("§2BlockQuest series:");
            ConfigurationSection cs = blockQuest.getConfig().getConfigurationSection("series");
            if(cs == null) {
                sender.sendMessage(" §cNone");
                return true;
            }
            for(String series : cs.getKeys(false)) {
                sender.sendMessage(" " + (blockQuest.getConfig().getBoolean("series." + series + ".enabled") ? "§a" : "§c") + series);
            }
        }
        return true;
    }

    private String getHelpFormat(String args, String description) {
        return " §2/blockquest §a" + args + " §8§l>>§7 " + description;
    }
}
