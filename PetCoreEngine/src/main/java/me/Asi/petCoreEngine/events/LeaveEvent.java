package me.Asi.petCoreEngine.events;

import me.Asi.petCoreEngine.PetCoreEngine;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveEvent implements Listener {

    private final PetCoreEngine plugin;

    public LeaveEvent(PetCoreEngine plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (plugin.getPetManager() != null) {
            plugin.getPetManager().handleQuit(event.getPlayer());
        }
    }
}
