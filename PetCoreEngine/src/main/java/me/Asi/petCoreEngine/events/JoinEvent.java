package me.Asi.petCoreEngine.events;

import me.Asi.petCoreEngine.PetCoreEngine;
import me.Asi.petCoreEngine.models.Pet;
import me.Asi.petCoreEngine.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

    private final PetCoreEngine plugin;

    public JoinEvent(PetCoreEngine plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.broadcastMessage("Tester pet system");


        PlayerData data = plugin.getPlayerManager().get(e.getPlayer().getUniqueId());

        Pet pet = new Pet("dog", 10, "starteregg");
        Pet cat = new Pet("cat", 10, "starteregg");
        Pet bunny = new Pet("bunny", 10, "starteregg");

        data.getPets().add(pet);
        data.equipPet(pet);

        data.getPets().add(cat);
        data.equipPet(cat);

        data.getPets().add(bunny);
        data.equipPet(bunny);

        for (int i = 0; i < 10; i++) {
            data.getPets().add(cat);
            data.equipPet(cat);
        }

        plugin.getPetVisualManager().spawnPets(e.getPlayer());
    }
}
