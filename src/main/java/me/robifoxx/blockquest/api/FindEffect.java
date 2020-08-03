package me.robifoxx.blockquest.api;

import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

/**
 * The head that floats when you find a block.
 */
public class FindEffect {
    public FindEffect(ItemStack head, ItemStack chest, ItemStack leg, ItemStack boot, FindEffect.ParticleData particleData, FindEffect.MovementData movementData) {

    }

    public static class ParticleData {
        private Particle particle;
        private double offX;
        private double offY;
        private double offZ;
        private double dx;
        private double dy;
        private double dz;
        private double speed;

        public ParticleData(Particle particle, double offX, double offY, double offZ, double dx, double dy, double dz, double speed) {
            this.particle = particle;
            this.offX = offX;
            this.offY = offY;
            this.offZ = offZ;
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
            this.speed = speed;
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

        public Particle getParticle() {
            return particle;
        }

        public double getSpeed() {
            return speed;
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

        public void setParticle(Particle particle) {
            this.particle = particle;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }
    }

    public static class MovementData {
        private double tickAmount;
        private double floatPerTick;
        private double rotatePerTick;

        public MovementData(double tickAmount, double floatPerTick, double rotatePerTick) {
            this.tickAmount = tickAmount;
            this.floatPerTick = floatPerTick;
            this.rotatePerTick = rotatePerTick;
        }
    }
}
