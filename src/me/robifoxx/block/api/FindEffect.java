package me.robifoxx.block.api;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

public class FindEffect {
    private ItemStack head;
    private ItemStack chest;
    private ItemStack leg;
    private ItemStack boot;
    private String name;
    private boolean visible;
    private boolean small;
    public FindEffect(ItemStack head, ItemStack chest, ItemStack leg, ItemStack boot, boolean visible, boolean small) {
        this.head = head;
        this.chest = chest;
        this.leg = leg;
        this.boot = boot;
        this.visible = visible;
        this.small = small;
        name = "";
    }
    public FindEffect(ItemStack head, ItemStack chest, ItemStack leg, ItemStack boot, boolean visible, boolean small, String name) {
        this.head = head;
        this.chest = chest;
        this.leg = leg;
        this.boot = boot;
        this.name = name;
        this.visible = visible;
        this.small = small;
    }

    public ItemStack getBoot() {
        return boot;
    }

    public ItemStack getChest() {
        return chest;
    }

    public ItemStack getHead() {
        return head;
    }

    public ItemStack getLeg() {
        return leg;
    }

    public void setBoot(ItemStack boot) {
        this.boot = boot;
    }

    public void setChest(ItemStack chest) {
        this.chest = chest;
    }

    public void setHead(ItemStack head) {
        this.head = head;
    }

    public void setLeg(ItemStack leg) {
        this.leg = leg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSmall() {
        return small;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setSmall(boolean small) {
        this.small = small;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public ArmorStand getArmorStand(Location l) {
        ArmorStand a = l.getWorld().spawn(l, ArmorStand.class);
        boolean visible = isVisible();
        boolean small = isSmall();
        a.setVisible(visible);
        a.setSmall(small);
        //a.setInvulnerable(true); // 1.8 :(
        StringBuilder name = new StringBuilder("§b§l§o§c§k");
        if(getName().length() >= 1) {
            name.append(getName().replace("&", "§"));
            a.setCustomNameVisible(true);
        } else {
            a.setCustomNameVisible(false);
        }
        a.setCustomName(name.toString());
        a.setGravity(false);
        a.setHelmet(getHead());
        a.setChestplate(getChest());
        a.setLeggings(getLeg());
        a.setBoots(getBoot());
        return a;
    }
}
