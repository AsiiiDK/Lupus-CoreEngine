package me.Asi.petCoreEngine.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;

    private double coins;
    private int rebirths;

    // All owned pets
    private final List<Pet> pets = new ArrayList<>();

    // Equipped pets (subset of pets)
    private final List<Pet> equippedPets = new ArrayList<>();

    private int maxEquipped = 3;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    /* =====================
       BASIC DATA
       ===================== */

    public UUID getUuid() {
        return uuid;
    }

    public double getCoins() {
        return coins;
    }

    public void addCoins(double amount) {
        this.coins += amount;
    }

    public void setCoins(double coins) {
        this.coins = coins;
    }

    public int getRebirths() {
        return rebirths;
    }

    public void setRebirths(int rebirths) {
        this.rebirths = rebirths;
    }

    /* =====================
       PETS
       ===================== */

    public List<Pet> getPets() {
        return pets;
    }

    public List<Pet> getEquippedPets() {
        return equippedPets;
    }

    public int getMaxEquipped() {
        return maxEquipped;
    }

    public void setMaxEquipped(int maxEquipped) {
        this.maxEquipped = maxEquipped;
    }

    public boolean equipPet(Pet pet) {

        if (!pets.contains(pet)) return false;
        if (equippedPets.size() >= maxEquipped) return false;

        equippedPets.add(pet);
        return true;
    }

    public void unequipPet(Pet pet) {
        equippedPets.remove(pet);
    }
}