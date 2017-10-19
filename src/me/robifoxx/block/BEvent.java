package me.robifoxx.block;

import com.darkblade12.particleeffect.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class BEvent implements Listener {
    @EventHandler
    public void join(PlayerJoinEvent e) {
        if(Main.data.getConfig().get("data." + Utils.getIdentifier(e.getPlayer()) + ".x") == null) {
            Main.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".x", "none");
            Main.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".y", "none");
            Main.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".z", "none");
            Main.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".world", "none");
            Main.data.saveConfig();
        }
        if(Main.blocksss.get(e.getPlayer().getName()) == null) {
            String x;
            String y;
            String z;
            String world;
            if(Main.saved_x.get(e.getPlayer().getName()) != null) {
                x = Main.saved_x.get(e.getPlayer().getName());
                y = Main.saved_z.get(e.getPlayer().getName());
                z = Main.saved_y.get(e.getPlayer().getName());
                world = Main.saved_world.get(e.getPlayer().getName());
            } else {
                if(Main.useMysql) {
                    x = SQLPlayer.getString(Utils.getIdentifier(e.getPlayer()), "X");
                    y = SQLPlayer.getString(Utils.getIdentifier(e.getPlayer()), "Y");
                    z = SQLPlayer.getString(Utils.getIdentifier(e.getPlayer()), "Z");
                    world = SQLPlayer.getString(Utils.getIdentifier(e.getPlayer()), "WORLD");
                } else {
                    x = Main.data.getConfig().getString("data." + Utils.getIdentifier(e.getPlayer()) + ".x");
                    y = Main.data.getConfig().getString("data." + Utils.getIdentifier(e.getPlayer()) + ".y");
                    z = Main.data.getConfig().getString("data." + Utils.getIdentifier(e.getPlayer()) + ".z");
                    world = Main.data.getConfig().getString("data." + Utils.getIdentifier(e.getPlayer()) + ".world");
                }
            }
            Main.saved_x.put(e.getPlayer().getName(), x);
            Main.saved_y.put(e.getPlayer().getName(), y);
            Main.saved_z.put(e.getPlayer().getName(), z);
            Main.saved_world.put(e.getPlayer().getName(), world);

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
            Main.blocksss.put(e.getPlayer().getName(), lst);
        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        if(Main.useMysql) {
            SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "X", Main.saved_x.get(e.getPlayer().getName()));
            SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "Y", Main.saved_y.get(e.getPlayer().getName()));
            SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "Z", Main.saved_z.get(e.getPlayer().getName()));
            SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "WORLD", Main.saved_world.get(e.getPlayer().getName()));
        } else {
            Main.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".x", Main.saved_x.get(e.getPlayer().getName()));
            Main.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".y", Main.saved_y.get(e.getPlayer().getName()));
            Main.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".z", Main.saved_z.get(e.getPlayer().getName()));
            Main.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".world", Main.saved_world.get(e.getPlayer().getName()));
            Main.data.saveConfig();
        }
    }

    @EventHandler
    public void click(PlayerInteractEvent e) {
        JavaPlugin plugin = Main.getProvidingPlugin(Main.class);
        /*if(e.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }*/
        if(Main.eventReturn.contains(e.getPlayer().getName())) {
            return;
        }
        Main.eventReturn.add(e.getPlayer().getName());
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Main.eventReturn.remove(e.getPlayer().getName());
        }, 1);
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            List<String> blocks =  plugin.getConfig().getStringList("blocks");
            String block = e.getClickedBlock().getLocation().getBlockX() + ";" + e.getClickedBlock().getLocation().getBlockY() + ";" + e.getClickedBlock().getLocation().getBlockZ() + ";" + e.getClickedBlock().getLocation().getWorld().getName();
            if(Main.inEdit.contains(e.getPlayer().getName())) {
                if(blocks.contains(block)) {
                    e.getPlayer().sendMessage("§cRemoved this block!");
                    blocks.remove(block);
                    plugin.getConfig().set("blocks", blocks);
                    plugin.saveConfig();
                } else {
                    e.getPlayer().sendMessage("§aAdded this block!");
                    blocks.add(block);
                    plugin.getConfig().set("blocks", blocks);
                    plugin.saveConfig();
                }
                Main.inEdit.remove(e.getPlayer().getName());
                e.getPlayer().sendMessage("§aExited edit mode.");
            } else {
                if(plugin.getConfig().getStringList("blocks").contains(block)) {
                    if(Main.blocksss.get(e.getPlayer().getName()) == null
                            || !Main.blocksss.get(e.getPlayer().getName()).contains(block)) {
                        if(!Main.enabled) {
                            e.getPlayer().sendMessage(Main.disabledMsg.replace("&", "§"));
                            return;
                        }
                        Main.saved_x.put(e.getPlayer().getName(), Main.saved_x.get(e.getPlayer().getName()) + ";" + e.getClickedBlock().getLocation().getBlockX());
                        Main.saved_y.put(e.getPlayer().getName(), Main.saved_y.get(e.getPlayer().getName()) + ";" + e.getClickedBlock().getLocation().getBlockY());
                        Main.saved_z.put(e.getPlayer().getName(), Main.saved_z.get(e.getPlayer().getName()) + ";" + e.getClickedBlock().getLocation().getBlockZ());
                        Main.saved_world.put(e.getPlayer().getName(), Main.saved_world.get(e.getPlayer().getName()) + ";" + e.getClickedBlock().getLocation().getWorld().getName());
                        if(Main.blocksss.get(e.getPlayer().getName()) == null) {
                            List<String> lst = new ArrayList<>();
                            lst.add(block);
                            Main.blocksss.put(e.getPlayer().getName(), lst);
                        } else {
                            Main.blocksss.get(e.getPlayer().getName()).add(block);
                        }
                        if(Main.useMysql) {
                            if(!Main.unsafeSave) {
                                SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "X", Main.saved_x.get(e.getPlayer().getName()));
                                SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "Y", Main.saved_y.get(e.getPlayer().getName()));
                                SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "Z", Main.saved_z.get(e.getPlayer().getName()));
                                SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "WORLD", Main.saved_world.get(e.getPlayer().getName()));
                            }
                        } else {
                            Main.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".x", Main.saved_x.get(e.getPlayer().getName()));
                            Main.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".y", Main.saved_y.get(e.getPlayer().getName()));
                            Main.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".z", Main.saved_z.get(e.getPlayer().getName()));
                            Main.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".world", Main.saved_world.get(e.getPlayer().getName()));
                            Main.data.saveConfig();
                        }
                        playFindEffect(e.getClickedBlock().getLocation().clone().add(0.5, 0, 0.5));
                        int blocksLeft = plugin.getConfig().getStringList("blocks").size() - Main.blocksss.get(e.getPlayer().getName()).size();
                        for(String s : plugin.getConfig().getStringList("find-block-commands")) {
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
                        if(Main.blocksss.get(e.getPlayer().getName()).size() >= plugin.getConfig().getStringList("blocks").size()) {
                            for(String s : plugin.getConfig().getStringList("all-blocks-found-commands")) {
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
                        if(Main.blocksss.get(e.getPlayer().getName()).contains(block)) {
                            int blocksLeft = plugin.getConfig().getStringList("blocks").size() - Main.blocksss.get(e.getPlayer().getName()).size();
                            if(blocksLeft <= 0) {
                                for(String s : plugin.getConfig().getStringList("already-found-all-blocks")) {
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
                            /*int blocksLeft = plugin.getConfig().getStringList("blocks").size() - Main.blocksss.get(e.getPlayer().getName()).size();
                            if(blocksLeft <= 0) {
                                for(String s : plugin.getConfig().getStringList("already-found-all-blocks")) {
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
                                for(String s : plugin.getConfig().getStringList("already-found-commands")) {
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
                            }*/
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void dmg(EntityDamageEvent e) {
        if(e.getEntity().getCustomName() != null
                && e.getEntity().getCustomName().equalsIgnoreCase("§b§l§o§c§k")) {
            e.setCancelled(true);
        }
    }

    public void playFindEffect(Location l) {
        JavaPlugin plugin = Main.getProvidingPlugin(Main.class);
        if(!Main.findEffect) {
            return;
        }
        boolean visible = !plugin.getConfig().getBoolean("find-effect.invisible");
        boolean small = plugin.getConfig().getBoolean("find-effect.small");
        double offset = 0.25;
        if(plugin.getConfig().get("find-effect.y-start") != null) {
            offset = plugin.getConfig().getDouble("find-effect.y-start");
        }
        String head = plugin.getConfig().getString("find-effect.head").equalsIgnoreCase("NONE") ? null : plugin.getConfig().getString("find-effect.head");
        String chest = plugin.getConfig().getString("find-effect.chest").equalsIgnoreCase("NONE") ? null : plugin.getConfig().getString("find-effect.chest");
        String leg = plugin.getConfig().getString("find-effect.leg").equalsIgnoreCase("NONE") ? null : plugin.getConfig().getString("find-effect.leg");
        String boot = plugin.getConfig().getString("find-effect.boot").equalsIgnoreCase("NONE") ? null : plugin.getConfig().getString("find-effect.boot");
        ArmorStand a = l.getWorld().spawn(l.clone().add(0, offset, 0), ArmorStand.class);
        a.setVisible(visible);
        a.setSmall(small);
        //a.setInvulnerable(true);
        a.setCustomName("§b§l§o§c§k");
        a.setCustomNameVisible(false);
        a.setGravity(false);
        a.getWorld().playSound(a.getLocation(), Sound.valueOf(plugin.getConfig().getString("find-effect.sound")), 1, plugin.getConfig().getInt("find-effect.pitch"));
        if(head != null) {
            a.setHelmet(new ItemStack(Material.valueOf(head)));
        }
        if(chest != null) {
            a.setHelmet(new ItemStack(Material.valueOf(chest)));
        }
        if(leg != null) {
            a.setHelmet(new ItemStack(Material.valueOf(leg)));
        }
        if(boot != null) {
            a.setHelmet(new ItemStack(Material.valueOf(boot)));
        }
        for(int i = 0; i < plugin.getConfig().getInt("find-effect.loop"); i++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location newLoc = a.getLocation().clone();
                newLoc.add(0.0, plugin.getConfig().getDouble("find-effect.levitation-per-loop"), 0.0);
                newLoc.setYaw(a.getLocation().getYaw() + plugin.getConfig().getInt("find-effect.yaw-rotation"));
                a.teleport(newLoc);
                String particle = plugin.getConfig().getString("find-effect.particle");
                if(!particle.equalsIgnoreCase("DISABLED")) {
                    ParticleEffect.valueOf(particle).display(0, 0, 0, 0, 1, a.getLocation(), 16);
                }
            }, i * plugin.getConfig().getInt("find-effect.scheduler"));
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, a::remove, plugin.getConfig().getInt("find-effect.loop") * plugin.getConfig().getInt("find-effect.scheduler"));
    }

    @EventHandler
    public void rl(PlayerCommandPreprocessEvent e) {
        if(e.getMessage().equalsIgnoreCase("/rl")
                || e.getMessage().equalsIgnoreCase("/reload")) {
            if(e.getPlayer().isOp()) {
                e.getPlayer().sendMessage("§c[§2B§alockQuest§c] DO NOT reload!");
                e.getPlayer().sendMessage("§c[§2B§alockQuest§c] Use restart instead, as reload messes up player stats.");
            }
        }
    }
}
