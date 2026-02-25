package me.Asi.petCoreEngine.managers.zone;

import me.Asi.petCoreEngine.PetCoreEngine;
import me.Asi.petCoreEngine.models.CoinZone;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ZoneManager {

    private final PetCoreEngine plugin;
    private final Map<String, CoinZone> zones = new HashMap<>();

    private File file;
    private FileConfiguration config;

    public ZoneManager(PetCoreEngine plugin) {
        this.plugin = plugin;
        load();
    }

    /* =====================
       LOAD FILE
       ===================== */

    public void load() {

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        file = new File(plugin.getDataFolder(), "zones.yaml");

        if (!file.exists()) {
            plugin.saveResource("zones.yaml", false);
        }

        config = YamlConfiguration.loadConfiguration(file);

        loadZones();
    }

    /* =====================
       LOAD ZONES
       ===================== */

    private void loadZones() {

        zones.clear();

        if (!config.isConfigurationSection("zones")) {
            plugin.getLogger().warning("No zones found in zones.yaml!");
            return;
        }

        for (String id : config.getConfigurationSection("zones").getKeys(false)) {

            String path = "zones." + id;

            String worldName = config.getString(path + ".world");
            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                plugin.getLogger().warning(
                        "Zone '" + id + "' has invalid world: " + worldName
                );
                continue;
            }

            int minX = config.getInt(path + ".min.x");
            int minY = config.getInt(path + ".min.y");
            int minZ = config.getInt(path + ".min.z");

            int maxX = config.getInt(path + ".max.x");
            int maxY = config.getInt(path + ".max.y");
            int maxZ = config.getInt(path + ".max.z");

            int maxCoins = config.getInt(path + ".max-coins");
            int spawnPerTick = config.getInt(path + ".spawn-per-tick");
            int respawnDelay = config.getInt(path + ".respawn-delay");

            int minHp = config.getInt(path + ".hp.min");
            int maxHp = config.getInt(path + ".hp.max");

            int minReward = config.getInt(path + ".reward.min");
            int maxReward = config.getInt(path + ".reward.max");

            CoinZone zone = new CoinZone(
                    id,
                    world,
                    minX, minY, minZ,
                    maxX, maxY, maxZ,
                    maxCoins,
                    spawnPerTick,
                    respawnDelay,
                    minHp, maxHp,
                    minReward, maxReward
            );

            zones.put(id.toLowerCase(), zone);

            plugin.getLogger().info("Loaded zone: " + id);
        }
    }

    /* =====================
       ACCESS METHODS
       ===================== */

    public Collection<CoinZone> getZones() {
        return zones.values();
    }

    public CoinZone getZone(String id) {
        return zones.get(id.toLowerCase());
    }

    public CoinZone getZoneAt(org.bukkit.Location loc) {
        for (CoinZone zone : zones.values()) {
            if (zone.contains(loc)) {
                return zone;
            }
        }
        return null;
    }

    /* =====================
       RELOAD SUPPORT
       ===================== */

    public void reload() {
        load();
    }
}