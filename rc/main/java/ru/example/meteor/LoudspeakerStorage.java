package ru.example.meteor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class LoudspeakerStorage {

    private final Set<Location> locations = new HashSet<>();
    private final File file;
    private final FileConfiguration config;

    public LoudspeakerStorage(MeteorPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "loudspeakers.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        load();
    }

    public void add(Location loc) {
        locations.add(loc.getBlock().getLocation());
        save();
    }

    public void remove(Location loc) {
        locations.remove(loc.getBlock().getLocation());
        save();
    }

    public boolean contains(Location loc) {
        return locations.contains(loc.getBlock().getLocation());
    }

    public Set<Location> getAll() {
        return new HashSet<>(locations);
    }

    private void load() {
        if (config.isConfigurationSection("speakers")) {
            for (String key : config.getConfigurationSection("speakers").getKeys(false)) {
                String world = config.getString("speakers." + key + ".world");
                double x = config.getDouble("speakers." + key + ".x");
                double y = config.getDouble("speakers." + key + ".y");
                double z = config.getDouble("speakers." + key + ".z");
                if (world != null && Bukkit.getWorld(world) != null) {
                    locations.add(new Location(Bukkit.getWorld(world), x, y, z));
                }
            }
        }
    }

    private void save() {
        config.set("speakers", null);
        int i = 0;
        for (Location loc : locations) {
            String path = "speakers." + i;
            config.set(path + ".world", loc.getWorld().getName());
            config.set(path + ".x", loc.getX());
            config.set(path + ".y", loc.getY());
            config.set(path + ".z", loc.getZ());
            i++;
        }
        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Error save loudspeakers.yml: " + e.getMessage());
        }
    }
}