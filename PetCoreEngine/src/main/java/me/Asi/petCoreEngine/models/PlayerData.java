package me.Asi.petCoreEngine.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    public static final int ABSOLUTE_MAX_EQUIPPED = 30;

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

    public List<Pet> getPets() {
        return Collections.unmodifiableList(pets);
    }

    public List<Pet> getEquippedPets() {
        return Collections.unmodifiableList(equippedPets);
    }

    public int getMaxEquipped() {
        return maxEquipped;
    }

    public void setMaxEquipped(int maxEquipped) {
        this.maxEquipped = Math.max(1, Math.min(ABSOLUTE_MAX_EQUIPPED, maxEquipped));
        while (equippedPets.size() > this.maxEquipped) {
            equippedPets.remove(equippedPets.size() - 1);
        }
    }

    public void addPet(Pet pet) {
        pets.add(pet);
    }

    public boolean equipPet(Pet pet) {
        if (!pets.contains(pet)) return false;
        if (equippedPets.contains(pet)) return false;
        if (equippedPets.size() >= maxEquipped) return false;

        equippedPets.add(pet);
        return true;
    }

    public void unequipPet(Pet pet) {
        equippedPets.remove(pet);
    }

    public void ensureValidEquippedState() {
        equippedPets.removeIf(pet -> !pets.contains(pet));
        while (equippedPets.size() > maxEquipped) {
            equippedPets.remove(equippedPets.size() - 1);
        }
    }
}
