package me.robifoxx.block.command;

import me.robifoxx.block.BlockQuestAPI;
import me.robifoxx.block.Main;
import me.robifoxx.block.Utils;
import me.robifoxx.block.mysql.SQLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlockQuestCommand implements CommandExecutor {
    private Main plugin;
    public BlockQuestCommand(Main plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("blockquest.command")) {
            sender.sendMessage(plugin.getConfig().getString("no-permission").replace("&", "§"));
            return true;
        }
        //plugin.getConfig().getStringList("blocks").size() - Main.blocksss.get(e.getPlayer().getName()).size()
        if(args.length < 1) {
            if (plugin.inEdit.remove(sender.getName())) {
                sender.sendMessage("§cYou disabled edit mode.");
            } else {
                sender.sendMessage("§7§m----------------------------------------");
                sender.sendMessage("§aYou entered edit mode!");
                sender.sendMessage("§aClick on blocks to add it to the config file!");
                sender.sendMessage("§aType §6/blockquest §ato exit edit mode.");
                sender.sendMessage("§7§m----------------------------------------");
                sender.sendMessage("§a§lType §6§l/blockquest reload §a§lto reload the config!");
                sender.sendMessage("§a§lType §6§l/blockquest stats [player] §a§lto check stats!");
                sender.sendMessage("§a§lType §6§l/blockquest save §a§lto save stats!");
                sender.sendMessage("§7§m----------------------------------------");
                if(!plugin.enabled) {
                    sender.sendMessage("§c§lBlocks are disabled. Players cant find them until you enable it with §6§l/blockquest toggle");
                }
                //sender.sendMessage("§a§lType §6§l/blockquest wipedata §a§lto clear data. §c§l!WARNING! This resets EVERYONE'S data!");
                plugin.inEdit.add(sender.getName());
            }
        } else {
            if(args[0].equalsIgnoreCase("reload")) {
                plugin.reloadConfig();
                Utils.sendMessageFromMSGS(sender, plugin.msgs.getConfig().getString("config-reloaded"));
            } else if(args[0].equalsIgnoreCase("toggle")) {
                plugin.enabled = !plugin.enabled;
                if(plugin.enabled) {
                    Utils.sendMessageFromMSGS(sender, plugin.msgs.getConfig().getString("enabled-blocks"));
                } else {
                    Utils.sendMessageFromMSGS(sender, plugin.msgs.getConfig().getString("disabled-blocks"));
                }
                plugin.getConfig().set("enabled", plugin.enabled);
                plugin.saveConfig();
            } else if(args[0].equalsIgnoreCase("save")) {
                int amount = 0;
                for(Player pl : Bukkit.getOnlinePlayers()) {
                    if (plugin.saved_x.get(pl.getName()) != null) {
                        amount++;
                        Utils.sendMessageFromMSGS(sender, plugin.msgs.getConfig().getString("saving-data-for").replace("%target%", pl.getName()));
                        if (plugin.useMysql) {
                            SQLPlayer.setString(Utils.getIdentifier(pl), "X", plugin.saved_x.get(pl.getName()));
                            SQLPlayer.setString(Utils.getIdentifier(pl), "Y", plugin.saved_y.get(pl.getName()));
                            SQLPlayer.setString(Utils.getIdentifier(pl), "Z", plugin.saved_z.get(pl.getName()));
                            SQLPlayer.setString(Utils.getIdentifier(pl), "WORLD", plugin.saved_world.get(pl.getName()));
                        } else {
                            plugin.data.getConfig().set("data." + Utils.getIdentifier(pl) + ".x", plugin.saved_x.get(pl.getName()));
                            plugin.data.getConfig().set("data." + Utils.getIdentifier(pl) + ".y", plugin.saved_y.get(pl.getName()));
                            plugin.data.getConfig().set("data." + Utils.getIdentifier(pl) + ".z", plugin.saved_z.get(pl.getName()));
                            plugin.data.getConfig().set("data." + Utils.getIdentifier(pl) + ".world", plugin.saved_world.get(pl.getName()));
                            plugin.data.saveConfig();
                        }
                    }
                }
                Utils.sendMessageFromMSGS(sender, plugin.msgs.getConfig().getString("finished-saving").replace("%amount%", "" + amount));
            } else if(args[0].equalsIgnoreCase("stats")) {
                int currentBlocks = plugin.getConfig().getStringList("blocks").size();
                if(args.length >= 2) {
                   /* String argReq = Utils.getIdentifierFromUsername(args[1]);
                    if((!plugin.useMysql && plugin.data.getConfig().getString("data." + argReq + ".x") != null)
                            || ( plugin.useMysql && SQLPlayer.playerExists(argReq))) {
                        foundBlocks = plugin.data.getConfig().getString("data." + argReq + ".x").split(";").length - 1;
                    } else {
                        Utils.sendMessageFromMSGS(sender, plugin.msgs.getConfig().getString("stats-unknown-player").replace("%target%", args[1]));
                        return true;
                    }*/
                    Utils.sendMessageFromMSGS(sender, plugin.msgs.getConfig().getString("personal-stats").replace("%target%", args[1])
                            .replace("%currentBlocks%", "" + currentBlocks)
                            .replace("%percent%", "" + BlockQuestAPI.getInstance().getFoundPercent(args[1], 2))
                            .replace("%foundBlocks%", "" + BlockQuestAPI.getInstance().getFoundBlocks(args[1])));
                } else {
                    int foundAllBlocks = 0;
                    if (!plugin.useMysql) {
                        for (String s : plugin.data.getConfig().getConfigurationSection("data").getKeys(false)) {
                            if (!s.equalsIgnoreCase("1-1-1-1-1-1")) {
                                int foundBlocks = plugin.data.getConfig().getString("data." + s + ".x").split(";").length - 1;
                                if (foundBlocks >= currentBlocks) {
                                    foundAllBlocks++;
                                }
                            }
                        }
                    } else {
                        for (String s : SQLPlayer.getAll()) {
                            int foundBlocks = SQLPlayer.getString(s, "X").split(";").length - 1;
                            if (foundBlocks >= currentBlocks) {
                                foundAllBlocks++;
                            }
                        }
                    }
                    Utils.sendMessageFromMSGS(sender, plugin.msgs.getConfig().getString("global-stats")
                            .replace("%currentBlocks%", "" + currentBlocks)
                            .replace("%percent%", BlockQuestAPI.getInstance().getFoundPercent(2) + "")
                            .replace("%foundAllBlocks%", "" + foundAllBlocks));
                }
            } /*else if(args[0].equalsIgnoreCase("wipedata")) {
                    sender.sendMessage("§aWiping data...");
                    boolean success = false;
                    if(useMysql) {
                        mysql.update("DROP TABLE BlockQuest");
                        createMySQL();
                        success = true;
                    } else {

                        Config c = new Config("plugins/BlockQuest", "data.yml");
                        c.create();
                        if(c.toFile().delete()) {
                            c.setDefault("data.yml");
                            c.getConfig().options().copyDefaults(true);
                            c.saveConfig();

                            data = c;
                            success = true;
                        }
                    }
                    if(success) {
                        sender.sendMessage("§aData Wiped successfully!");
                    } else {
                        sender.sendMessage("§cData wipe failed! :(");
                    }
                }*/
        }
        return true;
    }
}
