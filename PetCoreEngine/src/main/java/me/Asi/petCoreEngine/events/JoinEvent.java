package me.Asi.petCoreEngine.events;

import me.Asi.petCoreEngine.PetCoreEngine;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Legacy event class kept for compatibility.
 * Pet join/quit behavior is now handled in PetManager.
 */
public class JoinEvent implements Listener {

    private final PetCoreEngine plugin;

    public JoinEvent(PetCoreEngine plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (plugin.getPetManager() != null) {
            plugin.getPetManager().handleJoin(event.getPlayer());
        }
    }
}
