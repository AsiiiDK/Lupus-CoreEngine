package me.Asi.petCoreEngine.models;

public class CoinTier {
    public final String id;
    public final int weight;
    public final double hpMultiplier;
    public final double rewardMultiplier;

    public CoinTier(String id, int weight,
                    double hpMultiplier,
                    double rewardMultiplier) {
        this.id = id;
        this.weight = weight;
        this.hpMultiplier = hpMultiplier;
        this.rewardMultiplier = rewardMultiplier;
    }
}