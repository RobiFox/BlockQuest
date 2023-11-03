package me.robifoxx.blockquest.api;

import me.robifoxx.blockquest.BlockQuest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The head that floats when you find a block.
 */
public class FindEffect {
    private ItemStack[] equipment;
    private boolean small;
    private FindEffect.ParticleData particleData;
    private FindEffect.MovementData movementData;

    private FindEffect.Event beginEvent;
    private FindEffect.Event endEvent;

    private final BlockQuest plugin;

    /**
     * The constructor for the FindEffect class
     *
     * @param plugin The plugin that will handle the scheduler
     * @param equipment The equipment the FindEffect will have
     * @param small True if the armor stand should be a small one.
     * @param particleData Particle data
     * @param movementData Movement Data
     * @param beginEvent This interface's method will be called when the FindEffect appears
     * @param endEvent This interface's method will be called when the FindEffect disappears
     */
    public FindEffect(BlockQuest plugin, ItemStack[] equipment, boolean small, FindEffect.ParticleData particleData, FindEffect.MovementData movementData, FindEffect.Event beginEvent, FindEffect.Event endEvent) {
        this.equipment = equipment;
        this.small = small;
        this.particleData = particleData;
        this.movementData = movementData;

        this.beginEvent = beginEvent;
        this.endEvent = endEvent;

        this.plugin = plugin;
    }

    public MovementData getMovementData() {
        return movementData;
    }

    public ParticleData getParticleData() {
        return particleData;
    }

    public void setMovementData(MovementData movementData) {
        this.movementData = movementData;
    }

    public void setParticleData(ParticleData particleData) {
        this.particleData = particleData;
    }

    public boolean isSmall() {
        return small;
    }

    public Event getBeginEvent() {
        return beginEvent;
    }

    public Event getEndEvent() {
        return endEvent;
    }

    public ItemStack[] getEquipment() {
        return equipment;
    }

    public void setBeginEvent(Event beginEvent) {
        this.beginEvent = beginEvent;
    }

    public void setEndEvent(Event endEvent) {
        this.endEvent = endEvent;
    }

    public void setSmall(boolean small) {
        this.small = small;
    }

    public void setEquipment(ItemStack[] equipment) {
        this.equipment = equipment;
    }

    /**
     * This gets called when the Find Effect should be played.
     * Handles everything.
     * @param finder The player who found the block
     * @param blockLocation The location of the block.
     */
    public void create(Player finder, Location blockLocation) {
        MovementData movementData = getMovementData();
        ParticleData particleData = getParticleData();
        ArmorStand a = blockLocation.getWorld().spawn(blockLocation.clone().add(0.5, movementData.getOffset(), 0.5), ArmorStand.class, armor -> {
            armor.setGravity(false);
            armor.setInvulnerable(true);
            armor.setVisible(false);
            armor.getEquipment().setArmorContents(getEquipment());
            armor.setMarker(true);
            armor.setSmall(isSmall());
        });
        if(beginEvent != null) beginEvent.run(finder, a, blockLocation);
        for(int i = 0; i < movementData.getTickAmount(); i++) {
            int finalI = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location location = a.getLocation();
                location.add(0, movementData.getFloatPerTick(), 0);
                location.setYaw(location.getYaw() + movementData.getRotatePerTick());
                a.teleport(location);
                if (particleData != null && finalI % particleData.delay == 0)
                    plugin.spawnParticle(location,particleData);
            }, i);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if(endEvent != null) endEvent.run(finder, a, blockLocation);
            a.remove();
        }, movementData.getTickAmount());
    }

    public static class ParticleData {
        private Object particle;
        private int amount;
        private double offX;
        private double offY;
        private double offZ;
        private double dx;
        private double dy;
        private double dz;
        private double speed;
        private double delay;

        /**
         * This controls the particles that the FindEffect will emit.
         * @param particle The name of particle
         * @param amount The amount of particle each time it's displayed
         * @param offX The offset of the particle's X relative to the Armor Stand
         * @param offY The offset in Y
         * @param offZ The offset in Z
         * @param dx The dx that tells the particle direction or radius if speed is 0 on X
         * @param dy Particle direction/radius on Y
         * @param dz Particle direction/radius on Z
         * @param speed The speed of the particle. 0 will spread the particle across dx, dy, dz, everything above will send towards the dx, dy, dz with the specified speed.
         * @param delay The delay between each particle.
         */
        public ParticleData(Particle particle, int amount, double offX, double offY, double offZ, double dx, double dy, double dz, double speed, double delay) {
            this.particle = particle;
            this.amount = amount;
            this.offX = offX;
            this.offY = offY;
            this.offZ = offZ;
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
            this.speed = speed;
            setDelay(delay);
        }

        public int getAmount() {
            return amount;
        }

        public double getOffX() {
            return offX;
        }

        public double getOffY() {
            return offY;
        }

        public double getOffZ() {
            return offZ;
        }

        public double getDx() {
            return dx;
        }

        public double getDy() {
            return dy;
        }

        public double getDz() {
            return dz;
        }

        public Object getParticle() {
            return particle;
        }

        public double getSpeed() {
            return speed;
        }

        public double getDelay() {
            return delay;
        }

        public void setDx(double dx) {
            this.dx = dx;
        }

        public void setDy(double dy) {
            this.dy = dy;
        }

        public void setDz(double dz) {
            this.dz = dz;
        }

        public void setOffX(double offX) {
            this.offX = offX;
        }

        public void setOffY(double offY) {
            this.offY = offY;
        }

        public void setOffZ(double offZ) {
            this.offZ = offZ;
        }

        public void setParticle(Object particle) {
            this.particle = particle;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }

        public void setDelay(double delay) {
            this.delay = Math.max(delay, 1);
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }

    public static class MovementData {
        private int tickAmount;
        private double floatPerTick;
        private float rotatePerTick;
        private double offset;

        /**
         * The movement data of the FindEffect
         * @param tickAmount This is how long the armor stand will exist (and move)
         * @param floatPerTick This is how much the armor stand will ascend each tick
         * @param rotatePerTick This is how much the armor stand will rotate each tick
         * @param offset This is how much the armor stand will be offset when it spawns
         */
        public MovementData(int tickAmount, double floatPerTick, float rotatePerTick, double offset) {
            this.tickAmount = tickAmount;
            this.floatPerTick = floatPerTick;
            this.rotatePerTick = rotatePerTick;
            this.offset = offset;
        }

        public double getFloatPerTick() {
            return floatPerTick;
        }

        public float getRotatePerTick() {
            return rotatePerTick;
        }

        public int getTickAmount() {
            return tickAmount;
        }

        public double getOffset() {
            return offset;
        }

        public void setFloatPerTick(double floatPerTick) {
            this.floatPerTick = floatPerTick;
        }

        public void setRotatePerTick(float rotatePerTick) {
            this.rotatePerTick = rotatePerTick;
        }

        public void setTickAmount(int tickAmount) {
            this.tickAmount = tickAmount;
        }

        public void setOffset(double offset) {
            this.offset = offset;
        }
    }

    /**
     * Runs at specific FindEffect events (appear, and disappear)
     */
    public interface Event {
        void run(Player finder, ArmorStand findEffectLocation, Location blockLocation);
    }
}
