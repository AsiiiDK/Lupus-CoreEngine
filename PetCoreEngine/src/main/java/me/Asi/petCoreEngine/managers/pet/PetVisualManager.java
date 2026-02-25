package me.Asi.petCoreEngine.managers.pet;

import me.Asi.petCoreEngine.PetCoreEngine;
import me.Asi.petCoreEngine.models.Pet;
import me.Asi.petCoreEngine.models.PetDefinition;
import me.Asi.petCoreEngine.util.HeadUtil;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.*;

public class PetVisualManager {

    private final PetCoreEngine plugin;

    // Player -> Pet UUID -> visual stand
    private final Map<UUID, Map<UUID, ArmorStand>> visuals = new HashMap<>();

    public PetVisualManager(PetCoreEngine plugin) {
        this.plugin = plugin;
    }

    public void syncPlayerPets(Player player, List<Pet> equippedPets, PetConfigManager configManager) {
        Map<UUID, ArmorStand> current = visuals.computeIfAbsent(player.getUniqueId(), ignored -> new LinkedHashMap<>());

        Set<UUID> wantedIds = new HashSet<>();

        for (Pet pet : equippedPets) {
            wantedIds.add(pet.getUniqueId());

            if (current.containsKey(pet.getUniqueId())) {
                continue;
            }

            PetDefinition definition = configManager.getDefinition(pet.getTypeId());
            if (definition == null) {
                continue;
            }

            ArmorStand stand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
            stand.setInvisible(true);
            stand.setMarker(true);
            stand.setGravity(false);
            stand.setSmall(true);
            stand.setBasePlate(false);
            stand.setCollidable(false);
            stand.setPersistent(false);
            stand.getEquipment().setHelmet(HeadUtil.fromTextureURL(definition.getTextureUrl()));

            current.put(pet.getUniqueId(), stand);
        }

        Iterator<Map.Entry<UUID, ArmorStand>> iterator = current.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, ArmorStand> entry = iterator.next();
            if (!wantedIds.contains(entry.getKey())) {
                removeStand(entry.getValue());
                iterator.remove();
            }
        }
    }

    public Location getPetLocation(Player player, UUID petId) {
        ArmorStand stand = getStand(player, petId);
        return stand == null ? null : stand.getLocation();
    }

    public void movePetTowards(Player player, UUID petId, Location target, double speedPerTick) {
        ArmorStand stand = getStand(player, petId);
        if (stand == null || target == null) {
            return;
        }

        Location from = stand.getLocation();
        Location to = target.clone().add(0, 0.45, 0);
        if (!from.getWorld().equals(to.getWorld())) {
            return;
        }

        org.bukkit.util.Vector delta = to.toVector().subtract(from.toVector());
        double distance = delta.length();
        if (distance < 0.05) {
            stand.teleport(to);
            return;
        }

        org.bukkit.util.Vector step = delta.normalize().multiply(Math.min(speedPerTick, distance));
        stand.teleport(from.add(step));
    }

    public void placeIdleSlot(Player player, UUID petId, int index, int total) {
        ArmorStand stand = getStand(player, petId);
        if (stand == null) {
            return;
        }

        double radius = 1.4 + (Math.min(total, 30) * 0.01);
        double angle = Math.toRadians((360.0 / Math.max(1, total)) * index + (System.currentTimeMillis() / 25.0 % 360));

        double x = Math.cos(angle) * radius;
        double z = Math.sin(angle) * radius;

        Location target = player.getLocation().clone().add(x, 0.9, z);
        movePetTowards(player, petId, target, 0.35);
    }

    public void playAttackAnimation(Player player, UUID petId, Location coinLocation) {
        ArmorStand stand = getStand(player, petId);
        if (stand == null || coinLocation == null) {
            return;
        }

        Location attackPoint = coinLocation.clone().add(0, 0.9, 0);
        stand.teleport(attackPoint);

        Location bounceBack = attackPoint.clone().add(0, 0.22, 0);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            ArmorStand refreshed = getStand(player, petId);
            if (refreshed != null) {
                refreshed.teleport(bounceBack);
            }
        }, 1L);
    }

    private ArmorStand getStand(Player player, UUID petId) {
        Map<UUID, ArmorStand> playerVisuals = visuals.get(player.getUniqueId());
        if (playerVisuals == null) {
            return null;
        }

        ArmorStand stand = playerVisuals.get(petId);
        if (stand == null || stand.isDead()) {
            return null;
        }

        return stand;
    }

    public void removePets(Player player) {
        Map<UUID, ArmorStand> stands = visuals.remove(player.getUniqueId());
        if (stands == null) {
            return;
        }

        for (ArmorStand stand : stands.values()) {
            removeStand(stand);
        }
    }

    public void shutdown() {
        for (Map<UUID, ArmorStand> playerVisuals : visuals.values()) {
            for (ArmorStand stand : playerVisuals.values()) {
                removeStand(stand);
            }
        }

        visuals.clear();
    }

    private void removeStand(ArmorStand stand) {
        if (stand != null && !stand.isDead()) {
            stand.remove();
        }
    }
}
