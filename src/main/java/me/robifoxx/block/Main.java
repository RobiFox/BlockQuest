package me.robifoxx.block;

import me.robifoxx.block.api.Config;
import me.robifoxx.block.api.FindEffect;
import me.robifoxx.block.api.Metrics;
import me.robifoxx.block.api.Skulls;
import me.robifoxx.block.command.BlockQuestCommand;
import me.robifoxx.block.command.BlockQuestTab;
import me.robifoxx.block.mysql.MySQL;
import me.robifoxx.block.mysql.SQLPlayer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by RobiFoxx.
 * All rights reserved.
 */
public class Main extends JavaPlugin  {
    public MySQL mysql;
    public ArrayList<String> inEdit = new ArrayList<>();
    public HashMap<String, List<String>> blocksss = new HashMap<>();
    public HashMap<String, String> saved_x = new HashMap<>();
    public HashMap<String, String> saved_y = new HashMap<>();
    public HashMap<String, String> saved_z = new HashMap<>();
    public HashMap<String, String> saved_world = new HashMap<>();
    public Config data;
    public Config msgs;
    public boolean useMysql = false;
    public boolean unsafeSave = true;
    public ArrayList<String> eventReturn = new ArrayList<>();
    public boolean findEffect = false;
    public boolean enabled = false;
    public String disabledMsg = "&cBlocks aren't enabled yet!";
    public int checkFullInventory = 0;
    public String fullInventoryMsg = "&c&lYour inventory is full!";
    public Material hideFoundBlocks = Material.AIR;
    public FindEffect findEffectC;
    private static Main plugin;

    public void onEnable() {
        String fileName = this.getDescription().getName();
        if(!(new File("plugins/" + fileName + "/config.yml").exists())) {
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
        {
            Config c = new Config("plugins/" + fileName, "data.yml", this);
            c.create();
            c.setDefault("data.yml");
            if(!c.exists()) {
                c.getConfig().options().copyDefaults(true);
                c.saveConfig();
            }
            data = c;
        }
        {
            Config c = new Config("plugins/" + fileName, "messages.yml", this);
            c.create();

            c.setDefault("messages.yml");
            c.getConfig().options().copyDefaults(true);
            c.saveConfig();

            msgs = c;
        }
        if(getConfig().getString("use-mysql").equalsIgnoreCase("true")) {
            mysql = new MySQL(getConfig().getString("mysql-host"), getConfig().getString("mysql-database"), getConfig().getString("mysql-username"), getConfig().getString("mysql-password"));
            createMySQL();
            useMysql = true;
        }
        Bukkit.getPluginManager().registerEvents(new BlockQuestListener(this), this);
        if(getConfig().getString("mysql-unsafe-save") != null) {
            if(getConfig().getString("mysql-unsafe-save").equalsIgnoreCase("false")) {
                unsafeSave = false;
            }
        }
        if(getConfig().getStringList("already-found-all-blocks") == null) {
            getConfig().set("already-found-all-blocks", new ArrayList<String>().add("msg %player% You already found all blocks!"));
        }
        if(getConfig().get("enabled") != null) {
            enabled = getConfig().getBoolean("enabled");
            if(getConfig().get("disabled-msg") != null) {
                disabledMsg = getConfig().getString("disabled-msg");
            }
        }
        if(getConfig().get("use-uuid") != null) {
            Utils.useUUID = getConfig().getBoolean("use-uuid");
        }
        if (getConfig().get("check-full-inventory") != null) {
            checkFullInventory = getConfig().getInt("check-full-inventory");
            if (getConfig().get("full-inventory-msg") != null) {
                fullInventoryMsg = getConfig().getString("full-inventory-msg");
            }
        }
        {
            String s = getConfig().getString("hide-found-blocks");
            if (s != null) {
                if (s.equalsIgnoreCase("NONE")) {
                    hideFoundBlocks = null;
                } else {
                    hideFoundBlocks = Material.valueOf(s);
                }
            } else {
                hideFoundBlocks = null;
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
                    Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                        for(Player pl : Bukkit.getOnlinePlayers()) {
                            //If it was a reload, then dont bother to proceed
                            if(blocksss.get(pl.getName()) != null) {
                                boolean found = blocksss.get(pl.getName()).contains(s);
                                String[] splt = s.split(";");
                                //x;y;z;w
                                Location loc = new Location(Bukkit.getWorld(splt[3]), Integer.valueOf(splt[0]) + 0.5, Integer.valueOf(splt[1]) + 0.25, Integer.valueOf(splt[2]) + 0.5);
                                if(found) {
                                    if(!f_type.equalsIgnoreCase("DISABLED")) {
                                        loc.getWorld().spawnParticle(Particle.valueOf(f_type), loc.getX(), loc.getY(), loc.getZ(), f_quan, f_dx, f_dy, f_dz, f_speed);
                                    }
                                } else {
                                    if(!nf_type.equalsIgnoreCase("DISABLED")) {
                                        loc.getWorld().spawnParticle(Particle.valueOf(nf_type), loc.getX(), loc.getY(), loc.getZ(), nf_quan, nf_dx, nf_dy, nf_dz, nf_speed);
                                    }
                                }
                            }
                        }
                    });
                }
            }, loop, loop);
        }
        getLogger().info("Enabling Metrics (bStats).");
        Metrics m = new Metrics(this);
        m.addCustomChart(new Metrics.SingleLineChart("blocks", () -> BlockQuestAPI.getInstance().getAllBlocks().length));
        getLogger().info("Enabled Metrics.");
        plugin = this;
        {
            boolean visible = !getConfig().getBoolean("find-effect.invisible");
            boolean small = getConfig().getBoolean("find-effect.small");
            String head = getConfig().getString("find-effect.head").equalsIgnoreCase("NONE") ? null : getConfig().getString("find-effect.head");
            String chest = getConfig().getString("find-effect.chest").equalsIgnoreCase("NONE") ? null : getConfig().getString("find-effect.chest");
            String leg = getConfig().getString("find-effect.leg").equalsIgnoreCase("NONE") ? null : getConfig().getString("find-effect.leg");
            String boot = getConfig().getString("find-effect.boot").equalsIgnoreCase("NONE") ? null : getConfig().getString("find-effect.boot");
            ItemStack h = null;
            ItemStack c = null;
            ItemStack l = null;
            ItemStack b = null;
            if(head != null) {
                if(head.length() > 45) {
                    h = Skulls.createSkull(head);
                } else {
                    h = new ItemStack(Material.valueOf(head));
                }
            }
            if(chest != null) {
                c = new ItemStack(Material.valueOf(chest));
            }
            if(leg != null) {
                l = new ItemStack(Material.valueOf(leg));
            }
            if(boot != null) {
                b = new ItemStack(Material.valueOf(boot));
            }
            findEffectC = new FindEffect(h, c, l, b, visible, small, getConfig().getString("find-effect.custom-name"));
        }
        getCommand("blockquest").setExecutor(new BlockQuestCommand(this));
        getCommand("blockquest").setTabCompleter(new BlockQuestTab());

        // TEST START
       /* IBlockQuest bq = new IBlockQuest() {
            @Override
            public void onBlockFindSuccess(Player p, HiddenBlock hb) {
                p.sendMessage("You found a block");
                p.getInventory().addItem(new ItemStack(Material.GOLD_INGOT));
            }

            @Override
            public void blockAlreadyFound(Player p, HiddenBlock hb) {
                p.sendMessage("You already found this block");
            }

            @Override
            public void alreadyFoundAllBlocks(Player p, HiddenBlock hb) {
                p.sendMessage("You already found all blocks");
            }

            @Override
            public void foundAllBlocks(Player p, HiddenBlock hb) {
                p.sendMessage("You found all blocks");
                p.getInventory().addItem(new ItemStack(Material.DIAMOND));
            }
        };
        bq.registerBlock(new Location(Bukkit.getWorld("world"), 0, 0, 0));
        BlockQuestAPI.getInstance().registerQuest(bq);*/
        // TEST END
    }

    public void createMySQL() {
        mysql.update("CREATE TABLE IF NOT EXISTS " + this.getDescription().getName() + " (UUID varchar(128), X varchar(2048) default \"none\", Y varchar(2048) default \"none\", Z varchar(2048) default \"none\", WORLD varchar(2048) default \"none\")");
    }

    public void onDisable() {
        for(Player pl : Bukkit.getOnlinePlayers()) {
            if(saved_x.get(pl.getName()) != null) {
                if (useMysql) {
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
    }

    public static Main getPlugin() {
        return plugin;
    }
}
