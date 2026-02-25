package me.Asi.petCoreEngine.events;

import me.Asi.petCoreEngine.PetCoreEngine;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveEvent implements Listener {

    private final PetCoreEngine plugin;

    public LeaveEvent(PetCoreEngine plugin){
        this.plugin = plugin;
    }

    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        plugin.getPetVisualManager().removePets(player);
    }
}
