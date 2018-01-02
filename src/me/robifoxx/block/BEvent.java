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
    private Main m;
    public BEvent(Main m) {
        this.m = m;
    }
    @EventHandler
    public void join(PlayerJoinEvent e) {
        if(!m.useMysql) {
            if (m.data.getConfig().get("data." + Utils.getIdentifier(e.getPlayer()) + ".x") == null) {
                m.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".x", "none");
                m.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".y", "none");
                m.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".z", "none");
                m.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".world", "none");
                m.data.saveConfig();
            }
        } else {
            if(!SQLPlayer.playerExists(Utils.getIdentifier(e.getPlayer())))
                SQLPlayer.createPlayer(e.getPlayer(), "none", "none", "none", "none");
        }
        if(m.blocksss.get(e.getPlayer().getName()) == null) {
            String x;
            String y;
            String z;
            String world;
            if(m.saved_x.get(e.getPlayer().getName()) != null) {
                x = m.saved_x.get(e.getPlayer().getName());
                y = m.saved_z.get(e.getPlayer().getName());
                z = m.saved_y.get(e.getPlayer().getName());
                world = m.saved_world.get(e.getPlayer().getName());
            } else {
                if(m.useMysql) {
                    x = SQLPlayer.getString(Utils.getIdentifier(e.getPlayer()), "X");
                    y = SQLPlayer.getString(Utils.getIdentifier(e.getPlayer()), "Y");
                    z = SQLPlayer.getString(Utils.getIdentifier(e.getPlayer()), "Z");
                    world = SQLPlayer.getString(Utils.getIdentifier(e.getPlayer()), "WORLD");
                } else {
                    x = m.data.getConfig().getString("data." + Utils.getIdentifier(e.getPlayer()) + ".x");
                    y = m.data.getConfig().getString("data." + Utils.getIdentifier(e.getPlayer()) + ".y");
                    z = m.data.getConfig().getString("data." + Utils.getIdentifier(e.getPlayer()) + ".z");
                    world = m.data.getConfig().getString("data." + Utils.getIdentifier(e.getPlayer()) + ".world");
                }
            }
            m.saved_x.put(e.getPlayer().getName(), x);
            m.saved_y.put(e.getPlayer().getName(), y);
            m.saved_z.put(e.getPlayer().getName(), z);
            m.saved_world.put(e.getPlayer().getName(), world);

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
            m.blocksss.put(e.getPlayer().getName(), lst);
        }
        Utils.hideFoundBlocks(e.getPlayer());
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        if(m.useMysql) {
            SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "X", m.saved_x.get(e.getPlayer().getName()));
            SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "Y", m.saved_y.get(e.getPlayer().getName()));
            SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "Z", m.saved_z.get(e.getPlayer().getName()));
            SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "WORLD", m.saved_world.get(e.getPlayer().getName()));
        } else {
            m.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".x", m.saved_x.get(e.getPlayer().getName()));
            m.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".y", m.saved_y.get(e.getPlayer().getName()));
            m.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".z", m.saved_z.get(e.getPlayer().getName()));
            m.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".world", m.saved_world.get(e.getPlayer().getName()));
            m.data.saveConfig();
        }
        m.saved_x.remove(e.getPlayer().getName());
        m.saved_y.remove(e.getPlayer().getName());
        m.saved_z.remove(e.getPlayer().getName());
        m.saved_world.remove(e.getPlayer().getName());
        m.blocksss.remove(e.getPlayer().getName());
    }

    @EventHandler
    public void click(PlayerInteractEvent e) {
        /*if(e.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }*/
        if(m.eventReturn.contains(e.getPlayer().getName())) {
            return;
        }
        m.eventReturn.add(e.getPlayer().getName());
        Bukkit.getScheduler().scheduleSyncDelayedTask(m, () -> {
            m.eventReturn.remove(e.getPlayer().getName());
        }, 1);
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            List<String> blocks =  m.getConfig().getStringList("blocks");
            String block = e.getClickedBlock().getLocation().getBlockX() + ";" + e.getClickedBlock().getLocation().getBlockY() + ";" + e.getClickedBlock().getLocation().getBlockZ() + ";" + e.getClickedBlock().getLocation().getWorld().getName();
            if(m.inEdit.contains(e.getPlayer().getName())) {
                if(blocks.contains(block)) {
                    e.getPlayer().sendMessage("§cRemoved this block!");
                    blocks.remove(block);
                    m.getConfig().set("blocks", blocks);
                    m.saveConfig();
                } else {
                    e.getPlayer().sendMessage("§aAdded this block!");
                    blocks.add(block);
                    m.getConfig().set("blocks", blocks);
                    m.saveConfig();
                }
                m.inEdit.remove(e.getPlayer().getName());
                e.getPlayer().sendMessage("§aExited edit mode.");
            } else {
                if(m.getConfig().getStringList("blocks").contains(block)) {
                    if(m.blocksss.get(e.getPlayer().getName()) == null
                            || !m.blocksss.get(e.getPlayer().getName()).contains(block)) {
                        if(!m.enabled) {
                            e.getPlayer().sendMessage(m.disabledMsg.replace("&", "§"));
                            return;
                        }
                        m.saved_x.put(e.getPlayer().getName(), m.saved_x.get(e.getPlayer().getName()) + ";" + e.getClickedBlock().getLocation().getBlockX());
                        m.saved_y.put(e.getPlayer().getName(), m.saved_y.get(e.getPlayer().getName()) + ";" + e.getClickedBlock().getLocation().getBlockY());
                        m.saved_z.put(e.getPlayer().getName(), m.saved_z.get(e.getPlayer().getName()) + ";" + e.getClickedBlock().getLocation().getBlockZ());
                        m.saved_world.put(e.getPlayer().getName(), m.saved_world.get(e.getPlayer().getName()) + ";" + e.getClickedBlock().getLocation().getWorld().getName());
                        if(m.blocksss.get(e.getPlayer().getName()) == null) {
                            List<String> lst = new ArrayList<>();
                            lst.add(block);
                            m.blocksss.put(e.getPlayer().getName(), lst);
                        } else {
                            m.blocksss.get(e.getPlayer().getName()).add(block);
                        }
                        if(m.useMysql) {
                            if(!m.unsafeSave) {
                                SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "X", m.saved_x.get(e.getPlayer().getName()));
                                SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "Y", m.saved_y.get(e.getPlayer().getName()));
                                SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "Z", m.saved_z.get(e.getPlayer().getName()));
                                SQLPlayer.setString(Utils.getIdentifier(e.getPlayer()), "WORLD", m.saved_world.get(e.getPlayer().getName()));
                            }
                        } else {
                            m.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".x", m.saved_x.get(e.getPlayer().getName()));
                            m.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".y", m.saved_y.get(e.getPlayer().getName()));
                            m.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".z", m.saved_z.get(e.getPlayer().getName()));
                            m.data.getConfig().set("data." + Utils.getIdentifier(e.getPlayer()) + ".world", m.saved_world.get(e.getPlayer().getName()));
                            m.data.saveConfig();
                        }
                        int blocksLeft = m.getConfig().getStringList("blocks").size() - m.blocksss.get(e.getPlayer().getName()).size();
                        boolean foundAllBlocks = m.blocksss.get(e.getPlayer().getName()).size() >= m.getConfig().getStringList("blocks").size();
                        if(m.checkFullInventory >= Utils.getEmptyInventorySpaces(e.getPlayer())
                                && foundAllBlocks) {
                            if(e.getPlayer().getInventory().firstEmpty() == -1) {
                                m.blocksss.get(e.getPlayer().getName()).remove(block);
                                e.getPlayer().sendMessage(m.fullInventoryMsg.replace("&", "§"));
                                return;
                            }
                        }
                        playFindEffect(e.getClickedBlock().getLocation().clone().add(0.5, 0, 0.5));
                        for(String s : m.getConfig().getStringList("find-block-commands")) {
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
                            for(String s : m.getConfig().getStringList("all-blocks-found-commands")) {
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
                        if(m.blocksss.get(e.getPlayer().getName()).contains(block)) {
                            int blocksLeft = m.getConfig().getStringList("blocks").size() - m.blocksss.get(e.getPlayer().getName()).size();
                            if(blocksLeft <= 0) {
                                for(String s : m.getConfig().getStringList("already-found-all-blocks")) {
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
                                for(String s : m.getConfig().getStringList("already-found-commands")) {
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

    @EventHandler
    public void dmg(EntityDamageEvent e) {
        if(e.getEntity().getCustomName() != null
                && e.getEntity().getCustomName().startsWith("§b§l§o§c§k")) {
            e.setCancelled(true);
        }
    }

    public void playFindEffect(Location l) {
        if(!m.findEffect) {
            return;
        }
        boolean visible = !m.getConfig().getBoolean("find-effect.invisible");
        boolean small = m.getConfig().getBoolean("find-effect.small");
        double offset = 0.25;
        if(m.getConfig().get("find-effect.y-start") != null) {
            offset = m.getConfig().getDouble("find-effect.y-start");
        }
        String head = m.getConfig().getString("find-effect.head").equalsIgnoreCase("NONE") ? null : m.getConfig().getString("find-effect.head");
        String chest = m.getConfig().getString("find-effect.chest").equalsIgnoreCase("NONE") ? null : m.getConfig().getString("find-effect.chest");
        String leg = m.getConfig().getString("find-effect.leg").equalsIgnoreCase("NONE") ? null : m.getConfig().getString("find-effect.leg");
        String boot = m.getConfig().getString("find-effect.boot").equalsIgnoreCase("NONE") ? null : m.getConfig().getString("find-effect.boot");
        ArmorStand a = l.getWorld().spawn(l.clone().add(0, offset, 0), ArmorStand.class);
        a.setVisible(visible);
        a.setSmall(small);
        //a.setInvulnerable(true); // 1.8 :(
        StringBuilder name = new StringBuilder("§b§l§o§c§k");
        if(m.getConfig().get("find-effect.custom-name") != null
                && m.getConfig().getString("find-effect.custom-name").length() >= 1) {
            name.append(m.getConfig().getString("find-effect.custom-name").replace("&", "§"));
        }
        a.setCustomName(name.toString());
        a.setCustomNameVisible(false);
        a.setGravity(false);
        if(!m.getConfig().getString("find-effect.sound").equalsIgnoreCase("DISABLED")
                || !m.getConfig().getString("find-effect.sound").equalsIgnoreCase("NONE")) {
            a.getWorld().playSound(a.getLocation(), Sound.valueOf(m.getConfig().getString("find-effect.sound")), 1, m.getConfig().getInt("find-effect.sound-pitch"));
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
        for(int i = 0; i < m.getConfig().getInt("find-effect.loop"); i++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(m, () -> {
                Location newLoc = a.getLocation().clone();
                newLoc.add(0.0, m.getConfig().getDouble("find-effect.levitation-per-loop"), 0.0);
                newLoc.setYaw(a.getLocation().getYaw() + m.getConfig().getInt("find-effect.yaw-rotation"));
                a.teleport(newLoc);
                String particle = m.getConfig().getString("find-effect.particle");
                if(!particle.equalsIgnoreCase("DISABLED")) {
                    ParticleEffect.valueOf(particle).display(0, 0, 0, 0, 1, a.getLocation(), 16);
                }
            }, i * m.getConfig().getInt("find-effect.scheduler"));
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(m, () -> {
            if(m.getConfig().get("find-effect.disappear-commands.enabled") != null
                    && m.getConfig().getBoolean("find-effect.disappear-commands.enabled")) {
                for(String s : m.getConfig().getStringList("find-effect.disappear-commands.commands")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%locX%", a.getLocation().getX() + "").replace("%locY%", a.getLocation().getY() + "").replace("%locZ%", a.getLocation().getZ() + ""));
                }
            }
            a.remove();
        }, m.getConfig().getInt("find-effect.loop") * m.getConfig().getInt("find-effect.scheduler"));
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
