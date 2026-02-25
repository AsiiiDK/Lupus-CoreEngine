package me.Asi.petCoreEngine.models;

public class Pet {

    private final String id;
    private final double power;
    private int level;
    private String eggHatchedFrom;

    public Pet(String id, double power, String eggHatchedFrom) {
        this.id = id;
        this.power = power;
        this.level = 1;
        this.eggHatchedFrom = eggHatchedFrom;
    }

    public double getDamage() {
        return power * level;
    }

    public void levelUp() {
        level++;
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public String getEggHatchedFrom() {return eggHatchedFrom;}

}