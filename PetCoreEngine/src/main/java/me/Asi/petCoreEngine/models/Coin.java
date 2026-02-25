package me.Asi.petCoreEngine.models;

import org.bukkit.Location;

import java.util.UUID;

public class Coin {

    private final UUID id;
    private final Location location;
    private CoinZone zone;

    private double health;
    private final double maxHealth;
    private final double reward;

    public Coin(Location loc, double hp, double reward, CoinZone zone) {
        this.id = UUID.randomUUID();
        this.location = loc;
        this.health = hp;
        this.maxHealth = hp;
        this.reward = reward;
        this.zone = zone;
    }

    public void damage(double amount) {
        health = Math.max(0, health - amount);
    }

    public UUID getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public double getHealth() {
        return health;
    }

    public CoinZone getZone() {
        return zone;
    }

    public void setZone(CoinZone zone) {
        this.zone = zone;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getReward() {
        return reward;
    }
}
