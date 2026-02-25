package me.Asi.petCoreEngine.managers.coin;

import me.Asi.petCoreEngine.PetCoreEngine;
import me.Asi.petCoreEngine.models.CoinTier;
import me.Asi.petCoreEngine.models.CoinZone;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CoinSpawnManager {

    private final PetCoreEngine plugin;
    private BukkitTask task;

    // Zone → last spawn time
    private final Map<String, Long> zoneCooldowns = new HashMap<>();

    // Contains cached player data of where each player is
    private final Map<String, Set<Player>> zonePlayers = new HashMap<>();

    public CoinSpawnManager(PetCoreEngine plugin) {
        this.plugin = plugin;
        startTask();
    }

    /* =====================
       TASK
       ===================== */

    private void startTask() {

        task = Bukkit.getScheduler().runTaskTimer(
                plugin,
                this::tick,
                40L,
                40L
        );
    }

    public void shutdown() {
        if (task != null) {
            task.cancel();
        }
    }

    /* =====================
       MAIN LOOP
       ===================== */

    private void tick() {

        if (!plugin.getActive()) return;

        for (CoinZone zone :
                plugin.getZoneManager().getZones()) {

            if (!hasPlayersInside(zone))
                continue;

            int players = getPlayersInZone(zone);
            int scaledMax = zone.getScaledMaxCoins(players);

            if (zone.getActiveCoins() >= scaledMax)
                continue;

            if (!canSpawn(zone))
                continue;

            int toSpawn =
                    Math.min(
                            zone.getSpawnPerTick(),
                            scaledMax - zone.getActiveCoins()
                    );

            for (int i = 0; i < toSpawn; i++) {

                Location loc = findSafeLocation(zone);

                if (loc == null)
                    continue;

                CoinTier tier = zone.getRandomTier();

                int hp = (int)(zone.randomHp() * tier.hpMultiplier);
                int reward = (int)(zone.randomReward() * tier.rewardMultiplier);

                plugin.getCoinManager()
                        .spawnCoin(loc, hp, reward, zone);
            }

            zoneCooldowns.put(
                    zone.getId(),
                    System.currentTimeMillis()
            );
        }
    }

    /* =====================
       SAFE LOCATION LOGIC
       ===================== */

    private Location findSafeLocation(CoinZone zone) {

        for (int attempts = 0; attempts < 10; attempts++) {

            Location base = zone.randomLocation();

            // Move to highest solid block
            int y = base.getWorld()
                    .getHighestBlockYAt(base);

            Location ground =
                    new Location(
                            base.getWorld(),
                            base.getX(),
                            y,
                            base.getZ()
                    );

            if (isValidGround(ground)) {

                // Avoid stacking coins
                if (plugin.getCoinManager()
                        .getNearestCoin(ground, 1.5) == null) {

                    return ground.add(0, 1, 0);
                }
            }
        }

        return null;
    }

    private boolean isValidGround(Location loc) {

        Material type =
                loc.getBlock().getType();

        return type.isSolid();
    }

    /* =====================
       PLAYER CHECK
       ===================== */

    private boolean hasPlayersInside(CoinZone zone) {

        for (Player player :
                Bukkit.getOnlinePlayers()) {

            if (zone.contains(player.getLocation()))
                return true;
        }

        return false;
    }

    private void updateZonePlayers() {

        zonePlayers.clear();

        for (CoinZone zone :
                plugin.getZoneManager().getZones()) {

            Set<Player> players = new HashSet<>();

            for (Player player :
                    Bukkit.getOnlinePlayers()) {

                if (zone.contains(player.getLocation()))
                    players.add(player);
            }

            zonePlayers.put(zone.getId(), players);
        }
    }

    /* =====================
       RESPAWN DELAY
       ===================== */

    private boolean canSpawn(CoinZone zone) {
        long last =
                zoneCooldowns.getOrDefault(
                        zone.getId(),
                        0L
                );
        long delayMillis =
                zone.getRespawnDelay() * 1000L;
        return System.currentTimeMillis()
                - last >= delayMillis;
    }

        /* =====================
       AMOUNT OF PLAYERS IN 1 ZONE
       ===================== */

    private int getPlayersInZone(CoinZone zone) {
        int count = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (zone.contains(p.getLocation()))
                count++;
        }
        return count;
    }
}
