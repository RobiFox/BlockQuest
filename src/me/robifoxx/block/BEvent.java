package me.robifoxx.block;

import com.darkblade12.particleeffect.ParticleEffect;
import me.robifoxx.block.api.Skulls;
import me.robifoxx.block.mysql.SQLPlayer;
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
        if(!Main.getPlugin(Main.class).useMysql) {
            if (Main.getPlugin(Main.class).data.getConfig().get("data." + Utils.getIdentifier(e.getPlayer()) + ".x") == null) {
                Main.getPlugin(Main.class).data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".x", "none");
                Main.getPlugin(Main.class).data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".y", "none");
                Main.getPlugin(Main.class).data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".z", "none");
                Main.getPlugin(Main.class).data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".world", "none");
                Main.getPlugin(Main.class).data.saveConfig();
            }
        } else {
            if(!SQLPlayer.playerExists(Utils.getIdentifier(e.getPlayer())))
                SQLPlayer.createPlayer(e.getPlayer(), "none", "none", "none", "none");
        }
        if(Main.getPlugin(Main.class).blocksss.get(e.getPlayer().getName()) == null) {
            String x;
            String y;
            String z;
            String world;
            if(Main.getPlugin(Main.class).saved_x.get(e.getPlayer().getName()) != null) {
                x = Main.getPlugin(Main.class).saved_x.get(e.getPlayer().getName());
                y = Main.getPlugin(Main.class).saved_z.get(e.getPlayer().getName());
                z = Main.getPlugin(Main.class).saved_y.get(e.getPlayer().getName());
                world = Main.getPlugin(Main.class).saved_world.get(e.getPlayer().getName());
            } else {
                if(Main.getPlugin(Main.class).useMysql) {
                    x = SQLPlayer.getString(Utils.getIdentifier(e.getPlayer()), "X");
                    y = SQLPlayer.getString(Utils.getIdentifier(e.getPlayer()), "Y");
                    z = SQLPlayer.getString(Utils.getIdentifier(e.getPlayer()), "Z");
                    world = SQLPlayer.getString(Utils.getIdentifier(e.getPlayer()), "WORLD");
                } else {
                    x = Main.getPlugin(Main.class).data.getConfig().getString("data." + Utils.getIdentifier(e.getPlayer()) + ".x");
                    y = Main.getPlugin(Main.class).data.getConfig().getString("data." + Utils.getIdentifier(e.getPlayer()) + ".y");
                    z = Main.getPlugin(Main.class).data.getConfig().getString("data." + Utils.getIdentifier(e.getPlayer()) + ".z");
                    world = Main.getPlugin(Main.class).data.getConfig().getString("data." + Utils.getIdentifier(e.getPlayer()) + ".world");
                }
            }
            Main.getPlugin(Main.class).saved_x.put(e.getPlayer().getName(), x);
            Main.getPlugin(Main.class).saved_y.put(e.getPlayer().getName(), y);
            Main.getPlugin(Main.class).saved_z.put(e.getPlayer().getName(), z);
            Main.getPlugin(Main.class).saved_world.put(e.getPlayer().getName(), world);

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
            Main.getPlugin(Main.class).blocksss.put(e.getPlayer().getName(), lst);
        }
        Utils.hideFoundBlocks(e.getPlayer());
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        if(Main.getPlugin(Main.class).useMysql) {
            SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "X", Main.getPlugin(Main.class).saved_x.get(e.getPlayer().getName()));
            SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "Y", Main.getPlugin(Main.class).saved_y.get(e.getPlayer().getName()));
            SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "Z", Main.getPlugin(Main.class).saved_z.get(e.getPlayer().getName()));
            SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "WORLD", Main.getPlugin(Main.class).saved_world.get(e.getPlayer().getName()));
        } else {
            Main.getPlugin(Main.class).data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".x", Main.getPlugin(Main.class).saved_x.get(e.getPlayer().getName()));
            Main.getPlugin(Main.class).data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".y", Main.getPlugin(Main.class).saved_y.get(e.getPlayer().getName()));
            Main.getPlugin(Main.class).data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".z", Main.getPlugin(Main.class).saved_z.get(e.getPlayer().getName()));
            Main.getPlugin(Main.class).data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".world", Main.getPlugin(Main.class).saved_world.get(e.getPlayer().getName()));
            Main.getPlugin(Main.class).data.saveConfig();
        }
        Main.getPlugin(Main.class).saved_x.remove(e.getPlayer().getName());
        Main.getPlugin(Main.class).saved_y.remove(e.getPlayer().getName());
        Main.getPlugin(Main.class).saved_z.remove(e.getPlayer().getName());
        Main.getPlugin(Main.class).saved_world.remove(e.getPlayer().getName());
        Main.getPlugin(Main.class).blocksss.remove(e.getPlayer().getName());
    }

    @EventHandler
    public void click(PlayerInteractEvent e) {
        JavaPlugin plugin = Main.getProvidingPlugin(Main.class);
        /*if(e.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }*/
        if(Main.getPlugin(Main.class).eventReturn.contains(e.getPlayer().getName())) {
            return;
        }
        Main.getPlugin(Main.class).eventReturn.add(e.getPlayer().getName());
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Main.getPlugin(Main.class).eventReturn.remove(e.getPlayer().getName());
        }, 1);
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            List<String> blocks =  plugin.getConfig().getStringList("blocks");
            String block = e.getClickedBlock().getLocation().getBlockX() + ";" + e.getClickedBlock().getLocation().getBlockY() + ";" + e.getClickedBlock().getLocation().getBlockZ() + ";" + e.getClickedBlock().getLocation().getWorld().getName();
            if(Main.getPlugin(Main.class).inEdit.contains(e.getPlayer().getName())) {
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
                Main.getPlugin(Main.class).inEdit.remove(e.getPlayer().getName());
                e.getPlayer().sendMessage("§aExited edit mode.");
            } else {
                if(plugin.getConfig().getStringList("blocks").contains(block)) {
                    if(Main.getPlugin(Main.class).blocksss.get(e.getPlayer().getName()) == null
                            || !Main.getPlugin(Main.class).blocksss.get(e.getPlayer().getName()).contains(block)) {
                        if(!Main.getPlugin(Main.class).enabled) {
                            e.getPlayer().sendMessage(Main.getPlugin(Main.class).disabledMsg.replace("&", "§"));
                            return;
                        }
                        Main.getPlugin(Main.class).saved_x.put(e.getPlayer().getName(), Main.getPlugin(Main.class).saved_x.get(e.getPlayer().getName()) + ";" + e.getClickedBlock().getLocation().getBlockX());
                        Main.getPlugin(Main.class).saved_y.put(e.getPlayer().getName(), Main.getPlugin(Main.class).saved_y.get(e.getPlayer().getName()) + ";" + e.getClickedBlock().getLocation().getBlockY());
                        Main.getPlugin(Main.class).saved_z.put(e.getPlayer().getName(), Main.getPlugin(Main.class).saved_z.get(e.getPlayer().getName()) + ";" + e.getClickedBlock().getLocation().getBlockZ());
                        Main.getPlugin(Main.class).saved_world.put(e.getPlayer().getName(), Main.getPlugin(Main.class).saved_world.get(e.getPlayer().getName()) + ";" + e.getClickedBlock().getLocation().getWorld().getName());
                        if(Main.getPlugin(Main.class).blocksss.get(e.getPlayer().getName()) == null) {
                            List<String> lst = new ArrayList<>();
                            lst.add(block);
                            Main.getPlugin(Main.class).blocksss.put(e.getPlayer().getName(), lst);
                        } else {
                            Main.getPlugin(Main.class).blocksss.get(e.getPlayer().getName()).add(block);
                        }
                        if(Main.getPlugin(Main.class).useMysql) {
                            if(!Main.getPlugin(Main.class).unsafeSave) {
                                SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "X", Main.getPlugin(Main.class).saved_x.get(e.getPlayer().getName()));
                                SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "Y", Main.getPlugin(Main.class).saved_y.get(e.getPlayer().getName()));
                                SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "Z", Main.getPlugin(Main.class).saved_z.get(e.getPlayer().getName()));
                                SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "WORLD", Main.getPlugin(Main.class).saved_world.get(e.getPlayer().getName()));
                            }
                        } else {
                            Main.getPlugin(Main.class).data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".x", Main.getPlugin(Main.class).saved_x.get(e.getPlayer().getName()));
                            Main.getPlugin(Main.class).data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".y", Main.getPlugin(Main.class).saved_y.get(e.getPlayer().getName()));
                            Main.getPlugin(Main.class).data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".z", Main.getPlugin(Main.class).saved_z.get(e.getPlayer().getName()));
                            Main.getPlugin(Main.class).data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".world", Main.getPlugin(Main.class).saved_world.get(e.getPlayer().getName()));
                            Main.getPlugin(Main.class).data.saveConfig();
                        }
                        int blocksLeft = plugin.getConfig().getStringList("blocks").size() - Main.getPlugin(Main.class).blocksss.get(e.getPlayer().getName()).size();
                        boolean foundAllBlocks = Main.getPlugin(Main.class).blocksss.get(e.getPlayer().getName()).size() >= plugin.getConfig().getStringList("blocks").size();
                        if(Main.getPlugin(Main.class).checkFullInventory >= Utils.getEmptyInventorySpaces(e.getPlayer())
                                && foundAllBlocks) {
                            if(e.getPlayer().getInventory().firstEmpty() == -1) {
                                Main.getPlugin(Main.class).blocksss.get(e.getPlayer().getName()).remove(block);
                                e.getPlayer().sendMessage(Main.getPlugin(Main.class).fullInventoryMsg.replace("&", "§"));
                                return;
                            }
                        }
                        playFindEffect(e.getClickedBlock().getLocation().clone().add(0.5, 0, 0.5));
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
                            Utils.hideFoundBlocks(e.getPlayer());
                        }
                        if(foundAllBlocks) {
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
                        if(Main.getPlugin(Main.class).blocksss.get(e.getPlayer().getName()).contains(block)) {
                            int blocksLeft = plugin.getConfig().getStringList("blocks").size() - Main.getPlugin(Main.class).blocksss.get(e.getPlayer().getName()).size();
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
                            }
                            /*if(blocksLeft <= 0) {
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
                && e.getEntity().getCustomName().startsWith("§b§l§o§c§k")) {
            e.setCancelled(true);
        }
    }

    public void playFindEffect(Location l) {
        JavaPlugin plugin = Main.getProvidingPlugin(Main.class);
        if(!Main.getPlugin(Main.class).findEffect) {
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
        //a.setInvulnerable(true); // 1.8 :(
        StringBuilder name = new StringBuilder("§b§l§o§c§k");
        if(plugin.getConfig().get("find-effect.custom-name") != null
                && plugin.getConfig().getString("find-effect.custom-name").length() >= 1) {
            name.append(plugin.getConfig().getString("find-effect.custom-name").replace("&", "§"));
        }
        a.setCustomName(name.toString());
        a.setCustomNameVisible(false);
        a.setGravity(false);
        if(!plugin.getConfig().getString("find-effect.sound").equalsIgnoreCase("DISABLED")
                || !plugin.getConfig().getString("find-effect.sound").equalsIgnoreCase("NONE")) {
            a.getWorld().playSound(a.getLocation(), Sound.valueOf(plugin.getConfig().getString("find-effect.sound")), 1, plugin.getConfig().getInt("find-effect.sound-pitch"));
        }
        if(head != null) {
            if(head.length() > 45) {
                a.setHelmet(Skulls.createSkull(head));
            } else {
                a.setHelmet(new ItemStack(Material.valueOf(head)));
            }
        }
        if(chest != null) {
            a.setChestplate(new ItemStack(Material.valueOf(chest)));
        }
        if(leg != null) {
            a.setLeggings(new ItemStack(Material.valueOf(leg)));
        }
        if(boot != null) {
            a.setBoots(new ItemStack(Material.valueOf(boot)));
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
