package me.robifoxx.block;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by RobiFoxx.
 * All rights reserved.
 */
public class Main extends JavaPlugin implements Listener {
    public static MySQL mysql;
    ArrayList<String> inEdit = new ArrayList<>();
    static HashMap<String, List<String>> blocksss = new HashMap<>();
    HashMap<String, String> saved_x = new HashMap<>();
    HashMap<String, String> saved_y = new HashMap<>();
    HashMap<String, String> saved_z = new HashMap<>();
    HashMap<String, String> saved_world = new HashMap<>();
    Config data;
    boolean useMysql = false;
    boolean unsafeSave = true;
    ArrayList<String> eventReturn = new ArrayList<>();
    boolean placeholder = false;

    public void onEnable() {
        if(!(new File("plugins/BlockQuest/config.yml").exists())) {
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
        {

            Config c = new Config("plugins/BlockQuest", "data.yml");
            c.create();

            c.setDefault("data.yml");

            if(!c.exists()) {

                c.getConfig().options().copyDefaults(true);
                c.saveConfig();

            }

            data = c;

        }
        if(getConfig().getString("use-mysql").equalsIgnoreCase("true")) {
            mysql = new MySQL(getConfig().getString("mysql-host"), getConfig().getString("mysql-database"), getConfig().getString("mysql-username"), getConfig().getString("mysql-password"));
            createMySQL();
            useMysql = true;
        }
        Bukkit.getPluginManager().registerEvents(this, this);
        if(getConfig().getString("mysql-unsafe-save") != null) {
            if(getConfig().getString("mysql-unsafe-save").equalsIgnoreCase("false")) {
                unsafeSave = false;
            }
        }
        if(getConfig().getStringList("already-found-all-blocks") == null) {
            getConfig().set("already-found-all-blocks", new ArrayList<String>().add("msg %player% You already found all blocks!"));
        }
        if(getConfig().getString("placeholderapi") != null
                && getConfig().getString("placeholderapi").equalsIgnoreCase("true")) {
            if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                placeholder = true;
                new Placeholders(this).hook();
            } else {
                getLogger().warning("PlaceholderAPI not found, placeholders will not work.");
                getLogger().warning("Please install the following plugin:");
                getLogger().warning("https://www.spigotmc.org/resources/p.6245/");
            }
        }
    }

    public void createMySQL() {
        mysql.update("CREATE TABLE IF NOT EXISTS BlockQuest (UUID varchar(128), X varchar(2048) default \"none\", Y varchar(2048) default \"none\", Z varchar(2048) default \"none\", WORLD varchar(2048) default \"none\")");
    }

    @Override
    public void onDisable() {
        for(Player pl : Bukkit.getOnlinePlayers()) {
            if(useMysql) {
                SQLPlayer.setString(pl.getUniqueId().toString(), "X", saved_x.get(pl.getName()));
                SQLPlayer.setString(pl.getUniqueId().toString(), "Y", saved_y.get(pl.getName()));
                SQLPlayer.setString(pl.getUniqueId().toString(), "Z", saved_z.get(pl.getName()));
                SQLPlayer.setString(pl.getUniqueId().toString(), "WORLD", saved_world.get(pl.getName()));
            } else {
                data.getConfig().set("data." + pl.getUniqueId().toString() + ".x", saved_x.get(pl.getName()));
                data.getConfig().set("data." + pl.getUniqueId().toString() + ".y", saved_y.get(pl.getName()));
                data.getConfig().set("data." + pl.getUniqueId().toString() + ".z", saved_z.get(pl.getName()));
                data.getConfig().set("data." + pl.getUniqueId().toString() + ".world", saved_world.get(pl.getName()));
                data.saveConfig();
            }
        }
    }

    /*public void onDisable() {
        for(Player pl : Bukkit.getOnlinePlayers()) {
            if(useMysql) {
                SQLPlayer.setString(pl.getUniqueId().toString(), "X", saved_x.get(pl.getName()));
                SQLPlayer.setString(pl.getUniqueId().toString(), "Y", saved_y.get(pl.getName()));
                SQLPlayer.setString(pl.getUniqueId().toString(), "Z", saved_z.get(pl.getName()));
                SQLPlayer.setString(pl.getUniqueId().toString(), "WORLD", saved_world.get(pl.getName()));
            } else {
                data.getConfig().set("data." + pl.getUniqueId().toString() + ".x", saved_x.get(pl.getName()));
                data.getConfig().set("data." + pl.getUniqueId().toString() + ".y", saved_y.get(pl.getName()));
                data.getConfig().set("data." + pl.getUniqueId().toString() + ".z", saved_z.get(pl.getName()));
                data.getConfig().set("data." + pl.getUniqueId().toString() + ".world", saved_world.get(pl.getName()));
            }
        }
    }*/

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("blockquest")) {
            if(!sender.hasPermission("blockquest.command")) {
                sender.sendMessage(getConfig().getString("no-permission").replace("&", "§"));
                return true;
            }
            if(args.length < 1) {
                if (inEdit.remove(sender.getName())) {
                    sender.sendMessage("§cYou disabled edit mode.");
                } else {
                    sender.sendMessage("§aYou entered edit mode!");
                    sender.sendMessage("§aClick on blocks to add it to the config file!");
                    sender.sendMessage("§aType §6/blockquest §ato exit edit mode.");
                    sender.sendMessage("§a§lType §6§l/blockquest reload §a§lto reload the config!");
                    //sender.sendMessage("§a§lType §6§l/blockquest wipedata §a§lto clear data. §c§l!WARNING! This resets EVERYONE'S data!");
                    inEdit.add(sender.getName());
                }
            } else {
                if(args[0].equalsIgnoreCase("reload")) {
                    reloadConfig();
                    sender.sendMessage("§aConfig reloaded!");
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
        }
        return true;
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        if(data.getConfig().get("data." + e.getPlayer().getUniqueId().toString() + ".x") == null) {
            data.getConfig().set("data." + e.getPlayer().getUniqueId().toString() + ".x", "none");
            data.getConfig().set("data." + e.getPlayer().getUniqueId().toString() + ".y", "none");
            data.getConfig().set("data." + e.getPlayer().getUniqueId().toString() + ".z", "none");
            data.getConfig().set("data." + e.getPlayer().getUniqueId().toString() + ".world", "none");
            data.saveConfig();
        }
        if(blocksss.get(e.getPlayer().getName()) == null) {
            String x;
            String y;
            String z;
            String world;
            if(saved_x.get(e.getPlayer().getName()) != null) {
                x = saved_x.get(e.getPlayer().getName());
                y = saved_z.get(e.getPlayer().getName());
                z = saved_y.get(e.getPlayer().getName());
                world = saved_world.get(e.getPlayer().getName());
            } else {
                if(useMysql) {
                    x = SQLPlayer.getString(e.getPlayer().getUniqueId().toString(), "X");
                    y = SQLPlayer.getString(e.getPlayer().getUniqueId().toString(), "Y");
                    z = SQLPlayer.getString(e.getPlayer().getUniqueId().toString(), "Z");
                    world = SQLPlayer.getString(e.getPlayer().getUniqueId().toString(), "WORLD");
                } else {
                    x = data.getConfig().getString("data." + e.getPlayer().getUniqueId().toString() + ".x");
                    y = data.getConfig().getString("data." + e.getPlayer().getUniqueId().toString() + ".y");
                    z = data.getConfig().getString("data." + e.getPlayer().getUniqueId().toString() + ".z");
                    world = data.getConfig().getString("data." + e.getPlayer().getUniqueId().toString() + ".world");
                }
            }
            saved_x.put(e.getPlayer().getName(), x);
            saved_y.put(e.getPlayer().getName(), y);
            saved_z.put(e.getPlayer().getName(), z);
            saved_world.put(e.getPlayer().getName(), world);

            String[] x_splt = x.split(";");
            String[] y_splt = y.split(";");
            String[] z_splt = z.split(";");
            String[] world_splt = world.split(";");

            int loc = 0;
            List<String> lst = new ArrayList<>();
            for(String s : x_splt) {
                if(!s.equalsIgnoreCase("none")) {
                    lst.add(x_splt[loc] + ";" + y_splt[loc] + ";" + z_splt[loc] + ";" + world_splt[loc]);
                }
                loc++;
            }
            blocksss.put(e.getPlayer().getName(), lst);
        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        if(useMysql) {
            SQLPlayer.setString(e.getPlayer().getUniqueId().toString(), "X", saved_x.get(e.getPlayer().getName()));
            SQLPlayer.setString(e.getPlayer().getUniqueId().toString(), "Y", saved_y.get(e.getPlayer().getName()));
            SQLPlayer.setString(e.getPlayer().getUniqueId().toString(), "Z", saved_z.get(e.getPlayer().getName()));
            SQLPlayer.setString(e.getPlayer().getUniqueId().toString(), "WORLD", saved_world.get(e.getPlayer().getName()));
        } else {
            data.getConfig().set("data." + e.getPlayer().getUniqueId().toString() + ".x", saved_x.get(e.getPlayer().getName()));
            data.getConfig().set("data." + e.getPlayer().getUniqueId().toString() + ".y", saved_y.get(e.getPlayer().getName()));
            data.getConfig().set("data." + e.getPlayer().getUniqueId().toString() + ".z", saved_z.get(e.getPlayer().getName()));
            data.getConfig().set("data." + e.getPlayer().getUniqueId().toString() + ".world", saved_world.get(e.getPlayer().getName()));
            data.saveConfig();
        }
    }

    @EventHandler
    public void click(PlayerInteractEvent e) {
        /*if(e.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }*/
        if(eventReturn.contains(e.getPlayer().getName())) {
            return;
        }
        eventReturn.add(e.getPlayer().getName());
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            eventReturn.remove(e.getPlayer().getName());
        }, 1);
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            List<String> blocks = getConfig().getStringList("blocks");
            String block = e.getClickedBlock().getLocation().getBlockX() + ";" + e.getClickedBlock().getLocation().getBlockY() + ";" + e.getClickedBlock().getLocation().getBlockZ() + ";" + e.getClickedBlock().getLocation().getWorld().getName();
            if(inEdit.contains(e.getPlayer().getName())) {
                if(blocks.contains(block)) {
                    e.getPlayer().sendMessage("§cRemoved this block!");
                    blocks.remove(block);
                    getConfig().set("blocks", blocks);
                    saveConfig();
                } else {
                    e.getPlayer().sendMessage("§aAdded this block!");
                    blocks.add(block);
                    getConfig().set("blocks", blocks);
                    saveConfig();
                }
                inEdit.remove(e.getPlayer().getName());
                e.getPlayer().sendMessage("§aExited edit mode.");
            } else {
                if(getConfig().getStringList("blocks").contains(block)) {
                    if(blocksss.get(e.getPlayer().getName()) == null
                            || !blocksss.get(e.getPlayer().getName()).contains(block)) {
                        saved_x.put(e.getPlayer().getName(), saved_x.get(e.getPlayer().getName()) + ";" + e.getClickedBlock().getLocation().getBlockX());
                        saved_y.put(e.getPlayer().getName(), saved_y.get(e.getPlayer().getName()) + ";" + e.getClickedBlock().getLocation().getBlockY());
                        saved_z.put(e.getPlayer().getName(), saved_z.get(e.getPlayer().getName()) + ";" + e.getClickedBlock().getLocation().getBlockZ());
                        saved_world.put(e.getPlayer().getName(), saved_world.get(e.getPlayer().getName()) + ";" + e.getClickedBlock().getLocation().getWorld().getName());
                        if(blocksss.get(e.getPlayer().getName()) == null) {
                            List<String> lst = new ArrayList<>();
                            lst.add(block);
                            blocksss.put(e.getPlayer().getName(), lst);
                        } else {
                            blocksss.get(e.getPlayer().getName()).add(block);
                        }
                        if(useMysql) {
                            if(!unsafeSave) {
                                SQLPlayer.setString(e.getPlayer().getUniqueId().toString(), "X", saved_x.get(e.getPlayer().getName()));
                                SQLPlayer.setString(e.getPlayer().getUniqueId().toString(), "Y", saved_y.get(e.getPlayer().getName()));
                                SQLPlayer.setString(e.getPlayer().getUniqueId().toString(), "Z", saved_z.get(e.getPlayer().getName()));
                                SQLPlayer.setString(e.getPlayer().getUniqueId().toString(), "WORLD", saved_world.get(e.getPlayer().getName()));
                            }
                        } else {
                            data.getConfig().set("data." + e.getPlayer().getUniqueId().toString() + ".x", saved_x.get(e.getPlayer().getName()));
                            data.getConfig().set("data." + e.getPlayer().getUniqueId().toString() + ".y", saved_y.get(e.getPlayer().getName()));
                            data.getConfig().set("data." + e.getPlayer().getUniqueId().toString() + ".z", saved_z.get(e.getPlayer().getName()));
                            data.getConfig().set("data." + e.getPlayer().getUniqueId().toString() + ".world", saved_world.get(e.getPlayer().getName()));
                            data.saveConfig();
                        }
                        int blocksLeft = getConfig().getStringList("blocks").size() - blocksss.get(e.getPlayer().getName()).size();
                        for(String s : getConfig().getStringList("find-block-commands")) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", e.getPlayer().getName())
                                    .replace("%pLocX%", "" + e.getPlayer().getLocation().getX())
                                    .replace("%pLocY%", "" + e.getPlayer().getLocation().getY())
                                    .replace("%pLocZ%", "" + e.getPlayer().getLocation().getZ())
                                    .replace("%locX5%", "" + (e.getClickedBlock().getLocation().getX() + 0.5))
                                    .replace("%locY5%", "" + (e.getClickedBlock().getLocation().getY() + 0.5))
                                    .replace("%locZ5%", "" + (e.getClickedBlock().getLocation().getZ() + 0.5))
                                    .replace("%locX%", "" + e.getClickedBlock().getLocation().getX())
                                    .replace("%locY%", "" + e.getClickedBlock().getLocation().getY())
                                    .replace("%locZ%", "" + e.getClickedBlock().getLocation().getZ())
                                    .replace("%blockLeft%", "" + blocksLeft)
                                    .replace("%blocksLeft%", "" + blocksLeft));
                        }
                        if(blocksss.get(e.getPlayer().getName()).size() >= getConfig().getStringList("blocks").size()) {
                            for(String s : getConfig().getStringList("all-blocks-found-commands")) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", e.getPlayer().getName())
                                        .replace("%pLocX%", "" + e.getPlayer().getLocation().getX())
                                        .replace("%pLocY%", "" + e.getPlayer().getLocation().getY())
                                        .replace("%pLocZ%", "" + e.getPlayer().getLocation().getZ())
                                        .replace("%locX5%", "" + (e.getClickedBlock().getLocation().getX() + 0.5))
                                        .replace("%locY5%", "" + (e.getClickedBlock().getLocation().getY() + 0.5))
                                        .replace("%locZ5%", "" + (e.getClickedBlock().getLocation().getZ() + 0.5))
                                        .replace("%locX%", "" + e.getClickedBlock().getLocation().getX())
                                        .replace("%locY%", "" + e.getClickedBlock().getLocation().getY())
                                        .replace("%locZ%", "" + e.getClickedBlock().getLocation().getZ())
                                        .replace("%blockLeft%", "" + blocksLeft)
                                        .replace("%blocksLeft%", "" + blocksLeft));
                            }
                        }
                    } else {
                        if(blocksss.get(e.getPlayer().getName()).contains(block)) {
                            int blocksLeft = getConfig().getStringList("blocks").size() - blocksss.get(e.getPlayer().getName()).size();
                            if(blocksLeft <= 0) {
                                for(String s : getConfig().getStringList("already-found-all-blocks")) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", e.getPlayer().getName())
                                            .replace("%pLocX%", "" + e.getPlayer().getLocation().getX())
                                            .replace("%pLocY%", "" + e.getPlayer().getLocation().getY())
                                            .replace("%pLocZ%", "" + e.getPlayer().getLocation().getZ())
                                            .replace("%locX5%", "" + (e.getClickedBlock().getLocation().getX() + 0.5))
                                            .replace("%locY5%", "" + (e.getClickedBlock().getLocation().getY() + 0.5))
                                            .replace("%locZ5%", "" + (e.getClickedBlock().getLocation().getZ() + 0.5))
                                            .replace("%locX%", "" + e.getClickedBlock().getLocation().getX())
                                            .replace("%locY%", "" + e.getClickedBlock().getLocation().getY())
                                            .replace("%locZ%", "" + e.getClickedBlock().getLocation().getZ())
                                            .replace("%blockLeft%", "" + blocksLeft)
                                            .replace("%blocksLeft%", "" + blocksLeft));
                                }
                            } else {
                                for(String s : getConfig().getStringList("already-found-commands")) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", e.getPlayer().getName())
                                            .replace("%pLocX%", "" + e.getPlayer().getLocation().getX())
                                            .replace("%pLocY%", "" + e.getPlayer().getLocation().getY())
                                            .replace("%pLocZ%", "" + e.getPlayer().getLocation().getZ())
                                            .replace("%locX5%", "" + (e.getClickedBlock().getLocation().getX() + 0.5))
                                            .replace("%locY5%", "" + (e.getClickedBlock().getLocation().getY() + 0.5))
                                            .replace("%locZ5%", "" + (e.getClickedBlock().getLocation().getZ() + 0.5))
                                            .replace("%locX%", "" + e.getClickedBlock().getLocation().getX())
                                            .replace("%locY%", "" + e.getClickedBlock().getLocation().getY())
                                            .replace("%locZ%", "" + e.getClickedBlock().getLocation().getZ())
                                            .replace("%blockLeft%", "" + blocksLeft)
                                            .replace("%blocksLeft%", "" + blocksLeft));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
