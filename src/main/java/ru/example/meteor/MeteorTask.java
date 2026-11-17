package ru.example.meteor;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MeteorTask extends BukkitRunnable {
    private final MeteorPlugin plugin;
    private final Location target;
    private BlockDisplay display;
    private float rotation = 0f;
    private boolean started = false;

    public MeteorTask(MeteorPlugin plugin, Location target) {
        this.plugin = plugin;
        this.target = target.clone();
    }

    @Override
    public void run() {
        if (!started) {
            spawnDisplay();
            started = true;
            return;
        }
        if (display == null || !display.isValid()) {
            cancel();
            return;
        }

        Location loc = display.getLocation();
        double speed = Math.max(0.05, plugin.getConfig().getDouble("meteor.fall-speed", 2.0));
        loc.subtract(0, speed, 0);
        display.teleport(loc);

        rotation += (float) plugin.getConfig().getDouble("meteor.rotation-speed", 15.0);
        float size = (float) Math.max(0.1, plugin.getConfig().getDouble("meteor.size", 3.0));
        display.setTransformation(new Transformation(
                new Vector3f(0, 0, 0),
                new Quaternionf().rotationY((float) Math.toRadians(rotation)),
                new Vector3f(size, size, size),
                new Quaternionf()
        ));

        loc.getWorld().spawnParticle(Particle.FLAME, loc, 10, size * 0.5, size * 0.5, size * 0.5, 0.02);
        loc.getWorld().spawnParticle(Particle.LAVA, loc, 3, size * 0.3, size * 0.3, size * 0.3);

        if (!loc.getBlock().getType().isAir() ||
                !loc.clone().subtract(0, 1, 0).getBlock().getType().isAir()) {
            impact(loc);
            cancel();
        }
    }

    private void spawnDisplay() {
        double height = Math.max(1, plugin.getConfig().getDouble("meteor.spawn-height", 120.0));
        float size = (float) Math.max(0.1, plugin.getConfig().getDouble("meteor.size", 3.0));
        Location spawn = target.clone().add(0, height, 0);
        display = target.getWorld().spawn(spawn, BlockDisplay.class, d -> {
            d.setBlock(Material.MAGMA_BLOCK.createBlockData());
            d.setBillboard(Display.Billboard.FIXED);
            d.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0),
                    new Quaternionf(),
                    new Vector3f(size, size, size),
                    new Quaternionf()
            ));
        });
    }

    private void impact(Location loc) {
        if (display != null) display.remove();
        float power = (float) Math.max(0, plugin.getConfig().getDouble("meteor.explosion-power", 4.0));
        loc.getWorld().createExplosion(loc, power, true, true);

        int magmaRadius = Math.max(0, plugin.getConfig().getInt("meteor.magma-radius", 5));
        int fireRadius = Math.max(0, plugin.getConfig().getInt("meteor.fire-radius", 5));
        double magmaChance = clamp(plugin.getConfig().getDouble("meteor.magma-chance", 0.30));
        double fireChance = clamp(plugin.getConfig().getDouble("meteor.fire-chance", 0.40));

        for (int x = -magmaRadius; x <= magmaRadius; x++) {
            for (int z = -magmaRadius; z <= magmaRadius; z++) {
                Location l = loc.clone().add(x, 0, z);
                if (l.getBlock().getType().isAir() &&
                        l.clone().subtract(0, 1, 0).getBlock().getType().isSolid() &&
                        Math.random() < magmaChance) {
                    l.getBlock().setType(Material.MAGMA_BLOCK);
                }
            }
        }
        for (int x = -fireRadius; x <= fireRadius; x++) {
            for (int z = -fireRadius; z <= fireRadius; z++) {
                Location l = loc.clone().add(x, 0, z);
                if (l.getBlock().getType().isAir() &&
                        l.clone().subtract(0, 1, 0).getBlock().getType().isSolid() &&
                        Math.random() < fireChance) {
                    l.getBlock().setType(Material.FIRE);
                }
            }
        }
    }

    private double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
