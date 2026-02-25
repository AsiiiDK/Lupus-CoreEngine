package me.Asi.petCoreEngine.models;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class CoinZone {

    private final String id;
    private final World world;

    private final int minX, minY, minZ;
    private final int maxX, maxY, maxZ;

    private final int maxCoins;
    private final int spawnPerTick;
    private final int respawnDelay;

    private final int minHp, maxHp;
    private final int minReward, maxReward;

    private int activeCoins = 0;

    private final List<CoinTier> tiers = new ArrayList<>();

    /* =====================
       CONSTRUCTOR
       ===================== */

    public CoinZone(
            String id,
            World world,
            int minX, int minY, int minZ,
            int maxX, int maxY, int maxZ,
            int maxCoins,
            int spawnPerTick,
            int respawnDelay,
            int minHp, int maxHp,
            int minReward, int maxReward
    ) {
        this.id = Objects.requireNonNull(id, "Zone id cannot be null");
        this.world = Objects.requireNonNull(world, "World cannot be null");

        // Normalize bounds (prevents accidental reversed values)
        this.minX = Math.min(minX, maxX);
        this.maxX = Math.max(minX, maxX);

        this.minY = Math.min(minY, maxY);
        this.maxY = Math.max(minY, maxY);

        this.minZ = Math.min(minZ, maxZ);
        this.maxZ = Math.max(minZ, maxZ);

        this.maxCoins = maxCoins;
        this.spawnPerTick = spawnPerTick;
        this.respawnDelay = respawnDelay;

        this.minHp = Math.min(minHp, maxHp);
        this.maxHp = Math.max(minHp, maxHp);

        this.minReward = Math.min(minReward, maxReward);
        this.maxReward = Math.max(minReward, maxReward);

        // TODO Fix så den sætter til random tier istedet for hardcode
        tiers.add(new CoinTier("default", 100, 1.0, 1.0));
    }

    /* =====================
       LOGIC
       ===================== */

    public boolean contains(Location loc) {

        if (loc == null || loc.getWorld() == null)
            return false;

        if (!loc.getWorld().equals(world))
            return false;

        return loc.getX() >= minX && loc.getX() <= maxX
                && loc.getY() >= minY && loc.getY() <= maxY
                && loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }

    public CoinTier getRandomTier() {

        if (tiers.isEmpty()) {
            throw new IllegalStateException(
                    "Zone '" + id + "' has no tiers defined!"
            );
        }

        int total = tiers.stream()
                .mapToInt(t -> t.weight)
                .sum();

        if (total <= 0) {
            throw new IllegalStateException(
                    "Zone '" + id + "' has invalid tier weights!"
            );
        }

        int r = ThreadLocalRandom.current()
                .nextInt(total);

        int current = 0;

        for (CoinTier tier : tiers) {
            current += tier.weight;
            if (r < current)
                return tier;
        }

        return tiers.get(0); // fallback
    }

    public Location randomLocation() {

        int x = random(minX, maxX);
        int y = random(minY, maxY);
        int z = random(minZ, maxZ);

        return new Location(world, x + 0.5, y, z + 0.5);
    }

    public int randomHp() {
        return random(minHp, maxHp);
    }

    public int randomReward() {
        return random(minReward, maxReward);
    }

    private int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /* =====================
       ACTIVE COINS
       ===================== */

    public int getActiveCoins() {
        return activeCoins;
    }

    public void incrementActive() {
        activeCoins++;
    }

    public void decrementActive() {
        if (activeCoins > 0) {
            activeCoins--;
        }
    }

    public boolean isFull() {
        return activeCoins >= maxCoins;
    }

    public int getScaledMaxCoins(int playerCount) {
        return maxCoins + (playerCount * 2);
    }

    /* =====================
       GETTERS
       ===================== */

    public String getId() {
        return id;
    }

    public World getWorld() {
        return world;
    }

    public int getMaxCoins() {
        return maxCoins;
    }

    public int getSpawnPerTick() {
        return spawnPerTick;
    }

    public int getRespawnDelay() {
        return respawnDelay;
    }

    public int getMinHp() {
        return minHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getMinReward() {
        return minReward;
    }

    public int getMaxReward() {
        return maxReward;
    }

    /* =====================
       DEBUG
       ===================== */

    @Override
    public String toString() {
        return "CoinZone{" +
                "id='" + id + '\'' +
                ", world=" + world.getName() +
                ", bounds=[" + minX + "," + minY + "," + minZ +
                " -> " + maxX + "," + maxY + "," + maxZ + "]" +
                ", maxCoins=" + maxCoins +
                ", activeCoins=" + activeCoins +
                '}';
    }
}