package me.Asi.petCoreEngine.managers.pet;

import me.Asi.petCoreEngine.PetCoreEngine;
import me.Asi.petCoreEngine.models.Coin;
import me.Asi.petCoreEngine.models.Pet;
import me.Asi.petCoreEngine.models.PetDefinition;
import me.Asi.petCoreEngine.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class PetManager implements Listener {

    private static final double PET_TARGET_SEARCH_RADIUS = 16.0;

    private final PetCoreEngine plugin;
    private final PetConfigManager configManager;
    private final PetVisualManager visualManager;

    // Pet UUID -> tick when this pet can next attack
    private final Map<UUID, Long> nextAttackTick = new HashMap<>();

    private BukkitTask combatTask;

    public PetManager(PetCoreEngine plugin, PetConfigManager configManager, PetVisualManager visualManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.visualManager = visualManager;

        Bukkit.getPluginManager().registerEvents(this, plugin);
        startTask();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerManager().get(player.getUniqueId());

        if (data == null) {
            return;
        }

        applyProgression(data);
        grantStarterPetsIfNeeded(data);
        equipDefaultPets(data);
        visualManager.syncPlayerPets(player, data.getEquippedPets(), configManager);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        visualManager.removePets(event.getPlayer());
    }

    public void syncPlayer(Player player) {
        PlayerData data = plugin.getPlayerManager().get(player.getUniqueId());
        if (data == null) {
            return;
        }

        applyProgression(data);
        data.ensureValidEquippedState();

        if (data.getEquippedPets().isEmpty()) {
            visualManager.removePets(player);
            return;
        }

        visualManager.syncPlayerPets(player, data.getEquippedPets(), configManager);
    }

    private void startTask() {
        combatTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tickCombat, 2L, 2L);
    }

    private void tickCombat() {
        long tick = Bukkit.getCurrentTick();

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData data = plugin.getPlayerManager().get(player.getUniqueId());
            if (data == null) {
                continue;
            }

            applyProgression(data);
            data.ensureValidEquippedState();

            List<Pet> equippedPets = data.getEquippedPets();
            if (equippedPets.isEmpty()) {
                visualManager.removePets(player);
                continue;
            }

            visualManager.syncPlayerPets(player, equippedPets, configManager);

            for (int i = 0; i < equippedPets.size(); i++) {
                Pet pet = equippedPets.get(i);
                PetDefinition definition = configManager.getDefinition(pet.getTypeId());

                if (definition == null) {
                    visualManager.placeIdleSlot(player, pet.getUniqueId(), i, equippedPets.size());
                    continue;
                }

                Location petLocation = visualManager.getPetLocation(player, pet.getUniqueId());
                if (petLocation == null) {
                    visualManager.placeIdleSlot(player, pet.getUniqueId(), i, equippedPets.size());
                    continue;
                }

                Coin target = plugin.getCoinManager().getNearestCoin(petLocation, PET_TARGET_SEARCH_RADIUS);
                if (target == null) {
                    visualManager.placeIdleSlot(player, pet.getUniqueId(), i, equippedPets.size());
                    continue;
                }

                Location coinLocation = target.getLocation().clone().add(0, 0.7, 0);

                if (!petLocation.getWorld().equals(coinLocation.getWorld())) {
                    visualManager.placeIdleSlot(player, pet.getUniqueId(), i, equippedPets.size());
                    continue;
                }

                double rangeSquared = definition.getAttackRange() * definition.getAttackRange();
                double distanceSquared = petLocation.distanceSquared(coinLocation);

                if (distanceSquared > rangeSquared) {
                    visualManager.movePetTowards(player, pet.getUniqueId(), coinLocation, definition.getMoveSpeed());
                    continue;
                }

                long readyAt = nextAttackTick.getOrDefault(pet.getUniqueId(), 0L);
                if (tick < readyAt) {
                    visualManager.movePetTowards(player, pet.getUniqueId(), coinLocation, definition.getMoveSpeed());
                    continue;
                }

                plugin.getCoinManager().damageCoin(player, target, definition.calculateDamage(pet));
                visualManager.playAttackAnimation(player, pet.getUniqueId(), coinLocation);

                nextAttackTick.put(pet.getUniqueId(), tick + Math.max(1L, definition.getAttackCooldownTicks()));
            }
        }
    }

    private void applyProgression(PlayerData data) {
        data.setMaxEquipped(configManager.getMaxEquippedSlots(data.getRebirths()));
    }

    private void grantStarterPetsIfNeeded(PlayerData data) {
        if (!data.getPets().isEmpty()) {
            return;
        }

        for (String petId : configManager.getStarterPets()) {
            if (configManager.getDefinition(petId) == null) {
                continue;
            }
            data.addPet(new Pet(petId, 1, "starter"));
        }
    }

    private void equipDefaultPets(PlayerData data) {
        if (!data.getEquippedPets().isEmpty()) {
            return;
        }

        for (Pet pet : data.getPets()) {
            if (!data.equipPet(pet)) {
                break;
            }
        }
    }

    public void shutdown() {
        if (combatTask != null) {
            combatTask.cancel();
        }

        nextAttackTick.clear();
    }
}
