package me.Asi.petCoreEngine.models;

import java.util.*;

public class PlayerData {

    public static final int ABSOLUTE_MAX_EQUIPPED = 30;

    private final UUID uuid;

    private double coins;
    private int rebirths;

    // All owned pets
    private final List<Pet> pets = new ArrayList<>();

    // Equipped pets (subset of pets)
    private final List<Pet> equippedPets = new ArrayList<>();

    // Favorite pet ids for UI sorting/highlight
    private final Set<UUID> favoritePetIds = new HashSet<>();

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

    public boolean unequipPet(Pet pet) {
        return equippedPets.remove(pet);
    }

    public boolean isFavorite(Pet pet) {
        return pet != null && favoritePetIds.contains(pet.getUniqueId());
    }

    public boolean toggleFavorite(Pet pet) {
        if (pet == null || !pets.contains(pet)) {
            return false;
        }

        UUID id = pet.getUniqueId();
        if (favoritePetIds.contains(id)) {
            favoritePetIds.remove(id);
            return false;
        }

        favoritePetIds.add(id);
        return true;
    }

    public void ensureValidEquippedState() {
        Set<Pet> owned = new HashSet<>(pets);
        equippedPets.removeIf(pet -> !owned.contains(pet));
        while (equippedPets.size() > maxEquipped) {
            equippedPets.remove(equippedPets.size() - 1);
        }

        Set<UUID> validIds = new HashSet<>();
        for (Pet pet : pets) {
            validIds.add(pet.getUniqueId());
        }
        favoritePetIds.retainAll(validIds);
    }
}
