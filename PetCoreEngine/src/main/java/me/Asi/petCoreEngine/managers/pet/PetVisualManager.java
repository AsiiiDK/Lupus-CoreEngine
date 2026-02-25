package me.Asi.petCoreEngine.managers.pet;

import me.Asi.petCoreEngine.PetCoreEngine;
import me.Asi.petCoreEngine.models.Pet;
import me.Asi.petCoreEngine.models.PlayerData;
import me.Asi.petCoreEngine.util.HeadUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class PetVisualManager {

    private final PetCoreEngine plugin;

    // Player → list of pet stands
    private final Map<UUID, List<ArmorStand>> visuals = new HashMap<>();

    private BukkitTask followTask;

    // Example pet texture (replace later per pet type)
    private static final String DEFAULT_PET_TEXTURE =
            "http://textures.minecraft.net/texture/26629dfa3fdfef04054024e0156d5e19da5401b1911f59b4bd3982685fe54c2c";

    public PetVisualManager(PetCoreEngine plugin) {
        this.plugin = plugin;
        startFollowTask();
    }

    /* =====================
       SPAWN VISUALS
       ===================== */

    public void spawnPets(Player player) {

        PlayerData data =
                plugin.getPlayerManager()
                        .get(player.getUniqueId());

        if (data == null) return;

        removePets(player);

        List<ArmorStand> stands = new ArrayList<>();

        for (Pet pet : data.getEquippedPets()) {

            Location loc =
                    player.getLocation().clone();
                            //.clone()
                            //.add(0, 1, 0);

            ArmorStand stand =
                    player.getWorld().spawn(loc, ArmorStand.class);

            stand.setInvisible(true);
            stand.setMarker(true);
            stand.setGravity(false);
            stand.setSmall(true);
            stand.setBasePlate(false);
            stand.setCollidable(false);
            stand.setPersistent(false);

            stand.getEquipment().setHelmet(
                    HeadUtil.fromTextureURL(DEFAULT_PET_TEXTURE)
            );

            stands.add(stand);
        }

        visuals.put(player.getUniqueId(), stands);
    }

    /* =====================
       FOLLOW SYSTEM
       ===================== */

    private void startFollowTask() {

        followTask =
                Bukkit.getScheduler().runTaskTimer(
                        plugin,
                        this::updatePositions,
                        2L,
                        2L
                );
    }

    private void updatePositions() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            List<ArmorStand> stands =
                    visuals.get(player.getUniqueId());
            if (stands == null || stands.isEmpty())
                continue;
            double spacing = 1.2;
            for (int i = 0; i < stands.size(); i++) {
                ArmorStand stand = stands.get(i);
                if (stand == null || stand.isDead())
                    continue;
                double angle =
                        Math.toRadians(
                                (360.0 / stands.size()) * i
                        );
                double x =
                        Math.cos(angle) * spacing;
                double z =
                        Math.sin(angle) * spacing;
                Location target =
                        player.getLocation()
                                .clone()
                                .add(x, 1.0, z);
                stand.teleport(target);
            }
        }
    }

    /* =====================
       REMOVE
       ===================== */

    public void removePets(Player player) {

        List<ArmorStand> stands =
                visuals.remove(player.getUniqueId());

        if (stands == null) return;

        for (ArmorStand stand : stands) {
            if (stand != null && !stand.isDead())
                stand.remove();
        }
    }

    /* =====================
       CLEANUP
       ===================== */

    public void shutdown() {

        if (followTask != null)
            followTask.cancel();

        for (List<ArmorStand> stands : visuals.values()) {
            for (ArmorStand stand : stands) {
                if (stand != null && !stand.isDead())
                    stand.remove();
            }
        }

        visuals.clear();
    }
}