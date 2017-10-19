package me.robifoxx.block;

import com.darkblade12.particleeffect.ParticleEffect;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by RobiFoxx.
 * All rights reserved.
 */
public class Main extends JavaPlugin  {
    static MySQL mysql;
    static ArrayList<String> inEdit = new ArrayList<>();
    static HashMap<String, List<String>> blocksss = new HashMap<>();
    static HashMap<String, String> saved_x = new HashMap<>();
    static HashMap<String, String> saved_y = new HashMap<>();
    static HashMap<String, String> saved_z = new HashMap<>();
    static HashMap<String, String> saved_world = new HashMap<>();
    static Config data;
    static boolean useMysql = false;
    static boolean unsafeSave = true;
    static ArrayList<String> eventReturn = new ArrayList<>();
    static boolean findEffect = false;
    static boolean enabled = false;
    static String disabledMsg = "&cBlocks aren't enabled yet!";
    static boolean checkFullInventory = false;
    static String fullInventoryMsg = "&c&lYour inventory is full!";

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
        Bukkit.getPluginManager().registerEvents(new BEvent(), this);
        if(getConfig().getString("mysql-unsafe-save") != null) {
            if(getConfig().getString("mysql-unsafe-save").equalsIgnoreCase("false")) {
                unsafeSave = false;
            }
        }
        if(getConfig().getStringList("already-found-all-blocks") == null) {
            getConfig().set("already-found-all-blocks", new ArrayList<String>().add("msg %player% You already found all blocks!"));
        }
        if(getConfig().get("enabled") == null) {
            enabled = getConfig().getBoolean("enabled");
            if(getConfig().get("disabled-msg") == null) {
                disabledMsg = getConfig().getString("disabled-msg");
            }
        }
        if(getConfig().get("use-uuid") == null) {
            Utils.useUUID = getConfig().getBoolean("use-uuid");
        }
        if(getConfig().get("check-full-inventory") == null) {
            checkFullInventory = getConfig().getBoolean("check-full-inventory");
            if(getConfig().get("full-inventory-msg") == null) {
                fullInventoryMsg = getConfig().getString("full-inventory-msg");
            }
        }
        if(getConfig().getString("placeholderapi") != null
                && getConfig().getString("placeholderapi").equalsIgnoreCase("true")) {
            if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                new Placeholders(this).hook();
            } else {
                getLogger().warning("PlaceholderAPI not found, placeholders will not work.");
                getLogger().warning("Please install the following plugin:");
                getLogger().warning("https://www.spigotmc.org/resources/p.6245/");
            }
        }
        findEffect = getConfig().getBoolean("find-effect.enabled");
        boolean enabledParticle = getConfig().getBoolean("particles.enabled");
        if(enabledParticle) {
            int loop = getConfig().getInt("particles.loop");
            String f_type;
            float f_dx;
            float f_dy;
            float f_dz;
            float f_speed;
            int f_quan;
            String nf_type;
            float nf_dx;
            float nf_dy;
            float nf_dz;
            float nf_speed;
            int nf_quan;
            {
                String f = "found";
                f_type = getConfig().getString("particles." + f + ".type");
                f_dx = Float.valueOf(getConfig().getDouble("particles." + f + ".dx") + "");
                f_dy = Float.valueOf(getConfig().getDouble("particles." + f + ".dy") + "");
                f_dz = Float.valueOf(getConfig().getDouble("particles." + f + ".dz") + "");
                f_speed = Float.valueOf(getConfig().getDouble("particles." + f + ".speed") + "");
                f_quan = getConfig().getInt("particles." + f + ".quantity");
            }
            {
                String f = "notfound";
                nf_type = getConfig().getString("particles." + f + ".type");
                nf_dx = Float.valueOf(getConfig().getDouble("particles." + f + ".dx") + "");
                nf_dy = Float.valueOf(getConfig().getDouble("particles." + f + ".dy") + "");
                nf_dz = Float.valueOf(getConfig().getDouble("particles." + f + ".dz") + "");
                nf_speed = Float.valueOf(getConfig().getDouble("particles." + f + ".speed") + "");
                nf_quan = getConfig().getInt("particles." + f + ".quantity");
            }
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                for(String s : getConfig().getStringList("blocks")) {
                    for(Player pl : Bukkit.getOnlinePlayers()) {
                        //If it was a reload, then dont bother to proceed
                        if(blocksss.get(pl.getName()) != null) {
                            boolean found = blocksss.get(pl.getName()).contains(s);
                            String[] splt = s.split(";");
                            //x;y;z;w
                            Location loc = new Location(Bukkit.getWorld(splt[3]), Integer.valueOf(splt[0]) + 0.5, Integer.valueOf(splt[1]) + 0.25, Integer.valueOf(splt[2]) + 0.5);
                            if(found) {
                                if(!f_type.equalsIgnoreCase("DISABLED")) {
                                    ParticleEffect.valueOf(f_type).display(
                                            f_dx,
                                            f_dy,
                                            f_dz,
                                            f_speed,
                                            f_quan,
                                            loc, pl);
                                }
                            } else {
                                if(!nf_type.equalsIgnoreCase("DISABLED")) {
                                    ParticleEffect.valueOf(nf_type).display(
                                            nf_dx,
                                            nf_dy,
                                            nf_dz,
                                            nf_speed,
                                            nf_quan,
                                            loc, pl);
                                }
                            }
                        }
                    }
                }
            }, loop, loop);
        }
        new Metrics(this);
        getLogger().info("Enabled Metrics (bStats).");
    }

    public void createMySQL() {
        mysql.update("CREATE TABLE IF NOT EXISTS BlockQuest (UUID varchar(128), X varchar(2048) default \"none\", Y varchar(2048) default \"none\", Z varchar(2048) default \"none\", WORLD varchar(2048) default \"none\")");
    }

    @Override
    public void onDisable() {
        for(Player pl : Bukkit.getOnlinePlayers()) {
            if(useMysql) {
                SQLPlayer.setString(Utils.getIdentifier(pl), "X", saved_x.get(pl.getName()));
                SQLPlayer.setString(Utils.getIdentifier(pl), "Y", saved_y.get(pl.getName()));
                SQLPlayer.setString(Utils.getIdentifier(pl), "Z", saved_z.get(pl.getName()));
                SQLPlayer.setString(Utils.getIdentifier(pl), "WORLD", saved_world.get(pl.getName()));
            } else {
                data.getConfig().set("data." + Utils.getIdentifier(pl) + ".x", saved_x.get(pl.getName()));
                data.getConfig().set("data." + Utils.getIdentifier(pl) + ".y", saved_y.get(pl.getName()));
                data.getConfig().set("data." + Utils.getIdentifier(pl) + ".z", saved_z.get(pl.getName()));
                data.getConfig().set("data." + Utils.getIdentifier(pl) + ".world", saved_world.get(pl.getName()));
                data.saveConfig();
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("blockquest")) {
            if(!sender.hasPermission("blockquest.command")) {
                sender.sendMessage(getConfig().getString("no-permission").replace("&", "§"));
                return true;
            }
            //plugin.getConfig().getStringList("blocks").size() - Main.blocksss.get(e.getPlayer().getName()).size()
            if(args.length < 1) {
                if (inEdit.remove(sender.getName())) {
                    sender.sendMessage("§cYou disabled edit mode.");
                } else {
                    sender.sendMessage("§aYou entered edit mode!");
                    sender.sendMessage("§aClick on blocks to add it to the config file!");
                    sender.sendMessage("§aType §6/blockquest §ato exit edit mode.");
                    sender.sendMessage("§a§lType §6§l/blockquest reload §a§lto reload the config!");
                    sender.sendMessage("§a§lType §6§l/blockquest stats §a§lto check stats!");
                    if(!enabled) {
                        sender.sendMessage("§c§lBlocks are disabled. Players cant find them until you enable it with §6§l/blockquest toggle");
                    }
                    //sender.sendMessage("§a§lType §6§l/blockquest wipedata §a§lto clear data. §c§l!WARNING! This resets EVERYONE'S data!");
                    inEdit.add(sender.getName());
                }
            } else {
                if(args[0].equalsIgnoreCase("reload")) {
                    reloadConfig();
                    sender.sendMessage("§aConfig reloaded!");
                } else if(args[0].equalsIgnoreCase("toggle")) {
                    enabled = !enabled;
                    if(enabled) {
                        sender.sendMessage("§aEnabled Blocks!");
                    } else {
                        sender.sendMessage("§cDisabled Blocks!");
                    }
                    getConfig().set("enabled", enabled);
                    saveConfig();
                } else if(args[0].equalsIgnoreCase("stats")) {
                    int total = 0;
                    int foundAllBlocks = 0;
                    int currentBlocks = getConfig().getStringList("blocks").size();
                    for(String s : data.getConfig().getConfigurationSection("data").getKeys(false)) {
                        if(!s.equalsIgnoreCase("1-1-1-1-1-1")) {
                            String id = s;
                            if(!Utils.useUUID) {
                                id = Utils.getUsername(s);
                            }
                            total++;
                            int foundBlocks = data.getConfig().getString("data." + id + ".x").split(";").length - 1;
                            if(foundBlocks >= currentBlocks) {
                                foundAllBlocks++;
                            }
                        }
                    }
                    double foundPercent = ((foundAllBlocks * 1.0) / (total * 1.0)) * 100;
                    BigDecimal dec = new BigDecimal(foundPercent).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                    sender.sendMessage("§a§lCurrent Blocks: §e" + currentBlocks);
                    sender.sendMessage("§e§l" + dec + "% §a§lhas found all blocks.");
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
}
