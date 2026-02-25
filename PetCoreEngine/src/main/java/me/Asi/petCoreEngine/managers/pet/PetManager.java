package me.Asi.petCoreEngine.managers.pet;

import me.Asi.petCoreEngine.PetCoreEngine;
import me.Asi.petCoreEngine.models.Coin;
import me.Asi.petCoreEngine.models.Pet;
import me.Asi.petCoreEngine.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PetManager {

    private final PetCoreEngine plugin;

    public PetManager(PetCoreEngine plugin) {
        this.plugin = plugin;
        startTask();
    }

    private void startTask() {

        Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> tickPets(),
                20L,
                20L
        );
    }

    private void tickPets() {

        for (Player player : Bukkit.getOnlinePlayers()) {

            PlayerData data =
                    plugin.getPlayerManager()
                            .get(player.getUniqueId());

            if (data == null) continue;

            for (Pet pet : data.getEquippedPets()) {

                Coin target =
                        plugin.getCoinManager()
                                .getNearestCoin(
                                        player.getLocation(),
                                        8
                                );

                if (target == null) continue;

                plugin.getCoinManager()
                        .damageCoin(
                                player,
                                target,
                                pet.getDamage()
                        );
            }
        }
    }
}
